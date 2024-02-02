/*     */ package org.apache.http.impl.conn;
/*     */ 
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetDecoder;
/*     */ import java.nio.charset.CharsetEncoder;
/*     */ import java.nio.charset.CodingErrorAction;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.HttpConnection;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.config.ConnectionConfig;
/*     */ import org.apache.http.conn.HttpConnectionFactory;
/*     */ import org.apache.http.conn.ManagedHttpClientConnection;
/*     */ import org.apache.http.conn.routing.HttpRoute;
/*     */ import org.apache.http.entity.ContentLengthStrategy;
/*     */ import org.apache.http.impl.entity.LaxContentLengthStrategy;
/*     */ import org.apache.http.impl.entity.StrictContentLengthStrategy;
/*     */ import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
/*     */ public class ManagedHttpClientConnectionFactory
/*     */   implements HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection>
/*     */ {
/*  61 */   private static final AtomicLong COUNTER = new AtomicLong();
/*     */   
/*  63 */   public static final ManagedHttpClientConnectionFactory INSTANCE = new ManagedHttpClientConnectionFactory();
/*     */   
/*  65 */   private final Log log = LogFactory.getLog(DefaultManagedHttpClientConnection.class);
/*  66 */   private final Log headerLog = LogFactory.getLog("org.apache.http.headers");
/*  67 */   private final Log wireLog = LogFactory.getLog("org.apache.http.wire");
/*     */ 
/*     */   
/*     */   private final HttpMessageWriterFactory<HttpRequest> requestWriterFactory;
/*     */ 
/*     */   
/*     */   private final HttpMessageParserFactory<HttpResponse> responseParserFactory;
/*     */ 
/*     */   
/*     */   private final ContentLengthStrategy incomingContentStrategy;
/*     */ 
/*     */   
/*     */   private final ContentLengthStrategy outgoingContentStrategy;
/*     */ 
/*     */   
/*     */   public ManagedHttpClientConnectionFactory(HttpMessageWriterFactory<HttpRequest> requestWriterFactory, HttpMessageParserFactory<HttpResponse> responseParserFactory, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy) {
/*  83 */     this.requestWriterFactory = (requestWriterFactory != null) ? requestWriterFactory : (HttpMessageWriterFactory<HttpRequest>)DefaultHttpRequestWriterFactory.INSTANCE;
/*     */     
/*  85 */     this.responseParserFactory = (responseParserFactory != null) ? responseParserFactory : DefaultHttpResponseParserFactory.INSTANCE;
/*     */     
/*  87 */     this.incomingContentStrategy = (incomingContentStrategy != null) ? incomingContentStrategy : (ContentLengthStrategy)LaxContentLengthStrategy.INSTANCE;
/*     */     
/*  89 */     this.outgoingContentStrategy = (outgoingContentStrategy != null) ? outgoingContentStrategy : (ContentLengthStrategy)StrictContentLengthStrategy.INSTANCE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ManagedHttpClientConnectionFactory(HttpMessageWriterFactory<HttpRequest> requestWriterFactory, HttpMessageParserFactory<HttpResponse> responseParserFactory) {
/*  96 */     this(requestWriterFactory, responseParserFactory, null, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public ManagedHttpClientConnectionFactory(HttpMessageParserFactory<HttpResponse> responseParserFactory) {
/* 101 */     this(null, responseParserFactory);
/*     */   }
/*     */   
/*     */   public ManagedHttpClientConnectionFactory() {
/* 105 */     this(null, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public ManagedHttpClientConnection create(HttpRoute route, ConnectionConfig config) {
/* 110 */     ConnectionConfig cconfig = (config != null) ? config : ConnectionConfig.DEFAULT;
/* 111 */     CharsetDecoder charDecoder = null;
/* 112 */     CharsetEncoder charEncoder = null;
/* 113 */     Charset charset = cconfig.getCharset();
/* 114 */     CodingErrorAction malformedInputAction = (cconfig.getMalformedInputAction() != null) ? cconfig.getMalformedInputAction() : CodingErrorAction.REPORT;
/*     */     
/* 116 */     CodingErrorAction unmappableInputAction = (cconfig.getUnmappableInputAction() != null) ? cconfig.getUnmappableInputAction() : CodingErrorAction.REPORT;
/*     */     
/* 118 */     if (charset != null) {
/* 119 */       charDecoder = charset.newDecoder();
/* 120 */       charDecoder.onMalformedInput(malformedInputAction);
/* 121 */       charDecoder.onUnmappableCharacter(unmappableInputAction);
/* 122 */       charEncoder = charset.newEncoder();
/* 123 */       charEncoder.onMalformedInput(malformedInputAction);
/* 124 */       charEncoder.onUnmappableCharacter(unmappableInputAction);
/*     */     } 
/* 126 */     String id = "http-outgoing-" + Long.toString(COUNTER.getAndIncrement());
/* 127 */     return new LoggingManagedHttpClientConnection(id, this.log, this.headerLog, this.wireLog, cconfig.getBufferSize(), cconfig.getFragmentSizeHint(), charDecoder, charEncoder, cconfig.getMessageConstraints(), this.incomingContentStrategy, this.outgoingContentStrategy, this.requestWriterFactory, this.responseParserFactory);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\ManagedHttpClientConnectionFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */