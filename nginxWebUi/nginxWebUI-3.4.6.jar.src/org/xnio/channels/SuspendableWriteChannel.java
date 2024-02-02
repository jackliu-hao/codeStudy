package org.xnio.channels;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.xnio.ChannelListener;
import org.xnio.XnioExecutor;

public interface SuspendableWriteChannel extends CloseableChannel {
  void suspendWrites();
  
  void resumeWrites();
  
  boolean isWriteResumed();
  
  @Deprecated
  void wakeupWrites();
  
  void shutdownWrites() throws IOException;
  
  void awaitWritable() throws IOException;
  
  void awaitWritable(long paramLong, TimeUnit paramTimeUnit) throws IOException;
  
  @Deprecated
  XnioExecutor getWriteThread();
  
  ChannelListener.Setter<? extends SuspendableWriteChannel> getWriteSetter();
  
  ChannelListener.Setter<? extends SuspendableWriteChannel> getCloseSetter();
  
  boolean flush() throws IOException;
  
  boolean isOpen();
  
  void close() throws IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\SuspendableWriteChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */