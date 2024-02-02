/*     */ package org.apache.http.impl.conn;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.Socket;
/*     */ import java.nio.charset.CharsetDecoder;
/*     */ import java.nio.charset.CharsetEncoder;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.config.MessageConstraints;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class LoggingManagedHttpClientConnection
/*     */   extends DefaultManagedHttpClientConnection
/*     */ {
/*     */   private final Log log;
/*     */   private final Log headerLog;
/*     */   private final Wire wire;
/*     */   
/*     */   public LoggingManagedHttpClientConnection(String id, Log log, Log headerLog, Log wireLog, int bufferSize, int fragmentSizeHint, CharsetDecoder charDecoder, CharsetEncoder charEncoder, MessageConstraints constraints, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy, HttpMessageWriterFactory<HttpRequest> requestWriterFactory, HttpMessageParserFactory<HttpResponse> responseParserFactory) {
/*  66 */     super(id, bufferSize, fragmentSizeHint, charDecoder, charEncoder, constraints, incomingContentStrategy, outgoingContentStrategy, requestWriterFactory, responseParserFactory);
/*     */ 
/*     */     
/*  69 */     this.log = log;
/*  70 */     this.headerLog = headerLog;
/*  71 */     this.wire = new Wire(wireLog, id);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*  77 */     if (isOpen()) {
/*  78 */       if (this.log.isDebugEnabled()) {
/*  79 */         this.log.debug(getId() + ": Close connection");
/*     */       }
/*  81 */       super.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSocketTimeout(int timeout) {
/*  87 */     if (this.log.isDebugEnabled()) {
/*  88 */       this.log.debug(getId() + ": set socket timeout to " + timeout);
/*     */     }
/*  90 */     super.setSocketTimeout(timeout);
/*     */   }
/*     */ 
/*     */   
/*     */   public void shutdown() throws IOException {
/*  95 */     if (this.log.isDebugEnabled()) {
/*  96 */       this.log.debug(getId() + ": Shutdown connection");
/*     */     }
/*  98 */     super.shutdown();
/*     */   }
/*     */ 
/*     */   
/*     */   protected InputStream getSocketInputStream(Socket socket) throws IOException {
/* 103 */     InputStream in = super.getSocketInputStream(socket);
/* 104 */     if (this.wire.enabled()) {
/* 105 */       in = new LoggingInputStream(in, this.wire);
/*     */     }
/* 107 */     return in;
/*     */   }
/*     */ 
/*     */   
/*     */   protected OutputStream getSocketOutputStream(Socket socket) throws IOException {
/* 112 */     OutputStream out = super.getSocketOutputStream(socket);
/* 113 */     if (this.wire.enabled()) {
/* 114 */       out = new LoggingOutputStream(out, this.wire);
/*     */     }
/* 116 */     return out;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void onResponseReceived(HttpResponse response) {
/* 121 */     if (response != null && this.headerLog.isDebugEnabled()) {
/* 122 */       this.headerLog.debug(getId() + " << " + response.getStatusLine().toString());
/* 123 */       Header[] headers = response.getAllHeaders();
/* 124 */       for (Header header : headers) {
/* 125 */         this.headerLog.debug(getId() + " << " + header.toString());
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void onRequestSubmitted(HttpRequest request) {
/* 132 */     if (request != null && this.headerLog.isDebugEnabled()) {
/* 133 */       this.headerLog.debug(getId() + " >> " + request.getRequestLine().toString());
/* 134 */       Header[] headers = request.getAllHeaders();
/* 135 */       for (Header header : headers)
/* 136 */         this.headerLog.debug(getId() + " >> " + header.toString()); 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\LoggingManagedHttpClientConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */