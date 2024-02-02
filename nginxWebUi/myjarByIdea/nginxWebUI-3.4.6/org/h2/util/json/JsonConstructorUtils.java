package org.h2.util.json;

import java.io.ByteArrayOutputStream;
import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueJson;
import org.h2.value.ValueNull;

public final class JsonConstructorUtils {
   public static final int JSON_ABSENT_ON_NULL = 1;
   public static final int JSON_WITH_UNIQUE_KEYS = 2;

   private JsonConstructorUtils() {
   }

   public static void jsonObjectAppend(ByteArrayOutputStream var0, String var1, Value var2) {
      if (var0.size() > 1) {
         var0.write(44);
      }

      JSONByteArrayTarget.encodeString(var0, var1).write(58);
      byte[] var3 = var2.convertTo(TypeInfo.TYPE_JSON).getBytesNoCopy();
      var0.write(var3, 0, var3.length);
   }

   public static Value jsonObjectFinish(ByteArrayOutputStream var0, int var1) {
      var0.write(125);
      byte[] var2 = var0.toByteArray();
      if ((var1 & 2) != 0) {
         try {
            JSONBytesSource.parse(var2, new JSONValidationTargetWithUniqueKeys());
         } catch (RuntimeException var5) {
            String var4 = (String)JSONBytesSource.parse(var2, new JSONStringTarget());
            throw DbException.getInvalidValueException("JSON WITH UNIQUE KEYS", var4.length() < 128 ? var2 : var4.substring(0, 128) + "...");
         }
      }

      return ValueJson.getInternal(var2);
   }

   public static void jsonArrayAppend(ByteArrayOutputStream var0, Value var1, int var2) {
      if (var1 == ValueNull.INSTANCE) {
         if ((var2 & 1) != 0) {
            return;
         }

         var1 = ValueJson.NULL;
      }

      if (var0.size() > 1) {
         var0.write(44);
      }

      byte[] var3 = ((Value)var1).convertTo(TypeInfo.TYPE_JSON).getBytesNoCopy();
      var0.write(var3, 0, var3.length);
   }
}
