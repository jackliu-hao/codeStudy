/*    */ package org.apache.http.impl.conn;
/*    */ 
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import java.util.concurrent.atomic.AtomicLong;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.apache.commons.logging.LogFactory;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.conn.ManagedHttpClientConnection;
/*    */ import org.apache.http.conn.routing.HttpRoute;
/*    */ import org.apache.http.pool.AbstractConnPool;
/*    */ import org.apache.http.pool.ConnFactory;
/*    */ import org.apache.http.pool.PoolEntry;
/*    */ import org.apache.http.pool.PoolEntryCallback;
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
/*    */ class CPool
/*    */   extends AbstractConnPool<HttpRoute, ManagedHttpClientConnection, CPoolEntry>
/*    */ {
/* 48 */   private static final AtomicLong COUNTER = new AtomicLong();
/*    */   
/* 50 */   private final Log log = LogFactory.getLog(CPool.class);
/*    */   
/*    */   private final long timeToLive;
/*    */   
/*    */   private final TimeUnit timeUnit;
/*    */ 
/*    */   
/*    */   public CPool(ConnFactory<HttpRoute, ManagedHttpClientConnection> connFactory, int defaultMaxPerRoute, int maxTotal, long timeToLive, TimeUnit timeUnit) {
/* 58 */     super(connFactory, defaultMaxPerRoute, maxTotal);
/* 59 */     this.timeToLive = timeToLive;
/* 60 */     this.timeUnit = timeUnit;
/*    */   }
/*    */ 
/*    */   
/*    */   protected CPoolEntry createEntry(HttpRoute route, ManagedHttpClientConnection conn) {
/* 65 */     String id = Long.toString(COUNTER.getAndIncrement());
/* 66 */     return new CPoolEntry(this.log, id, route, conn, this.timeToLive, this.timeUnit);
/*    */   }
/*    */ 
/*    */   
/*    */   protected boolean validate(CPoolEntry entry) {
/* 71 */     return !((ManagedHttpClientConnection)entry.getConnection()).isStale();
/*    */   }
/*    */ 
/*    */   
/*    */   protected void enumAvailable(PoolEntryCallback<HttpRoute, ManagedHttpClientConnection> callback) {
/* 76 */     super.enumAvailable(callback);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void enumLeased(PoolEntryCallback<HttpRoute, ManagedHttpClientConnection> callback) {
/* 81 */     super.enumLeased(callback);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\CPool.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */