package com.beust.jcommander.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Lists {
   public static <K> List<K> newArrayList() {
      return new ArrayList();
   }

   public static <K> List<K> newArrayList(Collection<K> c) {
      return new ArrayList(c);
   }

   public static <K> List<K> newArrayList(K... c) {
      return new ArrayList(Arrays.asList(c));
   }

   public static <K> List<K> newArrayList(int size) {
      return new ArrayList(size);
   }

   public static <K> LinkedList<K> newLinkedList() {
      return new LinkedList();
   }

   public static <K> LinkedList<K> newLinkedList(Collection<K> c) {
      return new LinkedList(c);
   }
}
