package org.h2.value;

import org.h2.engine.CastDataProvider;
import org.h2.engine.SysProperties;
import org.h2.util.StringUtils;

public final class ValueVarcharIgnoreCase extends ValueStringBase {
   private static final ValueVarcharIgnoreCase EMPTY = new ValueVarcharIgnoreCase("");
   private int hash;

   private ValueVarcharIgnoreCase(String var1) {
      super(var1);
   }

   public int getValueType() {
      return 4;
   }

   public int compareTypeSafe(Value var1, CompareMode var2, CastDataProvider var3) {
      return var2.compareString(this.value, ((ValueStringBase)var1).value, true);
   }

   public boolean equals(Object var1) {
      return var1 instanceof ValueVarcharIgnoreCase && this.value.equalsIgnoreCase(((ValueVarcharIgnoreCase)var1).value);
   }

   public int hashCode() {
      if (this.hash == 0) {
         this.hash = this.value.toUpperCase().hashCode();
      }

      return this.hash;
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      return (var2 & 4) == 0 ? StringUtils.quoteStringSQL(var1.append("CAST("), this.value).append(" AS VARCHAR_IGNORECASE(").append(this.value.length()).append("))") : StringUtils.quoteStringSQL(var1, this.value);
   }

   public static ValueVarcharIgnoreCase get(String var0) {
      int var1 = var0.length();
      if (var1 == 0) {
         return EMPTY;
      } else {
         ValueVarcharIgnoreCase var2 = new ValueVarcharIgnoreCase(StringUtils.cache(var0));
         if (var1 > SysProperties.OBJECT_CACHE_MAX_PER_ELEMENT_SIZE) {
            return var2;
         } else {
            ValueVarcharIgnoreCase var3 = (ValueVarcharIgnoreCase)Value.cache(var2);
            return var3.value.equals(var0) ? var3 : var2;
         }
      }
   }
}
