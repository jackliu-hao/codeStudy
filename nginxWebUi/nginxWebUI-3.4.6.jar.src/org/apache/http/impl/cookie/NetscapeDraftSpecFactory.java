/*    */ package org.apache.http.impl.cookie;
/*    */ 
/*    */ import java.util.Collection;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.cookie.CookieSpec;
/*    */ import org.apache.http.cookie.CookieSpecFactory;
/*    */ import org.apache.http.cookie.CookieSpecProvider;
/*    */ import org.apache.http.params.HttpParams;
/*    */ import org.apache.http.protocol.HttpContext;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Deprecated
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ public class NetscapeDraftSpecFactory
/*    */   implements CookieSpecFactory, CookieSpecProvider
/*    */ {
/*    */   private final CookieSpec cookieSpec;
/*    */   
/*    */   public NetscapeDraftSpecFactory(String[] datepatterns) {
/* 58 */     this.cookieSpec = new NetscapeDraftSpec(datepatterns);
/*    */   }
/*    */   
/*    */   public NetscapeDraftSpecFactory() {
/* 62 */     this(null);
/*    */   }
/*    */ 
/*    */   
/*    */   public CookieSpec newInstance(HttpParams params) {
/* 67 */     if (params != null) {
/*    */       
/* 69 */       String[] patterns = null;
/* 70 */       Collection<?> param = (Collection)params.getParameter("http.protocol.cookie-datepatterns");
/*    */       
/* 72 */       if (param != null) {
/* 73 */         patterns = new String[param.size()];
/* 74 */         patterns = param.<String>toArray(patterns);
/*    */       } 
/* 76 */       return new NetscapeDraftSpec(patterns);
/*    */     } 
/* 78 */     return new NetscapeDraftSpec();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public CookieSpec create(HttpContext context) {
/* 84 */     return this.cookieSpec;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\cookie\NetscapeDraftSpecFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */