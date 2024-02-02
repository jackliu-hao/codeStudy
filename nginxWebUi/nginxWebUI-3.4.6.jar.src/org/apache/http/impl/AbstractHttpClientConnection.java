/*     */ package org.apache.http.impl;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.SocketTimeoutException;
/*     */ import org.apache.http.HttpClientConnection;
/*     */ import org.apache.http.HttpConnectionMetrics;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.HttpEntityEnclosingRequest;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpMessage;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.HttpResponseFactory;
/*     */ import org.apache.http.entity.ContentLengthStrategy;
/*     */ import org.apache.http.impl.entity.EntityDeserializer;
/*     */ import org.apache.http.impl.entity.EntitySerializer;
/*     */ import org.apache.http.impl.entity.LaxContentLengthStrategy;
/*     */ import org.apache.http.impl.entity.StrictContentLengthStrategy;
/*     */ import org.apache.http.impl.io.DefaultHttpResponseParser;
/*     */ import org.apache.http.impl.io.HttpRequestWriter;
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
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public abstract class AbstractHttpClientConnection
/*     */   implements HttpClientConnection
/*     */ {
/*     */   private final EntitySerializer entityserializer;
/*     */   private final EntityDeserializer entitydeserializer;
/*  81 */   private SessionInputBuffer inBuffer = null;
/*  82 */   private SessionOutputBuffer outbuffer = null;
/*  83 */   private EofSensor eofSensor = null;
/*  84 */   private HttpMessageParser<HttpResponse> responseParser = null;
/*  85 */   private HttpMessageWriter<HttpRequest> requestWriter = null;
/*  86 */   private HttpConnectionMetricsImpl metrics = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AbstractHttpClientConnection() {
/*  99 */     this.entityserializer = createEntitySerializer();
/* 100 */     this.entitydeserializer = createEntityDeserializer();
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
/* 123 */     return new EntityDeserializer((ContentLengthStrategy)new LaxContentLengthStrategy());
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
/* 139 */     return new EntitySerializer((ContentLengthStrategy)new StrictContentLengthStrategy());
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
/*     */   protected HttpResponseFactory createHttpResponseFactory() {
/* 154 */     return DefaultHttpResponseFactory.INSTANCE;
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
/*     */   protected HttpMessageParser<HttpResponse> createResponseParser(SessionInputBuffer buffer, HttpResponseFactory responseFactory, HttpParams params) {
/* 177 */     return (HttpMessageParser<HttpResponse>)new DefaultHttpResponseParser(buffer, null, responseFactory, params);
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
/*     */   protected HttpMessageWriter<HttpRequest> createRequestWriter(SessionOutputBuffer buffer, HttpParams params) {
/* 198 */     return (HttpMessageWriter<HttpRequest>)new HttpRequestWriter(buffer, null, params);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected HttpConnectionMetricsImpl createConnectionMetrics(HttpTransportMetrics inTransportMetric, HttpTransportMetrics outTransportMetric) {
/* 207 */     return new HttpConnectionMetricsImpl(inTransportMetric, outTransportMetric);
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
/*     */   
/*     */   protected void init(SessionInputBuffer sessionInputBuffer, SessionOutputBuffer sessionOutputBuffer, HttpParams params) {
/* 231 */     this.inBuffer = (SessionInputBuffer)Args.notNull(sessionInputBuffer, "Input session buffer");
/* 232 */     this.outbuffer = (SessionOutputBuffer)Args.notNull(sessionOutputBuffer, "Output session buffer");
/* 233 */     if (sessionInputBuffer instanceof EofSensor) {
/* 234 */       this.eofSensor = (EofSensor)sessionInputBuffer;
/*     */     }
/* 236 */     this.responseParser = createResponseParser(sessionInputBuffer, createHttpResponseFactory(), params);
/*     */ 
/*     */ 
/*     */     
/* 240 */     this.requestWriter = createRequestWriter(sessionOutputBuffer, params);
/*     */     
/* 242 */     this.metrics = createConnectionMetrics(sessionInputBuffer.getMetrics(), sessionOutputBuffer.getMetrics());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isResponseAvailable(int timeout) throws IOException {
/* 249 */     assertOpen();
/*     */     try {
/* 251 */       return this.inBuffer.isDataAvailable(timeout);
/* 252 */     } catch (SocketTimeoutException ex) {
/* 253 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void sendRequestHeader(HttpRequest request) throws HttpException, IOException {
/* 260 */     Args.notNull(request, "HTTP request");
/* 261 */     assertOpen();
/* 262 */     this.requestWriter.write((HttpMessage)request);
/* 263 */     this.metrics.incrementRequestCount();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void sendRequestEntity(HttpEntityEnclosingRequest request) throws HttpException, IOException {
/* 269 */     Args.notNull(request, "HTTP request");
/* 270 */     assertOpen();
/* 271 */     if (request.getEntity() == null) {
/*     */       return;
/*     */     }
/* 274 */     this.entityserializer.serialize(this.outbuffer, (HttpMessage)request, request.getEntity());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doFlush() throws IOException {
/* 281 */     this.outbuffer.flush();
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/* 286 */     assertOpen();
/* 287 */     doFlush();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpResponse receiveResponseHeader() throws HttpException, IOException {
/* 293 */     assertOpen();
/* 294 */     HttpResponse response = (HttpResponse)this.responseParser.parse();
/* 295 */     if (response.getStatusLine().getStatusCode() >= 200) {
/* 296 */       this.metrics.incrementResponseCount();
/*     */     }
/* 298 */     return response;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void receiveResponseEntity(HttpResponse response) throws HttpException, IOException {
/* 304 */     Args.notNull(response, "HTTP response");
/* 305 */     assertOpen();
/* 306 */     HttpEntity entity = this.entitydeserializer.deserialize(this.inBuffer, (HttpMessage)response);
/* 307 */     response.setEntity(entity);
/*     */   }
/*     */   
/*     */   protected boolean isEof() {
/* 311 */     return (this.eofSensor != null && this.eofSensor.isEof());
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isStale() {
/* 316 */     if (!isOpen()) {
/* 317 */       return true;
/*     */     }
/* 319 */     if (isEof()) {
/* 320 */       return true;
/*     */     }
/*     */     try {
/* 323 */       this.inBuffer.isDataAvailable(1);
/* 324 */       return isEof();
/* 325 */     } catch (SocketTimeoutException ex) {
/* 326 */       return false;
/* 327 */     } catch (IOException ex) {
/* 328 */       return true;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpConnectionMetrics getMetrics() {
/* 334 */     return this.metrics;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\AbstractHttpClientConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */