package org.xnio.conduits;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;
import org.xnio.Buffers;

public final class Conduits {
   private static final FileChannel NULL_FILE_CHANNEL = (FileChannel)AccessController.doPrivileged(new PrivilegedAction<FileChannel>() {
      public FileChannel run() {
         String osName = System.getProperty("os.name", "unknown").toLowerCase(Locale.US);

         try {
            return osName.contains("windows") ? (new FileOutputStream("NUL:")).getChannel() : (new FileOutputStream("/dev/null")).getChannel();
         } catch (FileNotFoundException var3) {
            throw new IOError(var3);
         }
      }
   });
   private static final ByteBuffer DRAIN_BUFFER = ByteBuffer.allocateDirect(16384);

   public static long transfer(StreamSourceConduit source, long count, ByteBuffer throughBuffer, WritableByteChannel sink) throws IOException {
      long total = 0L;
      throughBuffer.limit(0);

      while(total < count) {
         throughBuffer.compact();

         long res;
         try {
            if (count - total < (long)throughBuffer.remaining()) {
               throughBuffer.limit((int)(count - total));
            }

            res = (long)source.read(throughBuffer);
            if (res <= 0L) {
               long var9 = total == 0L ? res : total;
               return var9;
            }
         } finally {
            throughBuffer.flip();
         }

         res = (long)sink.write(throughBuffer);
         if (res == 0L) {
            return total;
         }

         total += res;
      }

      return total;
   }

   public static long transfer(ReadableByteChannel source, long count, ByteBuffer throughBuffer, StreamSinkConduit sink) throws IOException {
      long total = 0L;
      throughBuffer.limit(0);

      while(total < count) {
         throughBuffer.compact();

         long res;
         label76: {
            long var9;
            try {
               if (count - total < (long)throughBuffer.remaining()) {
                  throughBuffer.limit((int)(count - total));
               }

               res = (long)source.read(throughBuffer);
               if (res > 0L) {
                  break label76;
               }

               var9 = total == 0L ? res : total;
            } finally {
               throughBuffer.flip();
            }

            return var9;
         }

         res = (long)sink.write(throughBuffer);
         if (res == 0L) {
            return total;
         }

         total += res;
      }

      return total;
   }

   public static int writeFinalBasic(StreamSinkConduit conduit, ByteBuffer src) throws IOException {
      int res = conduit.write(src);
      if (!src.hasRemaining()) {
         conduit.terminateWrites();
      }

      return res;
   }

   public static long writeFinalBasic(StreamSinkConduit conduit, ByteBuffer[] srcs, int offset, int length) throws IOException {
      long res = conduit.write(srcs, offset, length);
      if (!Buffers.hasRemaining(srcs, offset, length)) {
         conduit.terminateWrites();
      }

      return res;
   }

   public static boolean sendFinalBasic(MessageSinkConduit conduit, ByteBuffer src) throws IOException {
      if (conduit.send(src)) {
         conduit.terminateWrites();
         return true;
      } else {
         return false;
      }
   }

   public static boolean sendFinalBasic(MessageSinkConduit conduit, ByteBuffer[] srcs, int offset, int length) throws IOException {
      if (conduit.send(srcs, offset, length)) {
         conduit.terminateWrites();
         return true;
      } else {
         return false;
      }
   }

   public static long drain(StreamSourceConduit conduit, long count) throws IOException {
      long total = 0L;
      ByteBuffer buffer = null;

      while(count != 0L) {
         if (NULL_FILE_CHANNEL != null) {
            long lres;
            while(count > 0L && (lres = conduit.transferTo(0L, count, NULL_FILE_CHANNEL)) != 0L) {
               total += lres;
               count -= lres;
            }

            if (total > 0L) {
               return total;
            }
         }

         if (buffer == null) {
            buffer = DRAIN_BUFFER.duplicate();
         }

         if ((long)buffer.limit() > count) {
            buffer.limit((int)count);
         }

         int ires = conduit.read(buffer);
         buffer.clear();
         switch (ires) {
            case -1:
               return total == 0L ? -1L : total;
            case 0:
               return total;
            default:
               total += (long)ires;
         }
      }

      return total;
   }
}
