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
