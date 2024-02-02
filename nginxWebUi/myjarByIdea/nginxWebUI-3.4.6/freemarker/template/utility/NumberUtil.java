package freemarker.template.utility;

import java.math.BigDecimal;
import java.math.BigInteger;

public class NumberUtil {
   private static final BigDecimal BIG_DECIMAL_INT_MIN = BigDecimal.valueOf(-2147483648L);
   private static final BigDecimal BIG_DECIMAL_INT_MAX = BigDecimal.valueOf(2147483647L);
   private static final BigInteger BIG_INTEGER_INT_MIN;
   private static final BigInteger BIG_INTEGER_INT_MAX;

   private NumberUtil() {
   }

   public static boolean isInfinite(Number num) {
      if (num instanceof Double) {
         return ((Double)num).isInfinite();
      } else if (num instanceof Float) {
         return ((Float)num).isInfinite();
      } else if (hasTypeThatIsKnownToNotSupportInfiniteAndNaN(num)) {
         return false;
      } else {
         throw new UnsupportedNumberClassException(num.getClass());
      }
   }

   public static boolean isNaN(Number num) {
      if (num instanceof Double) {
         return ((Double)num).isNaN();
      } else if (num instanceof Float) {
         return ((Float)num).isNaN();
      } else if (hasTypeThatIsKnownToNotSupportInfiniteAndNaN(num)) {
         return false;
      } else {
         throw new UnsupportedNumberClassException(num.getClass());
      }
   }

   public static int getSignum(Number num) throws ArithmeticException {
      int n;
      if (num instanceof Integer) {
         n = num.intValue();
         return n > 0 ? 1 : (n == 0 ? 0 : -1);
      } else if (num instanceof BigDecimal) {
         BigDecimal n = (BigDecimal)num;
         return n.signum();
      } else if (num instanceof Double) {
         double n = num.doubleValue();
         if (n > 0.0) {
            return 1;
         } else if (n == 0.0) {
            return 0;
         } else if (n < 0.0) {
            return -1;
         } else {
            throw new ArithmeticException("The signum of " + n + " is not defined.");
         }
      } else if (num instanceof Float) {
         float n = num.floatValue();
         if (n > 0.0F) {
            return 1;
         } else if (n == 0.0F) {
            return 0;
         } else if (n < 0.0F) {
            return -1;
         } else {
            throw new ArithmeticException("The signum of " + n + " is not defined.");
         }
      } else if (num instanceof Long) {
         long n = num.longValue();
         return n > 0L ? 1 : (n == 0L ? 0 : -1);
      } else if (num instanceof Short) {
         n = num.shortValue();
         return n > 0 ? 1 : (n == 0 ? 0 : -1);
      } else if (num instanceof Byte) {
         n = num.byteValue();
         return n > 0 ? 1 : (n == 0 ? 0 : -1);
      } else if (num instanceof BigInteger) {
         BigInteger n = (BigInteger)num;
         return n.signum();
      } else {
         throw new UnsupportedNumberClassException(num.getClass());
      }
   }

   public static boolean isIntegerBigDecimal(BigDecimal bd) {
      return bd.scale() <= 0 || bd.setScale(0, 1).compareTo(bd) == 0;
   }

   public static boolean hasTypeThatIsKnownToNotSupportInfiniteAndNaN(Number num) {
      return num instanceof Integer || num instanceof BigDecimal || num instanceof Long || num instanceof Short || num instanceof Byte || num instanceof BigInteger;
   }

   public static int toIntExact(Number num) {
      if (!(num instanceof Integer) && !(num instanceof Short) && !(num instanceof Byte)) {
         if (num instanceof Long) {
            long n = num.longValue();
            int result = (int)n;
            if (n != (long)result) {
               throw newLossyConverionException(num, Integer.class);
            } else {
               return result;
            }
         } else if (!(num instanceof Double) && !(num instanceof Float)) {
            if (num instanceof BigDecimal) {
               BigDecimal n = (BigDecimal)num;
               if (isIntegerBigDecimal(n) && n.compareTo(BIG_DECIMAL_INT_MAX) <= 0 && n.compareTo(BIG_DECIMAL_INT_MIN) >= 0) {
                  return n.intValue();
               } else {
                  throw newLossyConverionException(num, Integer.class);
               }
            } else if (num instanceof BigInteger) {
               BigInteger n = (BigInteger)num;
               if (n.compareTo(BIG_INTEGER_INT_MAX) <= 0 && n.compareTo(BIG_INTEGER_INT_MIN) >= 0) {
                  return n.intValue();
               } else {
                  throw newLossyConverionException(num, Integer.class);
               }
            } else {
               throw new UnsupportedNumberClassException(num.getClass());
            }
         } else {
            double n = num.doubleValue();
            if (n % 1.0 == 0.0 && !(n < -2.147483648E9) && !(n > 2.147483647E9)) {
               return (int)n;
            } else {
               throw newLossyConverionException(num, Integer.class);
            }
         }
      } else {
         return num.intValue();
      }
   }

   private static ArithmeticException newLossyConverionException(Number fromValue, Class toType) {
      return new ArithmeticException("Can't convert " + fromValue + " to type " + ClassUtil.getShortClassName(toType) + " without loss.");
   }

   static {
      BIG_INTEGER_INT_MIN = BIG_DECIMAL_INT_MIN.toBigInteger();
      BIG_INTEGER_INT_MAX = BIG_DECIMAL_INT_MAX.toBigInteger();
   }
}
