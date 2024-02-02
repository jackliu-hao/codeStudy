package org.h2.value;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.h2.message.DbException;
import org.h2.util.StringUtils;
import org.h2.util.json.JSONByteArrayTarget;
import org.h2.util.json.JSONBytesSource;
import org.h2.util.json.JSONItemType;
import org.h2.util.json.JSONStringSource;
import org.h2.util.json.JSONStringTarget;

public final class ValueJson extends ValueBytesBase {
   private static final byte[] NULL_BYTES;
   private static final byte[] TRUE_BYTES;
   private static final byte[] FALSE_BYTES;
   public static final ValueJson NULL;
   public static final ValueJson TRUE;
   public static final ValueJson FALSE;
   public static final ValueJson ZERO;

   private ValueJson(byte[] var1) {
      super(var1);
      int var2 = var1.length;
      if (var2 > 1048576) {
         throw DbException.getValueTooLongException(getTypeName(this.getValueType()), StringUtils.convertBytesToHex(var1, 41), (long)var2);
      }
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      String var3 = (String)JSONBytesSource.parse(this.value, new JSONStringTarget(true));
      return var1.append("JSON '").append(var3).append('\'');
   }

   public TypeInfo getType() {
      return TypeInfo.TYPE_JSON;
   }

   public int getValueType() {
      return 38;
   }

   public String getString() {
      return new String(this.value, StandardCharsets.UTF_8);
   }

   public JSONItemType getItemType() {
      switch (this.value[0]) {
         case 91:
            return JSONItemType.ARRAY;
         case 123:
            return JSONItemType.OBJECT;
         default:
            return JSONItemType.SCALAR;
      }
   }

   public static ValueJson fromJson(String var0) {
      byte[] var1;
      try {
         var1 = JSONStringSource.normalize(var0);
      } catch (RuntimeException var3) {
         if (var0.length() > 80) {
            var0 = (new StringBuilder(83)).append(var0, 0, 80).append("...").toString();
         }

         throw DbException.get(22018, (String)var0);
      }

      return getInternal(var1);
   }

   public static ValueJson fromJson(byte[] var0) {
      try {
         var0 = JSONBytesSource.normalize(var0);
      } catch (RuntimeException var3) {
         StringBuilder var2 = (new StringBuilder()).append("X'");
         if (var0.length > 40) {
            StringUtils.convertBytesToHex(var2, var0, 40).append("...");
         } else {
            StringUtils.convertBytesToHex(var2, var0);
         }

         throw DbException.get(22018, (String)var2.append('\'').toString());
      }

      return getInternal(var0);
   }

   public static ValueJson get(boolean var0) {
      return var0 ? TRUE : FALSE;
   }

   public static ValueJson get(int var0) {
      return var0 != 0 ? getNumber(Integer.toString(var0)) : ZERO;
   }

   public static ValueJson get(long var0) {
      return var0 != 0L ? getNumber(Long.toString(var0)) : ZERO;
   }

   public static ValueJson get(BigDecimal var0) {
      if (var0.signum() == 0 && var0.scale() == 0) {
         return ZERO;
      } else {
         String var1 = var0.toString();
         int var2 = var1.indexOf(69);
         if (var2 >= 0) {
            ++var2;
            if (var1.charAt(var2) == '+') {
               int var3 = var1.length();
               var1 = (new StringBuilder(var3 - 1)).append(var1, 0, var2).append(var1, var2 + 1, var3).toString();
            }
         }

         return getNumber(var1);
      }
   }

   public static ValueJson get(String var0) {
      return new ValueJson(JSONByteArrayTarget.encodeString(new ByteArrayOutputStream(var0.length() + 2), var0).toByteArray());
   }

   public static ValueJson getInternal(byte[] var0) {
      int var1 = var0.length;
      switch (var1) {
         case 1:
            if (var0[0] == 48) {
               return ZERO;
            }
         case 2:
         case 3:
         default:
            break;
         case 4:
            if (Arrays.equals(TRUE_BYTES, var0)) {
               return TRUE;
            }

            if (Arrays.equals(NULL_BYTES, var0)) {
               return NULL;
            }
            break;
         case 5:
            if (Arrays.equals(FALSE_BYTES, var0)) {
               return FALSE;
            }
      }

      return new ValueJson(var0);
   }

   private static ValueJson getNumber(String var0) {
      return new ValueJson(var0.getBytes(StandardCharsets.ISO_8859_1));
   }

   static {
      NULL_BYTES = "null".getBytes(StandardCharsets.ISO_8859_1);
      TRUE_BYTES = "true".getBytes(StandardCharsets.ISO_8859_1);
      FALSE_BYTES = "false".getBytes(StandardCharsets.ISO_8859_1);
      NULL = new ValueJson(NULL_BYTES);
      TRUE = new ValueJson(TRUE_BYTES);
      FALSE = new ValueJson(FALSE_BYTES);
      ZERO = new ValueJson(new byte[]{48});
   }
}
