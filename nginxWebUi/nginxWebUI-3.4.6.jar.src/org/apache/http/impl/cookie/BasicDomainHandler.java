/*     */ package org.apache.http.impl.cookie;
/*     */ 
/*     */ import java.util.Locale;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.conn.util.InetAddressUtils;
/*     */ import org.apache.http.cookie.ClientCookie;
/*     */ import org.apache.http.cookie.CommonCookieAttributeHandler;
/*     */ import org.apache.http.cookie.Cookie;
/*     */ import org.apache.http.cookie.CookieOrigin;
/*     */ import org.apache.http.cookie.CookieRestrictionViolationException;
/*     */ import org.apache.http.cookie.MalformedCookieException;
/*     */ import org.apache.http.cookie.SetCookie;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.TextUtils;
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
/*     */ public class BasicDomainHandler
/*     */   implements CommonCookieAttributeHandler
/*     */ {
/*     */   public void parse(SetCookie cookie, String value) throws MalformedCookieException {
/*  58 */     Args.notNull(cookie, "Cookie");
/*  59 */     if (TextUtils.isBlank(value)) {
/*  60 */       throw new MalformedCookieException("Blank or null value for domain attribute");
/*     */     }
/*     */     
/*  63 */     if (value.endsWith(".")) {
/*     */       return;
/*     */     }
/*  66 */     String domain = value;
/*  67 */     if (domain.startsWith(".")) {
/*  68 */       domain = domain.substring(1);
/*     */     }
/*  70 */     domain = domain.toLowerCase(Locale.ROOT);
/*  71 */     cookie.setDomain(domain);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
/*  77 */     Args.notNull(cookie, "Cookie");
/*  78 */     Args.notNull(origin, "Cookie origin");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  84 */     String host = origin.getHost();
/*  85 */     String domain = cookie.getDomain();
/*  86 */     if (domain == null) {
/*  87 */       throw new CookieRestrictionViolationException("Cookie 'domain' may not be null");
/*     */     }
/*  89 */     if (!host.equals(domain) && !domainMatch(domain, host)) {
/*  90 */       throw new CookieRestrictionViolationException("Illegal 'domain' attribute \"" + domain + "\". Domain of origin: \"" + host + "\"");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static boolean domainMatch(String domain, String host) {
/*  96 */     if (InetAddressUtils.isIPv4Address(host) || InetAddressUtils.isIPv6Address(host)) {
/*  97 */       return false;
/*     */     }
/*  99 */     String normalizedDomain = domain.startsWith(".") ? domain.substring(1) : domain;
/* 100 */     if (host.endsWith(normalizedDomain)) {
/* 101 */       int prefix = host.length() - normalizedDomain.length();
/*     */       
/* 103 */       if (prefix == 0) {
/* 104 */         return true;
/*     */       }
/* 106 */       if (prefix > 1 && host.charAt(prefix - 1) == '.') {
/* 107 */         return true;
/*     */       }
/*     */     } 
/* 110 */     return false;
/*     */   }
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
/* 122 */     if (domain.startsWith(".")) {
/* 123 */       domain = domain.substring(1);
/*     */     }
/* 125 */     domain = domain.toLowerCase(Locale.ROOT);
/* 126 */     if (host.equals(domain)) {
/* 127 */       return true;
/*     */     }
/* 129 */     if (cookie instanceof ClientCookie && (
/* 130 */       (ClientCookie)cookie).containsAttribute("domain")) {
/* 131 */       return domainMatch(domain, host);
/*     */     }
/*     */     
/* 134 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getAttributeName() {
/* 139 */     return "domain";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\cookie\BasicDomainHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */