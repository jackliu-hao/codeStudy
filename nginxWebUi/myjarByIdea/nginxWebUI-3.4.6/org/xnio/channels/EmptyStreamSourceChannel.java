package org.xnio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
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

public class EmptyStreamSourceChannel implements StreamSourceChannel, ReadListenerSettable<EmptyStreamSourceChannel>, CloseListenerSettable<EmptyStreamSourceChannel> {
   private final XnioIoThread thread;
   private final Runnable readRunnable = new Runnable() {
      public void run() {
         ChannelListener<? super EmptyStreamSourceChannel> listener = EmptyStreamSourceChannel.this.readListener;
         if (listener == null) {
            EmptyStreamSourceChannel.this.suspendReads();
         } else {
            ChannelListeners.invokeChannelListener(EmptyStreamSourceChannel.this, listener);
            int oldVal = EmptyStreamSourceChannel.this.state;
            if (Bits.allAreSet(oldVal, 4) && Bits.allAreClear(oldVal, 3)) {
               EmptyStreamSourceChannel.this.thread.execute(this);
            }

         }
      }
   };
   private volatile int state;
   private ChannelListener<? super EmptyStreamSourceChannel> readListener;
   private ChannelListener<? super EmptyStreamSourceChannel> closeListener;
   private static final int CLOSED = 1;
   private static final int EMPTIED = 2;
   private static final int RESUMED = 4;
   private static final AtomicIntegerFieldUpdater<EmptyStreamSourceChannel> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(EmptyStreamSourceChannel.class, "state");

   public EmptyStreamSourceChannel(XnioIoThread thread) {
      this.thread = thread;
   }

   public long transferTo(long position, long count, FileChannel target) throws IOException {
      return 0L;
   }

   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
      throughBuffer.limit(0);
      this.emptied();
      return -1L;
   }

   public void setReadListener(ChannelListener<? super EmptyStreamSourceChannel> readListener) {
      this.readListener = readListener;
   }

   public ChannelListener<? super EmptyStreamSourceChannel> getReadListener() {
      return this.readListener;
   }

   public void setCloseListener(ChannelListener<? super EmptyStreamSourceChannel> closeListener) {
      this.closeListener = closeListener;
   }

   public ChannelListener<? super EmptyStreamSourceChannel> getCloseListener() {
      return this.closeListener;
   }

   public ChannelListener.Setter<? extends EmptyStreamSourceChannel> getReadSetter() {
      return new ReadListenerSettable.Setter(this);
   }

   public ChannelListener.Setter<? extends EmptyStreamSourceChannel> getCloseSetter() {
      return new CloseListenerSettable.Setter(this);
   }

   private void emptied() {
      int oldVal;
      int newVal;
      do {
         oldVal = this.state;
         if (Bits.allAreSet(oldVal, 2)) {
            return;
         }

         newVal = oldVal | 2;
      } while(!stateUpdater.compareAndSet(this, oldVal, newVal));

   }

   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
      this.emptied();
      return -1L;
   }

   public long read(ByteBuffer[] dsts) throws IOException {
      this.emptied();
      return -1L;
   }

   public int read(ByteBuffer dst) throws IOException {
      this.emptied();
      return -1;
   }

   public void suspendReads() {
      int oldVal;
      int newVal;
      do {
         oldVal = this.state;
         if (Bits.allAreClear(oldVal, 4)) {
            return;
         }

         newVal = oldVal & -5;
      } while(!stateUpdater.compareAndSet(this, oldVal, newVal));

   }

   public void resumeReads() {
      int oldVal;
      byte newVal;
      do {
         oldVal = this.state;
         if (Bits.anyAreSet(oldVal, 5)) {
            return;
         }

         newVal = 4;
      } while(!stateUpdater.compareAndSet(this, oldVal, newVal));

      if (Bits.allAreClear(oldVal, 2)) {
         this.thread.execute(this.readRunnable);
      }

   }

   public boolean isReadResumed() {
      return Bits.allAreSet(this.state, 4);
   }

   public void wakeupReads() {
      int oldVal;
      byte newVal;
      do {
         oldVal = this.state;
         if (Bits.anyAreSet(oldVal, 1)) {
            return;
         }

         newVal = 4;
      } while(!stateUpdater.compareAndSet(this, oldVal, newVal));

      this.thread.execute(this.readRunnable);
   }

   public void shutdownReads() throws IOException {
      int oldVal = stateUpdater.getAndSet(this, 3);
      if (Bits.allAreClear(oldVal, 1)) {
         this.thread.execute(ChannelListeners.getChannelListenerTask(this, (ChannelListener)this.closeListener));
      }

   }

   public void awaitReadable() throws IOException {
   }

   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
   }

   /** @deprecated */
   @Deprecated
   public XnioExecutor getReadThread() {
      return this.thread;
   }

   public XnioIoThread getIoThread() {
      return this.thread;
   }

   public XnioWorker getWorker() {
      return this.thread.getWorker();
   }

   public boolean isOpen() {
      return Bits.allAreClear(this.state, 1);
   }

   public void close() throws IOException {
      int oldVal = stateUpdater.getAndSet(this, 3);
      if (Bits.allAreClear(oldVal, 1)) {
         this.thread.execute(ChannelListeners.getChannelListenerTask(this, (ChannelListener)this.closeListener));
      }

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
}
