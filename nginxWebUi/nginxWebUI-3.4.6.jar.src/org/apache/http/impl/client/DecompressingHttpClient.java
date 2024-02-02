/*     */ package org.apache.http.impl.client;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.URI;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.HttpEntityEnclosingRequest;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpRequestInterceptor;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.HttpResponseInterceptor;
/*     */ import org.apache.http.client.ClientProtocolException;
/*     */ import org.apache.http.client.HttpClient;
/*     */ import org.apache.http.client.ResponseHandler;
/*     */ import org.apache.http.client.methods.HttpUriRequest;
/*     */ import org.apache.http.client.protocol.RequestAcceptEncoding;
/*     */ import org.apache.http.client.protocol.ResponseContentEncoding;
/*     */ import org.apache.http.client.utils.URIUtils;
/*     */ import org.apache.http.conn.ClientConnectionManager;
/*     */ import org.apache.http.params.HttpParams;
/*     */ import org.apache.http.protocol.BasicHttpContext;
/*     */ import org.apache.http.protocol.HttpContext;
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
/*     */ @Deprecated
/*     */ public class DecompressingHttpClient
/*     */   implements HttpClient
/*     */ {
/*     */   private final HttpClient backend;
/*     */   private final HttpRequestInterceptor acceptEncodingInterceptor;
/*     */   private final HttpResponseInterceptor contentEncodingInterceptor;
/*     */   
/*     */   public DecompressingHttpClient() {
/*  89 */     this(new DefaultHttpClient());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DecompressingHttpClient(HttpClient backend) {
/*  99 */     this(backend, (HttpRequestInterceptor)new RequestAcceptEncoding(), (HttpResponseInterceptor)new ResponseContentEncoding());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   DecompressingHttpClient(HttpClient backend, HttpRequestInterceptor requestInterceptor, HttpResponseInterceptor responseInterceptor) {
/* 105 */     this.backend = backend;
/* 106 */     this.acceptEncodingInterceptor = requestInterceptor;
/* 107 */     this.contentEncodingInterceptor = responseInterceptor;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpParams getParams() {
/* 112 */     return this.backend.getParams();
/*     */   }
/*     */ 
/*     */   
/*     */   public ClientConnectionManager getConnectionManager() {
/* 117 */     return this.backend.getConnectionManager();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpResponse execute(HttpUriRequest request) throws IOException, ClientProtocolException {
/* 123 */     return execute(getHttpHost(request), (HttpRequest)request, (HttpContext)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpClient getHttpClient() {
/* 132 */     return this.backend;
/*     */   }
/*     */   
/*     */   HttpHost getHttpHost(HttpUriRequest request) {
/* 136 */     URI uri = request.getURI();
/* 137 */     return URIUtils.extractHost(uri);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpResponse execute(HttpUriRequest request, HttpContext context) throws IOException, ClientProtocolException {
/* 143 */     return execute(getHttpHost(request), (HttpRequest)request, context);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpResponse execute(HttpHost target, HttpRequest request) throws IOException, ClientProtocolException {
/* 149 */     return execute(target, request, (HttpContext)null);
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpResponse execute(HttpHost target, HttpRequest request, HttpContext context) throws IOException, ClientProtocolException {
/*     */     try {
/*     */       RequestWrapper requestWrapper;
/* 156 */       HttpContext localContext = (context != null) ? context : (HttpContext)new BasicHttpContext();
/*     */       
/* 158 */       if (request instanceof HttpEntityEnclosingRequest) {
/* 159 */         requestWrapper = new EntityEnclosingRequestWrapper((HttpEntityEnclosingRequest)request);
/*     */       } else {
/* 161 */         requestWrapper = new RequestWrapper(request);
/*     */       } 
/* 163 */       this.acceptEncodingInterceptor.process((HttpRequest)requestWrapper, localContext);
/* 164 */       HttpResponse response = this.backend.execute(target, (HttpRequest)requestWrapper, localContext);
/*     */       try {
/* 166 */         this.contentEncodingInterceptor.process(response, localContext);
/* 167 */         if (Boolean.TRUE.equals(localContext.getAttribute("http.client.response.uncompressed"))) {
/* 168 */           response.removeHeaders("Content-Length");
/* 169 */           response.removeHeaders("Content-Encoding");
/* 170 */           response.removeHeaders("Content-MD5");
/*     */         } 
/* 172 */         return response;
/* 173 */       } catch (HttpException ex) {
/* 174 */         EntityUtils.consume(response.getEntity());
/* 175 */         throw ex;
/* 176 */       } catch (IOException ex) {
/* 177 */         EntityUtils.consume(response.getEntity());
/* 178 */         throw ex;
/* 179 */       } catch (RuntimeException ex) {
/* 180 */         EntityUtils.consume(response.getEntity());
/* 181 */         throw ex;
/*     */       } 
/* 183 */     } catch (HttpException e) {
/* 184 */       throw new ClientProtocolException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
/* 192 */     return execute(getHttpHost(request), (HttpRequest)request, responseHandler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
/* 199 */     return execute(getHttpHost(request), (HttpRequest)request, responseHandler, context);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
/* 206 */     return execute(target, request, responseHandler, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
/* 213 */     HttpResponse response = execute(target, request, context);
/*     */     try {
/* 215 */       return (T)responseHandler.handleResponse(response);
/*     */     } finally {
/* 217 */       HttpEntity entity = response.getEntity();
/* 218 */       if (entity != null)
/* 219 */         EntityUtils.consume(entity); 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\DecompressingHttpClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */