package freemarker.core;

import freemarker.log.Logger;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

class JavaTemplateNumberFormatFactory extends TemplateNumberFormatFactory {
   static final JavaTemplateNumberFormatFactory INSTANCE = new JavaTemplateNumberFormatFactory();
   static final String COMPUTER = "computer";
   private static final Logger LOG = Logger.getLogger("freemarker.runtime");
   private static final ConcurrentHashMap<CacheKey, NumberFormat> GLOBAL_FORMAT_CACHE = new ConcurrentHashMap();
   private static final int LEAK_ALERT_NUMBER_FORMAT_CACHE_SIZE = 1024;

   private JavaTemplateNumberFormatFactory() {
   }

   public TemplateNumberFormat get(String params, Locale locale, Environment env) throws InvalidFormatParametersException {
      CacheKey cacheKey = new CacheKey(env != null ? env.transformNumberFormatGlobalCacheKey(params) : params, locale);
      NumberFormat jFormat = (NumberFormat)GLOBAL_FORMAT_CACHE.get(cacheKey);
      if (jFormat == null) {
         if ("number".equals(params)) {
            jFormat = NumberFormat.getNumberInstance(locale);
         } else if ("currency".equals(params)) {
            jFormat = NumberFormat.getCurrencyInstance(locale);
         } else if ("percent".equals(params)) {
            jFormat = NumberFormat.getPercentInstance(locale);
         } else if ("computer".equals(params)) {
            jFormat = env.getCNumberFormat();
         } else {
            try {
               jFormat = ExtendedDecimalFormatParser.parse(params, locale);
            } catch (java.text.ParseException var9) {
               String msg = var9.getMessage();
               throw new InvalidFormatParametersException(msg != null ? msg : "Invalid DecimalFormat pattern", var9);
            }
         }

         if (GLOBAL_FORMAT_CACHE.size() >= 1024) {
            boolean triggered = false;
            Class var12 = JavaTemplateNumberFormatFactory.class;
            synchronized(JavaTemplateNumberFormatFactory.class) {
               if (GLOBAL_FORMAT_CACHE.size() >= 1024) {
                  triggered = true;
                  GLOBAL_FORMAT_CACHE.clear();
               }
            }

            if (triggered) {
               LOG.warn("Global Java NumberFormat cache has exceeded 1024 entries => cache flushed. Typical cause: Some template generates high variety of format pattern strings.");
            }
         }

         NumberFormat prevJFormat = (NumberFormat)GLOBAL_FORMAT_CACHE.putIfAbsent(cacheKey, jFormat);
         if (prevJFormat != null) {
            jFormat = prevJFormat;
         }
      }

      NumberFormat jFormat = (NumberFormat)((NumberFormat)jFormat).clone();
      return new JavaTemplateNumberFormat(jFormat, params);
   }

   private static final class CacheKey {
      private final String pattern;
      private final Locale locale;

      CacheKey(String pattern, Locale locale) {
         this.pattern = pattern;
         this.locale = locale;
      }

      public boolean equals(Object o) {
         if (!(o instanceof CacheKey)) {
            return false;
         } else {
            CacheKey fk = (CacheKey)o;
            return fk.pattern.equals(this.pattern) && fk.locale.equals(this.locale);
         }
      }

      public int hashCode() {
         return this.pattern.hashCode() ^ this.locale.hashCode();
      }
   }
}
