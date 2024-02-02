package org.h2.value;

import org.h2.util.StringUtils;

public final class ValueEnum extends ValueEnumBase {
   private final ExtTypeInfoEnum enumerators;

   ValueEnum(ExtTypeInfoEnum var1, String var2, int var3) {
      super(var2, var3);
      this.enumerators = var1;
   }

   public TypeInfo getType() {
      return this.enumerators.getType();
   }

   public ExtTypeInfoEnum getEnumerators() {
      return this.enumerators;
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      if ((var2 & 4) == 0) {
         StringUtils.quoteStringSQL(var1.append("CAST("), this.label).append(" AS ");
         return this.enumerators.getType().getSQL(var1, var2).append(')');
      } else {
         return StringUtils.quoteStringSQL(var1, this.label);
      }
   }
}
