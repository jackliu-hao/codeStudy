/*    */ package org.apache.http.impl.io;
/*    */ 
/*    */ import org.apache.http.io.HttpTransportMetrics;
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
/*    */ public class HttpTransportMetricsImpl
/*    */   implements HttpTransportMetrics
/*    */ {
/* 39 */   private long bytesTransferred = 0L;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public long getBytesTransferred() {
/* 47 */     return this.bytesTransferred;
/*    */   }
/*    */   
/*    */   public void setBytesTransferred(long count) {
/* 51 */     this.bytesTransferred = count;
/*    */   }
/*    */   
/*    */   public void incrementBytesTransferred(long count) {
/* 55 */     this.bytesTransferred += count;
/*    */   }
/*    */ 
/*    */   
/*    */   public void reset() {
/* 60 */     this.bytesTransferred = 0L;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\io\HttpTransportMetricsImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */