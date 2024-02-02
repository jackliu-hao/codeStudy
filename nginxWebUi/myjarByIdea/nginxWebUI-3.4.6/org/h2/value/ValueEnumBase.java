package org.h2.value;

import java.math.BigDecimal;
import org.h2.engine.CastDataProvider;
import org.h2.util.StringUtils;

public class ValueEnumBase extends Value {
   final String label;
   private final int ordinal;

   protected ValueEnumBase(String var1, int var2) {
      this.label = var1;
      this.ordinal = var2;
   }

   public Value add(Value var1) {
      ValueInteger var2 = var1.convertToInt((Object)null);
      return this.convertToInt((Object)null).add(var2);
   }

   public int compareTypeSafe(Value var1, CompareMode var2, CastDataProvider var3) {
      return Integer.compare(this.getInt(), var1.getInt());
   }

   public Value divide(Value var1, TypeInfo var2) {
      ValueInteger var3 = var1.convertToInt((Object)null);
      return this.convertToInt((Object)null).divide(var3, var2);
   }

   public boolean equals(Object var1) {
      return var1 instanceof ValueEnumBase && this.getInt() == ((ValueEnumBase)var1).getInt();
   }

   public static ValueEnumBase get(String var0, int var1) {
      return new ValueEnumBase(var0, var1);
   }

   public int getInt() {
      return this.ordinal;
   }

   public long getLong() {
      return (long)this.ordinal;
   }

   public BigDecimal getBigDecimal() {
      return BigDecimal.valueOf((long)this.ordinal);
   }

   public float getFloat() {
      return (float)this.ordinal;
   }

   public double getDouble() {
      return (double)this.ordinal;
   }

   public int getSignum() {
      return Integer.signum(this.ordinal);
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      return StringUtils.quoteStringSQL(var1, this.label);
   }

   public String getString() {
      return this.label;
   }

   public TypeInfo getType() {
      return TypeInfo.TYPE_ENUM_UNDEFINED;
   }

   public int getValueType() {
      return 36;
   }

   public int getMemory() {
      return 120;
   }

   public int hashCode() {
      int var1 = 31;
      var1 += this.getString().hashCode();
      var1 += this.getInt();
      return var1;
   }

   public Value modulus(Value var1) {
      ValueInteger var2 = var1.convertToInt((Object)null);
      return this.convertToInt((Object)null).modulus(var2);
   }

   public Value multiply(Value var1) {
      ValueInteger var2 = var1.convertToInt((Object)null);
      return this.convertToInt((Object)null).multiply(var2);
   }

   public Value subtract(Value var1) {
      ValueInteger var2 = var1.convertToInt((Object)null);
      return this.convertToInt((Object)null).subtract(var2);
   }
}
