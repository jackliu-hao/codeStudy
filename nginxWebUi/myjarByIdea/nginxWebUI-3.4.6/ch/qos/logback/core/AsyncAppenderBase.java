package ch.qos.logback.core;

import ch.qos.logback.core.spi.AppenderAttachable;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import ch.qos.logback.core.util.InterruptUtil;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class AsyncAppenderBase<E> extends UnsynchronizedAppenderBase<E> implements AppenderAttachable<E> {
   AppenderAttachableImpl<E> aai = new AppenderAttachableImpl();
   BlockingQueue<E> blockingQueue;
   public static final int DEFAULT_QUEUE_SIZE = 256;
   int queueSize = 256;
   int appenderCount = 0;
   static final int UNDEFINED = -1;
   int discardingThreshold = -1;
   boolean neverBlock = false;
   AsyncAppenderBase<E>.Worker worker = new Worker();
   public static final int DEFAULT_MAX_FLUSH_TIME = 1000;
   int maxFlushTime = 1000;

   protected boolean isDiscardable(E eventObject) {
      return false;
   }

   protected void preprocess(E eventObject) {
   }

   public void start() {
      if (!this.isStarted()) {
         if (this.appenderCount == 0) {
            this.addError("No attached appenders found.");
         } else if (this.queueSize < 1) {
            this.addError("Invalid queue size [" + this.queueSize + "]");
         } else {
            this.blockingQueue = new ArrayBlockingQueue(this.queueSize);
            if (this.discardingThreshold == -1) {
               this.discardingThreshold = this.queueSize / 5;
            }

            this.addInfo("Setting discardingThreshold to " + this.discardingThreshold);
            this.worker.setDaemon(true);
            this.worker.setName("AsyncAppender-Worker-" + this.getName());
            super.start();
            this.worker.start();
         }
      }
   }

   public void stop() {
      if (this.isStarted()) {
         super.stop();
         this.worker.interrupt();
         InterruptUtil interruptUtil = new InterruptUtil(this.context);

         try {
            interruptUtil.maskInterruptFlag();
            this.worker.join((long)this.maxFlushTime);
            if (this.worker.isAlive()) {
               this.addWarn("Max queue flush timeout (" + this.maxFlushTime + " ms) exceeded. Approximately " + this.blockingQueue.size() + " queued events were possibly discarded.");
            } else {
               this.addInfo("Queue flush finished successfully within timeout.");
            }
         } catch (InterruptedException var7) {
            int remaining = this.blockingQueue.size();
            this.addError("Failed to join worker thread. " + remaining + " queued events may be discarded.", var7);
         } finally {
            interruptUtil.unmaskInterruptFlag();
         }

      }
   }

   protected void append(E eventObject) {
      if (!this.isQueueBelowDiscardingThreshold() || !this.isDiscardable(eventObject)) {
         this.preprocess(eventObject);
         this.put(eventObject);
      }
   }

   private boolean isQueueBelowDiscardingThreshold() {
      return this.blockingQueue.remainingCapacity() < this.discardingThreshold;
   }

   private void put(E eventObject) {
      if (this.neverBlock) {
         this.blockingQueue.offer(eventObject);
      } else {
         this.putUninterruptibly(eventObject);
      }

   }

   private void putUninterruptibly(E eventObject) {
      boolean interrupted = false;

      try {
         while(true) {
            try {
               this.blockingQueue.put(eventObject);
               return;
            } catch (InterruptedException var7) {
               interrupted = true;
            }
         }
      } finally {
         if (interrupted) {
            Thread.currentThread().interrupt();
         }

      }
   }

   public int getQueueSize() {
      return this.queueSize;
   }

   public void setQueueSize(int queueSize) {
      this.queueSize = queueSize;
   }

   public int getDiscardingThreshold() {
      return this.discardingThreshold;
   }

   public void setDiscardingThreshold(int discardingThreshold) {
      this.discardingThreshold = discardingThreshold;
   }

   public int getMaxFlushTime() {
      return this.maxFlushTime;
   }

   public void setMaxFlushTime(int maxFlushTime) {
      this.maxFlushTime = maxFlushTime;
   }

   public int getNumberOfElementsInQueue() {
      return this.blockingQueue.size();
   }

   public void setNeverBlock(boolean neverBlock) {
      this.neverBlock = neverBlock;
   }

   public boolean isNeverBlock() {
      return this.neverBlock;
   }

   public int getRemainingCapacity() {
      return this.blockingQueue.remainingCapacity();
   }

   public void addAppender(Appender<E> newAppender) {
      if (this.appenderCount == 0) {
         ++this.appenderCount;
         this.addInfo("Attaching appender named [" + newAppender.getName() + "] to AsyncAppender.");
         this.aai.addAppender(newAppender);
      } else {
         this.addWarn("One and only one appender may be attached to AsyncAppender.");
         this.addWarn("Ignoring additional appender named [" + newAppender.getName() + "]");
      }

   }

   public Iterator<Appender<E>> iteratorForAppenders() {
      return this.aai.iteratorForAppenders();
   }

   public Appender<E> getAppender(String name) {
      return this.aai.getAppender(name);
   }

   public boolean isAttached(Appender<E> eAppender) {
      return this.aai.isAttached(eAppender);
   }

   public void detachAndStopAllAppenders() {
      this.aai.detachAndStopAllAppenders();
   }

   public boolean detachAppender(Appender<E> eAppender) {
      return this.aai.detachAppender(eAppender);
   }

   public boolean detachAppender(String name) {
      return this.aai.detachAppender(name);
   }

   class Worker extends Thread {
      public void run() {
         AsyncAppenderBase<E> parent = AsyncAppenderBase.this;
         AppenderAttachableImpl<E> aai = parent.aai;

         while(parent.isStarted()) {
            try {
               E e = parent.blockingQueue.take();
               aai.appendLoopOnAppenders(e);
            } catch (InterruptedException var5) {
               break;
            }
         }

         AsyncAppenderBase.this.addInfo("Worker thread will flush remaining events before exiting. ");
         Iterator var6 = parent.blockingQueue.iterator();

         while(var6.hasNext()) {
            E ex = var6.next();
            aai.appendLoopOnAppenders(ex);
            parent.blockingQueue.remove(ex);
         }

         aai.detachAndStopAllAppenders();
      }
   }
}
