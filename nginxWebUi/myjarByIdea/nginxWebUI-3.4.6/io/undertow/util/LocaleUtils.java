package io.undertow.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class LocaleUtils {
   public static Locale getLocaleFromString(String localeString) {
      return localeString == null ? null : Locale.forLanguageTag(localeString);
   }

   public static List<Locale> getLocalesFromHeader(String acceptLanguage) {
      return acceptLanguage == null ? Collections.emptyList() : getLocalesFromHeader(Collections.singletonList(acceptLanguage));
   }

   public static List<Locale> getLocalesFromHeader(List<String> acceptLanguage) {
      if (acceptLanguage != null && !acceptLanguage.isEmpty()) {
         List<Locale> ret = new ArrayList();
         List<List<QValueParser.QValueResult>> parsedResults = QValueParser.parse(acceptLanguage);
         Iterator var3 = parsedResults.iterator();

         while(var3.hasNext()) {
            List<QValueParser.QValueResult> qvalueResult = (List)var3.next();
            Iterator var5 = qvalueResult.iterator();

            while(var5.hasNext()) {
               QValueParser.QValueResult res = (QValueParser.QValueResult)var5.next();
               if (!res.isQValueZero()) {
                  Locale e = getLocaleFromString(res.getValue());
                  ret.add(e);
               }
            }
         }

         return ret;
      } else {
         return Collections.emptyList();
      }
   }
}
