package org.xnio.channels;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.xnio.ChannelListener;
import org.xnio.XnioExecutor;

public interface SuspendableWriteChannel extends CloseableChannel {
   void suspendWrites();

   void resumeWrites();

   boolean isWriteResumed();

   /** @deprecated */
   @Deprecated
   void wakeupWrites();

   void shutdownWrites() throws IOException;

   void awaitWritable() throws IOException;

   void awaitWritable(long var1, TimeUnit var3) throws IOException;

   /** @deprecated */
   @Deprecated
   XnioExecutor getWriteThread();

   ChannelListener.Setter<? extends SuspendableWriteChannel> getWriteSetter();

   ChannelListener.Setter<? extends SuspendableWriteChannel> getCloseSetter();

   boolean flush() throws IOException;

   boolean isOpen();

   void close() throws IOException;
}
