/*    */ package org.apache.http.impl.conn;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.Date;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.apache.http.HttpClientConnection;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.conn.ManagedHttpClientConnection;
/*    */ import org.apache.http.conn.routing.HttpRoute;
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
/*    */ @Contract(threading = ThreadingBehavior.SAFE)
/*    */ class CPoolEntry
/*    */   extends PoolEntry<HttpRoute, ManagedHttpClientConnection>
/*    */ {
/*    */   private final Log log;
/*    */   private volatile boolean routeComplete;
/*    */   
/*    */   public CPoolEntry(Log log, String id, HttpRoute route, ManagedHttpClientConnection conn, long timeToLive, TimeUnit timeUnit) {
/* 56 */     super(id, route, conn, timeToLive, timeUnit);
/* 57 */     this.log = log;
/*    */   }
/*    */   
/*    */   public void markRouteComplete() {
/* 61 */     this.routeComplete = true;
/*    */   }
/*    */   
/*    */   public boolean isRouteComplete() {
/* 65 */     return this.routeComplete;
/*    */   }
/*    */   
/*    */   public void closeConnection() throws IOException {
/* 69 */     HttpClientConnection conn = (HttpClientConnection)getConnection();
/* 70 */     conn.close();
/*    */   }
/*    */   
/*    */   public void shutdownConnection() throws IOException {
/* 74 */     HttpClientConnection conn = (HttpClientConnection)getConnection();
/* 75 */     conn.shutdown();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isExpired(long now) {
/* 80 */     boolean expired = super.isExpired(now);
/* 81 */     if (expired && this.log.isDebugEnabled()) {
/* 82 */       this.log.debug("Connection " + this + " expired @ " + new Date(getExpiry()));
/*    */     }
/* 84 */     return expired;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isClosed() {
/* 89 */     HttpClientConnection conn = (HttpClientConnection)getConnection();
/* 90 */     return !conn.isOpen();
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() {
/*    */     try {
/* 96 */       closeConnection();
/* 97 */     } catch (IOException ex) {
/* 98 */       this.log.debug("I/O error closing connection", ex);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\CPoolEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */