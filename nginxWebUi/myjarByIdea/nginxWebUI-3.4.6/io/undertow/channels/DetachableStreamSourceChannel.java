package io.undertow.channels;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.Option;
import org.xnio.XnioExecutor;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.conduits.ConduitStreamSourceChannel;

public abstract class DetachableStreamSourceChannel implements StreamSourceChannel {
   protected final StreamSourceChannel delegate;
   protected ChannelListener.SimpleSetter<DetachableStreamSourceChannel> readSetter;
   protected ChannelListener.SimpleSetter<DetachableStreamSourceChannel> closeSetter;

   public DetachableStreamSourceChannel(StreamSourceChannel delegate) {
      this.delegate = delegate;
   }

   protected abstract boolean isFinished();

   public void resumeReads() {
      if (!this.isFinished()) {
         this.delegate.resumeReads();
      }
   }

   public long transferTo(long position, long count, FileChannel target) throws IOException {
      return this.isFinished() ? -1L : this.delegate.transferTo(position, count, target);
   }

   public void awaitReadable() throws IOException {
      if (!this.isFinished()) {
         this.delegate.awaitReadable();
      }
   }

   public void suspendReads() {
      if (!this.isFinished()) {
         this.delegate.suspendReads();
      }
   }

   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
      return this.isFinished() ? -1L : this.delegate.transferTo(count, throughBuffer, target);
   }

   public XnioWorker getWorker() {
      return this.delegate.getWorker();
   }

   public boolean isReadResumed() {
      return this.isFinished() ? false : this.delegate.isReadResumed();
   }

   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
      if (this.isFinished()) {
         throw UndertowMessages.MESSAGES.channelIsClosed();
      } else {
         return this.delegate.setOption(option, value);
      }
   }

   public boolean supportsOption(Option<?> option) {
      return this.delegate.supportsOption(option);
   }

   public void shutdownReads() throws IOException {
      if (!this.isFinished()) {
         this.delegate.shutdownReads();
      }
   }

   public ChannelListener.Setter<? extends StreamSourceChannel> getReadSetter() {
      if (this.readSetter == null) {
         this.readSetter = new ChannelListener.SimpleSetter();
         if (!this.isFinished()) {
            if (this.delegate instanceof ConduitStreamSourceChannel) {
               ((ConduitStreamSourceChannel)this.delegate).setReadListener(new SetterDelegatingListener(this.readSetter, this));
            } else {
               this.delegate.getReadSetter().set(new SetterDelegatingListener(this.readSetter, this));
            }
         }
      }

      return this.readSetter;
   }

   public boolean isOpen() {
      return this.isFinished() ? false : this.delegate.isOpen();
   }

   public long read(ByteBuffer[] dsts) throws IOException {
      return this.isFinished() ? -1L : this.delegate.read(dsts);
   }

   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
      return this.isFinished() ? -1L : this.delegate.read(dsts, offset, length);
   }

   public void wakeupReads() {
      if (!this.isFinished()) {
         this.delegate.wakeupReads();
      }
   }

   public XnioExecutor getReadThread() {
      return this.delegate.getReadThread();
   }

   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
      if (this.isFinished()) {
         throw UndertowMessages.MESSAGES.channelIsClosed();
      } else {
         this.delegate.awaitReadable(time, timeUnit);
      }
   }

   public ChannelListener.Setter<? extends StreamSourceChannel> getCloseSetter() {
      if (this.closeSetter == null) {
         this.closeSetter = new ChannelListener.SimpleSetter();
         if (!this.isFinished()) {
            if (this.delegate instanceof ConduitStreamSourceChannel) {
               ((ConduitStreamSourceChannel)this.delegate).setCloseListener(ChannelListeners.delegatingChannelListener(this, this.closeSetter));
            } else {
               this.delegate.getCloseSetter().set(ChannelListeners.delegatingChannelListener(this, this.closeSetter));
            }
         }
      }

      return this.closeSetter;
   }

   public void close() throws IOException {
      if (!this.isFinished()) {
         this.delegate.close();
      }
   }

   public <T> T getOption(Option<T> option) throws IOException {
      if (this.isFinished()) {
         throw UndertowMessages.MESSAGES.streamIsClosed();
      } else {
         return this.delegate.getOption(option);
      }
   }

   public int read(ByteBuffer dst) throws IOException {
      return this.isFinished() ? -1 : this.delegate.read(dst);
   }

   public XnioIoThread getIoThread() {
      return this.delegate.getIoThread();
   }

   private static class SetterDelegatingListener implements ChannelListener<StreamSourceChannel> {
      private final ChannelListener.SimpleSetter<StreamSourceChannel> setter;
      private final StreamSourceChannel channel;

      SetterDelegatingListener(ChannelListener.SimpleSetter<StreamSourceChannel> setter, StreamSourceChannel channel) {
         this.setter = setter;
         this.channel = channel;
      }

      public void handleEvent(StreamSourceChannel channel) {
         ChannelListener<? super StreamSourceChannel> channelListener = this.setter.get();
         if (channelListener != null) {
            ChannelListeners.invokeChannelListener(this.channel, channelListener);
         } else {
            UndertowLogger.REQUEST_LOGGER.debugf("suspending reads on %s to prevent listener runaway", channel);
            channel.suspendReads();
         }

      }

      public String toString() {
         return "Setter delegating channel listener -> " + this.setter;
      }
   }
}
