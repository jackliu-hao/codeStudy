package org.xnio.channels;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.xnio.ChannelListener;
import org.xnio.XnioExecutor;

public interface SuspendableReadChannel extends CloseableChannel {
   void suspendReads();

   void resumeReads();

   boolean isReadResumed();

   /** @deprecated */
   @Deprecated
   void wakeupReads();

   void shutdownReads() throws IOException;

   void awaitReadable() throws IOException;

   void awaitReadable(long var1, TimeUnit var3) throws IOException;

   /** @deprecated */
   @Deprecated
   XnioExecutor getReadThread();

   ChannelListener.Setter<? extends SuspendableReadChannel> getReadSetter();

   ChannelListener.Setter<? extends SuspendableReadChannel> getCloseSetter();
}
