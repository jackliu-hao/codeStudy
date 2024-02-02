package io.undertow.websockets.core.protocol.version08;

import io.undertow.connector.ByteBufferPool;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSocketVersion;
import io.undertow.websockets.core.protocol.version07.WebSocket07Channel;
import io.undertow.websockets.extensions.ExtensionFunction;
import java.util.Set;
import org.xnio.OptionMap;
import org.xnio.StreamConnection;

public class WebSocket08Channel extends WebSocket07Channel {
   public WebSocket08Channel(StreamConnection channel, ByteBufferPool bufferPool, String wsUrl, String subProtocols, boolean client, boolean allowExtensions, ExtensionFunction extensionFunction, Set<WebSocketChannel> openConnections, OptionMap options) {
      super(channel, bufferPool, wsUrl, subProtocols, client, allowExtensions, extensionFunction, openConnections, options);
   }

   public WebSocketVersion getVersion() {
      return WebSocketVersion.V08;
   }
}
