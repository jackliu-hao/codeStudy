package com.google.zxing.common.reedsolomon;

final class GenericGFPoly {
   private final GenericGF field;
   private final int[] coefficients;

   GenericGFPoly(GenericGF field, int[] coefficients) {
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
               result = GenericGF.addOrSubtract(result, coefficient);
            }

            return result;
         } else {
            result = this.coefficients[0];
            int size = this.coefficients.length;

            for(i = 1; i < size; ++i) {
               result = GenericGF.addOrSubtract(this.field.multiply(a, result), this.coefficients[i]);
            }

            return result;
         }
      }
   }

   GenericGFPoly addOrSubtract(GenericGFPoly other) {
      if (!this.field.equals(other.field)) {
         throw new IllegalArgumentException("GenericGFPolys do not have same GenericGF field");
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
            sumDiff[i] = GenericGF.addOrSubtract(smallerCoefficients[i - lengthDiff], largerCoefficients[i]);
         }

         return new GenericGFPoly(this.field, sumDiff);
      }
   }

   GenericGFPoly multiply(GenericGFPoly other) {
      if (!this.field.equals(other.field)) {
         throw new IllegalArgumentException("GenericGFPolys do not have same GenericGF field");
      } else if (!this.isZero() && !other.isZero()) {
         int[] aCoefficients;
         int aLength = (aCoefficients = this.coefficients).length;
         int[] bCoefficients;
         int bLength = (bCoefficients = other.coefficients).length;
         int[] product = new int[aLength + bLength - 1];

         for(int i = 0; i < aLength; ++i) {
            int aCoeff = aCoefficients[i];

            for(int j = 0; j < bLength; ++j) {
               product[i + j] = GenericGF.addOrSubtract(product[i + j], this.field.multiply(aCoeff, bCoefficients[j]));
            }
         }

         return new GenericGFPoly(this.field, product);
      } else {
         return this.field.getZero();
      }
   }

   GenericGFPoly multiply(int scalar) {
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

         return new GenericGFPoly(this.field, product);
      }
   }

   GenericGFPoly multiplyByMonomial(int degree, int coefficient) {
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

         return new GenericGFPoly(this.field, product);
      }
   }

   GenericGFPoly[] divide(GenericGFPoly other) {
      if (!this.field.equals(other.field)) {
         throw new IllegalArgumentException("GenericGFPolys do not have same GenericGF field");
      } else if (other.isZero()) {
         throw new IllegalArgumentException("Divide by 0");
      } else {
         GenericGFPoly quotient = this.field.getZero();
         GenericGFPoly remainder = this;
         int denominatorLeadingTerm = other.getCoefficient(other.getDegree());

         GenericGFPoly term;
         for(int inverseDenominatorLeadingTerm = this.field.inverse(denominatorLeadingTerm); remainder.getDegree() >= other.getDegree() && !remainder.isZero(); remainder = remainder.addOrSubtract(term)) {
            int degreeDifference = remainder.getDegree() - other.getDegree();
            int scale = this.field.multiply(remainder.getCoefficient(remainder.getDegree()), inverseDenominatorLeadingTerm);
            term = other.multiplyByMonomial(degreeDifference, scale);
            GenericGFPoly iterationQuotient = this.field.buildMonomial(degreeDifference, scale);
            quotient = quotient.addOrSubtract(iterationQuotient);
         }

         return new GenericGFPoly[]{quotient, remainder};
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
               int alphaPower;
               if ((alphaPower = this.field.log(coefficient)) == 0) {
                  result.append('1');
               } else if (alphaPower == 1) {
                  result.append('a');
               } else {
                  result.append("a^");
                  result.append(alphaPower);
               }
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
