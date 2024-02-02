/*    */ package org.apache.http.impl.conn;
/*    */ 
/*    */ import org.apache.http.HttpException;
/*    */ import org.apache.http.HttpHost;
/*    */ import org.apache.http.HttpRequest;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.conn.SchemePortResolver;
/*    */ import org.apache.http.protocol.HttpContext;
/*    */ import org.apache.http.util.Args;
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
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
/*    */ public class DefaultProxyRoutePlanner
/*    */   extends DefaultRoutePlanner
/*    */ {
/*    */   private final HttpHost proxy;
/*    */   
/*    */   public DefaultProxyRoutePlanner(HttpHost proxy, SchemePortResolver schemePortResolver) {
/* 51 */     super(schemePortResolver);
/* 52 */     this.proxy = (HttpHost)Args.notNull(proxy, "Proxy host");
/*    */   }
/*    */   
/*    */   public DefaultProxyRoutePlanner(HttpHost proxy) {
/* 56 */     this(proxy, null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected HttpHost determineProxy(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
/* 64 */     return this.proxy;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\DefaultProxyRoutePlanner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */