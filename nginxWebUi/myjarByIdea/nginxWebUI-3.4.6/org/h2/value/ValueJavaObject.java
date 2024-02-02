package org.h2.value;

import org.h2.engine.SysProperties;
import org.h2.message.DbException;
import org.h2.util.StringUtils;
import org.h2.util.Utils;

public final class ValueJavaObject extends ValueBytesBase {
   private static final ValueJavaObject EMPTY;

   protected ValueJavaObject(byte[] var1) {
      super(var1);
      int var2 = this.value.length;
      if (var2 > 1048576) {
         throw DbException.getValueTooLongException(getTypeName(this.getValueType()), StringUtils.convertBytesToHex(this.value, 41), (long)var2);
      }
   }

   public static ValueJavaObject getNoCopy(byte[] var0) {
      int var1 = var0.length;
      if (var1 == 0) {
         return EMPTY;
      } else {
         ValueJavaObject var2 = new ValueJavaObject(var0);
         return var1 > SysProperties.OBJECT_CACHE_MAX_PER_ELEMENT_SIZE ? var2 : (ValueJavaObject)Value.cache(var2);
      }
   }

   public TypeInfo getType() {
      return TypeInfo.TYPE_JAVA_OBJECT;
   }

   public int getValueType() {
      return 35;
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      return (var2 & 4) == 0 ? super.getSQL(var1.append("CAST("), 0).append(" AS JAVA_OBJECT)") : super.getSQL(var1, 0);
   }

   public String getString() {
      throw DbException.get(22018, (String)"JAVA_OBJECT to CHARACTER VARYING");
   }

   static {
      EMPTY = new ValueJavaObject(Utils.EMPTY_BYTES);
   }
}
