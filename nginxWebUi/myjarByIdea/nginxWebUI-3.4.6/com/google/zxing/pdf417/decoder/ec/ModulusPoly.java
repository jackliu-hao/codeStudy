package com.google.zxing.pdf417.decoder.ec;

final class ModulusPoly {
   private final ModulusGF field;
   private final int[] coefficients;

   ModulusPoly(ModulusGF field, int[] coefficients) {
      if (coefficients.length == 0) {
         throw new IllegalArgumentException();
      } else {
         this.field = field;
         int coefficientsLength;
         if ((coefficientsLength = coefficients.length) > 1 && coefficients[0] == 0) {
            int firstNonZero;
            for(firstNonZero = 1; firstNonZero < coefficientsLength && coefficients[firstNonZero] == 0; ++firstNonZero) {
            }

            if (firstNonZero == coefficientsLength) {
               this.coefficients = new int[]{0};
            } else {
               this.coefficients = new int[coefficientsLength - firstNonZero];
               System.arraycopy(coefficients, firstNonZero, this.coefficients, 0, this.coefficients.length);
            }
         } else {
            this.coefficients = coefficients;
         }
      }
   }

   int[] getCoefficients() {
      return this.coefficients;
   }

   int getDegree() {
      return this.coefficients.length - 1;
   }

   boolean isZero() {
      return this.coefficients[0] == 0;
   }

   int getCoefficient(int degree) {
      return this.coefficients[this.coefficients.length - 1 - degree];
   }

   int evaluateAt(int a) {
      if (a == 0) {
         return this.getCoefficient(0);
      } else {
         int result;
         int i;
         if (a == 1) {
            result = 0;
            int[] var7;
            i = (var7 = this.coefficients).length;

            for(int var5 = 0; var5 < i; ++var5) {
               int coefficient = var7[var5];
               result = this.field.add(result, coefficient);
            }

            return result;
         } else {
            result = this.coefficients[0];
            int size = this.coefficients.length;

            for(i = 1; i < size; ++i) {
               result = this.field.add(this.field.multiply(a, result), this.coefficients[i]);
            }

            return result;
         }
      }
   }

   ModulusPoly add(ModulusPoly other) {
      if (!this.field.equals(other.field)) {
         throw new IllegalArgumentException("ModulusPolys do not have same ModulusGF field");
      } else if (this.isZero()) {
         return other;
      } else if (other.isZero()) {
         return this;
      } else {
         int[] smallerCoefficients = this.coefficients;
         int[] largerCoefficients = other.coefficients;
         int[] sumDiff;
         if (smallerCoefficients.length > largerCoefficients.length) {
            sumDiff = smallerCoefficients;
            smallerCoefficients = largerCoefficients;
            largerCoefficients = sumDiff;
         }

         sumDiff = new int[largerCoefficients.length];
         int lengthDiff = largerCoefficients.length - smallerCoefficients.length;
         System.arraycopy(largerCoefficients, 0, sumDiff, 0, lengthDiff);

         for(int i = lengthDiff; i < largerCoefficients.length; ++i) {
            sumDiff[i] = this.field.add(smallerCoefficients[i - lengthDiff], largerCoefficients[i]);
         }

         return new ModulusPoly(this.field, sumDiff);
      }
   }

   ModulusPoly subtract(ModulusPoly other) {
      if (!this.field.equals(other.field)) {
         throw new IllegalArgumentException("ModulusPolys do not have same ModulusGF field");
      } else {
         return other.isZero() ? this : this.add(other.negative());
      }
   }

   ModulusPoly multiply(ModulusPoly other) {
      if (!this.field.equals(other.field)) {
         throw new IllegalArgumentException("ModulusPolys do not have same ModulusGF field");
      } else if (!this.isZero() && !other.isZero()) {
         int[] aCoefficients;
         int aLength = (aCoefficients = this.coefficients).length;
         int[] bCoefficients;
         int bLength = (bCoefficients = other.coefficients).length;
         int[] product = new int[aLength + bLength - 1];

         for(int i = 0; i < aLength; ++i) {
            int aCoeff = aCoefficients[i];

            for(int j = 0; j < bLength; ++j) {
               product[i + j] = this.field.add(product[i + j], this.field.multiply(aCoeff, bCoefficients[j]));
            }
         }

         return new ModulusPoly(this.field, product);
      } else {
         return this.field.getZero();
      }
   }

   ModulusPoly negative() {
      int size;
      int[] negativeCoefficients = new int[size = this.coefficients.length];

      for(int i = 0; i < size; ++i) {
         negativeCoefficients[i] = this.field.subtract(0, this.coefficients[i]);
      }

      return new ModulusPoly(this.field, negativeCoefficients);
   }

   ModulusPoly multiply(int scalar) {
      if (scalar == 0) {
         return this.field.getZero();
      } else if (scalar == 1) {
         return this;
      } else {
         int size;
         int[] product = new int[size = this.coefficients.length];

         for(int i = 0; i < size; ++i) {
            product[i] = this.field.multiply(this.coefficients[i], scalar);
         }

         return new ModulusPoly(this.field, product);
      }
   }

   ModulusPoly multiplyByMonomial(int degree, int coefficient) {
      if (degree < 0) {
         throw new IllegalArgumentException();
      } else if (coefficient == 0) {
         return this.field.getZero();
      } else {
         int size;
         int[] product = new int[(size = this.coefficients.length) + degree];

         for(int i = 0; i < size; ++i) {
            product[i] = this.field.multiply(this.coefficients[i], coefficient);
         }

         return new ModulusPoly(this.field, product);
      }
   }

   public String toString() {
      StringBuilder result = new StringBuilder(8 * this.getDegree());

      for(int degree = this.getDegree(); degree >= 0; --degree) {
         int coefficient;
         if ((coefficient = this.getCoefficient(degree)) != 0) {
            if (coefficient < 0) {
               result.append(" - ");
               coefficient = -coefficient;
            } else if (result.length() > 0) {
               result.append(" + ");
            }

            if (degree == 0 || coefficient != 1) {
               result.append(coefficient);
            }

            if (degree != 0) {
               if (degree == 1) {
                  result.append('x');
               } else {
                  result.append("x^");
                  result.append(degree);
               }
            }
         }
      }

      return result.toString();
   }
}
