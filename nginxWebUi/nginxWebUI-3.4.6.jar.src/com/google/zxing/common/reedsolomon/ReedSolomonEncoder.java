/*    */ package com.google.zxing.common.reedsolomon;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class ReedSolomonEncoder
/*    */ {
/*    */   private final GenericGF field;
/*    */   private final List<GenericGFPoly> cachedGenerators;
/*    */   
/*    */   public ReedSolomonEncoder(GenericGF field) {
/* 34 */     this.field = field;
/* 35 */     this.cachedGenerators = new ArrayList<>();
/* 36 */     this.cachedGenerators.add(new GenericGFPoly(field, new int[] { 1 }));
/*    */   }
/*    */   
/*    */   private GenericGFPoly buildGenerator(int degree) {
/* 40 */     if (degree >= this.cachedGenerators.size()) {
/* 41 */       GenericGFPoly lastGenerator = this.cachedGenerators.get(this.cachedGenerators.size() - 1);
/* 42 */       for (int d = this.cachedGenerators.size(); d <= degree; d++) {
/* 43 */         GenericGFPoly nextGenerator = lastGenerator.multiply(new GenericGFPoly(this.field, new int[] { 1, this.field
/* 44 */                 .exp(d - 1 + this.field.getGeneratorBase()) }));
/* 45 */         this.cachedGenerators.add(nextGenerator);
/* 46 */         lastGenerator = nextGenerator;
/*    */       } 
/*    */     } 
/* 49 */     return this.cachedGenerators.get(degree);
/*    */   }
/*    */   
/*    */   public void encode(int[] toEncode, int ecBytes) {
/* 53 */     if (ecBytes == 0) {
/* 54 */       throw new IllegalArgumentException("No error correction bytes");
/*    */     }
/*    */     int dataBytes;
/* 57 */     if ((dataBytes = toEncode.length - ecBytes) <= 0) {
/* 58 */       throw new IllegalArgumentException("No data bytes provided");
/*    */     }
/* 60 */     GenericGFPoly generator = buildGenerator(ecBytes);
/* 61 */     int[] infoCoefficients = new int[dataBytes];
/* 62 */     System.arraycopy(toEncode, 0, infoCoefficients, 0, dataBytes);
/*    */ 
/*    */ 
/*    */     
/* 66 */     int[] coefficients = (new GenericGFPoly(this.field, infoCoefficients)).multiplyByMonomial(ecBytes, 1).divide(generator)[1].getCoefficients();
/* 67 */     int numZeroCoefficients = ecBytes - coefficients.length;
/* 68 */     for (int i = 0; i < numZeroCoefficients; i++) {
/* 69 */       toEncode[dataBytes + i] = 0;
/*    */     }
/* 71 */     System.arraycopy(coefficients, 0, toEncode, dataBytes + numZeroCoefficients, coefficients.length);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\common\reedsolomon\ReedSolomonEncoder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */