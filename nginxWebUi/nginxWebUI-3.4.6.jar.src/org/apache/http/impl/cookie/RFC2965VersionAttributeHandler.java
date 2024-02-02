/*     */ package org.apache.http.impl.cookie;
/*     */ 
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.cookie.ClientCookie;
/*     */ import org.apache.http.cookie.CommonCookieAttributeHandler;
/*     */ import org.apache.http.cookie.Cookie;
/*     */ import org.apache.http.cookie.CookieOrigin;
/*     */ import org.apache.http.cookie.CookieRestrictionViolationException;
/*     */ import org.apache.http.cookie.MalformedCookieException;
/*     */ import org.apache.http.cookie.SetCookie;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class RFC2965VersionAttributeHandler
/*     */   implements CommonCookieAttributeHandler
/*     */ {
/*     */   public void parse(SetCookie cookie, String value) throws MalformedCookieException {
/*  60 */     Args.notNull(cookie, "Cookie");
/*  61 */     if (value == null) {
/*  62 */       throw new MalformedCookieException("Missing value for version attribute");
/*     */     }
/*     */     
/*  65 */     int version = -1;
/*     */     try {
/*  67 */       version = Integer.parseInt(value);
/*  68 */     } catch (NumberFormatException e) {
/*  69 */       version = -1;
/*     */     } 
/*  71 */     if (version < 0) {
/*  72 */       throw new MalformedCookieException("Invalid cookie version.");
/*     */     }
/*  74 */     cookie.setVersion(version);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
/*  83 */     Args.notNull(cookie, "Cookie");
/*  84 */     if (cookie instanceof org.apache.http.cookie.SetCookie2 && 
/*  85 */       cookie instanceof ClientCookie && !((ClientCookie)cookie).containsAttribute("version"))
/*     */     {
/*  87 */       throw new CookieRestrictionViolationException("Violates RFC 2965. Version attribute is required.");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean match(Cookie cookie, CookieOrigin origin) {
/*  95 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getAttributeName() {
/* 100 */     return "version";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\cookie\RFC2965VersionAttributeHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */