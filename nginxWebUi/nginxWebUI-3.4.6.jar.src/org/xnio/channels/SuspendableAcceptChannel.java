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
  
  @Deprecated
  void wakeupAccepts();
  
  void awaitAcceptable() throws IOException;
  
  void awaitAcceptable(long paramLong, TimeUnit paramTimeUnit) throws IOException;
  
  @Deprecated
  XnioExecutor getAcceptThread();
  
  XnioIoThread getIoThread();
  
  ChannelListener.Setter<? extends SuspendableAcceptChannel> getAcceptSetter();
  
  ChannelListener.Setter<? extends SuspendableAcceptChannel> getCloseSetter();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\SuspendableAcceptChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */