/*     */ package org.xnio.http;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URI;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.xnio.ChannelExceptionHandler;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.FutureResult;
/*     */ import org.xnio.IoFuture;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.Pooled;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio._private.Messages;
/*     */ import org.xnio.channels.BoundChannel;
/*     */ import org.xnio.channels.StreamSinkChannel;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ import org.xnio.conduits.PushBackStreamSourceConduit;
/*     */ import org.xnio.conduits.StreamSourceConduit;
/*     */ import org.xnio.ssl.SslConnection;
/*     */ import org.xnio.ssl.XnioSsl;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HttpUpgrade
/*     */ {
/*     */   public static IoFuture<SslConnection> performUpgrade(XnioWorker worker, XnioSsl ssl, InetSocketAddress bindAddress, URI uri, Map<String, String> headers, ChannelListener<? super SslConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap, HandshakeChecker handshakeChecker) {
/*  76 */     return (IoFuture)(new HttpUpgradeState<>(worker, ssl, bindAddress, uri, headers, openListener, bindListener, optionMap, handshakeChecker)).doUpgrade();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static IoFuture<SslConnection> performUpgrade(XnioWorker worker, XnioSsl ssl, InetSocketAddress bindAddress, URI uri, Map<String, List<String>> headers, ChannelListener<? super SslConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap, ExtendedHandshakeChecker handshakeChecker) {
/*  95 */     return (IoFuture)(new HttpUpgradeState<>(worker, ssl, bindAddress, uri, headers, openListener, bindListener, optionMap, handshakeChecker)).doUpgrade();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static IoFuture<StreamConnection> performUpgrade(XnioWorker worker, InetSocketAddress bindAddress, URI uri, Map<String, String> headers, ChannelListener<? super StreamConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap, HandshakeChecker handshakeChecker) {
/* 112 */     return (IoFuture)(new HttpUpgradeState<>(worker, null, bindAddress, uri, headers, openListener, bindListener, optionMap, handshakeChecker)).doUpgrade();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static IoFuture<StreamConnection> performUpgrade(XnioWorker worker, InetSocketAddress bindAddress, URI uri, Map<String, List<String>> headers, ChannelListener<? super StreamConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap, ExtendedHandshakeChecker handshakeChecker) {
/* 129 */     return (IoFuture)(new HttpUpgradeState<>(worker, null, bindAddress, uri, headers, openListener, bindListener, optionMap, handshakeChecker)).doUpgrade();
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T extends StreamConnection> IoFuture<T> performUpgrade(T connection, URI uri, Map<String, String> headers, ChannelListener<? super StreamConnection> openListener, HandshakeChecker handshakeChecker) {
/* 144 */     return (new HttpUpgradeState<>(connection, uri, headers, openListener, handshakeChecker)).upgradeExistingConnection();
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
/*     */ 
/*     */   
/*     */   public static <T extends StreamConnection> IoFuture<T> performUpgrade(T connection, URI uri, Map<String, List<String>> headers, ChannelListener<? super StreamConnection> openListener, ExtendedHandshakeChecker handshakeChecker) {
/* 158 */     return (new HttpUpgradeState<>(connection, uri, headers, openListener, handshakeChecker)).upgradeExistingConnection();
/*     */   }
/*     */ 
/*     */   
/*     */   private static class HttpUpgradeState<T extends StreamConnection>
/*     */   {
/*     */     private final XnioWorker worker;
/*     */     
/*     */     private final XnioSsl ssl;
/*     */     
/*     */     private final InetSocketAddress bindAddress;
/*     */     
/*     */     private final URI uri;
/*     */     
/*     */     private final Map<String, List<String>> headers;
/*     */     private final ChannelListener<? super T> openListener;
/*     */     private final ChannelListener<? super BoundChannel> bindListener;
/*     */     private final OptionMap optionMap;
/*     */     private final Object handshakeChecker;
/* 177 */     private final FutureResult<T> future = new FutureResult();
/*     */     
/*     */     private T connection;
/*     */     
/*     */     private HttpUpgradeState(XnioWorker worker, XnioSsl ssl, InetSocketAddress bindAddress, URI uri, Map<String, String> headers, ChannelListener<? super T> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap, HandshakeChecker handshakeChecker) {
/* 182 */       this.worker = worker;
/* 183 */       this.ssl = ssl;
/* 184 */       this.bindAddress = bindAddress;
/* 185 */       this.uri = uri;
/* 186 */       this.openListener = openListener;
/* 187 */       this.bindListener = bindListener;
/* 188 */       this.optionMap = optionMap;
/* 189 */       this.handshakeChecker = handshakeChecker;
/* 190 */       Map<String, List<String>> newHeaders = new HashMap<>();
/* 191 */       for (Map.Entry<String, String> entry : headers.entrySet()) {
/* 192 */         newHeaders.put(entry.getKey(), Collections.singletonList(entry.getValue()));
/*     */       }
/* 194 */       this.headers = newHeaders;
/*     */     }
/*     */     
/*     */     private HttpUpgradeState(XnioWorker worker, XnioSsl ssl, InetSocketAddress bindAddress, URI uri, Map<String, List<String>> headers, ChannelListener<? super T> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap, ExtendedHandshakeChecker handshakeChecker) {
/* 198 */       this.worker = worker;
/* 199 */       this.ssl = ssl;
/* 200 */       this.bindAddress = bindAddress;
/* 201 */       this.uri = uri;
/* 202 */       this.headers = headers;
/* 203 */       this.openListener = openListener;
/* 204 */       this.bindListener = bindListener;
/* 205 */       this.optionMap = optionMap;
/* 206 */       this.handshakeChecker = handshakeChecker;
/*     */     }
/*     */     
/*     */     public HttpUpgradeState(T connection, URI uri, Map<String, String> headers, ChannelListener<? super StreamConnection> openListener, HandshakeChecker handshakeChecker) {
/* 210 */       this.worker = connection.getWorker();
/* 211 */       this.ssl = null;
/* 212 */       this.bindAddress = null;
/* 213 */       this.uri = uri;
/* 214 */       this.openListener = (ChannelListener)openListener;
/* 215 */       this.bindListener = null;
/* 216 */       this.optionMap = OptionMap.EMPTY;
/* 217 */       this.handshakeChecker = handshakeChecker;
/* 218 */       this.connection = connection;
/* 219 */       Map<String, List<String>> newHeaders = new HashMap<>();
/* 220 */       for (Map.Entry<String, String> entry : headers.entrySet()) {
/* 221 */         newHeaders.put(entry.getKey(), Collections.singletonList(entry.getValue()));
/*     */       }
/* 223 */       this.headers = newHeaders;
/*     */     }
/*     */     
/*     */     public HttpUpgradeState(T connection, URI uri, Map<String, List<String>> headers, ChannelListener<? super StreamConnection> openListener, ExtendedHandshakeChecker handshakeChecker) {
/* 227 */       this.worker = connection.getWorker();
/* 228 */       this.ssl = null;
/* 229 */       this.bindAddress = null;
/* 230 */       this.uri = uri;
/* 231 */       this.headers = headers;
/* 232 */       this.openListener = (ChannelListener)openListener;
/* 233 */       this.bindListener = null;
/* 234 */       this.optionMap = OptionMap.EMPTY;
/* 235 */       this.handshakeChecker = handshakeChecker;
/* 236 */       this.connection = connection;
/*     */     }
/*     */ 
/*     */     
/*     */     private IoFuture<T> doUpgrade() {
/* 241 */       InetSocketAddress address = new InetSocketAddress(this.uri.getHost(), this.uri.getPort());
/*     */       
/* 243 */       ChannelListener<StreamConnection> connectListener = new ConnectionOpenListener();
/* 244 */       String scheme = this.uri.getScheme();
/* 245 */       if (scheme.equals("http")) {
/* 246 */         if (this.bindAddress == null) {
/* 247 */           this.worker.openStreamConnection(address, connectListener, this.bindListener, this.optionMap).addNotifier((IoFuture.Notifier)new FailureNotifier(), null);
/*     */         } else {
/* 249 */           this.worker.openStreamConnection(this.bindAddress, address, connectListener, this.bindListener, this.optionMap).addNotifier((IoFuture.Notifier)new FailureNotifier(), null);
/*     */         } 
/* 251 */       } else if (scheme.equals("https")) {
/* 252 */         if (this.ssl == null) {
/* 253 */           throw Messages.msg.missingSslProvider();
/*     */         }
/* 255 */         if (this.bindAddress == null) {
/* 256 */           this.ssl.openSslConnection(this.worker, address, connectListener, this.bindListener, this.optionMap).addNotifier((IoFuture.Notifier)new FailureNotifier(), null);
/*     */         } else {
/* 258 */           this.ssl.openSslConnection(this.worker, this.bindAddress, address, connectListener, this.bindListener, this.optionMap).addNotifier((IoFuture.Notifier)new FailureNotifier(), null);
/*     */         } 
/*     */       } else {
/* 261 */         throw Messages.msg.invalidURLScheme(scheme);
/*     */       } 
/* 263 */       return this.future.getIoFuture();
/*     */     }
/*     */ 
/*     */     
/*     */     private String buildHttpRequest() {
/* 268 */       StringBuilder builder = new StringBuilder();
/* 269 */       builder.append("GET ");
/* 270 */       builder.append(this.uri.getPath().isEmpty() ? "/" : this.uri.getPath());
/* 271 */       if (this.uri.getQuery() != null && !this.uri.getQuery().isEmpty()) {
/* 272 */         builder.append('?');
/* 273 */         builder.append(this.uri.getQuery());
/*     */       } 
/* 275 */       builder.append(" HTTP/1.1\r\n");
/* 276 */       Set<String> seen = new HashSet<>();
/* 277 */       for (Map.Entry<String, List<String>> headerEntry : this.headers.entrySet()) {
/* 278 */         for (String value : headerEntry.getValue()) {
/* 279 */           builder.append(headerEntry.getKey());
/* 280 */           builder.append(": ");
/* 281 */           builder.append(value);
/* 282 */           builder.append("\r\n");
/* 283 */           seen.add(((String)headerEntry.getKey()).toLowerCase(Locale.ENGLISH));
/*     */         } 
/*     */       } 
/* 286 */       if (!seen.contains("host")) {
/* 287 */         builder.append("Host: ");
/* 288 */         builder.append(getHost());
/* 289 */         builder.append("\r\n");
/*     */       } 
/* 291 */       if (!seen.contains("connection")) {
/* 292 */         builder.append("Connection: upgrade\r\n");
/*     */       }
/* 294 */       if (!seen.contains("upgrade")) {
/* 295 */         throw new IllegalArgumentException("Upgrade: header was not supplied in header arguments");
/*     */       }
/* 297 */       builder.append("\r\n");
/* 298 */       return builder.toString();
/*     */     }
/*     */     
/*     */     private String getHost() {
/* 302 */       String scheme = this.uri.getScheme();
/* 303 */       int port = this.uri.getPort();
/*     */       
/* 305 */       if (port < 0 || ("http".equals(scheme) && port == 80) || ("https".equals(scheme) && port == 443))
/*     */       {
/* 307 */         return this.uri.getHost();
/*     */       }
/*     */       
/* 310 */       return this.uri.getHost() + ":" + port;
/*     */     }
/*     */     
/*     */     public IoFuture<T> upgradeExistingConnection() {
/* 314 */       ChannelListener<StreamConnection> connectListener = new ConnectionOpenListener();
/* 315 */       connectListener.handleEvent((Channel)this.connection);
/* 316 */       return this.future.getIoFuture();
/*     */     }
/*     */     
/*     */     private class ConnectionOpenListener implements ChannelListener<StreamConnection> {
/*     */       private ConnectionOpenListener() {}
/*     */       
/*     */       public void handleEvent(StreamConnection channel) {
/* 323 */         HttpUpgrade.HttpUpgradeState.this.connection = (T)channel;
/* 324 */         ByteBuffer buffer = ByteBuffer.wrap(HttpUpgrade.HttpUpgradeState.this.buildHttpRequest().getBytes());
/*     */         
/*     */         while (true) {
/*     */           try {
/* 328 */             int r = channel.getSinkChannel().write(buffer);
/* 329 */             if (r == 0) {
/* 330 */               channel.getSinkChannel().getWriteSetter().set(new HttpUpgrade.HttpUpgradeState.StringWriteListener(buffer));
/* 331 */               channel.getSinkChannel().resumeWrites();
/*     */               return;
/*     */             } 
/* 334 */           } catch (IOException e) {
/* 335 */             IoUtils.safeClose((Closeable)channel);
/* 336 */             HttpUpgrade.HttpUpgradeState.this.future.setException(e);
/*     */             return;
/*     */           } 
/* 339 */           if (!buffer.hasRemaining()) {
/* 340 */             HttpUpgrade.HttpUpgradeState.this.flushUpgradeChannel();
/*     */             return;
/*     */           } 
/*     */         } 
/*     */       } } private void flushUpgradeChannel() {
/*     */       try {
/* 346 */         if (!this.connection.getSinkChannel().flush()) {
/*     */           
/* 348 */           this.connection.getSinkChannel().getWriteSetter().set(ChannelListeners.flushingChannelListener(new ChannelListener<StreamSinkChannel>()
/*     */                 {
/*     */                   public void handleEvent(StreamSinkChannel channel) {
/* 351 */                     channel.suspendWrites();
/* 352 */                     (new HttpUpgrade.HttpUpgradeState.UpgradeResultListener()).handleEvent((StreamSourceChannel)HttpUpgrade.HttpUpgradeState.this.connection.getSourceChannel());
/*     */                   }
/*     */                 },  new ChannelExceptionHandler<StreamSinkChannel>()
/*     */                 {
/*     */                   public void handleException(StreamSinkChannel channel, IOException exception) {
/* 357 */                     IoUtils.safeClose((Closeable)channel);
/* 358 */                     HttpUpgrade.HttpUpgradeState.this.future.setException(exception);
/*     */                   }
/*     */                 }));
/* 361 */           this.connection.getSinkChannel().resumeWrites();
/*     */           return;
/*     */         } 
/* 364 */       } catch (IOException e) {
/* 365 */         IoUtils.safeClose((Closeable)this.connection);
/* 366 */         this.future.setException(e);
/*     */         return;
/*     */       } 
/* 369 */       (new UpgradeResultListener()).handleEvent((StreamSourceChannel)this.connection.getSourceChannel());
/*     */     }
/*     */     
/*     */     private final class StringWriteListener
/*     */       implements ChannelListener<StreamSinkChannel> {
/*     */       final ByteBuffer buffer;
/*     */       
/*     */       private StringWriteListener(ByteBuffer buffer) {
/* 377 */         this.buffer = buffer;
/*     */       }
/*     */ 
/*     */ 
/*     */       
/*     */       public void handleEvent(StreamSinkChannel channel) {
/*     */         while (true) {
/*     */           try {
/* 385 */             int r = channel.write(this.buffer);
/* 386 */             if (r == 0) {
/*     */               return;
/*     */             }
/* 389 */           } catch (IOException e) {
/* 390 */             IoUtils.safeClose((Closeable)channel);
/* 391 */             HttpUpgrade.HttpUpgradeState.this.future.setException(e);
/*     */             return;
/*     */           } 
/* 394 */           if (!this.buffer.hasRemaining()) {
/* 395 */             channel.suspendWrites();
/* 396 */             HttpUpgrade.HttpUpgradeState.this.flushUpgradeChannel();
/*     */             return;
/*     */           } 
/*     */         } 
/*     */       } }
/*     */     
/* 402 */     private final class UpgradeResultListener implements ChannelListener<StreamSourceChannel> { private final HttpUpgradeParser parser = new HttpUpgradeParser();
/* 403 */       private ByteBuffer buffer = ByteBuffer.allocate(1024);
/*     */       
/*     */       private UpgradeResultListener() {}
/*     */       
/*     */       public void handleEvent(StreamSourceChannel channel) {
/*     */         while (true) {
/*     */           try {
/* 410 */             int r = channel.read(this.buffer);
/* 411 */             if (r == 0) {
/* 412 */               channel.getReadSetter().set(this);
/* 413 */               channel.resumeReads(); return;
/*     */             } 
/* 415 */             if (r == -1) {
/* 416 */               throw new ConnectionClosedEarlyException(Messages.msg.connectionClosedEarly().getMessage());
/*     */             }
/* 418 */             this.buffer.flip();
/* 419 */             this.parser.parse(this.buffer);
/* 420 */             if (!this.parser.isComplete()) {
/* 421 */               this.buffer.compact();
/*     */             }
/* 423 */           } catch (IOException e) {
/* 424 */             IoUtils.safeClose((Closeable)channel);
/* 425 */             HttpUpgrade.HttpUpgradeState.this.future.setException(e);
/*     */             
/*     */             return;
/*     */           } 
/* 429 */           if (this.parser.isComplete()) {
/* 430 */             channel.suspendReads();
/*     */             
/* 432 */             if (this.buffer.hasRemaining()) {
/* 433 */               StreamSourceConduit orig = HttpUpgrade.HttpUpgradeState.this.connection.getSourceChannel().getConduit();
/* 434 */               PushBackStreamSourceConduit pushBack = new PushBackStreamSourceConduit(orig);
/* 435 */               pushBack.pushBack(new Pooled<ByteBuffer>()
/*     */                   {
/*     */                     public void discard() {
/* 438 */                       HttpUpgrade.HttpUpgradeState.UpgradeResultListener.this.buffer = null;
/*     */                     }
/*     */ 
/*     */                     
/*     */                     public void free() {
/* 443 */                       HttpUpgrade.HttpUpgradeState.UpgradeResultListener.this.buffer = null;
/*     */                     }
/*     */ 
/*     */                     
/*     */                     public ByteBuffer getResource() throws IllegalStateException {
/* 448 */                       return HttpUpgrade.HttpUpgradeState.UpgradeResultListener.this.buffer;
/*     */                     }
/*     */ 
/*     */                     
/*     */                     public void close() {
/* 453 */                       free();
/*     */                     }
/*     */                   });
/* 456 */               HttpUpgrade.HttpUpgradeState.this.connection.getSourceChannel().setConduit((StreamSourceConduit)pushBack);
/*     */             } 
/*     */ 
/*     */             
/* 460 */             if (this.parser.getResponseCode() == 101) {
/* 461 */               HttpUpgrade.HttpUpgradeState.this.handleUpgrade(this.parser);
/* 462 */             } else if (this.parser.getResponseCode() == 301 || this.parser
/* 463 */               .getResponseCode() == 302 || this.parser
/* 464 */               .getResponseCode() == 303 || this.parser
/* 465 */               .getResponseCode() == 307 || this.parser
/* 466 */               .getResponseCode() == 308) {
/* 467 */               IoUtils.safeClose((Closeable)HttpUpgrade.HttpUpgradeState.this.connection);
/* 468 */               HttpUpgrade.HttpUpgradeState.this.handleRedirect(this.parser);
/*     */             } else {
/* 470 */               IoUtils.safeClose((Closeable)HttpUpgrade.HttpUpgradeState.this.connection);
/* 471 */               HttpUpgrade.HttpUpgradeState.this.future.setException(new UpgradeFailedException("Invalid response code " + this.parser.getResponseCode()));
/*     */             } 
/*     */             return;
/*     */           } 
/*     */         } 
/*     */       } } private void handleUpgrade(HttpUpgradeParser parser) {
/* 477 */       Map<String, String> simpleHeaders = new HashMap<>();
/* 478 */       for (Map.Entry<String, List<String>> e : parser.getHeaders().entrySet()) {
/* 479 */         simpleHeaders.put(e.getKey(), ((List<String>)e.getValue()).get(0));
/*     */       }
/* 481 */       String contentLength = simpleHeaders.get("content-length");
/* 482 */       if (contentLength != null && 
/* 483 */         !"0".equals(contentLength)) {
/* 484 */         this.future.setException(new IOException("Upgrade responses must have a content length of zero."));
/*     */         
/*     */         return;
/*     */       } 
/* 488 */       String transferCoding = simpleHeaders.get("transfer-encoding");
/* 489 */       if (transferCoding != null) {
/* 490 */         this.future.setException(new IOException("Upgrade responses cannot have a transfer coding"));
/*     */         
/*     */         return;
/*     */       } 
/* 494 */       if (this.handshakeChecker != null) {
/*     */         try {
/* 496 */           if (this.handshakeChecker instanceof ExtendedHandshakeChecker) {
/* 497 */             ((ExtendedHandshakeChecker)this.handshakeChecker).checkHandshakeExtended(parser.getHeaders());
/*     */           } else {
/* 499 */             ((HandshakeChecker)this.handshakeChecker).checkHandshake(simpleHeaders);
/*     */           } 
/* 501 */         } catch (IOException e) {
/* 502 */           IoUtils.safeClose((Closeable)this.connection);
/* 503 */           this.future.setException(e);
/*     */           return;
/*     */         } 
/*     */       }
/* 507 */       this.future.setResult(this.connection);
/* 508 */       ChannelListeners.invokeChannelListener((Channel)this.connection, this.openListener);
/*     */     }
/*     */     
/*     */     private void handleRedirect(HttpUpgradeParser parser) {
/* 512 */       List<String> location = parser.getHeaders().get("location");
/* 513 */       this.future.setException(new RedirectException(Messages.msg.redirect(), parser.getResponseCode(), (location == null) ? null : location.get(0)));
/*     */     }
/*     */     
/*     */     private class FailureNotifier extends IoFuture.HandlingNotifier<StreamConnection, Object> { private FailureNotifier() {}
/*     */       
/*     */       public void handleFailed(IOException exception, Object attachment) {
/* 519 */         HttpUpgrade.HttpUpgradeState.this.future.setException(exception);
/*     */       }
/*     */ 
/*     */       
/*     */       public void handleCancelled(Object attachment) {
/* 524 */         HttpUpgrade.HttpUpgradeState.this.future.setCancelled();
/*     */       } }
/*     */   
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\http\HttpUpgrade.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */