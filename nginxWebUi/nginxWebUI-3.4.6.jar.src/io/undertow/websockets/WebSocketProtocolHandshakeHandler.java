/*     */ package io.undertow.websockets;
/*     */ 
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.HttpUpgradeListener;
/*     */ import io.undertow.server.handlers.ResponseCodeHandler;
/*     */ import io.undertow.util.Methods;
/*     */ import io.undertow.websockets.core.WebSocketChannel;
/*     */ import io.undertow.websockets.core.WebSocketLogger;
/*     */ import io.undertow.websockets.core.protocol.Handshake;
/*     */ import io.undertow.websockets.core.protocol.version07.Hybi07Handshake;
/*     */ import io.undertow.websockets.core.protocol.version08.Hybi08Handshake;
/*     */ import io.undertow.websockets.core.protocol.version13.Hybi13Handshake;
/*     */ import io.undertow.websockets.extensions.ExtensionHandshake;
/*     */ import io.undertow.websockets.spi.AsyncWebSocketHttpServerExchange;
/*     */ import io.undertow.websockets.spi.WebSocketHttpExchange;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.xnio.StreamConnection;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WebSocketProtocolHandshakeHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   private final Set<Handshake> handshakes;
/*     */   private final HttpUpgradeListener upgradeListener;
/*     */   private final WebSocketConnectionCallback callback;
/*  59 */   private final Set<WebSocketChannel> peerConnections = Collections.newSetFromMap(new ConcurrentHashMap<>());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final HttpHandler next;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSocketProtocolHandshakeHandler(WebSocketConnectionCallback callback) {
/*  73 */     this(callback, (HttpHandler)ResponseCodeHandler.HANDLE_404);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSocketProtocolHandshakeHandler(WebSocketConnectionCallback callback, HttpHandler next) {
/*  83 */     this.callback = callback;
/*  84 */     Set<Handshake> handshakes = new HashSet<>();
/*  85 */     handshakes.add(new Hybi13Handshake());
/*  86 */     handshakes.add(new Hybi08Handshake());
/*  87 */     handshakes.add(new Hybi07Handshake());
/*  88 */     this.handshakes = handshakes;
/*  89 */     this.next = next;
/*  90 */     this.upgradeListener = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSocketProtocolHandshakeHandler(Collection<Handshake> handshakes, WebSocketConnectionCallback callback) {
/* 101 */     this(handshakes, callback, (HttpHandler)ResponseCodeHandler.HANDLE_404);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSocketProtocolHandshakeHandler(Collection<Handshake> handshakes, WebSocketConnectionCallback callback, HttpHandler next) {
/* 112 */     this.callback = callback;
/* 113 */     this.handshakes = new HashSet<>(handshakes);
/* 114 */     this.next = next;
/* 115 */     this.upgradeListener = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSocketProtocolHandshakeHandler(HttpUpgradeListener callback) {
/* 125 */     this(callback, (HttpHandler)ResponseCodeHandler.HANDLE_404);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSocketProtocolHandshakeHandler(HttpUpgradeListener callback, HttpHandler next) {
/* 135 */     this.callback = null;
/* 136 */     Set<Handshake> handshakes = new HashSet<>();
/* 137 */     handshakes.add(new Hybi13Handshake());
/* 138 */     handshakes.add(new Hybi08Handshake());
/* 139 */     handshakes.add(new Hybi07Handshake());
/* 140 */     this.handshakes = handshakes;
/* 141 */     this.next = next;
/* 142 */     this.upgradeListener = callback;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSocketProtocolHandshakeHandler(Collection<Handshake> handshakes, HttpUpgradeListener callback) {
/* 154 */     this(handshakes, callback, (HttpHandler)ResponseCodeHandler.HANDLE_404);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSocketProtocolHandshakeHandler(Collection<Handshake> handshakes, HttpUpgradeListener callback, HttpHandler next) {
/* 165 */     this.callback = null;
/* 166 */     this.handshakes = new HashSet<>(handshakes);
/* 167 */     this.next = next;
/* 168 */     this.upgradeListener = callback;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 173 */     if (!exchange.getRequestMethod().equals(Methods.GET)) {
/*     */       
/* 175 */       this.next.handleRequest(exchange);
/*     */       return;
/*     */     } 
/* 178 */     final AsyncWebSocketHttpServerExchange facade = new AsyncWebSocketHttpServerExchange(exchange, this.peerConnections);
/* 179 */     Handshake handshaker = null;
/* 180 */     for (Handshake method : this.handshakes) {
/* 181 */       if (method.matches((WebSocketHttpExchange)facade)) {
/* 182 */         handshaker = method;
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/* 187 */     if (handshaker == null) {
/* 188 */       this.next.handleRequest(exchange);
/*     */     } else {
/* 190 */       WebSocketLogger.REQUEST_LOGGER.debugf("Attempting websocket handshake with %s on %s", handshaker, exchange);
/* 191 */       final Handshake selected = handshaker;
/* 192 */       if (this.upgradeListener == null) {
/* 193 */         exchange.upgradeChannel(new HttpUpgradeListener()
/*     */             {
/*     */               public void handleUpgrade(StreamConnection streamConnection, HttpServerExchange exchange) {
/* 196 */                 WebSocketChannel channel = selected.createChannel((WebSocketHttpExchange)facade, streamConnection, facade.getBufferPool());
/* 197 */                 WebSocketProtocolHandshakeHandler.this.peerConnections.add(channel);
/* 198 */                 WebSocketProtocolHandshakeHandler.this.callback.onConnect((WebSocketHttpExchange)facade, channel);
/*     */               }
/*     */             });
/*     */       } else {
/* 202 */         exchange.upgradeChannel(this.upgradeListener);
/*     */       } 
/* 204 */       handshaker.handshake((WebSocketHttpExchange)facade);
/*     */     } 
/*     */   }
/*     */   
/*     */   public Set<WebSocketChannel> getPeerConnections() {
/* 209 */     return this.peerConnections;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSocketProtocolHandshakeHandler addExtension(ExtensionHandshake extension) {
/* 219 */     if (extension != null) {
/* 220 */       for (Handshake handshake : this.handshakes) {
/* 221 */         handshake.addExtension(extension);
/*     */       }
/*     */     }
/* 224 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\WebSocketProtocolHandshakeHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */