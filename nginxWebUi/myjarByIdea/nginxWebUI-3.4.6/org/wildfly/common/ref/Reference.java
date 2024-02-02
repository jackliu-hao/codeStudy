package org.wildfly.common.ref;

import java.util.EnumSet;

public interface Reference<T, A> {
   T get();

   A getAttachment();

   void clear();

   Type getType();

   public static enum Type {
      STRONG,
      WEAK,
      PHANTOM,
      SOFT,
      NULL;

      private static final int fullSize = values().length;

      public static boolean isFull(EnumSet<Type> set) {
         return set != null && set.size() == fullSize;
      }

      public boolean in(Type v1) {
         return this == v1;
      }

      public boolean in(Type v1, Type v2) {
         return this == v1 || this == v2;
      }

      public boolean in(Type v1, Type v2, Type v3) {
         return this == v1 || this == v2 || this == v3;
      }

      public boolean in(Type... values) {
         if (values != null) {
            Type[] var2 = values;
            int var3 = values.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               Type value = var2[var4];
               if (this == value) {
                  return true;
               }
            }
         }

         return false;
      }
   }
}
