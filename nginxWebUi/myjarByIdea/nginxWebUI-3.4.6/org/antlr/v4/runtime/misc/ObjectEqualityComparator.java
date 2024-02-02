package org.antlr.v4.runtime.misc;

public final class ObjectEqualityComparator extends AbstractEqualityComparator<Object> {
   public static final ObjectEqualityComparator INSTANCE = new ObjectEqualityComparator();

   public int hashCode(Object obj) {
      return obj == null ? 0 : obj.hashCode();
   }

   public boolean equals(Object a, Object b) {
      if (a == null) {
         return b == null;
      } else {
         return a.equals(b);
      }
   }
}
