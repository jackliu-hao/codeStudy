package io.undertow.servlet.util;

import java.util.Enumeration;

public class EmptyEnumeration implements Enumeration<Object> {
   private static final Enumeration<?> INSTANCE = new EmptyEnumeration();

   public static <T> Enumeration<T> instance() {
      return INSTANCE;
   }

   private EmptyEnumeration() {
   }

   public boolean hasMoreElements() {
      return false;
   }

   public Object nextElement() {
      return null;
   }
}
