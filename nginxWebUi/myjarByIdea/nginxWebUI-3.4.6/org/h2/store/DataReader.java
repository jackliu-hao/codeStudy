package org.h2.store;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class DataReader extends Reader {
   private final InputStream in;

   public DataReader(InputStream var1) {
      this.in = var1;
   }

   public byte readByte() throws IOException {
      int var1 = this.in.read();
      if (var1 < 0) {
         throw new FastEOFException();
      } else {
         return (byte)var1;
      }
   }

   public int readVarInt() throws IOException {
      byte var1 = this.readByte();
      if (var1 >= 0) {
         return var1;
      } else {
         int var2 = var1 & 127;
         var1 = this.readByte();
         if (var1 >= 0) {
            return var2 | var1 << 7;
         } else {
            var2 |= (var1 & 127) << 7;
            var1 = this.readByte();
            if (var1 >= 0) {
               return var2 | var1 << 14;
            } else {
               var2 |= (var1 & 127) << 14;
               var1 = this.readByte();
               return var1 >= 0 ? var2 | var1 << 21 : var2 | (var1 & 127) << 21 | this.readByte() << 28;
            }
         }
      }
   }

   private char readChar() throws IOException {
      int var1 = this.readByte() & 255;
      if (var1 < 128) {
         return (char)var1;
      } else {
         return var1 >= 224 ? (char)(((var1 & 15) << 12) + ((this.readByte() & 63) << 6) + (this.readByte() & 63)) : (char)(((var1 & 31) << 6) + (this.readByte() & 63));
      }
   }

   public void close() throws IOException {
   }

   public int read(char[] var1, int var2, int var3) throws IOException {
      if (var3 == 0) {
         return 0;
      } else {
         int var4 = 0;

         try {
            while(var4 < var3) {
               var1[var2 + var4] = this.readChar();
               ++var4;
            }

            return var3;
         } catch (EOFException var6) {
            return var4 == 0 ? -1 : var4;
         }
      }
   }

   static class FastEOFException extends EOFException {
      private static final long serialVersionUID = 1L;

      public synchronized Throwable fillInStackTrace() {
         return null;
      }
   }
}
