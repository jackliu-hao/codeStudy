package org.xnio.channels;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.IoUtils;
import org.xnio.Option;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio._private.Messages;

public class AssembledChannel implements CloseableChannel {
   private final SuspendableReadChannel readChannel;
   private final SuspendableWriteChannel writeChannel;
   private final ChannelListener.SimpleSetter<AssembledChannel> closeSetter = new ChannelListener.SimpleSetter();
   private final ChannelListener<CloseableChannel> listener = new ChannelListener<CloseableChannel>() {
      public void handleEvent(CloseableChannel channel) {
         AssembledChannel obj = AssembledChannel.this;

         int newState;
         int oldState;
         do {
            oldState = AssembledChannel.stateUpdater.get(obj);
            if (oldState == 3) {
               return;
            }

            newState = oldState;
            if (channel == AssembledChannel.this.readChannel) {
               newState = oldState | 1;
            }

            if (channel == AssembledChannel.this.writeChannel) {
               newState |= 2;
            }
         } while(!AssembledChannel.stateUpdater.compareAndSet(obj, oldState, newState));

         if (newState == 3) {
            ChannelListeners.invokeChannelListener(obj, AssembledChannel.this.closeSetter.get());
         }

      }
   };
   private volatile int state = 0;
   private static final AtomicIntegerFieldUpdater<AssembledChannel> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(AssembledChannel.class, "state");

   public AssembledChannel(SuspendableReadChannel readChannel, SuspendableWriteChannel writeChannel) {
      this.readChannel = readChannel;
      this.writeChannel = writeChannel;
      if (readChannel.getWorker() != writeChannel.getWorker()) {
         throw Messages.msg.differentWorkers();
      }
   }

   public ChannelListener.Setter<? extends CloseableChannel> getCloseSetter() {
      this.readChannel.getCloseSetter().set(this.listener);
      this.writeChannel.getCloseSetter().set(this.listener);
      return this.closeSetter;
   }

   public XnioWorker getWorker() {
      return this.readChannel.getWorker();
   }

   public XnioIoThread getIoThread() {
      return this.readChannel.getIoThread();
   }

   public void close() throws IOException {
      try {
         this.readChannel.close();
         this.writeChannel.close();
      } finally {
         IoUtils.safeClose((Closeable)this.readChannel);
         IoUtils.safeClose((Closeable)this.writeChannel);
      }

   }

   public boolean isOpen() {
      return this.readChannel.isOpen() && this.writeChannel.isOpen();
   }

   public boolean supportsOption(Option<?> option) {
      return this.readChannel.supportsOption(option) || this.writeChannel.supportsOption(option);
   }

   private static <T> T nonNullOrFirst(T one, T two) {
      return one != null ? one : two;
   }

   public <T> T getOption(Option<T> option) throws IOException {
      return nonNullOrFirst(this.readChannel.getOption(option), this.writeChannel.getOption(option));
   }

   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
      return nonNullOrFirst(this.readChannel.setOption(option, value), this.writeChannel.setOption(option, value));
   }
}
