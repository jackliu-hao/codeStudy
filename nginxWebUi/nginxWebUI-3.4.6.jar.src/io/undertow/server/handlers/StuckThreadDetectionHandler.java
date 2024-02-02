/*     */ package io.undertow.server.handlers;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.server.HandlerWrapper;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*     */ import io.undertow.util.WorkerUtils;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Queue;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentLinkedQueue;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import org.xnio.XnioExecutor;
/*     */ import org.xnio.XnioIoThread;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StuckThreadDetectionHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   public static final int DEFAULT_THRESHOLD = 600;
/*  55 */   private final AtomicInteger stuckCount = new AtomicInteger(0);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int threshold;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  68 */   private final ConcurrentHashMap<Long, MonitoredThread> activeThreads = new ConcurrentHashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  73 */   private final Queue<CompletedStuckThread> completedStuckThreadsQueue = new ConcurrentLinkedQueue<>();
/*     */ 
/*     */   
/*     */   private final HttpHandler next;
/*     */ 
/*     */   
/*  79 */   private final Runnable stuckThreadTask = new Runnable()
/*     */     {
/*     */       public void run() {
/*  82 */         long thresholdInMillis = StuckThreadDetectionHandler.this.threshold * 1000L;
/*     */ 
/*     */ 
/*     */         
/*  86 */         for (StuckThreadDetectionHandler.MonitoredThread monitoredThread : StuckThreadDetectionHandler.this.activeThreads.values()) {
/*  87 */           long activeTime = monitoredThread.getActiveTimeInMillis();
/*     */           
/*  89 */           if (activeTime >= thresholdInMillis && monitoredThread.markAsStuckIfStillRunning()) {
/*  90 */             int numStuckThreads = StuckThreadDetectionHandler.this.stuckCount.incrementAndGet();
/*  91 */             StuckThreadDetectionHandler.this.notifyStuckThreadDetected(monitoredThread, activeTime, numStuckThreads);
/*     */           } 
/*     */         } 
/*     */         
/*  95 */         StuckThreadDetectionHandler.CompletedStuckThread completedStuckThread = StuckThreadDetectionHandler.this.completedStuckThreadsQueue.poll();
/*  96 */         for (; completedStuckThread != null; completedStuckThread = StuckThreadDetectionHandler.this.completedStuckThreadsQueue.poll()) {
/*     */           
/*  98 */           int numStuckThreads = StuckThreadDetectionHandler.this.stuckCount.decrementAndGet();
/*  99 */           StuckThreadDetectionHandler.this.notifyStuckThreadCompleted(completedStuckThread, numStuckThreads);
/*     */         } 
/* 101 */         synchronized (StuckThreadDetectionHandler.this) {
/* 102 */           if (StuckThreadDetectionHandler.this.activeThreads.isEmpty()) {
/* 103 */             StuckThreadDetectionHandler.this.timerKey = null;
/*     */           } else {
/* 105 */             StuckThreadDetectionHandler.this.timerKey = WorkerUtils.executeAfter((XnioIoThread)Thread.currentThread(), StuckThreadDetectionHandler.this.stuckThreadTask, 1L, TimeUnit.SECONDS);
/*     */           } 
/*     */         } 
/*     */       }
/*     */     };
/*     */   
/*     */   private volatile XnioExecutor.Key timerKey;
/*     */   
/*     */   public StuckThreadDetectionHandler(HttpHandler next) {
/* 114 */     this(600, next);
/*     */   }
/*     */   
/*     */   public StuckThreadDetectionHandler(int threshold, HttpHandler next) {
/* 118 */     this.threshold = threshold;
/* 119 */     this.next = next;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getThreshold() {
/* 126 */     return this.threshold;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void notifyStuckThreadDetected(MonitoredThread monitoredThread, long activeTime, int numStuckThreads) {
/* 133 */     Throwable th = new Throwable();
/* 134 */     th.setStackTrace(monitoredThread.getThread().getStackTrace());
/* 135 */     UndertowLogger.REQUEST_LOGGER
/* 136 */       .stuckThreadDetected(monitoredThread.getThread().getName(), monitoredThread.getThread().getId(), activeTime, monitoredThread
/* 137 */         .getStartTime(), monitoredThread.getRequestUri(), this.threshold, numStuckThreads, th);
/*     */   }
/*     */ 
/*     */   
/*     */   private void notifyStuckThreadCompleted(CompletedStuckThread thread, int numStuckThreads) {
/* 142 */     UndertowLogger.REQUEST_LOGGER
/* 143 */       .stuckThreadCompleted(thread.getName(), thread.getId(), thread.getTotalActiveTime(), numStuckThreads);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 156 */     Long key = Long.valueOf(Thread.currentThread().getId());
/* 157 */     MonitoredThread monitoredThread = new MonitoredThread(Thread.currentThread(), exchange.getRequestURI() + exchange.getQueryString());
/* 158 */     this.activeThreads.put(key, monitoredThread);
/* 159 */     if (this.timerKey == null) {
/* 160 */       synchronized (this) {
/* 161 */         if (this.timerKey == null) {
/* 162 */           this.timerKey = exchange.getIoThread().executeAfter(this.stuckThreadTask, 1L, TimeUnit.SECONDS);
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/*     */     try {
/* 168 */       this.next.handleRequest(exchange);
/*     */     } finally {
/* 170 */       this.activeThreads.remove(key);
/* 171 */       if (monitoredThread.markAsDone() == MonitoredThreadState.STUCK) {
/* 172 */         this.completedStuckThreadsQueue.add(new CompletedStuckThread(monitoredThread
/* 173 */               .getThread(), monitoredThread
/* 174 */               .getActiveTimeInMillis()));
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public long[] getStuckThreadIds() {
/* 180 */     List<Long> idList = new ArrayList<>();
/* 181 */     for (MonitoredThread monitoredThread : this.activeThreads.values()) {
/* 182 */       if (monitoredThread.isMarkedAsStuck()) {
/* 183 */         idList.add(Long.valueOf(monitoredThread.getThread().getId()));
/*     */       }
/*     */     } 
/*     */     
/* 187 */     long[] result = new long[idList.size()];
/* 188 */     for (int i = 0; i < result.length; i++) {
/* 189 */       result[i] = ((Long)idList.get(i)).longValue();
/*     */     }
/* 191 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   private static class MonitoredThread
/*     */   {
/*     */     private final Thread thread;
/*     */     
/*     */     private final String requestUri;
/*     */     
/*     */     private final long start;
/* 202 */     private final AtomicInteger state = new AtomicInteger(StuckThreadDetectionHandler.MonitoredThreadState.RUNNING
/* 203 */         .ordinal());
/*     */     
/*     */     MonitoredThread(Thread thread, String requestUri) {
/* 206 */       this.thread = thread;
/* 207 */       this.requestUri = requestUri;
/* 208 */       this.start = System.currentTimeMillis();
/*     */     }
/*     */     
/*     */     public Thread getThread() {
/* 212 */       return this.thread;
/*     */     }
/*     */     
/*     */     public String getRequestUri() {
/* 216 */       return this.requestUri;
/*     */     }
/*     */     
/*     */     public long getActiveTimeInMillis() {
/* 220 */       return System.currentTimeMillis() - this.start;
/*     */     }
/*     */     
/*     */     public Date getStartTime() {
/* 224 */       return new Date(this.start);
/*     */     }
/*     */     
/*     */     public boolean markAsStuckIfStillRunning() {
/* 228 */       return this.state.compareAndSet(StuckThreadDetectionHandler.MonitoredThreadState.RUNNING.ordinal(), StuckThreadDetectionHandler.MonitoredThreadState.STUCK
/* 229 */           .ordinal());
/*     */     }
/*     */     
/*     */     public StuckThreadDetectionHandler.MonitoredThreadState markAsDone() {
/* 233 */       int val = this.state.getAndSet(StuckThreadDetectionHandler.MonitoredThreadState.DONE.ordinal());
/* 234 */       return StuckThreadDetectionHandler.MonitoredThreadState.values()[val];
/*     */     }
/*     */     
/*     */     boolean isMarkedAsStuck() {
/* 238 */       return (this.state.get() == StuckThreadDetectionHandler.MonitoredThreadState.STUCK.ordinal());
/*     */     }
/*     */   }
/*     */   
/*     */   private static class CompletedStuckThread
/*     */   {
/*     */     private final String threadName;
/*     */     private final long threadId;
/*     */     private final long totalActiveTime;
/*     */     
/*     */     CompletedStuckThread(Thread thread, long totalActiveTime) {
/* 249 */       this.threadName = thread.getName();
/* 250 */       this.threadId = thread.getId();
/* 251 */       this.totalActiveTime = totalActiveTime;
/*     */     }
/*     */     
/*     */     public String getName() {
/* 255 */       return this.threadName;
/*     */     }
/*     */     
/*     */     public long getId() {
/* 259 */       return this.threadId;
/*     */     }
/*     */     
/*     */     public long getTotalActiveTime() {
/* 263 */       return this.totalActiveTime;
/*     */     }
/*     */   }
/*     */   
/*     */   private enum MonitoredThreadState {
/* 268 */     RUNNING, STUCK, DONE;
/*     */   }
/*     */   
/*     */   public static final class Wrapper
/*     */     implements HandlerWrapper {
/*     */     private final int threshhold;
/*     */     
/*     */     public Wrapper(int threshhold) {
/* 276 */       this.threshhold = threshhold;
/*     */     }
/*     */     
/*     */     public Wrapper() {
/* 280 */       this.threshhold = 600;
/*     */     }
/*     */ 
/*     */     
/*     */     public HttpHandler wrap(HttpHandler handler) {
/* 285 */       return new StuckThreadDetectionHandler(this.threshhold, handler);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 291 */     return "stuck-thread-detector( " + this.threshold + " )";
/*     */   }
/*     */   
/*     */   public static class Builder
/*     */     implements HandlerBuilder
/*     */   {
/*     */     public String name() {
/* 298 */       return "stuck-thread-detector";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/* 303 */       return Collections.singletonMap("threshhold", Integer.class);
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/* 308 */       return Collections.emptySet();
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/* 313 */       return "threshhold";
/*     */     }
/*     */ 
/*     */     
/*     */     public HandlerWrapper build(Map<String, Object> config) {
/* 318 */       Integer threshhold = (Integer)config.get("threshhold");
/* 319 */       if (threshhold == null) {
/* 320 */         return new StuckThreadDetectionHandler.Wrapper();
/*     */       }
/* 322 */       return new StuckThreadDetectionHandler.Wrapper(threshhold.intValue());
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\StuckThreadDetectionHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */