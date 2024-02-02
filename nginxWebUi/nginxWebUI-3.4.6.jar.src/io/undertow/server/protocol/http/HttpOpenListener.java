/*     */ package io.undertow.server.protocol.http;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.UndertowOptions;
/*     */ import io.undertow.conduits.BytesReceivedStreamSourceConduit;
/*     */ import io.undertow.conduits.BytesSentStreamSinkConduit;
/*     */ import io.undertow.conduits.IdleTimeoutConduit;
/*     */ import io.undertow.conduits.ReadTimeoutStreamSourceConduit;
/*     */ import io.undertow.conduits.WriteTimeoutStreamSinkConduit;
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.server.ConnectorStatistics;
/*     */ import io.undertow.server.ConnectorStatisticsImpl;
/*     */ import io.undertow.server.DelegateOpenListener;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.OpenListener;
/*     */ import io.undertow.server.ServerConnection;
/*     */ import io.undertow.server.XnioByteBufferPool;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.util.Collections;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.Options;
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
/*     */ public final class HttpOpenListener
/*     */   implements ChannelListener<StreamConnection>, DelegateOpenListener
/*     */ {
/*  59 */   private final Set<HttpServerConnection> connections = Collections.newSetFromMap(new ConcurrentHashMap<>());
/*     */   
/*     */   private final ByteBufferPool bufferPool;
/*     */   
/*     */   private final int bufferSize;
/*     */   
/*     */   private volatile HttpHandler rootHandler;
/*     */   
/*     */   private volatile OptionMap undertowOptions;
/*     */   
/*     */   private volatile HttpRequestParser parser;
/*     */   private volatile boolean statisticsEnabled;
/*     */   private final ConnectorStatisticsImpl connectorStatistics;
/*     */   
/*     */   @Deprecated
/*     */   public HttpOpenListener(Pool<ByteBuffer> pool) {
/*  75 */     this(pool, OptionMap.EMPTY);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public HttpOpenListener(Pool<ByteBuffer> pool, OptionMap undertowOptions) {
/*  80 */     this((ByteBufferPool)new XnioByteBufferPool(pool), undertowOptions);
/*     */   }
/*     */   
/*     */   public HttpOpenListener(ByteBufferPool pool) {
/*  84 */     this(pool, OptionMap.EMPTY);
/*     */   }
/*     */   
/*     */   public HttpOpenListener(ByteBufferPool pool, OptionMap undertowOptions) {
/*  88 */     this.undertowOptions = undertowOptions;
/*  89 */     this.bufferPool = pool;
/*  90 */     PooledByteBuffer buf = pool.allocate();
/*  91 */     this.bufferSize = buf.getBuffer().remaining();
/*  92 */     buf.close();
/*  93 */     this.parser = HttpRequestParser.instance(undertowOptions);
/*  94 */     this.connectorStatistics = new ConnectorStatisticsImpl();
/*  95 */     this.statisticsEnabled = undertowOptions.get(UndertowOptions.ENABLE_CONNECTOR_STATISTICS, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleEvent(StreamConnection channel) {
/* 100 */     handleEvent(channel, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleEvent(StreamConnection channel, PooledByteBuffer buffer) {
/* 105 */     if (UndertowLogger.REQUEST_LOGGER.isTraceEnabled()) {
/* 106 */       UndertowLogger.REQUEST_LOGGER.tracef("Opened connection with %s", channel.getPeerAddress());
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 111 */       Integer readTimeout = (Integer)channel.getOption(Options.READ_TIMEOUT);
/* 112 */       Integer idle = (Integer)this.undertowOptions.get(UndertowOptions.IDLE_TIMEOUT);
/* 113 */       if (idle != null) {
/* 114 */         IdleTimeoutConduit conduit = new IdleTimeoutConduit(channel);
/* 115 */         channel.getSourceChannel().setConduit((StreamSourceConduit)conduit);
/* 116 */         channel.getSinkChannel().setConduit((StreamSinkConduit)conduit);
/*     */       } 
/* 118 */       if (readTimeout != null && readTimeout.intValue() > 0) {
/* 119 */         channel.getSourceChannel().setConduit((StreamSourceConduit)new ReadTimeoutStreamSourceConduit(channel.getSourceChannel().getConduit(), channel, (OpenListener)this));
/*     */       }
/* 121 */       Integer writeTimeout = (Integer)channel.getOption(Options.WRITE_TIMEOUT);
/* 122 */       if (writeTimeout != null && writeTimeout.intValue() > 0) {
/* 123 */         channel.getSinkChannel().setConduit((StreamSinkConduit)new WriteTimeoutStreamSinkConduit(channel.getSinkChannel().getConduit(), channel, (OpenListener)this));
/*     */       }
/* 125 */     } catch (IOException e) {
/* 126 */       IoUtils.safeClose((Closeable)channel);
/* 127 */       UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/* 128 */     } catch (Throwable t) {
/* 129 */       IoUtils.safeClose((Closeable)channel);
/* 130 */       UndertowLogger.REQUEST_IO_LOGGER.handleUnexpectedFailure(t);
/*     */     } 
/* 132 */     if (this.statisticsEnabled) {
/* 133 */       channel.getSinkChannel().setConduit((StreamSinkConduit)new BytesSentStreamSinkConduit(channel.getSinkChannel().getConduit(), this.connectorStatistics.sentAccumulator()));
/* 134 */       channel.getSourceChannel().setConduit((StreamSourceConduit)new BytesReceivedStreamSourceConduit(channel.getSourceChannel().getConduit(), this.connectorStatistics.receivedAccumulator()));
/*     */     } 
/*     */     
/* 137 */     final HttpServerConnection connection = new HttpServerConnection(channel, this.bufferPool, this.rootHandler, this.undertowOptions, this.bufferSize, this.statisticsEnabled ? this.connectorStatistics : null);
/* 138 */     HttpReadListener readListener = new HttpReadListener(connection, this.parser, this.statisticsEnabled ? this.connectorStatistics : null);
/*     */ 
/*     */     
/* 141 */     if (buffer != null) {
/* 142 */       if (buffer.getBuffer().hasRemaining()) {
/* 143 */         connection.setExtraBytes(buffer);
/*     */       } else {
/* 145 */         buffer.close();
/*     */       } 
/*     */     }
/* 148 */     if (this.connectorStatistics != null && this.statisticsEnabled) {
/* 149 */       this.connectorStatistics.incrementConnectionCount();
/*     */     }
/*     */     
/* 152 */     this.connections.add(connection);
/* 153 */     connection.addCloseListener(new ServerConnection.CloseListener()
/*     */         {
/*     */           public void closed(ServerConnection c) {
/* 156 */             HttpOpenListener.this.connections.remove(connection);
/*     */           }
/*     */         });
/* 159 */     connection.setReadListener(readListener);
/* 160 */     readListener.newRequest();
/* 161 */     channel.getSourceChannel().setReadListener(readListener);
/* 162 */     readListener.handleEvent(channel.getSourceChannel());
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHandler getRootHandler() {
/* 167 */     return this.rootHandler;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setRootHandler(HttpHandler rootHandler) {
/* 172 */     this.rootHandler = rootHandler;
/*     */   }
/*     */ 
/*     */   
/*     */   public OptionMap getUndertowOptions() {
/* 177 */     return this.undertowOptions;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setUndertowOptions(OptionMap undertowOptions) {
/* 182 */     if (undertowOptions == null) {
/* 183 */       throw UndertowMessages.MESSAGES.argumentCannotBeNull("undertowOptions");
/*     */     }
/* 185 */     this.undertowOptions = undertowOptions;
/* 186 */     this.parser = HttpRequestParser.instance(undertowOptions);
/* 187 */     this.statisticsEnabled = undertowOptions.get(UndertowOptions.ENABLE_CONNECTOR_STATISTICS, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBufferPool getBufferPool() {
/* 192 */     return this.bufferPool;
/*     */   }
/*     */ 
/*     */   
/*     */   public ConnectorStatistics getConnectorStatistics() {
/* 197 */     if (this.statisticsEnabled) {
/* 198 */       return (ConnectorStatistics)this.connectorStatistics;
/*     */     }
/* 200 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void closeConnections() {
/* 205 */     for (HttpServerConnection i : this.connections) {
/* 206 */       i.getIoThread().execute(new Runnable()
/*     */           {
/*     */             public void run() {
/* 209 */               IoUtils.safeClose((Closeable)i);
/*     */             }
/*     */           });
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\http\HttpOpenListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */