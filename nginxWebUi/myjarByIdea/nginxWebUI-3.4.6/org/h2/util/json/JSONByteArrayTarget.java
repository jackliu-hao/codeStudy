package org.h2.util.json;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import org.h2.util.ByteStack;

public final class JSONByteArrayTarget extends JSONTarget<byte[]> {
   private static final byte[] NULL_BYTES;
   private static final byte[] FALSE_BYTES;
   private static final byte[] TRUE_BYTES;
   private static final byte[] U00_BYTES;
   private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
   private final ByteStack stack = new ByteStack();
   private boolean needSeparator;
   private boolean afterName;

   public static ByteArrayOutputStream encodeString(ByteArrayOutputStream var0, String var1) {
      var0.write(34);
      int var2 = 0;

      for(int var3 = var1.length(); var2 < var3; ++var2) {
         char var4 = var1.charAt(var2);
         switch (var4) {
            case '\b':
               var0.write(92);
               var0.write(98);
               break;
            case '\t':
               var0.write(92);
               var0.write(116);
               break;
            case '\n':
               var0.write(92);
               var0.write(110);
               break;
            case '\f':
               var0.write(92);
               var0.write(102);
               break;
            case '\r':
               var0.write(92);
               var0.write(114);
               break;
            case '"':
               var0.write(92);
               var0.write(34);
               break;
            case '\\':
               var0.write(92);
               var0.write(92);
               break;
            default:
               if (var4 >= ' ') {
                  if (var4 < 128) {
                     var0.write(var4);
                  } else if (var4 < 2048) {
                     var0.write(192 | var4 >> 6);
                     var0.write(128 | var4 & 63);
                  } else if (!Character.isSurrogate(var4)) {
                     var0.write(224 | var4 >> 12);
                     var0.write(128 | var4 >> 6 & 63);
                     var0.write(128 | var4 & 63);
                  } else {
                     if (!Character.isHighSurrogate(var4)) {
                        throw new IllegalArgumentException();
                     }

                     ++var2;
                     char var5;
                     if (var2 >= var3 || !Character.isLowSurrogate(var5 = var1.charAt(var2))) {
                        throw new IllegalArgumentException();
                     }

                     int var6 = Character.toCodePoint(var4, var5);
                     var0.write(240 | var6 >> 18);
                     var0.write(128 | var6 >> 12 & 63);
                     var0.write(128 | var6 >> 6 & 63);
                     var0.write(128 | var6 & 63);
                  }
               } else {
                  var0.write(U00_BYTES, 0, 4);
                  var0.write(JSONStringTarget.HEX[var4 >>> 4 & 15]);
                  var0.write(JSONStringTarget.HEX[var4 & 15]);
               }
         }
      }

      var0.write(34);
      return var0;
   }

   public void startObject() {
      this.beforeValue();
      this.afterName = false;
      this.stack.push((byte)1);
      this.baos.write(123);
   }

   public void endObject() {
      if (!this.afterName && this.stack.poll(-1) == 1) {
         this.baos.write(125);
         this.afterValue();
      } else {
         throw new IllegalStateException();
      }
   }

   public void startArray() {
      this.beforeValue();
      this.afterName = false;
      this.stack.push((byte)2);
      this.baos.write(91);
   }

   public void endArray() {
      if (this.stack.poll(-1) != 2) {
         throw new IllegalStateException();
      } else {
         this.baos.write(93);
         this.afterValue();
      }
   }

   public void member(String var1) {
      if (!this.afterName && this.stack.peek(-1) == 1) {
         this.afterName = true;
         this.beforeValue();
         encodeString(this.baos, var1).write(58);
      } else {
         throw new IllegalStateException();
      }
   }

   public void valueNull() {
      this.beforeValue();
      this.baos.write(NULL_BYTES, 0, 4);
      this.afterValue();
   }

   public void valueFalse() {
      this.beforeValue();
      this.baos.write(FALSE_BYTES, 0, 5);
      this.afterValue();
   }

   public void valueTrue() {
      this.beforeValue();
      this.baos.write(TRUE_BYTES, 0, 4);
      this.afterValue();
   }

   public void valueNumber(BigDecimal var1) {
      label12: {
         this.beforeValue();
         String var2 = var1.toString();
         int var3 = var2.indexOf(69);
         byte[] var4 = var2.getBytes(StandardCharsets.ISO_8859_1);
         if (var3 >= 0) {
            ++var3;
            if (var2.charAt(var3) == '+') {
               this.baos.write(var4, 0, var3);
               this.baos.write(var4, var3 + 1, var4.length - var3 - 1);
               break label12;
            }
         }

         this.baos.write(var4, 0, var4.length);
      }

      this.afterValue();
   }

   public void valueString(String var1) {
      this.beforeValue();
      encodeString(this.baos, var1);
      this.afterValue();
   }

   private void beforeValue() {
      if (!this.afterName && this.stack.peek(-1) == 1) {
         throw new IllegalStateException();
      } else {
         if (this.needSeparator) {
            if (this.stack.isEmpty()) {
               throw new IllegalStateException();
            }

            this.needSeparator = false;
            this.baos.write(44);
         }

      }
   }

   private void afterValue() {
      this.needSeparator = true;
      this.afterName = false;
   }

   public boolean isPropertyExpected() {
      return !this.afterName && this.stack.peek(-1) == 1;
   }

   public boolean isValueSeparatorExpected() {
      return this.needSeparator;
   }

   public byte[] getResult() {
      if (this.stack.isEmpty() && this.baos.size() != 0) {
         return this.baos.toByteArray();
      } else {
         throw new IllegalStateException();
      }
   }

   static {
      NULL_BYTES = "null".getBytes(StandardCharsets.ISO_8859_1);
      FALSE_BYTES = "false".getBytes(StandardCharsets.ISO_8859_1);
      TRUE_BYTES = "true".getBytes(StandardCharsets.ISO_8859_1);
      U00_BYTES = "\\u00".getBytes(StandardCharsets.ISO_8859_1);
   }
}
