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
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.protocols.http2.Http2Channel;
/*     */ import io.undertow.util.FlexBase64;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.IoFuture;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio.channels.BoundChannel;
/*     */ import org.xnio.conduits.StreamSinkConduit;
/*     */ import org.xnio.conduits.StreamSourceConduit;
/*     */ import org.xnio.http.HttpUpgrade;
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
/*     */ public class Http2ClearClientProvider
/*     */   implements ClientProvider
/*     */ {
/*     */   public void connect(ClientCallback<ClientConnection> listener, URI uri, XnioWorker worker, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
/*  69 */     connect(listener, (InetSocketAddress)null, uri, worker, ssl, bufferPool, options);
/*     */   }
/*     */ 
/*     */   
/*     */   public void connect(ClientCallback<ClientConnection> listener, URI uri, XnioIoThread ioThread, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
/*  74 */     connect(listener, (InetSocketAddress)null, uri, ioThread, ssl, bufferPool, options);
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<String> handlesSchemes() {
/*  79 */     return new HashSet<>(Arrays.asList(new String[] { "h2c" }));
/*     */   }
/*     */ 
/*     */   
/*     */   public void connect(ClientCallback<ClientConnection> listener, InetSocketAddress bindAddress, URI uri, XnioWorker worker, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
/*     */     URI upgradeUri;
/*     */     try {
/*  86 */       upgradeUri = new URI("http", uri.getUserInfo(), uri.getHost(), uri.getPort(), uri.getPath(), uri.getQuery(), uri.getFragment());
/*  87 */     } catch (URISyntaxException e) {
/*  88 */       listener.failed(new IOException(e));
/*     */       return;
/*     */     } 
/*  91 */     Map<String, String> headers = createHeaders(options, bufferPool, uri);
/*  92 */     HttpUpgrade.performUpgrade(worker, bindAddress, upgradeUri, headers, new Http2ClearOpenListener(bufferPool, options, listener, uri.getHost()), null, options, null).addNotifier(new FailedNotifier(listener), null);
/*     */   }
/*     */ 
/*     */   
/*     */   public void connect(final ClientCallback<ClientConnection> listener, InetSocketAddress bindAddress, final URI uri, XnioIoThread ioThread, XnioSsl ssl, final ByteBufferPool bufferPool, final OptionMap options) {
/*     */     final URI upgradeUri;
/*     */     try {
/*  99 */       upgradeUri = new URI("http", uri.getUserInfo(), uri.getHost(), uri.getPort(), uri.getPath(), uri.getQuery(), uri.getFragment());
/* 100 */     } catch (URISyntaxException e) {
/* 101 */       listener.failed(new IOException(e));
/*     */       
/*     */       return;
/*     */     } 
/* 105 */     if (bindAddress != null) {
/* 106 */       ioThread.openStreamConnection(bindAddress, new InetSocketAddress(uri.getHost(), uri.getPort()), new ChannelListener<StreamConnection>()
/*     */           {
/*     */             public void handleEvent(StreamConnection channel) {
/* 109 */               Map<String, String> headers = Http2ClearClientProvider.this.createHeaders(options, bufferPool, uri);
/* 110 */               HttpUpgrade.performUpgrade(channel, upgradeUri, headers, new Http2ClearClientProvider.Http2ClearOpenListener(bufferPool, options, listener, uri.getHost()), null).addNotifier(new Http2ClearClientProvider.FailedNotifier(listener), null);
/*     */             }
/*     */           }new ChannelListener<BoundChannel>()
/*     */           {
/*     */             
/*     */             public void handleEvent(BoundChannel channel) {}
/*     */           }, 
/* 117 */           options).addNotifier(new FailedNotifier(listener), null);
/*     */     } else {
/* 119 */       ioThread.openStreamConnection(new InetSocketAddress(uri.getHost(), uri.getPort()), new ChannelListener<StreamConnection>()
/*     */           {
/*     */             public void handleEvent(StreamConnection channel) {
/* 122 */               Map<String, String> headers = Http2ClearClientProvider.this.createHeaders(options, bufferPool, uri);
/* 123 */               HttpUpgrade.performUpgrade(channel, upgradeUri, headers, new Http2ClearClientProvider.Http2ClearOpenListener(bufferPool, options, listener, uri.getHost()), null).addNotifier(new Http2ClearClientProvider.FailedNotifier(listener), null);
/*     */             }
/*     */           }new ChannelListener<BoundChannel>()
/*     */           {
/*     */             
/*     */             public void handleEvent(BoundChannel channel) {}
/*     */           }, 
/* 130 */           options).addNotifier(new FailedNotifier(listener), null);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private Map<String, String> createHeaders(OptionMap options, ByteBufferPool bufferPool, URI uri) {
/* 136 */     Map<String, String> headers = new HashMap<>();
/* 137 */     headers.put("HTTP2-Settings", createSettingsFrame(options, bufferPool));
/* 138 */     headers.put("Upgrade", "h2c");
/* 139 */     headers.put("Connection", "Upgrade, HTTP2-Settings");
/* 140 */     headers.put("Host", uri.getHost());
/* 141 */     headers.put("X-HTTP2-connect-only", "connect");
/* 142 */     return headers;
/*     */   }
/*     */ 
/*     */   
/*     */   public static String createSettingsFrame(OptionMap options, ByteBufferPool bufferPool) {
/* 147 */     PooledByteBuffer b = bufferPool.allocate();
/*     */     try {
/* 149 */       ByteBuffer currentBuffer = b.getBuffer();
/*     */       
/* 151 */       if (options.contains(UndertowOptions.HTTP2_SETTINGS_HEADER_TABLE_SIZE)) {
/* 152 */         pushOption(currentBuffer, 1, ((Integer)options.get(UndertowOptions.HTTP2_SETTINGS_HEADER_TABLE_SIZE)).intValue());
/*     */       }
/* 154 */       if (options.contains(UndertowOptions.HTTP2_SETTINGS_ENABLE_PUSH)) {
/* 155 */         pushOption(currentBuffer, 2, ((Boolean)options.get(UndertowOptions.HTTP2_SETTINGS_ENABLE_PUSH)).booleanValue() ? 1 : 0);
/*     */       }
/*     */       
/* 158 */       if (options.contains(UndertowOptions.HTTP2_SETTINGS_MAX_CONCURRENT_STREAMS)) {
/* 159 */         pushOption(currentBuffer, 3, ((Integer)options.get(UndertowOptions.HTTP2_SETTINGS_MAX_CONCURRENT_STREAMS)).intValue());
/*     */       }
/*     */       
/* 162 */       if (options.contains(UndertowOptions.HTTP2_SETTINGS_INITIAL_WINDOW_SIZE)) {
/* 163 */         pushOption(currentBuffer, 4, ((Integer)options.get(UndertowOptions.HTTP2_SETTINGS_INITIAL_WINDOW_SIZE)).intValue());
/*     */       }
/*     */       
/* 166 */       if (options.contains(UndertowOptions.HTTP2_SETTINGS_MAX_FRAME_SIZE)) {
/* 167 */         pushOption(currentBuffer, 5, ((Integer)options.get(UndertowOptions.HTTP2_SETTINGS_MAX_FRAME_SIZE)).intValue());
/*     */       }
/*     */       
/* 170 */       if (options.contains(UndertowOptions.HTTP2_SETTINGS_MAX_HEADER_LIST_SIZE)) {
/* 171 */         pushOption(currentBuffer, 6, ((Integer)options.get(UndertowOptions.HTTP2_SETTINGS_MAX_HEADER_LIST_SIZE)).intValue());
/* 172 */       } else if (options.contains(UndertowOptions.MAX_HEADER_SIZE)) {
/* 173 */         pushOption(currentBuffer, 6, ((Integer)options.get(UndertowOptions.HTTP2_SETTINGS_MAX_HEADER_LIST_SIZE)).intValue());
/*     */       } 
/* 175 */       currentBuffer.flip();
/* 176 */       return FlexBase64.encodeStringURL(currentBuffer, false);
/*     */     } finally {
/* 178 */       b.close();
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void pushOption(ByteBuffer currentBuffer, int id, int value) {
/* 183 */     currentBuffer.put((byte)(id >> 8 & 0xFF));
/* 184 */     currentBuffer.put((byte)(id & 0xFF));
/* 185 */     currentBuffer.put((byte)(value >> 24 & 0xFF));
/* 186 */     currentBuffer.put((byte)(value >> 16 & 0xFF));
/* 187 */     currentBuffer.put((byte)(value >> 8 & 0xFF));
/* 188 */     currentBuffer.put((byte)(value & 0xFF));
/*     */   }
/*     */   
/*     */   private static class Http2ClearOpenListener
/*     */     implements ChannelListener<StreamConnection> {
/*     */     private final ByteBufferPool bufferPool;
/*     */     private final OptionMap options;
/*     */     private final ClientCallback<ClientConnection> listener;
/*     */     private final String defaultHost;
/*     */     
/*     */     Http2ClearOpenListener(ByteBufferPool bufferPool, OptionMap options, ClientCallback<ClientConnection> listener, String defaultHost) {
/* 199 */       this.bufferPool = bufferPool;
/* 200 */       this.options = options;
/* 201 */       this.listener = listener;
/* 202 */       this.defaultHost = defaultHost;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void handleEvent(StreamConnection channel) {
/*     */       final Http2ClearClientProvider.ClientStatisticsImpl clientStatistics;
/* 210 */       if (this.options.get(UndertowOptions.ENABLE_STATISTICS, false)) {
/* 211 */         clientStatistics = new Http2ClearClientProvider.ClientStatisticsImpl();
/* 212 */         channel.getSinkChannel().setConduit((StreamSinkConduit)new BytesSentStreamSinkConduit(channel.getSinkChannel().getConduit(), new ByteActivityCallback()
/*     */               {
/*     */                 public void activity(long bytes) {
/* 215 */                   clientStatistics.written = clientStatistics.written + bytes;
/*     */                 }
/*     */               }));
/* 218 */         channel.getSourceChannel().setConduit((StreamSourceConduit)new BytesReceivedStreamSourceConduit(channel.getSourceChannel().getConduit(), new ByteActivityCallback()
/*     */               {
/*     */                 public void activity(long bytes) {
/* 221 */                   clientStatistics.read = clientStatistics.read + bytes;
/*     */                 }
/*     */               }));
/*     */       } else {
/* 225 */         clientStatistics = null;
/*     */       } 
/*     */       
/* 228 */       Http2Channel http2Channel = new Http2Channel(channel, null, this.bufferPool, null, true, true, this.options);
/* 229 */       Http2ClientConnection http2ClientConnection = new Http2ClientConnection(http2Channel, true, this.defaultHost, clientStatistics, false);
/*     */       
/* 231 */       this.listener.completed(http2ClientConnection);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class FailedNotifier implements IoFuture.Notifier<StreamConnection, Object> {
/*     */     private final ClientCallback<ClientConnection> listener;
/*     */     
/*     */     FailedNotifier(ClientCallback<ClientConnection> listener) {
/* 239 */       this.listener = listener;
/*     */     }
/*     */ 
/*     */     
/*     */     public void notify(IoFuture<? extends StreamConnection> ioFuture, Object attachment) {
/* 244 */       if (ioFuture.getStatus() == IoFuture.Status.FAILED)
/* 245 */         this.listener.failed(ioFuture.getException()); 
/*     */     } }
/*     */   
/*     */   private static class ClientStatisticsImpl implements ClientStatistics {
/*     */     private long requestCount;
/*     */     private long read;
/*     */     
/*     */     public long getRequestCount() {
/* 253 */       return this.requestCount;
/*     */     } private long written;
/*     */     private ClientStatisticsImpl() {}
/*     */     public void setRequestCount(long requestCount) {
/* 257 */       this.requestCount = requestCount;
/*     */     }
/*     */     
/*     */     public void setRead(long read) {
/* 261 */       this.read = read;
/*     */     }
/*     */     
/*     */     public void setWritten(long written) {
/* 265 */       this.written = written;
/*     */     }
/*     */ 
/*     */     
/*     */     public long getRequests() {
/* 270 */       return this.requestCount;
/*     */     }
/*     */ 
/*     */     
/*     */     public long getRead() {
/* 275 */       return this.read;
/*     */     }
/*     */ 
/*     */     
/*     */     public long getWritten() {
/* 280 */       return this.written;
/*     */     }
/*     */ 
/*     */     
/*     */     public void reset() {
/* 285 */       this.read = 0L;
/* 286 */       this.written = 0L;
/* 287 */       this.requestCount = 0L;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\client\http2\Http2ClearClientProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */