package io.undertow.websockets.client;

import io.undertow.connector.ByteBufferPool;
import io.undertow.util.FlexBase64;
import io.undertow.websockets.WebSocketExtension;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSocketMessages;
import io.undertow.websockets.core.WebSocketVersion;
import io.undertow.websockets.core.protocol.version13.WebSocket13Channel;
import io.undertow.websockets.extensions.CompositeExtensionFunction;
import io.undertow.websockets.extensions.ExtensionFunction;
import io.undertow.websockets.extensions.ExtensionHandshake;
import io.undertow.websockets.extensions.NoopExtensionFunction;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.xnio.OptionMap;
import org.xnio.StreamConnection;
import org.xnio.http.ExtendedHandshakeChecker;

public class WebSocket13ClientHandshake extends WebSocketClientHandshake {
   public static final String MAGIC_NUMBER = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
   private final WebSocketClientNegotiation negotiation;
   private final Set<ExtensionHandshake> extensions;

   public WebSocket13ClientHandshake(URI url, WebSocketClientNegotiation negotiation, Set<ExtensionHandshake> extensions) {
      super(url);
      this.negotiation = negotiation;
      this.extensions = extensions == null ? Collections.emptySet() : extensions;
   }

   public WebSocket13ClientHandshake(URI url) {
      this(url, (WebSocketClientNegotiation)null, (Set)null);
   }

   public WebSocketChannel createChannel(StreamConnection channel, String wsUri, ByteBufferPool bufferPool, OptionMap options) {
      if (this.negotiation != null && this.negotiation.getSelectedExtensions() != null && !this.negotiation.getSelectedExtensions().isEmpty()) {
         List<WebSocketExtension> selected = this.negotiation.getSelectedExtensions();
         List<ExtensionFunction> negotiated = new ArrayList();
         if (selected != null && !selected.isEmpty()) {
            Iterator var7 = selected.iterator();

            while(var7.hasNext()) {
               WebSocketExtension ext = (WebSocketExtension)var7.next();
               Iterator var9 = this.extensions.iterator();

               while(var9.hasNext()) {
                  ExtensionHandshake extHandshake = (ExtensionHandshake)var9.next();
                  if (ext.getName().equals(extHandshake.getName())) {
                     negotiated.add(extHandshake.create());
                  }
               }
            }
         }

         return new WebSocket13Channel(channel, bufferPool, wsUri, this.negotiation.getSelectedSubProtocol(), true, !negotiated.isEmpty(), CompositeExtensionFunction.compose((List)negotiated), new HashSet(), options);
      } else {
         return new WebSocket13Channel(channel, bufferPool, wsUri, this.negotiation != null ? this.negotiation.getSelectedSubProtocol() : "", true, false, NoopExtensionFunction.INSTANCE, new HashSet(), options);
      }
   }

   public Map<String, String> createHeaders() {
      Map<String, String> headers = new HashMap();
      headers.put("Upgrade", "websocket");
      headers.put("Connection", "upgrade");
      String key = this.createSecKey();
      headers.put("Sec-WebSocket-Key", key);
      headers.put("Sec-WebSocket-Version", this.getVersion().toHttpHeaderValue());
      if (this.negotiation != null) {
         List<String> subProtocols = this.negotiation.getSupportedSubProtocols();
         if (subProtocols != null && !subProtocols.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            Iterator<String> it = subProtocols.iterator();

            while(it.hasNext()) {
               sb.append((String)it.next());
               if (it.hasNext()) {
                  sb.append(", ");
               }
            }

            headers.put("Sec-WebSocket-Protocol", sb.toString());
         }

         List<WebSocketExtension> extensions = this.negotiation.getSupportedExtensions();
         if (extensions != null && !extensions.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            Iterator<WebSocketExtension> it = extensions.iterator();

            while(it.hasNext()) {
               WebSocketExtension next = (WebSocketExtension)it.next();
               sb.append(next.getName());
               Iterator var8 = next.getParameters().iterator();

               while(var8.hasNext()) {
                  WebSocketExtension.Parameter param = (WebSocketExtension.Parameter)var8.next();
                  sb.append("; ");
                  sb.append(param.getName());
                  if (param.getValue() != null && param.getValue().length() > 0) {
                     sb.append("=");
                     sb.append(param.getValue());
                  }
               }

               if (it.hasNext()) {
                  sb.append(", ");
               }
            }

            headers.put("Sec-WebSocket-Extensions", sb.toString());
         }
      }

      return headers;
   }

   protected String createSecKey() {
      SecureRandom random = new SecureRandom();
      byte[] data = new byte[16];

      for(int i = 0; i < 4; ++i) {
         int val = random.nextInt();
         data[i * 4] = (byte)val;
         data[i * 4 + 1] = (byte)(val >> 8 & 255);
         data[i * 4 + 2] = (byte)(val >> 16 & 255);
         data[i * 4 + 3] = (byte)(val >> 24 & 255);
      }

      return FlexBase64.encodeString(data, false);
   }

   public ExtendedHandshakeChecker handshakeChecker(URI uri, Map<String, List<String>> requestHeaders) {
      final String sentKey = requestHeaders.containsKey("Sec-WebSocket-Key") ? (String)((List)requestHeaders.get("Sec-WebSocket-Key")).get(0) : null;
      return new ExtendedHandshakeChecker() {
         public void checkHandshakeExtended(Map<String, List<String>> headers) throws IOException {
            try {
               if (WebSocket13ClientHandshake.this.negotiation != null) {
                  WebSocket13ClientHandshake.this.negotiation.afterRequest(headers);
               }

               String upgrade = WebSocket13ClientHandshake.this.getFirst("Upgrade", headers);
               if (upgrade != null && upgrade.trim().equalsIgnoreCase("websocket")) {
                  String connHeader = WebSocket13ClientHandshake.this.getFirst("Connection", headers);
                  if (connHeader != null && connHeader.trim().equalsIgnoreCase("upgrade")) {
                     String acceptKey = WebSocket13ClientHandshake.this.getFirst("Sec-WebSocket-Accept", headers);
                     String dKey = WebSocket13ClientHandshake.this.solve(sentKey);
                     if (!dKey.equals(acceptKey)) {
                        throw WebSocketMessages.MESSAGES.webSocketAcceptKeyMismatch(dKey, acceptKey);
                     } else {
                        if (WebSocket13ClientHandshake.this.negotiation != null) {
                           String subProto = WebSocket13ClientHandshake.this.getFirst("Sec-WebSocket-Protocol", headers);
                           if (subProto != null && !subProto.isEmpty() && !WebSocket13ClientHandshake.this.negotiation.getSupportedSubProtocols().contains(subProto)) {
                              throw WebSocketMessages.MESSAGES.unsupportedProtocol(subProto, WebSocket13ClientHandshake.this.negotiation.getSupportedSubProtocols());
                           }

                           List<WebSocketExtension> extensions = Collections.emptyList();
                           String extHeader = WebSocket13ClientHandshake.this.getFirst("Sec-WebSocket-Extensions", headers);
                           if (extHeader != null) {
                              extensions = WebSocketExtension.parse(extHeader);
                           }

                           WebSocket13ClientHandshake.this.negotiation.handshakeComplete(subProto, extensions);
                        }

                     }
                  } else {
                     throw WebSocketMessages.MESSAGES.noWebSocketConnectionHeader();
                  }
               } else {
                  throw WebSocketMessages.MESSAGES.noWebSocketUpgradeHeader();
               }
            } catch (IOException var9) {
               throw var9;
            } catch (Exception var10) {
               throw new IOException(var10);
            }
         }
      };
   }

   private String getFirst(String key, Map<String, List<String>> map) {
      List<String> list = (List)map.get(key.toLowerCase(Locale.ENGLISH));
      return list != null && !list.isEmpty() ? (String)list.get(0) : null;
   }

   protected final String solve(String nonceBase64) {
      try {
         String concat = nonceBase64 + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
         MessageDigest digest = MessageDigest.getInstance("SHA1");
         digest.update(concat.getBytes(StandardCharsets.UTF_8));
         byte[] bytes = digest.digest();
         return FlexBase64.encodeString(bytes, false);
      } catch (NoSuchAlgorithmException var5) {
         throw new RuntimeException(var5);
      }
   }

   public WebSocketVersion getVersion() {
      return WebSocketVersion.V13;
   }
}
