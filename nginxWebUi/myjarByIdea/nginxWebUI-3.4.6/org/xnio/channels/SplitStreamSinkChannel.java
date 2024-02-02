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

public final class SplitStreamSinkChannel implements StreamSinkChannel, WriteListenerSettable<SplitStreamSinkChannel>, CloseListenerSettable<SplitStreamSinkChannel> {
   private final StreamSinkChannel delegate;
   private volatile int state;
   private ChannelListener<? super SplitStreamSinkChannel> writeListener;
   private ChannelListener<? super SplitStreamSinkChannel> closeListener;
   private static final int FLAG_DELEGATE_CONFIG = 1;
   private static final int FLAG_CLOSE_REQ = 2;
   private static final int FLAG_CLOSE_COMP = 4;
   private static final AtomicIntegerFieldUpdater<SplitStreamSinkChannel> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(SplitStreamSinkChannel.class, "state");

   public SplitStreamSinkChannel(StreamSinkChannel delegate, boolean delegateConfig) {
      this.delegate = delegate;
      this.state = delegateConfig ? 1 : 0;
      delegate.getWriteSetter().set(new ChannelListener<StreamSinkChannel>() {
         public void handleEvent(StreamSinkChannel channel) {
            ChannelListeners.invokeChannelListener(SplitStreamSinkChannel.this, SplitStreamSinkChannel.this.writeListener);
         }
      });
   }

   public SplitStreamSinkChannel(StreamSinkChannel delegate) {
      this(delegate, false);
   }

   public void setWriteListener(ChannelListener<? super SplitStreamSinkChannel> writeListener) {
      this.writeListener = writeListener;
   }

   public ChannelListener<? super SplitStreamSinkChannel> getWriteListener() {
      return this.writeListener;
   }

   public void setCloseListener(ChannelListener<? super SplitStreamSinkChannel> closeListener) {
      this.closeListener = closeListener;
   }

   public ChannelListener<? super SplitStreamSinkChannel> getCloseListener() {
      return this.closeListener;
   }

   public ChannelListener.Setter<? extends SplitStreamSinkChannel> getCloseSetter() {
      return new CloseListenerSettable.Setter(this);
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      return this.delegate.writeFinal(src);
   }

   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
      return this.delegate.writeFinal(srcs, offset, length);
   }

   public long writeFinal(ByteBuffer[] srcs) throws IOException {
      return this.delegate.writeFinal(srcs);
   }

   public ChannelListener.Setter<? extends SplitStreamSinkChannel> getWriteSetter() {
      return new WriteListenerSettable.Setter(this);
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

      this.delegate.shutdownWrites();
   }

   public boolean isOpen() {
      return Bits.allAreClear(this.state, 2);
   }

   public void close() throws IOException {
      this.shutdownWrites();
      this.flush();
   }

   public boolean flush() throws IOException {
      int oldVal = this.state;
      if (Bits.allAreSet(oldVal, 4)) {
         return true;
      } else {
         boolean flushed = this.delegate.flush();
         if (!flushed) {
            return false;
         } else {
            while(!Bits.allAreClear(oldVal, 2)) {
               int newVal = oldVal | 4;
               if (stateUpdater.compareAndSet(this, oldVal, newVal)) {
                  ChannelListeners.invokeChannelListener(this, this.closeListener);
                  return true;
               }
            }

            return true;
         }
      }
   }

   public long transferFrom(FileChannel src, long position, long count) throws IOException {
      return this.delegate.transferFrom(src, position, count);
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      return this.delegate.transferFrom(source, count, throughBuffer);
   }

   public int write(ByteBuffer src) throws IOException {
      return this.delegate.write(src);
   }

   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
      return this.delegate.write(srcs, offset, length);
   }

   public long write(ByteBuffer[] srcs) throws IOException {
      return this.delegate.write(srcs);
   }

   public void suspendWrites() {
      this.delegate.suspendWrites();
   }

   public void resumeWrites() {
      this.delegate.resumeWrites();
   }

   public boolean isWriteResumed() {
      return this.delegate.isWriteResumed();
   }

   public void wakeupWrites() {
      this.delegate.wakeupWrites();
   }

   public void awaitWritable() throws IOException {
      this.delegate.awaitWritable();
   }

   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
      this.delegate.awaitWritable(time, timeUnit);
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

   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
      return Bits.allAreSet(this.state, 1) ? this.delegate.setOption(option, value) : null;
   }

   public <T> T getOption(Option<T> option) throws IOException {
      return Bits.allAreSet(this.state, 1) ? this.delegate.getOption(option) : null;
   }

   public boolean supportsOption(Option<?> option) {
      return Bits.allAreSet(this.state, 1) ? this.delegate.supportsOption(option) : false;
   }
}
