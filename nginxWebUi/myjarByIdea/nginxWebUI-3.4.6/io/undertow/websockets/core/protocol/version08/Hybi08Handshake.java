package io.undertow.websockets.core.protocol.version08;

import io.undertow.connector.ByteBufferPool;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSocketVersion;
import io.undertow.websockets.core.protocol.version07.Hybi07Handshake;
import io.undertow.websockets.extensions.CompositeExtensionFunction;
import io.undertow.websockets.extensions.ExtensionFunction;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.xnio.StreamConnection;

public class Hybi08Handshake extends Hybi07Handshake {
   public Hybi08Handshake() {
      super(WebSocketVersion.V08, Collections.emptySet(), false);
   }

   public Hybi08Handshake(Set<String> subprotocols, boolean allowExtensions) {
      super(WebSocketVersion.V08, subprotocols, allowExtensions);
   }

   public WebSocketChannel createChannel(WebSocketHttpExchange exchange, StreamConnection channel, ByteBufferPool pool) {
      List<ExtensionFunction> extensionFunctions = this.initExtensions(exchange);
      return new WebSocket08Channel(channel, pool, getWebSocketLocation(exchange), exchange.getResponseHeader("Sec-WebSocket-Protocol"), false, !extensionFunctions.isEmpty(), CompositeExtensionFunction.compose(extensionFunctions), exchange.getPeerConnections(), exchange.getOptions());
   }
}
