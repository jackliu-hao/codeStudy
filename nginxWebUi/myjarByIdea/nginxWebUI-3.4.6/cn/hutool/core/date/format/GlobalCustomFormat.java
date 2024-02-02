package cn.hutool.core.date.format;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class GlobalCustomFormat {
   public static final String FORMAT_SECONDS = "#sss";
   public static final String FORMAT_MILLISECONDS = "#SSS";
   private static final Map<CharSequence, Function<Date, String>> formatterMap = new ConcurrentHashMap();
   private static final Map<CharSequence, Function<CharSequence, Date>> parserMap = new ConcurrentHashMap();

   public static void putFormatter(String format, Function<Date, String> func) {
      Assert.notNull(format, "Format must be not null !");
      Assert.notNull(func, "Function must be not null !");
      formatterMap.put(format, func);
   }

   public static void putParser(String format, Function<CharSequence, Date> func) {
      Assert.notNull(format, "Format must be not null !");
      Assert.notNull(func, "Function must be not null !");
      parserMap.put(format, func);
   }

   public static boolean isCustomFormat(String format) {
      return formatterMap.containsKey(format);
   }

   public static String format(Date date, CharSequence format) {
      if (null != formatterMap) {
         Function<Date, String> func = (Function)formatterMap.get(format);
         if (null != func) {
            return (String)func.apply(date);
         }
      }

      return null;
   }

   public static String format(TemporalAccessor temporalAccessor, CharSequence format) {
      return format((Date)DateUtil.date(temporalAccessor), format);
   }

   public static Date parse(CharSequence dateStr, String format) {
      if (null != parserMap) {
         Function<CharSequence, Date> func = (Function)parserMap.get(format);
         if (null != func) {
            return (Date)func.apply(dateStr);
         }
      }

      return null;
   }

   static {
      putFormatter("#sss", (date) -> {
         return String.valueOf(Math.floorDiv(date.getTime(), 1000L));
      });
      putParser("#sss", (dateStr) -> {
         return DateUtil.date(Math.multiplyExact(Long.parseLong(dateStr.toString()), 1000L));
      });
      putFormatter("#SSS", (date) -> {
         return String.valueOf(date.getTime());
      });
      putParser("#SSS", (dateStr) -> {
         return DateUtil.date(Long.parseLong(dateStr.toString()));
      });
   }
}
