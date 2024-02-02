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
  
  void awaitWritable(long paramLong, TimeUnit paramTimeUnit) throws IOException;
  
  XnioIoThread getWriteThread();
  
  void setWriteReadyHandler(WriteReadyHandler paramWriteReadyHandler);
  
  void truncateWrites() throws IOException;
  
  boolean flush() throws IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\SinkConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */