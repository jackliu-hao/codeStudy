package org.xnio.ssl;

import java.io.IOException;
import javax.net.ssl.SSLSession;
import org.xnio.ChannelListener;
import org.xnio.StreamConnection;
import org.xnio.XnioIoThread;
import org.xnio.channels.SslChannel;

public abstract class SslConnection extends StreamConnection implements SslChannel {
   protected SslConnection(XnioIoThread thread) {
      super(thread);
   }

   public abstract void startHandshake() throws IOException;

   public abstract SSLSession getSslSession();

   public abstract ChannelListener.Setter<? extends SslConnection> getHandshakeSetter();

   public ChannelListener.Setter<? extends SslConnection> getCloseSetter() {
      return super.getCloseSetter();
   }
}
