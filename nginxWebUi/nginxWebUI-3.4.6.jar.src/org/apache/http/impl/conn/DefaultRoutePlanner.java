/*     */ package org.apache.http.impl.conn;
/*     */ 
/*     */ import java.net.InetAddress;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.ProtocolException;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.client.config.RequestConfig;
/*     */ import org.apache.http.client.protocol.HttpClientContext;
/*     */ import org.apache.http.conn.SchemePortResolver;
/*     */ import org.apache.http.conn.UnsupportedSchemeException;
/*     */ import org.apache.http.conn.routing.HttpRoute;
/*     */ import org.apache.http.conn.routing.HttpRoutePlanner;
/*     */ import org.apache.http.protocol.HttpContext;
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
/*     */ public class DefaultRoutePlanner
/*     */   implements HttpRoutePlanner
/*     */ {
/*     */   private final SchemePortResolver schemePortResolver;
/*     */   
/*     */   public DefaultRoutePlanner(SchemePortResolver schemePortResolver) {
/*  60 */     this.schemePortResolver = (schemePortResolver != null) ? schemePortResolver : DefaultSchemePortResolver.INSTANCE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpRoute determineRoute(HttpHost host, HttpRequest request, HttpContext context) throws HttpException {
/*     */     HttpHost httpHost1;
/*  69 */     Args.notNull(request, "Request");
/*  70 */     if (host == null) {
/*  71 */       throw new ProtocolException("Target host is not specified");
/*     */     }
/*  73 */     HttpClientContext clientContext = HttpClientContext.adapt(context);
/*  74 */     RequestConfig config = clientContext.getRequestConfig();
/*  75 */     InetAddress local = config.getLocalAddress();
/*  76 */     HttpHost proxy = config.getProxy();
/*  77 */     if (proxy == null) {
/*  78 */       proxy = determineProxy(host, request, context);
/*     */     }
/*     */ 
/*     */     
/*  82 */     if (host.getPort() <= 0) {
/*     */       try {
/*  84 */         httpHost1 = new HttpHost(host.getHostName(), this.schemePortResolver.resolve(host), host.getSchemeName());
/*     */ 
/*     */       
/*     */       }
/*  88 */       catch (UnsupportedSchemeException ex) {
/*  89 */         throw new HttpException(ex.getMessage());
/*     */       } 
/*     */     } else {
/*  92 */       httpHost1 = host;
/*     */     } 
/*  94 */     boolean secure = httpHost1.getSchemeName().equalsIgnoreCase("https");
/*  95 */     return (proxy == null) ? new HttpRoute(httpHost1, local, secure) : new HttpRoute(httpHost1, local, proxy, secure);
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
/*     */   protected HttpHost determineProxy(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
/* 109 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\DefaultRoutePlanner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */