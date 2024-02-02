/*     */ package org.apache.http.impl;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.Socket;
/*     */ import org.apache.http.HttpConnection;
/*     */ import org.apache.http.HttpConnectionFactory;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.config.ConnectionConfig;
/*     */ import org.apache.http.entity.ContentLengthStrategy;
/*     */ import org.apache.http.io.HttpMessageParserFactory;
/*     */ import org.apache.http.io.HttpMessageWriterFactory;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
/*     */ public class DefaultBHttpServerConnectionFactory
/*     */   implements HttpConnectionFactory<DefaultBHttpServerConnection>
/*     */ {
/*  52 */   public static final DefaultBHttpServerConnectionFactory INSTANCE = new DefaultBHttpServerConnectionFactory();
/*     */ 
/*     */   
/*     */   private final ConnectionConfig cconfig;
/*     */   
/*     */   private final ContentLengthStrategy incomingContentStrategy;
/*     */   
/*     */   private final ContentLengthStrategy outgoingContentStrategy;
/*     */   
/*     */   private final HttpMessageParserFactory<HttpRequest> requestParserFactory;
/*     */   
/*     */   private final HttpMessageWriterFactory<HttpResponse> responseWriterFactory;
/*     */ 
/*     */   
/*     */   public DefaultBHttpServerConnectionFactory(ConnectionConfig cconfig, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy, HttpMessageParserFactory<HttpRequest> requestParserFactory, HttpMessageWriterFactory<HttpResponse> responseWriterFactory) {
/*  67 */     this.cconfig = (cconfig != null) ? cconfig : ConnectionConfig.DEFAULT;
/*  68 */     this.incomingContentStrategy = incomingContentStrategy;
/*  69 */     this.outgoingContentStrategy = outgoingContentStrategy;
/*  70 */     this.requestParserFactory = requestParserFactory;
/*  71 */     this.responseWriterFactory = responseWriterFactory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultBHttpServerConnectionFactory(ConnectionConfig cconfig, HttpMessageParserFactory<HttpRequest> requestParserFactory, HttpMessageWriterFactory<HttpResponse> responseWriterFactory) {
/*  78 */     this(cconfig, null, null, requestParserFactory, responseWriterFactory);
/*     */   }
/*     */   
/*     */   public DefaultBHttpServerConnectionFactory(ConnectionConfig cconfig) {
/*  82 */     this(cconfig, null, null, null, null);
/*     */   }
/*     */   
/*     */   public DefaultBHttpServerConnectionFactory() {
/*  86 */     this(null, null, null, null, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public DefaultBHttpServerConnection createConnection(Socket socket) throws IOException {
/*  91 */     DefaultBHttpServerConnection conn = new DefaultBHttpServerConnection(this.cconfig.getBufferSize(), this.cconfig.getFragmentSizeHint(), ConnSupport.createDecoder(this.cconfig), ConnSupport.createEncoder(this.cconfig), this.cconfig.getMessageConstraints(), this.incomingContentStrategy, this.outgoingContentStrategy, this.requestParserFactory, this.responseWriterFactory);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 101 */     conn.bind(socket);
/* 102 */     return conn;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\DefaultBHttpServerConnectionFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */