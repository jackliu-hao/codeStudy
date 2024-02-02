package org.xnio.nio;

import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.XnioIoThread;
import org.xnio.channels.CloseableChannel;

abstract class AbstractNioChannel<C extends AbstractNioChannel<C>> implements CloseableChannel {
   protected static final int DEFAULT_BUFFER_SIZE = 65536;
   private final ChannelListener.SimpleSetter<C> closeSetter = new ChannelListener.SimpleSetter();
   protected final NioXnioWorker worker;

   AbstractNioChannel(NioXnioWorker worker) {
      this.worker = worker;
   }

   public final NioXnioWorker getWorker() {
      return this.worker;
   }

   public final ChannelListener.Setter<? extends C> getCloseSetter() {
      return this.closeSetter;
   }

   public XnioIoThread getIoThread() {
      return this.worker.chooseThread();
   }

   protected final C typed() {
      return this;
   }

   protected final void invokeCloseHandler() {
      ChannelListeners.invokeChannelListener(this.typed(), this.closeSetter.get());
   }
}
