/*     */ package org.noear.solon.schedule;
/*     */ 
/*     */ import java.util.Date;
/*     */ import org.noear.solon.Utils;
/*     */ import org.noear.solon.core.event.EventBus;
/*     */ import org.noear.solon.schedule.cron.CronExpressionPlus;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class JobEntity
/*     */   extends Thread
/*     */ {
/*     */   final CronExpressionPlus cron;
/*     */   final long fixedRate;
/*     */   final long fixedDelay;
/*     */   final Runnable runnable;
/*     */   final boolean concurrent;
/*     */   private boolean isCanceled;
/*     */   private long sleepMillis;
/*     */   private Date baseTime;
/*     */   private Date nextTime;
/*     */   
/*     */   public JobEntity(String name, long fixedRate, long fixedDelay, boolean concurrent, Runnable runnable) {
/*  59 */     this(name, (CronExpressionPlus)null, fixedRate, fixedDelay, concurrent, runnable);
/*     */   }
/*     */   
/*     */   public JobEntity(String name, CronExpressionPlus cron, boolean concurrent, Runnable runnable) {
/*  63 */     this(name, cron, 0L, 0L, concurrent, runnable);
/*     */   }
/*     */   
/*     */   private JobEntity(String name, CronExpressionPlus cron, long fixedRate, long fixedDelay, boolean concurrent, Runnable runnable) {
/*  67 */     this.cron = cron;
/*  68 */     this.fixedRate = fixedRate;
/*  69 */     this.fixedDelay = fixedDelay;
/*  70 */     this.runnable = runnable;
/*  71 */     this.concurrent = concurrent;
/*     */     
/*  73 */     this.baseTime = new Date();
/*     */     
/*  75 */     if (Utils.isNotEmpty(name)) {
/*  76 */       setName("Job:" + name);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void cancel() {
/*  84 */     this.isCanceled = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void run() {
/*  92 */     if (this.fixedDelay > 0L) {
/*  93 */       sleep0(this.fixedDelay);
/*     */     }
/*     */     
/*     */     while (true) {
/*  97 */       if (!this.isCanceled) {
/*     */         try {
/*  99 */           scheduling();
/* 100 */         } catch (Throwable e) {
/* 101 */           e = Utils.throwableUnwrap(e);
/* 102 */           EventBus.push(new ScheduledException(e));
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void scheduling() throws Throwable {
/* 112 */     if (this.fixedRate > 0L) {
/*     */       
/* 114 */       this.sleepMillis = System.currentTimeMillis() - this.baseTime.getTime();
/*     */       
/* 116 */       if (this.sleepMillis >= this.fixedRate) {
/* 117 */         this.baseTime = new Date();
/* 118 */         exec();
/*     */ 
/*     */         
/* 121 */         this.sleepMillis = this.fixedRate;
/*     */       } else {
/*     */         
/* 124 */         this.sleepMillis = 100L;
/*     */       } 
/*     */       
/* 127 */       sleep0(this.sleepMillis);
/*     */     } else {
/*     */       
/* 130 */       this.nextTime = this.cron.getNextValidTimeAfter(this.baseTime);
/* 131 */       this.sleepMillis = System.currentTimeMillis() - this.nextTime.getTime();
/*     */       
/* 133 */       if (this.sleepMillis >= 0L) {
/* 134 */         this.baseTime = this.nextTime;
/* 135 */         this.nextTime = this.cron.getNextValidTimeAfter(this.baseTime);
/*     */         
/* 137 */         if (this.sleepMillis <= 1000L) {
/* 138 */           exec();
/*     */ 
/*     */           
/* 141 */           if (this.concurrent) {
/* 142 */             this.sleepMillis = System.currentTimeMillis() - this.nextTime.getTime();
/*     */           } else {
/* 144 */             this.baseTime = new Date();
/*     */             
/* 146 */             this.nextTime = this.cron.getNextValidTimeAfter(this.baseTime);
/* 147 */             this.sleepMillis = System.currentTimeMillis() - this.nextTime.getTime();
/*     */           } 
/*     */         } 
/*     */       } 
/*     */       
/* 152 */       sleep0(this.sleepMillis);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void exec() {
/* 158 */     if (this.concurrent) {
/* 159 */       Utils.pools.submit(this::exec0);
/*     */     } else {
/* 161 */       exec0();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void exec0() {
/*     */     try {
/* 167 */       if (this.concurrent) {
/* 168 */         Thread.currentThread().setName(getName());
/*     */       }
/*     */       
/* 171 */       this.runnable.run();
/* 172 */     } catch (Throwable e) {
/* 173 */       EventBus.push(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void sleep0(long sleep) {
/* 178 */     if (sleep < 0L) {
/* 179 */       sleep = 100L;
/*     */     }
/*     */     
/*     */     try {
/* 183 */       Thread.sleep(sleep);
/* 184 */     } catch (Exception e) {
/* 185 */       EventBus.push(e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\schedule\JobEntity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */