package org.h2.engine;

import org.h2.message.DbException;

public final class GeneratedKeysMode {
   public static final int NONE = 0;
   public static final int AUTO = 1;
   public static final int COLUMN_NUMBERS = 2;
   public static final int COLUMN_NAMES = 3;

   public static int valueOf(Object var0) {
      if (var0 != null && !Boolean.FALSE.equals(var0)) {
         if (Boolean.TRUE.equals(var0)) {
            return 1;
         } else if (var0 instanceof int[]) {
            return ((int[])((int[])var0)).length > 0 ? 2 : 0;
         } else if (var0 instanceof String[]) {
            return ((String[])((String[])var0)).length > 0 ? 3 : 0;
         } else {
            throw DbException.getInternalError();
         }
      } else {
         return 0;
      }
   }

   private GeneratedKeysMode() {
   }
}
