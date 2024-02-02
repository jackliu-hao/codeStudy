package org.xnio.channels;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.channels.WritableByteChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import org.xnio.Buffers;
import org.xnio.ChannelListener;
import org.xnio.Option;
import org.xnio.XnioIoThread;

public final class Channels {
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

   private Channels() {
   }

   public static void flushBlocking(SuspendableWriteChannel channel) throws IOException {
      while(!channel.flush()) {
         channel.awaitWritable();
      }

   }

   public static boolean flushBlocking(SuspendableWriteChannel channel, long time, TimeUnit unit) throws IOException {
      if (channel.flush()) {
         return true;
      } else {
         long remaining = unit.toNanos(time);
         long now = System.nanoTime();

         do {
            channel.awaitWritable(remaining, TimeUnit.NANOSECONDS);
            if (channel.flush()) {
               return true;
            }
         } while((remaining -= Math.max(-now + (now = System.nanoTime()), 0L)) > 0L);

         return false;
      }
   }

   public static void shutdownWritesBlocking(SuspendableWriteChannel channel) throws IOException {
      channel.shutdownWrites();
      flushBlocking(channel);
   }

   public static boolean shutdownWritesBlocking(SuspendableWriteChannel channel, long time, TimeUnit unit) throws IOException {
      channel.shutdownWrites();
      return flushBlocking(channel, time, unit);
   }

   public static <C extends WritableByteChannel & SuspendableWriteChannel> int writeBlocking(C channel, ByteBuffer buffer) throws IOException {
      int t = 0;

      while(buffer.hasRemaining()) {
         int res = channel.write(buffer);
         if (res == 0) {
            ((SuspendableWriteChannel)channel).awaitWritable();
         } else {
            t += res;
         }
      }

      return t;
   }

   public static <C extends WritableByteChannel & SuspendableWriteChannel> int writeBlocking(C channel, ByteBuffer buffer, long time, TimeUnit unit) throws IOException {
      long remaining = unit.toNanos(time);
      long now = System.nanoTime();
      int t = 0;

      while(buffer.hasRemaining() && remaining > 0L) {
         int res = channel.write(buffer);
         if (res == 0) {
            ((SuspendableWriteChannel)channel).awaitWritable(remaining, TimeUnit.NANOSECONDS);
            remaining -= Math.max(-now + (now = System.nanoTime()), 0L);
         } else {
            t += res;
         }
      }

      return t;
   }

   public static <C extends GatheringByteChannel & SuspendableWriteChannel> long writeBlocking(C channel, ByteBuffer[] buffers, int offs, int len) throws IOException {
      long t = 0L;

      while(Buffers.hasRemaining(buffers, offs, len)) {
         long res = channel.write(buffers, offs, len);
         if (res == 0L) {
            ((SuspendableWriteChannel)channel).awaitWritable();
         } else {
            t += res;
         }
      }

      return t;
   }

   public static <C extends GatheringByteChannel & SuspendableWriteChannel> long writeBlocking(C channel, ByteBuffer[] buffers, int offs, int len, long time, TimeUnit unit) throws IOException {
      long remaining = unit.toNanos(time);
      long now = System.nanoTime();
      long t = 0L;

      while(Buffers.hasRemaining(buffers, offs, len) && remaining > 0L) {
         long res = channel.write(buffers, offs, len);
         if (res == 0L) {
            ((SuspendableWriteChannel)channel).awaitWritable(remaining, TimeUnit.NANOSECONDS);
            remaining -= Math.max(-now + (now = System.nanoTime()), 0L);
         } else {
            t += res;
         }
      }

      return t;
   }

   public static <C extends WritableMessageChannel> void sendBlocking(C channel, ByteBuffer buffer) throws IOException {
      while(!channel.send(buffer)) {
         channel.awaitWritable();
      }

   }

   public static <C extends WritableMessageChannel> boolean sendBlocking(C channel, ByteBuffer buffer, long time, TimeUnit unit) throws IOException {
      long remaining = unit.toNanos(time);

      for(long now = System.nanoTime(); remaining > 0L; remaining -= Math.max(-now + (now = System.nanoTime()), 0L)) {
         if (channel.send(buffer)) {
            return true;
         }

         channel.awaitWritable(remaining, TimeUnit.NANOSECONDS);
      }

      return false;
   }

   public static <C extends WritableMessageChannel> void sendBlocking(C channel, ByteBuffer[] buffers, int offs, int len) throws IOException {
      while(!channel.send(buffers, offs, len)) {
         channel.awaitWritable();
      }

   }

   public static <C extends WritableMessageChannel> boolean sendBlocking(C channel, ByteBuffer[] buffers, int offs, int len, long time, TimeUnit unit) throws IOException {
      long remaining = unit.toNanos(time);

      for(long now = System.nanoTime(); remaining > 0L; remaining -= Math.max(-now + (now = System.nanoTime()), 0L)) {
         if (channel.send(buffers, offs, len)) {
            return true;
         }

         channel.awaitWritable(remaining, TimeUnit.NANOSECONDS);
      }

      return false;
   }

   public static <C extends ReadableByteChannel & SuspendableReadChannel> int readBlocking(C channel, ByteBuffer buffer) throws IOException {
      int res;
      while((res = channel.read(buffer)) == 0 && buffer.hasRemaining()) {
         ((SuspendableReadChannel)channel).awaitReadable();
      }

      return res;
   }

   public static <C extends ReadableByteChannel & SuspendableReadChannel> int readBlocking(C channel, ByteBuffer buffer, long time, TimeUnit unit) throws IOException {
      int res = channel.read(buffer);
      if (res != 0) {
         return res;
      } else {
         long remaining = unit.toNanos(time);

         for(long now = System.nanoTime(); buffer.hasRemaining() && remaining > 0L; remaining -= Math.max(-now + (now = System.nanoTime()), 0L)) {
            ((SuspendableReadChannel)channel).awaitReadable(remaining, TimeUnit.NANOSECONDS);
            res = channel.read(buffer);
            if (res != 0) {
               return res;
            }
         }

         return res;
      }
   }

   public static <C extends ScatteringByteChannel & SuspendableReadChannel> long readBlocking(C channel, ByteBuffer[] buffers, int offs, int len) throws IOException {
      long res;
      while((res = channel.read(buffers, offs, len)) == 0L) {
         ((SuspendableReadChannel)channel).awaitReadable();
      }

      return res;
   }

   public static <C extends ScatteringByteChannel & SuspendableReadChannel> long readBlocking(C channel, ByteBuffer[] buffers, int offs, int len, long time, TimeUnit unit) throws IOException {
      long res = channel.read(buffers, offs, len);
      if (res != 0L) {
         return res;
      } else {
         long remaining = unit.toNanos(time);

         for(long now = System.nanoTime(); Buffers.hasRemaining(buffers, offs, len) && remaining > 0L; remaining -= Math.max(-now + (now = System.nanoTime()), 0L)) {
            ((SuspendableReadChannel)channel).awaitReadable(remaining, TimeUnit.NANOSECONDS);
            res = channel.read(buffers, offs, len);
            if (res != 0L) {
               return res;
            }
         }

         return res;
      }
   }

   public static <C extends ReadableMessageChannel> int receiveBlocking(C channel, ByteBuffer buffer) throws IOException {
      int res;
      while((res = channel.receive(buffer)) == 0) {
         channel.awaitReadable();
      }

      return res;
   }

   public static <C extends ReadableMessageChannel> int receiveBlocking(C channel, ByteBuffer buffer, long time, TimeUnit unit) throws IOException {
      int res = channel.receive(buffer);
      if (res != 0) {
         return res;
      } else {
         long remaining = unit.toNanos(time);

         for(long now = System.nanoTime(); buffer.hasRemaining() && remaining > 0L; remaining -= Math.max(-now + (now = System.nanoTime()), 0L)) {
            channel.awaitReadable(remaining, TimeUnit.NANOSECONDS);
            res = channel.receive(buffer);
            if (res != 0) {
               return res;
            }
         }

         return res;
      }
   }

   public static <C extends ReadableMessageChannel> long receiveBlocking(C channel, ByteBuffer[] buffers, int offs, int len) throws IOException {
      long res;
      while((res = channel.receive(buffers, offs, len)) == 0L) {
         channel.awaitReadable();
      }

      return res;
   }

   public static <C extends ReadableMessageChannel> long receiveBlocking(C channel, ByteBuffer[] buffers, int offs, int len, long time, TimeUnit unit) throws IOException {
      long res = channel.receive(buffers, offs, len);
      if (res != 0L) {
         return res;
      } else {
         long remaining = unit.toNanos(time);

         for(long now = System.nanoTime(); Buffers.hasRemaining(buffers, offs, len) && remaining > 0L; remaining -= Math.max(-now + (now = System.nanoTime()), 0L)) {
            channel.awaitReadable(remaining, TimeUnit.NANOSECONDS);
            res = channel.receive(buffers, offs, len);
            if (res != 0L) {
               return res;
            }
         }

         return res;
      }
   }

   public static <C extends ConnectedChannel, A extends AcceptingChannel<C>> C acceptBlocking(A channel) throws IOException {
      ConnectedChannel accepted;
      while((accepted = channel.accept()) == null) {
         channel.awaitAcceptable();
      }

      return accepted;
   }

   public static <C extends ConnectedChannel, A extends AcceptingChannel<C>> C acceptBlocking(A channel, long time, TimeUnit unit) throws IOException {
      C accepted = channel.accept();
      if (accepted == null) {
         channel.awaitAcceptable(time, unit);
         return channel.accept();
      } else {
         return accepted;
      }
   }

   public static void transferBlocking(StreamSinkChannel destination, FileChannel source, long startPosition, long count) throws IOException {
      long res;
      for(long remaining = count; remaining > 0L; startPosition += res) {
         while((res = destination.transferFrom(source, startPosition, remaining)) == 0L) {
            try {
               destination.awaitWritable();
            } catch (InterruptedIOException var13) {
               long bytes = count - remaining;
               if (bytes > 2147483647L) {
                  var13.bytesTransferred = -1;
               } else {
                  var13.bytesTransferred = (int)bytes;
               }
            }
         }

         remaining -= res;
      }

   }

   public static void transferBlocking(FileChannel destination, StreamSourceChannel source, long startPosition, long count) throws IOException {
      long res;
      for(long remaining = count; remaining > 0L; startPosition += res) {
         while((res = source.transferTo(startPosition, remaining, destination)) == 0L) {
            try {
               source.awaitReadable();
            } catch (InterruptedIOException var13) {
               long bytes = count - remaining;
               if (bytes > 2147483647L) {
                  var13.bytesTransferred = -1;
               } else {
                  var13.bytesTransferred = (int)bytes;
               }
            }
         }

         remaining -= res;
      }

   }

   public static long transferBlocking(StreamSinkChannel destination, StreamSourceChannel source, ByteBuffer throughBuffer, long count) throws IOException {
      long t = 0L;

      long res;
      do {
         if (t >= count) {
            return t;
         }

         try {
            while((res = source.transferTo(count, throughBuffer, destination)) == 0L) {
               if (throughBuffer.hasRemaining()) {
                  writeBlocking(destination, throughBuffer);
               } else {
                  source.awaitReadable();
               }
            }

            t += res;
         } catch (InterruptedIOException var11) {
            int transferred = var11.bytesTransferred;
            t += (long)transferred;
            if (transferred >= 0 && t <= 2147483647L) {
               var11.bytesTransferred = (int)t;
            } else {
               var11.bytesTransferred = -1;
            }

            throw var11;
         }
      } while(res != -1L);

      return t == 0L ? -1L : t;
   }

   public static <T extends CloseableChannel> void setCloseListener(T channel, ChannelListener<? super T> listener) {
      ChannelListener.Setter<? extends T> setter = channel.getCloseSetter();
      setter.set(listener);
   }

   public static <T extends AcceptingChannel<?>> void setAcceptListener(T channel, ChannelListener<? super T> listener) {
      ChannelListener.Setter<? extends T> setter = channel.getAcceptSetter();
      setter.set(listener);
   }

   public static <T extends SuspendableReadChannel> void setReadListener(T channel, ChannelListener<? super T> listener) {
      ChannelListener.Setter<? extends T> setter = channel.getReadSetter();
      setter.set(listener);
   }

   public static <T extends SuspendableWriteChannel> void setWriteListener(T channel, ChannelListener<? super T> listener) {
      ChannelListener.Setter<? extends T> setter = channel.getWriteSetter();
      setter.set(listener);
   }

   public static ByteChannel wrapByteChannel(final ByteChannel original) {
      return new ByteChannel() {
         public int read(ByteBuffer dst) throws IOException {
            return original.read(dst);
         }

         public boolean isOpen() {
            return original.isOpen();
         }

         public void close() throws IOException {
            original.close();
         }

         public int write(ByteBuffer src) throws IOException {
            return original.write(src);
         }

         public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
            return original.write(srcs, offset, length);
         }

         public long write(ByteBuffer[] srcs) throws IOException {
            return original.write(srcs);
         }

         public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
            return original.read(dsts, offset, length);
         }

         public long read(ByteBuffer[] dsts) throws IOException {
            return original.read(dsts);
         }
      };
   }

   public static <T> T getOption(Configurable configurable, Option<T> option, T defaultValue) {
      try {
         T value = configurable.getOption(option);
         return value == null ? defaultValue : value;
      } catch (IOException var4) {
         return defaultValue;
      }
   }

   public static boolean getOption(Configurable configurable, Option<Boolean> option, boolean defaultValue) {
      try {
         Boolean value = (Boolean)configurable.getOption(option);
         return value == null ? defaultValue : value;
      } catch (IOException var4) {
         return defaultValue;
      }
   }

   public static int getOption(Configurable configurable, Option<Integer> option, int defaultValue) {
      try {
         Integer value = (Integer)configurable.getOption(option);
         return value == null ? defaultValue : value;
      } catch (IOException var4) {
         return defaultValue;
      }
   }

   public static long getOption(Configurable configurable, Option<Long> option, long defaultValue) {
      try {
         Long value = (Long)configurable.getOption(option);
         return value == null ? defaultValue : value;
      } catch (IOException var5) {
         return defaultValue;
      }
   }

   public static <T extends Channel> T unwrap(Class<T> targetType, Channel channel) {
      while(channel != null) {
         if (targetType.isInstance(channel)) {
            return (Channel)targetType.cast(channel);
         }

         if (!(channel instanceof WrappedChannel)) {
            return null;
         }

         channel = ((WrappedChannel)channel).getChannel();
      }

      return null;
   }

   public static long drain(StreamSourceChannel channel, long count) throws IOException {
      long total = 0L;
      ByteBuffer buffer = null;

      while(count != 0L) {
         if (NULL_FILE_CHANNEL != null) {
            long lres;
            while(count > 0L && (lres = channel.transferTo(0L, count, NULL_FILE_CHANNEL)) != 0L) {
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

         int ires = channel.read(buffer);
         buffer.clear();
         switch (ires) {
            case -1:
               return total == 0L ? -1L : total;
            case 0:
               return total;
            default:
               total += (long)ires;
               count -= (long)ires;
         }
      }

      return total;
   }

   public static long drain(ReadableByteChannel channel, long count) throws IOException {
      if (channel instanceof StreamSourceChannel) {
         return drain((StreamSourceChannel)channel, count);
      } else {
         long total = 0L;
         ByteBuffer buffer = null;

         while(count != 0L) {
            if (NULL_FILE_CHANNEL != null) {
               long lres;
               while(count > 0L && (lres = NULL_FILE_CHANNEL.transferFrom(channel, 0L, count)) != 0L) {
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

            int ires = channel.read(buffer);
            buffer.clear();
            switch (ires) {
               case -1:
                  return total == 0L ? -1L : total;
               case 0:
                  return total;
               default:
                  total += (long)ires;
                  count -= (long)ires;
            }
         }

         return total;
      }
   }

   public static long drain(FileChannel channel, long position, long count) throws IOException {
      if (channel instanceof StreamSourceChannel) {
         return drain((StreamSourceChannel)channel, count);
      } else {
         long total = 0L;
         ByteBuffer buffer = null;

         while(count != 0L) {
            if (NULL_FILE_CHANNEL != null) {
               long lres;
               while(count > 0L && (lres = channel.transferTo(position, count, NULL_FILE_CHANNEL)) != 0L) {
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

            int ires = channel.read(buffer);
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

   public static void resumeReadsAsync(final SuspendableReadChannel channel) {
      XnioIoThread ioThread = channel.getIoThread();
      if (ioThread == Thread.currentThread()) {
         channel.resumeReads();
      } else {
         ioThread.execute(new Runnable() {
            public void run() {
               channel.resumeReads();
            }
         });
      }

   }

   public static void resumeWritesAsync(final SuspendableWriteChannel channel) {
      XnioIoThread ioThread = channel.getIoThread();
      if (ioThread == Thread.currentThread()) {
         channel.resumeWrites();
      } else {
         ioThread.execute(new Runnable() {
            public void run() {
               channel.resumeWrites();
            }
         });
      }

   }

   public static int writeFinalBasic(StreamSinkChannel channel, ByteBuffer src) throws IOException {
      int res = channel.write(src);
      if (!src.hasRemaining()) {
         channel.shutdownWrites();
      }

      return res;
   }

   public static long writeFinalBasic(StreamSinkChannel channel, ByteBuffer[] srcs, int offset, int length) throws IOException {
      long res = channel.write(srcs, offset, length);
      if (!Buffers.hasRemaining(srcs, offset, length)) {
         channel.shutdownWrites();
      }

      return res;
   }
}
