package io.undertow.server.handlers;

import io.undertow.UndertowLogger;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.builder.HandlerBuilder;
import io.undertow.util.WorkerUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.xnio.XnioExecutor;
import org.xnio.XnioIoThread;

public class StuckThreadDetectionHandler implements HttpHandler {
   public static final int DEFAULT_THRESHOLD = 600;
   private final AtomicInteger stuckCount;
   private final int threshold;
   private final ConcurrentHashMap<Long, MonitoredThread> activeThreads;
   private final Queue<CompletedStuckThread> completedStuckThreadsQueue;
   private final HttpHandler next;
   private final Runnable stuckThreadTask;
   private volatile XnioExecutor.Key timerKey;

   public StuckThreadDetectionHandler(HttpHandler next) {
      this(600, next);
   }

   public StuckThreadDetectionHandler(int threshold, HttpHandler next) {
      this.stuckCount = new AtomicInteger(0);
      this.activeThreads = new ConcurrentHashMap();
      this.completedStuckThreadsQueue = new ConcurrentLinkedQueue();
      this.stuckThreadTask = new Runnable() {
         public void run() {
            long thresholdInMillis = (long)StuckThreadDetectionHandler.this.threshold * 1000L;
            Iterator var3 = StuckThreadDetectionHandler.this.activeThreads.values().iterator();

            while(var3.hasNext()) {
               MonitoredThread monitoredThread = (MonitoredThread)var3.next();
               long activeTime = monitoredThread.getActiveTimeInMillis();
               if (activeTime >= thresholdInMillis && monitoredThread.markAsStuckIfStillRunning()) {
                  int numStuckThreadsx = StuckThreadDetectionHandler.this.stuckCount.incrementAndGet();
                  StuckThreadDetectionHandler.this.notifyStuckThreadDetected(monitoredThread, activeTime, numStuckThreadsx);
               }
            }

            for(CompletedStuckThread completedStuckThread = (CompletedStuckThread)StuckThreadDetectionHandler.this.completedStuckThreadsQueue.poll(); completedStuckThread != null; completedStuckThread = (CompletedStuckThread)StuckThreadDetectionHandler.this.completedStuckThreadsQueue.poll()) {
               int numStuckThreads = StuckThreadDetectionHandler.this.stuckCount.decrementAndGet();
               StuckThreadDetectionHandler.this.notifyStuckThreadCompleted(completedStuckThread, numStuckThreads);
            }

            synchronized(StuckThreadDetectionHandler.this) {
               if (StuckThreadDetectionHandler.this.activeThreads.isEmpty()) {
                  StuckThreadDetectionHandler.this.timerKey = null;
               } else {
                  StuckThreadDetectionHandler.this.timerKey = WorkerUtils.executeAfter((XnioIoThread)Thread.currentThread(), StuckThreadDetectionHandler.this.stuckThreadTask, 1L, TimeUnit.SECONDS);
               }

            }
         }
      };
      this.threshold = threshold;
      this.next = next;
   }

   public int getThreshold() {
      return this.threshold;
   }

   private void notifyStuckThreadDetected(MonitoredThread monitoredThread, long activeTime, int numStuckThreads) {
      Throwable th = new Throwable();
      th.setStackTrace(monitoredThread.getThread().getStackTrace());
      UndertowLogger.REQUEST_LOGGER.stuckThreadDetected(monitoredThread.getThread().getName(), monitoredThread.getThread().getId(), activeTime, monitoredThread.getStartTime(), monitoredThread.getRequestUri(), this.threshold, numStuckThreads, th);
   }

   private void notifyStuckThreadCompleted(CompletedStuckThread thread, int numStuckThreads) {
      UndertowLogger.REQUEST_LOGGER.stuckThreadCompleted(thread.getName(), thread.getId(), thread.getTotalActiveTime(), numStuckThreads);
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      Long key = Thread.currentThread().getId();
      MonitoredThread monitoredThread = new MonitoredThread(Thread.currentThread(), exchange.getRequestURI() + exchange.getQueryString());
      this.activeThreads.put(key, monitoredThread);
      if (this.timerKey == null) {
         synchronized(this) {
            if (this.timerKey == null) {
               this.timerKey = exchange.getIoThread().executeAfter(this.stuckThreadTask, 1L, TimeUnit.SECONDS);
            }
         }
      }

      try {
         this.next.handleRequest(exchange);
      } finally {
         this.activeThreads.remove(key);
         if (monitoredThread.markAsDone() == StuckThreadDetectionHandler.MonitoredThreadState.STUCK) {
            this.completedStuckThreadsQueue.add(new CompletedStuckThread(monitoredThread.getThread(), monitoredThread.getActiveTimeInMillis()));
         }

      }

   }

   public long[] getStuckThreadIds() {
      List<Long> idList = new ArrayList();
      Iterator var2 = this.activeThreads.values().iterator();

      while(var2.hasNext()) {
         MonitoredThread monitoredThread = (MonitoredThread)var2.next();
         if (monitoredThread.isMarkedAsStuck()) {
            idList.add(monitoredThread.getThread().getId());
         }
      }

      long[] result = new long[idList.size()];

      for(int i = 0; i < result.length; ++i) {
         result[i] = (Long)idList.get(i);
      }

      return result;
   }

   public String toString() {
      return "stuck-thread-detector( " + this.threshold + " )";
   }

   public static class Builder implements HandlerBuilder {
      public String name() {
         return "stuck-thread-detector";
      }

      public Map<String, Class<?>> parameters() {
         return Collections.singletonMap("threshhold", Integer.class);
      }

      public Set<String> requiredParameters() {
         return Collections.emptySet();
      }

      public String defaultParameter() {
         return "threshhold";
      }

      public HandlerWrapper build(Map<String, Object> config) {
         Integer threshhold = (Integer)config.get("threshhold");
         return threshhold == null ? new Wrapper() : new Wrapper(threshhold);
      }
   }

   public static final class Wrapper implements HandlerWrapper {
      private final int threshhold;

      public Wrapper(int threshhold) {
         this.threshhold = threshhold;
      }

      public Wrapper() {
         this.threshhold = 600;
      }

      public HttpHandler wrap(HttpHandler handler) {
         return new StuckThreadDetectionHandler(this.threshhold, handler);
      }
   }

   private static enum MonitoredThreadState {
      RUNNING,
      STUCK,
      DONE;
   }

   private static class CompletedStuckThread {
      private final String threadName;
      private final long threadId;
      private final long totalActiveTime;

      CompletedStuckThread(Thread thread, long totalActiveTime) {
         this.threadName = thread.getName();
         this.threadId = thread.getId();
         this.totalActiveTime = totalActiveTime;
      }

      public String getName() {
         return this.threadName;
      }

      public long getId() {
         return this.threadId;
      }

      public long getTotalActiveTime() {
         return this.totalActiveTime;
      }
   }

   private static class MonitoredThread {
      private final Thread thread;
      private final String requestUri;
      private final long start;
      private final AtomicInteger state;

      MonitoredThread(Thread thread, String requestUri) {
         this.state = new AtomicInteger(StuckThreadDetectionHandler.MonitoredThreadState.RUNNING.ordinal());
         this.thread = thread;
         this.requestUri = requestUri;
         this.start = System.currentTimeMillis();
      }

      public Thread getThread() {
         return this.thread;
      }

      public String getRequestUri() {
         return this.requestUri;
      }

      public long getActiveTimeInMillis() {
         return System.currentTimeMillis() - this.start;
      }

      public Date getStartTime() {
         return new Date(this.start);
      }

      public boolean markAsStuckIfStillRunning() {
         return this.state.compareAndSet(StuckThreadDetectionHandler.MonitoredThreadState.RUNNING.ordinal(), StuckThreadDetectionHandler.MonitoredThreadState.STUCK.ordinal());
      }

      public MonitoredThreadState markAsDone() {
         int val = this.state.getAndSet(StuckThreadDetectionHandler.MonitoredThreadState.DONE.ordinal());
         return StuckThreadDetectionHandler.MonitoredThreadState.values()[val];
      }

      boolean isMarkedAsStuck() {
         return this.state.get() == StuckThreadDetectionHandler.MonitoredThreadState.STUCK.ordinal();
      }
   }
}
