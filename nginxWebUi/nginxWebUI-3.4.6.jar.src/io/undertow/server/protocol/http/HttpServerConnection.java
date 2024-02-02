/*     */ package io.undertow.server.protocol.http;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.conduits.ReadDataStreamSourceConduit;
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.server.AbstractServerConnection;
/*     */ import io.undertow.server.ConduitWrapper;
/*     */ import io.undertow.server.ConnectionSSLSessionInfo;
/*     */ import io.undertow.server.ConnectorStatisticsImpl;
/*     */ import io.undertow.server.Connectors;
/*     */ import io.undertow.server.ExchangeCompletionListener;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.HttpUpgradeListener;
/*     */ import io.undertow.server.SSLSessionInfo;
/*     */ import io.undertow.server.ServerConnection;
/*     */ import io.undertow.util.ConduitFactory;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.HttpString;
/*     */ import io.undertow.util.ImmediatePooledByteBuffer;
/*     */ import io.undertow.util.Methods;
/*     */ import java.io.Closeable;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Collection;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.channels.SslChannel;
/*     */ import org.xnio.conduits.Conduit;
/*     */ import org.xnio.conduits.ConduitStreamSinkChannel;
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
/*     */ 
/*     */ public final class HttpServerConnection
/*     */   extends AbstractServerConnection
/*     */ {
/*     */   private SSLSessionInfo sslSessionInfo;
/*     */   private HttpReadListener readListener;
/*     */   private PipeliningBufferingStreamSinkConduit pipelineBuffer;
/*     */   private HttpResponseConduit responseConduit;
/*     */   private ServerFixedLengthStreamSinkConduit fixedLengthStreamSinkConduit;
/*     */   private ReadDataStreamSourceConduit readDataStreamSourceConduit;
/*     */   private HttpUpgradeListener upgradeListener;
/*     */   private boolean connectHandled;
/*     */   
/*     */   public HttpServerConnection(StreamConnection channel, ByteBufferPool bufferPool, HttpHandler rootHandler, OptionMap undertowOptions, int bufferSize, final ConnectorStatisticsImpl connectorStatistics) {
/*  72 */     super(channel, bufferPool, rootHandler, undertowOptions, bufferSize);
/*  73 */     if (channel instanceof SslChannel) {
/*  74 */       this.sslSessionInfo = (SSLSessionInfo)new ConnectionSSLSessionInfo((SslChannel)channel, this);
/*     */     }
/*  76 */     this.responseConduit = new HttpResponseConduit(channel.getSinkChannel().getConduit(), bufferPool, this);
/*     */     
/*  78 */     this.fixedLengthStreamSinkConduit = new ServerFixedLengthStreamSinkConduit((StreamSinkConduit)this.responseConduit, false, false);
/*  79 */     this.readDataStreamSourceConduit = new ReadDataStreamSourceConduit(channel.getSourceChannel().getConduit(), this);
/*     */     
/*  81 */     addCloseListener(new ServerConnection.CloseListener()
/*     */         {
/*     */           public void closed(ServerConnection connection) {
/*  84 */             if (connectorStatistics != null) {
/*  85 */               connectorStatistics.decrementConnectionCount();
/*     */             }
/*  87 */             HttpServerConnection.this.responseConduit.freeBuffers();
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpServerExchange sendOutOfBandResponse(HttpServerExchange exchange) {
/*  94 */     if (exchange == null || !HttpContinue.requiresContinueResponse(exchange)) {
/*  95 */       throw UndertowMessages.MESSAGES.outOfBandResponseOnlyAllowedFor100Continue();
/*     */     }
/*  97 */     final AbstractServerConnection.ConduitState state = resetChannel();
/*  98 */     HttpServerExchange newExchange = new HttpServerExchange((ServerConnection)this);
/*  99 */     for (HttpString header : exchange.getRequestHeaders().getHeaderNames()) {
/* 100 */       newExchange.getRequestHeaders().putAll(header, (Collection)exchange.getRequestHeaders().get(header));
/*     */     }
/* 102 */     newExchange.setProtocol(exchange.getProtocol());
/* 103 */     newExchange.setRequestMethod(exchange.getRequestMethod());
/* 104 */     exchange.setRequestURI(exchange.getRequestURI(), exchange.isHostIncludedInRequestURI());
/* 105 */     exchange.setRequestPath(exchange.getRequestPath());
/* 106 */     exchange.setRelativePath(exchange.getRelativePath());
/* 107 */     newExchange.getRequestHeaders().put(Headers.CONNECTION, Headers.KEEP_ALIVE.toString());
/* 108 */     newExchange.getRequestHeaders().put(Headers.CONTENT_LENGTH, 0L);
/* 109 */     newExchange.setPersistent(true);
/*     */     
/* 111 */     Connectors.terminateRequest(newExchange);
/* 112 */     newExchange.addResponseWrapper(new ConduitWrapper<StreamSinkConduit>()
/*     */         {
/*     */           public StreamSinkConduit wrap(ConduitFactory<StreamSinkConduit> factory, HttpServerExchange exchange)
/*     */           {
/* 116 */             final HttpResponseConduit httpResponseConduit = new HttpResponseConduit(HttpServerConnection.this.getSinkChannel().getConduit(), HttpServerConnection.this.getByteBufferPool(), HttpServerConnection.this, exchange);
/* 117 */             exchange.addExchangeCompleteListener(new ExchangeCompletionListener()
/*     */                 {
/*     */                   public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener) {
/* 120 */                     httpResponseConduit.freeContinueResponse();
/* 121 */                     nextListener.proceed();
/*     */                   }
/*     */                 });
/* 124 */             ServerFixedLengthStreamSinkConduit fixed = new ServerFixedLengthStreamSinkConduit((StreamSinkConduit)httpResponseConduit, false, false);
/* 125 */             fixed.reset(0L, exchange);
/* 126 */             return (StreamSinkConduit)fixed;
/*     */           }
/*     */         });
/*     */ 
/*     */     
/* 131 */     this.channel.getSourceChannel().setConduit(source(state));
/* 132 */     newExchange.addExchangeCompleteListener(new ExchangeCompletionListener()
/*     */         {
/*     */           public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener) {
/* 135 */             HttpServerConnection.this.restoreChannel(state);
/*     */           }
/*     */         });
/* 138 */     return newExchange;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isContinueResponseSupported() {
/* 143 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void terminateRequestChannel(HttpServerExchange exchange) {
/* 148 */     if (!exchange.isPersistent()) {
/* 149 */       IoUtils.safeClose((Closeable)getChannel().getSourceChannel());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void ungetRequestBytes(PooledByteBuffer unget) {
/* 160 */     if (getExtraBytes() == null) {
/* 161 */       setExtraBytes(unget);
/*     */     } else {
/* 163 */       PooledByteBuffer eb = getExtraBytes();
/* 164 */       ByteBuffer buf = eb.getBuffer();
/* 165 */       ByteBuffer ugBuffer = unget.getBuffer();
/*     */       
/* 167 */       if (ugBuffer.limit() - ugBuffer.remaining() > buf.remaining()) {
/*     */         
/* 169 */         ugBuffer.compact();
/* 170 */         ugBuffer.put(buf);
/* 171 */         ugBuffer.flip();
/* 172 */         eb.close();
/* 173 */         setExtraBytes(unget);
/*     */       } else {
/*     */         
/* 176 */         byte[] data = new byte[ugBuffer.remaining() + buf.remaining()];
/* 177 */         int first = ugBuffer.remaining();
/* 178 */         ugBuffer.get(data, 0, ugBuffer.remaining());
/* 179 */         buf.get(data, first, buf.remaining());
/* 180 */         eb.close();
/* 181 */         unget.close();
/* 182 */         ByteBuffer newBuffer = ByteBuffer.wrap(data);
/* 183 */         setExtraBytes((PooledByteBuffer)new ImmediatePooledByteBuffer(newBuffer));
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLSessionInfo getSslSessionInfo() {
/* 190 */     return this.sslSessionInfo;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSslSessionInfo(SSLSessionInfo sessionInfo) {
/* 195 */     this.sslSessionInfo = sessionInfo;
/*     */   }
/*     */   
/*     */   public SSLSession getSslSession() {
/* 199 */     if (this.channel instanceof SslChannel) {
/* 200 */       return ((SslChannel)this.channel).getSslSession();
/*     */     }
/* 202 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected StreamConnection upgradeChannel() {
/* 207 */     clearChannel();
/* 208 */     if (this.extraBytes != null) {
/* 209 */       this.channel.getSourceChannel().setConduit((StreamSourceConduit)new ReadDataStreamSourceConduit(this.channel.getSourceChannel().getConduit(), this));
/*     */     }
/* 211 */     return this.channel;
/*     */   }
/*     */ 
/*     */   
/*     */   protected StreamSinkConduit getSinkConduit(HttpServerExchange exchange, StreamSinkConduit conduit) {
/* 216 */     if (exchange.getRequestMethod().equals(Methods.CONNECT) && !this.connectHandled) {
/*     */       
/* 218 */       exchange.setPersistent(false);
/* 219 */       exchange.getResponseHeaders().put(Headers.CONNECTION, "close");
/*     */     } 
/* 221 */     return HttpTransferEncoding.createSinkConduit(exchange);
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean isUpgradeSupported() {
/* 226 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean isConnectSupported() {
/* 231 */     return true;
/*     */   }
/*     */   
/*     */   void setReadListener(HttpReadListener readListener) {
/* 235 */     this.readListener = readListener;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void exchangeComplete(HttpServerExchange exchange) {
/* 240 */     if (this.fixedLengthStreamSinkConduit != null) {
/* 241 */       this.fixedLengthStreamSinkConduit.clearExchange();
/*     */     }
/* 243 */     if (this.pipelineBuffer == null) {
/* 244 */       this.readListener.exchangeComplete(exchange);
/*     */     } else {
/* 246 */       this.pipelineBuffer.exchangeComplete(exchange);
/*     */     } 
/*     */   }
/*     */   
/*     */   HttpReadListener getReadListener() {
/* 251 */     return this.readListener;
/*     */   }
/*     */   
/*     */   ReadDataStreamSourceConduit getReadDataStreamSourceConduit() {
/* 255 */     return this.readDataStreamSourceConduit;
/*     */   }
/*     */   
/*     */   public PipeliningBufferingStreamSinkConduit getPipelineBuffer() {
/* 259 */     return this.pipelineBuffer;
/*     */   }
/*     */   
/*     */   public HttpResponseConduit getResponseConduit() {
/* 263 */     return this.responseConduit;
/*     */   }
/*     */   
/*     */   ServerFixedLengthStreamSinkConduit getFixedLengthStreamSinkConduit() {
/* 267 */     return this.fixedLengthStreamSinkConduit;
/*     */   }
/*     */   
/*     */   protected HttpUpgradeListener getUpgradeListener() {
/* 271 */     return this.upgradeListener;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void setUpgradeListener(HttpUpgradeListener upgradeListener) {
/* 276 */     this.upgradeListener = upgradeListener;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void setConnectListener(HttpUpgradeListener connectListener) {
/* 281 */     this.upgradeListener = connectListener;
/* 282 */     this.connectHandled = true;
/*     */   }
/*     */   
/*     */   void setCurrentExchange(HttpServerExchange exchange) {
/* 286 */     this.current = exchange;
/*     */   }
/*     */   
/*     */   public void setPipelineBuffer(PipeliningBufferingStreamSinkConduit pipelineBuffer) {
/* 290 */     this.pipelineBuffer = pipelineBuffer;
/* 291 */     this.responseConduit = new HttpResponseConduit((StreamSinkConduit)pipelineBuffer, this.bufferPool, this);
/* 292 */     this.fixedLengthStreamSinkConduit = new ServerFixedLengthStreamSinkConduit((StreamSinkConduit)this.responseConduit, false, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getTransportProtocol() {
/* 297 */     return "http/1.1";
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isRequestTrailerFieldsSupported() {
/* 302 */     if (this.current == null) {
/* 303 */       return false;
/*     */     }
/*     */     
/* 306 */     String te = this.current.getRequestHeaders().getFirst(Headers.TRANSFER_ENCODING);
/* 307 */     if (te == null) {
/* 308 */       return false;
/*     */     }
/* 310 */     return te.equalsIgnoreCase(Headers.CHUNKED.toString());
/*     */   }
/*     */   
/*     */   boolean isConnectHandled() {
/* 314 */     return this.connectHandled;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\http\HttpServerConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */