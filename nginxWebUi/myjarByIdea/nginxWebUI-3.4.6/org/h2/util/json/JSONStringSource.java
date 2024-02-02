package org.h2.util.json;

import java.math.BigDecimal;
import org.h2.util.StringUtils;

public final class JSONStringSource extends JSONTextSource {
   private final String string;
   private final int length;
   private int index;

   public static <R> R parse(String var0, JSONTarget<R> var1) {
      (new JSONStringSource(var0, var1)).parse();
      return var1.getResult();
   }

   public static byte[] normalize(String var0) {
      return (byte[])parse(var0, new JSONByteArrayTarget());
   }

   JSONStringSource(String var1, JSONTarget<?> var2) {
      super(var2);
      this.string = var1;
      this.length = var1.length();
      if (this.length == 0) {
         throw new IllegalArgumentException();
      } else {
         if (var1.charAt(this.index) == '\ufeff') {
            ++this.index;
         }

      }
   }

   int nextCharAfterWhitespace() {
      int var1 = this.index;

      while(var1 < this.length) {
         char var2 = this.string.charAt(var1++);
         switch (var2) {
            case '\t':
            case '\n':
            case '\r':
            case ' ':
               break;
            default:
               this.index = var1;
               return var2;
         }
      }

      return -1;
   }

   void readKeyword1(String var1) {
      int var2 = var1.length() - 1;
      if (!this.string.regionMatches(this.index, var1, 1, var2)) {
         throw new IllegalArgumentException();
      } else {
         this.index += var2;
      }
   }

   void parseNumber(boolean var1) {
      int var2 = this.index;
      int var3 = var2 - 1;
      var2 = this.skipInt(var2, var1);
      if (var2 < this.length) {
         label36: {
            char var4 = this.string.charAt(var2);
            if (var4 == '.') {
               var2 = this.skipInt(var2 + 1, false);
               if (var2 >= this.length) {
                  break label36;
               }

               var4 = this.string.charAt(var2);
            }

            if (var4 == 'E' || var4 == 'e') {
               ++var2;
               if (var2 >= this.length) {
                  throw new IllegalArgumentException();
               }

               var4 = this.string.charAt(var2);
               if (var4 == '+' || var4 == '-') {
                  ++var2;
               }

               var2 = this.skipInt(var2, false);
            }
         }
      }

      this.target.valueNumber(new BigDecimal(this.string.substring(var3, var2)));
      this.index = var2;
   }

   private int skipInt(int var1, boolean var2) {
      while(true) {
         if (var1 < this.length) {
            char var3 = this.string.charAt(var1);
            if (var3 >= '0' && var3 <= '9') {
               var2 = true;
               ++var1;
               continue;
            }
         }

         if (!var2) {
            throw new IllegalArgumentException();
         }

         return var1;
      }
   }

   int nextChar() {
      if (this.index >= this.length) {
         throw new IllegalArgumentException();
      } else {
         return this.string.charAt(this.index++);
      }
   }

   char readHex() {
      if (this.index + 3 >= this.length) {
         throw new IllegalArgumentException();
      } else {
         try {
            return (char)Integer.parseInt(this.string.substring(this.index, this.index += 4), 16);
         } catch (NumberFormatException var2) {
            throw new IllegalArgumentException();
         }
      }
   }

   public String toString() {
      return StringUtils.addAsterisk(this.string, this.index);
   }
}
