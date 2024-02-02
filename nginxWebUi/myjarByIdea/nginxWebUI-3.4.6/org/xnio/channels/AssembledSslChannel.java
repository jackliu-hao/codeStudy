package org.xnio.channels;

import java.io.IOException;
import javax.net.ssl.SSLSession;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio._private.Messages;

public class AssembledSslChannel extends AssembledConnectedChannel implements SslChannel {
   private final SslChannel sslChannel;
   private final ChannelListener.Setter<AssembledSslChannel> handshakeSetter;

   public AssembledSslChannel(SuspendableReadChannel readChannel, SuspendableWriteChannel writeChannel) {
      super(readChannel, writeChannel);
      if (readChannel instanceof SslChannel) {
         this.sslChannel = (SslChannel)readChannel;
      } else {
         if (!(writeChannel instanceof SslChannel)) {
            throw Messages.msg.oneChannelMustBeSSL();
         }

         this.sslChannel = (SslChannel)writeChannel;
      }

      this.handshakeSetter = ChannelListeners.getDelegatingSetter(this.sslChannel.getHandshakeSetter(), this);
   }

   public void startHandshake() throws IOException {
      this.sslChannel.startHandshake();
   }

   public SSLSession getSslSession() {
      return this.sslChannel.getSslSession();
   }

   public ChannelListener.Setter<? extends AssembledSslChannel> getHandshakeSetter() {
      return this.handshakeSetter;
   }

   public ChannelListener.Setter<? extends AssembledSslChannel> getCloseSetter() {
      return super.getCloseSetter();
   }
}
