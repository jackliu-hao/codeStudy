/*    */ package org.apache.http.impl.cookie;
/*    */ 
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.cookie.CookieSpec;
/*    */ import org.apache.http.cookie.CookieSpecProvider;
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
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ public class IgnoreSpecProvider
/*    */   implements CookieSpecProvider
/*    */ {
/*    */   private volatile CookieSpec cookieSpec;
/*    */   
/*    */   public CookieSpec create(HttpContext context) {
/* 52 */     if (this.cookieSpec == null) {
/* 53 */       synchronized (this) {
/* 54 */         if (this.cookieSpec == null) {
/* 55 */           this.cookieSpec = new IgnoreSpec();
/*    */         }
/*    */       } 
/*    */     }
/* 59 */     return this.cookieSpec;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\cookie\IgnoreSpecProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */