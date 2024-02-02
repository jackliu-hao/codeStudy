/*    */ package org.apache.http.impl.client;
/*    */ 
/*    */ import org.apache.http.HttpResponse;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.client.ServiceUnavailableRetryStrategy;
/*    */ import org.apache.http.protocol.HttpContext;
/*    */ import org.apache.http.util.Args;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ public class DefaultServiceUnavailableRetryStrategy
/*    */   implements ServiceUnavailableRetryStrategy
/*    */ {
/*    */   private final int maxRetries;
/*    */   private final long retryInterval;
/*    */   
/*    */   public DefaultServiceUnavailableRetryStrategy(int maxRetries, int retryInterval) {
/* 62 */     Args.positive(maxRetries, "Max retries");
/* 63 */     Args.positive(retryInterval, "Retry interval");
/* 64 */     this.maxRetries = maxRetries;
/* 65 */     this.retryInterval = retryInterval;
/*    */   }
/*    */   
/*    */   public DefaultServiceUnavailableRetryStrategy() {
/* 69 */     this(1, 1000);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean retryRequest(HttpResponse response, int executionCount, HttpContext context) {
/* 74 */     return (executionCount <= this.maxRetries && response.getStatusLine().getStatusCode() == 503);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public long getRetryInterval() {
/* 80 */     return this.retryInterval;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\DefaultServiceUnavailableRetryStrategy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */