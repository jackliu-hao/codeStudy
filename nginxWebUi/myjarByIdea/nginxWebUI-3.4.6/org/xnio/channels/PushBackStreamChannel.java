package org.xnio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;
import org.xnio.Buffers;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.Option;
import org.xnio.Pooled;
import org.xnio.XnioExecutor;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;

public final class PushBackStreamChannel implements StreamSourceChannel, WrappedChannel<StreamSourceChannel> {
   private final StreamSourceChannel firstChannel;
   private StreamSourceChannel channel;
   private ChannelListener<? super PushBackStreamChannel> readListener;
   private ChannelListener<? super PushBackStreamChannel> closeListener;

   public PushBackStreamChannel(StreamSourceChannel channel) {
      this.channel = this.firstChannel = channel;
      this.firstChannel.getReadSetter().set(new ChannelListener<StreamSourceChannel>() {
         public void handleEvent(StreamSourceChannel channel) {
            ChannelListeners.invokeChannelListener(PushBackStreamChannel.this, PushBackStreamChannel.this.readListener);
         }
      });
      this.firstChannel.getCloseSetter().set(new ChannelListener<StreamSourceChannel>() {
         public void handleEvent(StreamSourceChannel channel) {
            ChannelListeners.invokeChannelListener(PushBackStreamChannel.this, PushBackStreamChannel.this.closeListener);
         }
      });
   }

   public void setReadListener(ChannelListener<? super PushBackStreamChannel> readListener) {
      this.readListener = readListener;
   }

   public void setCloseListener(ChannelListener<? super PushBackStreamChannel> closeListener) {
      this.closeListener = closeListener;
   }

   public ChannelListener.Setter<? extends PushBackStreamChannel> getReadSetter() {
      return new ChannelListener.Setter<PushBackStreamChannel>() {
         public void set(ChannelListener<? super PushBackStreamChannel> listener) {
            PushBackStreamChannel.this.setReadListener(listener);
         }
      };
   }

   public ChannelListener.Setter<? extends PushBackStreamChannel> getCloseSetter() {
      return new ChannelListener.Setter<PushBackStreamChannel>() {
         public void set(ChannelListener<? super PushBackStreamChannel> listener) {
            PushBackStreamChannel.this.setCloseListener(listener);
         }
      };
   }

   public long transferTo(long position, long count, FileChannel target) throws IOException {
      StreamSourceChannel channel = this.channel;
      return channel == null ? 0L : channel.transferTo(position, count, target);
   }

   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
      StreamSourceChannel channel = this.channel;
      return channel == null ? -1L : channel.transferTo(count, throughBuffer, target);
   }

   public int read(ByteBuffer dst) throws IOException {
      StreamSourceChannel channel = this.channel;
      return channel == null ? -1 : channel.read(dst);
   }

   public long read(ByteBuffer[] dsts) throws IOException {
      StreamSourceChannel channel = this.channel;
      return channel == null ? -1L : channel.read(dsts);
   }

   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
      StreamSourceChannel channel = this.channel;
      return channel == null ? -1L : channel.read(dsts, offset, length);
   }

   public void unget(Pooled<ByteBuffer> buffer) {
      StreamSourceChannel old = this.channel;
      if (old == null) {
         buffer.free();
      } else {
         this.channel = new BufferHolder(old, buffer);
      }
   }

   public void suspendReads() {
      this.firstChannel.suspendReads();
   }

   public void resumeReads() {
      StreamSourceChannel channel = this.channel;
      if (channel != null) {
         channel.resumeReads();
      }

   }

   public boolean isReadResumed() {
      return this.firstChannel.isReadResumed();
   }

   public void wakeupReads() {
      this.firstChannel.wakeupReads();
   }

   public void shutdownReads() throws IOException {
      StreamSourceChannel old = this.channel;
      if (old != null) {
         this.channel = null;
         old.shutdownReads();
      }

   }

   public void awaitReadable() throws IOException {
      StreamSourceChannel channel = this.channel;
      if (channel != null) {
         channel.awaitReadable();
      }

   }

   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
      StreamSourceChannel channel = this.channel;
      if (channel != null) {
         channel.awaitReadable(time, timeUnit);
      }

   }

   /** @deprecated */
   @Deprecated
   public XnioExecutor getReadThread() {
      return this.firstChannel.getReadThread();
   }

   public XnioIoThread getIoThread() {
      return this.firstChannel.getIoThread();
   }

   public XnioWorker getWorker() {
      return this.firstChannel.getWorker();
   }

   public boolean isOpen() {
      return this.firstChannel.isOpen();
   }

   public void close() throws IOException {
      StreamSourceChannel old = this.channel;
      if (old != null) {
         this.channel = null;
         old.close();
      }

   }

   public boolean supportsOption(Option<?> option) {
      return this.firstChannel.supportsOption(option);
   }

   public <T> T getOption(Option<T> option) throws IOException {
      return this.firstChannel.getOption(option);
   }

   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
      return this.firstChannel.setOption(option, value);
   }

   public StreamSourceChannel getChannel() {
      return this.firstChannel;
   }

   class BufferHolder implements StreamSourceChannel {
      private final StreamSourceChannel next;
      private final Pooled<ByteBuffer> buffer;

      BufferHolder(StreamSourceChannel next, Pooled<ByteBuffer> buffer) {
         this.next = next;
         this.buffer = buffer;
      }

      public long transferTo(long position, long count, FileChannel target) throws IOException {
         long cnt;
         try {
            ByteBuffer src = (ByteBuffer)this.buffer.getResource();
            int pos = src.position();
            int rem = src.remaining();
            if ((long)rem > count) {
               long var11;
               try {
                  src.limit(pos + (int)count);
                  var11 = (long)target.write(src, position);
               } finally {
                  src.limit(pos + rem);
               }

               return var11;
            }

            cnt = (long)target.write(src, position);
            if (cnt != (long)rem) {
               return cnt;
            }

            this.moveToNext();
            position += cnt;
            count -= cnt;
         } catch (IllegalStateException var17) {
            this.moveToNext();
            cnt = 0L;
         }

         return cnt + this.next.transferTo(position, count, target);
      }

      public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
         throughBuffer.clear();

         long cnt;
         try {
            ByteBuffer src = (ByteBuffer)this.buffer.getResource();
            int pos = src.position();
            int rem = src.remaining();
            if ((long)rem > count) {
               long var10;
               try {
                  src.limit(pos + (int)count);
                  throughBuffer.limit(0);
                  var10 = (long)target.write(src);
               } finally {
                  src.limit(pos + rem);
               }

               return var10;
            }

            cnt = (long)target.write(src);
            if (cnt != (long)rem) {
               return cnt;
            }

            this.moveToNext();
         } catch (IllegalStateException var16) {
            this.moveToNext();
            cnt = 0L;
         }

         long res = this.next.transferTo(count - cnt, throughBuffer, target);
         return res > 0L ? cnt + res : (cnt > 0L ? cnt : res);
      }

      public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
         long cnt;
         try {
            ByteBuffer src = (ByteBuffer)this.buffer.getResource();
            cnt = (long)Buffers.copy(dsts, offset, length, src);
            if (src.hasRemaining()) {
               return cnt;
            }

            StreamSourceChannel next = PushBackStreamChannel.this.channel = this.next;
            this.buffer.free();
            if (cnt > 0L && next == PushBackStreamChannel.this.firstChannel) {
               return cnt;
            }
         } catch (IllegalStateException var8) {
            this.moveToNext();
            cnt = 0L;
         }

         long res = this.next.read(dsts, offset, length);
         return res > 0L ? res + cnt : (cnt > 0L ? cnt : res);
      }

      public long read(ByteBuffer[] dsts) throws IOException {
         return this.read(dsts, 0, dsts.length);
      }

      public int read(ByteBuffer dst) throws IOException {
         if (!dst.hasRemaining()) {
            return 0;
         } else {
            int cnt;
            try {
               ByteBuffer src = (ByteBuffer)this.buffer.getResource();
               cnt = Buffers.copy(dst, src);
               if (src.hasRemaining()) {
                  return cnt;
               }

               StreamSourceChannel next = this.moveToNext();
               if (cnt > 0 && next == PushBackStreamChannel.this.firstChannel) {
                  return cnt;
               }
            } catch (IllegalStateException var5) {
               this.moveToNext();
               cnt = 0;
            }

            int res = this.next.read(dst);
            return res > 0 ? res + cnt : (cnt > 0 ? cnt : res);
         }
      }

      public void close() throws IOException {
         this.buffer.free();
         this.next.close();
      }

      public void resumeReads() {
         PushBackStreamChannel.this.firstChannel.wakeupReads();
      }

      public void shutdownReads() throws IOException {
         this.buffer.free();
         this.next.shutdownReads();
      }

      public void awaitReadable() throws IOException {
      }

      public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
      }

      public boolean isOpen() {
         throw new UnsupportedOperationException();
      }

      public ChannelListener.Setter<? extends StreamSourceChannel> getReadSetter() {
         throw new UnsupportedOperationException();
      }

      public ChannelListener.Setter<? extends StreamSourceChannel> getCloseSetter() {
         throw new UnsupportedOperationException();
      }

      public void suspendReads() {
         throw new UnsupportedOperationException();
      }

      public boolean isReadResumed() {
         throw new UnsupportedOperationException();
      }

      public void wakeupReads() {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public XnioExecutor getReadThread() {
         throw new UnsupportedOperationException();
      }

      public XnioIoThread getIoThread() {
         throw new UnsupportedOperationException();
      }

      public XnioWorker getWorker() {
         throw new UnsupportedOperationException();
      }

      public boolean supportsOption(Option<?> option) {
         throw new UnsupportedOperationException();
      }

      public <T> T getOption(Option<T> option) throws IOException {
         throw new UnsupportedOperationException();
      }

      public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
         throw new UnsupportedOperationException();
      }

      private final StreamSourceChannel moveToNext() {
         this.buffer.free();
         return PushBackStreamChannel.this.channel = this.next;
      }
   }
}
