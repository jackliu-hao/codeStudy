/*    */ package org.apache.http.impl.conn;
/*    */ 
/*    */ import org.apache.http.HttpHost;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.conn.SchemePortResolver;
/*    */ import org.apache.http.conn.UnsupportedSchemeException;
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
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ public class DefaultSchemePortResolver
/*    */   implements SchemePortResolver
/*    */ {
/* 44 */   public static final DefaultSchemePortResolver INSTANCE = new DefaultSchemePortResolver();
/*    */ 
/*    */   
/*    */   public int resolve(HttpHost host) throws UnsupportedSchemeException {
/* 48 */     Args.notNull(host, "HTTP host");
/* 49 */     int port = host.getPort();
/* 50 */     if (port > 0) {
/* 51 */       return port;
/*    */     }
/* 53 */     String name = host.getSchemeName();
/* 54 */     if (name.equalsIgnoreCase("http"))
/* 55 */       return 80; 
/* 56 */     if (name.equalsIgnoreCase("https")) {
/* 57 */       return 443;
/*    */     }
/* 59 */     throw new UnsupportedSchemeException(name + " protocol is not supported");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\DefaultSchemePortResolver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */