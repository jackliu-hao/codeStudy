package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.utility.NumberUtil;
import freemarker.template.utility.OptimizerUtil;
import freemarker.template.utility.StringUtil;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public abstract class ArithmeticEngine {
   public static final BigDecimalEngine BIGDECIMAL_ENGINE = new BigDecimalEngine();
   public static final ConservativeEngine CONSERVATIVE_ENGINE = new ConservativeEngine();
   protected int minScale = 12;
   protected int maxScale = 12;
   protected int roundingPolicy = 4;

   public abstract int compareNumbers(Number var1, Number var2) throws TemplateException;

   public abstract Number add(Number var1, Number var2) throws TemplateException;

   public abstract Number subtract(Number var1, Number var2) throws TemplateException;

   public abstract Number multiply(Number var1, Number var2) throws TemplateException;

   public abstract Number divide(Number var1, Number var2) throws TemplateException;

   public abstract Number modulus(Number var1, Number var2) throws TemplateException;

   public abstract Number toNumber(String var1);

   public void setMinScale(int minScale) {
      if (minScale < 0) {
         throw new IllegalArgumentException("minScale < 0");
      } else {
         this.minScale = minScale;
      }
   }

   public void setMaxScale(int maxScale) {
      if (maxScale < this.minScale) {
         throw new IllegalArgumentException("maxScale < minScale");
      } else {
         this.maxScale = maxScale;
      }
   }

   public void setRoundingPolicy(int roundingPolicy) {
      if (roundingPolicy != 2 && roundingPolicy != 1 && roundingPolicy != 3 && roundingPolicy != 5 && roundingPolicy != 6 && roundingPolicy != 4 && roundingPolicy != 7 && roundingPolicy != 0) {
         throw new IllegalArgumentException("invalid rounding policy");
      } else {
         this.roundingPolicy = roundingPolicy;
      }
   }

   private static BigDecimal toBigDecimal(Number num) {
      if (num instanceof BigDecimal) {
         return (BigDecimal)num;
      } else if (!(num instanceof Integer) && !(num instanceof Long) && !(num instanceof Byte) && !(num instanceof Short)) {
         if (num instanceof BigInteger) {
            return new BigDecimal((BigInteger)num);
         } else {
            try {
               return new BigDecimal(num.toString());
            } catch (NumberFormatException var2) {
               if (NumberUtil.isInfinite(num)) {
                  throw new NumberFormatException("It's impossible to convert an infinte value (" + num.getClass().getSimpleName() + " " + num + ") to BigDecimal.");
               } else {
                  throw new NumberFormatException("Can't parse this as BigDecimal number: " + StringUtil.jQuote((Object)num));
               }
            }
         }
      } else {
         return BigDecimal.valueOf(num.longValue());
      }
   }

   private static Number toBigDecimalOrDouble(String s) {
      if (s.length() > 2) {
         char c = s.charAt(0);
         if (c == 'I' && (s.equals("INF") || s.equals("Infinity"))) {
            return Double.POSITIVE_INFINITY;
         }

         if (c == 'N' && s.equals("NaN")) {
            return Double.NaN;
         }

         if (c == '-' && s.charAt(1) == 'I' && (s.equals("-INF") || s.equals("-Infinity"))) {
            return Double.NEGATIVE_INFINITY;
         }
      }

      return new BigDecimal(s);
   }

   public static class ConservativeEngine extends ArithmeticEngine {
      private static final int INTEGER = 0;
      private static final int LONG = 1;
      private static final int FLOAT = 2;
      private static final int DOUBLE = 3;
      private static final int BIGINTEGER = 4;
      private static final int BIGDECIMAL = 5;
      private static final Map classCodes = createClassCodesMap();

      public int compareNumbers(Number var1, Number var2) throws TemplateException {
         // $FF: Couldn't be decompiled
      }

      public Number add(Number var1, Number var2) throws TemplateException {
         // $FF: Couldn't be decompiled
      }

      public Number subtract(Number var1, Number var2) throws TemplateException {
         // $FF: Couldn't be decompiled
      }

      public Number multiply(Number var1, Number var2) throws TemplateException {
         // $FF: Couldn't be decompiled
      }

      public Number divide(Number var1, Number var2) throws TemplateException {
         // $FF: Couldn't be decompiled
      }

      public Number modulus(Number var1, Number var2) throws TemplateException {
         // $FF: Couldn't be decompiled
      }

      public Number toNumber(String s) {
         Number n = ArithmeticEngine.toBigDecimalOrDouble(s);
         return n instanceof BigDecimal ? OptimizerUtil.optimizeNumberRepresentation(n) : n;
      }

      private static Map createClassCodesMap() {
         Map map = new HashMap(17);
         Integer intcode = 0;
         map.put(Byte.class, intcode);
         map.put(Short.class, intcode);
         map.put(Integer.class, intcode);
         map.put(Long.class, 1);
         map.put(Float.class, 2);
         map.put(Double.class, 3);
         map.put(BigInteger.class, 4);
         map.put(BigDecimal.class, 5);
         return map;
      }

      private static int getClassCode(Number num) throws TemplateException {
         try {
            return (Integer)classCodes.get(num.getClass());
         } catch (NullPointerException var2) {
            if (num == null) {
               throw new _MiscTemplateException("The Number object was null.");
            } else {
               throw new _MiscTemplateException(new Object[]{"Unknown number type ", num.getClass().getName()});
            }
         }
      }

      private static int getCommonClassCode(Number num1, Number num2) throws TemplateException {
         int c1 = getClassCode(num1);
         int c2 = getClassCode(num2);
         int c = c1 > c2 ? c1 : c2;
         switch (c) {
            case 2:
               if ((c1 < c2 ? c1 : c2) == 1) {
                  return 3;
               }
               break;
            case 4:
               int min = c1 < c2 ? c1 : c2;
               if (min == 3 || min == 2) {
                  return 5;
               }
         }

         return c;
      }

      private static BigInteger toBigInteger(Number num) {
         return num instanceof BigInteger ? (BigInteger)num : new BigInteger(num.toString());
      }
   }

   public static class BigDecimalEngine extends ArithmeticEngine {
      public int compareNumbers(Number first, Number second) {
         int firstSignum = NumberUtil.getSignum(first);
         int secondSignum = NumberUtil.getSignum(second);
         if (firstSignum != secondSignum) {
            return firstSignum < secondSignum ? -1 : (firstSignum > secondSignum ? 1 : 0);
         } else if (firstSignum == 0 && secondSignum == 0) {
            return 0;
         } else {
            if (first.getClass() == second.getClass()) {
               if (first instanceof BigDecimal) {
                  return ((BigDecimal)first).compareTo((BigDecimal)second);
               }

               if (first instanceof Integer) {
                  return ((Integer)first).compareTo((Integer)second);
               }

               if (first instanceof Long) {
                  return ((Long)first).compareTo((Long)second);
               }

               if (first instanceof Double) {
                  return ((Double)first).compareTo((Double)second);
               }

               if (first instanceof Float) {
                  return ((Float)first).compareTo((Float)second);
               }

               if (first instanceof Byte) {
                  return ((Byte)first).compareTo((Byte)second);
               }

               if (first instanceof Short) {
                  return ((Short)first).compareTo((Short)second);
               }
            }

            double secondD;
            if (first instanceof Double) {
               secondD = first.doubleValue();
               if (Double.isInfinite(secondD)) {
                  if (NumberUtil.hasTypeThatIsKnownToNotSupportInfiniteAndNaN(second)) {
                     return secondD == Double.NEGATIVE_INFINITY ? -1 : 1;
                  }

                  if (second instanceof Float) {
                     return Double.compare(secondD, second.doubleValue());
                  }
               }
            }

            float secondF;
            if (first instanceof Float) {
               secondF = first.floatValue();
               if (Float.isInfinite(secondF)) {
                  if (NumberUtil.hasTypeThatIsKnownToNotSupportInfiniteAndNaN(second)) {
                     return secondF == Float.NEGATIVE_INFINITY ? -1 : 1;
                  }

                  if (second instanceof Double) {
                     return Double.compare((double)secondF, second.doubleValue());
                  }
               }
            }

            if (second instanceof Double) {
               secondD = second.doubleValue();
               if (Double.isInfinite(secondD)) {
                  if (NumberUtil.hasTypeThatIsKnownToNotSupportInfiniteAndNaN(first)) {
                     return secondD == Double.NEGATIVE_INFINITY ? 1 : -1;
                  }

                  if (first instanceof Float) {
                     return Double.compare(first.doubleValue(), secondD);
                  }
               }
            }

            if (second instanceof Float) {
               secondF = second.floatValue();
               if (Float.isInfinite(secondF)) {
                  if (NumberUtil.hasTypeThatIsKnownToNotSupportInfiniteAndNaN(first)) {
                     return secondF == Float.NEGATIVE_INFINITY ? 1 : -1;
                  }

                  if (first instanceof Double) {
                     return Double.compare(first.doubleValue(), (double)secondF);
                  }
               }
            }

            return ArithmeticEngine.toBigDecimal(first).compareTo(ArithmeticEngine.toBigDecimal(second));
         }
      }

      public Number add(Number first, Number second) {
         BigDecimal left = ArithmeticEngine.toBigDecimal(first);
         BigDecimal right = ArithmeticEngine.toBigDecimal(second);
         return left.add(right);
      }

      public Number subtract(Number first, Number second) {
         BigDecimal left = ArithmeticEngine.toBigDecimal(first);
         BigDecimal right = ArithmeticEngine.toBigDecimal(second);
         return left.subtract(right);
      }

      public Number multiply(Number first, Number second) {
         BigDecimal left = ArithmeticEngine.toBigDecimal(first);
         BigDecimal right = ArithmeticEngine.toBigDecimal(second);
         BigDecimal result = left.multiply(right);
         if (result.scale() > this.maxScale) {
            result = result.setScale(this.maxScale, this.roundingPolicy);
         }

         return result;
      }

      public Number divide(Number first, Number second) {
         BigDecimal left = ArithmeticEngine.toBigDecimal(first);
         BigDecimal right = ArithmeticEngine.toBigDecimal(second);
         return this.divide(left, right);
      }

      public Number modulus(Number first, Number second) {
         long left = first.longValue();
         long right = second.longValue();
         return left % right;
      }

      public Number toNumber(String s) {
         return ArithmeticEngine.toBigDecimalOrDouble(s);
      }

      private BigDecimal divide(BigDecimal left, BigDecimal right) {
         int scale1 = left.scale();
         int scale2 = right.scale();
         int scale = Math.max(scale1, scale2);
         scale = Math.max(this.minScale, scale);
         return left.divide(right, scale, this.roundingPolicy);
      }
   }
}
