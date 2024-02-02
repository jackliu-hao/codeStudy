/*     */ package io.undertow.server.protocol.ajp;
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
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.OpenListener;
/*     */ import io.undertow.server.ServerConnection;
/*     */ import io.undertow.server.XnioByteBufferPool;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.Collections;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.Options;
/*     */ import org.xnio.Pool;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.channels.StreamSourceChannel;
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
/*     */ public class AjpOpenListener
/*     */   implements OpenListener
/*     */ {
/*  58 */   private static final String DEFAULT_AJP_ALLOWED_REQUEST_ATTRIBUTES_PATTERN = SecurityActions.getSystemProperty("io.undertow.ajp.allowedRequestAttributesPattern");
/*     */   
/*  60 */   private final Set<AjpServerConnection> connections = Collections.newSetFromMap(new ConcurrentHashMap<>());
/*     */   
/*     */   private final ByteBufferPool bufferPool;
/*     */   
/*     */   private final int bufferSize;
/*     */   
/*     */   private volatile String scheme;
/*     */   
/*     */   private volatile HttpHandler rootHandler;
/*     */   
/*     */   private volatile OptionMap undertowOptions;
/*     */   
/*     */   private volatile AjpRequestParser parser;
/*     */   private volatile boolean statisticsEnabled;
/*     */   private final ConnectorStatisticsImpl connectorStatistics;
/*     */   
/*  76 */   private final ServerConnection.CloseListener closeListener = new ServerConnection.CloseListener()
/*     */     {
/*     */       public void closed(ServerConnection connection) {
/*  79 */         AjpOpenListener.this.connectorStatistics.decrementConnectionCount();
/*     */       }
/*     */     };
/*     */   
/*     */   public AjpOpenListener(Pool<ByteBuffer> pool) {
/*  84 */     this(pool, OptionMap.EMPTY);
/*     */   }
/*     */   
/*     */   public AjpOpenListener(Pool<ByteBuffer> pool, OptionMap undertowOptions) {
/*  88 */     this((ByteBufferPool)new XnioByteBufferPool(pool), undertowOptions);
/*     */   }
/*     */   
/*     */   public AjpOpenListener(ByteBufferPool pool) {
/*  92 */     this(pool, OptionMap.EMPTY);
/*     */   }
/*     */   
/*     */   public AjpOpenListener(ByteBufferPool pool, OptionMap undertowOptions) {
/*  96 */     this.undertowOptions = undertowOptions;
/*  97 */     this.bufferPool = pool;
/*  98 */     PooledByteBuffer buf = pool.allocate();
/*  99 */     this.bufferSize = buf.getBuffer().remaining();
/* 100 */     buf.close();
/* 101 */     this.parser = new AjpRequestParser((String)undertowOptions.get(UndertowOptions.URL_CHARSET, StandardCharsets.UTF_8.name()), undertowOptions.get(UndertowOptions.DECODE_URL, true), undertowOptions.get(UndertowOptions.MAX_PARAMETERS, 1000), undertowOptions.get(UndertowOptions.MAX_HEADERS, 200), undertowOptions.get(UndertowOptions.ALLOW_ENCODED_SLASH, false), undertowOptions.get(UndertowOptions.ALLOW_UNESCAPED_CHARACTERS_IN_URL, false), (String)undertowOptions.get(UndertowOptions.AJP_ALLOWED_REQUEST_ATTRIBUTES_PATTERN, DEFAULT_AJP_ALLOWED_REQUEST_ATTRIBUTES_PATTERN));
/* 102 */     this.connectorStatistics = new ConnectorStatisticsImpl();
/* 103 */     this.statisticsEnabled = undertowOptions.get(UndertowOptions.ENABLE_CONNECTOR_STATISTICS, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleEvent(StreamConnection channel) {
/* 108 */     if (UndertowLogger.REQUEST_LOGGER.isTraceEnabled()) {
/* 109 */       UndertowLogger.REQUEST_LOGGER.tracef("Opened connection with %s", channel.getPeerAddress());
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 114 */       Integer readTimeout = (Integer)channel.getOption(Options.READ_TIMEOUT);
/* 115 */       Integer idle = (Integer)this.undertowOptions.get(UndertowOptions.IDLE_TIMEOUT);
/* 116 */       if (idle != null) {
/* 117 */         IdleTimeoutConduit conduit = new IdleTimeoutConduit(channel);
/* 118 */         channel.getSourceChannel().setConduit((StreamSourceConduit)conduit);
/* 119 */         channel.getSinkChannel().setConduit((StreamSinkConduit)conduit);
/*     */       } 
/* 121 */       if (readTimeout != null && readTimeout.intValue() > 0) {
/* 122 */         channel.getSourceChannel().setConduit((StreamSourceConduit)new ReadTimeoutStreamSourceConduit(channel.getSourceChannel().getConduit(), channel, this));
/*     */       }
/* 124 */       Integer writeTimeout = (Integer)channel.getOption(Options.WRITE_TIMEOUT);
/* 125 */       if (writeTimeout != null && writeTimeout.intValue() > 0) {
/* 126 */         channel.getSinkChannel().setConduit((StreamSinkConduit)new WriteTimeoutStreamSinkConduit(channel.getSinkChannel().getConduit(), channel, this));
/*     */       }
/* 128 */     } catch (IOException e) {
/* 129 */       IoUtils.safeClose((Closeable)channel);
/* 130 */       UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/*     */     } 
/* 132 */     if (this.statisticsEnabled) {
/* 133 */       channel.getSinkChannel().setConduit((StreamSinkConduit)new BytesSentStreamSinkConduit(channel.getSinkChannel().getConduit(), this.connectorStatistics.sentAccumulator()));
/* 134 */       channel.getSourceChannel().setConduit((StreamSourceConduit)new BytesReceivedStreamSourceConduit(channel.getSourceChannel().getConduit(), this.connectorStatistics.receivedAccumulator()));
/* 135 */       this.connectorStatistics.incrementConnectionCount();
/*     */     } 
/*     */     
/* 138 */     final AjpServerConnection connection = new AjpServerConnection(channel, this.bufferPool, this.rootHandler, this.undertowOptions, this.bufferSize);
/* 139 */     AjpReadListener readListener = new AjpReadListener(connection, this.scheme, this.parser, this.statisticsEnabled ? this.connectorStatistics : null);
/* 140 */     if (this.statisticsEnabled) {
/* 141 */       connection.addCloseListener(this.closeListener);
/*     */     }
/* 143 */     connection.setAjpReadListener(readListener);
/*     */     
/* 145 */     this.connections.add(connection);
/* 146 */     connection.addCloseListener(new ServerConnection.CloseListener()
/*     */         {
/*     */           public void closed(ServerConnection c) {
/* 149 */             AjpOpenListener.this.connections.remove(connection);
/*     */           }
/*     */         });
/*     */ 
/*     */     
/* 154 */     readListener.startRequest();
/* 155 */     channel.getSourceChannel().setReadListener(readListener);
/* 156 */     readListener.handleEvent((StreamSourceChannel)channel.getSourceChannel());
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHandler getRootHandler() {
/* 161 */     return this.rootHandler;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setRootHandler(HttpHandler rootHandler) {
/* 166 */     this.rootHandler = rootHandler;
/*     */   }
/*     */ 
/*     */   
/*     */   public OptionMap getUndertowOptions() {
/* 171 */     return this.undertowOptions;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setUndertowOptions(OptionMap undertowOptions) {
/* 176 */     if (undertowOptions == null) {
/* 177 */       throw UndertowMessages.MESSAGES.argumentCannotBeNull("undertowOptions");
/*     */     }
/* 179 */     this.undertowOptions = undertowOptions;
/* 180 */     this.statisticsEnabled = undertowOptions.get(UndertowOptions.ENABLE_CONNECTOR_STATISTICS, false);
/* 181 */     this.parser = new AjpRequestParser((String)undertowOptions.get(UndertowOptions.URL_CHARSET, StandardCharsets.UTF_8.name()), undertowOptions.get(UndertowOptions.DECODE_URL, true), undertowOptions.get(UndertowOptions.MAX_PARAMETERS, 1000), undertowOptions.get(UndertowOptions.MAX_HEADERS, 200), undertowOptions.get(UndertowOptions.ALLOW_ENCODED_SLASH, false), undertowOptions.get(UndertowOptions.ALLOW_UNESCAPED_CHARACTERS_IN_URL, false));
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBufferPool getBufferPool() {
/* 186 */     return this.bufferPool;
/*     */   }
/*     */ 
/*     */   
/*     */   public ConnectorStatistics getConnectorStatistics() {
/* 191 */     if (this.statisticsEnabled) {
/* 192 */       return (ConnectorStatistics)this.connectorStatistics;
/*     */     }
/* 194 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void closeConnections() {
/* 199 */     for (AjpServerConnection i : this.connections) {
/* 200 */       IoUtils.safeClose((Closeable)i);
/*     */     }
/*     */   }
/*     */   
/*     */   public String getScheme() {
/* 205 */     return this.scheme;
/*     */   }
/*     */   
/*     */   public void setScheme(String scheme) {
/* 209 */     this.scheme = scheme;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\ajp\AjpOpenListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */