package com.beust.jcommander.internal;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Maps {
   public static <K, V> Map<K, V> newHashMap() {
      return new HashMap();
   }

   public static <K, V> Map<K, V> newLinkedHashMap() {
      return new LinkedHashMap();
   }

   public static <T> Map<T, T> newHashMap(T... parameters) {
      Map<T, T> result = newHashMap();

      for(int i = 0; i < parameters.length; i += 2) {
         result.put(parameters[i], parameters[i + 1]);
      }

      return result;
   }
}
