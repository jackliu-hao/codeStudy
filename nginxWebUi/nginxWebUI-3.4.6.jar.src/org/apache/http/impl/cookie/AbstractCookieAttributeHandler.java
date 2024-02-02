/*    */ package org.apache.http.impl.cookie;
/*    */ 
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.cookie.Cookie;
/*    */ import org.apache.http.cookie.CookieAttributeHandler;
/*    */ import org.apache.http.cookie.CookieOrigin;
/*    */ import org.apache.http.cookie.MalformedCookieException;
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
/*    */ public abstract class AbstractCookieAttributeHandler
/*    */   implements CookieAttributeHandler
/*    */ {
/*    */   public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {}
/*    */   
/*    */   public boolean match(Cookie cookie, CookieOrigin origin) {
/* 52 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\cookie\AbstractCookieAttributeHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */