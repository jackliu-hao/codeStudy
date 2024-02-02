/*     */ package org.apache.http.impl.cookie;
/*     */ 
/*     */ import java.util.Locale;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class RFC2109DomainHandler
/*     */   implements CommonCookieAttributeHandler
/*     */ {
/*     */   public void parse(SetCookie cookie, String value) throws MalformedCookieException {
/*  56 */     Args.notNull(cookie, "Cookie");
/*  57 */     if (value == null) {
/*  58 */       throw new MalformedCookieException("Missing value for domain attribute");
/*     */     }
/*  60 */     if (value.trim().isEmpty()) {
/*  61 */       throw new MalformedCookieException("Blank value for domain attribute");
/*     */     }
/*  63 */     cookie.setDomain(value);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
/*  69 */     Args.notNull(cookie, "Cookie");
/*  70 */     Args.notNull(origin, "Cookie origin");
/*  71 */     String host = origin.getHost();
/*  72 */     String domain = cookie.getDomain();
/*  73 */     if (domain == null) {
/*  74 */       throw new CookieRestrictionViolationException("Cookie domain may not be null");
/*     */     }
/*  76 */     if (!domain.equals(host)) {
/*  77 */       int dotIndex = domain.indexOf('.');
/*  78 */       if (dotIndex == -1) {
/*  79 */         throw new CookieRestrictionViolationException("Domain attribute \"" + domain + "\" does not match the host \"" + host + "\"");
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  85 */       if (!domain.startsWith(".")) {
/*  86 */         throw new CookieRestrictionViolationException("Domain attribute \"" + domain + "\" violates RFC 2109: domain must start with a dot");
/*     */       }
/*     */ 
/*     */ 
/*     */       
/*  91 */       dotIndex = domain.indexOf('.', 1);
/*  92 */       if (dotIndex < 0 || dotIndex == domain.length() - 1) {
/*  93 */         throw new CookieRestrictionViolationException("Domain attribute \"" + domain + "\" violates RFC 2109: domain must contain an embedded dot");
/*     */       }
/*     */ 
/*     */       
/*  97 */       host = host.toLowerCase(Locale.ROOT);
/*  98 */       if (!host.endsWith(domain)) {
/*  99 */         throw new CookieRestrictionViolationException("Illegal domain attribute \"" + domain + "\". Domain of origin: \"" + host + "\"");
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 104 */       String hostWithoutDomain = host.substring(0, host.length() - domain.length());
/* 105 */       if (hostWithoutDomain.indexOf('.') != -1) {
/* 106 */         throw new CookieRestrictionViolationException("Domain attribute \"" + domain + "\" violates RFC 2109: host minus domain may not contain any dots");
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean match(Cookie cookie, CookieOrigin origin) {
/* 115 */     Args.notNull(cookie, "Cookie");
/* 116 */     Args.notNull(origin, "Cookie origin");
/* 117 */     String host = origin.getHost();
/* 118 */     String domain = cookie.getDomain();
/* 119 */     if (domain == null) {
/* 120 */       return false;
/*     */     }
/* 122 */     return (host.equals(domain) || (domain.startsWith(".") && host.endsWith(domain)));
/*     */   }
/*     */ 
/*     */   
/*     */   public String getAttributeName() {
/* 127 */     return "domain";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\cookie\RFC2109DomainHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */