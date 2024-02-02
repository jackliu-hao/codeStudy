/*     */ package org.apache.http.impl;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.net.Socket;
/*     */ import java.nio.charset.CharsetDecoder;
/*     */ import java.nio.charset.CharsetEncoder;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.HttpEntityEnclosingRequest;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpMessage;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.HttpServerConnection;
/*     */ import org.apache.http.config.MessageConstraints;
/*     */ import org.apache.http.entity.ContentLengthStrategy;
/*     */ import org.apache.http.impl.entity.DisallowIdentityContentLengthStrategy;
/*     */ import org.apache.http.impl.io.DefaultHttpRequestParserFactory;
/*     */ import org.apache.http.impl.io.DefaultHttpResponseWriterFactory;
/*     */ import org.apache.http.io.HttpMessageParser;
/*     */ import org.apache.http.io.HttpMessageParserFactory;
/*     */ import org.apache.http.io.HttpMessageWriter;
/*     */ import org.apache.http.io.HttpMessageWriterFactory;
/*     */ import org.apache.http.util.Args;
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
/*     */ public class DefaultBHttpServerConnection
/*     */   extends BHttpConnectionBase
/*     */   implements HttpServerConnection
/*     */ {
/*     */   private final HttpMessageParser<HttpRequest> requestParser;
/*     */   private final HttpMessageWriter<HttpResponse> responseWriter;
/*     */   
/*     */   public DefaultBHttpServerConnection(int bufferSize, int fragmentSizeHint, CharsetDecoder charDecoder, CharsetEncoder charEncoder, MessageConstraints constraints, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy, HttpMessageParserFactory<HttpRequest> requestParserFactory, HttpMessageWriterFactory<HttpResponse> responseWriterFactory) {
/*  93 */     super(bufferSize, fragmentSizeHint, charDecoder, charEncoder, constraints, (incomingContentStrategy != null) ? incomingContentStrategy : (ContentLengthStrategy)DisallowIdentityContentLengthStrategy.INSTANCE, outgoingContentStrategy);
/*     */ 
/*     */     
/*  96 */     this.requestParser = ((requestParserFactory != null) ? requestParserFactory : DefaultHttpRequestParserFactory.INSTANCE).create(getSessionInputBuffer(), constraints);
/*     */     
/*  98 */     this.responseWriter = ((responseWriterFactory != null) ? responseWriterFactory : DefaultHttpResponseWriterFactory.INSTANCE).create(getSessionOutputBuffer());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultBHttpServerConnection(int bufferSize, CharsetDecoder charDecoder, CharsetEncoder charEncoder, MessageConstraints constraints) {
/* 107 */     this(bufferSize, bufferSize, charDecoder, charEncoder, constraints, (ContentLengthStrategy)null, (ContentLengthStrategy)null, (HttpMessageParserFactory<HttpRequest>)null, (HttpMessageWriterFactory<HttpResponse>)null);
/*     */   }
/*     */   
/*     */   public DefaultBHttpServerConnection(int bufferSize) {
/* 111 */     this(bufferSize, bufferSize, (CharsetDecoder)null, (CharsetEncoder)null, (MessageConstraints)null, (ContentLengthStrategy)null, (ContentLengthStrategy)null, (HttpMessageParserFactory<HttpRequest>)null, (HttpMessageWriterFactory<HttpResponse>)null);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void onRequestReceived(HttpRequest request) {}
/*     */ 
/*     */   
/*     */   protected void onResponseSubmitted(HttpResponse response) {}
/*     */ 
/*     */   
/*     */   public void bind(Socket socket) throws IOException {
/* 122 */     super.bind(socket);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpRequest receiveRequestHeader() throws HttpException, IOException {
/* 128 */     ensureOpen();
/* 129 */     HttpRequest request = (HttpRequest)this.requestParser.parse();
/* 130 */     onRequestReceived(request);
/* 131 */     incrementRequestCount();
/* 132 */     return request;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void receiveRequestEntity(HttpEntityEnclosingRequest request) throws HttpException, IOException {
/* 138 */     Args.notNull(request, "HTTP request");
/* 139 */     ensureOpen();
/* 140 */     HttpEntity entity = prepareInput((HttpMessage)request);
/* 141 */     request.setEntity(entity);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void sendResponseHeader(HttpResponse response) throws HttpException, IOException {
/* 147 */     Args.notNull(response, "HTTP response");
/* 148 */     ensureOpen();
/* 149 */     this.responseWriter.write((HttpMessage)response);
/* 150 */     onResponseSubmitted(response);
/* 151 */     if (response.getStatusLine().getStatusCode() >= 200) {
/* 152 */       incrementResponseCount();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void sendResponseEntity(HttpResponse response) throws HttpException, IOException {
/* 159 */     Args.notNull(response, "HTTP response");
/* 160 */     ensureOpen();
/* 161 */     HttpEntity entity = response.getEntity();
/* 162 */     if (entity == null) {
/*     */       return;
/*     */     }
/* 165 */     OutputStream outStream = prepareOutput((HttpMessage)response);
/* 166 */     entity.writeTo(outStream);
/* 167 */     outStream.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/* 172 */     ensureOpen();
/* 173 */     doFlush();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\DefaultBHttpServerConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */