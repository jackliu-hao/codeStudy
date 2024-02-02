/*     */ package io.undertow.server.protocol.http2;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.UndertowOptions;
/*     */ import io.undertow.conduits.BytesReceivedStreamSourceConduit;
/*     */ import io.undertow.conduits.BytesSentStreamSinkConduit;
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.protocols.http2.Http2Channel;
/*     */ import io.undertow.server.ConnectorStatistics;
/*     */ import io.undertow.server.ConnectorStatisticsImpl;
/*     */ import io.undertow.server.DelegateOpenListener;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.XnioByteBufferPool;
/*     */ import java.io.Closeable;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.util.Collections;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.Pool;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.conduits.StreamSinkConduit;
/*     */ import org.xnio.conduits.StreamSourceConduit;
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
/*     */ public final class Http2OpenListener
/*     */   implements ChannelListener<StreamConnection>, DelegateOpenListener
/*     */ {
/*  55 */   private final Set<Http2Channel> connections = Collections.newSetFromMap(new ConcurrentHashMap<>());
/*     */   
/*     */   public static final String HTTP2 = "h2";
/*     */   
/*     */   @Deprecated
/*     */   public static final String HTTP2_14 = "h2-14";
/*     */   private final ByteBufferPool bufferPool;
/*     */   private final int bufferSize;
/*     */   
/*  64 */   private final ChannelListener<Http2Channel> closeTask = new ChannelListener<Http2Channel>()
/*     */     {
/*     */       public void handleEvent(Http2Channel channel) {
/*  67 */         Http2OpenListener.this.connectorStatistics.decrementConnectionCount();
/*     */       }
/*     */     };
/*     */ 
/*     */   
/*     */   private volatile HttpHandler rootHandler;
/*     */   private volatile OptionMap undertowOptions;
/*     */   private volatile boolean statisticsEnabled;
/*     */   private final ConnectorStatisticsImpl connectorStatistics;
/*     */   private final String protocol;
/*     */   
/*     */   @Deprecated
/*     */   public Http2OpenListener(Pool<ByteBuffer> pool) {
/*  80 */     this(pool, OptionMap.EMPTY);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public Http2OpenListener(Pool<ByteBuffer> pool, OptionMap undertowOptions) {
/*  85 */     this(pool, undertowOptions, "h2");
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public Http2OpenListener(Pool<ByteBuffer> pool, OptionMap undertowOptions, String protocol) {
/*  90 */     this((ByteBufferPool)new XnioByteBufferPool(pool), undertowOptions, protocol);
/*     */   }
/*     */   
/*     */   public Http2OpenListener(ByteBufferPool pool) {
/*  94 */     this(pool, OptionMap.EMPTY);
/*     */   }
/*     */   
/*     */   public Http2OpenListener(ByteBufferPool pool, OptionMap undertowOptions) {
/*  98 */     this(pool, undertowOptions, "h2");
/*     */   }
/*     */   
/*     */   public Http2OpenListener(ByteBufferPool pool, OptionMap undertowOptions, String protocol) {
/* 102 */     this.undertowOptions = undertowOptions;
/* 103 */     this.bufferPool = pool;
/* 104 */     PooledByteBuffer buf = pool.allocate();
/* 105 */     this.bufferSize = buf.getBuffer().remaining();
/* 106 */     buf.close();
/* 107 */     this.connectorStatistics = new ConnectorStatisticsImpl();
/* 108 */     this.statisticsEnabled = undertowOptions.get(UndertowOptions.ENABLE_STATISTICS, false);
/* 109 */     this.protocol = protocol;
/*     */   }
/*     */   
/*     */   public void handleEvent(StreamConnection channel, PooledByteBuffer buffer) {
/* 113 */     if (UndertowLogger.REQUEST_LOGGER.isTraceEnabled()) {
/* 114 */       UndertowLogger.REQUEST_LOGGER.tracef("Opened HTTP/2 connection with %s", channel.getPeerAddress());
/*     */     }
/*     */ 
/*     */     
/* 118 */     Http2Channel http2Channel = new Http2Channel(channel, this.protocol, this.bufferPool, buffer, false, false, this.undertowOptions);
/* 119 */     Integer idleTimeout = (Integer)this.undertowOptions.get(UndertowOptions.IDLE_TIMEOUT);
/* 120 */     if (idleTimeout != null && idleTimeout.intValue() > 0) {
/* 121 */       http2Channel.setIdleTimeout(idleTimeout.intValue());
/*     */     }
/* 123 */     if (this.statisticsEnabled) {
/* 124 */       channel.getSinkChannel().setConduit((StreamSinkConduit)new BytesSentStreamSinkConduit(channel.getSinkChannel().getConduit(), this.connectorStatistics.sentAccumulator()));
/* 125 */       channel.getSourceChannel().setConduit((StreamSourceConduit)new BytesReceivedStreamSourceConduit(channel.getSourceChannel().getConduit(), this.connectorStatistics.receivedAccumulator()));
/* 126 */       this.connectorStatistics.incrementConnectionCount();
/* 127 */       http2Channel.addCloseTask(this.closeTask);
/*     */     } 
/*     */     
/* 130 */     this.connections.add(http2Channel);
/* 131 */     http2Channel.addCloseTask(new ChannelListener<Http2Channel>()
/*     */         {
/*     */           public void handleEvent(Http2Channel channel) {
/* 134 */             Http2OpenListener.this.connections.remove(channel);
/*     */           }
/*     */         });
/* 137 */     http2Channel.getReceiveSetter().set(new Http2ReceiveListener(this.rootHandler, getUndertowOptions(), this.bufferSize, this.connectorStatistics));
/* 138 */     http2Channel.resumeReceives();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ConnectorStatistics getConnectorStatistics() {
/* 144 */     if (this.statisticsEnabled) {
/* 145 */       return (ConnectorStatistics)this.connectorStatistics;
/*     */     }
/* 147 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void closeConnections() {
/* 152 */     for (Http2Channel i : this.connections) {
/* 153 */       IoUtils.safeClose((Closeable)i);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHandler getRootHandler() {
/* 159 */     return this.rootHandler;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setRootHandler(HttpHandler rootHandler) {
/* 164 */     this.rootHandler = rootHandler;
/*     */   }
/*     */ 
/*     */   
/*     */   public OptionMap getUndertowOptions() {
/* 169 */     return this.undertowOptions;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setUndertowOptions(OptionMap undertowOptions) {
/* 174 */     if (undertowOptions == null) {
/* 175 */       throw UndertowMessages.MESSAGES.argumentCannotBeNull("undertowOptions");
/*     */     }
/* 177 */     this.undertowOptions = undertowOptions;
/* 178 */     this.statisticsEnabled = undertowOptions.get(UndertowOptions.ENABLE_CONNECTOR_STATISTICS, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBufferPool getBufferPool() {
/* 183 */     return this.bufferPool;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleEvent(StreamConnection channel) {
/* 188 */     handleEvent(channel, null);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\http2\Http2OpenListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */