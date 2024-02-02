/*    */ package io.undertow.websockets.core.protocol.version13;
/*    */ 
/*    */ import io.undertow.connector.ByteBufferPool;
/*    */ import io.undertow.websockets.core.WebSocketChannel;
/*    */ import io.undertow.websockets.core.WebSocketVersion;
/*    */ import io.undertow.websockets.core.protocol.version07.Hybi07Handshake;
/*    */ import io.undertow.websockets.extensions.CompositeExtensionFunction;
/*    */ import io.undertow.websockets.extensions.ExtensionFunction;
/*    */ import io.undertow.websockets.spi.WebSocketHttpExchange;
/*    */ import java.io.Closeable;
/*    */ import java.security.NoSuchAlgorithmException;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import java.util.Set;
/*    */ import org.xnio.IoUtils;
/*    */ import org.xnio.StreamConnection;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Hybi13Handshake
/*    */   extends Hybi07Handshake
/*    */ {
/*    */   public Hybi13Handshake() {
/* 45 */     super(WebSocketVersion.V13, Collections.emptySet(), false);
/*    */   }
/*    */   
/*    */   public Hybi13Handshake(Set<String> subprotocols, boolean allowExtensions) {
/* 49 */     super(WebSocketVersion.V13, subprotocols, allowExtensions);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void handshakeInternal(WebSocketHttpExchange exchange) {
/* 54 */     String origin = exchange.getRequestHeader("Origin");
/* 55 */     if (origin != null) {
/* 56 */       exchange.setResponseHeader("Origin", origin);
/*    */     }
/* 58 */     selectSubprotocol(exchange);
/* 59 */     selectExtensions(exchange);
/* 60 */     exchange.setResponseHeader("Sec-WebSocket-Location", getWebSocketLocation(exchange));
/*    */     
/* 62 */     String key = exchange.getRequestHeader("Sec-WebSocket-Key");
/*    */     try {
/* 64 */       String solution = solve(key);
/* 65 */       exchange.setResponseHeader("Sec-WebSocket-Accept", solution);
/* 66 */       performUpgrade(exchange);
/* 67 */     } catch (NoSuchAlgorithmException e) {
/* 68 */       IoUtils.safeClose((Closeable)exchange);
/* 69 */       exchange.endExchange();
/*    */       return;
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public WebSocketChannel createChannel(WebSocketHttpExchange exchange, StreamConnection channel, ByteBufferPool pool) {
/* 76 */     List<ExtensionFunction> extensionFunctions = initExtensions(exchange);
/* 77 */     return (WebSocketChannel)new WebSocket13Channel(channel, pool, getWebSocketLocation(exchange), exchange.getResponseHeader("Sec-WebSocket-Protocol"), false, !extensionFunctions.isEmpty(), CompositeExtensionFunction.compose(extensionFunctions), exchange.getPeerConnections(), exchange.getOptions());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\protocol\version13\Hybi13Handshake.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */