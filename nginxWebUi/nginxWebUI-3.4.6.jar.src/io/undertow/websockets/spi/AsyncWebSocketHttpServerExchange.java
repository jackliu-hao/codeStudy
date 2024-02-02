/*     */ package io.undertow.websockets.spi;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.io.IoCallback;
/*     */ import io.undertow.io.Sender;
/*     */ import io.undertow.security.api.SecurityContext;
/*     */ import io.undertow.security.idm.Account;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.HttpUpgradeListener;
/*     */ import io.undertow.server.session.SessionConfig;
/*     */ import io.undertow.server.session.SessionManager;
/*     */ import io.undertow.util.AttachmentKey;
/*     */ import io.undertow.util.HeaderMap;
/*     */ import io.undertow.util.HttpString;
/*     */ import io.undertow.websockets.core.WebSocketChannel;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.security.Principal;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Deque;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.FinishedIoFuture;
/*     */ import org.xnio.FutureResult;
/*     */ import org.xnio.IoFuture;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.channels.StreamSourceChannel;
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
/*     */ public class AsyncWebSocketHttpServerExchange
/*     */   implements WebSocketHttpExchange
/*     */ {
/*     */   private final HttpServerExchange exchange;
/*     */   private Sender sender;
/*     */   private final Set<WebSocketChannel> peerConnections;
/*     */   
/*     */   public AsyncWebSocketHttpServerExchange(HttpServerExchange exchange, Set<WebSocketChannel> peerConnections) {
/*  67 */     this.exchange = exchange;
/*  68 */     this.peerConnections = peerConnections;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> void putAttachment(AttachmentKey<T> key, T value) {
/*  74 */     this.exchange.putAttachment(key, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T getAttachment(AttachmentKey<T> key) {
/*  79 */     return (T)this.exchange.getAttachment(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getRequestHeader(String headerName) {
/*  84 */     return this.exchange.getRequestHeaders().getFirst(HttpString.tryFromString(headerName));
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<String, List<String>> getRequestHeaders() {
/*  89 */     Map<String, List<String>> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
/*  90 */     for (HttpString header : this.exchange.getRequestHeaders().getHeaderNames()) {
/*  91 */       headers.put(header.toString(), new ArrayList<>((Collection<? extends String>)this.exchange.getRequestHeaders().get(header)));
/*     */     }
/*  93 */     return Collections.unmodifiableMap(headers);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getResponseHeader(String headerName) {
/*  98 */     return this.exchange.getResponseHeaders().getFirst(HttpString.tryFromString(headerName));
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<String, List<String>> getResponseHeaders() {
/* 103 */     Map<String, List<String>> headers = new HashMap<>();
/* 104 */     for (HttpString header : this.exchange.getResponseHeaders().getHeaderNames()) {
/* 105 */       headers.put(header.toString(), new ArrayList<>((Collection<? extends String>)this.exchange.getResponseHeaders().get(header)));
/*     */     }
/* 107 */     return Collections.unmodifiableMap(headers);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setResponseHeaders(Map<String, List<String>> headers) {
/* 112 */     HeaderMap map = this.exchange.getRequestHeaders();
/* 113 */     map.clear();
/* 114 */     for (Map.Entry<String, List<String>> header : headers.entrySet()) {
/* 115 */       map.addAll(HttpString.tryFromString(header.getKey()), header.getValue());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void setResponseHeader(String headerName, String headerValue) {
/* 121 */     this.exchange.getResponseHeaders().put(HttpString.tryFromString(headerName), headerValue);
/*     */   }
/*     */ 
/*     */   
/*     */   public void upgradeChannel(HttpUpgradeListener upgradeCallback) {
/* 126 */     this.exchange.upgradeChannel(upgradeCallback);
/*     */   }
/*     */ 
/*     */   
/*     */   public IoFuture<Void> sendData(ByteBuffer data) {
/* 131 */     if (this.sender == null) {
/* 132 */       this.sender = this.exchange.getResponseSender();
/*     */     }
/* 134 */     final FutureResult<Void> future = new FutureResult();
/* 135 */     this.sender.send(data, new IoCallback()
/*     */         {
/*     */           public void onComplete(HttpServerExchange exchange, Sender sender) {
/* 138 */             future.setResult(null);
/*     */           }
/*     */ 
/*     */           
/*     */           public void onException(HttpServerExchange exchange, Sender sender, IOException exception) {
/* 143 */             UndertowLogger.REQUEST_IO_LOGGER.ioException(exception);
/* 144 */             future.setException(exception);
/*     */           }
/*     */         });
/*     */     
/* 148 */     return future.getIoFuture();
/*     */   }
/*     */ 
/*     */   
/*     */   public IoFuture<byte[]> readRequestData() {
/* 153 */     final ByteArrayOutputStream data = new ByteArrayOutputStream();
/* 154 */     PooledByteBuffer pooled = this.exchange.getConnection().getByteBufferPool().allocate();
/* 155 */     final ByteBuffer buffer = pooled.getBuffer();
/* 156 */     StreamSourceChannel channel = this.exchange.getRequestChannel();
/*     */     
/*     */     try {
/*     */       while (true) {
/* 160 */         int res = channel.read(buffer);
/* 161 */         if (res == -1)
/* 162 */           return (IoFuture<byte[]>)new FinishedIoFuture(data.toByteArray()); 
/* 163 */         if (res == 0) {
/*     */           
/* 165 */           final FutureResult<byte[]> future = new FutureResult();
/* 166 */           channel.getReadSetter().set(new ChannelListener<StreamSourceChannel>()
/*     */               {
/*     */                 public void handleEvent(StreamSourceChannel channel)
/*     */                 {
/*     */                   try {
/* 171 */                     int res = channel.read(buffer);
/* 172 */                     if (res == -1) {
/* 173 */                       future.setResult(data.toByteArray());
/* 174 */                       channel.suspendReads(); return;
/*     */                     } 
/* 176 */                     if (res == 0) {
/*     */                       return;
/*     */                     }
/* 179 */                     buffer.flip();
/* 180 */                     while (buffer.hasRemaining()) {
/* 181 */                       data.write(buffer.get());
/*     */                     }
/* 183 */                     buffer.clear();
/*     */                   
/*     */                   }
/* 186 */                   catch (IOException e) {
/* 187 */                     future.setException(e);
/*     */                   } 
/*     */                 }
/*     */               });
/* 191 */           channel.resumeReads();
/* 192 */           return future.getIoFuture();
/*     */         } 
/* 194 */         buffer.flip();
/* 195 */         while (buffer.hasRemaining()) {
/* 196 */           data.write(buffer.get());
/*     */         }
/* 198 */         buffer.clear();
/*     */       }
/*     */     
/* 201 */     } catch (IOException e) {
/* 202 */       final FutureResult<byte[]> future = new FutureResult();
/* 203 */       future.setException(e);
/* 204 */       return future.getIoFuture();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void endExchange() {
/* 213 */     this.exchange.endExchange();
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/*     */     try {
/* 219 */       this.exchange.endExchange();
/*     */     } finally {
/* 221 */       IoUtils.safeClose((Closeable)this.exchange.getConnection());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String getRequestScheme() {
/* 227 */     return this.exchange.getRequestScheme();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getRequestURI() {
/* 232 */     String q = this.exchange.getQueryString();
/* 233 */     if (q == null || q.isEmpty()) {
/* 234 */       return this.exchange.getRequestURI();
/*     */     }
/* 236 */     return this.exchange.getRequestURI() + "?" + q;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteBufferPool getBufferPool() {
/* 242 */     return this.exchange.getConnection().getByteBufferPool();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getQueryString() {
/* 247 */     return this.exchange.getQueryString();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getSession() {
/* 252 */     SessionManager sm = (SessionManager)this.exchange.getAttachment(SessionManager.ATTACHMENT_KEY);
/* 253 */     SessionConfig sessionCookieConfig = (SessionConfig)this.exchange.getAttachment(SessionConfig.ATTACHMENT_KEY);
/* 254 */     if (sm != null && sessionCookieConfig != null) {
/* 255 */       return sm.getSession(this.exchange, sessionCookieConfig);
/*     */     }
/* 257 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<String, List<String>> getRequestParameters() {
/* 262 */     Map<String, List<String>> params = new HashMap<>();
/* 263 */     for (Map.Entry<String, Deque<String>> param : (Iterable<Map.Entry<String, Deque<String>>>)this.exchange.getQueryParameters().entrySet()) {
/* 264 */       params.put(param.getKey(), new ArrayList<>(param.getValue()));
/*     */     }
/* 266 */     return params;
/*     */   }
/*     */ 
/*     */   
/*     */   public Principal getUserPrincipal() {
/* 271 */     SecurityContext sc = this.exchange.getSecurityContext();
/* 272 */     if (sc == null) {
/* 273 */       return null;
/*     */     }
/* 275 */     Account authenticatedAccount = sc.getAuthenticatedAccount();
/* 276 */     if (authenticatedAccount == null) {
/* 277 */       return null;
/*     */     }
/* 279 */     return authenticatedAccount.getPrincipal();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isUserInRole(String role) {
/* 284 */     SecurityContext sc = this.exchange.getSecurityContext();
/* 285 */     if (sc == null) {
/* 286 */       return false;
/*     */     }
/* 288 */     Account authenticatedAccount = sc.getAuthenticatedAccount();
/* 289 */     if (authenticatedAccount == null) {
/* 290 */       return false;
/*     */     }
/* 292 */     return authenticatedAccount.getRoles().contains(role);
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<WebSocketChannel> getPeerConnections() {
/* 297 */     return this.peerConnections;
/*     */   }
/*     */ 
/*     */   
/*     */   public OptionMap getOptions() {
/* 302 */     return this.exchange.getConnection().getUndertowOptions();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\spi\AsyncWebSocketHttpServerExchange.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */