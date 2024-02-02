package org.xnio.channels;

import java.io.IOException;
import javax.net.ssl.SSLSession;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;

public class AssembledConnectedSslStreamChannel extends AssembledConnectedStreamChannel implements ConnectedSslStreamChannel {
   private final SslChannel sslChannel;
   private final ChannelListener.Setter<AssembledConnectedSslStreamChannel> handshakeSetter;

   public AssembledConnectedSslStreamChannel(SslChannel sslChannel, StreamSourceChannel source, StreamSinkChannel sink) {
      super(sslChannel, source, sink);
      this.sslChannel = sslChannel;
      this.handshakeSetter = ChannelListeners.getDelegatingSetter(sslChannel.getHandshakeSetter(), this);
   }

   public AssembledConnectedSslStreamChannel(StreamSourceChannel source, StreamSinkChannel sink) {
      this(new AssembledSslChannel(source, sink), source, sink);
   }

   public void startHandshake() throws IOException {
      this.sslChannel.startHandshake();
   }

   public SSLSession getSslSession() {
      return this.sslChannel.getSslSession();
   }

   public ChannelListener.Setter<? extends AssembledConnectedSslStreamChannel> getHandshakeSetter() {
      return this.handshakeSetter;
   }

   public ChannelListener.Setter<? extends AssembledConnectedSslStreamChannel> getCloseSetter() {
      return super.getCloseSetter();
   }

   public ChannelListener.Setter<? extends AssembledConnectedSslStreamChannel> getReadSetter() {
      return super.getReadSetter();
   }

   public ChannelListener.Setter<? extends AssembledConnectedSslStreamChannel> getWriteSetter() {
      return super.getWriteSetter();
   }
}
