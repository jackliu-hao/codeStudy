/*     */ package io.undertow.websockets.core.protocol.version07;
/*     */ 
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.websockets.core.WebSocketChannel;
/*     */ import io.undertow.websockets.core.WebSocketVersion;
/*     */ import io.undertow.websockets.core.protocol.Handshake;
/*     */ import io.undertow.websockets.extensions.CompositeExtensionFunction;
/*     */ import io.undertow.websockets.extensions.ExtensionFunction;
/*     */ import io.undertow.websockets.spi.WebSocketHttpExchange;
/*     */ import java.io.Closeable;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.xnio.IoUtils;
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
/*     */ public class Hybi07Handshake
/*     */   extends Handshake
/*     */ {
/*     */   public static final String MAGIC_NUMBER = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
/*     */   
/*     */   protected Hybi07Handshake(WebSocketVersion version, Set<String> subprotocols, boolean allowExtensions) {
/*  50 */     super(version, "SHA1", "258EAFA5-E914-47DA-95CA-C5AB0DC85B11", subprotocols);
/*  51 */     this.allowExtensions = allowExtensions;
/*     */   }
/*     */   
/*     */   public Hybi07Handshake(Set<String> subprotocols, boolean allowExtensions) {
/*  55 */     this(WebSocketVersion.V07, subprotocols, allowExtensions);
/*     */   }
/*     */   
/*     */   public Hybi07Handshake() {
/*  59 */     this(WebSocketVersion.V07, Collections.emptySet(), false);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean matches(WebSocketHttpExchange exchange) {
/*  64 */     if (exchange.getRequestHeader("Sec-WebSocket-Key") != null && exchange
/*  65 */       .getRequestHeader("Sec-WebSocket-Version") != null) {
/*  66 */       return exchange.getRequestHeader("Sec-WebSocket-Version")
/*  67 */         .equals(getVersion().toHttpHeaderValue());
/*     */     }
/*  69 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void handshakeInternal(WebSocketHttpExchange exchange) {
/*  74 */     String origin = exchange.getRequestHeader("Sec-WebSocket-Origin");
/*  75 */     if (origin != null) {
/*  76 */       exchange.setResponseHeader("Sec-WebSocket-Origin", origin);
/*     */     }
/*  78 */     selectSubprotocol(exchange);
/*  79 */     selectExtensions(exchange);
/*  80 */     exchange.setResponseHeader("Sec-WebSocket-Location", getWebSocketLocation(exchange));
/*     */     
/*  82 */     String key = exchange.getRequestHeader("Sec-WebSocket-Key");
/*     */     try {
/*  84 */       String solution = solve(key);
/*  85 */       exchange.setResponseHeader("Sec-WebSocket-Accept", solution);
/*  86 */       performUpgrade(exchange);
/*  87 */     } catch (NoSuchAlgorithmException e) {
/*  88 */       IoUtils.safeClose((Closeable)exchange);
/*  89 */       exchange.endExchange();
/*     */       return;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected final String solve(String nonceBase64) throws NoSuchAlgorithmException {
/*  96 */     String concat = nonceBase64.trim() + getMagicNumber();
/*  97 */     MessageDigest digest = MessageDigest.getInstance(getHashAlgorithm());
/*  98 */     digest.update(concat.getBytes(StandardCharsets.UTF_8));
/*  99 */     return Base64.encodeBytes(digest.digest()).trim();
/*     */   }
/*     */ 
/*     */   
/*     */   public WebSocketChannel createChannel(WebSocketHttpExchange exchange, StreamConnection channel, ByteBufferPool pool) {
/* 104 */     List<ExtensionFunction> extensionFunctions = initExtensions(exchange);
/* 105 */     return new WebSocket07Channel(channel, pool, getWebSocketLocation(exchange), exchange.getResponseHeader("Sec-WebSocket-Protocol"), false, !extensionFunctions.isEmpty(), CompositeExtensionFunction.compose(extensionFunctions), exchange.getPeerConnections(), exchange.getOptions());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\protocol\version07\Hybi07Handshake.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */