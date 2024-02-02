package io.undertow.websockets.client;

import io.undertow.connector.ByteBufferPool;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSocketVersion;
import io.undertow.websockets.extensions.ExtensionHandshake;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.xnio.OptionMap;
import org.xnio.StreamConnection;
import org.xnio.http.ExtendedHandshakeChecker;

public abstract class WebSocketClientHandshake {
   protected final URI url;

   public static WebSocketClientHandshake create(WebSocketVersion version, URI uri) {
      return create(version, uri, (WebSocketClientNegotiation)null, (Set)null);
   }

   public static WebSocketClientHandshake create(WebSocketVersion version, URI uri, WebSocketClientNegotiation clientNegotiation, Set<ExtensionHandshake> extensions) {
      switch (version) {
         case V13:
            return new WebSocket13ClientHandshake(uri, clientNegotiation, extensions);
         default:
            throw new IllegalArgumentException();
      }
   }

   public WebSocketClientHandshake(URI url) {
      this.url = url;
   }

   public abstract WebSocketChannel createChannel(StreamConnection var1, String var2, ByteBufferPool var3, OptionMap var4);

   public abstract Map<String, String> createHeaders();

   public abstract ExtendedHandshakeChecker handshakeChecker(URI var1, Map<String, List<String>> var2);
}
