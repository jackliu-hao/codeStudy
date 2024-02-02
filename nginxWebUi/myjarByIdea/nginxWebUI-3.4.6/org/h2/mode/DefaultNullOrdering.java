package org.h2.mode;

public enum DefaultNullOrdering {
   LOW(2, 4),
   HIGH(4, 2),
   FIRST(2, 2),
   LAST(4, 4);

   private static final DefaultNullOrdering[] VALUES = values();
   private final int defaultAscNulls;
   private final int defaultDescNulls;
   private final int nullAsc;
   private final int nullDesc;

   public static DefaultNullOrdering valueOf(int var0) {
      return VALUES[var0];
   }

   private DefaultNullOrdering(int var3, int var4) {
      this.defaultAscNulls = var3;
      this.defaultDescNulls = var4;
      this.nullAsc = var3 == 2 ? -1 : 1;
      this.nullDesc = var4 == 2 ? -1 : 1;
   }

   public int addExplicitNullOrdering(int var1) {
      if ((var1 & 6) == 0) {
         var1 |= (var1 & 1) == 0 ? this.defaultAscNulls : this.defaultDescNulls;
      }

      return var1;
   }

   public int compareNull(boolean var1, int var2) {
      if ((var2 & 2) != 0) {
         return var1 ? -1 : 1;
      } else if ((var2 & 4) != 0) {
         return var1 ? 1 : -1;
      } else if ((var2 & 1) == 0) {
         return var1 ? this.nullAsc : -this.nullAsc;
      } else {
         return var1 ? this.nullDesc : -this.nullDesc;
      }
   }
}
