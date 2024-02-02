/*     */ package org.apache.http.impl.conn;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.HttpClientConnection;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.conn.ClientConnectionManager;
/*     */ import org.apache.http.conn.ClientConnectionOperator;
/*     */ import org.apache.http.conn.ClientConnectionRequest;
/*     */ import org.apache.http.conn.ManagedClientConnection;
/*     */ import org.apache.http.conn.OperatedClientConnection;
/*     */ import org.apache.http.conn.routing.HttpRoute;
/*     */ import org.apache.http.conn.scheme.SchemeRegistry;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.Asserts;
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
/*     */ @Contract(threading = ThreadingBehavior.SAFE)
/*     */ public class BasicClientConnectionManager
/*     */   implements ClientConnectionManager
/*     */ {
/*  73 */   private final Log log = LogFactory.getLog(getClass());
/*     */   
/*  75 */   private static final AtomicLong COUNTER = new AtomicLong();
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String MISUSE_MESSAGE = "Invalid use of BasicClientConnManager: connection still allocated.\nMake sure to release the connection before allocating another one.";
/*     */ 
/*     */ 
/*     */   
/*     */   private final SchemeRegistry schemeRegistry;
/*     */ 
/*     */ 
/*     */   
/*     */   private final ClientConnectionOperator connOperator;
/*     */ 
/*     */ 
/*     */   
/*     */   private HttpPoolEntry poolEntry;
/*     */ 
/*     */ 
/*     */   
/*     */   private ManagedClientConnectionImpl conn;
/*     */ 
/*     */   
/*     */   private volatile boolean shutdown;
/*     */ 
/*     */ 
/*     */   
/*     */   public BasicClientConnectionManager(SchemeRegistry schreg) {
/* 103 */     Args.notNull(schreg, "Scheme registry");
/* 104 */     this.schemeRegistry = schreg;
/* 105 */     this.connOperator = createConnectionOperator(schreg);
/*     */   }
/*     */   
/*     */   public BasicClientConnectionManager() {
/* 109 */     this(SchemeRegistryFactory.createDefault());
/*     */   }
/*     */ 
/*     */   
/*     */   protected void finalize() throws Throwable {
/*     */     try {
/* 115 */       shutdown();
/*     */     } finally {
/* 117 */       super.finalize();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public SchemeRegistry getSchemeRegistry() {
/* 123 */     return this.schemeRegistry;
/*     */   }
/*     */   
/*     */   protected ClientConnectionOperator createConnectionOperator(SchemeRegistry schreg) {
/* 127 */     return new DefaultClientConnectionOperator(schreg);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ClientConnectionRequest requestConnection(final HttpRoute route, final Object state) {
/* 135 */     return new ClientConnectionRequest()
/*     */       {
/*     */         public void abortRequest() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         public ManagedClientConnection getConnection(long timeout, TimeUnit timeUnit) {
/* 145 */           return BasicClientConnectionManager.this.getConnection(route, state);
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void assertNotShutdown() {
/* 153 */     Asserts.check(!this.shutdown, "Connection manager has been shut down");
/*     */   }
/*     */   
/*     */   ManagedClientConnection getConnection(HttpRoute route, Object state) {
/* 157 */     Args.notNull(route, "Route");
/* 158 */     synchronized (this) {
/* 159 */       assertNotShutdown();
/* 160 */       if (this.log.isDebugEnabled()) {
/* 161 */         this.log.debug("Get connection for route " + route);
/*     */       }
/* 163 */       Asserts.check((this.conn == null), "Invalid use of BasicClientConnManager: connection still allocated.\nMake sure to release the connection before allocating another one.");
/* 164 */       if (this.poolEntry != null && !this.poolEntry.getPlannedRoute().equals(route)) {
/* 165 */         this.poolEntry.close();
/* 166 */         this.poolEntry = null;
/*     */       } 
/* 168 */       if (this.poolEntry == null) {
/* 169 */         String id = Long.toString(COUNTER.getAndIncrement());
/* 170 */         OperatedClientConnection opconn = this.connOperator.createConnection();
/* 171 */         this.poolEntry = new HttpPoolEntry(this.log, id, route, opconn, 0L, TimeUnit.MILLISECONDS);
/*     */       } 
/* 173 */       long now = System.currentTimeMillis();
/* 174 */       if (this.poolEntry.isExpired(now)) {
/* 175 */         this.poolEntry.close();
/* 176 */         this.poolEntry.getTracker().reset();
/*     */       } 
/* 178 */       this.conn = new ManagedClientConnectionImpl(this, this.connOperator, this.poolEntry);
/* 179 */       return this.conn;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void shutdownConnection(HttpClientConnection conn) {
/*     */     try {
/* 185 */       conn.shutdown();
/* 186 */     } catch (IOException iox) {
/* 187 */       if (this.log.isDebugEnabled()) {
/* 188 */         this.log.debug("I/O exception shutting down connection", iox);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void releaseConnection(ManagedClientConnection conn, long keepalive, TimeUnit timeUnit) {
/* 195 */     Args.check(conn instanceof ManagedClientConnectionImpl, "Connection class mismatch, connection not obtained from this manager");
/*     */     
/* 197 */     ManagedClientConnectionImpl managedConn = (ManagedClientConnectionImpl)conn;
/* 198 */     synchronized (managedConn) {
/* 199 */       if (this.log.isDebugEnabled()) {
/* 200 */         this.log.debug("Releasing connection " + conn);
/*     */       }
/* 202 */       if (managedConn.getPoolEntry() == null) {
/*     */         return;
/*     */       }
/* 205 */       ClientConnectionManager manager = managedConn.getManager();
/* 206 */       Asserts.check((manager == this), "Connection not obtained from this manager");
/* 207 */       synchronized (this) {
/* 208 */         if (this.shutdown) {
/* 209 */           shutdownConnection((HttpClientConnection)managedConn);
/*     */           return;
/*     */         } 
/*     */         try {
/* 213 */           if (managedConn.isOpen() && !managedConn.isMarkedReusable()) {
/* 214 */             shutdownConnection((HttpClientConnection)managedConn);
/*     */           }
/* 216 */           if (managedConn.isMarkedReusable()) {
/* 217 */             this.poolEntry.updateExpiry(keepalive, (timeUnit != null) ? timeUnit : TimeUnit.MILLISECONDS);
/* 218 */             if (this.log.isDebugEnabled()) {
/*     */               String s;
/* 220 */               if (keepalive > 0L) {
/* 221 */                 s = "for " + keepalive + " " + timeUnit;
/*     */               } else {
/* 223 */                 s = "indefinitely";
/*     */               } 
/* 225 */               this.log.debug("Connection can be kept alive " + s);
/*     */             } 
/*     */           } 
/*     */         } finally {
/* 229 */           managedConn.detach();
/* 230 */           this.conn = null;
/* 231 */           if (this.poolEntry.isClosed()) {
/* 232 */             this.poolEntry = null;
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void closeExpiredConnections() {
/* 241 */     synchronized (this) {
/* 242 */       assertNotShutdown();
/* 243 */       long now = System.currentTimeMillis();
/* 244 */       if (this.poolEntry != null && this.poolEntry.isExpired(now)) {
/* 245 */         this.poolEntry.close();
/* 246 */         this.poolEntry.getTracker().reset();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void closeIdleConnections(long idletime, TimeUnit timeUnit) {
/* 253 */     Args.notNull(timeUnit, "Time unit");
/* 254 */     synchronized (this) {
/* 255 */       assertNotShutdown();
/* 256 */       long time = timeUnit.toMillis(idletime);
/* 257 */       if (time < 0L) {
/* 258 */         time = 0L;
/*     */       }
/* 260 */       long deadline = System.currentTimeMillis() - time;
/* 261 */       if (this.poolEntry != null && this.poolEntry.getUpdated() <= deadline) {
/* 262 */         this.poolEntry.close();
/* 263 */         this.poolEntry.getTracker().reset();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void shutdown() {
/* 270 */     synchronized (this) {
/* 271 */       this.shutdown = true;
/*     */       try {
/* 273 */         if (this.poolEntry != null) {
/* 274 */           this.poolEntry.close();
/*     */         }
/*     */       } finally {
/* 277 */         this.poolEntry = null;
/* 278 */         this.conn = null;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\BasicClientConnectionManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */