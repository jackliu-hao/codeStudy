package org.xnio.channels;

import java.io.IOException;
import org.xnio.ChannelListener;

public interface SimpleAcceptingChannel<C extends CloseableChannel> extends SuspendableAcceptChannel {
  C accept() throws IOException;
  
  ChannelListener.Setter<? extends SimpleAcceptingChannel<C>> getAcceptSetter();
  
  ChannelListener.Setter<? extends SimpleAcceptingChannel<C>> getCloseSetter();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\SimpleAcceptingChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */