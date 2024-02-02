package org.xnio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import org.xnio.Bits;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.Option;
import org.xnio.XnioExecutor;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;

public final class NullStreamSinkChannel implements StreamSinkChannel, WriteListenerSettable<NullStreamSinkChannel>, CloseListenerSettable<NullStreamSinkChannel> {
   private final XnioIoThread thread;
   private volatile int state;
   private ChannelListener<? super NullStreamSinkChannel> writeListener;
   private ChannelListener<? super NullStreamSinkChannel> closeListener;
   private static final AtomicIntegerFieldUpdater<NullStreamSinkChannel> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(NullStreamSinkChannel.class, "state");
   private static final int FLAG_ENTERED = 1;
   private static final int FLAG_CLOSED = 2;
   private static final int FLAG_RESUMED = 4;

   public NullStreamSinkChannel(XnioIoThread thread) {
      this.thread = thread;
   }

   public XnioWorker getWorker() {
      return this.thread.getWorker();
   }

   public XnioIoThread getIoThread() {
      return this.thread;
   }

   /** @deprecated */
   @Deprecated
   public XnioExecutor getWriteThread() {
      return this.thread;
   }

   public long transferFrom(FileChannel src, long position, long count) throws IOException {
      int val = this.enterWrite();

      long var7;
      try {
         var7 = Math.min(src.size() - position, count);
      } finally {
         this.exitWrite(val);
      }

      return var7;
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      int val = this.enterWrite();

      long var6;
      try {
         var6 = Channels.drain(source, count);
      } finally {
         throughBuffer.limit(0);
         this.exitWrite(val);
      }

      return var6;
   }

   public void setWriteListener(ChannelListener<? super NullStreamSinkChannel> writeListener) {
      this.writeListener = writeListener;
   }

   public ChannelListener<? super NullStreamSinkChannel> getWriteListener() {
      return this.writeListener;
   }

   public void setCloseListener(ChannelListener<? super NullStreamSinkChannel> closeListener) {
      this.closeListener = closeListener;
   }

   public ChannelListener<? super NullStreamSinkChannel> getCloseListener() {
      return this.closeListener;
   }

   public ChannelListener.Setter<NullStreamSinkChannel> getWriteSetter() {
      return new WriteListenerSettable.Setter(this);
   }

   public ChannelListener.Setter<NullStreamSinkChannel> getCloseSetter() {
      return new CloseListenerSettable.Setter(this);
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      return Channels.writeFinalBasic(this, src);
   }

   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
      return Channels.writeFinalBasic(this, srcs, offset, length);
   }

   public long writeFinal(ByteBuffer[] srcs) throws IOException {
      return Channels.writeFinalBasic(this, srcs, 0, srcs.length);
   }

   public int write(ByteBuffer src) throws IOException {
      int val = this.enterWrite();

      int var3;
      try {
         var3 = src.remaining();
      } finally {
         src.position(src.limit());
         this.exitWrite(val);
      }

      return var3;
   }

   public long write(ByteBuffer[] srcs) throws IOException {
      return this.write(srcs, 0, srcs.length);
   }

   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
      if (length == 0) {
         return 0L;
      } else {
         int val = this.enterWrite();

         try {
            long t = 0L;

            for(int i = 0; i < length; ++i) {
               ByteBuffer src = srcs[i];
               t += (long)src.remaining();
               src.position(src.limit());
            }

            long var13 = t;
            return var13;
         } finally {
            this.exitWrite(val);
         }
      }
   }

   public void suspendWrites() {
      while(true) {
         int oldVal = this.state;
         if (!Bits.allAreClear(oldVal, 4) && !Bits.allAreSet(oldVal, 2)) {
            int newVal = oldVal & -5;
            if (!stateUpdater.compareAndSet(this, oldVal, newVal)) {
               continue;
            }

            return;
         }

         return;
      }
   }

   public void resumeWrites() {
      int oldVal;
      int newVal;
      do {
         oldVal = this.state;
         if (Bits.anyAreSet(oldVal, 6)) {
            return;
         }

         newVal = oldVal | 4;
      } while(!stateUpdater.compareAndSet(this, oldVal, newVal));

      this.thread.execute(ChannelListeners.getChannelListenerTask(this, (ChannelListener)this.writeListener));
   }

   public void wakeupWrites() {
      this.resumeWrites();
   }

   public boolean isWriteResumed() {
      int state = this.state;
      return Bits.allAreSet(state, 4) && Bits.allAreClear(state, 2);
   }

   public void shutdownWrites() throws IOException {
      int oldVal;
      int newVal;
      do {
         oldVal = this.state;
         if (Bits.allAreSet(oldVal, 2)) {
            return;
         }

         newVal = oldVal | 2;
      } while(!stateUpdater.compareAndSet(this, oldVal, newVal));

      this.writeListener = null;
      ChannelListeners.invokeChannelListener(this, this.closeListener);
   }

   public void awaitWritable() throws IOException {
   }

   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
   }

   public boolean flush() throws IOException {
      return true;
   }

   public boolean isOpen() {
      return Bits.allAreClear(this.state, 2);
   }

   public void close() throws IOException {
      this.shutdownWrites();
   }

   public boolean supportsOption(Option<?> option) {
      return false;
   }

   public <T> T getOption(Option<T> option) throws IOException {
      return null;
   }

   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
      return null;
   }

   private int enterWrite() throws ClosedChannelException {
      int oldVal;
      int newVal;
      do {
         oldVal = this.state;
         if (Bits.allAreSet(oldVal, 1)) {
            throw new ConcurrentStreamChannelAccessException();
         }

         if (Bits.allAreSet(oldVal, 2)) {
            throw new ClosedChannelException();
         }

         newVal = oldVal | 1;
      } while(!stateUpdater.compareAndSet(this, oldVal, newVal));

      return newVal;
   }

   private void exitWrite(int oldVal) {
      for(int newVal = oldVal & -2; !stateUpdater.compareAndSet(this, oldVal, newVal); newVal = oldVal & -2) {
         oldVal = this.state;
      }

   }
}
