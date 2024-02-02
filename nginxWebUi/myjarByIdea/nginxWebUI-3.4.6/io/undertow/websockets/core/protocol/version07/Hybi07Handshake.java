package io.undertow.websockets.core.protocol.version07;

import io.undertow.connector.ByteBufferPool;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSocketVersion;
import io.undertow.websockets.core.protocol.Handshake;
import io.undertow.websockets.extensions.CompositeExtensionFunction;
import io.undertow.websockets.extensions.ExtensionFunction;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import java.io.Closeable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.xnio.IoUtils;
import org.xnio.StreamConnection;

public class Hybi07Handshake extends Handshake {
   public static final String MAGIC_NUMBER = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

   protected Hybi07Handshake(WebSocketVersion version, Set<String> subprotocols, boolean allowExtensions) {
      super(version, "SHA1", "258EAFA5-E914-47DA-95CA-C5AB0DC85B11", subprotocols);
      this.allowExtensions = allowExtensions;
   }

   public Hybi07Handshake(Set<String> subprotocols, boolean allowExtensions) {
      this(WebSocketVersion.V07, subprotocols, allowExtensions);
   }

   public Hybi07Handshake() {
      this(WebSocketVersion.V07, Collections.emptySet(), false);
   }

   public boolean matches(WebSocketHttpExchange exchange) {
      return exchange.getRequestHeader("Sec-WebSocket-Key") != null && exchange.getRequestHeader("Sec-WebSocket-Version") != null ? exchange.getRequestHeader("Sec-WebSocket-Version").equals(this.getVersion().toHttpHeaderValue()) : false;
   }

   protected void handshakeInternal(WebSocketHttpExchange exchange) {
      String origin = exchange.getRequestHeader("Sec-WebSocket-Origin");
      if (origin != null) {
         exchange.setResponseHeader("Sec-WebSocket-Origin", origin);
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

   protected final String solve(String nonceBase64) throws NoSuchAlgorithmException {
      String concat = nonceBase64.trim() + this.getMagicNumber();
      MessageDigest digest = MessageDigest.getInstance(this.getHashAlgorithm());
      digest.update(concat.getBytes(StandardCharsets.UTF_8));
      return Base64.encodeBytes(digest.digest()).trim();
   }

   public WebSocketChannel createChannel(WebSocketHttpExchange exchange, StreamConnection channel, ByteBufferPool pool) {
      List<ExtensionFunction> extensionFunctions = this.initExtensions(exchange);
      return new WebSocket07Channel(channel, pool, getWebSocketLocation(exchange), exchange.getResponseHeader("Sec-WebSocket-Protocol"), false, !extensionFunctions.isEmpty(), CompositeExtensionFunction.compose(extensionFunctions), exchange.getPeerConnections(), exchange.getOptions());
   }
}
