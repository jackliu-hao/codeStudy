package com.beust.jcommander.internal;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class Sets {
   public static <K> Set<K> newHashSet() {
      return new HashSet();
   }

   public static <K> Set<K> newLinkedHashSet() {
      return new LinkedHashSet();
   }
}
