package org.h2.value;

import java.nio.charset.StandardCharsets;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;
import org.h2.util.StringUtils;
import org.h2.util.Utils;

public final class ValueVarbinary extends ValueBytesBase {
   public static final ValueVarbinary EMPTY;
   private TypeInfo type;

   protected ValueVarbinary(byte[] var1) {
      super(var1);
      int var2 = var1.length;
      if (var2 > 1048576) {
         throw DbException.getValueTooLongException(getTypeName(this.getValueType()), StringUtils.convertBytesToHex(var1, 41), (long)var2);
      }
   }

   public static ValueVarbinary get(byte[] var0) {
      if (var0.length == 0) {
         return EMPTY;
      } else {
         var0 = Utils.cloneByteArray(var0);
         return getNoCopy(var0);
      }
   }

   public static ValueVarbinary getNoCopy(byte[] var0) {
      if (var0.length == 0) {
         return EMPTY;
      } else {
         ValueVarbinary var1 = new ValueVarbinary(var0);
         return var0.length > SysProperties.OBJECT_CACHE_MAX_PER_ELEMENT_SIZE ? var1 : (ValueVarbinary)Value.cache(var1);
      }
   }

   public TypeInfo getType() {
      TypeInfo var1 = this.type;
      if (var1 == null) {
         long var2 = (long)this.value.length;
         this.type = var1 = new TypeInfo(6, var2, 0, (ExtTypeInfo)null);
      }

      return var1;
   }

   public int getValueType() {
      return 6;
   }

   public String getString() {
      return new String(this.value, StandardCharsets.UTF_8);
   }

   static {
      EMPTY = new ValueVarbinary(Utils.EMPTY_BYTES);
   }
}
