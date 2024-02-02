package org.h2.util;

public final class SimpleColumnInfo {
   public final String name;
   public final int type;
   public final String typeName;
   public final int precision;
   public final int scale;

   public SimpleColumnInfo(String var1, int var2, String var3, int var4, int var5) {
      this.name = var1;
      this.type = var2;
      this.typeName = var3;
      this.precision = var4;
      this.scale = var5;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         SimpleColumnInfo var2 = (SimpleColumnInfo)var1;
         return this.name.equals(var2.name);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.name.hashCode();
   }
}
