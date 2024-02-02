package cn.hutool.core.convert;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;

public class NumberWordFormatter {
   private static final String[] NUMBER = new String[]{"", "ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE"};
   private static final String[] NUMBER_TEEN = new String[]{"TEN", "ELEVEN", "TWELVE", "THIRTEEN", "FOURTEEN", "FIFTEEN", "SIXTEEN", "SEVENTEEN", "EIGHTEEN", "NINETEEN"};
   private static final String[] NUMBER_TEN = new String[]{"TEN", "TWENTY", "THIRTY", "FORTY", "FIFTY", "SIXTY", "SEVENTY", "EIGHTY", "NINETY"};
   private static final String[] NUMBER_MORE = new String[]{"", "THOUSAND", "MILLION", "BILLION"};
   private static final String[] NUMBER_SUFFIX = new String[]{"k", "w", "", "m", "", "", "b", "", "", "t", "", "", "p", "", "", "e"};

   public static String format(Object x) {
      return x != null ? format(x.toString()) : "";
   }

   public static String formatSimple(long value) {
      return formatSimple(value, true);
   }

   public static String formatSimple(long value, boolean isTwo) {
      if (value < 1000L) {
         return String.valueOf(value);
      } else {
         int index = -1;
         double res = (double)value;

         while(res > 10.0 && (!isTwo || index < 1)) {
            if (res >= 1000.0) {
               res /= 1000.0;
               ++index;
            }

            if (res > 10.0) {
               res /= 10.0;
               ++index;
            }
         }

         return String.format("%s%s", NumberUtil.decimalFormat("#.##", res), NUMBER_SUFFIX[index]);
      }
   }

   private static String format(String x) {
      int z = x.indexOf(".");
      String rstr = "";
      String lstr;
      if (z > -1) {
         lstr = x.substring(0, z);
         rstr = x.substring(z + 1);
      } else {
         lstr = x;
      }

      String lstrrev = StrUtil.reverse(lstr);
      String[] a = new String[5];
      switch (lstrrev.length() % 3) {
         case 1:
            lstrrev = lstrrev + "00";
            break;
         case 2:
            lstrrev = lstrrev + "0";
      }

      StringBuilder lm = new StringBuilder();

      for(int i = 0; i < lstrrev.length() / 3; ++i) {
         a[i] = StrUtil.reverse(lstrrev.substring(3 * i, 3 * i + 3));
         if (!"000".equals(a[i])) {
            if (i != 0) {
               lm.insert(0, transThree(a[i]) + " " + parseMore(i) + " ");
            } else {
               lm = new StringBuilder(transThree(a[i]));
            }
         } else {
            lm.append(transThree(a[i]));
         }
      }

      String xs = "";
      if (z > -1) {
         xs = "AND CENTS " + transTwo(rstr) + " ";
      }

      return lm.toString().trim() + " " + xs + "ONLY";
   }

   private static String parseFirst(String s) {
      return NUMBER[Integer.parseInt(s.substring(s.length() - 1))];
   }

   private static String parseTeen(String s) {
      return NUMBER_TEEN[Integer.parseInt(s) - 10];
   }

   private static String parseTen(String s) {
      return NUMBER_TEN[Integer.parseInt(s.substring(0, 1)) - 1];
   }

   private static String parseMore(int i) {
      return NUMBER_MORE[i];
   }

   private static String transTwo(String s) {
      if (s.length() > 2) {
         s = s.substring(0, 2);
      } else if (s.length() < 2) {
         s = "0" + s;
      }

      String value;
      if (s.startsWith("0")) {
         value = parseFirst(s);
      } else if (s.startsWith("1")) {
         value = parseTeen(s);
      } else if (s.endsWith("0")) {
         value = parseTen(s);
      } else {
         value = parseTen(s) + " " + parseFirst(s);
      }

      return value;
   }

   private static String transThree(String s) {
      String value;
      if (s.startsWith("0")) {
         value = transTwo(s.substring(1));
      } else if ("00".equals(s.substring(1))) {
         value = parseFirst(s.substring(0, 1)) + " HUNDRED";
      } else {
         value = parseFirst(s.substring(0, 1)) + " HUNDRED AND " + transTwo(s.substring(1));
      }

      return value;
   }
}
