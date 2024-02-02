package org.apache.commons.compress.utils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;

public final class IOUtils {
   private static final int COPY_BUF_SIZE = 8024;
   private static final int SKIP_BUF_SIZE = 4096;
   public static final LinkOption[] EMPTY_LINK_OPTIONS = new LinkOption[0];
   private static final byte[] SKIP_BUF = new byte[4096];

   private IOUtils() {
   }

   public static long copy(InputStream input, OutputStream output) throws IOException {
      return copy(input, output, 8024);
   }

   public static long copy(InputStream input, OutputStream output, int buffersize) throws IOException {
      if (buffersize < 1) {
         throw new IllegalArgumentException("buffersize must be bigger than 0");
      } else {
         byte[] buffer = new byte[buffersize];
         int n = false;

         long count;
         int n;
         for(count = 0L; -1 != (n = input.read(buffer)); count += (long)n) {
            output.write(buffer, 0, n);
         }

         return count;
      }
   }

   public static long skip(InputStream input, long numToSkip) throws IOException {
      long available;
      long skipped;
      for(available = numToSkip; numToSkip > 0L; numToSkip -= skipped) {
         skipped = input.skip(numToSkip);
         if (skipped == 0L) {
            break;
         }
      }

      while(numToSkip > 0L) {
         int read = readFully(input, SKIP_BUF, 0, (int)Math.min(numToSkip, 4096L));
         if (read < 1) {
            break;
         }

         numToSkip -= (long)read;
      }

      return available - numToSkip;
   }

   public static int read(File file, byte[] array) throws IOException {
      InputStream inputStream = Files.newInputStream(file.toPath());
      Throwable var3 = null;

      int var4;
      try {
         var4 = readFully(inputStream, array, 0, array.length);
      } catch (Throwable var13) {
         var3 = var13;
         throw var13;
      } finally {
         if (inputStream != null) {
            if (var3 != null) {
               try {
                  inputStream.close();
               } catch (Throwable var12) {
                  var3.addSuppressed(var12);
               }
            } else {
               inputStream.close();
            }
         }

      }

      return var4;
   }

   public static int readFully(InputStream input, byte[] array) throws IOException {
      return readFully(input, array, 0, array.length);
   }

   public static int readFully(InputStream input, byte[] array, int offset, int len) throws IOException {
      if (len >= 0 && offset >= 0 && len + offset <= array.length && len + offset >= 0) {
         int count = 0;

         int x;
         for(int x = false; count != len; count += x) {
            x = input.read(array, offset + count, len - count);
            if (x == -1) {
               break;
            }
         }

         return count;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public static void readFully(ReadableByteChannel channel, ByteBuffer b) throws IOException {
      int expectedLength = b.remaining();

      int read;
      int readNow;
      for(read = 0; read < expectedLength; read += readNow) {
         readNow = channel.read(b);
         if (readNow <= 0) {
            break;
         }
      }

      if (read < expectedLength) {
         throw new EOFException();
      }
   }

   public static byte[] toByteArray(InputStream input) throws IOException {
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      copy((InputStream)input, output);
      return output.toByteArray();
   }

   public static void closeQuietly(Closeable c) {
      if (c != null) {
         try {
            c.close();
         } catch (IOException var2) {
         }
      }

   }

   public static void copy(File sourceFile, OutputStream outputStream) throws IOException {
      Files.copy(sourceFile.toPath(), outputStream);
   }

   public static long copyRange(InputStream input, long len, OutputStream output) throws IOException {
      return copyRange(input, len, output, 8024);
   }

   public static long copyRange(InputStream input, long len, OutputStream output, int buffersize) throws IOException {
      if (buffersize < 1) {
         throw new IllegalArgumentException("buffersize must be bigger than 0");
      } else {
         byte[] buffer = new byte[(int)Math.min((long)buffersize, len)];
         int n = false;

         long count;
         int n;
         for(count = 0L; count < len && -1 != (n = input.read(buffer, 0, (int)Math.min(len - count, (long)buffer.length))); count += (long)n) {
            output.write(buffer, 0, n);
         }

         return count;
      }
   }

   public static byte[] readRange(InputStream input, int len) throws IOException {
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      copyRange(input, (long)len, output);
      return output.toByteArray();
   }

   public static byte[] readRange(ReadableByteChannel input, int len) throws IOException {
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      ByteBuffer b = ByteBuffer.allocate(Math.min(len, 8024));

      int readNow;
      for(int read = 0; read < len; read += readNow) {
         readNow = input.read(b);
         if (readNow <= 0) {
            break;
         }

         output.write(b.array(), 0, readNow);
         b.rewind();
      }

      return output.toByteArray();
   }
}
