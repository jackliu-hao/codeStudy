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
import org.xnio.conduits.ConduitStreamSinkChannel;

public abstract class DetachableStreamSinkChannel implements StreamSinkChannel {
   protected final StreamSinkChannel delegate;
   protected ChannelListener.SimpleSetter<DetachableStreamSinkChannel> writeSetter;
   protected ChannelListener.SimpleSetter<DetachableStreamSinkChannel> closeSetter;

   public DetachableStreamSinkChannel(StreamSinkChannel delegate) {
      this.delegate = delegate;
   }

   protected abstract boolean isFinished();

   public void suspendWrites() {
      if (!this.isFinished()) {
         this.delegate.suspendWrites();
      }
   }

   public boolean isWriteResumed() {
      return this.isFinished() ? false : this.delegate.isWriteResumed();
   }

   public void shutdownWrites() throws IOException {
      if (!this.isFinished()) {
         this.delegate.shutdownWrites();
      }
   }

   public void awaitWritable() throws IOException {
      if (this.isFinished()) {
         throw UndertowMessages.MESSAGES.channelIsClosed();
      } else {
         this.delegate.awaitWritable();
      }
   }

   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
      if (this.isFinished()) {
         throw UndertowMessages.MESSAGES.channelIsClosed();
      } else {
         this.delegate.awaitWritable(time, timeUnit);
      }
   }

   public XnioExecutor getWriteThread() {
      return this.delegate.getWriteThread();
   }

   public boolean isOpen() {
      return !this.isFinished() && this.delegate.isOpen();
   }

   public void close() throws IOException {
      if (!this.isFinished()) {
         this.delegate.close();
      }
   }

   public boolean flush() throws IOException {
      return this.isFinished() ? true : this.delegate.flush();
   }

   public long transferFrom(FileChannel src, long position, long count) throws IOException {
      if (this.isFinished()) {
         throw UndertowMessages.MESSAGES.channelIsClosed();
      } else {
         return this.delegate.transferFrom(src, position, count);
      }
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      if (this.isFinished()) {
         throw UndertowMessages.MESSAGES.channelIsClosed();
      } else {
         return this.delegate.transferFrom(source, count, throughBuffer);
      }
   }

   public ChannelListener.Setter<? extends StreamSinkChannel> getWriteSetter() {
      if (this.writeSetter == null) {
         this.writeSetter = new ChannelListener.SimpleSetter();
         if (!this.isFinished()) {
            if (this.delegate instanceof ConduitStreamSinkChannel) {
               ((ConduitStreamSinkChannel)this.delegate).setWriteListener(new SetterDelegatingListener(this.writeSetter, this));
            } else {
               this.delegate.getWriteSetter().set(new SetterDelegatingListener(this.writeSetter, this));
            }
         }
      }

      return this.writeSetter;
   }

   public ChannelListener.Setter<? extends StreamSinkChannel> getCloseSetter() {
      if (this.closeSetter == null) {
         this.closeSetter = new ChannelListener.SimpleSetter();
         if (!this.isFinished()) {
            this.delegate.getCloseSetter().set(ChannelListeners.delegatingChannelListener(this, this.closeSetter));
         }
      }

      return this.closeSetter;
   }

   public XnioWorker getWorker() {
      return this.delegate.getWorker();
   }

   public XnioIoThread getIoThread() {
      return this.delegate.getIoThread();
   }

   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
      if (this.isFinished()) {
         throw UndertowMessages.MESSAGES.channelIsClosed();
      } else {
         return this.delegate.write(srcs, offset, length);
      }
   }

   public long write(ByteBuffer[] srcs) throws IOException {
      if (this.isFinished()) {
         throw UndertowMessages.MESSAGES.channelIsClosed();
      } else {
         return this.delegate.write(srcs);
      }
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      if (this.isFinished()) {
         throw UndertowMessages.MESSAGES.channelIsClosed();
      } else {
         return this.delegate.writeFinal(src);
      }
   }

   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
      if (this.isFinished()) {
         throw UndertowMessages.MESSAGES.channelIsClosed();
      } else {
         return this.delegate.writeFinal(srcs, offset, length);
      }
   }

   public long writeFinal(ByteBuffer[] srcs) throws IOException {
      if (this.isFinished()) {
         throw UndertowMessages.MESSAGES.channelIsClosed();
      } else {
         return this.delegate.writeFinal(srcs);
      }
   }

   public boolean supportsOption(Option<?> option) {
      return this.delegate.supportsOption(option);
   }

   public <T> T getOption(Option<T> option) throws IOException {
      if (this.isFinished()) {
         throw UndertowMessages.MESSAGES.channelIsClosed();
      } else {
         return this.delegate.getOption(option);
      }
   }

   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
      if (this.isFinished()) {
         throw UndertowMessages.MESSAGES.channelIsClosed();
      } else {
         return this.delegate.setOption(option, value);
      }
   }

   public int write(ByteBuffer src) throws IOException {
      if (this.isFinished()) {
         throw UndertowMessages.MESSAGES.channelIsClosed();
      } else {
         return this.delegate.write(src);
      }
   }

   public void resumeWrites() {
      if (!this.isFinished()) {
         this.delegate.resumeWrites();
      }
   }

   public void wakeupWrites() {
      if (!this.isFinished()) {
         this.delegate.wakeupWrites();
      }
   }

   public void responseDone() {
      if (this.delegate instanceof ConduitStreamSinkChannel) {
         ((ConduitStreamSinkChannel)this.delegate).setCloseListener((ChannelListener)null);
         ((ConduitStreamSinkChannel)this.delegate).setWriteListener((ChannelListener)null);
      } else {
         this.delegate.getCloseSetter().set((ChannelListener)null);
         this.delegate.getWriteSetter().set((ChannelListener)null);
      }

      if (this.delegate.isWriteResumed()) {
         this.delegate.suspendWrites();
      }

   }

   private static class SetterDelegatingListener implements ChannelListener<StreamSinkChannel> {
      private final ChannelListener.SimpleSetter<StreamSinkChannel> setter;
      private final StreamSinkChannel channel;

      SetterDelegatingListener(ChannelListener.SimpleSetter<StreamSinkChannel> setter, StreamSinkChannel channel) {
         this.setter = setter;
         this.channel = channel;
      }

      public void handleEvent(StreamSinkChannel channel) {
         ChannelListener<? super StreamSinkChannel> channelListener = this.setter.get();
         if (channelListener != null) {
            ChannelListeners.invokeChannelListener(this.channel, channelListener);
         } else {
            UndertowLogger.REQUEST_LOGGER.debugf("suspending writes on %s to prevent listener runaway", channel);
            channel.suspendWrites();
         }

      }

      public String toString() {
         return "Setter delegating channel listener -> " + this.setter;
      }
   }
}
