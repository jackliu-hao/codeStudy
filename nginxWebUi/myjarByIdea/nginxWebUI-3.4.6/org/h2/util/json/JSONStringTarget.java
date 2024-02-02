package org.h2.util.json;

import java.math.BigDecimal;
import org.h2.util.ByteStack;

public final class JSONStringTarget extends JSONTarget<String> {
   static final char[] HEX = "0123456789abcdef".toCharArray();
   static final byte OBJECT = 1;
   static final byte ARRAY = 2;
   private final StringBuilder builder;
   private final ByteStack stack;
   private final boolean asciiPrintableOnly;
   private boolean needSeparator;
   private boolean afterName;

   public static StringBuilder encodeString(StringBuilder var0, String var1, boolean var2) {
      var0.append('"');
      int var3 = 0;

      for(int var4 = var1.length(); var3 < var4; ++var3) {
         char var5 = var1.charAt(var3);
         switch (var5) {
            case '\b':
               var0.append("\\b");
               break;
            case '\t':
               var0.append("\\t");
               break;
            case '\n':
               var0.append("\\n");
               break;
            case '\f':
               var0.append("\\f");
               break;
            case '\r':
               var0.append("\\r");
               break;
            case '"':
               var0.append("\\\"");
               break;
            case '\'':
               if (var2) {
                  var0.append("\\u0027");
               } else {
                  var0.append('\'');
               }
               break;
            case '\\':
               var0.append("\\\\");
               break;
            default:
               if (var5 < ' ') {
                  var0.append("\\u00").append(HEX[var5 >>> 4 & 15]).append(HEX[var5 & 15]);
               } else if (var2 && var5 > 127) {
                  var0.append("\\u").append(HEX[var5 >>> 12 & 15]).append(HEX[var5 >>> 8 & 15]).append(HEX[var5 >>> 4 & 15]).append(HEX[var5 & 15]);
               } else {
                  var0.append(var5);
               }
         }
      }

      return var0.append('"');
   }

   public JSONStringTarget() {
      this(false);
   }

   public JSONStringTarget(boolean var1) {
      this.builder = new StringBuilder();
      this.stack = new ByteStack();
      this.asciiPrintableOnly = var1;
   }

   public void startObject() {
      this.beforeValue();
      this.afterName = false;
      this.stack.push((byte)1);
      this.builder.append('{');
   }

   public void endObject() {
      if (!this.afterName && this.stack.poll(-1) == 1) {
         this.builder.append('}');
         this.afterValue();
      } else {
         throw new IllegalStateException();
      }
   }

   public void startArray() {
      this.beforeValue();
      this.afterName = false;
      this.stack.push((byte)2);
      this.builder.append('[');
   }

   public void endArray() {
      if (this.stack.poll(-1) != 2) {
         throw new IllegalStateException();
      } else {
         this.builder.append(']');
         this.afterValue();
      }
   }

   public void member(String var1) {
      if (!this.afterName && this.stack.peek(-1) == 1) {
         this.afterName = true;
         this.beforeValue();
         encodeString(this.builder, var1, this.asciiPrintableOnly).append(':');
      } else {
         throw new IllegalStateException();
      }
   }

   public void valueNull() {
      this.beforeValue();
      this.builder.append("null");
      this.afterValue();
   }

   public void valueFalse() {
      this.beforeValue();
      this.builder.append("false");
      this.afterValue();
   }

   public void valueTrue() {
      this.beforeValue();
      this.builder.append("true");
      this.afterValue();
   }

   public void valueNumber(BigDecimal var1) {
      label12: {
         this.beforeValue();
         String var2 = var1.toString();
         int var3 = var2.indexOf(69);
         if (var3 >= 0) {
            ++var3;
            if (var2.charAt(var3) == '+') {
               this.builder.append(var2, 0, var3).append(var2, var3 + 1, var2.length());
               break label12;
            }
         }

         this.builder.append(var2);
      }

      this.afterValue();
   }

   public void valueString(String var1) {
      this.beforeValue();
      encodeString(this.builder, var1, this.asciiPrintableOnly);
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
            this.builder.append(',');
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

   public String getResult() {
      if (this.stack.isEmpty() && this.builder.length() != 0) {
         return this.builder.toString();
      } else {
         throw new IllegalStateException();
      }
   }
}
