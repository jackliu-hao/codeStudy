package org.xnio.conduits;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.xnio.XnioIoThread;

public interface SourceConduit extends Conduit {
   void terminateReads() throws IOException;

   boolean isReadShutdown();

   void resumeReads();

   void suspendReads();

   void wakeupReads();

   boolean isReadResumed();

   void awaitReadable() throws IOException;

   void awaitReadable(long var1, TimeUnit var3) throws IOException;

   XnioIoThread getReadThread();

   void setReadReadyHandler(ReadReadyHandler var1);
}
