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
/*    */ @Deprecated
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ public class BrowserCompatSpecFactory
/*    */   implements CookieSpecFactory, CookieSpecProvider
/*    */ {
/*    */   private final SecurityLevel securityLevel;
/*    */   private final CookieSpec cookieSpec;
/*    */   
/*    */   public enum SecurityLevel
/*    */   {
/* 55 */     SECURITYLEVEL_DEFAULT,
/* 56 */     SECURITYLEVEL_IE_MEDIUM;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public BrowserCompatSpecFactory(String[] datepatterns, SecurityLevel securityLevel) {
/* 64 */     this.securityLevel = securityLevel;
/* 65 */     this.cookieSpec = new BrowserCompatSpec(datepatterns, securityLevel);
/*    */   }
/*    */   
/*    */   public BrowserCompatSpecFactory(String[] datepatterns) {
/* 69 */     this(null, SecurityLevel.SECURITYLEVEL_DEFAULT);
/*    */   }
/*    */   
/*    */   public BrowserCompatSpecFactory() {
/* 73 */     this(null, SecurityLevel.SECURITYLEVEL_DEFAULT);
/*    */   }
/*    */ 
/*    */   
/*    */   public CookieSpec newInstance(HttpParams params) {
/* 78 */     if (params != null) {
/*    */       
/* 80 */       String[] patterns = null;
/* 81 */       Collection<?> param = (Collection)params.getParameter("http.protocol.cookie-datepatterns");
/*    */       
/* 83 */       if (param != null) {
/* 84 */         patterns = new String[param.size()];
/* 85 */         patterns = param.<String>toArray(patterns);
/*    */       } 
/* 87 */       return new BrowserCompatSpec(patterns, this.securityLevel);
/*    */     } 
/* 89 */     return new BrowserCompatSpec(null, this.securityLevel);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public CookieSpec create(HttpContext context) {
/* 95 */     return this.cookieSpec;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\cookie\BrowserCompatSpecFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */