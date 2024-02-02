package org.h2.value;

import java.math.BigDecimal;
import org.h2.engine.CastDataProvider;

public final class ValueBoolean extends Value {
   public static final int PRECISION = 1;
   public static final int DISPLAY_SIZE = 5;
   public static final ValueBoolean TRUE = new ValueBoolean(true);
   public static final ValueBoolean FALSE = new ValueBoolean(false);
   private final boolean value;

   private ValueBoolean(boolean var1) {
      this.value = var1;
   }

   public TypeInfo getType() {
      return TypeInfo.TYPE_BOOLEAN;
   }

   public int getValueType() {
      return 8;
   }

   public int getMemory() {
      return 0;
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      return var1.append(this.getString());
   }

   public String getString() {
      return this.value ? "TRUE" : "FALSE";
   }

   public boolean getBoolean() {
      return this.value;
   }

   public byte getByte() {
      return (byte)(this.value ? 1 : 0);
   }

   public short getShort() {
      return (short)(this.value ? 1 : 0);
   }

   public int getInt() {
      return this.value ? 1 : 0;
   }

   public long getLong() {
      return this.value ? 1L : 0L;
   }

   public BigDecimal getBigDecimal() {
      return this.value ? BigDecimal.ONE : BigDecimal.ZERO;
   }

   public float getFloat() {
      return this.value ? 1.0F : 0.0F;
   }

   public double getDouble() {
      return this.value ? 1.0 : 0.0;
   }

   public Value negate() {
      return this.value ? FALSE : TRUE;
   }

   public int compareTypeSafe(Value var1, CompareMode var2, CastDataProvider var3) {
      return Boolean.compare(this.value, ((ValueBoolean)var1).value);
   }

   public int hashCode() {
      return this.value ? 1 : 0;
   }

   public static ValueBoolean get(boolean var0) {
      return var0 ? TRUE : FALSE;
   }

   public boolean equals(Object var1) {
      return this == var1;
   }
}
