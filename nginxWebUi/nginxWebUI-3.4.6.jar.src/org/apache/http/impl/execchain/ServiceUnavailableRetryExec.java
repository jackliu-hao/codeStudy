/*     */ package org.apache.http.impl.execchain;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InterruptedIOException;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.client.ServiceUnavailableRetryStrategy;
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
/*     */ public class ServiceUnavailableRetryExec
/*     */   implements ClientExecChain
/*     */ {
/*  62 */   private final Log log = LogFactory.getLog(getClass());
/*     */ 
/*     */   
/*     */   private final ClientExecChain requestExecutor;
/*     */   
/*     */   private final ServiceUnavailableRetryStrategy retryStrategy;
/*     */ 
/*     */   
/*     */   public ServiceUnavailableRetryExec(ClientExecChain requestExecutor, ServiceUnavailableRetryStrategy retryStrategy) {
/*  71 */     Args.notNull(requestExecutor, "HTTP request executor");
/*  72 */     Args.notNull(retryStrategy, "Retry strategy");
/*  73 */     this.requestExecutor = requestExecutor;
/*  74 */     this.retryStrategy = retryStrategy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CloseableHttpResponse execute(HttpRoute route, HttpRequestWrapper request, HttpClientContext context, HttpExecutionAware execAware) throws IOException, HttpException {
/*  83 */     Header[] origheaders = request.getAllHeaders();
/*  84 */     for (int c = 1;; c++) {
/*  85 */       CloseableHttpResponse response = this.requestExecutor.execute(route, request, context, execAware);
/*     */       
/*     */       try {
/*  88 */         if (this.retryStrategy.retryRequest((HttpResponse)response, c, (HttpContext)context) && RequestEntityProxy.isRepeatable((HttpRequest)request)) {
/*     */           
/*  90 */           response.close();
/*  91 */           long nextInterval = this.retryStrategy.getRetryInterval();
/*  92 */           if (nextInterval > 0L) {
/*     */             try {
/*  94 */               this.log.trace("Wait for " + nextInterval);
/*  95 */               Thread.sleep(nextInterval);
/*  96 */             } catch (InterruptedException e) {
/*  97 */               Thread.currentThread().interrupt();
/*  98 */               throw new InterruptedIOException();
/*     */             } 
/*     */           }
/* 101 */           request.setHeaders(origheaders);
/*     */         } else {
/* 103 */           return response;
/*     */         } 
/* 105 */       } catch (RuntimeException ex) {
/* 106 */         response.close();
/* 107 */         throw ex;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\execchain\ServiceUnavailableRetryExec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */