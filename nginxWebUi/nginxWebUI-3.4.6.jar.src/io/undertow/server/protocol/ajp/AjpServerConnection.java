/*     */ package io.undertow.server.protocol.ajp;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.server.AbstractServerConnection;
/*     */ import io.undertow.server.BasicSSLSessionInfo;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.HttpUpgradeListener;
/*     */ import io.undertow.server.SSLSessionInfo;
/*     */ import io.undertow.util.DateUtils;
/*     */ import java.io.Closeable;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.channels.SuspendableWriteChannel;
/*     */ import org.xnio.conduits.ConduitStreamSinkChannel;
/*     */ import org.xnio.conduits.StreamSinkConduit;
/*     */ import org.xnio.conduits.WriteReadyHandler;
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
/*     */ public final class AjpServerConnection
/*     */   extends AbstractServerConnection
/*     */ {
/*     */   private SSLSessionInfo sslSessionInfo;
/*     */   private WriteReadyHandler.ChannelListenerHandler<ConduitStreamSinkChannel> writeReadyHandler;
/*     */   private AjpReadListener ajpReadListener;
/*     */   
/*     */   public AjpServerConnection(StreamConnection channel, ByteBufferPool bufferPool, HttpHandler rootHandler, OptionMap undertowOptions, int bufferSize) {
/*  50 */     super(channel, bufferPool, rootHandler, undertowOptions, bufferSize);
/*  51 */     this.writeReadyHandler = new WriteReadyHandler.ChannelListenerHandler((SuspendableWriteChannel)channel.getSinkChannel());
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpServerExchange sendOutOfBandResponse(HttpServerExchange exchange) {
/*  56 */     throw UndertowMessages.MESSAGES.outOfBandResponseNotSupported();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isContinueResponseSupported() {
/*  61 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void terminateRequestChannel(HttpServerExchange exchange) {
/*  66 */     if (!exchange.isPersistent()) {
/*  67 */       IoUtils.safeClose((Closeable)getChannel().getSourceChannel());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void restoreChannel(AbstractServerConnection.ConduitState state) {
/*  73 */     super.restoreChannel(state);
/*  74 */     this.channel.getSinkChannel().getConduit().setWriteReadyHandler((WriteReadyHandler)this.writeReadyHandler);
/*     */   }
/*     */ 
/*     */   
/*     */   public AbstractServerConnection.ConduitState resetChannel() {
/*  79 */     AbstractServerConnection.ConduitState state = super.resetChannel();
/*  80 */     this.channel.getSinkChannel().getConduit().setWriteReadyHandler((WriteReadyHandler)this.writeReadyHandler);
/*  81 */     return state;
/*     */   }
/*     */ 
/*     */   
/*     */   public void clearChannel() {
/*  86 */     super.clearChannel();
/*  87 */     this.channel.getSinkChannel().getConduit().setWriteReadyHandler((WriteReadyHandler)this.writeReadyHandler);
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLSessionInfo getSslSessionInfo() {
/*  92 */     return this.sslSessionInfo;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSslSessionInfo(SSLSessionInfo sessionInfo) {
/*  97 */     this.sslSessionInfo = sessionInfo;
/*     */   }
/*     */   
/*     */   void setSSLSessionInfo(BasicSSLSessionInfo sslSessionInfo) {
/* 101 */     this.sslSessionInfo = (SSLSessionInfo)sslSessionInfo;
/*     */   }
/*     */ 
/*     */   
/*     */   protected StreamConnection upgradeChannel() {
/* 106 */     throw UndertowMessages.MESSAGES.upgradeNotSupported();
/*     */   }
/*     */ 
/*     */   
/*     */   protected StreamSinkConduit getSinkConduit(HttpServerExchange exchange, StreamSinkConduit conduit) {
/* 111 */     DateUtils.addDateHeaderIfRequired(exchange);
/* 112 */     return conduit;
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean isUpgradeSupported() {
/* 117 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean isConnectSupported() {
/* 122 */     return false;
/*     */   }
/*     */   
/*     */   void setAjpReadListener(AjpReadListener ajpReadListener) {
/* 126 */     this.ajpReadListener = ajpReadListener;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void exchangeComplete(HttpServerExchange exchange) {
/* 131 */     this.ajpReadListener.exchangeComplete(exchange);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void setConnectListener(HttpUpgradeListener connectListener) {
/* 136 */     throw UndertowMessages.MESSAGES.connectNotSupported();
/*     */   }
/*     */   
/*     */   void setCurrentExchange(HttpServerExchange exchange) {
/* 140 */     this.current = exchange;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getTransportProtocol() {
/* 145 */     return "ajp";
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isRequestTrailerFieldsSupported() {
/* 150 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\ajp\AjpServerConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */