package org.apache.commons.compress.utils;

import java.util.ArrayList;
import java.util.Iterator;

public class Lists {
   public static <E> ArrayList<E> newArrayList() {
      return new ArrayList();
   }

   public static <E> ArrayList<E> newArrayList(Iterator<? extends E> iterator) {
      ArrayList<E> list = newArrayList();
      Iterators.addAll(list, iterator);
      return list;
   }

   private Lists() {
   }
}
