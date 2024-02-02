package ch.qos.logback.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class StringCollectionUtil {
   public static void retainMatching(Collection<String> values, String... patterns) {
      retainMatching(values, (Collection)Arrays.asList(patterns));
   }

   public static void retainMatching(Collection<String> values, Collection<String> patterns) {
      if (!patterns.isEmpty()) {
         List<String> matches = new ArrayList(values.size());
         Iterator var3 = patterns.iterator();

         while(var3.hasNext()) {
            String p = (String)var3.next();
            Pattern pattern = Pattern.compile(p);
            Iterator var6 = values.iterator();

            while(var6.hasNext()) {
               String value = (String)var6.next();
               if (pattern.matcher(value).matches()) {
                  matches.add(value);
               }
            }
         }

         values.retainAll(matches);
      }
   }

   public static void removeMatching(Collection<String> values, String... patterns) {
      removeMatching(values, (Collection)Arrays.asList(patterns));
   }

   public static void removeMatching(Collection<String> values, Collection<String> patterns) {
      List<String> matches = new ArrayList(values.size());
      Iterator var3 = patterns.iterator();

      while(var3.hasNext()) {
         String p = (String)var3.next();
         Pattern pattern = Pattern.compile(p);
         Iterator var6 = values.iterator();

         while(var6.hasNext()) {
            String value = (String)var6.next();
            if (pattern.matcher(value).matches()) {
               matches.add(value);
            }
         }
      }

      values.removeAll(matches);
   }
}
