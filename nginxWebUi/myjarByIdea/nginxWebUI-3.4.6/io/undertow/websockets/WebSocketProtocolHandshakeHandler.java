package io.undertow.websockets;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.HttpUpgradeListener;
import io.undertow.server.handlers.ResponseCodeHandler;
import io.undertow.util.Methods;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSocketLogger;
import io.undertow.websockets.core.protocol.Handshake;
import io.undertow.websockets.core.protocol.version07.Hybi07Handshake;
import io.undertow.websockets.core.protocol.version08.Hybi08Handshake;
import io.undertow.websockets.core.protocol.version13.Hybi13Handshake;
import io.undertow.websockets.extensions.ExtensionHandshake;
import io.undertow.websockets.spi.AsyncWebSocketHttpServerExchange;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.xnio.StreamConnection;

public class WebSocketProtocolHandshakeHandler implements HttpHandler {
   private final Set<Handshake> handshakes;
   private final HttpUpgradeListener upgradeListener;
   private final WebSocketConnectionCallback callback;
   private final Set<WebSocketChannel> peerConnections;
   private final HttpHandler next;

   public WebSocketProtocolHandshakeHandler(WebSocketConnectionCallback callback) {
      this((WebSocketConnectionCallback)callback, (HttpHandler)ResponseCodeHandler.HANDLE_404);
   }

   public WebSocketProtocolHandshakeHandler(WebSocketConnectionCallback callback, HttpHandler next) {
      this.peerConnections = Collections.newSetFromMap(new ConcurrentHashMap());
      this.callback = callback;
      Set<Handshake> handshakes = new HashSet();
      handshakes.add(new Hybi13Handshake());
      handshakes.add(new Hybi08Handshake());
      handshakes.add(new Hybi07Handshake());
      this.handshakes = handshakes;
      this.next = next;
      this.upgradeListener = null;
   }

   public WebSocketProtocolHandshakeHandler(Collection<Handshake> handshakes, WebSocketConnectionCallback callback) {
      this(handshakes, (WebSocketConnectionCallback)callback, ResponseCodeHandler.HANDLE_404);
   }

   public WebSocketProtocolHandshakeHandler(Collection<Handshake> handshakes, WebSocketConnectionCallback callback, HttpHandler next) {
      this.peerConnections = Collections.newSetFromMap(new ConcurrentHashMap());
      this.callback = callback;
      this.handshakes = new HashSet(handshakes);
      this.next = next;
      this.upgradeListener = null;
   }

   public WebSocketProtocolHandshakeHandler(HttpUpgradeListener callback) {
      this((HttpUpgradeListener)callback, (HttpHandler)ResponseCodeHandler.HANDLE_404);
   }

   public WebSocketProtocolHandshakeHandler(HttpUpgradeListener callback, HttpHandler next) {
      this.peerConnections = Collections.newSetFromMap(new ConcurrentHashMap());
      this.callback = null;
      Set<Handshake> handshakes = new HashSet();
      handshakes.add(new Hybi13Handshake());
      handshakes.add(new Hybi08Handshake());
      handshakes.add(new Hybi07Handshake());
      this.handshakes = handshakes;
      this.next = next;
      this.upgradeListener = callback;
   }

   public WebSocketProtocolHandshakeHandler(Collection<Handshake> handshakes, HttpUpgradeListener callback) {
      this(handshakes, (HttpUpgradeListener)callback, ResponseCodeHandler.HANDLE_404);
   }

   public WebSocketProtocolHandshakeHandler(Collection<Handshake> handshakes, HttpUpgradeListener callback, HttpHandler next) {
      this.peerConnections = Collections.newSetFromMap(new ConcurrentHashMap());
      this.callback = null;
      this.handshakes = new HashSet(handshakes);
      this.next = next;
      this.upgradeListener = callback;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      if (!exchange.getRequestMethod().equals(Methods.GET)) {
         this.next.handleRequest(exchange);
      } else {
         final AsyncWebSocketHttpServerExchange facade = new AsyncWebSocketHttpServerExchange(exchange, this.peerConnections);
         final Handshake handshaker = null;
         Iterator var4 = this.handshakes.iterator();

         while(var4.hasNext()) {
            Handshake method = (Handshake)var4.next();
            if (method.matches(facade)) {
               handshaker = method;
               break;
            }
         }

         if (handshaker == null) {
            this.next.handleRequest(exchange);
         } else {
            WebSocketLogger.REQUEST_LOGGER.debugf("Attempting websocket handshake with %s on %s", handshaker, exchange);
            if (this.upgradeListener == null) {
               exchange.upgradeChannel(new HttpUpgradeListener() {
                  public void handleUpgrade(StreamConnection streamConnection, HttpServerExchange exchange) {
                     WebSocketChannel channel = handshaker.createChannel(facade, streamConnection, facade.getBufferPool());
                     WebSocketProtocolHandshakeHandler.this.peerConnections.add(channel);
                     WebSocketProtocolHandshakeHandler.this.callback.onConnect(facade, channel);
                  }
               });
            } else {
               exchange.upgradeChannel(this.upgradeListener);
            }

            handshaker.handshake(facade);
         }

      }
   }

   public Set<WebSocketChannel> getPeerConnections() {
      return this.peerConnections;
   }

   public WebSocketProtocolHandshakeHandler addExtension(ExtensionHandshake extension) {
      if (extension != null) {
         Iterator var2 = this.handshakes.iterator();

         while(var2.hasNext()) {
            Handshake handshake = (Handshake)var2.next();
            handshake.addExtension(extension);
         }
      }

      return this;
   }
}
