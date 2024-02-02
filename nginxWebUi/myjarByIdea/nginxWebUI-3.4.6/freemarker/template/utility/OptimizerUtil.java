package freemarker.template.utility;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OptimizerUtil {
   private static final BigInteger INTEGER_MIN = new BigInteger(Integer.toString(Integer.MIN_VALUE));
   private static final BigInteger INTEGER_MAX = new BigInteger(Integer.toString(Integer.MAX_VALUE));
   private static final BigInteger LONG_MIN = new BigInteger(Long.toString(Long.MIN_VALUE));
   private static final BigInteger LONG_MAX = new BigInteger(Long.toString(Long.MAX_VALUE));

   private OptimizerUtil() {
   }

   public static List optimizeListStorage(List list) {
      switch (list.size()) {
         case 0:
            return Collections.EMPTY_LIST;
         case 1:
            return Collections.singletonList(list.get(0));
         default:
            if (list instanceof ArrayList) {
               ((ArrayList)list).trimToSize();
            }

            return list;
      }
   }

   public static Number optimizeNumberRepresentation(Number number) {
      if (number instanceof BigDecimal) {
         BigDecimal bd = (BigDecimal)number;
         if (bd.scale() == 0) {
            number = bd.unscaledValue();
         } else {
            double d = bd.doubleValue();
            if (d != Double.POSITIVE_INFINITY && d != Double.NEGATIVE_INFINITY) {
               return d;
            }
         }
      }

      if (number instanceof BigInteger) {
         BigInteger bi = (BigInteger)number;
         if (bi.compareTo(INTEGER_MAX) <= 0 && bi.compareTo(INTEGER_MIN) >= 0) {
            return bi.intValue();
         }

         if (bi.compareTo(LONG_MAX) <= 0 && bi.compareTo(LONG_MIN) >= 0) {
            return bi.longValue();
         }
      }

      return (Number)number;
   }
}
