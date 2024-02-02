package com.beust.jcommander;

import com.beust.jcommander.internal.Maps;
import java.util.Iterator;
import java.util.Map;

public class FuzzyMap {
   public static <V> V findInMap(Map<? extends IKey, V> map, IKey name, boolean caseSensitive, boolean allowAbbreviations) {
      if (allowAbbreviations) {
         return findAbbreviatedValue(map, name, caseSensitive);
      } else if (caseSensitive) {
         return map.get(name);
      } else {
         Iterator i$ = map.keySet().iterator();

         IKey c;
         do {
            if (!i$.hasNext()) {
               return null;
            }

            c = (IKey)i$.next();
         } while(!c.getName().equalsIgnoreCase(name.getName()));

         return map.get(c);
      }
   }

   private static <V> V findAbbreviatedValue(Map<? extends IKey, V> map, IKey name, boolean caseSensitive) {
      String string = name.getName();
      Map<String, V> results = Maps.newHashMap();
      Iterator i$ = map.keySet().iterator();

      while(i$.hasNext()) {
         IKey c = (IKey)i$.next();
         String n = c.getName();
         boolean match = caseSensitive && n.startsWith(string) || !caseSensitive && n.toLowerCase().startsWith(string.toLowerCase());
         if (match) {
            results.put(n, map.get(c));
         }
      }

      if (results.size() > 1) {
         throw new ParameterException("Ambiguous option: " + name + " matches " + results.keySet());
      } else {
         Object result;
         if (results.size() == 1) {
            result = results.values().iterator().next();
         } else {
            result = null;
         }

         return result;
      }
   }

   interface IKey {
      String getName();
   }
}
