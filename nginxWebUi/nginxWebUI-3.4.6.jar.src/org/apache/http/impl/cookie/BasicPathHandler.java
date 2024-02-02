/*    */ package org.apache.http.impl.cookie;
/*    */ 
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.cookie.CommonCookieAttributeHandler;
/*    */ import org.apache.http.cookie.Cookie;
/*    */ import org.apache.http.cookie.CookieOrigin;
/*    */ import org.apache.http.cookie.MalformedCookieException;
/*    */ import org.apache.http.cookie.SetCookie;
/*    */ import org.apache.http.util.Args;
/*    */ import org.apache.http.util.TextUtils;
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
/*    */ public class BasicPathHandler
/*    */   implements CommonCookieAttributeHandler
/*    */ {
/*    */   public void parse(SetCookie cookie, String value) throws MalformedCookieException {
/* 54 */     Args.notNull(cookie, "Cookie");
/* 55 */     cookie.setPath(!TextUtils.isBlank(value) ? value : "/");
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {}
/*    */ 
/*    */   
/*    */   static boolean pathMatch(String uriPath, String cookiePath) {
/* 64 */     String normalizedCookiePath = cookiePath;
/* 65 */     if (normalizedCookiePath == null) {
/* 66 */       normalizedCookiePath = "/";
/*    */     }
/* 68 */     if (normalizedCookiePath.length() > 1 && normalizedCookiePath.endsWith("/")) {
/* 69 */       normalizedCookiePath = normalizedCookiePath.substring(0, normalizedCookiePath.length() - 1);
/*    */     }
/* 71 */     if (uriPath.startsWith(normalizedCookiePath)) {
/* 72 */       if (normalizedCookiePath.equals("/")) {
/* 73 */         return true;
/*    */       }
/* 75 */       if (uriPath.length() == normalizedCookiePath.length()) {
/* 76 */         return true;
/*    */       }
/* 78 */       if (uriPath.charAt(normalizedCookiePath.length()) == '/') {
/* 79 */         return true;
/*    */       }
/*    */     } 
/* 82 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean match(Cookie cookie, CookieOrigin origin) {
/* 87 */     Args.notNull(cookie, "Cookie");
/* 88 */     Args.notNull(origin, "Cookie origin");
/* 89 */     return pathMatch(origin.getPath(), cookie.getPath());
/*    */   }
/*    */ 
/*    */   
/*    */   public String getAttributeName() {
/* 94 */     return "path";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\cookie\BasicPathHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */