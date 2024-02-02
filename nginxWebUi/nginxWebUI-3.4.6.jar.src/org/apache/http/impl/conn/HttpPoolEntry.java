/*    */ package org.apache.http.impl.conn;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.Date;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.apache.http.conn.OperatedClientConnection;
/*    */ import org.apache.http.conn.routing.HttpRoute;
/*    */ import org.apache.http.conn.routing.RouteTracker;
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
/*    */ 
/*    */ 
/*    */ @Deprecated
/*    */ class HttpPoolEntry
/*    */   extends PoolEntry<HttpRoute, OperatedClientConnection>
/*    */ {
/*    */   private final Log log;
/*    */   private final RouteTracker tracker;
/*    */   
/*    */   public HttpPoolEntry(Log log, String id, HttpRoute route, OperatedClientConnection conn, long timeToLive, TimeUnit timeUnit) {
/* 56 */     super(id, route, conn, timeToLive, timeUnit);
/* 57 */     this.log = log;
/* 58 */     this.tracker = new RouteTracker(route);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isExpired(long now) {
/* 63 */     boolean expired = super.isExpired(now);
/* 64 */     if (expired && this.log.isDebugEnabled()) {
/* 65 */       this.log.debug("Connection " + this + " expired @ " + new Date(getExpiry()));
/*    */     }
/* 67 */     return expired;
/*    */   }
/*    */   
/*    */   RouteTracker getTracker() {
/* 71 */     return this.tracker;
/*    */   }
/*    */   
/*    */   HttpRoute getPlannedRoute() {
/* 75 */     return (HttpRoute)getRoute();
/*    */   }
/*    */   
/*    */   HttpRoute getEffectiveRoute() {
/* 79 */     return this.tracker.toRoute();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isClosed() {
/* 84 */     OperatedClientConnection conn = (OperatedClientConnection)getConnection();
/* 85 */     return !conn.isOpen();
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() {
/* 90 */     OperatedClientConnection conn = (OperatedClientConnection)getConnection();
/*    */     try {
/* 92 */       conn.close();
/* 93 */     } catch (IOException ex) {
/* 94 */       this.log.debug("I/O error closing connection", ex);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\HttpPoolEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */