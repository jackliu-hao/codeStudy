/*     */ package org.apache.http.impl.client;
/*     */ 
/*     */ import java.net.ProxySelector;
/*     */ import org.apache.http.ConnectionReuseStrategy;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.conn.ClientConnectionManager;
/*     */ import org.apache.http.conn.routing.HttpRoutePlanner;
/*     */ import org.apache.http.impl.DefaultConnectionReuseStrategy;
/*     */ import org.apache.http.impl.NoConnectionReuseStrategy;
/*     */ import org.apache.http.impl.conn.PoolingClientConnectionManager;
/*     */ import org.apache.http.impl.conn.ProxySelectorRoutePlanner;
/*     */ import org.apache.http.impl.conn.SchemeRegistryFactory;
/*     */ import org.apache.http.params.HttpParams;
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
/*     */ @Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
/*     */ public class SystemDefaultHttpClient
/*     */   extends DefaultHttpClient
/*     */ {
/*     */   public SystemDefaultHttpClient(HttpParams params) {
/* 115 */     super((ClientConnectionManager)null, params);
/*     */   }
/*     */   
/*     */   public SystemDefaultHttpClient() {
/* 119 */     super((ClientConnectionManager)null, (HttpParams)null);
/*     */   }
/*     */ 
/*     */   
/*     */   protected ClientConnectionManager createClientConnectionManager() {
/* 124 */     PoolingClientConnectionManager connmgr = new PoolingClientConnectionManager(SchemeRegistryFactory.createSystemDefault());
/*     */     
/* 126 */     String s = System.getProperty("http.keepAlive", "true");
/* 127 */     if ("true".equalsIgnoreCase(s)) {
/* 128 */       s = System.getProperty("http.maxConnections", "5");
/* 129 */       int max = Integer.parseInt(s);
/* 130 */       connmgr.setDefaultMaxPerRoute(max);
/* 131 */       connmgr.setMaxTotal(2 * max);
/*     */     } 
/* 133 */     return (ClientConnectionManager)connmgr;
/*     */   }
/*     */ 
/*     */   
/*     */   protected HttpRoutePlanner createHttpRoutePlanner() {
/* 138 */     return (HttpRoutePlanner)new ProxySelectorRoutePlanner(getConnectionManager().getSchemeRegistry(), ProxySelector.getDefault());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected ConnectionReuseStrategy createConnectionReuseStrategy() {
/* 144 */     String s = System.getProperty("http.keepAlive", "true");
/* 145 */     if ("true".equalsIgnoreCase(s)) {
/* 146 */       return (ConnectionReuseStrategy)new DefaultConnectionReuseStrategy();
/*     */     }
/* 148 */     return (ConnectionReuseStrategy)new NoConnectionReuseStrategy();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\SystemDefaultHttpClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */