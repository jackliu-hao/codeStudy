/*     */ package org.apache.http.impl;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.http.HttpConnectionMetrics;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.HttpEntityEnclosingRequest;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpMessage;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpRequestFactory;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.HttpServerConnection;
/*     */ import org.apache.http.entity.ContentLengthStrategy;
/*     */ import org.apache.http.impl.entity.DisallowIdentityContentLengthStrategy;
/*     */ import org.apache.http.impl.entity.EntityDeserializer;
/*     */ import org.apache.http.impl.entity.EntitySerializer;
/*     */ import org.apache.http.impl.entity.LaxContentLengthStrategy;
/*     */ import org.apache.http.impl.entity.StrictContentLengthStrategy;
/*     */ import org.apache.http.impl.io.DefaultHttpRequestParser;
/*     */ import org.apache.http.impl.io.HttpResponseWriter;
/*     */ import org.apache.http.io.EofSensor;
/*     */ import org.apache.http.io.HttpMessageParser;
/*     */ import org.apache.http.io.HttpMessageWriter;
/*     */ import org.apache.http.io.HttpTransportMetrics;
/*     */ import org.apache.http.io.SessionInputBuffer;
/*     */ import org.apache.http.io.SessionOutputBuffer;
/*     */ import org.apache.http.params.HttpParams;
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
/*     */ @Deprecated
/*     */ public abstract class AbstractHttpServerConnection
/*     */   implements HttpServerConnection
/*     */ {
/*     */   private final EntitySerializer entityserializer;
/*     */   private final EntityDeserializer entitydeserializer;
/*  79 */   private SessionInputBuffer inBuffer = null;
/*  80 */   private SessionOutputBuffer outbuffer = null;
/*  81 */   private EofSensor eofSensor = null;
/*  82 */   private HttpMessageParser<HttpRequest> requestParser = null;
/*  83 */   private HttpMessageWriter<HttpResponse> responseWriter = null;
/*  84 */   private HttpConnectionMetricsImpl metrics = null;
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
/*     */   public AbstractHttpServerConnection() {
/*  96 */     this.entityserializer = createEntitySerializer();
/*  97 */     this.entitydeserializer = createEntityDeserializer();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void assertOpen() throws IllegalStateException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected EntityDeserializer createEntityDeserializer() {
/* 119 */     return new EntityDeserializer((ContentLengthStrategy)new DisallowIdentityContentLengthStrategy((ContentLengthStrategy)new LaxContentLengthStrategy(0)));
/*     */   }
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
/*     */   protected EntitySerializer createEntitySerializer() {
/* 135 */     return new EntitySerializer((ContentLengthStrategy)new StrictContentLengthStrategy());
/*     */   }
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
/*     */   protected HttpRequestFactory createHttpRequestFactory() {
/* 149 */     return DefaultHttpRequestFactory.INSTANCE;
/*     */   }
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
/*     */   protected HttpMessageParser<HttpRequest> createRequestParser(SessionInputBuffer buffer, HttpRequestFactory requestFactory, HttpParams params) {
/* 171 */     return (HttpMessageParser<HttpRequest>)new DefaultHttpRequestParser(buffer, null, requestFactory, params);
/*     */   }
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
/*     */   protected HttpMessageWriter<HttpResponse> createResponseWriter(SessionOutputBuffer buffer, HttpParams params) {
/* 191 */     return (HttpMessageWriter<HttpResponse>)new HttpResponseWriter(buffer, null, params);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected HttpConnectionMetricsImpl createConnectionMetrics(HttpTransportMetrics inTransportMetric, HttpTransportMetrics outTransportMetric) {
/* 200 */     return new HttpConnectionMetricsImpl(inTransportMetric, outTransportMetric);
/*     */   }
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
/*     */   protected void init(SessionInputBuffer inBuffer, SessionOutputBuffer outbuffer, HttpParams params) {
/* 223 */     this.inBuffer = (SessionInputBuffer)Args.notNull(inBuffer, "Input session buffer");
/* 224 */     this.outbuffer = (SessionOutputBuffer)Args.notNull(outbuffer, "Output session buffer");
/* 225 */     if (inBuffer instanceof EofSensor) {
/* 226 */       this.eofSensor = (EofSensor)inBuffer;
/*     */     }
/* 228 */     this.requestParser = createRequestParser(inBuffer, createHttpRequestFactory(), params);
/*     */ 
/*     */ 
/*     */     
/* 232 */     this.responseWriter = createResponseWriter(outbuffer, params);
/*     */     
/* 234 */     this.metrics = createConnectionMetrics(inBuffer.getMetrics(), outbuffer.getMetrics());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpRequest receiveRequestHeader() throws HttpException, IOException {
/* 242 */     assertOpen();
/* 243 */     HttpRequest request = (HttpRequest)this.requestParser.parse();
/* 244 */     this.metrics.incrementRequestCount();
/* 245 */     return request;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void receiveRequestEntity(HttpEntityEnclosingRequest request) throws HttpException, IOException {
/* 251 */     Args.notNull(request, "HTTP request");
/* 252 */     assertOpen();
/* 253 */     HttpEntity entity = this.entitydeserializer.deserialize(this.inBuffer, (HttpMessage)request);
/* 254 */     request.setEntity(entity);
/*     */   }
/*     */   
/*     */   protected void doFlush() throws IOException {
/* 258 */     this.outbuffer.flush();
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/* 263 */     assertOpen();
/* 264 */     doFlush();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void sendResponseHeader(HttpResponse response) throws HttpException, IOException {
/* 270 */     Args.notNull(response, "HTTP response");
/* 271 */     assertOpen();
/* 272 */     this.responseWriter.write((HttpMessage)response);
/* 273 */     if (response.getStatusLine().getStatusCode() >= 200) {
/* 274 */       this.metrics.incrementResponseCount();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void sendResponseEntity(HttpResponse response) throws HttpException, IOException {
/* 281 */     if (response.getEntity() == null) {
/*     */       return;
/*     */     }
/* 284 */     this.entityserializer.serialize(this.outbuffer, (HttpMessage)response, response.getEntity());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isEof() {
/* 291 */     return (this.eofSensor != null && this.eofSensor.isEof());
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isStale() {
/* 296 */     if (!isOpen()) {
/* 297 */       return true;
/*     */     }
/* 299 */     if (isEof()) {
/* 300 */       return true;
/*     */     }
/*     */     try {
/* 303 */       this.inBuffer.isDataAvailable(1);
/* 304 */       return isEof();
/* 305 */     } catch (IOException ex) {
/* 306 */       return true;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpConnectionMetrics getMetrics() {
/* 312 */     return this.metrics;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\AbstractHttpServerConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */