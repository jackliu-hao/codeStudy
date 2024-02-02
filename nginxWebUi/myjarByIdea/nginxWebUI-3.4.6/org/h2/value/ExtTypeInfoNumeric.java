package org.h2.value;

public final class ExtTypeInfoNumeric extends ExtTypeInfo {
   public static final ExtTypeInfoNumeric DECIMAL = new ExtTypeInfoNumeric();

   private ExtTypeInfoNumeric() {
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      return var1.append("DECIMAL");
   }
}
