/*    */ package org.apache.http.impl.conn;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import java.util.concurrent.atomic.AtomicLong;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.apache.http.conn.ClientConnectionOperator;
/*    */ import org.apache.http.conn.OperatedClientConnection;
/*    */ import org.apache.http.conn.routing.HttpRoute;
/*    */ import org.apache.http.pool.AbstractConnPool;
/*    */ import org.apache.http.pool.ConnFactory;
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
/*    */ @Deprecated
/*    */ class HttpConnPool
/*    */   extends AbstractConnPool<HttpRoute, OperatedClientConnection, HttpPoolEntry>
/*    */ {
/* 48 */   private static final AtomicLong COUNTER = new AtomicLong();
/*    */   
/*    */   private final Log log;
/*    */   
/*    */   private final long timeToLive;
/*    */   
/*    */   private final TimeUnit timeUnit;
/*    */ 
/*    */   
/*    */   public HttpConnPool(Log log, ClientConnectionOperator connOperator, int defaultMaxPerRoute, int maxTotal, long timeToLive, TimeUnit timeUnit) {
/* 58 */     super(new InternalConnFactory(connOperator), defaultMaxPerRoute, maxTotal);
/* 59 */     this.log = log;
/* 60 */     this.timeToLive = timeToLive;
/* 61 */     this.timeUnit = timeUnit;
/*    */   }
/*    */ 
/*    */   
/*    */   protected HttpPoolEntry createEntry(HttpRoute route, OperatedClientConnection conn) {
/* 66 */     String id = Long.toString(COUNTER.getAndIncrement());
/* 67 */     return new HttpPoolEntry(this.log, id, route, conn, this.timeToLive, this.timeUnit);
/*    */   }
/*    */   
/*    */   static class InternalConnFactory
/*    */     implements ConnFactory<HttpRoute, OperatedClientConnection> {
/*    */     private final ClientConnectionOperator connOperator;
/*    */     
/*    */     InternalConnFactory(ClientConnectionOperator connOperator) {
/* 75 */       this.connOperator = connOperator;
/*    */     }
/*    */ 
/*    */     
/*    */     public OperatedClientConnection create(HttpRoute route) throws IOException {
/* 80 */       return this.connOperator.createConnection();
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\HttpConnPool.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */