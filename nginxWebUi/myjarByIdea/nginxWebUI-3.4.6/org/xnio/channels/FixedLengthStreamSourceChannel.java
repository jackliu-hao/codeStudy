package org.xnio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;
import org.xnio.Bits;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.Option;
import org.xnio.XnioExecutor;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio._private.Messages;

public final class FixedLengthStreamSourceChannel implements StreamSourceChannel, ProtectedWrappedChannel<StreamSourceChannel>, ReadListenerSettable<FixedLengthStreamSourceChannel>, CloseListenerSettable<FixedLengthStreamSourceChannel> {
   private final StreamSourceChannel delegate;
   private final Object guard;
   private final ChannelListener<? super FixedLengthStreamSourceChannel> finishListener;
   private ChannelListener<? super FixedLengthStreamSourceChannel> readListener;
   private ChannelListener<? super FixedLengthStreamSourceChannel> closeListener;
   private int state;
   private long remaining;
   private static final int FLAG_CLOSED = 1;
   private static final int FLAG_FINISHED = 2;
   private static final int FLAG_CONFIGURABLE = 4;
   private static final int FLAG_PASS_CLOSE = 8;

   public FixedLengthStreamSourceChannel(StreamSourceChannel delegate, long contentLength, ChannelListener<? super FixedLengthStreamSourceChannel> finishListener, Object guard) {
      this(delegate, contentLength, false, finishListener, guard);
   }

   public FixedLengthStreamSourceChannel(StreamSourceChannel delegate, long contentLength, boolean configurable, ChannelListener<? super FixedLengthStreamSourceChannel> finishListener, Object guard) {
      this(delegate, contentLength, configurable, false, finishListener, guard);
   }

   public FixedLengthStreamSourceChannel(StreamSourceChannel delegate, long contentLength, boolean configurable, boolean propagateClose, ChannelListener<? super FixedLengthStreamSourceChannel> finishListener, Object guard) {
      this.guard = guard;
      this.finishListener = finishListener;
      if (contentLength < 0L) {
         throw Messages.msg.parameterOutOfRange("contentLength");
      } else {
         this.delegate = delegate;
         this.remaining = contentLength;
         delegate.getReadSetter().set(new ChannelListener<StreamSourceChannel>() {
            public void handleEvent(StreamSourceChannel channel) {
               ChannelListeners.invokeChannelListener(FixedLengthStreamSourceChannel.this, FixedLengthStreamSourceChannel.this.readListener);
            }
         });
         this.state = (configurable ? 4 : 0) | (propagateClose ? 8 : 0);
      }
   }

   public void setReadListener(ChannelListener<? super FixedLengthStreamSourceChannel> readListener) {
      this.readListener = readListener;
   }

   public ChannelListener<? super FixedLengthStreamSourceChannel> getReadListener() {
      return this.readListener;
   }

   public void setCloseListener(ChannelListener<? super FixedLengthStreamSourceChannel> closeListener) {
      this.closeListener = closeListener;
   }

   public ChannelListener<? super FixedLengthStreamSourceChannel> getCloseListener() {
      return this.closeListener;
   }

   public ChannelListener.Setter<FixedLengthStreamSourceChannel> getReadSetter() {
      return new ReadListenerSettable.Setter(this);
   }

   public ChannelListener.Setter<FixedLengthStreamSourceChannel> getCloseSetter() {
      return new CloseListenerSettable.Setter(this);
   }

   public long transferTo(long position, long count, FileChannel target) throws IOException {
      long remaining = this.remaining;
      if (!Bits.anyAreSet(this.state, 3) && remaining != 0L && count != 0L) {
         long res = 0L;

         long var10;
         try {
            var10 = res = this.delegate.transferTo(position, Math.min(count, remaining), target);
         } finally {
            if (res > 0L && (this.remaining = remaining - res) == 0L) {
               this.state |= 2;
               this.callFinish();
            }

         }

         return var10;
      } else {
         return 0L;
      }
   }

   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
      long remaining = this.remaining;
      if (!Bits.anyAreSet(this.state, 3) && remaining != 0L) {
         if (count == 0L) {
            return 0L;
         } else {
            long res = 0L;

            long var9;
            try {
               var9 = res = this.delegate.transferTo(Math.min(count, remaining), throughBuffer, target);
            } finally {
               if (res > 0L && (this.remaining = remaining - res) == 0L) {
                  this.state |= 2;
                  this.callFinish();
               }

            }

            return var9;
         }
      } else {
         return -1L;
      }
   }

   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
      long remaining = this.remaining;
      if (!Bits.anyAreSet(this.state, 3) && remaining != 0L) {
         if (length == 0) {
            return 0L;
         } else if (length == 1) {
            return (long)this.read(dsts[offset]);
         } else {
            long res = 0L;

            long var23;
            try {
               long t = 0L;

               for(int i = 0; i < length; ++i) {
                  ByteBuffer buffer = dsts[i + offset];
                  int lim;
                  t += (long)((lim = buffer.limit()) - buffer.position());
                  if (t > remaining) {
                     buffer.limit(lim - (int)(t - remaining));

                     try {
                        long var13 = res = this.delegate.read(dsts, offset, i + 1);
                        return var13;
                     } finally {
                        buffer.limit(lim);
                     }
                  }
               }

               var23 = res = t == 0L ? 0L : this.delegate.read(dsts, offset, length);
            } finally {
               if (res > 0L && (this.remaining = remaining - res) == 0L) {
                  this.state |= 2;
                  this.callFinish();
               }

            }

            return var23;
         }
      } else {
         return -1L;
      }
   }

   public long read(ByteBuffer[] dsts) throws IOException {
      return this.read(dsts, 0, dsts.length);
   }

   public int read(ByteBuffer dst) throws IOException {
      long remaining = this.remaining;
      if (!Bits.anyAreSet(this.state, 3) && remaining != 0L) {
         int res = 0;

         int var7;
         try {
            int lim = dst.limit();
            int pos = dst.position();
            if ((long)(lim - pos) <= remaining) {
               var7 = res = this.delegate.read(dst);
               return var7;
            }

            dst.limit((int)(remaining - (long)pos));

            try {
               var7 = res = this.delegate.read(dst);
            } finally {
               dst.limit(lim);
            }
         } finally {
            if (res > 0 && (this.remaining = remaining - (long)res) == 0L) {
               this.state |= 2;
               this.callFinish();
            }

         }

         return var7;
      } else {
         return -1;
      }
   }

   public void suspendReads() {
      if (Bits.allAreClear(this.state, 3)) {
         this.delegate.suspendReads();
      }

   }

   public void resumeReads() {
      if (Bits.allAreClear(this.state, 3)) {
         this.delegate.resumeReads();
      } else {
         this.delegate.getIoThread().execute(ChannelListeners.getChannelListenerTask(this, (ChannelListener)this.readListener));
      }

   }

   public boolean isReadResumed() {
      return Bits.allAreClear(this.state, 3) && this.delegate.isReadResumed();
   }

   public void wakeupReads() {
      if (Bits.allAreClear(this.state, 3)) {
         this.delegate.wakeupReads();
      } else {
         this.delegate.getIoThread().execute(ChannelListeners.getChannelListenerTask(this, (ChannelListener)this.readListener));
      }

   }

   public void shutdownReads() throws IOException {
      int state = this.state;
      if (Bits.allAreClear(state, 1)) {
         try {
            this.state = state | 1 | 2;
            if (Bits.allAreSet(state, 8)) {
               this.delegate.shutdownReads();
            }
         } finally {
            if (Bits.allAreClear(state, 2)) {
               this.callFinish();
            }

            this.callClosed();
         }
      }

   }

   public void awaitReadable() throws IOException {
      if (!Bits.anyAreSet(this.state, 3)) {
         this.delegate.awaitReadable();
      }
   }

   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
      if (!Bits.anyAreSet(this.state, 3)) {
         this.delegate.awaitReadable(time, timeUnit);
      }
   }

   /** @deprecated */
   @Deprecated
   public XnioExecutor getReadThread() {
      return this.delegate.getReadThread();
   }

   public XnioIoThread getIoThread() {
      return this.delegate.getIoThread();
   }

   public XnioWorker getWorker() {
      return this.delegate.getWorker();
   }

   public boolean isOpen() {
      return Bits.allAreClear(this.state, 1);
   }

   public void close() throws IOException {
      this.shutdownReads();
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

   public StreamSourceChannel getChannel(Object guard) {
      Object ourGuard = this.guard;
      return ourGuard != null && guard != ourGuard ? null : this.delegate;
   }

   public long getRemaining() {
      return this.remaining;
   }

   private void callFinish() {
      ChannelListeners.invokeChannelListener(this, this.finishListener);
   }

   private void callClosed() {
      ChannelListeners.invokeChannelListener(this, this.closeListener);
   }
}
