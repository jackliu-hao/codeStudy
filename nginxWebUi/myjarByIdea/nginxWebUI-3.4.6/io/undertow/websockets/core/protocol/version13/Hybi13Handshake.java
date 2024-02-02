package io.undertow.websockets.core.protocol.version13;

import io.undertow.connector.ByteBufferPool;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSocketVersion;
import io.undertow.websockets.core.protocol.version07.Hybi07Handshake;
import io.undertow.websockets.extensions.CompositeExtensionFunction;
import io.undertow.websockets.extensions.ExtensionFunction;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import java.io.Closeable;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.xnio.IoUtils;
import org.xnio.StreamConnection;

public class Hybi13Handshake extends Hybi07Handshake {
   public Hybi13Handshake() {
      super(WebSocketVersion.V13, Collections.emptySet(), false);
   }

   public Hybi13Handshake(Set<String> subprotocols, boolean allowExtensions) {
      super(WebSocketVersion.V13, subprotocols, allowExtensions);
   }

   protected void handshakeInternal(WebSocketHttpExchange exchange) {
      String origin = exchange.getRequestHeader("Origin");
      if (origin != null) {
         exchange.setResponseHeader("Origin", origin);
      }

      this.selectSubprotocol(exchange);
      this.selectExtensions(exchange);
      exchange.setResponseHeader("Sec-WebSocket-Location", getWebSocketLocation(exchange));
      String key = exchange.getRequestHeader("Sec-WebSocket-Key");

      try {
         String solution = this.solve(key);
         exchange.setResponseHeader("Sec-WebSocket-Accept", solution);
         this.performUpgrade(exchange);
      } catch (NoSuchAlgorithmException var5) {
         IoUtils.safeClose((Closeable)exchange);
         exchange.endExchange();
      }
   }

   public WebSocketChannel createChannel(WebSocketHttpExchange exchange, StreamConnection channel, ByteBufferPool pool) {
      List<ExtensionFunction> extensionFunctions = this.initExtensions(exchange);
      return new WebSocket13Channel(channel, pool, getWebSocketLocation(exchange), exchange.getResponseHeader("Sec-WebSocket-Protocol"), false, !extensionFunctions.isEmpty(), CompositeExtensionFunction.compose(extensionFunctions), exchange.getPeerConnections(), exchange.getOptions());
   }
}
