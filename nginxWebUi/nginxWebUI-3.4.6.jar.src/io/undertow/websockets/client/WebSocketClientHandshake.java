/*    */ package io.undertow.websockets.client;
/*    */ 
/*    */ import io.undertow.connector.ByteBufferPool;
/*    */ import io.undertow.websockets.core.WebSocketChannel;
/*    */ import io.undertow.websockets.core.WebSocketVersion;
/*    */ import io.undertow.websockets.extensions.ExtensionHandshake;
/*    */ import java.net.URI;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ import org.xnio.OptionMap;
/*    */ import org.xnio.StreamConnection;
/*    */ import org.xnio.http.ExtendedHandshakeChecker;
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
/*    */ public abstract class WebSocketClientHandshake
/*    */ {
/*    */   protected final URI url;
/*    */   
/*    */   public static WebSocketClientHandshake create(WebSocketVersion version, URI uri) {
/* 42 */     return create(version, uri, null, null);
/*    */   }
/*    */   
/*    */   public static WebSocketClientHandshake create(WebSocketVersion version, URI uri, WebSocketClientNegotiation clientNegotiation, Set<ExtensionHandshake> extensions) {
/* 46 */     switch (version) {
/*    */       case V13:
/* 48 */         return new WebSocket13ClientHandshake(uri, clientNegotiation, extensions);
/*    */     } 
/* 50 */     throw new IllegalArgumentException();
/*    */   }
/*    */   
/*    */   public WebSocketClientHandshake(URI url) {
/* 54 */     this.url = url;
/*    */   }
/*    */   
/*    */   public abstract WebSocketChannel createChannel(StreamConnection paramStreamConnection, String paramString, ByteBufferPool paramByteBufferPool, OptionMap paramOptionMap);
/*    */   
/*    */   public abstract Map<String, String> createHeaders();
/*    */   
/*    */   public abstract ExtendedHandshakeChecker handshakeChecker(URI paramURI, Map<String, List<String>> paramMap);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\client\WebSocketClientHandshake.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */