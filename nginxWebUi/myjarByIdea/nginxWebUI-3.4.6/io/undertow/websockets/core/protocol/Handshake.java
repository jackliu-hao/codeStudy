package io.undertow.websockets.core.protocol;

import io.undertow.connector.ByteBufferPool;
import io.undertow.websockets.WebSocketExtension;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSocketVersion;
import io.undertow.websockets.extensions.ExtensionFunction;
import io.undertow.websockets.extensions.ExtensionHandshake;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.xnio.IoFuture;
import org.xnio.StreamConnection;

public abstract class Handshake {
   private final WebSocketVersion version;
   private final String hashAlgorithm;
   private final String magicNumber;
   protected final Set<String> subprotocols;
   private static final byte[] EMPTY = new byte[0];
   private static final Pattern PATTERN = Pattern.compile("\\s*,\\s*");
   protected Set<ExtensionHandshake> availableExtensions = new HashSet();
   protected boolean allowExtensions;

   protected Handshake(WebSocketVersion version, String hashAlgorithm, String magicNumber, Set<String> subprotocols) {
      this.version = version;
      this.hashAlgorithm = hashAlgorithm;
      this.magicNumber = magicNumber;
      this.subprotocols = subprotocols;
   }

   public WebSocketVersion getVersion() {
      return this.version;
   }

   public String getHashAlgorithm() {
      return this.hashAlgorithm;
   }

   public String getMagicNumber() {
      return this.magicNumber;
   }

   protected static String getWebSocketLocation(WebSocketHttpExchange exchange) {
      String scheme;
      if ("https".equals(exchange.getRequestScheme())) {
         scheme = "wss";
      } else {
         scheme = "ws";
      }

      return scheme + "://" + exchange.getRequestHeader("Host") + exchange.getRequestURI();
   }

   public final void handshake(WebSocketHttpExchange exchange) {
      exchange.putAttachment(WebSocketVersion.ATTACHMENT_KEY, this.version);
      this.handshakeInternal(exchange);
   }

   protected abstract void handshakeInternal(WebSocketHttpExchange var1);

   public abstract boolean matches(WebSocketHttpExchange var1);

   public abstract WebSocketChannel createChannel(WebSocketHttpExchange var1, StreamConnection var2, ByteBufferPool var3);

   protected final void performUpgrade(WebSocketHttpExchange exchange, byte[] data) {
      exchange.setResponseHeader("Content-Length", String.valueOf(data.length));
      exchange.setResponseHeader("Upgrade", "WebSocket");
      exchange.setResponseHeader("Connection", "Upgrade");
      this.upgradeChannel(exchange, data);
   }

   protected void upgradeChannel(WebSocketHttpExchange exchange, byte[] data) {
      if (data.length > 0) {
         writePayload(exchange, ByteBuffer.wrap(data));
      } else {
         exchange.endExchange();
      }

   }

   private static void writePayload(final WebSocketHttpExchange exchange, ByteBuffer payload) {
      exchange.sendData(payload).addNotifier(new IoFuture.Notifier<Void, Object>() {
         public void notify(IoFuture<? extends Void> ioFuture, Object attachment) {
            if (ioFuture.getStatus() == IoFuture.Status.DONE) {
               exchange.endExchange();
            } else {
               exchange.close();
            }

         }
      }, (Object)null);
   }

   protected final void performUpgrade(WebSocketHttpExchange exchange) {
      this.performUpgrade(exchange, EMPTY);
   }

   protected final void selectSubprotocol(WebSocketHttpExchange exchange) {
      String requestedSubprotocols = exchange.getRequestHeader("Sec-WebSocket-Protocol");
      if (requestedSubprotocols != null) {
         String[] requestedSubprotocolArray = PATTERN.split(requestedSubprotocols);
         String subProtocol = this.supportedSubprotols(requestedSubprotocolArray);
         if (subProtocol != null && !subProtocol.isEmpty()) {
            exchange.setResponseHeader("Sec-WebSocket-Protocol", subProtocol);
         }

      }
   }

   protected final void selectExtensions(WebSocketHttpExchange exchange) {
      List<WebSocketExtension> requestedExtensions = WebSocketExtension.parse(exchange.getRequestHeader("Sec-WebSocket-Extensions"));
      List<WebSocketExtension> extensions = this.selectedExtension(requestedExtensions);
      if (extensions != null && !extensions.isEmpty()) {
         exchange.setResponseHeader("Sec-WebSocket-Extensions", WebSocketExtension.toExtensionHeader(extensions));
      }

   }

   protected String supportedSubprotols(String[] requestedSubprotocolArray) {
      String[] var2 = requestedSubprotocolArray;
      int var3 = requestedSubprotocolArray.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String p = var2[var4];
         String requestedSubprotocol = p.trim();
         Iterator var7 = this.subprotocols.iterator();

         while(var7.hasNext()) {
            String supportedSubprotocol = (String)var7.next();
            if (requestedSubprotocol.equals(supportedSubprotocol)) {
               return supportedSubprotocol;
            }
         }
      }

      return null;
   }

   protected List<WebSocketExtension> selectedExtension(List<WebSocketExtension> extensionList) {
      List<WebSocketExtension> selected = new ArrayList();
      List<ExtensionHandshake> configured = new ArrayList();
      Iterator var4 = extensionList.iterator();

      while(var4.hasNext()) {
         WebSocketExtension ext = (WebSocketExtension)var4.next();
         Iterator var6 = this.availableExtensions.iterator();

         while(var6.hasNext()) {
            ExtensionHandshake extHandshake = (ExtensionHandshake)var6.next();
            WebSocketExtension negotiated = extHandshake.accept(ext);
            if (negotiated != null && !extHandshake.isIncompatible(configured)) {
               selected.add(negotiated);
               configured.add(extHandshake);
            }
         }
      }

      return selected;
   }

   public final void addExtension(ExtensionHandshake extension) {
      this.availableExtensions.add(extension);
      this.allowExtensions = true;
   }

   protected final List<ExtensionFunction> initExtensions(WebSocketHttpExchange exchange) {
      String extHeader = exchange.getResponseHeaders().get("Sec-WebSocket-Extensions") != null ? (String)((List)exchange.getResponseHeaders().get("Sec-WebSocket-Extensions")).get(0) : null;
      List<ExtensionFunction> negotiated = new ArrayList();
      if (extHeader != null) {
         List<WebSocketExtension> extensions = WebSocketExtension.parse(extHeader);
         if (extensions != null && !extensions.isEmpty()) {
            Iterator var5 = extensions.iterator();

            while(var5.hasNext()) {
               WebSocketExtension ext = (WebSocketExtension)var5.next();
               Iterator var7 = this.availableExtensions.iterator();

               while(var7.hasNext()) {
                  ExtensionHandshake extHandshake = (ExtensionHandshake)var7.next();
                  if (extHandshake.getName().equals(ext.getName())) {
                     negotiated.add(extHandshake.create());
                  }
               }
            }
         }
      }

      return negotiated;
   }
}
