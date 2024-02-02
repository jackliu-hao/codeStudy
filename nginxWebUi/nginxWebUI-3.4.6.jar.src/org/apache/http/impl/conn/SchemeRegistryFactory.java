/*    */ package org.apache.http.impl.conn;
/*    */ 
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.conn.scheme.PlainSocketFactory;
/*    */ import org.apache.http.conn.scheme.Scheme;
/*    */ import org.apache.http.conn.scheme.SchemeRegistry;
/*    */ import org.apache.http.conn.scheme.SchemeSocketFactory;
/*    */ import org.apache.http.conn.ssl.SSLSocketFactory;
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
/*    */ 
/*    */ 
/*    */ @Deprecated
/*    */ @Contract(threading = ThreadingBehavior.SAFE)
/*    */ public final class SchemeRegistryFactory
/*    */ {
/*    */   public static SchemeRegistry createDefault() {
/* 50 */     SchemeRegistry registry = new SchemeRegistry();
/* 51 */     registry.register(new Scheme("http", 80, (SchemeSocketFactory)PlainSocketFactory.getSocketFactory()));
/*    */     
/* 53 */     registry.register(new Scheme("https", 443, (SchemeSocketFactory)SSLSocketFactory.getSocketFactory()));
/*    */     
/* 55 */     return registry;
/*    */   }
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
/*    */   public static SchemeRegistry createSystemDefault() {
/* 83 */     SchemeRegistry registry = new SchemeRegistry();
/* 84 */     registry.register(new Scheme("http", 80, (SchemeSocketFactory)PlainSocketFactory.getSocketFactory()));
/*    */     
/* 86 */     registry.register(new Scheme("https", 443, (SchemeSocketFactory)SSLSocketFactory.getSystemSocketFactory()));
/*    */     
/* 88 */     return registry;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\SchemeRegistryFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */