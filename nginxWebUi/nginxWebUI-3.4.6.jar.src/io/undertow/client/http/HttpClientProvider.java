/*     */ package io.undertow.client.http;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.UndertowOptions;
/*     */ import io.undertow.client.ALPNClientSelector;
/*     */ import io.undertow.client.ClientCallback;
/*     */ import io.undertow.client.ClientConnection;
/*     */ import io.undertow.client.ClientProvider;
/*     */ import io.undertow.client.http2.Http2ClientProvider;
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URI;
/*     */ import java.nio.channels.Channel;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.IoFuture;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.Options;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
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
/*     */ public class HttpClientProvider
/*     */   implements ClientProvider
/*     */ {
/*     */   public Set<String> handlesSchemes() {
/*  55 */     return new HashSet<>(Arrays.asList(new String[] { "http", "https" }));
/*     */   }
/*     */ 
/*     */   
/*     */   public void connect(ClientCallback<ClientConnection> listener, URI uri, XnioWorker worker, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
/*  60 */     connect(listener, (InetSocketAddress)null, uri, worker, ssl, bufferPool, options);
/*     */   }
/*     */ 
/*     */   
/*     */   public void connect(ClientCallback<ClientConnection> listener, URI uri, XnioIoThread ioThread, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
/*  65 */     connect(listener, (InetSocketAddress)null, uri, ioThread, ssl, bufferPool, options);
/*     */   }
/*     */ 
/*     */   
/*     */   public void connect(ClientCallback<ClientConnection> listener, InetSocketAddress bindAddress, URI uri, XnioWorker worker, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
/*  70 */     if (uri.getScheme().equals("https")) {
/*  71 */       if (ssl == null) {
/*  72 */         listener.failed(UndertowMessages.MESSAGES.sslWasNull());
/*     */         return;
/*     */       } 
/*  75 */       OptionMap tlsOptions = OptionMap.builder().addAll(options).set(Options.SSL_STARTTLS, true).getMap();
/*  76 */       if (bindAddress == null) {
/*  77 */         ssl.openSslConnection(worker, new InetSocketAddress(uri.getHost(), (uri.getPort() == -1) ? 443 : uri.getPort()), createOpenListener(listener, bufferPool, tlsOptions, uri), tlsOptions).addNotifier(createNotifier(listener), null);
/*     */       } else {
/*  79 */         ssl.openSslConnection(worker, bindAddress, new InetSocketAddress(uri.getHost(), (uri.getPort() == -1) ? 443 : uri.getPort()), createOpenListener(listener, bufferPool, tlsOptions, uri), tlsOptions).addNotifier(createNotifier(listener), null);
/*     */       }
/*     */     
/*  82 */     } else if (bindAddress == null) {
/*  83 */       worker.openStreamConnection(new InetSocketAddress(uri.getHost(), (uri.getPort() == -1) ? 80 : uri.getPort()), createOpenListener(listener, bufferPool, options, uri), options).addNotifier(createNotifier(listener), null);
/*     */     } else {
/*  85 */       worker.openStreamConnection(bindAddress, new InetSocketAddress(uri.getHost(), (uri.getPort() == -1) ? 80 : uri.getPort()), createOpenListener(listener, bufferPool, options, uri), null, options).addNotifier(createNotifier(listener), null);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void connect(ClientCallback<ClientConnection> listener, InetSocketAddress bindAddress, URI uri, XnioIoThread ioThread, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
/*  92 */     if (uri.getScheme().equals("https")) {
/*  93 */       if (ssl == null) {
/*  94 */         listener.failed(UndertowMessages.MESSAGES.sslWasNull());
/*     */         return;
/*     */       } 
/*  97 */       OptionMap tlsOptions = OptionMap.builder().addAll(options).set(Options.SSL_STARTTLS, true).getMap();
/*  98 */       if (bindAddress == null) {
/*  99 */         ssl.openSslConnection(ioThread, new InetSocketAddress(uri.getHost(), (uri.getPort() == -1) ? 443 : uri.getPort()), createOpenListener(listener, bufferPool, tlsOptions, uri), tlsOptions).addNotifier(createNotifier(listener), null);
/*     */       } else {
/* 101 */         ssl.openSslConnection(ioThread, bindAddress, new InetSocketAddress(uri.getHost(), (uri.getPort() == -1) ? 443 : uri.getPort()), createOpenListener(listener, bufferPool, tlsOptions, uri), tlsOptions).addNotifier(createNotifier(listener), null);
/*     */       }
/*     */     
/* 104 */     } else if (bindAddress == null) {
/* 105 */       ioThread.openStreamConnection(new InetSocketAddress(uri.getHost(), (uri.getPort() == -1) ? 80 : uri.getPort()), createOpenListener(listener, bufferPool, options, uri), options).addNotifier(createNotifier(listener), null);
/*     */     } else {
/* 107 */       ioThread.openStreamConnection(bindAddress, new InetSocketAddress(uri.getHost(), (uri.getPort() == -1) ? 80 : uri.getPort()), createOpenListener(listener, bufferPool, options, uri), null, options).addNotifier(createNotifier(listener), null);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private IoFuture.Notifier<StreamConnection, Object> createNotifier(final ClientCallback<ClientConnection> listener) {
/* 113 */     return new IoFuture.Notifier<StreamConnection, Object>()
/*     */       {
/*     */         public void notify(IoFuture<? extends StreamConnection> ioFuture, Object o) {
/* 116 */           if (ioFuture.getStatus() == IoFuture.Status.FAILED) {
/* 117 */             listener.failed(ioFuture.getException());
/*     */           }
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   private ChannelListener<StreamConnection> createOpenListener(final ClientCallback<ClientConnection> listener, final ByteBufferPool bufferPool, final OptionMap options, final URI uri) {
/* 124 */     return new ChannelListener<StreamConnection>()
/*     */       {
/*     */         public void handleEvent(StreamConnection connection) {
/* 127 */           HttpClientProvider.this.handleConnected(connection, listener, bufferPool, options, uri);
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void handleConnected(StreamConnection connection, final ClientCallback<ClientConnection> listener, final ByteBufferPool bufferPool, final OptionMap options, URI uri) {
/* 135 */     boolean h2 = options.get(UndertowOptions.ENABLE_HTTP2, false);
/* 136 */     if (connection instanceof SslConnection && h2) {
/* 137 */       List<ALPNClientSelector.ALPNProtocol> protocolList = new ArrayList<>();
/* 138 */       if (h2) {
/* 139 */         protocolList.add(Http2ClientProvider.alpnProtocol(listener, uri, bufferPool, options));
/*     */       }
/*     */       
/* 142 */       ALPNClientSelector.runAlpn((SslConnection)connection, new ChannelListener<SslConnection>()
/*     */           {
/*     */             public void handleEvent(SslConnection connection) {
/* 145 */               listener.completed(new HttpClientConnection((StreamConnection)connection, options, bufferPool));
/*     */             }
/* 147 */           }listener, protocolList.<ALPNClientSelector.ALPNProtocol>toArray(new ALPNClientSelector.ALPNProtocol[protocolList.size()]));
/*     */     } else {
/* 149 */       if (connection instanceof SslConnection) {
/*     */         try {
/* 151 */           ((SslConnection)connection).startHandshake();
/* 152 */         } catch (Throwable t) {
/* 153 */           listener.failed((t instanceof IOException) ? (IOException)t : new IOException(t));
/*     */         } 
/*     */       }
/* 156 */       listener.completed(new HttpClientConnection(connection, options, bufferPool));
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\client\http\HttpClientProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */