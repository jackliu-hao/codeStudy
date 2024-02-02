/*     */ package org.wildfly.common.math;
/*     */ 
/*     */ import org.wildfly.common.Assert;
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
/*     */ public final class HashMath
/*     */ {
/*     */   private static final int PRESELECTED_PRIME = 1299827;
/*     */   
/*     */   public static int roundToPowerOfTwo(int value) {
/*  42 */     Assert.checkMinimumParameter("value", 0, value);
/*  43 */     Assert.checkMaximumParameter("value", 1073741824, value);
/*  44 */     return (value <= 1) ? value : (Integer.highestOneBit(value - 1) << 1);
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
/*     */   public static int multiHashOrdered(int accumulatedHash, int prime, int nextHash) {
/*  60 */     return multiplyWrap(accumulatedHash, prime) + nextHash;
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
/*     */   public static int multiHashUnordered(int accumulatedHash, int prime, int nextHash) {
/*  76 */     return multiplyWrap(nextHash, prime) + accumulatedHash;
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
/*     */   public static int multiHashOrdered(int accumulatedHash, int nextHash) {
/*  91 */     return multiHashOrdered(accumulatedHash, 1299827, nextHash);
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
/*     */   public static int multiHashUnordered(int accumulatedHash, int nextHash) {
/* 106 */     return multiHashUnordered(accumulatedHash, 1299827, nextHash);
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
/*     */   public static int multiplyWrap(int a, int b) {
/* 120 */     long r1 = a * b;
/* 121 */     return (int)r1 ^ (int)(r1 >>> 32L);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\math\HashMath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */