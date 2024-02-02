package io.undertow.server.protocol.proxy;

import io.undertow.connector.ByteBufferPool;
import io.undertow.protocols.ssl.UndertowXnioSsl;
import io.undertow.server.OpenListener;
import org.xnio.ChannelListener;
import org.xnio.OptionMap;
import org.xnio.StreamConnection;

public class ProxyProtocolOpenListener implements ChannelListener<StreamConnection> {
   private final OpenListener openListener;
   private final UndertowXnioSsl ssl;
   private final ByteBufferPool bufferPool;
   private final OptionMap sslOptionMap;

   public ProxyProtocolOpenListener(OpenListener openListener, UndertowXnioSsl ssl, ByteBufferPool bufferPool, OptionMap sslOptionMap) {
      this.openListener = openListener;
      this.ssl = ssl;
      this.bufferPool = bufferPool;
      this.sslOptionMap = sslOptionMap;
   }

   public void handleEvent(StreamConnection streamConnection) {
      streamConnection.getSourceChannel().setReadListener(new ProxyProtocolReadListener(streamConnection, this.openListener, this.ssl, this.bufferPool, this.sslOptionMap));
      streamConnection.getSourceChannel().wakeupReads();
   }
}
