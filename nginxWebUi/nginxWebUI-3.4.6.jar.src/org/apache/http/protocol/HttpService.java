/*     */ package org.apache.http.protocol;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.http.ConnectionReuseStrategy;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.HttpEntityEnclosingRequest;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.HttpResponseFactory;
/*     */ import org.apache.http.HttpServerConnection;
/*     */ import org.apache.http.HttpVersion;
/*     */ import org.apache.http.ProtocolVersion;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.entity.ByteArrayEntity;
/*     */ import org.apache.http.impl.DefaultConnectionReuseStrategy;
/*     */ import org.apache.http.impl.DefaultHttpResponseFactory;
/*     */ import org.apache.http.params.HttpParams;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.EncodingUtils;
/*     */ import org.apache.http.util.EntityUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ public class HttpService
/*     */ {
/*  81 */   private volatile HttpParams params = null;
/*  82 */   private volatile HttpProcessor processor = null;
/*  83 */   private volatile HttpRequestHandlerMapper handlerMapper = null;
/*  84 */   private volatile ConnectionReuseStrategy connStrategy = null;
/*  85 */   private volatile HttpResponseFactory responseFactory = null;
/*  86 */   private volatile HttpExpectationVerifier expectationVerifier = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public HttpService(HttpProcessor processor, ConnectionReuseStrategy connStrategy, HttpResponseFactory responseFactory, HttpRequestHandlerResolver handlerResolver, HttpExpectationVerifier expectationVerifier, HttpParams params) {
/* 110 */     this(processor, connStrategy, responseFactory, new HttpRequestHandlerResolverAdapter(handlerResolver), expectationVerifier);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 115 */     this.params = params;
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
/*     */   @Deprecated
/*     */   public HttpService(HttpProcessor processor, ConnectionReuseStrategy connStrategy, HttpResponseFactory responseFactory, HttpRequestHandlerResolver handlerResolver, HttpParams params) {
/* 138 */     this(processor, connStrategy, responseFactory, new HttpRequestHandlerResolverAdapter(handlerResolver), (HttpExpectationVerifier)null);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 143 */     this.params = params;
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
/*     */   @Deprecated
/*     */   public HttpService(HttpProcessor proc, ConnectionReuseStrategy connStrategy, HttpResponseFactory responseFactory) {
/* 162 */     setHttpProcessor(proc);
/* 163 */     setConnReuseStrategy(connStrategy);
/* 164 */     setResponseFactory(responseFactory);
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
/*     */   public HttpService(HttpProcessor processor, ConnectionReuseStrategy connStrategy, HttpResponseFactory responseFactory, HttpRequestHandlerMapper handlerMapper, HttpExpectationVerifier expectationVerifier) {
/* 187 */     this.processor = (HttpProcessor)Args.notNull(processor, "HTTP processor");
/* 188 */     this.connStrategy = (connStrategy != null) ? connStrategy : (ConnectionReuseStrategy)DefaultConnectionReuseStrategy.INSTANCE;
/*     */     
/* 190 */     this.responseFactory = (responseFactory != null) ? responseFactory : (HttpResponseFactory)DefaultHttpResponseFactory.INSTANCE;
/*     */     
/* 192 */     this.handlerMapper = handlerMapper;
/* 193 */     this.expectationVerifier = expectationVerifier;
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
/*     */   public HttpService(HttpProcessor processor, ConnectionReuseStrategy connStrategy, HttpResponseFactory responseFactory, HttpRequestHandlerMapper handlerMapper) {
/* 213 */     this(processor, connStrategy, responseFactory, handlerMapper, (HttpExpectationVerifier)null);
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
/*     */   public HttpService(HttpProcessor processor, HttpRequestHandlerMapper handlerMapper) {
/* 226 */     this(processor, (ConnectionReuseStrategy)null, (HttpResponseFactory)null, handlerMapper, (HttpExpectationVerifier)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setHttpProcessor(HttpProcessor processor) {
/* 234 */     Args.notNull(processor, "HTTP processor");
/* 235 */     this.processor = processor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setConnReuseStrategy(ConnectionReuseStrategy connStrategy) {
/* 243 */     Args.notNull(connStrategy, "Connection reuse strategy");
/* 244 */     this.connStrategy = connStrategy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setResponseFactory(HttpResponseFactory responseFactory) {
/* 252 */     Args.notNull(responseFactory, "Response factory");
/* 253 */     this.responseFactory = responseFactory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setParams(HttpParams params) {
/* 261 */     this.params = params;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setHandlerResolver(HttpRequestHandlerResolver handlerResolver) {
/* 269 */     this.handlerMapper = new HttpRequestHandlerResolverAdapter(handlerResolver);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setExpectationVerifier(HttpExpectationVerifier expectationVerifier) {
/* 277 */     this.expectationVerifier = expectationVerifier;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public HttpParams getParams() {
/* 285 */     return this.params;
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
/*     */   public void handleRequest(HttpServerConnection conn, HttpContext context) throws IOException, HttpException {
/* 302 */     context.setAttribute("http.connection", conn);
/*     */     
/* 304 */     HttpRequest request = null;
/* 305 */     HttpResponse response = null;
/*     */     
/*     */     try {
/* 308 */       request = conn.receiveRequestHeader();
/* 309 */       if (request instanceof HttpEntityEnclosingRequest)
/*     */       {
/* 311 */         if (((HttpEntityEnclosingRequest)request).expectContinue()) {
/* 312 */           response = this.responseFactory.newHttpResponse((ProtocolVersion)HttpVersion.HTTP_1_1, 100, context);
/*     */           
/* 314 */           if (this.expectationVerifier != null) {
/*     */             try {
/* 316 */               this.expectationVerifier.verify(request, response, context);
/* 317 */             } catch (HttpException ex) {
/* 318 */               response = this.responseFactory.newHttpResponse((ProtocolVersion)HttpVersion.HTTP_1_0, 500, context);
/*     */               
/* 320 */               handleException(ex, response);
/*     */             } 
/*     */           }
/* 323 */           if (response.getStatusLine().getStatusCode() < 200) {
/*     */ 
/*     */             
/* 326 */             conn.sendResponseHeader(response);
/* 327 */             conn.flush();
/* 328 */             response = null;
/* 329 */             conn.receiveRequestEntity((HttpEntityEnclosingRequest)request);
/*     */           } 
/*     */         } else {
/* 332 */           conn.receiveRequestEntity((HttpEntityEnclosingRequest)request);
/*     */         } 
/*     */       }
/*     */       
/* 336 */       context.setAttribute("http.request", request);
/*     */       
/* 338 */       if (response == null) {
/* 339 */         response = this.responseFactory.newHttpResponse((ProtocolVersion)HttpVersion.HTTP_1_1, 200, context);
/*     */         
/* 341 */         this.processor.process(request, context);
/* 342 */         doService(request, response, context);
/*     */       } 
/*     */ 
/*     */       
/* 346 */       if (request instanceof HttpEntityEnclosingRequest) {
/* 347 */         HttpEntity entity = ((HttpEntityEnclosingRequest)request).getEntity();
/* 348 */         EntityUtils.consume(entity);
/*     */       }
/*     */     
/* 351 */     } catch (HttpException ex) {
/* 352 */       response = this.responseFactory.newHttpResponse((ProtocolVersion)HttpVersion.HTTP_1_0, 500, context);
/*     */ 
/*     */       
/* 355 */       handleException(ex, response);
/*     */     } 
/*     */     
/* 358 */     context.setAttribute("http.response", response);
/*     */     
/* 360 */     this.processor.process(response, context);
/* 361 */     conn.sendResponseHeader(response);
/* 362 */     if (canResponseHaveBody(request, response)) {
/* 363 */       conn.sendResponseEntity(response);
/*     */     }
/* 365 */     conn.flush();
/*     */     
/* 367 */     if (!this.connStrategy.keepAlive(response, context)) {
/* 368 */       conn.close();
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean canResponseHaveBody(HttpRequest request, HttpResponse response) {
/* 373 */     if (request != null && "HEAD".equalsIgnoreCase(request.getRequestLine().getMethod())) {
/* 374 */       return false;
/*     */     }
/* 376 */     int status = response.getStatusLine().getStatusCode();
/* 377 */     return (status >= 200 && status != 204 && status != 304 && status != 205);
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
/*     */   protected void handleException(HttpException ex, HttpResponse response) {
/* 392 */     if (ex instanceof org.apache.http.MethodNotSupportedException) {
/* 393 */       response.setStatusCode(501);
/* 394 */     } else if (ex instanceof org.apache.http.UnsupportedHttpVersionException) {
/* 395 */       response.setStatusCode(505);
/* 396 */     } else if (ex instanceof org.apache.http.ProtocolException) {
/* 397 */       response.setStatusCode(400);
/*     */     } else {
/* 399 */       response.setStatusCode(500);
/*     */     } 
/* 401 */     String message = ex.getMessage();
/* 402 */     if (message == null) {
/* 403 */       message = ex.toString();
/*     */     }
/* 405 */     byte[] msg = EncodingUtils.getAsciiBytes(message);
/* 406 */     ByteArrayEntity entity = new ByteArrayEntity(msg);
/* 407 */     entity.setContentType("text/plain; charset=US-ASCII");
/* 408 */     response.setEntity((HttpEntity)entity);
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
/*     */   protected void doService(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
/* 432 */     HttpRequestHandler handler = null;
/* 433 */     if (this.handlerMapper != null) {
/* 434 */       handler = this.handlerMapper.lookup(request);
/*     */     }
/* 436 */     if (handler != null) {
/* 437 */       handler.handle(request, response, context);
/*     */     } else {
/* 439 */       response.setStatusCode(501);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   private static class HttpRequestHandlerResolverAdapter
/*     */     implements HttpRequestHandlerMapper
/*     */   {
/*     */     private final HttpRequestHandlerResolver resolver;
/*     */ 
/*     */ 
/*     */     
/*     */     public HttpRequestHandlerResolverAdapter(HttpRequestHandlerResolver resolver) {
/* 454 */       this.resolver = resolver;
/*     */     }
/*     */ 
/*     */     
/*     */     public HttpRequestHandler lookup(HttpRequest request) {
/* 459 */       return this.resolver.lookup(request.getRequestLine().getUri());
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\protocol\HttpService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */