/*     */ package org.apache.http.impl.conn;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.conn.ClientConnectionManager;
/*     */ import org.apache.http.conn.OperatedClientConnection;
/*     */ import org.apache.http.conn.routing.HttpRoute;
/*     */ import org.apache.http.params.HttpParams;
/*     */ import org.apache.http.protocol.HttpContext;
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
/*     */ public abstract class AbstractPooledConnAdapter
/*     */   extends AbstractClientConnAdapter
/*     */ {
/*     */   protected volatile AbstractPoolEntry poolEntry;
/*     */   
/*     */   protected AbstractPooledConnAdapter(ClientConnectionManager manager, AbstractPoolEntry entry) {
/*  66 */     super(manager, entry.connection);
/*  67 */     this.poolEntry = entry;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getId() {
/*  72 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected AbstractPoolEntry getPoolEntry() {
/*  84 */     return this.poolEntry;
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
/*     */   protected void assertValid(AbstractPoolEntry entry) {
/*  96 */     if (isReleased() || entry == null) {
/*  97 */       throw new ConnectionShutdownException();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected final void assertAttached() {
/* 106 */     if (this.poolEntry == null) {
/* 107 */       throw new ConnectionShutdownException();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected synchronized void detach() {
/* 117 */     this.poolEntry = null;
/* 118 */     super.detach();
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpRoute getRoute() {
/* 123 */     AbstractPoolEntry entry = getPoolEntry();
/* 124 */     assertValid(entry);
/* 125 */     return (entry.tracker == null) ? null : entry.tracker.toRoute();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void open(HttpRoute route, HttpContext context, HttpParams params) throws IOException {
/* 132 */     AbstractPoolEntry entry = getPoolEntry();
/* 133 */     assertValid(entry);
/* 134 */     entry.open(route, context, params);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void tunnelTarget(boolean secure, HttpParams params) throws IOException {
/* 140 */     AbstractPoolEntry entry = getPoolEntry();
/* 141 */     assertValid(entry);
/* 142 */     entry.tunnelTarget(secure, params);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void tunnelProxy(HttpHost next, boolean secure, HttpParams params) throws IOException {
/* 148 */     AbstractPoolEntry entry = getPoolEntry();
/* 149 */     assertValid(entry);
/* 150 */     entry.tunnelProxy(next, secure, params);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void layerProtocol(HttpContext context, HttpParams params) throws IOException {
/* 156 */     AbstractPoolEntry entry = getPoolEntry();
/* 157 */     assertValid(entry);
/* 158 */     entry.layerProtocol(context, params);
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 163 */     AbstractPoolEntry entry = getPoolEntry();
/* 164 */     if (entry != null) {
/* 165 */       entry.shutdownEntry();
/*     */     }
/*     */     
/* 168 */     OperatedClientConnection conn = getWrappedConnection();
/* 169 */     if (conn != null) {
/* 170 */       conn.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void shutdown() throws IOException {
/* 176 */     AbstractPoolEntry entry = getPoolEntry();
/* 177 */     if (entry != null) {
/* 178 */       entry.shutdownEntry();
/*     */     }
/*     */     
/* 181 */     OperatedClientConnection conn = getWrappedConnection();
/* 182 */     if (conn != null) {
/* 183 */       conn.shutdown();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getState() {
/* 189 */     AbstractPoolEntry entry = getPoolEntry();
/* 190 */     assertValid(entry);
/* 191 */     return entry.getState();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setState(Object state) {
/* 196 */     AbstractPoolEntry entry = getPoolEntry();
/* 197 */     assertValid(entry);
/* 198 */     entry.setState(state);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\AbstractPooledConnAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */