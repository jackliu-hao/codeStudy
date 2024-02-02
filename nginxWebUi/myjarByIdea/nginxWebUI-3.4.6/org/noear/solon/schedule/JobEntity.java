package org.noear.solon.schedule;

import java.util.Date;
import org.noear.solon.Utils;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.schedule.cron.CronExpressionPlus;

class JobEntity extends Thread {
   final CronExpressionPlus cron;
   final long fixedRate;
   final long fixedDelay;
   final Runnable runnable;
   final boolean concurrent;
   private boolean isCanceled;
   private long sleepMillis;
   private Date baseTime;
   private Date nextTime;

   public JobEntity(String name, long fixedRate, long fixedDelay, boolean concurrent, Runnable runnable) {
      this(name, (CronExpressionPlus)null, fixedRate, fixedDelay, concurrent, runnable);
   }

   public JobEntity(String name, CronExpressionPlus cron, boolean concurrent, Runnable runnable) {
      this(name, cron, 0L, 0L, concurrent, runnable);
   }

   private JobEntity(String name, CronExpressionPlus cron, long fixedRate, long fixedDelay, boolean concurrent, Runnable runnable) {
      this.cron = cron;
      this.fixedRate = fixedRate;
      this.fixedDelay = fixedDelay;
      this.runnable = runnable;
      this.concurrent = concurrent;
      this.baseTime = new Date();
      if (Utils.isNotEmpty(name)) {
         this.setName("Job:" + name);
      }

   }

   public void cancel() {
      this.isCanceled = true;
   }

   public void run() {
      if (this.fixedDelay > 0L) {
         this.sleep0(this.fixedDelay);
      }

      while(true) {
         while(this.isCanceled) {
         }

         try {
            this.scheduling();
         } catch (Throwable var2) {
            Throwable e = Utils.throwableUnwrap(var2);
            EventBus.push(new ScheduledException(e));
         }
      }
   }

   private void scheduling() throws Throwable {
      if (this.fixedRate > 0L) {
         this.sleepMillis = System.currentTimeMillis() - this.baseTime.getTime();
         if (this.sleepMillis >= this.fixedRate) {
            this.baseTime = new Date();
            this.exec();
            this.sleepMillis = this.fixedRate;
         } else {
            this.sleepMillis = 100L;
         }

         this.sleep0(this.sleepMillis);
      } else {
         this.nextTime = this.cron.getNextValidTimeAfter(this.baseTime);
         this.sleepMillis = System.currentTimeMillis() - this.nextTime.getTime();
         if (this.sleepMillis >= 0L) {
            this.baseTime = this.nextTime;
            this.nextTime = this.cron.getNextValidTimeAfter(this.baseTime);
            if (this.sleepMillis <= 1000L) {
               this.exec();
               if (this.concurrent) {
                  this.sleepMillis = System.currentTimeMillis() - this.nextTime.getTime();
               } else {
                  this.baseTime = new Date();
                  this.nextTime = this.cron.getNextValidTimeAfter(this.baseTime);
                  this.sleepMillis = System.currentTimeMillis() - this.nextTime.getTime();
               }
            }
         }

         this.sleep0(this.sleepMillis);
      }

   }

   private void exec() {
      if (this.concurrent) {
         Utils.pools.submit(this::exec0);
      } else {
         this.exec0();
      }

   }

   private void exec0() {
      try {
         if (this.concurrent) {
            Thread.currentThread().setName(this.getName());
         }

         this.runnable.run();
      } catch (Throwable var2) {
         EventBus.push(var2);
      }

   }

   private void sleep0(long sleep) {
      if (sleep < 0L) {
         sleep = 100L;
      }

      try {
         Thread.sleep(sleep);
      } catch (Exception var4) {
         EventBus.push(var4);
      }

   }
}
