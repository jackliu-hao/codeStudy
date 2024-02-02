/*     */ package ch.qos.logback.core.util;
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
/*     */ public class DefaultInvocationGate
/*     */   implements InvocationGate
/*     */ {
/*     */   static final int MASK_DECREASE_RIGHT_SHIFT_COUNT = 2;
/*     */   private static final int MAX_MASK = 65535;
/*     */   static final int DEFAULT_MASK = 15;
/*  30 */   private volatile long mask = 15L;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  37 */   private long invocationCounter = 0L;
/*     */   
/*     */   private static final long MASK_INCREASE_THRESHOLD = 100L;
/*     */   
/*     */   private static final long MASK_DECREASE_THRESHOLD = 800L;
/*     */   
/*     */   private long minDelayThreshold;
/*     */   private long maxDelayThreshold;
/*     */   long lowerLimitForMaskMatch;
/*     */   long upperLimitForNoMaskMatch;
/*     */   
/*     */   public DefaultInvocationGate() {
/*  49 */     this(100L, 800L, System.currentTimeMillis());
/*     */   }
/*     */   
/*     */   public DefaultInvocationGate(long minDelayThreshold, long maxDelayThreshold, long currentTime) {
/*  53 */     this.minDelayThreshold = minDelayThreshold;
/*  54 */     this.maxDelayThreshold = maxDelayThreshold;
/*  55 */     this.lowerLimitForMaskMatch = currentTime + minDelayThreshold;
/*  56 */     this.upperLimitForNoMaskMatch = currentTime + maxDelayThreshold;
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
/*     */   public final boolean isTooSoon(long currentTime) {
/*  72 */     boolean maskMatch = ((this.invocationCounter++ & this.mask) == this.mask);
/*     */     
/*  74 */     if (maskMatch) {
/*  75 */       if (currentTime < this.lowerLimitForMaskMatch) {
/*  76 */         increaseMask();
/*     */       }
/*  78 */       updateLimits(currentTime);
/*     */     }
/*  80 */     else if (currentTime > this.upperLimitForNoMaskMatch) {
/*  81 */       decreaseMask();
/*  82 */       updateLimits(currentTime);
/*  83 */       return false;
/*     */     } 
/*     */     
/*  86 */     return !maskMatch;
/*     */   }
/*     */   
/*     */   private void updateLimits(long currentTime) {
/*  90 */     this.lowerLimitForMaskMatch = currentTime + this.minDelayThreshold;
/*  91 */     this.upperLimitForNoMaskMatch = currentTime + this.maxDelayThreshold;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   long getMask() {
/*  97 */     return this.mask;
/*     */   }
/*     */   
/*     */   private void increaseMask() {
/* 101 */     if (this.mask >= 65535L)
/*     */       return; 
/* 103 */     this.mask = this.mask << 1L | 0x1L;
/*     */   }
/*     */   
/*     */   private void decreaseMask() {
/* 107 */     this.mask >>>= 2L;
/*     */   }
/*     */   
/*     */   public long getInvocationCounter() {
/* 111 */     return this.invocationCounter;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\cor\\util\DefaultInvocationGate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */