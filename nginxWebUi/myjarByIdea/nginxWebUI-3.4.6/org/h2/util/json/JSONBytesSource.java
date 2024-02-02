package org.h2.util.json;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class JSONBytesSource extends JSONTextSource {
   private final byte[] bytes;
   private final int length;
   private int index;

   public static <R> R parse(byte[] var0, JSONTarget<R> var1) {
      int var2 = var0.length;
      Charset var3 = null;
      byte var4;
      byte var5;
      if (var2 >= 4) {
         var4 = var0[0];
         var5 = var0[1];
         byte var6 = var0[2];
         byte var7 = var0[3];
         switch (var4) {
            case -2:
               if (var5 == -1) {
                  var3 = StandardCharsets.UTF_16BE;
               }
               break;
            case -1:
               if (var5 == -2) {
                  if (var6 == 0 && var7 == 0) {
                     var3 = Charset.forName("UTF-32LE");
                  } else {
                     var3 = StandardCharsets.UTF_16LE;
                  }
               }
               break;
            case 0:
               if (var5 != 0) {
                  var3 = StandardCharsets.UTF_16BE;
               } else if (var6 == 0 && var7 != 0 || var6 == -2 && var7 == -1) {
                  var3 = Charset.forName("UTF-32BE");
               }
               break;
            default:
               if (var5 == 0) {
                  if (var6 == 0 && var7 == 0) {
                     var3 = Charset.forName("UTF-32LE");
                  } else {
                     var3 = StandardCharsets.UTF_16LE;
                  }
               }
         }
      } else if (var2 >= 2) {
         var4 = var0[0];
         var5 = var0[1];
         if (var4 != 0) {
            if (var5 == 0) {
               var3 = StandardCharsets.UTF_16LE;
            }
         } else if (var5 != 0) {
            var3 = StandardCharsets.UTF_16BE;
         }
      }

      ((JSONTextSource)(var3 == null ? new JSONBytesSource(var0, var1) : new JSONStringSource(new String(var0, var3), var1))).parse();
      return var1.getResult();
   }

   public static byte[] normalize(byte[] var0) {
      return (byte[])parse(var0, new JSONByteArrayTarget());
   }

   JSONBytesSource(byte[] var1, JSONTarget<?> var2) {
      super(var2);
      this.bytes = var1;
      this.length = var1.length;
      if (this.nextChar() != 65279) {
         this.index = 0;
      }

   }

   int nextCharAfterWhitespace() {
      int var1 = this.index;

      while(var1 < this.length) {
         byte var2 = this.bytes[var1++];
         switch (var2) {
            case 9:
            case 10:
            case 13:
            case 32:
               break;
            default:
               if (var2 < 0) {
                  throw new IllegalArgumentException();
               }

               this.index = var1;
               return var2;
         }
      }

      return -1;
   }

   void readKeyword1(String var1) {
      int var2 = var1.length() - 1;
      if (this.index + var2 > this.length) {
         throw new IllegalArgumentException();
      } else {
         int var3 = this.index;

         for(int var4 = 1; var4 <= var2; ++var4) {
            if (this.bytes[var3] != var1.charAt(var4)) {
               throw new IllegalArgumentException();
            }

            ++var3;
         }

         this.index += var2;
      }
   }

   void parseNumber(boolean var1) {
      int var2 = this.index;
      int var3 = var2 - 1;
      var2 = this.skipInt(var2, var1);
      if (var2 < this.length) {
         label36: {
            byte var4 = this.bytes[var2];
            if (var4 == 46) {
               var2 = this.skipInt(var2 + 1, false);
               if (var2 >= this.length) {
                  break label36;
               }

               var4 = this.bytes[var2];
            }

            if (var4 == 69 || var4 == 101) {
               ++var2;
               if (var2 >= this.length) {
                  throw new IllegalArgumentException();
               }

               var4 = this.bytes[var2];
               if (var4 == 43 || var4 == 45) {
                  ++var2;
               }

               var2 = this.skipInt(var2, false);
            }
         }
      }

      this.target.valueNumber(new BigDecimal(new String(this.bytes, var3, var2 - var3)));
      this.index = var2;
   }

   private int skipInt(int var1, boolean var2) {
      while(true) {
         if (var1 < this.length) {
            byte var3 = this.bytes[var1];
            if (var3 >= 48 && var3 <= 57) {
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
         int var1 = this.bytes[this.index++] & 255;
         if (var1 >= 128) {
            int var2;
            if (var1 >= 224) {
               int var3;
               if (var1 >= 240) {
                  if (this.index + 2 >= this.length) {
                     throw new IllegalArgumentException();
                  }

                  var2 = this.bytes[this.index++] & 255;
                  var3 = this.bytes[this.index++] & 255;
                  int var4 = this.bytes[this.index++] & 255;
                  var1 = ((var1 & 15) << 18) + ((var2 & 63) << 12) + ((var3 & 63) << 6) + (var4 & 63);
                  if (var1 < 65536 || var1 > 1114111 || (var2 & 192) != 128 || (var3 & 192) != 128 || (var4 & 192) != 128) {
                     throw new IllegalArgumentException();
                  }
               } else {
                  if (this.index + 1 >= this.length) {
                     throw new IllegalArgumentException();
                  }

                  var2 = this.bytes[this.index++] & 255;
                  var3 = this.bytes[this.index++] & 255;
                  var1 = ((var1 & 15) << 12) + ((var2 & 63) << 6) + (var3 & 63);
                  if (var1 < 2048 || (var2 & 192) != 128 || (var3 & 192) != 128) {
                     throw new IllegalArgumentException();
                  }
               }
            } else {
               if (this.index >= this.length) {
                  throw new IllegalArgumentException();
               }

               var2 = this.bytes[this.index++] & 255;
               var1 = ((var1 & 31) << 6) + (var2 & 63);
               if (var1 < 128 || (var2 & 192) != 128) {
                  throw new IllegalArgumentException();
               }
            }
         }

         return var1;
      }
   }

   char readHex() {
      if (this.index + 3 >= this.length) {
         throw new IllegalArgumentException();
      } else {
         int var1;
         try {
            var1 = Integer.parseInt(new String(this.bytes, this.index, 4), 16);
         } catch (NumberFormatException var3) {
            throw new IllegalArgumentException();
         }

         this.index += 4;
         return (char)var1;
      }
   }

   public String toString() {
      return new String(this.bytes, 0, this.index, StandardCharsets.UTF_8) + "[*]" + new String(this.bytes, this.index, this.length, StandardCharsets.UTF_8);
   }
}
