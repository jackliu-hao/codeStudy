/*     */ package org.apache.http.impl.conn.tsccm;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.conn.ConnectionPoolTimeoutException;
/*     */ import org.apache.http.conn.OperatedClientConnection;
/*     */ import org.apache.http.conn.routing.HttpRoute;
/*     */ import org.apache.http.impl.conn.IdleConnectionHandler;
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
/*     */ @Deprecated
/*     */ public abstract class AbstractConnPool
/*     */ {
/*  86 */   private final Log log = LogFactory.getLog(getClass());
/*  87 */   protected Set<BasicPoolEntry> leasedConnections = new HashSet<BasicPoolEntry>();
/*  88 */   protected IdleConnectionHandler idleConnHandler = new IdleConnectionHandler();
/*  89 */   protected final Lock poolLock = new ReentrantLock();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int numConnections;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected volatile boolean isShutDown;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Set<BasicPoolEntryRef> issuedConnections;
/*     */ 
/*     */ 
/*     */   
/*     */   protected ReferenceQueue<Object> refQueue;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void enableConnectionGC() throws IllegalStateException {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final BasicPoolEntry getEntry(HttpRoute route, Object state, long timeout, TimeUnit timeUnit) throws ConnectionPoolTimeoutException, InterruptedException {
/* 119 */     return requestPoolEntry(route, state).getPoolEntry(timeout, timeUnit);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract PoolEntryRequest requestPoolEntry(HttpRoute paramHttpRoute, Object paramObject);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void freeEntry(BasicPoolEntry paramBasicPoolEntry, boolean paramBoolean, long paramLong, TimeUnit paramTimeUnit);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void handleReference(Reference<?> ref) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void handleLostEntry(HttpRoute paramHttpRoute);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void closeIdleConnections(long idletime, TimeUnit timeUnit) {
/* 162 */     Args.notNull(timeUnit, "Time unit");
/*     */     
/* 164 */     this.poolLock.lock();
/*     */     try {
/* 166 */       this.idleConnHandler.closeIdleConnections(timeUnit.toMillis(idletime));
/*     */     } finally {
/* 168 */       this.poolLock.unlock();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void closeExpiredConnections() {
/* 173 */     this.poolLock.lock();
/*     */     try {
/* 175 */       this.idleConnHandler.closeExpiredConnections();
/*     */     } finally {
/* 177 */       this.poolLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void deleteClosedConnections();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void shutdown() {
/* 193 */     this.poolLock.lock();
/*     */     
/*     */     try {
/* 196 */       if (this.isShutDown) {
/*     */         return;
/*     */       }
/*     */ 
/*     */       
/* 201 */       Iterator<BasicPoolEntry> iter = this.leasedConnections.iterator();
/* 202 */       while (iter.hasNext()) {
/* 203 */         BasicPoolEntry entry = iter.next();
/* 204 */         iter.remove();
/* 205 */         closeConnection(entry.getConnection());
/*     */       } 
/* 207 */       this.idleConnHandler.removeAll();
/*     */       
/* 209 */       this.isShutDown = true;
/*     */     } finally {
/*     */       
/* 212 */       this.poolLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void closeConnection(OperatedClientConnection conn) {
/* 223 */     if (conn != null)
/*     */       try {
/* 225 */         conn.close();
/* 226 */       } catch (IOException ex) {
/* 227 */         this.log.debug("I/O error closing connection", ex);
/*     */       }  
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\tsccm\AbstractConnPool.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */