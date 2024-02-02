package freemarker.core;

import java.util.Locale;

public class _CoreLocaleUtils {
   public static Locale getLessSpecificLocale(Locale locale) {
      String country = locale.getCountry();
      if (locale.getVariant().length() != 0) {
         String language = locale.getLanguage();
         return country != null ? new Locale(language, country) : new Locale(language);
      } else {
         return country.length() != 0 ? new Locale(locale.getLanguage()) : null;
      }
   }
}
