package com.google.zxing.common.reedsolomon;

public final class ReedSolomonDecoder {
   private final GenericGF field;

   public ReedSolomonDecoder(GenericGF field) {
      this.field = field;
   }

   public void decode(int[] received, int twoS) throws ReedSolomonException {
      GenericGFPoly poly = new GenericGFPoly(this.field, received);
      int[] syndromeCoefficients = new int[twoS];
      boolean noError = true;

      for(int i = 0; i < twoS; ++i) {
         int eval = poly.evaluateAt(this.field.exp(i + this.field.getGeneratorBase()));
         syndromeCoefficients[twoS - 1 - i] = eval;
         if (eval != 0) {
            noError = false;
         }
      }

      if (!noError) {
         GenericGFPoly syndrome = new GenericGFPoly(this.field, syndromeCoefficients);
         GenericGFPoly[] sigmaOmega;
         GenericGFPoly sigma = (sigmaOmega = this.runEuclideanAlgorithm(this.field.buildMonomial(twoS, 1), syndrome, twoS))[0];
         GenericGFPoly omega = sigmaOmega[1];
         int[] errorLocations = this.findErrorLocations(sigma);
         int[] errorMagnitudes = this.findErrorMagnitudes(omega, errorLocations);

         for(int i = 0; i < errorLocations.length; ++i) {
            int position;
            if ((position = received.length - 1 - this.field.log(errorLocations[i])) < 0) {
               throw new ReedSolomonException("Bad error location");
            }

            received[position] = GenericGF.addOrSubtract(received[position], errorMagnitudes[i]);
         }

      }
   }

   private GenericGFPoly[] runEuclideanAlgorithm(GenericGFPoly a, GenericGFPoly b, int R) throws ReedSolomonException {
      GenericGFPoly rLast;
      if (a.getDegree() < b.getDegree()) {
         rLast = a;
         a = b;
         b = rLast;
      }

      rLast = a;
      GenericGFPoly r = b;
      GenericGFPoly tLast = this.field.getZero();
      GenericGFPoly t = this.field.getOne();

      do {
         GenericGFPoly q;
         if (r.getDegree() < R / 2) {
            int sigmaTildeAtZero;
            if ((sigmaTildeAtZero = t.getCoefficient(0)) == 0) {
               throw new ReedSolomonException("sigmaTilde(0) was zero");
            }

            int inverse = this.field.inverse(sigmaTildeAtZero);
            q = t.multiply(inverse);
            GenericGFPoly omega = r.multiply(inverse);
            return new GenericGFPoly[]{q, omega};
         }

         GenericGFPoly rLastLast = rLast;
         GenericGFPoly tLastLast = tLast;
         rLast = r;
         tLast = t;
         if (r.isZero()) {
            throw new ReedSolomonException("r_{i-1} was zero");
         }

         r = rLastLast;
         q = this.field.getZero();
         int denominatorLeadingTerm = rLast.getCoefficient(rLast.getDegree());

         int degreeDiff;
         int scale;
         for(int dltInverse = this.field.inverse(denominatorLeadingTerm); r.getDegree() >= rLast.getDegree() && !r.isZero(); r = r.addOrSubtract(rLast.multiplyByMonomial(degreeDiff, scale))) {
            degreeDiff = r.getDegree() - rLast.getDegree();
            scale = this.field.multiply(r.getCoefficient(r.getDegree()), dltInverse);
            q = q.addOrSubtract(this.field.buildMonomial(degreeDiff, scale));
         }

         t = q.multiply(t).addOrSubtract(tLastLast);
      } while(r.getDegree() < rLast.getDegree());

      throw new IllegalStateException("Division algorithm failed to reduce polynomial?");
   }

   private int[] findErrorLocations(GenericGFPoly errorLocator) throws ReedSolomonException {
      int numErrors;
      if ((numErrors = errorLocator.getDegree()) == 1) {
         return new int[]{errorLocator.getCoefficient(1)};
      } else {
         int[] result = new int[numErrors];
         int e = 0;

         for(int i = 1; i < this.field.getSize() && e < numErrors; ++i) {
            if (errorLocator.evaluateAt(i) == 0) {
               result[e] = this.field.inverse(i);
               ++e;
            }
         }

         if (e != numErrors) {
            throw new ReedSolomonException("Error locator degree does not match number of roots");
         } else {
            return result;
         }
      }
   }

   private int[] findErrorMagnitudes(GenericGFPoly errorEvaluator, int[] errorLocations) {
      int s;
      int[] result = new int[s = errorLocations.length];

      for(int i = 0; i < s; ++i) {
         int xiInverse = this.field.inverse(errorLocations[i]);
         int denominator = 1;

         for(int j = 0; j < s; ++j) {
            if (i != j) {
               int term;
               int termPlus1 = ((term = this.field.multiply(errorLocations[j], xiInverse)) & 1) == 0 ? term | 1 : term & -2;
               denominator = this.field.multiply(denominator, termPlus1);
            }
         }

         result[i] = this.field.multiply(errorEvaluator.evaluateAt(xiInverse), this.field.inverse(denominator));
         if (this.field.getGeneratorBase() != 0) {
            result[i] = this.field.multiply(result[i], xiInverse);
         }
      }

      return result;
   }
}
