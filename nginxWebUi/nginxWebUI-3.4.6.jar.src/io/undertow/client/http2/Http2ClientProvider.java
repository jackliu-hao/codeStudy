/*     */ package io.undertow.client.http2;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.UndertowOptions;
/*     */ import io.undertow.client.ALPNClientSelector;
/*     */ import io.undertow.client.ClientCallback;
/*     */ import io.undertow.client.ClientConnection;
/*     */ import io.undertow.client.ClientProvider;
/*     */ import io.undertow.client.ClientStatistics;
/*     */ import io.undertow.conduits.ByteActivityCallback;
/*     */ import io.undertow.conduits.BytesReceivedStreamSourceConduit;
/*     */ import io.undertow.conduits.BytesSentStreamSinkConduit;
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.protocols.http2.Http2Channel;
/*     */ import java.io.Closeable;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URI;
/*     */ import java.nio.channels.Channel;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.IoFuture;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.Options;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio.conduits.StreamSinkConduit;
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
/*     */ public class Http2ClientProvider
/*     */   implements ClientProvider
/*     */ {
/*     */   private static final String HTTP2 = "h2";
/*     */   private static final String HTTP_1_1 = "http/1.1";
/*     */   
/*  61 */   private static final ChannelListener<SslConnection> FAILED = new ChannelListener<SslConnection>()
/*     */     {
/*     */       public void handleEvent(SslConnection connection) {
/*  64 */         UndertowLogger.ROOT_LOGGER.alpnConnectionFailed(connection);
/*  65 */         IoUtils.safeClose((Closeable)connection);
/*     */       }
/*     */     };
/*     */ 
/*     */   
/*     */   public void connect(ClientCallback<ClientConnection> listener, URI uri, XnioWorker worker, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
/*  71 */     connect(listener, (InetSocketAddress)null, uri, worker, ssl, bufferPool, options);
/*     */   }
/*     */ 
/*     */   
/*     */   public void connect(ClientCallback<ClientConnection> listener, URI uri, XnioIoThread ioThread, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
/*  76 */     connect(listener, (InetSocketAddress)null, uri, ioThread, ssl, bufferPool, options);
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<String> handlesSchemes() {
/*  81 */     return new HashSet<>(Arrays.asList(new String[] { "h2" }));
/*     */   }
/*     */ 
/*     */   
/*     */   public void connect(ClientCallback<ClientConnection> listener, InetSocketAddress bindAddress, URI uri, XnioWorker worker, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
/*  86 */     if (ssl == null) {
/*  87 */       listener.failed(UndertowMessages.MESSAGES.sslWasNull());
/*     */       return;
/*     */     } 
/*  90 */     OptionMap tlsOptions = OptionMap.builder().addAll(options).set(Options.SSL_STARTTLS, true).getMap();
/*  91 */     if (bindAddress == null) {
/*  92 */       ssl.openSslConnection(worker, new InetSocketAddress(uri.getHost(), (uri.getPort() == -1) ? 443 : uri.getPort()), createOpenListener(listener, uri, ssl, bufferPool, tlsOptions), tlsOptions).addNotifier(createNotifier(listener), null);
/*     */     } else {
/*  94 */       ssl.openSslConnection(worker, bindAddress, new InetSocketAddress(uri.getHost(), (uri.getPort() == -1) ? 443 : uri.getPort()), createOpenListener(listener, uri, ssl, bufferPool, tlsOptions), tlsOptions).addNotifier(createNotifier(listener), null);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void connect(ClientCallback<ClientConnection> listener, InetSocketAddress bindAddress, URI uri, XnioIoThread ioThread, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
/* 101 */     if (ssl == null) {
/* 102 */       listener.failed(UndertowMessages.MESSAGES.sslWasNull());
/*     */       return;
/*     */     } 
/* 105 */     if (bindAddress == null) {
/* 106 */       OptionMap tlsOptions = OptionMap.builder().addAll(options).set(Options.SSL_STARTTLS, true).getMap();
/* 107 */       ssl.openSslConnection(ioThread, new InetSocketAddress(uri.getHost(), (uri.getPort() == -1) ? 443 : uri.getPort()), createOpenListener(listener, uri, ssl, bufferPool, tlsOptions), options).addNotifier(createNotifier(listener), null);
/*     */     } else {
/* 109 */       ssl.openSslConnection(ioThread, bindAddress, new InetSocketAddress(uri.getHost(), (uri.getPort() == -1) ? 443 : uri.getPort()), createOpenListener(listener, uri, ssl, bufferPool, options), options).addNotifier(createNotifier(listener), null);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private IoFuture.Notifier<StreamConnection, Object> createNotifier(final ClientCallback<ClientConnection> listener) {
/* 115 */     return new IoFuture.Notifier<StreamConnection, Object>()
/*     */       {
/*     */         public void notify(IoFuture<? extends StreamConnection> ioFuture, Object o) {
/* 118 */           if (ioFuture.getStatus() == IoFuture.Status.FAILED) {
/* 119 */             listener.failed(ioFuture.getException());
/*     */           }
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   private ChannelListener<StreamConnection> createOpenListener(final ClientCallback<ClientConnection> listener, final URI uri, XnioSsl ssl, final ByteBufferPool bufferPool, final OptionMap options) {
/* 126 */     return new ChannelListener<StreamConnection>()
/*     */       {
/*     */         public void handleEvent(StreamConnection connection) {
/* 129 */           Http2ClientProvider.this.handleConnected(connection, listener, uri, bufferPool, options);
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   public static ALPNClientSelector.ALPNProtocol alpnProtocol(final ClientCallback<ClientConnection> listener, final URI uri, final ByteBufferPool bufferPool, final OptionMap options) {
/* 135 */     return new ALPNClientSelector.ALPNProtocol(new ChannelListener<SslConnection>()
/*     */         {
/*     */           public void handleEvent(SslConnection connection) {
/* 138 */             listener.completed(Http2ClientProvider.createHttp2Channel((StreamConnection)connection, bufferPool, options, uri.getHost()));
/*     */           }
/*     */         }"h2");
/*     */   }
/*     */   
/*     */   private void handleConnected(StreamConnection connection, ClientCallback<ClientConnection> listener, URI uri, ByteBufferPool bufferPool, OptionMap options) {
/* 144 */     ALPNClientSelector.runAlpn((SslConnection)connection, FAILED, listener, new ALPNClientSelector.ALPNProtocol[] { alpnProtocol(listener, uri, bufferPool, options) });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static Http2ClientConnection createHttp2Channel(StreamConnection connection, ByteBufferPool bufferPool, OptionMap options, String defaultHost) {
/*     */     final ClientStatisticsImpl clientStatistics;
/* 151 */     if (options.get(UndertowOptions.ENABLE_STATISTICS, false)) {
/* 152 */       clientStatistics = new ClientStatisticsImpl();
/* 153 */       connection.getSinkChannel().setConduit((StreamSinkConduit)new BytesSentStreamSinkConduit(connection.getSinkChannel().getConduit(), new ByteActivityCallback()
/*     */             {
/*     */               public void activity(long bytes) {
/* 156 */                 clientStatistics.written = clientStatistics.written + bytes;
/*     */               }
/*     */             }));
/* 159 */       connection.getSourceChannel().setConduit((StreamSourceConduit)new BytesReceivedStreamSourceConduit(connection.getSourceChannel().getConduit(), new ByteActivityCallback()
/*     */             {
/*     */               public void activity(long bytes) {
/* 162 */                 clientStatistics.read = clientStatistics.read + bytes;
/*     */               }
/*     */             }));
/*     */     } else {
/* 166 */       clientStatistics = null;
/*     */     } 
/* 168 */     Http2Channel http2Channel = new Http2Channel(connection, null, bufferPool, null, true, false, options);
/* 169 */     return new Http2ClientConnection(http2Channel, false, defaultHost, clientStatistics, true);
/*     */   }
/*     */   
/*     */   private static class ClientStatisticsImpl implements ClientStatistics { private long requestCount;
/*     */     private long read;
/*     */     
/*     */     public long getRequests() {
/* 176 */       return this.requestCount;
/*     */     }
/*     */     private long written;
/*     */     private ClientStatisticsImpl() {}
/*     */     public long getRead() {
/* 181 */       return this.read;
/*     */     }
/*     */ 
/*     */     
/*     */     public long getWritten() {
/* 186 */       return this.written;
/*     */     }
/*     */ 
/*     */     
/*     */     public void reset() {
/* 191 */       this.read = 0L;
/* 192 */       this.written = 0L;
/* 193 */       this.requestCount = 0L;
/*     */     } }
/*     */ 
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\client\http2\Http2ClientProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */