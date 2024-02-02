package org.xnio.conduits;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.xnio.XnioIoThread;

public interface SinkConduit extends Conduit {
   void terminateWrites() throws IOException;

   boolean isWriteShutdown();

   void resumeWrites();

   void suspendWrites();

   void wakeupWrites();

   boolean isWriteResumed();

   void awaitWritable() throws IOException;

   void awaitWritable(long var1, TimeUnit var3) throws IOException;

   XnioIoThread getWriteThread();

   void setWriteReadyHandler(WriteReadyHandler var1);

   void truncateWrites() throws IOException;

   boolean flush() throws IOException;
}
