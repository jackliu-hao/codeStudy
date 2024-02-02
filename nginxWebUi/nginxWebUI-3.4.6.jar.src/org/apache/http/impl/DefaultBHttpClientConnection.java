/*     */ package org.apache.http.impl;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketTimeoutException;
/*     */ import java.nio.charset.CharsetDecoder;
/*     */ import java.nio.charset.CharsetEncoder;
/*     */ import org.apache.http.HttpClientConnection;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.HttpEntityEnclosingRequest;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpMessage;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.config.MessageConstraints;
/*     */ import org.apache.http.entity.ContentLengthStrategy;
/*     */ import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
/*     */ import org.apache.http.impl.io.DefaultHttpResponseParserFactory;
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
/*     */ 
/*     */ 
/*     */ public class DefaultBHttpClientConnection
/*     */   extends BHttpConnectionBase
/*     */   implements HttpClientConnection
/*     */ {
/*     */   private final HttpMessageParser<HttpResponse> responseParser;
/*     */   private final HttpMessageWriter<HttpRequest> requestWriter;
/*     */   
/*     */   public DefaultBHttpClientConnection(int bufferSize, int fragmentSizeHint, CharsetDecoder charDecoder, CharsetEncoder charEncoder, MessageConstraints constraints, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy, HttpMessageWriterFactory<HttpRequest> requestWriterFactory, HttpMessageParserFactory<HttpResponse> responseParserFactory) {
/*  95 */     super(bufferSize, fragmentSizeHint, charDecoder, charEncoder, constraints, incomingContentStrategy, outgoingContentStrategy);
/*     */     
/*  97 */     this.requestWriter = ((requestWriterFactory != null) ? requestWriterFactory : DefaultHttpRequestWriterFactory.INSTANCE).create(getSessionOutputBuffer());
/*     */     
/*  99 */     this.responseParser = ((responseParserFactory != null) ? responseParserFactory : DefaultHttpResponseParserFactory.INSTANCE).create(getSessionInputBuffer(), constraints);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultBHttpClientConnection(int bufferSize, CharsetDecoder charDecoder, CharsetEncoder charEncoder, MessageConstraints constraints) {
/* 108 */     this(bufferSize, bufferSize, charDecoder, charEncoder, constraints, (ContentLengthStrategy)null, (ContentLengthStrategy)null, (HttpMessageWriterFactory<HttpRequest>)null, (HttpMessageParserFactory<HttpResponse>)null);
/*     */   }
/*     */   
/*     */   public DefaultBHttpClientConnection(int bufferSize) {
/* 112 */     this(bufferSize, bufferSize, (CharsetDecoder)null, (CharsetEncoder)null, (MessageConstraints)null, (ContentLengthStrategy)null, (ContentLengthStrategy)null, (HttpMessageWriterFactory<HttpRequest>)null, (HttpMessageParserFactory<HttpResponse>)null);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void onResponseReceived(HttpResponse response) {}
/*     */ 
/*     */   
/*     */   protected void onRequestSubmitted(HttpRequest request) {}
/*     */ 
/*     */   
/*     */   public void bind(Socket socket) throws IOException {
/* 123 */     super.bind(socket);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isResponseAvailable(int timeout) throws IOException {
/* 128 */     ensureOpen();
/*     */     try {
/* 130 */       return awaitInput(timeout);
/* 131 */     } catch (SocketTimeoutException ex) {
/* 132 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void sendRequestHeader(HttpRequest request) throws HttpException, IOException {
/* 139 */     Args.notNull(request, "HTTP request");
/* 140 */     ensureOpen();
/* 141 */     this.requestWriter.write((HttpMessage)request);
/* 142 */     onRequestSubmitted(request);
/* 143 */     incrementRequestCount();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void sendRequestEntity(HttpEntityEnclosingRequest request) throws HttpException, IOException {
/* 149 */     Args.notNull(request, "HTTP request");
/* 150 */     ensureOpen();
/* 151 */     HttpEntity entity = request.getEntity();
/* 152 */     if (entity == null) {
/*     */       return;
/*     */     }
/* 155 */     OutputStream outStream = prepareOutput((HttpMessage)request);
/* 156 */     entity.writeTo(outStream);
/* 157 */     outStream.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpResponse receiveResponseHeader() throws HttpException, IOException {
/* 162 */     ensureOpen();
/* 163 */     HttpResponse response = (HttpResponse)this.responseParser.parse();
/* 164 */     onResponseReceived(response);
/* 165 */     if (response.getStatusLine().getStatusCode() >= 200) {
/* 166 */       incrementResponseCount();
/*     */     }
/* 168 */     return response;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void receiveResponseEntity(HttpResponse response) throws HttpException, IOException {
/* 174 */     Args.notNull(response, "HTTP response");
/* 175 */     ensureOpen();
/* 176 */     HttpEntity entity = prepareInput((HttpMessage)response);
/* 177 */     response.setEntity(entity);
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/* 182 */     ensureOpen();
/* 183 */     doFlush();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\DefaultBHttpClientConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */