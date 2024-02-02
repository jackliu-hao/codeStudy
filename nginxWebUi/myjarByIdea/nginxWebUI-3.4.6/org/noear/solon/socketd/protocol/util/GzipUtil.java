package org.noear.solon.socketd.protocol.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class GzipUtil {
   public static ByteArrayOutputStream compressDo(byte[] bytes) throws IOException {
      if (bytes != null && bytes.length != 0) {
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         GZIPOutputStream gzip = new GZIPOutputStream(out);
         Throwable var3 = null;

         try {
            gzip.write(bytes);
         } catch (Throwable var12) {
            var3 = var12;
            throw var12;
         } finally {
            if (gzip != null) {
               if (var3 != null) {
                  try {
                     gzip.close();
                  } catch (Throwable var11) {
                     var3.addSuppressed(var11);
                  }
               } else {
                  gzip.close();
               }
            }

         }

         return out;
      } else {
         return null;
      }
   }

   public static byte[] compress(byte[] bytes) throws IOException {
      return compressDo(bytes).toByteArray();
   }

   public static ByteArrayOutputStream uncompressDo(byte[] bytes) throws IOException {
      if (bytes != null && bytes.length != 0) {
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         ByteArrayInputStream in = new ByteArrayInputStream(bytes);
         GZIPInputStream ungzip = new GZIPInputStream(in);
         byte[] buffer = new byte[256];

         int n;
         while((n = ungzip.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
         }

         return out;
      } else {
         return null;
      }
   }

   public static byte[] uncompress(byte[] bytes) throws IOException {
      if (bytes == null) {
         return null;
      } else {
         ByteArrayOutputStream tmp = uncompressDo(bytes);
         return tmp == null ? null : tmp.toByteArray();
      }
   }
}
