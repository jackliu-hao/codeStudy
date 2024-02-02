package org.h2.value;

import org.h2.engine.CastDataProvider;
import org.h2.engine.SysProperties;
import org.h2.util.StringUtils;

public final class ValueChar extends ValueStringBase {
   private ValueChar(String var1) {
      super(var1);
   }

   public int getValueType() {
      return 1;
   }

   public int compareTypeSafe(Value var1, CompareMode var2, CastDataProvider var3) {
      return var2.compareString(this.convertToChar().getString(), var1.convertToChar().getString(), false);
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      if ((var2 & 4) == 0) {
         int var3 = this.value.length();
         return StringUtils.quoteStringSQL(var1.append("CAST("), this.value).append(" AS CHAR(").append(var3 > 0 ? var3 : 1).append("))");
      } else {
         return StringUtils.quoteStringSQL(var1, this.value);
      }
   }

   public static ValueChar get(String var0) {
      ValueChar var1 = new ValueChar(StringUtils.cache(var0));
      return var0.length() > SysProperties.OBJECT_CACHE_MAX_PER_ELEMENT_SIZE ? var1 : (ValueChar)Value.cache(var1);
   }
}
