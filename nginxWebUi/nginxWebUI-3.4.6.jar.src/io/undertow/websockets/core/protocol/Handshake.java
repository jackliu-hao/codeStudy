/*     */ package io.undertow.websockets.core.protocol;
/*     */ 
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.websockets.WebSocketExtension;
/*     */ import io.undertow.websockets.core.WebSocketChannel;
/*     */ import io.undertow.websockets.core.WebSocketVersion;
/*     */ import io.undertow.websockets.extensions.ExtensionFunction;
/*     */ import io.undertow.websockets.extensions.ExtensionHandshake;
/*     */ import io.undertow.websockets.spi.WebSocketHttpExchange;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.regex.Pattern;
/*     */ import org.xnio.IoFuture;
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
/*     */ public abstract class Handshake
/*     */ {
/*     */   private final WebSocketVersion version;
/*     */   private final String hashAlgorithm;
/*     */   private final String magicNumber;
/*     */   protected final Set<String> subprotocols;
/*  49 */   private static final byte[] EMPTY = new byte[0];
/*  50 */   private static final Pattern PATTERN = Pattern.compile("\\s*,\\s*");
/*  51 */   protected Set<ExtensionHandshake> availableExtensions = new HashSet<>();
/*     */   protected boolean allowExtensions;
/*     */   
/*     */   protected Handshake(WebSocketVersion version, String hashAlgorithm, String magicNumber, Set<String> subprotocols) {
/*  55 */     this.version = version;
/*  56 */     this.hashAlgorithm = hashAlgorithm;
/*  57 */     this.magicNumber = magicNumber;
/*  58 */     this.subprotocols = subprotocols;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSocketVersion getVersion() {
/*  65 */     return this.version;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getHashAlgorithm() {
/*  72 */     return this.hashAlgorithm;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getMagicNumber() {
/*  79 */     return this.magicNumber;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static String getWebSocketLocation(WebSocketHttpExchange exchange) {
/*     */     String scheme;
/*  87 */     if ("https".equals(exchange.getRequestScheme())) {
/*  88 */       scheme = "wss";
/*     */     } else {
/*  90 */       scheme = "ws";
/*     */     } 
/*  92 */     return scheme + "://" + exchange.getRequestHeader("Host") + exchange.getRequestURI();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void handshake(WebSocketHttpExchange exchange) {
/* 101 */     exchange.putAttachment(WebSocketVersion.ATTACHMENT_KEY, this.version);
/* 102 */     handshakeInternal(exchange);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void handshakeInternal(WebSocketHttpExchange paramWebSocketHttpExchange);
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract boolean matches(WebSocketHttpExchange paramWebSocketHttpExchange);
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract WebSocketChannel createChannel(WebSocketHttpExchange paramWebSocketHttpExchange, StreamConnection paramStreamConnection, ByteBufferPool paramByteBufferPool);
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void performUpgrade(WebSocketHttpExchange exchange, byte[] data) {
/* 121 */     exchange.setResponseHeader("Content-Length", String.valueOf(data.length));
/* 122 */     exchange.setResponseHeader("Upgrade", "WebSocket");
/* 123 */     exchange.setResponseHeader("Connection", "Upgrade");
/* 124 */     upgradeChannel(exchange, data);
/*     */   }
/*     */   
/*     */   protected void upgradeChannel(WebSocketHttpExchange exchange, byte[] data) {
/* 128 */     if (data.length > 0) {
/* 129 */       writePayload(exchange, ByteBuffer.wrap(data));
/*     */     } else {
/* 131 */       exchange.endExchange();
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void writePayload(final WebSocketHttpExchange exchange, ByteBuffer payload) {
/* 136 */     exchange.sendData(payload).addNotifier(new IoFuture.Notifier<Void, Object>()
/*     */         {
/*     */           public void notify(IoFuture<? extends Void> ioFuture, Object attachment) {
/* 139 */             if (ioFuture.getStatus() == IoFuture.Status.DONE) {
/* 140 */               exchange.endExchange();
/*     */             } else {
/* 142 */               exchange.close();
/*     */             } 
/*     */           }
/*     */         },  null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void performUpgrade(WebSocketHttpExchange exchange) {
/* 152 */     performUpgrade(exchange, EMPTY);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void selectSubprotocol(WebSocketHttpExchange exchange) {
/* 160 */     String requestedSubprotocols = exchange.getRequestHeader("Sec-WebSocket-Protocol");
/* 161 */     if (requestedSubprotocols == null) {
/*     */       return;
/*     */     }
/*     */     
/* 165 */     String[] requestedSubprotocolArray = PATTERN.split(requestedSubprotocols);
/* 166 */     String subProtocol = supportedSubprotols(requestedSubprotocolArray);
/* 167 */     if (subProtocol != null && !subProtocol.isEmpty()) {
/* 168 */       exchange.setResponseHeader("Sec-WebSocket-Protocol", subProtocol);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void selectExtensions(WebSocketHttpExchange exchange) {
/* 175 */     List<WebSocketExtension> requestedExtensions = WebSocketExtension.parse(exchange.getRequestHeader("Sec-WebSocket-Extensions"));
/* 176 */     List<WebSocketExtension> extensions = selectedExtension(requestedExtensions);
/* 177 */     if (extensions != null && !extensions.isEmpty()) {
/* 178 */       exchange.setResponseHeader("Sec-WebSocket-Extensions", WebSocketExtension.toExtensionHeader(extensions));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected String supportedSubprotols(String[] requestedSubprotocolArray) {
/* 184 */     for (String p : requestedSubprotocolArray) {
/* 185 */       String requestedSubprotocol = p.trim();
/*     */       
/* 187 */       for (String supportedSubprotocol : this.subprotocols) {
/* 188 */         if (requestedSubprotocol.equals(supportedSubprotocol)) {
/* 189 */           return supportedSubprotocol;
/*     */         }
/*     */       } 
/*     */     } 
/* 193 */     return null;
/*     */   }
/*     */   
/*     */   protected List<WebSocketExtension> selectedExtension(List<WebSocketExtension> extensionList) {
/* 197 */     List<WebSocketExtension> selected = new ArrayList<>();
/* 198 */     List<ExtensionHandshake> configured = new ArrayList<>();
/* 199 */     for (WebSocketExtension ext : extensionList) {
/* 200 */       for (ExtensionHandshake extHandshake : this.availableExtensions) {
/* 201 */         WebSocketExtension negotiated = extHandshake.accept(ext);
/* 202 */         if (negotiated != null && !extHandshake.isIncompatible(configured)) {
/* 203 */           selected.add(negotiated);
/* 204 */           configured.add(extHandshake);
/*     */         } 
/*     */       } 
/*     */     } 
/* 208 */     return selected;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void addExtension(ExtensionHandshake extension) {
/* 217 */     this.availableExtensions.add(extension);
/* 218 */     this.allowExtensions = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final List<ExtensionFunction> initExtensions(WebSocketHttpExchange exchange) {
/* 229 */     String extHeader = (exchange.getResponseHeaders().get("Sec-WebSocket-Extensions") != null) ? ((List<String>)exchange.getResponseHeaders().get("Sec-WebSocket-Extensions")).get(0) : null;
/*     */     
/* 231 */     List<ExtensionFunction> negotiated = new ArrayList<>();
/* 232 */     if (extHeader != null) {
/* 233 */       List<WebSocketExtension> extensions = WebSocketExtension.parse(extHeader);
/* 234 */       if (extensions != null && !extensions.isEmpty()) {
/* 235 */         for (WebSocketExtension ext : extensions) {
/* 236 */           for (ExtensionHandshake extHandshake : this.availableExtensions) {
/* 237 */             if (extHandshake.getName().equals(ext.getName())) {
/* 238 */               negotiated.add(extHandshake.create());
/*     */             }
/*     */           } 
/*     */         } 
/*     */       }
/*     */     } 
/* 244 */     return negotiated;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\protocol\Handshake.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */