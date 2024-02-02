/*     */ package io.undertow.websockets.client;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.client.ClientCallback;
/*     */ import io.undertow.client.ClientConnection;
/*     */ import io.undertow.client.ClientExchange;
/*     */ import io.undertow.client.ClientRequest;
/*     */ import io.undertow.client.UndertowClient;
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.protocols.ssl.UndertowXnioSsl;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.Methods;
/*     */ import io.undertow.util.Protocols;
/*     */ import io.undertow.websockets.core.WebSocketChannel;
/*     */ import io.undertow.websockets.core.WebSocketLogger;
/*     */ import io.undertow.websockets.core.WebSocketVersion;
/*     */ import io.undertow.websockets.extensions.ExtensionHandshake;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.nio.channels.Channel;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.xnio.Cancellable;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.FutureResult;
/*     */ import org.xnio.IoFuture;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio.http.HttpUpgrade;
/*     */ import org.xnio.http.RedirectException;
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
/*     */ public class WebSocketClient
/*     */ {
/*     */   public static final String BIND_PROPERTY = "io.undertow.websockets.BIND_ADDRESS";
/*  67 */   private static final int MAX_REDIRECTS = Integer.getInteger("io.undertow.websockets.max-redirects", 5).intValue();
/*     */   
/*     */   @Deprecated
/*     */   public static IoFuture<WebSocketChannel> connect(XnioWorker worker, ByteBufferPool bufferPool, OptionMap optionMap, URI uri, WebSocketVersion version) {
/*  71 */     return connect(worker, bufferPool, optionMap, uri, version, (WebSocketClientNegotiation)null);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public static IoFuture<WebSocketChannel> connect(XnioWorker worker, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap optionMap, URI uri, WebSocketVersion version) {
/*  76 */     return connect(worker, ssl, bufferPool, optionMap, uri, version, null);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public static IoFuture<WebSocketChannel> connect(XnioWorker worker, ByteBufferPool bufferPool, OptionMap optionMap, URI uri, WebSocketVersion version, WebSocketClientNegotiation clientNegotiation) {
/*  81 */     return connect(worker, null, bufferPool, optionMap, uri, version, clientNegotiation);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public static IoFuture<WebSocketChannel> connect(XnioWorker worker, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap optionMap, URI uri, WebSocketVersion version, WebSocketClientNegotiation clientNegotiation) {
/*  86 */     return connect(worker, ssl, bufferPool, optionMap, uri, version, clientNegotiation, null);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public static IoFuture<WebSocketChannel> connect(XnioWorker worker, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap optionMap, URI uri, WebSocketVersion version, WebSocketClientNegotiation clientNegotiation, Set<ExtensionHandshake> clientExtensions) {
/*  91 */     return connect(worker, ssl, bufferPool, optionMap, null, uri, version, clientNegotiation, clientExtensions);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public static IoFuture<WebSocketChannel> connect(XnioWorker worker, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap optionMap, InetSocketAddress bindAddress, URI uri, WebSocketVersion version, WebSocketClientNegotiation clientNegotiation, Set<ExtensionHandshake> clientExtensions) {
/*  96 */     return connectionBuilder(worker, bufferPool, uri)
/*  97 */       .setSsl(ssl)
/*  98 */       .setOptionMap(optionMap)
/*  99 */       .setBindAddress(bindAddress)
/* 100 */       .setVersion(version)
/* 101 */       .setClientNegotiation(clientNegotiation)
/* 102 */       .setClientExtensions(clientExtensions)
/* 103 */       .connect();
/*     */   }
/*     */   
/*     */   public static class ConnectionBuilder
/*     */   {
/*     */     private final XnioWorker worker;
/*     */     private final ByteBufferPool bufferPool;
/*     */     private final URI uri;
/*     */     private XnioSsl ssl;
/* 112 */     private OptionMap optionMap = OptionMap.EMPTY;
/*     */     private InetSocketAddress bindAddress;
/* 114 */     private WebSocketVersion version = WebSocketVersion.V13;
/*     */     private WebSocketClientNegotiation clientNegotiation;
/*     */     private Set<ExtensionHandshake> clientExtensions;
/*     */     private URI proxyUri;
/*     */     private XnioSsl proxySsl;
/*     */     
/*     */     public ConnectionBuilder(XnioWorker worker, ByteBufferPool bufferPool, URI uri) {
/* 121 */       this.worker = worker;
/* 122 */       this.bufferPool = bufferPool;
/* 123 */       this.uri = uri;
/*     */     }
/*     */     
/*     */     public XnioWorker getWorker() {
/* 127 */       return this.worker;
/*     */     }
/*     */     
/*     */     public URI getUri() {
/* 131 */       return this.uri;
/*     */     }
/*     */     
/*     */     public XnioSsl getSsl() {
/* 135 */       return this.ssl;
/*     */     }
/*     */     
/*     */     public ConnectionBuilder setSsl(XnioSsl ssl) {
/* 139 */       this.ssl = ssl;
/* 140 */       return this;
/*     */     }
/*     */     
/*     */     public ByteBufferPool getBufferPool() {
/* 144 */       return this.bufferPool;
/*     */     }
/*     */     
/*     */     public OptionMap getOptionMap() {
/* 148 */       return this.optionMap;
/*     */     }
/*     */     
/*     */     public ConnectionBuilder setOptionMap(OptionMap optionMap) {
/* 152 */       this.optionMap = optionMap;
/* 153 */       return this;
/*     */     }
/*     */     
/*     */     public InetSocketAddress getBindAddress() {
/* 157 */       return this.bindAddress;
/*     */     }
/*     */     
/*     */     public ConnectionBuilder setBindAddress(InetSocketAddress bindAddress) {
/* 161 */       this.bindAddress = bindAddress;
/* 162 */       return this;
/*     */     }
/*     */     
/*     */     public WebSocketVersion getVersion() {
/* 166 */       return this.version;
/*     */     }
/*     */     
/*     */     public ConnectionBuilder setVersion(WebSocketVersion version) {
/* 170 */       this.version = version;
/* 171 */       return this;
/*     */     }
/*     */     
/*     */     public WebSocketClientNegotiation getClientNegotiation() {
/* 175 */       return this.clientNegotiation;
/*     */     }
/*     */     
/*     */     public ConnectionBuilder setClientNegotiation(WebSocketClientNegotiation clientNegotiation) {
/* 179 */       this.clientNegotiation = clientNegotiation;
/* 180 */       return this;
/*     */     }
/*     */     
/*     */     public Set<ExtensionHandshake> getClientExtensions() {
/* 184 */       return this.clientExtensions;
/*     */     }
/*     */     
/*     */     public ConnectionBuilder setClientExtensions(Set<ExtensionHandshake> clientExtensions) {
/* 188 */       this.clientExtensions = clientExtensions;
/* 189 */       return this;
/*     */     }
/*     */     
/*     */     public URI getProxyUri() {
/* 193 */       return this.proxyUri;
/*     */     }
/*     */     
/*     */     public ConnectionBuilder setProxyUri(URI proxyUri) {
/* 197 */       this.proxyUri = proxyUri;
/* 198 */       return this;
/*     */     }
/*     */     
/*     */     public XnioSsl getProxySsl() {
/* 202 */       return this.proxySsl;
/*     */     }
/*     */     
/*     */     public ConnectionBuilder setProxySsl(XnioSsl proxySsl) {
/* 206 */       this.proxySsl = proxySsl;
/* 207 */       return this;
/*     */     }
/*     */     
/*     */     public IoFuture<WebSocketChannel> connect() {
/* 211 */       return connectImpl(this.uri, new FutureResult(), 0);
/*     */     } private IoFuture<WebSocketChannel> connectImpl(final URI uri, final FutureResult<WebSocketChannel> ioFuture, final int redirectCount) {
/*     */       final URI newUri;
/* 214 */       WebSocketLogger.REQUEST_LOGGER.debugf("Opening websocket connection to %s", uri);
/* 215 */       String scheme = isSecure(uri) ? "https" : "http";
/*     */       
/*     */       try {
/* 218 */         newUri = new URI(scheme, uri.getUserInfo(), uri.getHost(), (uri.getPort() == -1) ? (isSecure(uri) ? 443 : 80) : uri.getPort(), uri.getPath().isEmpty() ? "/" : uri.getPath(), uri.getQuery(), uri.getFragment());
/* 219 */       } catch (URISyntaxException e) {
/* 220 */         throw new RuntimeException(e);
/*     */       } 
/* 222 */       final WebSocketClientHandshake handshake = WebSocketClientHandshake.create(this.version, newUri, this.clientNegotiation, this.clientExtensions);
/* 223 */       Map<String, String> originalHeaders = handshake.createHeaders();
/* 224 */       originalHeaders.put("Host", uri.getHost() + ":" + newUri.getPort());
/* 225 */       final Map<String, List<String>> headers = new HashMap<>();
/* 226 */       for (Map.Entry<String, String> entry : originalHeaders.entrySet()) {
/* 227 */         List<String> list = new ArrayList<>();
/* 228 */         list.add(entry.getValue());
/* 229 */         headers.put(entry.getKey(), list);
/*     */       } 
/* 231 */       if (this.clientNegotiation != null) {
/* 232 */         this.clientNegotiation.beforeRequest(headers);
/*     */       }
/* 234 */       InetSocketAddress toBind = this.bindAddress;
/* 235 */       String sysBind = System.getProperty("io.undertow.websockets.BIND_ADDRESS");
/* 236 */       if (toBind == null && sysBind != null) {
/* 237 */         toBind = new InetSocketAddress(sysBind, 0);
/*     */       }
/* 239 */       if (this.proxyUri != null) {
/* 240 */         UndertowClient.getInstance().connect(new ClientCallback<ClientConnection>()
/*     */             {
/*     */               public void completed(final ClientConnection connection) {
/* 243 */                 int port = (uri.getPort() > 0) ? uri.getPort() : (WebSocketClient.ConnectionBuilder.this.isSecure(uri) ? 443 : 80);
/*     */ 
/*     */ 
/*     */                 
/* 247 */                 ClientRequest cr = (new ClientRequest()).setMethod(Methods.CONNECT).setPath(uri.getHost() + ":" + port).setProtocol(Protocols.HTTP_1_1);
/* 248 */                 cr.getRequestHeaders().put(Headers.HOST, WebSocketClient.ConnectionBuilder.this.proxyUri.getHost() + ":" + ((WebSocketClient.ConnectionBuilder.this.proxyUri.getPort() > 0) ? WebSocketClient.ConnectionBuilder.this.proxyUri.getPort() : 80));
/* 249 */                 connection.sendRequest(cr, new ClientCallback<ClientExchange>()
/*     */                     {
/*     */                       public void completed(ClientExchange result) {
/* 252 */                         result.setResponseListener(new ClientCallback<ClientExchange>()
/*     */                             {
/*     */                               public void completed(ClientExchange response) {
/*     */                                 try {
/* 256 */                                   if (response.getResponse().getResponseCode() == 200) {
/*     */                                     try {
/* 258 */                                       StreamConnection targetConnection = connection.performUpgrade();
/* 259 */                                       WebSocketLogger.REQUEST_LOGGER.debugf("Established websocket connection to %s", uri);
/* 260 */                                       if (WebSocketClient.ConnectionBuilder.this.isSecure(uri)) {
/* 261 */                                         handleConnectionWithExistingConnection((StreamConnection)((UndertowXnioSsl)WebSocketClient.ConnectionBuilder.this.ssl).wrapExistingConnection(targetConnection, WebSocketClient.ConnectionBuilder.this.optionMap, uri));
/*     */                                       } else {
/* 263 */                                         handleConnectionWithExistingConnection(targetConnection);
/*     */                                       } 
/* 265 */                                     } catch (IOException e) {
/* 266 */                                       ioFuture.setException(e);
/* 267 */                                     } catch (Exception e) {
/* 268 */                                       ioFuture.setException(new IOException(e));
/*     */                                     } 
/*     */                                   } else {
/* 271 */                                     ioFuture.setException(UndertowMessages.MESSAGES.proxyConnectionFailed(response.getResponse().getResponseCode()));
/*     */                                   } 
/* 273 */                                 } catch (Exception e) {
/* 274 */                                   ioFuture.setException(new IOException(e));
/*     */                                 } 
/*     */                               }
/*     */ 
/*     */ 
/*     */                               
/*     */                               private void handleConnectionWithExistingConnection(StreamConnection targetConnection) {
/* 281 */                                 final IoFuture<?> result = HttpUpgrade.performUpgrade(targetConnection, newUri, headers, new WebSocketClient.ConnectionBuilder.WebsocketConnectionListener(WebSocketClient.ConnectionBuilder.this.optionMap, handshake, newUri, ioFuture), handshake.handshakeChecker(newUri, headers));
/*     */                                 
/* 283 */                                 result.addNotifier(new IoFuture.Notifier<Object, Object>()
/*     */                                     {
/*     */                                       public void notify(IoFuture<?> res, Object attachment) {
/* 286 */                                         if (res.getStatus() == IoFuture.Status.FAILED) {
/* 287 */                                           ioFuture.setException(res.getException());
/*     */                                         }
/*     */                                       }
/*     */                                     },  null);
/* 291 */                                 ioFuture.addCancelHandler(new Cancellable()
/*     */                                     {
/*     */                                       public Cancellable cancel() {
/* 294 */                                         result.cancel();
/* 295 */                                         return null;
/*     */                                       }
/*     */                                     });
/*     */                               }
/*     */ 
/*     */                               
/*     */                               public void failed(IOException e) {
/* 302 */                                 ioFuture.setException(e);
/*     */                               }
/*     */                             });
/*     */                       }
/*     */                       
/*     */                       public void failed(IOException e) {
/* 308 */                         ioFuture.setException(e);
/*     */                       }
/*     */                     });
/*     */               }
/*     */               
/*     */               public void failed(IOException e) {
/* 314 */                 ioFuture.setException(e);
/*     */               }
/*     */             }this.bindAddress, this.proxyUri, this.worker, this.proxySsl, this.bufferPool, this.optionMap);
/*     */       } else {
/*     */         final IoFuture<?> result;
/*     */         
/* 320 */         if (this.ssl != null) {
/* 321 */           result = HttpUpgrade.performUpgrade(this.worker, this.ssl, toBind, newUri, headers, new WebsocketConnectionListener(this.optionMap, handshake, newUri, ioFuture), null, this.optionMap, handshake.handshakeChecker(newUri, headers));
/*     */         } else {
/* 323 */           result = HttpUpgrade.performUpgrade(this.worker, toBind, newUri, headers, new WebsocketConnectionListener(this.optionMap, handshake, newUri, ioFuture), null, this.optionMap, handshake.handshakeChecker(newUri, headers));
/*     */         } 
/* 325 */         result.addNotifier(new IoFuture.Notifier<Object, Object>()
/*     */             {
/*     */               public void notify(IoFuture<?> res, Object attachment) {
/* 328 */                 if (res.getStatus() == IoFuture.Status.FAILED) {
/* 329 */                   IOException exception = res.getException();
/* 330 */                   if (exception instanceof RedirectException) {
/* 331 */                     if (redirectCount == WebSocketClient.MAX_REDIRECTS) {
/* 332 */                       ioFuture.setException(UndertowMessages.MESSAGES.tooManyRedirects(exception));
/*     */                     } else {
/* 334 */                       String path = ((RedirectException)exception).getLocation();
/*     */                       try {
/* 336 */                         WebSocketClient.ConnectionBuilder.this.connectImpl(new URI(path), ioFuture, redirectCount + 1);
/* 337 */                       } catch (URISyntaxException e) {
/* 338 */                         ioFuture.setException(new IOException(e));
/*     */                       } 
/*     */                     } 
/*     */                   } else {
/* 342 */                     ioFuture.setException(exception);
/*     */                   } 
/*     */                 } 
/*     */               }
/*     */             }null);
/* 347 */         ioFuture.addCancelHandler(new Cancellable()
/*     */             {
/*     */               public Cancellable cancel() {
/* 350 */                 result.cancel();
/* 351 */                 return null;
/*     */               }
/*     */             });
/*     */       } 
/* 355 */       return ioFuture.getIoFuture();
/*     */     }
/*     */     
/*     */     private boolean isSecure(URI uri) {
/* 359 */       return (uri.getScheme().equals("wss") || uri.getScheme().equals("https"));
/*     */     }
/*     */     
/*     */     private class WebsocketConnectionListener implements ChannelListener<StreamConnection> {
/*     */       private final OptionMap options;
/*     */       private final WebSocketClientHandshake handshake;
/*     */       private final URI newUri;
/*     */       private final FutureResult<WebSocketChannel> ioFuture;
/*     */       
/*     */       WebsocketConnectionListener(OptionMap options, WebSocketClientHandshake handshake, URI newUri, FutureResult<WebSocketChannel> ioFuture) {
/* 369 */         this.options = options;
/* 370 */         this.handshake = handshake;
/* 371 */         this.newUri = newUri;
/* 372 */         this.ioFuture = ioFuture;
/*     */       }
/*     */ 
/*     */       
/*     */       public void handleEvent(StreamConnection channel) {
/* 377 */         WebSocketChannel result = this.handshake.createChannel(channel, this.newUri.toString(), WebSocketClient.ConnectionBuilder.this.bufferPool, this.options);
/* 378 */         this.ioFuture.setResult(result);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ConnectionBuilder connectionBuilder(XnioWorker worker, ByteBufferPool bufferPool, URI uri) {
/* 391 */     return new ConnectionBuilder(worker, bufferPool, uri);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\client\WebSocketClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */