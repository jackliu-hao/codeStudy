/*     */ package io.undertow.client.ajp;
/*     */ 
/*     */ import io.undertow.UndertowOptions;
/*     */ import io.undertow.client.ClientCallback;
/*     */ import io.undertow.client.ClientConnection;
/*     */ import io.undertow.client.ClientProvider;
/*     */ import io.undertow.client.ClientStatistics;
/*     */ import io.undertow.conduits.ByteActivityCallback;
/*     */ import io.undertow.conduits.BytesReceivedStreamSourceConduit;
/*     */ import io.undertow.conduits.BytesSentStreamSinkConduit;
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.protocols.ajp.AjpClientChannel;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URI;
/*     */ import java.nio.channels.Channel;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.IoFuture;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio.conduits.StreamSinkConduit;
/*     */ import org.xnio.conduits.StreamSourceConduit;
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
/*     */ public class AjpClientProvider
/*     */   implements ClientProvider
/*     */ {
/*     */   public Set<String> handlesSchemes() {
/*  53 */     return new HashSet<>(Arrays.asList(new String[] { "ajp" }));
/*     */   }
/*     */ 
/*     */   
/*     */   public void connect(ClientCallback<ClientConnection> listener, URI uri, XnioWorker worker, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
/*  58 */     connect(listener, (InetSocketAddress)null, uri, worker, ssl, bufferPool, options);
/*     */   }
/*     */ 
/*     */   
/*     */   public void connect(ClientCallback<ClientConnection> listener, URI uri, XnioIoThread ioThread, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
/*  63 */     connect(listener, (InetSocketAddress)null, uri, ioThread, ssl, bufferPool, options);
/*     */   }
/*     */ 
/*     */   
/*     */   public void connect(final ClientCallback<ClientConnection> listener, InetSocketAddress bindAddress, final URI uri, XnioWorker worker, final XnioSsl ssl, final ByteBufferPool bufferPool, final OptionMap options) {
/*  68 */     ChannelListener<StreamConnection> openListener = new ChannelListener<StreamConnection>()
/*     */       {
/*     */         public void handleEvent(StreamConnection connection) {
/*  71 */           AjpClientProvider.this.handleConnected(connection, listener, uri, ssl, bufferPool, options);
/*     */         }
/*     */       };
/*  74 */     IoFuture.Notifier<StreamConnection, Object> notifier = new IoFuture.Notifier<StreamConnection, Object>()
/*     */       {
/*     */         public void notify(IoFuture<? extends StreamConnection> ioFuture, Object o) {
/*  77 */           if (ioFuture.getStatus() == IoFuture.Status.FAILED) {
/*  78 */             listener.failed(ioFuture.getException());
/*     */           }
/*     */         }
/*     */       };
/*  82 */     if (bindAddress == null) {
/*  83 */       worker.openStreamConnection(new InetSocketAddress(uri.getHost(), (uri.getPort() == -1) ? 8009 : uri.getPort()), openListener, options).addNotifier(notifier, null);
/*     */     } else {
/*  85 */       worker.openStreamConnection(bindAddress, new InetSocketAddress(uri.getHost(), (uri.getPort() == -1) ? 8009 : uri.getPort()), openListener, null, options).addNotifier(notifier, null);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void connect(final ClientCallback<ClientConnection> listener, InetSocketAddress bindAddress, final URI uri, XnioIoThread ioThread, final XnioSsl ssl, final ByteBufferPool bufferPool, final OptionMap options) {
/*  91 */     ChannelListener<StreamConnection> openListener = new ChannelListener<StreamConnection>()
/*     */       {
/*     */         public void handleEvent(StreamConnection connection) {
/*  94 */           AjpClientProvider.this.handleConnected(connection, listener, uri, ssl, bufferPool, options);
/*     */         }
/*     */       };
/*  97 */     IoFuture.Notifier<StreamConnection, Object> notifier = new IoFuture.Notifier<StreamConnection, Object>()
/*     */       {
/*     */         public void notify(IoFuture<? extends StreamConnection> ioFuture, Object o) {
/* 100 */           if (ioFuture.getStatus() == IoFuture.Status.FAILED) {
/* 101 */             listener.failed(ioFuture.getException());
/*     */           }
/*     */         }
/*     */       };
/* 105 */     if (bindAddress == null) {
/* 106 */       ioThread.openStreamConnection(new InetSocketAddress(uri.getHost(), (uri.getPort() == -1) ? 8009 : uri.getPort()), openListener, options).addNotifier(notifier, null);
/*     */     } else {
/* 108 */       ioThread.openStreamConnection(bindAddress, new InetSocketAddress(uri.getHost(), (uri.getPort() == -1) ? 8009 : uri.getPort()), openListener, null, options).addNotifier(notifier, null);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void handleConnected(StreamConnection connection, ClientCallback<ClientConnection> listener, URI uri, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
/*     */     final ClientStatisticsImpl clientStatistics;
/* 116 */     if (options.get(UndertowOptions.ENABLE_STATISTICS, false)) {
/* 117 */       clientStatistics = new ClientStatisticsImpl();
/* 118 */       connection.getSinkChannel().setConduit((StreamSinkConduit)new BytesSentStreamSinkConduit(connection.getSinkChannel().getConduit(), new ByteActivityCallback()
/*     */             {
/*     */               public void activity(long bytes) {
/* 121 */                 clientStatistics.written = clientStatistics.written + bytes;
/*     */               }
/*     */             }));
/* 124 */       connection.getSourceChannel().setConduit((StreamSourceConduit)new BytesReceivedStreamSourceConduit(connection.getSourceChannel().getConduit(), new ByteActivityCallback()
/*     */             {
/*     */               public void activity(long bytes) {
/* 127 */                 clientStatistics.read = clientStatistics.read + bytes;
/*     */               }
/*     */             }));
/*     */     } else {
/* 131 */       clientStatistics = null;
/*     */     } 
/*     */     
/* 134 */     listener.completed(new AjpClientConnection(new AjpClientChannel(connection, bufferPool, options), options, bufferPool, clientStatistics));
/*     */   }
/*     */   
/*     */   private static class ClientStatisticsImpl implements ClientStatistics {
/*     */     private long requestCount;
/*     */     private long read;
/*     */     
/*     */     public long getRequests() {
/* 142 */       return this.requestCount;
/*     */     }
/*     */     private long written;
/*     */     private ClientStatisticsImpl() {}
/*     */     public long getRead() {
/* 147 */       return this.read;
/*     */     }
/*     */ 
/*     */     
/*     */     public long getWritten() {
/* 152 */       return this.written;
/*     */     }
/*     */ 
/*     */     
/*     */     public void reset() {
/* 157 */       this.read = 0L;
/* 158 */       this.written = 0L;
/* 159 */       this.requestCount = 0L;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\client\ajp\AjpClientProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */