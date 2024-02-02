/*     */ package io.undertow.client.http2;
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
/*     */ import io.undertow.protocols.http2.Http2Channel;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URI;
/*     */ import java.nio.ByteBuffer;
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
/*     */ import org.xnio.conduits.ConduitStreamSinkChannel;
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
/*     */ public class Http2PriorKnowledgeClientProvider
/*     */   implements ClientProvider
/*     */ {
/*  55 */   private static final byte[] PRI_REQUEST = new byte[] { 80, 82, 73, 32, 42, 32, 72, 84, 84, 80, 47, 50, 46, 48, 13, 10, 13, 10, 83, 77, 13, 10, 13, 10 };
/*     */ 
/*     */   
/*     */   public void connect(ClientCallback<ClientConnection> listener, URI uri, XnioWorker worker, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
/*  59 */     connect(listener, (InetSocketAddress)null, uri, worker, ssl, bufferPool, options);
/*     */   }
/*     */ 
/*     */   
/*     */   public void connect(ClientCallback<ClientConnection> listener, URI uri, XnioIoThread ioThread, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
/*  64 */     connect(listener, (InetSocketAddress)null, uri, ioThread, ssl, bufferPool, options);
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<String> handlesSchemes() {
/*  69 */     return new HashSet<>(Arrays.asList(new String[] { "h2c-prior" }));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void connect(ClientCallback<ClientConnection> listener, InetSocketAddress bindAddress, URI uri, XnioWorker worker, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
/*  75 */     if (bindAddress == null) {
/*  76 */       worker.openStreamConnection(new InetSocketAddress(uri.getHost(), (uri.getPort() == -1) ? 80 : uri.getPort()), createOpenListener(listener, bufferPool, options, uri.getHost()), options).addNotifier(createNotifier(listener), null);
/*     */     } else {
/*  78 */       worker.openStreamConnection(bindAddress, new InetSocketAddress(uri.getHost(), (uri.getPort() == -1) ? 80 : uri.getPort()), createOpenListener(listener, bufferPool, options, uri.getHost()), null, options).addNotifier(createNotifier(listener), null);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void connect(ClientCallback<ClientConnection> listener, InetSocketAddress bindAddress, URI uri, XnioIoThread ioThread, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
/*  84 */     if (bindAddress == null) {
/*  85 */       ioThread.openStreamConnection(new InetSocketAddress(uri.getHost(), (uri.getPort() == -1) ? 80 : uri.getPort()), createOpenListener(listener, bufferPool, options, uri.getHost()), options).addNotifier(createNotifier(listener), null);
/*     */     } else {
/*  87 */       ioThread.openStreamConnection(bindAddress, new InetSocketAddress(uri.getHost(), (uri.getPort() == -1) ? 80 : uri.getPort()), createOpenListener(listener, bufferPool, options, uri.getHost()), null, options).addNotifier(createNotifier(listener), null);
/*     */     } 
/*     */   }
/*     */   
/*     */   private IoFuture.Notifier<StreamConnection, Object> createNotifier(final ClientCallback<ClientConnection> listener) {
/*  92 */     return new IoFuture.Notifier<StreamConnection, Object>()
/*     */       {
/*     */         public void notify(IoFuture<? extends StreamConnection> ioFuture, Object o) {
/*  95 */           if (ioFuture.getStatus() == IoFuture.Status.FAILED) {
/*  96 */             listener.failed(ioFuture.getException());
/*     */           }
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   private ChannelListener<StreamConnection> createOpenListener(final ClientCallback<ClientConnection> listener, final ByteBufferPool bufferPool, final OptionMap options, final String defaultHost) {
/* 103 */     return new ChannelListener<StreamConnection>()
/*     */       {
/*     */         public void handleEvent(StreamConnection connection) {
/* 106 */           Http2PriorKnowledgeClientProvider.this.handleConnected(connection, listener, bufferPool, options, defaultHost);
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void handleConnected(final StreamConnection connection, final ClientCallback<ClientConnection> listener, final ByteBufferPool bufferPool, final OptionMap options, final String defaultHost) {
/*     */     try {
/*     */       final ClientStatisticsImpl clientStatistics;
/* 116 */       if (options.get(UndertowOptions.ENABLE_STATISTICS, false)) {
/* 117 */         clientStatistics = new ClientStatisticsImpl();
/* 118 */         connection.getSinkChannel().setConduit((StreamSinkConduit)new BytesSentStreamSinkConduit(connection.getSinkChannel().getConduit(), new ByteActivityCallback()
/*     */               {
/*     */                 public void activity(long bytes) {
/* 121 */                   clientStatistics.written = clientStatistics.written + bytes;
/*     */                 }
/*     */               }));
/* 124 */         connection.getSourceChannel().setConduit((StreamSourceConduit)new BytesReceivedStreamSourceConduit(connection.getSourceChannel().getConduit(), new ByteActivityCallback()
/*     */               {
/*     */                 public void activity(long bytes) {
/* 127 */                   clientStatistics.read = clientStatistics.read + bytes;
/*     */                 }
/*     */               }));
/*     */       } else {
/* 131 */         clientStatistics = null;
/*     */       } 
/*     */       
/* 134 */       final ByteBuffer pri = ByteBuffer.wrap(PRI_REQUEST);
/* 135 */       pri.flip();
/* 136 */       ConduitStreamSinkChannel sink = connection.getSinkChannel();
/* 137 */       sink.write(pri);
/* 138 */       if (pri.hasRemaining()) {
/* 139 */         sink.setWriteListener(new ChannelListener<ConduitStreamSinkChannel>()
/*     */             {
/*     */               public void handleEvent(ConduitStreamSinkChannel channel) {
/*     */                 try {
/* 143 */                   channel.write(pri);
/* 144 */                   if (pri.hasRemaining()) {
/*     */                     return;
/*     */                   }
/* 147 */                   listener.completed(new Http2ClientConnection(new Http2Channel(connection, null, bufferPool, null, true, false, options), false, defaultHost, clientStatistics, false));
/* 148 */                 } catch (Throwable t) {
/* 149 */                   IOException e = (t instanceof IOException) ? (IOException)t : new IOException(t);
/* 150 */                   listener.failed(e);
/*     */                 } 
/*     */               }
/*     */             });
/*     */         return;
/*     */       } 
/* 156 */       listener.completed(new Http2ClientConnection(new Http2Channel(connection, null, bufferPool, null, true, false, options), false, defaultHost, clientStatistics, false));
/* 157 */     } catch (Throwable t) {
/* 158 */       IOException e = (t instanceof IOException) ? (IOException)t : new IOException(t);
/* 159 */       listener.failed(e);
/*     */     } 
/*     */   }
/*     */   private static class ClientStatisticsImpl implements ClientStatistics { private long requestCount;
/*     */     private long read;
/*     */     
/*     */     public long getRequests() {
/* 166 */       return this.requestCount;
/*     */     }
/*     */     private long written;
/*     */     private ClientStatisticsImpl() {}
/*     */     public long getRead() {
/* 171 */       return this.read;
/*     */     }
/*     */ 
/*     */     
/*     */     public long getWritten() {
/* 176 */       return this.written;
/*     */     }
/*     */ 
/*     */     
/*     */     public void reset() {
/* 181 */       this.read = 0L;
/* 182 */       this.written = 0L;
/* 183 */       this.requestCount = 0L;
/*     */     } }
/*     */ 
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\client\http2\Http2PriorKnowledgeClientProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */