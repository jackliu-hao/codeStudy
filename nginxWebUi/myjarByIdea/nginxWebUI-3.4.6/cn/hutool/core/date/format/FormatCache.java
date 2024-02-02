package cn.hutool.core.date.format;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Tuple;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

abstract class FormatCache<F extends Format> {
   static final int NONE = -1;
   private final ConcurrentMap<Tuple, F> cInstanceCache = new ConcurrentHashMap(7);
   private static final ConcurrentMap<Tuple, String> C_DATE_TIME_INSTANCE_CACHE = new ConcurrentHashMap(7);

   public F getInstance() {
      return this.getDateTimeInstance(3, 3, (TimeZone)null, (Locale)null);
   }

   public F getInstance(String pattern, TimeZone timeZone, Locale locale) {
      Assert.notBlank(pattern, "pattern must not be blank");
      if (timeZone == null) {
         timeZone = TimeZone.getDefault();
      }

      if (locale == null) {
         locale = Locale.getDefault();
      }

      Tuple key = new Tuple(new Object[]{pattern, timeZone, locale});
      F format = (Format)this.cInstanceCache.get(key);
      if (format == null) {
         format = this.createInstance(pattern, timeZone, locale);
         F previousValue = (Format)this.cInstanceCache.putIfAbsent(key, format);
         if (previousValue != null) {
            format = previousValue;
         }
      }

      return format;
   }

   protected abstract F createInstance(String var1, TimeZone var2, Locale var3);

   F getDateTimeInstance(Integer dateStyle, Integer timeStyle, TimeZone timeZone, Locale locale) {
      if (locale == null) {
         locale = Locale.getDefault();
      }

      String pattern = getPatternForStyle(dateStyle, timeStyle, locale);
      return this.getInstance(pattern, timeZone, locale);
   }

   F getDateInstance(int dateStyle, TimeZone timeZone, Locale locale) {
      return this.getDateTimeInstance(dateStyle, (Integer)null, timeZone, locale);
   }

   F getTimeInstance(int timeStyle, TimeZone timeZone, Locale locale) {
      return this.getDateTimeInstance((Integer)null, timeStyle, timeZone, locale);
   }

   static String getPatternForStyle(Integer dateStyle, Integer timeStyle, Locale locale) {
      Tuple key = new Tuple(new Object[]{dateStyle, timeStyle, locale});
      String pattern = (String)C_DATE_TIME_INSTANCE_CACHE.get(key);
      if (pattern == null) {
         try {
            DateFormat formatter;
            if (dateStyle == null) {
               formatter = DateFormat.getTimeInstance(timeStyle, locale);
            } else if (timeStyle == null) {
               formatter = DateFormat.getDateInstance(dateStyle, locale);
            } else {
               formatter = DateFormat.getDateTimeInstance(dateStyle, timeStyle, locale);
            }

            pattern = ((SimpleDateFormat)formatter).toPattern();
            String previous = (String)C_DATE_TIME_INSTANCE_CACHE.putIfAbsent(key, pattern);
            if (previous != null) {
               pattern = previous;
            }
         } catch (ClassCastException var7) {
            throw new IllegalArgumentException("No date time pattern for locale: " + locale);
         }
      }

      return pattern;
   }
}
