/*    */ package org.apache.http.impl.cookie;
/*    */ 
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.cookie.CommonCookieAttributeHandler;
/*    */ import org.apache.http.cookie.Cookie;
/*    */ import org.apache.http.cookie.CookieOrigin;
/*    */ import org.apache.http.cookie.CookieRestrictionViolationException;
/*    */ import org.apache.http.cookie.MalformedCookieException;
/*    */ import org.apache.http.cookie.SetCookie;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ public class RFC2109VersionHandler
/*    */   extends AbstractCookieAttributeHandler
/*    */   implements CommonCookieAttributeHandler
/*    */ {
/*    */   public void parse(SetCookie cookie, String value) throws MalformedCookieException {
/* 54 */     Args.notNull(cookie, "Cookie");
/* 55 */     if (value == null) {
/* 56 */       throw new MalformedCookieException("Missing value for version attribute");
/*    */     }
/* 58 */     if (value.trim().isEmpty()) {
/* 59 */       throw new MalformedCookieException("Blank value for version attribute");
/*    */     }
/*    */     try {
/* 62 */       cookie.setVersion(Integer.parseInt(value));
/* 63 */     } catch (NumberFormatException e) {
/* 64 */       throw new MalformedCookieException("Invalid version: " + e.getMessage());
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
/* 72 */     Args.notNull(cookie, "Cookie");
/* 73 */     if (cookie.getVersion() < 0) {
/* 74 */       throw new CookieRestrictionViolationException("Cookie version may not be negative");
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public String getAttributeName() {
/* 80 */     return "version";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\cookie\RFC2109VersionHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */