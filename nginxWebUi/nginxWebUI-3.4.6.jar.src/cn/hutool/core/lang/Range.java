/*     */ package cn.hutool.core.lang;
/*     */ 
/*     */ import cn.hutool.core.thread.lock.NoLock;
/*     */ import java.io.Serializable;
/*     */ import java.util.Iterator;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReentrantLock;
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
/*     */ public class Range<T>
/*     */   implements Iterable<T>, Iterator<T>, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  29 */   private Lock lock = new ReentrantLock();
/*     */ 
/*     */ 
/*     */   
/*     */   private final T start;
/*     */ 
/*     */ 
/*     */   
/*     */   private final T end;
/*     */ 
/*     */ 
/*     */   
/*     */   private T next;
/*     */ 
/*     */ 
/*     */   
/*     */   private final Stepper<T> stepper;
/*     */ 
/*     */ 
/*     */   
/*  49 */   private int index = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final boolean includeStart;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final boolean includeEnd;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Range(T start, Stepper<T> stepper) {
/*  66 */     this(start, null, stepper);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Range(T start, T end, Stepper<T> stepper) {
/*  77 */     this(start, end, stepper, true, true);
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
/*     */   public Range(T start, T end, Stepper<T> stepper, boolean isIncludeStart, boolean isIncludeEnd) {
/*  90 */     Assert.notNull(start, "First element must be not null!", new Object[0]);
/*  91 */     this.start = start;
/*  92 */     this.end = end;
/*  93 */     this.stepper = stepper;
/*  94 */     this.next = safeStep(this.start);
/*  95 */     this.includeStart = isIncludeStart;
/*  96 */     this.includeEnd = isIncludeEnd;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Range<T> disableLock() {
/* 106 */     this.lock = (Lock)new NoLock();
/* 107 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasNext() {
/* 112 */     this.lock.lock();
/*     */     try {
/* 114 */       if (0 == this.index && this.includeStart) {
/* 115 */         return true;
/*     */       }
/* 117 */       if (null == this.next)
/* 118 */         return false; 
/* 119 */       if (false == this.includeEnd && this.next.equals(this.end)) {
/* 120 */         return false;
/*     */       }
/*     */     } finally {
/* 123 */       this.lock.unlock();
/*     */     } 
/* 125 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public T next() {
/* 130 */     this.lock.lock();
/*     */     try {
/* 132 */       if (false == hasNext()) {
/* 133 */         throw new NoSuchElementException("Has no next range!");
/*     */       }
/* 135 */       return nextUncheck();
/*     */     } finally {
/* 137 */       this.lock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private T nextUncheck() {
/*     */     T current;
/* 146 */     if (0 == this.index) {
/* 147 */       current = this.start;
/* 148 */       if (false == this.includeStart) {
/*     */         
/* 150 */         this.index++;
/* 151 */         return nextUncheck();
/*     */       } 
/*     */     } else {
/* 154 */       current = this.next;
/* 155 */       this.next = safeStep(this.next);
/*     */     } 
/*     */     
/* 158 */     this.index++;
/* 159 */     return current;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private T safeStep(T base) {
/* 169 */     int index = this.index;
/* 170 */     T next = null;
/*     */     try {
/* 172 */       next = this.stepper.step(base, this.end, index);
/* 173 */     } catch (Exception exception) {}
/*     */ 
/*     */ 
/*     */     
/* 177 */     return next;
/*     */   }
/*     */ 
/*     */   
/*     */   public void remove() {
/* 182 */     throw new UnsupportedOperationException("Can not remove ranged element!");
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<T> iterator() {
/* 187 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Range<T> reset() {
/* 196 */     this.lock.lock();
/*     */     try {
/* 198 */       this.index = 0;
/* 199 */       this.next = safeStep(this.start);
/*     */     } finally {
/* 201 */       this.lock.unlock();
/*     */     } 
/* 203 */     return this;
/*     */   }
/*     */   
/*     */   @FunctionalInterface
/*     */   public static interface Stepper<T> {
/*     */     T step(T param1T1, T param1T2, int param1Int);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\Range.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */