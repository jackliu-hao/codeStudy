/*     */ package io.undertow.websockets.client;
/*     */ 
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.util.FlexBase64;
/*     */ import io.undertow.websockets.WebSocketExtension;
/*     */ import io.undertow.websockets.core.WebSocketChannel;
/*     */ import io.undertow.websockets.core.WebSocketMessages;
/*     */ import io.undertow.websockets.core.WebSocketVersion;
/*     */ import io.undertow.websockets.core.protocol.version13.WebSocket13Channel;
/*     */ import io.undertow.websockets.extensions.CompositeExtensionFunction;
/*     */ import io.undertow.websockets.extensions.ExtensionFunction;
/*     */ import io.undertow.websockets.extensions.ExtensionHandshake;
/*     */ import io.undertow.websockets.extensions.NoopExtensionFunction;
/*     */ import java.io.IOException;
/*     */ import java.net.URI;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.SecureRandom;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.http.ExtendedHandshakeChecker;
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
/*     */ public class WebSocket13ClientHandshake
/*     */   extends WebSocketClientHandshake
/*     */ {
/*     */   public static final String MAGIC_NUMBER = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
/*     */   private final WebSocketClientNegotiation negotiation;
/*     */   private final Set<ExtensionHandshake> extensions;
/*     */   
/*     */   public WebSocket13ClientHandshake(URI url, WebSocketClientNegotiation negotiation, Set<ExtensionHandshake> extensions) {
/*  64 */     super(url);
/*  65 */     this.negotiation = negotiation;
/*  66 */     this.extensions = (extensions == null) ? Collections.<ExtensionHandshake>emptySet() : extensions;
/*     */   }
/*     */   
/*     */   public WebSocket13ClientHandshake(URI url) {
/*  70 */     this(url, null, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public WebSocketChannel createChannel(StreamConnection channel, String wsUri, ByteBufferPool bufferPool, OptionMap options) {
/*  75 */     if (this.negotiation != null && this.negotiation.getSelectedExtensions() != null && !this.negotiation.getSelectedExtensions().isEmpty()) {
/*     */       
/*  77 */       List<WebSocketExtension> selected = this.negotiation.getSelectedExtensions();
/*  78 */       List<ExtensionFunction> negotiated = new ArrayList<>();
/*  79 */       if (selected != null && !selected.isEmpty()) {
/*  80 */         for (WebSocketExtension ext : selected) {
/*  81 */           for (ExtensionHandshake extHandshake : this.extensions) {
/*  82 */             if (ext.getName().equals(extHandshake.getName())) {
/*  83 */               negotiated.add(extHandshake.create());
/*     */             }
/*     */           } 
/*     */         } 
/*     */       }
/*  88 */       return (WebSocketChannel)new WebSocket13Channel(channel, bufferPool, wsUri, this.negotiation.getSelectedSubProtocol(), true, !negotiated.isEmpty(), CompositeExtensionFunction.compose(negotiated), new HashSet(), options);
/*     */     } 
/*  90 */     return (WebSocketChannel)new WebSocket13Channel(channel, bufferPool, wsUri, (this.negotiation != null) ? this.negotiation.getSelectedSubProtocol() : "", true, false, NoopExtensionFunction.INSTANCE, new HashSet(), options);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, String> createHeaders() {
/*  96 */     Map<String, String> headers = new HashMap<>();
/*  97 */     headers.put("Upgrade", "websocket");
/*  98 */     headers.put("Connection", "upgrade");
/*  99 */     String key = createSecKey();
/* 100 */     headers.put("Sec-WebSocket-Key", key);
/* 101 */     headers.put("Sec-WebSocket-Version", getVersion().toHttpHeaderValue());
/* 102 */     if (this.negotiation != null) {
/* 103 */       List<String> subProtocols = this.negotiation.getSupportedSubProtocols();
/* 104 */       if (subProtocols != null && !subProtocols.isEmpty()) {
/* 105 */         StringBuilder sb = new StringBuilder();
/* 106 */         Iterator<String> it = subProtocols.iterator();
/* 107 */         while (it.hasNext()) {
/* 108 */           sb.append(it.next());
/* 109 */           if (it.hasNext()) {
/* 110 */             sb.append(", ");
/*     */           }
/*     */         } 
/* 113 */         headers.put("Sec-WebSocket-Protocol", sb.toString());
/*     */       } 
/* 115 */       List<WebSocketExtension> extensions = this.negotiation.getSupportedExtensions();
/* 116 */       if (extensions != null && !extensions.isEmpty()) {
/* 117 */         StringBuilder sb = new StringBuilder();
/* 118 */         Iterator<WebSocketExtension> it = extensions.iterator();
/* 119 */         while (it.hasNext()) {
/* 120 */           WebSocketExtension next = it.next();
/* 121 */           sb.append(next.getName());
/* 122 */           for (WebSocketExtension.Parameter param : next.getParameters()) {
/* 123 */             sb.append("; ");
/* 124 */             sb.append(param.getName());
/*     */ 
/*     */ 
/*     */             
/* 128 */             if (param.getValue() != null && param.getValue().length() > 0) {
/* 129 */               sb.append("=");
/* 130 */               sb.append(param.getValue());
/*     */             } 
/*     */           } 
/* 133 */           if (it.hasNext()) {
/* 134 */             sb.append(", ");
/*     */           }
/*     */         } 
/* 137 */         headers.put("Sec-WebSocket-Extensions", sb.toString());
/*     */       } 
/*     */     } 
/* 140 */     return headers;
/*     */   }
/*     */ 
/*     */   
/*     */   protected String createSecKey() {
/* 145 */     SecureRandom random = new SecureRandom();
/* 146 */     byte[] data = new byte[16];
/* 147 */     for (int i = 0; i < 4; i++) {
/* 148 */       int val = random.nextInt();
/* 149 */       data[i * 4] = (byte)val;
/* 150 */       data[i * 4 + 1] = (byte)(val >> 8 & 0xFF);
/* 151 */       data[i * 4 + 2] = (byte)(val >> 16 & 0xFF);
/* 152 */       data[i * 4 + 3] = (byte)(val >> 24 & 0xFF);
/*     */     } 
/* 154 */     return FlexBase64.encodeString(data, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public ExtendedHandshakeChecker handshakeChecker(URI uri, Map<String, List<String>> requestHeaders) {
/* 159 */     final String sentKey = requestHeaders.containsKey("Sec-WebSocket-Key") ? ((List<String>)requestHeaders.get("Sec-WebSocket-Key")).get(0) : null;
/* 160 */     return new ExtendedHandshakeChecker()
/*     */       {
/*     */         public void checkHandshakeExtended(Map<String, List<String>> headers) throws IOException
/*     */         {
/*     */           try {
/* 165 */             if (WebSocket13ClientHandshake.this.negotiation != null) {
/* 166 */               WebSocket13ClientHandshake.this.negotiation.afterRequest(headers);
/*     */             }
/* 168 */             String upgrade = WebSocket13ClientHandshake.this.getFirst("Upgrade", headers);
/* 169 */             if (upgrade == null || !upgrade.trim().equalsIgnoreCase("websocket")) {
/* 170 */               throw WebSocketMessages.MESSAGES.noWebSocketUpgradeHeader();
/*     */             }
/* 172 */             String connHeader = WebSocket13ClientHandshake.this.getFirst("Connection", headers);
/* 173 */             if (connHeader == null || !connHeader.trim().equalsIgnoreCase("upgrade")) {
/* 174 */               throw WebSocketMessages.MESSAGES.noWebSocketConnectionHeader();
/*     */             }
/* 176 */             String acceptKey = WebSocket13ClientHandshake.this.getFirst("Sec-WebSocket-Accept", headers);
/* 177 */             String dKey = WebSocket13ClientHandshake.this.solve(sentKey);
/* 178 */             if (!dKey.equals(acceptKey)) {
/* 179 */               throw WebSocketMessages.MESSAGES.webSocketAcceptKeyMismatch(dKey, acceptKey);
/*     */             }
/* 181 */             if (WebSocket13ClientHandshake.this.negotiation != null) {
/* 182 */               String subProto = WebSocket13ClientHandshake.this.getFirst("Sec-WebSocket-Protocol", headers);
/* 183 */               if (subProto != null && !subProto.isEmpty() && !WebSocket13ClientHandshake.this.negotiation.getSupportedSubProtocols().contains(subProto)) {
/* 184 */                 throw WebSocketMessages.MESSAGES.unsupportedProtocol(subProto, WebSocket13ClientHandshake.this.negotiation.getSupportedSubProtocols());
/*     */               }
/* 186 */               List<WebSocketExtension> extensions = Collections.emptyList();
/* 187 */               String extHeader = WebSocket13ClientHandshake.this.getFirst("Sec-WebSocket-Extensions", headers);
/* 188 */               if (extHeader != null) {
/* 189 */                 extensions = WebSocketExtension.parse(extHeader);
/*     */               }
/* 191 */               WebSocket13ClientHandshake.this.negotiation.handshakeComplete(subProto, extensions);
/*     */             } 
/* 193 */           } catch (IOException e) {
/* 194 */             throw e;
/* 195 */           } catch (Exception e) {
/* 196 */             throw new IOException(e);
/*     */           } 
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   private String getFirst(String key, Map<String, List<String>> map) {
/* 203 */     List<String> list = map.get(key.toLowerCase(Locale.ENGLISH));
/* 204 */     if (list == null || list.isEmpty()) {
/* 205 */       return null;
/*     */     }
/* 207 */     return list.get(0);
/*     */   }
/*     */   
/*     */   protected final String solve(String nonceBase64) {
/*     */     try {
/* 212 */       String concat = nonceBase64 + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
/* 213 */       MessageDigest digest = MessageDigest.getInstance("SHA1");
/*     */       
/* 215 */       digest.update(concat.getBytes(StandardCharsets.UTF_8));
/* 216 */       byte[] bytes = digest.digest();
/* 217 */       return FlexBase64.encodeString(bytes, false);
/* 218 */     } catch (NoSuchAlgorithmException e) {
/* 219 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   public WebSocketVersion getVersion() {
/* 224 */     return WebSocketVersion.V13;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\client\WebSocket13ClientHandshake.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */