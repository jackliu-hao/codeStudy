package org.xnio.channels;

import java.io.IOException;
import java.nio.channels.InterruptibleChannel;
import org.xnio.ChannelListener;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;

public interface CloseableChannel extends InterruptibleChannel, Configurable {
  ChannelListener.Setter<? extends CloseableChannel> getCloseSetter();
  
  XnioWorker getWorker();
  
  XnioIoThread getIoThread();
  
  void close() throws IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\CloseableChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */