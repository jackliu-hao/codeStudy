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
/*     */ final class GenericGFPoly
/*     */ {
/*     */   private final GenericGF field;
/*     */   private final int[] coefficients;
/*     */   
/*     */   GenericGFPoly(GenericGF field, int[] coefficients) {
/*  43 */     if (coefficients.length == 0) {
/*  44 */       throw new IllegalArgumentException();
/*     */     }
/*  46 */     this.field = field;
/*     */     int coefficientsLength;
/*  48 */     if ((coefficientsLength = coefficients.length) > 1 && coefficients[0] == 0) {
/*     */       
/*  50 */       int firstNonZero = 1;
/*  51 */       while (firstNonZero < coefficientsLength && coefficients[firstNonZero] == 0) {
/*  52 */         firstNonZero++;
/*     */       }
/*  54 */       if (firstNonZero == coefficientsLength) {
/*  55 */         this.coefficients = new int[] { 0 }; return;
/*     */       } 
/*  57 */       this.coefficients = new int[coefficientsLength - firstNonZero];
/*  58 */       System.arraycopy(coefficients, firstNonZero, this.coefficients, 0, this.coefficients.length);
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */ 
/*     */     
/*  65 */     this.coefficients = coefficients;
/*     */   }
/*     */ 
/*     */   
/*     */   int[] getCoefficients() {
/*  70 */     return this.coefficients;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int getDegree() {
/*  77 */     return this.coefficients.length - 1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isZero() {
/*  84 */     return (this.coefficients[0] == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int getCoefficient(int degree) {
/*  91 */     return this.coefficients[this.coefficients.length - 1 - degree];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int evaluateAt(int a) {
/*  98 */     if (a == 0)
/*     */     {
/* 100 */       return getCoefficient(0);
/*     */     }
/* 102 */     if (a == 1) {
/*     */       
/* 104 */       int j = 0; int arrayOfInt[], k; byte b;
/* 105 */       for (k = (arrayOfInt = this.coefficients).length, b = 0; b < k; ) { int coefficient = arrayOfInt[b];
/* 106 */         j = GenericGF.addOrSubtract(j, coefficient); b++; }
/*     */       
/* 108 */       return j;
/*     */     } 
/* 110 */     int result = this.coefficients[0];
/* 111 */     int size = this.coefficients.length;
/* 112 */     for (int i = 1; i < size; i++) {
/* 113 */       result = GenericGF.addOrSubtract(this.field.multiply(a, result), this.coefficients[i]);
/*     */     }
/* 115 */     return result;
/*     */   }
/*     */   
/*     */   GenericGFPoly addOrSubtract(GenericGFPoly other) {
/* 119 */     if (!this.field.equals(other.field)) {
/* 120 */       throw new IllegalArgumentException("GenericGFPolys do not have same GenericGF field");
/*     */     }
/* 122 */     if (isZero()) {
/* 123 */       return other;
/*     */     }
/* 125 */     if (other.isZero()) {
/* 126 */       return this;
/*     */     }
/*     */     
/* 129 */     int[] smallerCoefficients = this.coefficients;
/* 130 */     int[] largerCoefficients = other.coefficients;
/* 131 */     if (smallerCoefficients.length > largerCoefficients.length) {
/* 132 */       int[] temp = smallerCoefficients;
/* 133 */       smallerCoefficients = largerCoefficients;
/* 134 */       largerCoefficients = temp;
/*     */     } 
/* 136 */     int[] sumDiff = new int[largerCoefficients.length];
/* 137 */     int lengthDiff = largerCoefficients.length - smallerCoefficients.length;
/*     */     
/* 139 */     System.arraycopy(largerCoefficients, 0, sumDiff, 0, lengthDiff);
/*     */     
/* 141 */     for (int i = lengthDiff; i < largerCoefficients.length; i++) {
/* 142 */       sumDiff[i] = GenericGF.addOrSubtract(smallerCoefficients[i - lengthDiff], largerCoefficients[i]);
/*     */     }
/*     */     
/* 145 */     return new GenericGFPoly(this.field, sumDiff);
/*     */   }
/*     */   
/*     */   GenericGFPoly multiply(GenericGFPoly other) {
/* 149 */     if (!this.field.equals(other.field)) {
/* 150 */       throw new IllegalArgumentException("GenericGFPolys do not have same GenericGF field");
/*     */     }
/* 152 */     if (isZero() || other.isZero()) {
/* 153 */       return this.field.getZero();
/*     */     }
/*     */     
/* 156 */     int aCoefficients[], aLength = (aCoefficients = this.coefficients).length;
/*     */     
/* 158 */     int bCoefficients[], bLength = (bCoefficients = other.coefficients).length;
/* 159 */     int[] product = new int[aLength + bLength - 1];
/* 160 */     for (int i = 0; i < aLength; i++) {
/* 161 */       int aCoeff = aCoefficients[i];
/* 162 */       for (int j = 0; j < bLength; j++) {
/* 163 */         product[i + j] = GenericGF.addOrSubtract(product[i + j], this.field
/* 164 */             .multiply(aCoeff, bCoefficients[j]));
/*     */       }
/*     */     } 
/* 167 */     return new GenericGFPoly(this.field, product);
/*     */   }
/*     */   
/*     */   GenericGFPoly multiply(int scalar) {
/* 171 */     if (scalar == 0) {
/* 172 */       return this.field.getZero();
/*     */     }
/* 174 */     if (scalar == 1) {
/* 175 */       return this;
/*     */     }
/*     */     
/* 178 */     int size, product[] = new int[size = this.coefficients.length];
/* 179 */     for (int i = 0; i < size; i++) {
/* 180 */       product[i] = this.field.multiply(this.coefficients[i], scalar);
/*     */     }
/* 182 */     return new GenericGFPoly(this.field, product);
/*     */   }
/*     */   
/*     */   GenericGFPoly multiplyByMonomial(int degree, int coefficient) {
/* 186 */     if (degree < 0) {
/* 187 */       throw new IllegalArgumentException();
/*     */     }
/* 189 */     if (coefficient == 0) {
/* 190 */       return this.field.getZero();
/*     */     }
/*     */     
/* 193 */     int size, product[] = new int[(size = this.coefficients.length) + degree];
/* 194 */     for (int i = 0; i < size; i++) {
/* 195 */       product[i] = this.field.multiply(this.coefficients[i], coefficient);
/*     */     }
/* 197 */     return new GenericGFPoly(this.field, product);
/*     */   }
/*     */   
/*     */   GenericGFPoly[] divide(GenericGFPoly other) {
/* 201 */     if (!this.field.equals(other.field)) {
/* 202 */       throw new IllegalArgumentException("GenericGFPolys do not have same GenericGF field");
/*     */     }
/* 204 */     if (other.isZero()) {
/* 205 */       throw new IllegalArgumentException("Divide by 0");
/*     */     }
/*     */     
/* 208 */     GenericGFPoly quotient = this.field.getZero();
/* 209 */     GenericGFPoly remainder = this;
/*     */     
/* 211 */     int denominatorLeadingTerm = other.getCoefficient(other.getDegree());
/* 212 */     int inverseDenominatorLeadingTerm = this.field.inverse(denominatorLeadingTerm);
/*     */     
/* 214 */     while (remainder.getDegree() >= other.getDegree() && !remainder.isZero()) {
/* 215 */       int degreeDifference = remainder.getDegree() - other.getDegree();
/* 216 */       int scale = this.field.multiply(remainder.getCoefficient(remainder.getDegree()), inverseDenominatorLeadingTerm);
/* 217 */       GenericGFPoly term = other.multiplyByMonomial(degreeDifference, scale);
/* 218 */       GenericGFPoly iterationQuotient = this.field.buildMonomial(degreeDifference, scale);
/* 219 */       quotient = quotient.addOrSubtract(iterationQuotient);
/* 220 */       remainder = remainder.addOrSubtract(term);
/*     */     } 
/*     */     
/* 223 */     return new GenericGFPoly[] { quotient, remainder };
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 228 */     StringBuilder result = new StringBuilder(8 * getDegree());
/* 229 */     for (int degree = getDegree(); degree >= 0; degree--) {
/*     */       int coefficient;
/* 231 */       if ((coefficient = getCoefficient(degree)) != 0) {
/* 232 */         if (coefficient < 0) {
/* 233 */           result.append(" - ");
/* 234 */           coefficient = -coefficient;
/*     */         }
/* 236 */         else if (result.length() > 0) {
/* 237 */           result.append(" + ");
/*     */         } 
/*     */         
/* 240 */         if (degree == 0 || coefficient != 1) {
/*     */           int alphaPower;
/* 242 */           if ((alphaPower = this.field.log(coefficient)) == 0) {
/* 243 */             result.append('1');
/* 244 */           } else if (alphaPower == 1) {
/* 245 */             result.append('a');
/*     */           } else {
/* 247 */             result.append("a^");
/* 248 */             result.append(alphaPower);
/*     */           } 
/*     */         } 
/* 251 */         if (degree != 0) {
/* 252 */           if (degree == 1) {
/* 253 */             result.append('x');
/*     */           } else {
/* 255 */             result.append("x^");
/* 256 */             result.append(degree);
/*     */           } 
/*     */         }
/*     */       } 
/*     */     } 
/* 261 */     return result.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\common\reedsolomon\GenericGFPoly.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */