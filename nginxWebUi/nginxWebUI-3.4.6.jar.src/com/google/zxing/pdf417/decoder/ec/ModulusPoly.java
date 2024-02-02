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
/*     */ final class ModulusPoly
/*     */ {
/*     */   private final ModulusGF field;
/*     */   private final int[] coefficients;
/*     */   
/*     */   ModulusPoly(ModulusGF field, int[] coefficients) {
/*  29 */     if (coefficients.length == 0) {
/*  30 */       throw new IllegalArgumentException();
/*     */     }
/*  32 */     this.field = field;
/*     */     int coefficientsLength;
/*  34 */     if ((coefficientsLength = coefficients.length) > 1 && coefficients[0] == 0) {
/*     */       
/*  36 */       int firstNonZero = 1;
/*  37 */       while (firstNonZero < coefficientsLength && coefficients[firstNonZero] == 0) {
/*  38 */         firstNonZero++;
/*     */       }
/*  40 */       if (firstNonZero == coefficientsLength) {
/*  41 */         this.coefficients = new int[] { 0 }; return;
/*     */       } 
/*  43 */       this.coefficients = new int[coefficientsLength - firstNonZero];
/*  44 */       System.arraycopy(coefficients, firstNonZero, this.coefficients, 0, this.coefficients.length);
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */ 
/*     */     
/*  51 */     this.coefficients = coefficients;
/*     */   }
/*     */ 
/*     */   
/*     */   int[] getCoefficients() {
/*  56 */     return this.coefficients;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int getDegree() {
/*  63 */     return this.coefficients.length - 1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isZero() {
/*  70 */     return (this.coefficients[0] == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int getCoefficient(int degree) {
/*  77 */     return this.coefficients[this.coefficients.length - 1 - degree];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int evaluateAt(int a) {
/*  84 */     if (a == 0)
/*     */     {
/*  86 */       return getCoefficient(0);
/*     */     }
/*  88 */     if (a == 1) {
/*     */       
/*  90 */       int j = 0; int arrayOfInt[], k; byte b;
/*  91 */       for (k = (arrayOfInt = this.coefficients).length, b = 0; b < k; ) { int coefficient = arrayOfInt[b];
/*  92 */         j = this.field.add(j, coefficient); b++; }
/*     */       
/*  94 */       return j;
/*     */     } 
/*  96 */     int result = this.coefficients[0];
/*  97 */     int size = this.coefficients.length;
/*  98 */     for (int i = 1; i < size; i++) {
/*  99 */       result = this.field.add(this.field.multiply(a, result), this.coefficients[i]);
/*     */     }
/* 101 */     return result;
/*     */   }
/*     */   
/*     */   ModulusPoly add(ModulusPoly other) {
/* 105 */     if (!this.field.equals(other.field)) {
/* 106 */       throw new IllegalArgumentException("ModulusPolys do not have same ModulusGF field");
/*     */     }
/* 108 */     if (isZero()) {
/* 109 */       return other;
/*     */     }
/* 111 */     if (other.isZero()) {
/* 112 */       return this;
/*     */     }
/*     */     
/* 115 */     int[] smallerCoefficients = this.coefficients;
/* 116 */     int[] largerCoefficients = other.coefficients;
/* 117 */     if (smallerCoefficients.length > largerCoefficients.length) {
/* 118 */       int[] temp = smallerCoefficients;
/* 119 */       smallerCoefficients = largerCoefficients;
/* 120 */       largerCoefficients = temp;
/*     */     } 
/* 122 */     int[] sumDiff = new int[largerCoefficients.length];
/* 123 */     int lengthDiff = largerCoefficients.length - smallerCoefficients.length;
/*     */     
/* 125 */     System.arraycopy(largerCoefficients, 0, sumDiff, 0, lengthDiff);
/*     */     
/* 127 */     for (int i = lengthDiff; i < largerCoefficients.length; i++) {
/* 128 */       sumDiff[i] = this.field.add(smallerCoefficients[i - lengthDiff], largerCoefficients[i]);
/*     */     }
/*     */     
/* 131 */     return new ModulusPoly(this.field, sumDiff);
/*     */   }
/*     */   
/*     */   ModulusPoly subtract(ModulusPoly other) {
/* 135 */     if (!this.field.equals(other.field)) {
/* 136 */       throw new IllegalArgumentException("ModulusPolys do not have same ModulusGF field");
/*     */     }
/* 138 */     if (other.isZero()) {
/* 139 */       return this;
/*     */     }
/* 141 */     return add(other.negative());
/*     */   }
/*     */   
/*     */   ModulusPoly multiply(ModulusPoly other) {
/* 145 */     if (!this.field.equals(other.field)) {
/* 146 */       throw new IllegalArgumentException("ModulusPolys do not have same ModulusGF field");
/*     */     }
/* 148 */     if (isZero() || other.isZero()) {
/* 149 */       return this.field.getZero();
/*     */     }
/*     */     
/* 152 */     int aCoefficients[], aLength = (aCoefficients = this.coefficients).length;
/*     */     
/* 154 */     int bCoefficients[], bLength = (bCoefficients = other.coefficients).length;
/* 155 */     int[] product = new int[aLength + bLength - 1];
/* 156 */     for (int i = 0; i < aLength; i++) {
/* 157 */       int aCoeff = aCoefficients[i];
/* 158 */       for (int j = 0; j < bLength; j++) {
/* 159 */         product[i + j] = this.field.add(product[i + j], this.field.multiply(aCoeff, bCoefficients[j]));
/*     */       }
/*     */     } 
/* 162 */     return new ModulusPoly(this.field, product);
/*     */   }
/*     */ 
/*     */   
/*     */   ModulusPoly negative() {
/* 167 */     int size, negativeCoefficients[] = new int[size = this.coefficients.length];
/* 168 */     for (int i = 0; i < size; i++) {
/* 169 */       negativeCoefficients[i] = this.field.subtract(0, this.coefficients[i]);
/*     */     }
/* 171 */     return new ModulusPoly(this.field, negativeCoefficients);
/*     */   }
/*     */   
/*     */   ModulusPoly multiply(int scalar) {
/* 175 */     if (scalar == 0) {
/* 176 */       return this.field.getZero();
/*     */     }
/* 178 */     if (scalar == 1) {
/* 179 */       return this;
/*     */     }
/*     */     
/* 182 */     int size, product[] = new int[size = this.coefficients.length];
/* 183 */     for (int i = 0; i < size; i++) {
/* 184 */       product[i] = this.field.multiply(this.coefficients[i], scalar);
/*     */     }
/* 186 */     return new ModulusPoly(this.field, product);
/*     */   }
/*     */   
/*     */   ModulusPoly multiplyByMonomial(int degree, int coefficient) {
/* 190 */     if (degree < 0) {
/* 191 */       throw new IllegalArgumentException();
/*     */     }
/* 193 */     if (coefficient == 0) {
/* 194 */       return this.field.getZero();
/*     */     }
/*     */     
/* 197 */     int size, product[] = new int[(size = this.coefficients.length) + degree];
/* 198 */     for (int i = 0; i < size; i++) {
/* 199 */       product[i] = this.field.multiply(this.coefficients[i], coefficient);
/*     */     }
/* 201 */     return new ModulusPoly(this.field, product);
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
/*     */   public String toString() {
/* 234 */     StringBuilder result = new StringBuilder(8 * getDegree());
/* 235 */     for (int degree = getDegree(); degree >= 0; degree--) {
/*     */       int coefficient;
/* 237 */       if ((coefficient = getCoefficient(degree)) != 0) {
/* 238 */         if (coefficient < 0) {
/* 239 */           result.append(" - ");
/* 240 */           coefficient = -coefficient;
/*     */         }
/* 242 */         else if (result.length() > 0) {
/* 243 */           result.append(" + ");
/*     */         } 
/*     */         
/* 246 */         if (degree == 0 || coefficient != 1) {
/* 247 */           result.append(coefficient);
/*     */         }
/* 249 */         if (degree != 0) {
/* 250 */           if (degree == 1) {
/* 251 */             result.append('x');
/*     */           } else {
/* 253 */             result.append("x^");
/* 254 */             result.append(degree);
/*     */           } 
/*     */         }
/*     */       } 
/*     */     } 
/* 259 */     return result.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\pdf417\decoder\ec\ModulusPoly.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */