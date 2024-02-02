/*     */ package org.apache.http.impl.cookie;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import org.apache.http.conn.util.PublicSuffixMatcher;
/*     */ import org.apache.http.cookie.Cookie;
/*     */ import org.apache.http.cookie.CookieAttributeHandler;
/*     */ import org.apache.http.cookie.CookieOrigin;
/*     */ import org.apache.http.cookie.MalformedCookieException;
/*     */ import org.apache.http.cookie.SetCookie;
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
/*     */ @Deprecated
/*     */ public class PublicSuffixFilter
/*     */   implements CookieAttributeHandler
/*     */ {
/*     */   private final CookieAttributeHandler wrapped;
/*     */   private Collection<String> exceptions;
/*     */   private Collection<String> suffixes;
/*     */   private PublicSuffixMatcher matcher;
/*     */   
/*     */   public PublicSuffixFilter(CookieAttributeHandler wrapped) {
/*  58 */     this.wrapped = wrapped;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPublicSuffixes(Collection<String> suffixes) {
/*  68 */     this.suffixes = suffixes;
/*  69 */     this.matcher = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setExceptions(Collection<String> exceptions) {
/*  78 */     this.exceptions = exceptions;
/*  79 */     this.matcher = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean match(Cookie cookie, CookieOrigin origin) {
/*  87 */     if (isForPublicSuffix(cookie)) {
/*  88 */       return false;
/*     */     }
/*  90 */     return this.wrapped.match(cookie, origin);
/*     */   }
/*     */ 
/*     */   
/*     */   public void parse(SetCookie cookie, String value) throws MalformedCookieException {
/*  95 */     this.wrapped.parse(cookie, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
/* 100 */     this.wrapped.validate(cookie, origin);
/*     */   }
/*     */   
/*     */   private boolean isForPublicSuffix(Cookie cookie) {
/* 104 */     if (this.matcher == null) {
/* 105 */       this.matcher = new PublicSuffixMatcher(this.suffixes, this.exceptions);
/*     */     }
/* 107 */     return this.matcher.matches(cookie.getDomain());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\cookie\PublicSuffixFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */