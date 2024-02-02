package org.xnio.channels;

import java.io.IOException;
import org.xnio.ChannelListener;

public interface AcceptingChannel<C extends ConnectedChannel> extends BoundChannel, SimpleAcceptingChannel<C> {
  C accept() throws IOException;
  
  ChannelListener.Setter<? extends AcceptingChannel<C>> getAcceptSetter();
  
  ChannelListener.Setter<? extends AcceptingChannel<C>> getCloseSetter();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\AcceptingChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */