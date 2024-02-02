package org.h2.value;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;

abstract class ValueStringBase extends Value {
   String value;
   private TypeInfo type;

   ValueStringBase(String var1) {
      int var2 = var1.length();
      if (var2 > 1048576) {
         throw DbException.getValueTooLongException(getTypeName(this.getValueType()), var1, (long)var2);
      } else {
         this.value = var1;
      }
   }

   public final TypeInfo getType() {
      TypeInfo var1 = this.type;
      if (var1 == null) {
         int var2 = this.value.length();
         this.type = var1 = new TypeInfo(this.getValueType(), (long)var2, 0, (ExtTypeInfo)null);
      }

      return var1;
   }

   public int compareTypeSafe(Value var1, CompareMode var2, CastDataProvider var3) {
      return var2.compareString(this.value, ((ValueStringBase)var1).value, false);
   }

   public int hashCode() {
      return this.getClass().hashCode() ^ this.value.hashCode();
   }

   public final String getString() {
      return this.value;
   }

   public final byte[] getBytes() {
      return this.value.getBytes(StandardCharsets.UTF_8);
   }

   public final boolean getBoolean() {
      String var1 = this.value.trim();
      if (!var1.equalsIgnoreCase("true") && !var1.equalsIgnoreCase("t") && !var1.equalsIgnoreCase("yes") && !var1.equalsIgnoreCase("y")) {
         if (!var1.equalsIgnoreCase("false") && !var1.equalsIgnoreCase("f") && !var1.equalsIgnoreCase("no") && !var1.equalsIgnoreCase("n")) {
            try {
               return (new BigDecimal(var1)).signum() != 0;
            } catch (NumberFormatException var3) {
               throw this.getDataConversionError(8);
            }
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   public final byte getByte() {
      try {
         return Byte.parseByte(this.value.trim());
      } catch (NumberFormatException var2) {
         throw DbException.get(22018, var2, this.value);
      }
   }

   public final short getShort() {
      try {
         return Short.parseShort(this.value.trim());
      } catch (NumberFormatException var2) {
         throw DbException.get(22018, var2, this.value);
      }
   }

   public final int getInt() {
      try {
         return Integer.parseInt(this.value.trim());
      } catch (NumberFormatException var2) {
         throw DbException.get(22018, var2, this.value);
      }
   }

   public final long getLong() {
      try {
         return Long.parseLong(this.value.trim());
      } catch (NumberFormatException var2) {
         throw DbException.get(22018, var2, this.value);
      }
   }

   public final BigDecimal getBigDecimal() {
      try {
         return new BigDecimal(this.value.trim());
      } catch (NumberFormatException var2) {
         throw DbException.get(22018, var2, this.value);
      }
   }

   public final float getFloat() {
      try {
         return Float.parseFloat(this.value.trim());
      } catch (NumberFormatException var2) {
         throw DbException.get(22018, var2, this.value);
      }
   }

   public final double getDouble() {
      try {
         return Double.parseDouble(this.value.trim());
      } catch (NumberFormatException var2) {
         throw DbException.get(22018, var2, this.value);
      }
   }

   public final int getMemory() {
      return this.value.length() * 2 + 94;
   }

   public boolean equals(Object var1) {
      return var1 != null && this.getClass() == var1.getClass() && this.value.equals(((ValueStringBase)var1).value);
   }
}
