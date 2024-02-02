/*     */ package org.apache.http.impl.execchain;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.NoHttpResponseException;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.client.HttpRequestRetryHandler;
/*     */ import org.apache.http.client.NonRepeatableRequestException;
/*     */ import org.apache.http.client.methods.CloseableHttpResponse;
/*     */ import org.apache.http.client.methods.HttpExecutionAware;
/*     */ import org.apache.http.client.methods.HttpRequestWrapper;
/*     */ import org.apache.http.client.protocol.HttpClientContext;
/*     */ import org.apache.http.conn.routing.HttpRoute;
/*     */ import org.apache.http.protocol.HttpContext;
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
/*     */ public class RetryExec
/*     */   implements ClientExecChain
/*     */ {
/*  63 */   private final Log log = LogFactory.getLog(getClass());
/*     */   
/*     */   private final ClientExecChain requestExecutor;
/*     */   
/*     */   private final HttpRequestRetryHandler retryHandler;
/*     */ 
/*     */   
/*     */   public RetryExec(ClientExecChain requestExecutor, HttpRequestRetryHandler retryHandler) {
/*  71 */     Args.notNull(requestExecutor, "HTTP request executor");
/*  72 */     Args.notNull(retryHandler, "HTTP request retry handler");
/*  73 */     this.requestExecutor = requestExecutor;
/*  74 */     this.retryHandler = retryHandler;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CloseableHttpResponse execute(HttpRoute route, HttpRequestWrapper request, HttpClientContext context, HttpExecutionAware execAware) throws IOException, HttpException {
/*  83 */     Args.notNull(route, "HTTP route");
/*  84 */     Args.notNull(request, "HTTP request");
/*  85 */     Args.notNull(context, "HTTP context");
/*  86 */     Header[] origheaders = request.getAllHeaders();
/*  87 */     for (int execCount = 1;; execCount++) {
/*     */       try {
/*  89 */         return this.requestExecutor.execute(route, request, context, execAware);
/*  90 */       } catch (IOException ex) {
/*  91 */         if (execAware != null && execAware.isAborted()) {
/*  92 */           this.log.debug("Request has been aborted");
/*  93 */           throw ex;
/*     */         } 
/*  95 */         if (this.retryHandler.retryRequest(ex, execCount, (HttpContext)context)) {
/*  96 */           if (this.log.isInfoEnabled()) {
/*  97 */             this.log.info("I/O exception (" + ex.getClass().getName() + ") caught when processing request to " + route + ": " + ex.getMessage());
/*     */           }
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 103 */           if (this.log.isDebugEnabled()) {
/* 104 */             this.log.debug(ex.getMessage(), ex);
/*     */           }
/* 106 */           if (!RequestEntityProxy.isRepeatable((HttpRequest)request)) {
/* 107 */             this.log.debug("Cannot retry non-repeatable request");
/* 108 */             throw new NonRepeatableRequestException("Cannot retry request with a non-repeatable request entity", ex);
/*     */           } 
/*     */           
/* 111 */           request.setHeaders(origheaders);
/* 112 */           if (this.log.isInfoEnabled()) {
/* 113 */             this.log.info("Retrying request to " + route);
/*     */           }
/*     */         } else {
/* 116 */           if (ex instanceof NoHttpResponseException) {
/* 117 */             NoHttpResponseException updatedex = new NoHttpResponseException(route.getTargetHost().toHostString() + " failed to respond");
/*     */             
/* 119 */             updatedex.setStackTrace(ex.getStackTrace());
/* 120 */             throw updatedex;
/*     */           } 
/* 122 */           throw ex;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\execchain\RetryExec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */