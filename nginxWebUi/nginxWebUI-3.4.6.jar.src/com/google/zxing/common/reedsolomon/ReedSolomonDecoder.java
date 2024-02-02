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
/*     */ public final class ReedSolomonDecoder
/*     */ {
/*     */   private final GenericGF field;
/*     */   
/*     */   public ReedSolomonDecoder(GenericGF field) {
/*  46 */     this.field = field;
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
/*     */   public void decode(int[] received, int twoS) throws ReedSolomonException {
/*  59 */     GenericGFPoly poly = new GenericGFPoly(this.field, received);
/*  60 */     int[] syndromeCoefficients = new int[twoS];
/*  61 */     boolean noError = true;
/*  62 */     for (int i = 0; i < twoS; i++) {
/*  63 */       int eval = poly.evaluateAt(this.field.exp(i + this.field.getGeneratorBase()));
/*  64 */       syndromeCoefficients[twoS - 1 - i] = eval;
/*  65 */       if (eval != 0) {
/*  66 */         noError = false;
/*     */       }
/*     */     } 
/*  69 */     if (noError) {
/*     */       return;
/*     */     }
/*  72 */     GenericGFPoly syndrome = new GenericGFPoly(this.field, syndromeCoefficients);
/*     */ 
/*     */     
/*  75 */     GenericGFPoly sigmaOmega[], sigma = (sigmaOmega = runEuclideanAlgorithm(this.field.buildMonomial(twoS, 1), syndrome, twoS))[0];
/*  76 */     GenericGFPoly omega = sigmaOmega[1];
/*  77 */     int[] errorLocations = findErrorLocations(sigma);
/*  78 */     int[] errorMagnitudes = findErrorMagnitudes(omega, errorLocations);
/*  79 */     for (int j = 0; j < errorLocations.length; j++) {
/*     */       int position;
/*  81 */       if ((position = received.length - 1 - this.field.log(errorLocations[j])) < 0) {
/*  82 */         throw new ReedSolomonException("Bad error location");
/*     */       }
/*  84 */       received[position] = GenericGF.addOrSubtract(received[position], errorMagnitudes[j]);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private GenericGFPoly[] runEuclideanAlgorithm(GenericGFPoly a, GenericGFPoly b, int R) throws ReedSolomonException {
/*  91 */     if (a.getDegree() < b.getDegree()) {
/*  92 */       GenericGFPoly temp = a;
/*  93 */       a = b;
/*  94 */       b = temp;
/*     */     } 
/*     */     
/*  97 */     GenericGFPoly rLast = a;
/*  98 */     GenericGFPoly r = b;
/*  99 */     GenericGFPoly tLast = this.field.getZero();
/* 100 */     GenericGFPoly t = this.field.getOne();
/*     */ 
/*     */     
/* 103 */     while (r.getDegree() >= R / 2) {
/* 104 */       GenericGFPoly rLastLast = rLast;
/* 105 */       GenericGFPoly tLastLast = tLast;
/* 106 */       rLast = r;
/* 107 */       tLast = t;
/*     */ 
/*     */       
/* 110 */       if (rLast.isZero())
/*     */       {
/* 112 */         throw new ReedSolomonException("r_{i-1} was zero");
/*     */       }
/* 114 */       r = rLastLast;
/* 115 */       GenericGFPoly q = this.field.getZero();
/* 116 */       int denominatorLeadingTerm = rLast.getCoefficient(rLast.getDegree());
/* 117 */       int dltInverse = this.field.inverse(denominatorLeadingTerm);
/* 118 */       while (r.getDegree() >= rLast.getDegree() && !r.isZero()) {
/* 119 */         int degreeDiff = r.getDegree() - rLast.getDegree();
/* 120 */         int scale = this.field.multiply(r.getCoefficient(r.getDegree()), dltInverse);
/* 121 */         q = q.addOrSubtract(this.field.buildMonomial(degreeDiff, scale));
/* 122 */         r = r.addOrSubtract(rLast.multiplyByMonomial(degreeDiff, scale));
/*     */       } 
/*     */       
/* 125 */       t = q.multiply(tLast).addOrSubtract(tLastLast);
/*     */       
/* 127 */       if (r.getDegree() >= rLast.getDegree()) {
/* 128 */         throw new IllegalStateException("Division algorithm failed to reduce polynomial?");
/*     */       }
/*     */     } 
/*     */     
/*     */     int sigmaTildeAtZero;
/* 133 */     if ((sigmaTildeAtZero = t.getCoefficient(0)) == 0) {
/* 134 */       throw new ReedSolomonException("sigmaTilde(0) was zero");
/*     */     }
/*     */     
/* 137 */     int inverse = this.field.inverse(sigmaTildeAtZero);
/* 138 */     GenericGFPoly sigma = t.multiply(inverse);
/* 139 */     GenericGFPoly omega = r.multiply(inverse);
/* 140 */     return new GenericGFPoly[] { sigma, omega };
/*     */   }
/*     */ 
/*     */   
/*     */   private int[] findErrorLocations(GenericGFPoly errorLocator) throws ReedSolomonException {
/*     */     int numErrors;
/* 146 */     if ((numErrors = errorLocator.getDegree()) == 1) {
/* 147 */       return new int[] { errorLocator.getCoefficient(1) };
/*     */     }
/* 149 */     int[] result = new int[numErrors];
/* 150 */     int e = 0;
/* 151 */     for (int i = 1; i < this.field.getSize() && e < numErrors; i++) {
/* 152 */       if (errorLocator.evaluateAt(i) == 0) {
/* 153 */         result[e] = this.field.inverse(i);
/* 154 */         e++;
/*     */       } 
/*     */     } 
/* 157 */     if (e != numErrors) {
/* 158 */       throw new ReedSolomonException("Error locator degree does not match number of roots");
/*     */     }
/* 160 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private int[] findErrorMagnitudes(GenericGFPoly errorEvaluator, int[] errorLocations) {
/* 166 */     int s, result[] = new int[s = errorLocations.length];
/* 167 */     for (int i = 0; i < s; i++) {
/* 168 */       int xiInverse = this.field.inverse(errorLocations[i]);
/* 169 */       int denominator = 1;
/* 170 */       for (int j = 0; j < s; j++) {
/* 171 */         if (i != j) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 177 */           int term, termPlus1 = (((term = this.field.multiply(errorLocations[j], xiInverse)) & 0x1) == 0) ? (term | 0x1) : (term & 0xFFFFFFFE);
/* 178 */           denominator = this.field.multiply(denominator, termPlus1);
/*     */         } 
/*     */       } 
/* 181 */       result[i] = this.field.multiply(errorEvaluator.evaluateAt(xiInverse), this.field
/* 182 */           .inverse(denominator));
/* 183 */       if (this.field.getGeneratorBase() != 0) {
/* 184 */         result[i] = this.field.multiply(result[i], xiInverse);
/*     */       }
/*     */     } 
/* 187 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\common\reedsolomon\ReedSolomonDecoder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */