/*     */ package oshi.util;
/*     */ 
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.function.Supplier;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
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
/*     */ @ThreadSafe
/*     */ public final class Memoizer
/*     */ {
/*  39 */   private static final Supplier<Long> defaultExpirationNanos = memoize(Memoizer::queryExpirationConfig, TimeUnit.MINUTES
/*  40 */       .toNanos(1L));
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static long queryExpirationConfig() {
/*  46 */     return TimeUnit.MILLISECONDS.toNanos(GlobalConfig.get("oshi.util.memoizer.expiration", 300));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long defaultExpiration() {
/*  57 */     return ((Long)defaultExpirationNanos.get()).longValue();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> Supplier<T> memoize(final Supplier<T> original, final long ttlNanos) {
/*  75 */     return new Supplier<T>() {
/*  76 */         final Supplier<T> delegate = original;
/*     */         
/*     */         volatile T value;
/*     */         volatile long expirationNanos;
/*     */         
/*     */         public T get() {
/*  82 */           long nanos = this.expirationNanos;
/*  83 */           long now = System.nanoTime();
/*  84 */           if (nanos == 0L || (ttlNanos >= 0L && now - nanos >= 0L)) {
/*  85 */             synchronized (this) {
/*  86 */               if (nanos == this.expirationNanos) {
/*  87 */                 T t = this.delegate.get();
/*  88 */                 this.value = t;
/*  89 */                 nanos = now + ttlNanos;
/*  90 */                 this.expirationNanos = (nanos == 0L) ? 1L : nanos;
/*  91 */                 return t;
/*     */               } 
/*     */             } 
/*     */           }
/*  95 */           return this.value;
/*     */         }
/*     */       };
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
/*     */   public static <T> Supplier<T> memoize(Supplier<T> original) {
/* 110 */     return memoize(original, -1L);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\osh\\util\Memoizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */