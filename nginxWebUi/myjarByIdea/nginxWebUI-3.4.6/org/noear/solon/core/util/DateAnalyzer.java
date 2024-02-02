package org.noear.solon.core.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.noear.solon.Solon;

public class DateAnalyzer {
   public static final String FORMAT_29 = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
   public static final String FORMAT_24_ISO08601 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
   public static final String FORMAT_23_a = "yyyy-MM-dd HH:mm:ss,SSS";
   public static final String FORMAT_23_b = "yyyy-MM-dd HH:mm:ss.SSS";
   public static final String FORMAT_22 = "yyyyMMddHHmmssSSSZ";
   public static final String FORMAT_19_ISO = "yyyy-MM-dd'T'HH:mm:ss";
   public static final String FORMAT_19_a = "yyyy-MM-dd HH:mm:ss";
   public static final String FORMAT_19_b = "yyyy/MM/dd HH:mm:ss";
   public static final String FORMAT_19_c = "yyyy.MM.dd HH:mm:ss";
   public static final String FORMAT_17 = "yyyyMMddHHmmssSSS";
   public static final String FORMAT_16_a = "yyyy-MM-dd HH:mm";
   public static final String FORMAT_16_b = "yyyy/MM/dd HH:mm";
   public static final String FORMAT_16_c = "yyyy.MM.dd HH:mm";
   public static final String FORMAT_14 = "yyyyMMddHHmmss";
   public static final String FORMAT_10_a = "yyyy-MM-dd";
   public static final String FORMAT_10_b = "yyyy/MM/dd";
   public static final String FORMAT_10_c = "yyyy.MM.dd";
   public static final String FORMAT_9 = "HH时mm分ss秒";
   public static final String FORMAT_8_a = "HH:mm:ss";
   public static final String FORMAT_8_b = "yyyyMMdd";
   private static DateAnalyzer global = new DateAnalyzer();

   public static void setGlobal(DateAnalyzer instance) {
      if (instance != null) {
         global = instance;
      }

   }

   public static DateAnalyzer getGlobal() {
      return global;
   }

   public Date parse(String val) throws ParseException {
      int len = val.length();
      String ft = null;
      if (len == 29) {
         if (val.charAt(26) == ':' && val.charAt(28) == '0') {
            ft = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
         }
      } else if (len == 24) {
         if (val.charAt(10) == 'T') {
            ft = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
         }
      } else if (len == 23) {
         if (val.charAt(19) == ',') {
            ft = "yyyy-MM-dd HH:mm:ss,SSS";
         } else {
            ft = "yyyy-MM-dd HH:mm:ss.SSS";
         }
      } else if (len == 22) {
         ft = "yyyyMMddHHmmssSSSZ";
      } else {
         char c1;
         if (len == 19) {
            if (val.charAt(10) == 'T') {
               ft = "yyyy-MM-dd'T'HH:mm:ss";
            } else {
               c1 = val.charAt(4);
               if (c1 == '/') {
                  ft = "yyyy/MM/dd HH:mm:ss";
               } else if (c1 == '.') {
                  ft = "yyyy.MM.dd HH:mm:ss";
               } else {
                  ft = "yyyy-MM-dd HH:mm:ss";
               }
            }
         } else if (len == 17) {
            ft = "yyyyMMddHHmmssSSS";
         } else if (len == 16) {
            c1 = val.charAt(4);
            if (c1 == '/') {
               ft = "yyyy/MM/dd HH:mm";
            } else if (c1 == '.') {
               ft = "yyyy.MM.dd HH:mm";
            } else {
               ft = "yyyy-MM-dd HH:mm";
            }
         } else if (len == 14) {
            ft = "yyyyMMddHHmmss";
         } else if (len == 10) {
            c1 = val.charAt(4);
            if (c1 == '/') {
               ft = "yyyy/MM/dd";
            } else if (c1 == '.') {
               ft = "yyyy.MM.dd";
            } else if (c1 == '-') {
               ft = "yyyy-MM-dd";
            }
         } else if (len == 9) {
            c1 = val.charAt(4);
            if (c1 == '/') {
               ft = "yyyy/MM/dd";
            } else if (c1 == '.') {
               ft = "yyyy.MM.dd";
            } else if (c1 == '-') {
               ft = "yyyy-MM-dd";
            } else {
               ft = "HH时mm分ss秒";
            }
         } else if (len == 8) {
            c1 = val.charAt(4);
            if (c1 == '/') {
               ft = "yyyy/MM/dd";
            } else if (c1 == '.') {
               ft = "yyyy.MM.dd";
            } else if (c1 == '-') {
               ft = "yyyy-MM-dd";
            } else if (val.charAt(2) == ':') {
               ft = "HH:mm:ss";
            } else {
               ft = "yyyyMMdd";
            }
         }
      }

      if (ft != null) {
         DateFormat df = null;
         if (Solon.app() == null) {
            df = new SimpleDateFormat(ft);
         } else {
            df = new SimpleDateFormat(ft, Solon.cfg().locale());
         }

         df.setTimeZone(TimeZone.getDefault());
         return df.parse(val);
      } else {
         return null;
      }
   }
}
