package org.h2.value;

import java.nio.charset.StandardCharsets;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;
import org.h2.util.StringUtils;
import org.h2.util.Utils;

public final class ValueBinary extends ValueBytesBase {
   private TypeInfo type;

   protected ValueBinary(byte[] var1) {
      super(var1);
      int var2 = var1.length;
      if (var2 > 1048576) {
         throw DbException.getValueTooLongException(getTypeName(this.getValueType()), StringUtils.convertBytesToHex(var1, 41), (long)var2);
      }
   }

   public static ValueBinary get(byte[] var0) {
      return getNoCopy(Utils.cloneByteArray(var0));
   }

   public static ValueBinary getNoCopy(byte[] var0) {
      ValueBinary var1 = new ValueBinary(var0);
      return var0.length > SysProperties.OBJECT_CACHE_MAX_PER_ELEMENT_SIZE ? var1 : (ValueBinary)Value.cache(var1);
   }

   public TypeInfo getType() {
      TypeInfo var1 = this.type;
      if (var1 == null) {
         long var2 = (long)this.value.length;
         this.type = var1 = new TypeInfo(5, var2, 0, (ExtTypeInfo)null);
      }

      return var1;
   }

   public int getValueType() {
      return 5;
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      if ((var2 & 4) == 0) {
         int var3 = this.value.length;
         return super.getSQL(var1.append("CAST("), var2).append(" AS BINARY(").append(var3 > 0 ? var3 : 1).append("))");
      } else {
         return super.getSQL(var1, var2);
      }
   }

   public String getString() {
      return new String(this.value, StandardCharsets.UTF_8);
   }
}
