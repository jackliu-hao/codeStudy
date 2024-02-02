/*    */ package io.undertow.websockets.core.protocol.version08;
/*    */ 
/*    */ import io.undertow.connector.ByteBufferPool;
/*    */ import io.undertow.websockets.core.WebSocketChannel;
/*    */ import io.undertow.websockets.core.WebSocketVersion;
/*    */ import io.undertow.websockets.core.protocol.version07.Hybi07Handshake;
/*    */ import io.undertow.websockets.extensions.CompositeExtensionFunction;
/*    */ import io.undertow.websockets.extensions.ExtensionFunction;
/*    */ import io.undertow.websockets.spi.WebSocketHttpExchange;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import java.util.Set;
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
/*    */ 
/*    */ public class Hybi08Handshake
/*    */   extends Hybi07Handshake
/*    */ {
/*    */   public Hybi08Handshake() {
/* 43 */     super(WebSocketVersion.V08, Collections.emptySet(), false);
/*    */   }
/*    */   
/*    */   public Hybi08Handshake(Set<String> subprotocols, boolean allowExtensions) {
/* 47 */     super(WebSocketVersion.V08, subprotocols, allowExtensions);
/*    */   }
/*    */ 
/*    */   
/*    */   public WebSocketChannel createChannel(WebSocketHttpExchange exchange, StreamConnection channel, ByteBufferPool pool) {
/* 52 */     List<ExtensionFunction> extensionFunctions = initExtensions(exchange);
/* 53 */     return (WebSocketChannel)new WebSocket08Channel(channel, pool, getWebSocketLocation(exchange), exchange.getResponseHeader("Sec-WebSocket-Protocol"), false, !extensionFunctions.isEmpty(), CompositeExtensionFunction.compose(extensionFunctions), exchange.getPeerConnections(), exchange.getOptions());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\protocol\version08\Hybi08Handshake.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */