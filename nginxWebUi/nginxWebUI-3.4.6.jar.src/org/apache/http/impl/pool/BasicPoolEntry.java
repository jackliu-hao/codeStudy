/*    */ package org.apache.http.impl.pool;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.http.HttpClientConnection;
/*    */ import org.apache.http.HttpHost;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.pool.PoolEntry;
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
/*    */ @Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
/*    */ public class BasicPoolEntry
/*    */   extends PoolEntry<HttpHost, HttpClientConnection>
/*    */ {
/*    */   public BasicPoolEntry(String id, HttpHost route, HttpClientConnection conn) {
/* 49 */     super(id, route, conn);
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() {
/*    */     try {
/* 55 */       HttpClientConnection connection = (HttpClientConnection)getConnection();
/*    */       try {
/* 57 */         int socketTimeout = connection.getSocketTimeout();
/* 58 */         if (socketTimeout <= 0 || socketTimeout > 1000) {
/* 59 */           connection.setSocketTimeout(1000);
/*    */         }
/* 61 */         connection.close();
/* 62 */       } catch (IOException ex) {
/* 63 */         connection.shutdown();
/*    */       } 
/* 65 */     } catch (IOException ignore) {}
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isClosed() {
/* 71 */     return !((HttpClientConnection)getConnection()).isOpen();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\pool\BasicPoolEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */