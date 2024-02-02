/*     */ package org.wildfly.common.lock;
/*     */ 
/*     */ import java.security.AccessController;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.locks.Condition;
/*     */ import org.wildfly.common.Assert;
/*     */ import org.wildfly.common.cpu.ProcessorInfo;
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
/*     */ public class SpinLock
/*     */   implements ExtendedLock
/*     */ {
/*     */   private static final long ownerOffset;
/*     */   
/*     */   static {
/*     */     try {
/*  44 */       ownerOffset = JDKSpecific.unsafe.objectFieldOffset(SpinLock.class.getDeclaredField("owner"));
/*  45 */     } catch (NoSuchFieldException e) {
/*  46 */       throw new NoSuchFieldError(e.getMessage());
/*     */     } 
/*  48 */   } private static final int spinLimit = ((Integer)AccessController.<Integer>doPrivileged(() -> Integer.valueOf(System.getProperty("jboss.spin-lock.limit", (ProcessorInfo.availableProcessors() == 1) ? "0" : "5000"))))
/*     */ 
/*     */ 
/*     */     
/*  52 */     .intValue();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile Thread owner;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int level;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isLocked() {
/*  71 */     return (this.owner != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isHeldByCurrentThread() {
/*  80 */     return (this.owner == Thread.currentThread());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isFair() {
/*  89 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void lock() {
/*  97 */     int spins = 0;
/*     */     while (true) {
/*  99 */       Thread owner = this.owner;
/* 100 */       if (owner == Thread.currentThread()) {
/* 101 */         this.level++; return;
/*     */       } 
/* 103 */       if (owner == null && JDKSpecific.unsafe.compareAndSwapObject(this, ownerOffset, null, Thread.currentThread())) {
/* 104 */         this.level = 1; return;
/*     */       } 
/* 106 */       if (spins >= spinLimit) {
/* 107 */         Thread.yield(); continue;
/*     */       } 
/* 109 */       JDKSpecific.onSpinWait();
/* 110 */       spins++;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void lockInterruptibly() throws InterruptedException {
/* 122 */     int spins = 0;
/*     */     while (true) {
/* 124 */       if (Thread.interrupted()) throw new InterruptedException(); 
/* 125 */       Thread owner = this.owner;
/* 126 */       if (owner == Thread.currentThread()) {
/* 127 */         this.level++; return;
/*     */       } 
/* 129 */       if (owner == null && JDKSpecific.unsafe.compareAndSwapObject(this, ownerOffset, null, Thread.currentThread())) {
/* 130 */         this.level = 1; return;
/*     */       } 
/* 132 */       if (spins >= spinLimit) {
/* 133 */         Thread.yield(); continue;
/*     */       } 
/* 135 */       JDKSpecific.onSpinWait();
/* 136 */       spins++;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean tryLock() {
/* 147 */     Thread owner = this.owner;
/* 148 */     if (owner == Thread.currentThread()) {
/* 149 */       this.level++;
/* 150 */       return true;
/* 151 */     }  if (owner == null && JDKSpecific.unsafe.compareAndSwapObject(this, ownerOffset, null, Thread.currentThread())) {
/* 152 */       this.level = 1;
/* 153 */       return true;
/*     */     } 
/* 155 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void unlock() {
/* 165 */     Thread owner = this.owner;
/* 166 */     if (owner == Thread.currentThread()) {
/* 167 */       if (--this.level == 0) this.owner = null; 
/*     */     } else {
/* 169 */       throw new IllegalMonitorStateException();
/*     */     } 
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
/*     */   public boolean tryLock(long time, TimeUnit unit) throws UnsupportedOperationException {
/* 182 */     throw Assert.unsupported();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Condition newCondition() throws UnsupportedOperationException {
/* 192 */     throw Assert.unsupported();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\lock\SpinLock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */