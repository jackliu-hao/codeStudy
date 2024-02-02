/*     */ package com.google.zxing.common.reedsolomon;
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
/*     */ public final class GenericGF
/*     */ {
/*  32 */   public static final GenericGF AZTEC_DATA_12 = new GenericGF(4201, 4096, 1);
/*  33 */   public static final GenericGF AZTEC_DATA_10 = new GenericGF(1033, 1024, 1);
/*  34 */   public static final GenericGF AZTEC_DATA_6 = new GenericGF(67, 64, 1);
/*  35 */   public static final GenericGF AZTEC_PARAM = new GenericGF(19, 16, 1);
/*  36 */   public static final GenericGF QR_CODE_FIELD_256 = new GenericGF(285, 256, 0);
/*     */   public static final GenericGF DATA_MATRIX_FIELD_256;
/*  38 */   public static final GenericGF AZTEC_DATA_8 = DATA_MATRIX_FIELD_256 = new GenericGF(301, 256, 1);
/*  39 */   public static final GenericGF MAXICODE_FIELD_64 = AZTEC_DATA_6;
/*     */ 
/*     */   
/*     */   private final int[] expTable;
/*     */ 
/*     */   
/*     */   private final int[] logTable;
/*     */ 
/*     */   
/*     */   private final GenericGFPoly zero;
/*     */ 
/*     */   
/*     */   private final GenericGFPoly one;
/*     */   
/*     */   private final int size;
/*     */   
/*     */   private final int primitive;
/*     */   
/*     */   private final int generatorBase;
/*     */ 
/*     */   
/*     */   public GenericGF(int primitive, int size, int b) {
/*  61 */     this.primitive = primitive;
/*  62 */     this.size = size;
/*  63 */     this.generatorBase = b;
/*     */     
/*  65 */     this.expTable = new int[size];
/*  66 */     this.logTable = new int[size];
/*  67 */     int x = 1; int i;
/*  68 */     for (i = 0; i < size; i++) {
/*  69 */       this.expTable[i] = x;
/*     */       
/*  71 */       if ((x = x << 1) >= size)
/*     */       {
/*  73 */         x = (x ^ primitive) & size - 1;
/*     */       }
/*     */     } 
/*  76 */     for (i = 0; i < size - 1; i++) {
/*  77 */       this.logTable[this.expTable[i]] = i;
/*     */     }
/*     */     
/*  80 */     this.zero = new GenericGFPoly(this, new int[] { 0 });
/*  81 */     this.one = new GenericGFPoly(this, new int[] { 1 });
/*     */   }
/*     */   
/*     */   GenericGFPoly getZero() {
/*  85 */     return this.zero;
/*     */   }
/*     */   
/*     */   GenericGFPoly getOne() {
/*  89 */     return this.one;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   GenericGFPoly buildMonomial(int degree, int coefficient) {
/*  96 */     if (degree < 0) {
/*  97 */       throw new IllegalArgumentException();
/*     */     }
/*  99 */     if (coefficient == 0) {
/* 100 */       return this.zero;
/*     */     }
/*     */     int[] coefficients;
/* 103 */     (coefficients = new int[degree + 1])[0] = coefficient;
/* 104 */     return new GenericGFPoly(this, coefficients);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static int addOrSubtract(int a, int b) {
/* 113 */     return a ^ b;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int exp(int a) {
/* 120 */     return this.expTable[a];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int log(int a) {
/* 127 */     if (a == 0) {
/* 128 */       throw new IllegalArgumentException();
/*     */     }
/* 130 */     return this.logTable[a];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int inverse(int a) {
/* 137 */     if (a == 0) {
/* 138 */       throw new ArithmeticException();
/*     */     }
/* 140 */     return this.expTable[this.size - this.logTable[a] - 1];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int multiply(int a, int b) {
/* 147 */     if (a == 0 || b == 0) {
/* 148 */       return 0;
/*     */     }
/* 150 */     return this.expTable[(this.logTable[a] + this.logTable[b]) % (this.size - 1)];
/*     */   }
/*     */   
/*     */   public int getSize() {
/* 154 */     return this.size;
/*     */   }
/*     */   
/*     */   public int getGeneratorBase() {
/* 158 */     return this.generatorBase;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 163 */     return "GF(0x" + Integer.toHexString(this.primitive) + ',' + this.size + ')';
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\common\reedsolomon\GenericGF.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */