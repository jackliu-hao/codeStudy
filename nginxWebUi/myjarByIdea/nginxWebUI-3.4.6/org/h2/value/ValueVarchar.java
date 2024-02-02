package org.h2.value;

import org.h2.engine.CastDataProvider;
import org.h2.engine.SysProperties;
import org.h2.util.StringUtils;

public final class ValueVarchar extends ValueStringBase {
   public static final ValueVarchar EMPTY = new ValueVarchar("");

   private ValueVarchar(String var1) {
      super(var1);
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      return StringUtils.quoteStringSQL(var1, this.value);
   }

   public int getValueType() {
      return 2;
   }

   public static Value get(String var0) {
      return get(var0, (CastDataProvider)null);
   }

   public static Value get(String var0, CastDataProvider var1) {
      if (!var0.isEmpty()) {
         ValueVarchar var2 = new ValueVarchar(StringUtils.cache(var0));
         return (Value)(var0.length() > SysProperties.OBJECT_CACHE_MAX_PER_ELEMENT_SIZE ? var2 : Value.cache(var2));
      } else {
         return (Value)(var1 != null && var1.getMode().treatEmptyStringsAsNull ? ValueNull.INSTANCE : EMPTY);
      }
   }
}
