package org.h2.value;

import java.util.Objects;
import org.h2.util.geometry.EWKTUtils;

public final class ExtTypeInfoGeometry extends ExtTypeInfo {
   private final int type;
   private final Integer srid;

   static StringBuilder toSQL(StringBuilder var0, int var1, Integer var2) {
      if (var1 == 0 && var2 == null) {
         return var0;
      } else {
         var0.append('(');
         if (var1 == 0) {
            var0.append("GEOMETRY");
         } else {
            EWKTUtils.formatGeometryTypeAndDimensionSystem(var0, var1);
         }

         if (var2 != null) {
            var0.append(", ").append(var2);
         }

         return var0.append(')');
      }
   }

   public ExtTypeInfoGeometry(int var1, Integer var2) {
      this.type = var1;
      this.srid = var2;
   }

   public int hashCode() {
      return 31 * (this.srid == null ? 0 : this.srid.hashCode()) + this.type;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && var1.getClass() == ExtTypeInfoGeometry.class) {
         ExtTypeInfoGeometry var2 = (ExtTypeInfoGeometry)var1;
         return this.type == var2.type && Objects.equals(this.srid, var2.srid);
      } else {
         return false;
      }
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      return toSQL(var1, this.type, this.srid);
   }

   public int getType() {
      return this.type;
   }

   public Integer getSrid() {
      return this.srid;
   }
}
