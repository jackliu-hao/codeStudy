package org.xnio.channels;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.xnio.ChannelListener;
import org.xnio.XnioExecutor;
import org.xnio.XnioIoThread;

public interface SuspendableAcceptChannel extends CloseableChannel {
   void suspendAccepts();

   void resumeAccepts();

   boolean isAcceptResumed();

   /** @deprecated */
   @Deprecated
   void wakeupAccepts();

   void awaitAcceptable() throws IOException;

   void awaitAcceptable(long var1, TimeUnit var3) throws IOException;

   /** @deprecated */
   @Deprecated
   XnioExecutor getAcceptThread();

   XnioIoThread getIoThread();

   ChannelListener.Setter<? extends SuspendableAcceptChannel> getAcceptSetter();

   ChannelListener.Setter<? extends SuspendableAcceptChannel> getCloseSetter();
}
