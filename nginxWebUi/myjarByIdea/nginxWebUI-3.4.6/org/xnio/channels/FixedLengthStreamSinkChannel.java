package org.xnio.channels;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;
import org.xnio.Bits;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.IoUtils;
import org.xnio.Option;
import org.xnio.XnioExecutor;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio._private.Messages;

public final class FixedLengthStreamSinkChannel implements StreamSinkChannel, ProtectedWrappedChannel<StreamSinkChannel>, WriteListenerSettable<FixedLengthStreamSinkChannel>, CloseListenerSettable<FixedLengthStreamSinkChannel> {
   private final StreamSinkChannel delegate;
   private final Object guard;
   private final ChannelListener<? super FixedLengthStreamSinkChannel> finishListener;
   private ChannelListener<? super FixedLengthStreamSinkChannel> writeListener;
   private ChannelListener<? super FixedLengthStreamSinkChannel> closeListener;
   private int state;
   private long count;
   private static final int FLAG_CLOSE_REQUESTED = 1;
   private static final int FLAG_CLOSE_COMPLETE = 2;
   private static final int FLAG_CONFIGURABLE = 4;
   private static final int FLAG_PASS_CLOSE = 8;

   public FixedLengthStreamSinkChannel(StreamSinkChannel delegate, long contentLength, boolean configurable, boolean propagateClose, ChannelListener<? super FixedLengthStreamSinkChannel> finishListener, Object guard) {
      if (contentLength < 0L) {
         throw Messages.msg.parameterOutOfRange("contentLength");
      } else if (delegate == null) {
         throw Messages.msg.nullParameter("delegate");
      } else {
         this.guard = guard;
         this.delegate = delegate;
         this.finishListener = finishListener;
         this.state = (configurable ? 4 : 0) | (propagateClose ? 8 : 0);
         this.count = contentLength;
         delegate.getWriteSetter().set(new ChannelListener<StreamSinkChannel>() {
            public void handleEvent(StreamSinkChannel channel) {
               ChannelListeners.invokeChannelListener(FixedLengthStreamSinkChannel.this, FixedLengthStreamSinkChannel.this.writeListener);
            }
         });
      }
   }

   public void setWriteListener(ChannelListener<? super FixedLengthStreamSinkChannel> listener) {
      this.writeListener = listener;
   }

   public ChannelListener<? super FixedLengthStreamSinkChannel> getWriteListener() {
      return this.writeListener;
   }

   public void setCloseListener(ChannelListener<? super FixedLengthStreamSinkChannel> listener) {
      this.closeListener = listener;
   }

   public ChannelListener<? super FixedLengthStreamSinkChannel> getCloseListener() {
      return this.closeListener;
   }

   public ChannelListener.Setter<FixedLengthStreamSinkChannel> getWriteSetter() {
      return new WriteListenerSettable.Setter(this);
   }

   public ChannelListener.Setter<FixedLengthStreamSinkChannel> getCloseSetter() {
      return new CloseListenerSettable.Setter(this);
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      return this.write(src, true);
   }

   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
      return this.write(srcs, offset, length, true);
   }

   public long writeFinal(ByteBuffer[] srcs) throws IOException {
      return this.write(srcs, 0, srcs.length, true);
   }

   public StreamSinkChannel getChannel(Object guard) {
      Object ourGuard = this.guard;
      return ourGuard != null && guard != ourGuard ? null : this.delegate;
   }

   /** @deprecated */
   @Deprecated
   public XnioExecutor getWriteThread() {
      return this.delegate.getWriteThread();
   }

   public XnioIoThread getIoThread() {
      return this.delegate.getIoThread();
   }

   public XnioWorker getWorker() {
      return this.delegate.getWorker();
   }

   public int write(ByteBuffer src) throws IOException {
      return this.write(src, false);
   }

   private int write(ByteBuffer src, boolean finalWrite) throws IOException {
      if (Bits.allAreSet(this.state, 1)) {
         throw new ClosedChannelException();
      } else if (!src.hasRemaining()) {
         return 0;
      } else {
         int res = 0;
         long remaining = this.count;
         if (remaining == 0L) {
            throw Messages.msg.fixedOverflow();
         } else {
            int var8;
            try {
               int lim = src.limit();
               int pos = src.position();
               if ((long)(lim - pos) > remaining) {
                  src.limit((int)(remaining - (long)pos));

                  try {
                     var8 = res = this.doWrite(src, finalWrite);
                     return var8;
                  } finally {
                     src.limit(lim);
                  }
               }

               var8 = res = this.doWrite(src, finalWrite);
            } finally {
               this.count = remaining - (long)res;
            }

            return var8;
         }
      }
   }

   public long write(ByteBuffer[] srcs) throws IOException {
      return this.write(srcs, 0, srcs.length);
   }

   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
      return this.write(srcs, offset, length, false);
   }

   private long write(ByteBuffer[] srcs, int offset, int length, boolean writeFinal) throws IOException {
      if (Bits.allAreSet(this.state, 1)) {
         throw new ClosedChannelException();
      } else if (length == 0) {
         return 0L;
      } else if (length == 1) {
         return (long)this.write(srcs[offset]);
      } else {
         long remaining = this.count;
         if (remaining == 0L) {
            throw Messages.msg.fixedOverflow();
         } else {
            long res = 0L;

            try {
               long t = 0L;

               for(int i = 0; i < length; ++i) {
                  ByteBuffer buffer = srcs[i + offset];
                  int lim;
                  t += (long)((lim = buffer.limit()) - buffer.position());
                  if (t > remaining) {
                     buffer.limit(lim - (int)(t - remaining));

                     try {
                        long var14 = res = this.doWrite(srcs, offset, i + 1, writeFinal);
                        return var14;
                     } finally {
                        buffer.limit(lim);
                     }
                  }
               }

               long var24;
               if (t == 0L) {
                  var24 = 0L;
                  return var24;
               } else {
                  var24 = res = this.doWrite(srcs, offset, length, writeFinal);
                  return var24;
               }
            } finally {
               this.count = remaining - res;
            }
         }
      }
   }

   private long doWrite(ByteBuffer[] srcs, int offset, int length, boolean writeFinal) throws IOException {
      return writeFinal ? this.delegate.writeFinal(srcs, offset, length) : this.delegate.write(srcs, offset, length);
   }

   private int doWrite(ByteBuffer src, boolean finalWrite) throws IOException {
      return finalWrite ? this.delegate.writeFinal(src) : this.delegate.write(src);
   }

   public long transferFrom(FileChannel src, long position, long count) throws IOException {
      if (Bits.allAreSet(this.state, 1)) {
         throw new ClosedChannelException();
      } else if (count == 0L) {
         return 0L;
      } else {
         long remaining = this.count;
         if (remaining == 0L) {
            throw Messages.msg.fixedOverflow();
         } else {
            long res = 0L;

            long var10;
            try {
               var10 = res = this.delegate.transferFrom(src, position, Math.min(count, remaining));
            } finally {
               this.count = remaining - res;
            }

            return var10;
         }
      }
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      if (Bits.allAreSet(this.state, 1)) {
         throw new ClosedChannelException();
      } else if (count == 0L) {
         return 0L;
      } else {
         long remaining = this.count;
         if (remaining == 0L) {
            throw Messages.msg.fixedOverflow();
         } else {
            long res = 0L;

            long var9;
            try {
               var9 = res = this.delegate.transferFrom(source, Math.min(count, remaining), throughBuffer);
            } finally {
               this.count = remaining - res;
            }

            return var9;
         }
      }
   }

   public boolean flush() throws IOException {
      int state = this.state;
      if (Bits.anyAreSet(state, 2)) {
         return true;
      } else {
         boolean flushed = false;

         boolean var3;
         try {
            var3 = flushed = this.delegate.flush();
         } finally {
            if (flushed && Bits.allAreSet(state, 1)) {
               this.state = state | 2;
               this.callFinish();
               this.callClosed();
               if (this.count != 0L) {
                  throw Messages.msg.fixedUnderflow(this.count);
               }
            }

         }

         return var3;
      }
   }

   public void suspendWrites() {
      if (Bits.allAreClear(this.state, 2)) {
         this.delegate.suspendWrites();
      }

   }

   public void resumeWrites() {
      if (Bits.allAreClear(this.state, 2)) {
         this.delegate.resumeWrites();
      }

   }

   public boolean isWriteResumed() {
      return Bits.allAreClear(this.state, 2) && this.delegate.isWriteResumed();
   }

   public void wakeupWrites() {
      if (Bits.allAreClear(this.state, 2)) {
         this.delegate.wakeupWrites();
      }

   }

   public void shutdownWrites() throws IOException {
      int state = this.state;
      if (!Bits.allAreSet(state, 1)) {
         this.state = state | 1;
         if (Bits.allAreSet(state, 8)) {
            this.delegate.shutdownWrites();
         }

      }
   }

   public void awaitWritable() throws IOException {
      this.delegate.awaitWritable();
   }

   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
      this.delegate.awaitWritable(time, timeUnit);
   }

   public boolean isOpen() {
      return Bits.allAreClear(this.state, 1);
   }

   public void close() throws IOException {
      int state = this.state;
      if (!Bits.allAreSet(state, 2)) {
         this.state = state | 1 | 2;

         try {
            long count = this.count;
            if (count != 0L) {
               if (Bits.allAreSet(state, 8)) {
                  IoUtils.safeClose((Closeable)this.delegate);
               }

               throw Messages.msg.fixedUnderflow(count);
            }

            if (Bits.allAreSet(state, 8)) {
               this.delegate.close();
            }
         } finally {
            this.callClosed();
            this.callFinish();
         }

      }
   }

   public boolean supportsOption(Option<?> option) {
      return Bits.allAreSet(this.state, 4) && this.delegate.supportsOption(option);
   }

   public <T> T getOption(Option<T> option) throws IOException {
      return Bits.allAreSet(this.state, 4) ? this.delegate.getOption(option) : null;
   }

   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
      return Bits.allAreSet(this.state, 4) ? this.delegate.setOption(option, value) : null;
   }

   public long getRemaining() {
      return this.count;
   }

   private void callFinish() {
      ChannelListeners.invokeChannelListener(this, this.finishListener);
   }

   private void callClosed() {
      ChannelListeners.invokeChannelListener(this, this.closeListener);
   }
}
