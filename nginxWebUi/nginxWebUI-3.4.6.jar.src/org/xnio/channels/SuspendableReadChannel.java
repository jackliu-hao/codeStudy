package org.xnio.channels;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.xnio.ChannelListener;
import org.xnio.XnioExecutor;

public interface SuspendableReadChannel extends CloseableChannel {
  void suspendReads();
  
  void resumeReads();
  
  boolean isReadResumed();
  
  @Deprecated
  void wakeupReads();
  
  void shutdownReads() throws IOException;
  
  void awaitReadable() throws IOException;
  
  void awaitReadable(long paramLong, TimeUnit paramTimeUnit) throws IOException;
  
  @Deprecated
  XnioExecutor getReadThread();
  
  ChannelListener.Setter<? extends SuspendableReadChannel> getReadSetter();
  
  ChannelListener.Setter<? extends SuspendableReadChannel> getCloseSetter();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\SuspendableReadChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */