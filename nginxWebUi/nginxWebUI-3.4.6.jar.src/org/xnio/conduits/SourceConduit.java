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
  
  void awaitReadable(long paramLong, TimeUnit paramTimeUnit) throws IOException;
  
  XnioIoThread getReadThread();
  
  void setReadReadyHandler(ReadReadyHandler paramReadReadyHandler);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\SourceConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */