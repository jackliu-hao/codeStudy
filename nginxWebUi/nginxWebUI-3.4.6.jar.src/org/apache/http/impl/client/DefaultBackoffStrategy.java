/*    */ package org.apache.http.impl.client;
/*    */ 
/*    */ import org.apache.http.HttpResponse;
/*    */ import org.apache.http.client.ConnectionBackoffStrategy;
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
/*    */ public class DefaultBackoffStrategy
/*    */   implements ConnectionBackoffStrategy
/*    */ {
/*    */   public boolean shouldBackoff(Throwable t) {
/* 47 */     return (t instanceof java.net.SocketTimeoutException || t instanceof java.net.ConnectException);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean shouldBackoff(HttpResponse resp) {
/* 52 */     return (resp.getStatusLine().getStatusCode() == 429 || resp.getStatusLine().getStatusCode() == 503);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\DefaultBackoffStrategy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */