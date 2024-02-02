package org.xnio.channels;

import java.io.IOException;
import javax.net.ssl.SSLSession;
import org.xnio.ChannelListener;

public interface SslChannel extends ConnectedChannel {
  void startHandshake() throws IOException;
  
  SSLSession getSslSession();
  
  ChannelListener.Setter<? extends SslChannel> getCloseSetter();
  
  ChannelListener.Setter<? extends SslChannel> getHandshakeSetter();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\SslChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */