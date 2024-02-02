/*     */ package com.google.zxing.pdf417.decoder.ec;
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
/*     */ public final class ModulusGF
/*     */ {
/*  29 */   public static final ModulusGF PDF417_GF = new ModulusGF(929, 3);
/*     */   
/*     */   private final int[] expTable;
/*     */   private final int[] logTable;
/*     */   private final ModulusPoly zero;
/*     */   private final ModulusPoly one;
/*     */   private final int modulus;
/*     */   
/*     */   private ModulusGF(int modulus, int generator) {
/*  38 */     this.modulus = modulus;
/*  39 */     this.expTable = new int[modulus];
/*  40 */     this.logTable = new int[modulus];
/*  41 */     int x = 1; int i;
/*  42 */     for (i = 0; i < modulus; i++) {
/*  43 */       this.expTable[i] = x;
/*  44 */       x = x * generator % modulus;
/*     */     } 
/*  46 */     for (i = 0; i < modulus - 1; i++) {
/*  47 */       this.logTable[this.expTable[i]] = i;
/*     */     }
/*     */     
/*  50 */     this.zero = new ModulusPoly(this, new int[] { 0 });
/*  51 */     this.one = new ModulusPoly(this, new int[] { 1 });
/*     */   }
/*     */ 
/*     */   
/*     */   ModulusPoly getZero() {
/*  56 */     return this.zero;
/*     */   }
/*     */   
/*     */   ModulusPoly getOne() {
/*  60 */     return this.one;
/*     */   }
/*     */   
/*     */   ModulusPoly buildMonomial(int degree, int coefficient) {
/*  64 */     if (degree < 0) {
/*  65 */       throw new IllegalArgumentException();
/*     */     }
/*  67 */     if (coefficient == 0) {
/*  68 */       return this.zero;
/*     */     }
/*     */     int[] coefficients;
/*  71 */     (coefficients = new int[degree + 1])[0] = coefficient;
/*  72 */     return new ModulusPoly(this, coefficients);
/*     */   }
/*     */   
/*     */   int add(int a, int b) {
/*  76 */     return (a + b) % this.modulus;
/*     */   }
/*     */   
/*     */   int subtract(int a, int b) {
/*  80 */     return (this.modulus + a - b) % this.modulus;
/*     */   }
/*     */   
/*     */   int exp(int a) {
/*  84 */     return this.expTable[a];
/*     */   }
/*     */   
/*     */   int log(int a) {
/*  88 */     if (a == 0) {
/*  89 */       throw new IllegalArgumentException();
/*     */     }
/*  91 */     return this.logTable[a];
/*     */   }
/*     */   
/*     */   int inverse(int a) {
/*  95 */     if (a == 0) {
/*  96 */       throw new ArithmeticException();
/*     */     }
/*  98 */     return this.expTable[this.modulus - this.logTable[a] - 1];
/*     */   }
/*     */   
/*     */   int multiply(int a, int b) {
/* 102 */     if (a == 0 || b == 0) {
/* 103 */       return 0;
/*     */     }
/* 105 */     return this.expTable[(this.logTable[a] + this.logTable[b]) % (this.modulus - 1)];
/*     */   }
/*     */   
/*     */   int getSize() {
/* 109 */     return this.modulus;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\pdf417\decoder\ec\ModulusGF.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */