/*     */ package org.apache.http.impl.conn;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.conn.ClientConnectionManager;
/*     */ import org.apache.http.conn.ClientConnectionOperator;
/*     */ import org.apache.http.conn.ClientConnectionRequest;
/*     */ import org.apache.http.conn.ManagedClientConnection;
/*     */ import org.apache.http.conn.routing.HttpRoute;
/*     */ import org.apache.http.conn.routing.RouteTracker;
/*     */ import org.apache.http.conn.scheme.SchemeRegistry;
/*     */ import org.apache.http.params.HttpParams;
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
/*     */ @Deprecated
/*     */ @Contract(threading = ThreadingBehavior.SAFE)
/*     */ public class SingleClientConnManager
/*     */   implements ClientConnectionManager
/*     */ {
/*  68 */   private final Log log = LogFactory.getLog(getClass());
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String MISUSE_MESSAGE = "Invalid use of SingleClientConnManager: connection still allocated.\nMake sure to release the connection before allocating another one.";
/*     */ 
/*     */ 
/*     */   
/*     */   protected final SchemeRegistry schemeRegistry;
/*     */ 
/*     */ 
/*     */   
/*     */   protected final ClientConnectionOperator connOperator;
/*     */ 
/*     */ 
/*     */   
/*     */   protected final boolean alwaysShutDown;
/*     */ 
/*     */ 
/*     */   
/*     */   protected volatile PoolEntry uniquePoolEntry;
/*     */ 
/*     */ 
/*     */   
/*     */   protected volatile ConnAdapter managedConn;
/*     */ 
/*     */ 
/*     */   
/*     */   protected volatile long lastReleaseTime;
/*     */ 
/*     */ 
/*     */   
/*     */   protected volatile long connectionExpiresTime;
/*     */ 
/*     */ 
/*     */   
/*     */   protected volatile boolean isShutDown;
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public SingleClientConnManager(HttpParams params, SchemeRegistry schreg) {
/* 110 */     this(schreg);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SingleClientConnManager(SchemeRegistry schreg) {
/* 118 */     Args.notNull(schreg, "Scheme registry");
/* 119 */     this.schemeRegistry = schreg;
/* 120 */     this.connOperator = createConnectionOperator(schreg);
/* 121 */     this.uniquePoolEntry = new PoolEntry();
/* 122 */     this.managedConn = null;
/* 123 */     this.lastReleaseTime = -1L;
/* 124 */     this.alwaysShutDown = false;
/* 125 */     this.isShutDown = false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SingleClientConnManager() {
/* 132 */     this(SchemeRegistryFactory.createDefault());
/*     */   }
/*     */ 
/*     */   
/*     */   protected void finalize() throws Throwable {
/*     */     try {
/* 138 */       shutdown();
/*     */     } finally {
/* 140 */       super.finalize();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public SchemeRegistry getSchemeRegistry() {
/* 146 */     return this.schemeRegistry;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ClientConnectionOperator createConnectionOperator(SchemeRegistry schreg) {
/* 163 */     return new DefaultClientConnectionOperator(schreg);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void assertStillUp() throws IllegalStateException {
/* 172 */     Asserts.check(!this.isShutDown, "Manager is shut down");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ClientConnectionRequest requestConnection(final HttpRoute route, final Object state) {
/* 180 */     return new ClientConnectionRequest()
/*     */       {
/*     */         public void abortRequest() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         public ManagedClientConnection getConnection(long timeout, TimeUnit timeUnit) {
/* 190 */           return SingleClientConnManager.this.getConnection(route, state);
/*     */         }
/*     */       };
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
/*     */   
/*     */   public ManagedClientConnection getConnection(HttpRoute route, Object state) {
/* 206 */     Args.notNull(route, "Route");
/* 207 */     assertStillUp();
/*     */     
/* 209 */     if (this.log.isDebugEnabled()) {
/* 210 */       this.log.debug("Get connection for route " + route);
/*     */     }
/*     */     
/* 213 */     synchronized (this) {
/*     */       
/* 215 */       Asserts.check((this.managedConn == null), "Invalid use of SingleClientConnManager: connection still allocated.\nMake sure to release the connection before allocating another one.");
/*     */ 
/*     */       
/* 218 */       boolean recreate = false;
/* 219 */       boolean shutdown = false;
/*     */ 
/*     */       
/* 222 */       closeExpiredConnections();
/*     */       
/* 224 */       if (this.uniquePoolEntry.connection.isOpen()) {
/* 225 */         RouteTracker tracker = this.uniquePoolEntry.tracker;
/* 226 */         shutdown = (tracker == null || !tracker.toRoute().equals(route));
/*     */ 
/*     */       
/*     */       }
/*     */       else {
/*     */ 
/*     */ 
/*     */         
/* 234 */         recreate = true;
/*     */       } 
/*     */       
/* 237 */       if (shutdown) {
/* 238 */         recreate = true;
/*     */         try {
/* 240 */           this.uniquePoolEntry.shutdown();
/* 241 */         } catch (IOException iox) {
/* 242 */           this.log.debug("Problem shutting down connection.", iox);
/*     */         } 
/*     */       } 
/*     */       
/* 246 */       if (recreate) {
/* 247 */         this.uniquePoolEntry = new PoolEntry();
/*     */       }
/*     */       
/* 250 */       this.managedConn = new ConnAdapter(this.uniquePoolEntry, route);
/*     */       
/* 252 */       return this.managedConn;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void releaseConnection(ManagedClientConnection conn, long validDuration, TimeUnit timeUnit) {
/* 260 */     Args.check(conn instanceof ConnAdapter, "Connection class mismatch, connection not obtained from this manager");
/*     */     
/* 262 */     assertStillUp();
/*     */     
/* 264 */     if (this.log.isDebugEnabled()) {
/* 265 */       this.log.debug("Releasing connection " + conn);
/*     */     }
/*     */     
/* 268 */     ConnAdapter sca = (ConnAdapter)conn;
/* 269 */     synchronized (sca) {
/* 270 */       if (sca.poolEntry == null) {
/*     */         return;
/*     */       }
/*     */       
/* 274 */       ClientConnectionManager manager = sca.getManager();
/* 275 */       Asserts.check((manager == this), "Connection not obtained from this manager");
/*     */       
/*     */       try {
/* 278 */         if (sca.isOpen() && (this.alwaysShutDown || !sca.isMarkedReusable())) {
/*     */ 
/*     */           
/* 281 */           if (this.log.isDebugEnabled()) {
/* 282 */             this.log.debug("Released connection open but not reusable.");
/*     */           }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 289 */           sca.shutdown();
/*     */         } 
/* 291 */       } catch (IOException iox) {
/* 292 */         if (this.log.isDebugEnabled()) {
/* 293 */           this.log.debug("Exception shutting down released connection.", iox);
/*     */         }
/*     */       } finally {
/*     */         
/* 297 */         sca.detach();
/* 298 */         synchronized (this) {
/* 299 */           this.managedConn = null;
/* 300 */           this.lastReleaseTime = System.currentTimeMillis();
/* 301 */           if (validDuration > 0L) {
/* 302 */             this.connectionExpiresTime = timeUnit.toMillis(validDuration) + this.lastReleaseTime;
/*     */           } else {
/* 304 */             this.connectionExpiresTime = Long.MAX_VALUE;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void closeExpiredConnections() {
/* 313 */     long time = this.connectionExpiresTime;
/* 314 */     if (System.currentTimeMillis() >= time) {
/* 315 */       closeIdleConnections(0L, TimeUnit.MILLISECONDS);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void closeIdleConnections(long idletime, TimeUnit timeUnit) {
/* 321 */     assertStillUp();
/*     */ 
/*     */     
/* 324 */     Args.notNull(timeUnit, "Time unit");
/*     */     
/* 326 */     synchronized (this) {
/* 327 */       if (this.managedConn == null && this.uniquePoolEntry.connection.isOpen()) {
/* 328 */         long cutoff = System.currentTimeMillis() - timeUnit.toMillis(idletime);
/*     */         
/* 330 */         if (this.lastReleaseTime <= cutoff) {
/*     */           try {
/* 332 */             this.uniquePoolEntry.close();
/* 333 */           } catch (IOException iox) {
/*     */             
/* 335 */             this.log.debug("Problem closing idle connection.", iox);
/*     */           } 
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void shutdown() {
/* 344 */     this.isShutDown = true;
/* 345 */     synchronized (this) {
/*     */       try {
/* 347 */         if (this.uniquePoolEntry != null) {
/* 348 */           this.uniquePoolEntry.shutdown();
/*     */         }
/* 350 */       } catch (IOException iox) {
/*     */         
/* 352 */         this.log.debug("Problem while shutting down manager.", iox);
/*     */       } finally {
/* 354 */         this.uniquePoolEntry = null;
/* 355 */         this.managedConn = null;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   protected void revokeConnection() {
/* 361 */     ConnAdapter conn = this.managedConn;
/* 362 */     if (conn == null) {
/*     */       return;
/*     */     }
/* 365 */     conn.detach();
/*     */     
/* 367 */     synchronized (this) {
/*     */       try {
/* 369 */         this.uniquePoolEntry.shutdown();
/* 370 */       } catch (IOException iox) {
/*     */         
/* 372 */         this.log.debug("Problem while shutting down connection.", iox);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected class PoolEntry
/*     */     extends AbstractPoolEntry
/*     */   {
/*     */     protected PoolEntry() {
/* 387 */       super(SingleClientConnManager.this.connOperator, null);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected void close() throws IOException {
/* 394 */       shutdownEntry();
/* 395 */       if (this.connection.isOpen()) {
/* 396 */         this.connection.close();
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected void shutdown() throws IOException {
/* 404 */       shutdownEntry();
/* 405 */       if (this.connection.isOpen()) {
/* 406 */         this.connection.shutdown();
/*     */       }
/*     */     }
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
/*     */   protected class ConnAdapter
/*     */     extends AbstractPooledConnAdapter
/*     */   {
/*     */     protected ConnAdapter(SingleClientConnManager.PoolEntry entry, HttpRoute route) {
/* 424 */       super(SingleClientConnManager.this, entry);
/* 425 */       markReusable();
/* 426 */       entry.route = route;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\SingleClientConnManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */