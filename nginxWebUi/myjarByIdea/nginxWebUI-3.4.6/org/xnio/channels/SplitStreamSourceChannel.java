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

public final class SplitStreamSourceChannel implements StreamSourceChannel, ReadListenerSettable<SplitStreamSourceChannel>, CloseListenerSettable<SplitStreamSourceChannel> {
   private final StreamSourceChannel delegate;
   private volatile int state;
   private ChannelListener<? super SplitStreamSourceChannel> readListener;
   private ChannelListener<? super SplitStreamSourceChannel> closeListener;
   private static final int FLAG_DELEGATE_CONFIG = 1;
   private static final int FLAG_CLOSED = 2;
   private static final AtomicIntegerFieldUpdater<SplitStreamSourceChannel> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(SplitStreamSourceChannel.class, "state");

   public SplitStreamSourceChannel(StreamSourceChannel delegate, boolean delegateConfig) {
      this.delegate = delegate;
      delegate.getReadSetter().set(new ChannelListener<StreamSourceChannel>() {
         public void handleEvent(StreamSourceChannel channel) {
            ChannelListeners.invokeChannelListener(SplitStreamSourceChannel.this, SplitStreamSourceChannel.this.readListener);
         }
      });
      this.state = delegateConfig ? 1 : 0;
   }

   public SplitStreamSourceChannel(StreamSourceChannel delegate) {
      this(delegate, false);
   }

   public void setReadListener(ChannelListener<? super SplitStreamSourceChannel> readListener) {
      this.readListener = readListener;
   }

   public ChannelListener<? super SplitStreamSourceChannel> getReadListener() {
      return this.readListener;
   }

   public void setCloseListener(ChannelListener<? super SplitStreamSourceChannel> closeListener) {
      this.closeListener = closeListener;
   }

   public ChannelListener<? super SplitStreamSourceChannel> getCloseListener() {
      return this.closeListener;
   }

   public ChannelListener.Setter<? extends SplitStreamSourceChannel> getReadSetter() {
      return new ReadListenerSettable.Setter(this);
   }

   public ChannelListener.Setter<? extends SplitStreamSourceChannel> getCloseSetter() {
      return new CloseListenerSettable.Setter(this);
   }

   public long transferTo(long position, long count, FileChannel target) throws IOException {
      return this.delegate.transferTo(position, count, target);
   }

   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
      return this.delegate.transferTo(count, throughBuffer, target);
   }

   public int read(ByteBuffer dst) throws IOException {
      return this.delegate.read(dst);
   }

   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
      return this.delegate.read(dsts, offset, length);
   }

   public long read(ByteBuffer[] dsts) throws IOException {
      return this.delegate.read(dsts);
   }

   public void suspendReads() {
      this.delegate.suspendReads();
   }

   public void resumeReads() {
      this.delegate.resumeReads();
   }

   public void wakeupReads() {
      this.delegate.wakeupReads();
   }

   public boolean isReadResumed() {
      return this.delegate.isReadResumed();
   }

   public void awaitReadable() throws IOException {
      this.delegate.awaitReadable();
   }

   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
      this.delegate.awaitReadable(time, timeUnit);
   }

   public XnioWorker getWorker() {
      return this.delegate.getWorker();
   }

   public boolean supportsOption(Option<?> option) {
      return Bits.allAreSet(this.state, 1) ? this.delegate.supportsOption(option) : false;
   }

   public <T> T getOption(Option<T> option) throws IOException {
      return Bits.allAreSet(this.state, 1) ? this.delegate.getOption(option) : null;
   }

   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
      return Bits.allAreSet(this.state, 1) ? this.delegate.setOption(option, value) : null;
   }

   public void shutdownReads() throws IOException {
      int oldVal;
      int newVal;
      do {
         oldVal = this.state;
         if (Bits.allAreSet(oldVal, 2)) {
            return;
         }

         newVal = oldVal | 2;
      } while(!stateUpdater.compareAndSet(this, oldVal, newVal));

      this.delegate.shutdownReads();
      ChannelListeners.invokeChannelListener(this, this.closeListener);
   }

   /** @deprecated */
   @Deprecated
   public XnioExecutor getReadThread() {
      return this.delegate.getReadThread();
   }

   public XnioIoThread getIoThread() {
      return this.delegate.getIoThread();
   }

   public boolean isOpen() {
      return Bits.allAreClear(this.state, 2);
   }

   public void close() throws IOException {
      this.shutdownReads();
   }
}
