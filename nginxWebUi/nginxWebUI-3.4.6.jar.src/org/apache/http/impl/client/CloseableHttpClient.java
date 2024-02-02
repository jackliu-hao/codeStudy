/*     */ package org.apache.http.impl.client;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.net.URI;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.client.ClientProtocolException;
/*     */ import org.apache.http.client.HttpClient;
/*     */ import org.apache.http.client.ResponseHandler;
/*     */ import org.apache.http.client.methods.CloseableHttpResponse;
/*     */ import org.apache.http.client.methods.HttpUriRequest;
/*     */ import org.apache.http.client.utils.URIUtils;
/*     */ import org.apache.http.protocol.HttpContext;
/*     */ import org.apache.http.util.Args;
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
/*     */ @Contract(threading = ThreadingBehavior.SAFE)
/*     */ public abstract class CloseableHttpClient
/*     */   implements HttpClient, Closeable
/*     */ {
/*  59 */   private final Log log = LogFactory.getLog(getClass());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CloseableHttpResponse execute(HttpHost target, HttpRequest request, HttpContext context) throws IOException, ClientProtocolException {
/*  72 */     return doExecute(target, request, context);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CloseableHttpResponse execute(HttpUriRequest request, HttpContext context) throws IOException, ClientProtocolException {
/*  82 */     Args.notNull(request, "HTTP request");
/*  83 */     return doExecute(determineTarget(request), (HttpRequest)request, context);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static HttpHost determineTarget(HttpUriRequest request) throws ClientProtocolException {
/*  89 */     HttpHost target = null;
/*     */     
/*  91 */     URI requestURI = request.getURI();
/*  92 */     if (requestURI.isAbsolute()) {
/*  93 */       target = URIUtils.extractHost(requestURI);
/*  94 */       if (target == null) {
/*  95 */         throw new ClientProtocolException("URI does not specify a valid host name: " + requestURI);
/*     */       }
/*     */     } 
/*     */     
/*  99 */     return target;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CloseableHttpResponse execute(HttpUriRequest request) throws IOException, ClientProtocolException {
/* 108 */     return execute(request, (HttpContext)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CloseableHttpResponse execute(HttpHost target, HttpRequest request) throws IOException, ClientProtocolException {
/* 118 */     return doExecute(target, request, null);
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
/*     */   public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
/* 140 */     return execute(request, responseHandler, (HttpContext)null);
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
/*     */   public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
/* 164 */     HttpHost target = determineTarget(request);
/* 165 */     return execute(target, (HttpRequest)request, responseHandler, context);
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
/*     */ 
/*     */   
/*     */   public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
/* 191 */     return execute(target, request, responseHandler, null);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
/* 219 */     Args.notNull(responseHandler, "Response handler");
/*     */     
/* 221 */     CloseableHttpResponse response = execute(target, request, context);
/*     */     try {
/* 223 */       T result = (T)responseHandler.handleResponse((HttpResponse)response);
/* 224 */       HttpEntity entity = response.getEntity();
/* 225 */       EntityUtils.consume(entity);
/* 226 */       return result;
/* 227 */     } catch (ClientProtocolException t) {
/*     */       
/* 229 */       HttpEntity entity = response.getEntity();
/*     */       try {
/* 231 */         EntityUtils.consume(entity);
/* 232 */       } catch (Exception t2) {
/*     */ 
/*     */         
/* 235 */         this.log.warn("Error consuming content after an exception.", t2);
/*     */       } 
/* 237 */       throw t;
/*     */     } finally {
/* 239 */       response.close();
/*     */     } 
/*     */   }
/*     */   
/*     */   protected abstract CloseableHttpResponse doExecute(HttpHost paramHttpHost, HttpRequest paramHttpRequest, HttpContext paramHttpContext) throws IOException, ClientProtocolException;
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\CloseableHttpClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */