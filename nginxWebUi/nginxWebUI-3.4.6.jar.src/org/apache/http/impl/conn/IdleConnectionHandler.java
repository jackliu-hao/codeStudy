/*     */ package org.apache.http.impl.conn;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.HttpConnection;
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
/*     */ public class IdleConnectionHandler
/*     */ {
/*  54 */   private final Log log = LogFactory.getLog(getClass());
/*     */ 
/*     */   
/*     */   private final Map<HttpConnection, TimeValues> connectionToTimes;
/*     */ 
/*     */ 
/*     */   
/*     */   public IdleConnectionHandler() {
/*  62 */     this.connectionToTimes = new HashMap<HttpConnection, TimeValues>();
/*     */   }
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
/*     */   public void add(HttpConnection connection, long validDuration, TimeUnit unit) {
/*  75 */     long timeAdded = System.currentTimeMillis();
/*     */     
/*  77 */     if (this.log.isDebugEnabled()) {
/*  78 */       this.log.debug("Adding connection at: " + timeAdded);
/*     */     }
/*     */     
/*  81 */     this.connectionToTimes.put(connection, new TimeValues(timeAdded, validDuration, unit));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean remove(HttpConnection connection) {
/*  93 */     TimeValues times = this.connectionToTimes.remove(connection);
/*  94 */     if (times == null) {
/*  95 */       this.log.warn("Removing a connection that never existed!");
/*  96 */       return true;
/*     */     } 
/*  98 */     return (System.currentTimeMillis() <= times.timeExpires);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeAll() {
/* 106 */     this.connectionToTimes.clear();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void closeIdleConnections(long idleTime) {
/* 117 */     long idleTimeout = System.currentTimeMillis() - idleTime;
/*     */     
/* 119 */     if (this.log.isDebugEnabled()) {
/* 120 */       this.log.debug("Checking for connections, idle timeout: " + idleTimeout);
/*     */     }
/*     */     
/* 123 */     for (Map.Entry<HttpConnection, TimeValues> entry : this.connectionToTimes.entrySet()) {
/* 124 */       HttpConnection conn = entry.getKey();
/* 125 */       TimeValues times = entry.getValue();
/* 126 */       long connectionTime = times.timeAdded;
/* 127 */       if (connectionTime <= idleTimeout) {
/* 128 */         if (this.log.isDebugEnabled()) {
/* 129 */           this.log.debug("Closing idle connection, connection time: " + connectionTime);
/*     */         }
/*     */         try {
/* 132 */           conn.close();
/* 133 */         } catch (IOException ex) {
/* 134 */           this.log.debug("I/O error closing connection", ex);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void closeExpiredConnections() {
/* 142 */     long now = System.currentTimeMillis();
/* 143 */     if (this.log.isDebugEnabled()) {
/* 144 */       this.log.debug("Checking for expired connections, now: " + now);
/*     */     }
/*     */     
/* 147 */     for (Map.Entry<HttpConnection, TimeValues> entry : this.connectionToTimes.entrySet()) {
/* 148 */       HttpConnection conn = entry.getKey();
/* 149 */       TimeValues times = entry.getValue();
/* 150 */       if (times.timeExpires <= now) {
/* 151 */         if (this.log.isDebugEnabled()) {
/* 152 */           this.log.debug("Closing connection, expired @: " + times.timeExpires);
/*     */         }
/*     */         try {
/* 155 */           conn.close();
/* 156 */         } catch (IOException ex) {
/* 157 */           this.log.debug("I/O error closing connection", ex);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class TimeValues
/*     */   {
/*     */     private final long timeAdded;
/*     */     
/*     */     private final long timeExpires;
/*     */ 
/*     */     
/*     */     TimeValues(long now, long validDuration, TimeUnit validUnit) {
/* 173 */       this.timeAdded = now;
/* 174 */       if (validDuration > 0L) {
/* 175 */         this.timeExpires = now + validUnit.toMillis(validDuration);
/*     */       } else {
/* 177 */         this.timeExpires = Long.MAX_VALUE;
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\IdleConnectionHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */