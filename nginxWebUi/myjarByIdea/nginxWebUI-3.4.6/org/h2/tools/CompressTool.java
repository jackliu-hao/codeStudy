package org.h2.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.h2.compress.CompressDeflate;
import org.h2.compress.CompressLZF;
import org.h2.compress.CompressNo;
import org.h2.compress.Compressor;
import org.h2.compress.LZFInputStream;
import org.h2.compress.LZFOutputStream;
import org.h2.message.DbException;
import org.h2.util.Bits;
import org.h2.util.StringUtils;
import org.h2.util.Utils;

public class CompressTool {
   private static final int MAX_BUFFER_SIZE = 393216;
   private byte[] buffer;

   private CompressTool() {
   }

   private byte[] getBuffer(int var1) {
      if (var1 > 393216) {
         return Utils.newBytes(var1);
      } else {
         if (this.buffer == null || this.buffer.length < var1) {
            this.buffer = Utils.newBytes(var1);
         }

         return this.buffer;
      }
   }

   public static CompressTool getInstance() {
      return new CompressTool();
   }

   public byte[] compress(byte[] var1, String var2) {
      int var3 = var1.length;
      if (var1.length < 5) {
         var2 = "NO";
      }

      Compressor var4 = getCompressor(var2);
      byte[] var5 = this.getBuffer((var3 < 100 ? var3 + 100 : var3) * 2);
      int var6 = compress(var1, var1.length, var4, var5);
      return Utils.copyBytes(var5, var6);
   }

   private static int compress(byte[] var0, int var1, Compressor var2, byte[] var3) {
      var3[0] = (byte)var2.getAlgorithm();
      int var4 = 1 + writeVariableInt(var3, 1, var1);
      int var5 = var2.compress(var0, 0, var1, var3, var4);
      if (var5 > var1 + var4 || var5 <= 0) {
         var3[0] = 0;
         System.arraycopy(var0, 0, var3, var4, var1);
         var5 = var1 + var4;
      }

      return var5;
   }

   public byte[] expand(byte[] var1) {
      if (var1.length == 0) {
         throw DbException.get(90104);
      } else {
         byte var2 = var1[0];
         Compressor var3 = getCompressor(var2);

         try {
            int var4 = readVariableInt(var1, 1);
            int var5 = 1 + getVariableIntLength(var4);
            byte[] var6 = Utils.newBytes(var4);
            var3.expand(var1, var5, var1.length - var5, var6, 0, var4);
            return var6;
         } catch (Exception var7) {
            throw DbException.get(90104, var7);
         }
      }
   }

   public static void expand(byte[] var0, byte[] var1, int var2) {
      byte var3 = var0[0];
      Compressor var4 = getCompressor(var3);

      try {
         int var5 = readVariableInt(var0, 1);
         int var6 = 1 + getVariableIntLength(var5);
         var4.expand(var0, var6, var0.length - var6, var1, var2, var5);
      } catch (Exception var7) {
         throw DbException.get(90104, var7);
      }
   }

   public static int readVariableInt(byte[] var0, int var1) {
      int var2 = var0[var1++] & 255;
      if (var2 < 128) {
         return var2;
      } else if (var2 < 192) {
         return ((var2 & 63) << 8) + (var0[var1] & 255);
      } else if (var2 < 224) {
         return ((var2 & 31) << 16) + ((var0[var1++] & 255) << 8) + (var0[var1] & 255);
      } else {
         return var2 < 240 ? ((var2 & 15) << 24) + ((var0[var1++] & 255) << 16) + ((var0[var1++] & 255) << 8) + (var0[var1] & 255) : Bits.readInt(var0, var1);
      }
   }

   public static int writeVariableInt(byte[] var0, int var1, int var2) {
      if (var2 < 0) {
         var0[var1++] = -16;
         Bits.writeInt(var0, var1, var2);
         return 5;
      } else if (var2 < 128) {
         var0[var1] = (byte)var2;
         return 1;
      } else if (var2 < 16384) {
         var0[var1++] = (byte)(128 | var2 >> 8);
         var0[var1] = (byte)var2;
         return 2;
      } else if (var2 < 2097152) {
         var0[var1++] = (byte)(192 | var2 >> 16);
         var0[var1++] = (byte)(var2 >> 8);
         var0[var1] = (byte)var2;
         return 3;
      } else if (var2 < 268435456) {
         Bits.writeInt(var0, var1, var2 | -536870912);
         return 4;
      } else {
         var0[var1++] = -16;
         Bits.writeInt(var0, var1, var2);
         return 5;
      }
   }

   public static int getVariableIntLength(int var0) {
      if (var0 < 0) {
         return 5;
      } else if (var0 < 128) {
         return 1;
      } else if (var0 < 16384) {
         return 2;
      } else if (var0 < 2097152) {
         return 3;
      } else {
         return var0 < 268435456 ? 4 : 5;
      }
   }

   private static Compressor getCompressor(String var0) {
      if (var0 == null) {
         var0 = "LZF";
      }

      int var1 = var0.indexOf(32);
      String var2 = null;
      if (var1 > 0) {
         var2 = var0.substring(var1 + 1);
         var0 = var0.substring(0, var1);
      }

      int var3 = getCompressAlgorithm(var0);
      Compressor var4 = getCompressor(var3);
      var4.setOptions(var2);
      return var4;
   }

   private static int getCompressAlgorithm(String var0) {
      var0 = StringUtils.toUpperEnglish(var0);
      if ("NO".equals(var0)) {
         return 0;
      } else if ("LZF".equals(var0)) {
         return 1;
      } else if ("DEFLATE".equals(var0)) {
         return 2;
      } else {
         throw DbException.get(90103, var0);
      }
   }

   private static Compressor getCompressor(int var0) {
      switch (var0) {
         case 0:
            return new CompressNo();
         case 1:
            return new CompressLZF();
         case 2:
            return new CompressDeflate();
         default:
            throw DbException.get(90103, Integer.toString(var0));
      }
   }

   public static OutputStream wrapOutputStream(OutputStream var0, String var1, String var2) {
      try {
         if ("GZIP".equals(var1)) {
            var0 = new GZIPOutputStream((OutputStream)var0);
         } else if ("ZIP".equals(var1)) {
            ZipOutputStream var3 = new ZipOutputStream((OutputStream)var0);
            var3.putNextEntry(new ZipEntry(var2));
            var0 = var3;
         } else if ("DEFLATE".equals(var1)) {
            var0 = new DeflaterOutputStream((OutputStream)var0);
         } else if ("LZF".equals(var1)) {
            var0 = new LZFOutputStream((OutputStream)var0);
         } else if (var1 != null) {
            throw DbException.get(90103, var1);
         }

         return (OutputStream)var0;
      } catch (IOException var4) {
         throw DbException.convertIOException(var4, (String)null);
      }
   }

   public static InputStream wrapInputStream(InputStream var0, String var1, String var2) {
      try {
         if ("GZIP".equals(var1)) {
            var0 = new GZIPInputStream((InputStream)var0);
         } else if ("ZIP".equals(var1)) {
            ZipInputStream var3 = new ZipInputStream((InputStream)var0);

            ZipEntry var4;
            do {
               var4 = var3.getNextEntry();
               if (var4 == null) {
                  return null;
               }
            } while(!var2.equals(var4.getName()));

            var0 = var3;
         } else if ("DEFLATE".equals(var1)) {
            var0 = new InflaterInputStream((InputStream)var0);
         } else if ("LZF".equals(var1)) {
            var0 = new LZFInputStream((InputStream)var0);
         } else if (var1 != null) {
            throw DbException.get(90103, var1);
         }

         return (InputStream)var0;
      } catch (IOException var5) {
         throw DbException.convertIOException(var5, (String)null);
      }
   }
}
