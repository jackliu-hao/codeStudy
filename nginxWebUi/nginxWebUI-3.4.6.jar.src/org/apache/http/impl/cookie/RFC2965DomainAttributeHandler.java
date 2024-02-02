/*     */ package org.apache.http.impl.cookie;
/*     */ 
/*     */ import java.util.Locale;
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
/*     */ 
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class RFC2965DomainAttributeHandler
/*     */   implements CommonCookieAttributeHandler
/*     */ {
/*     */   public void parse(SetCookie cookie, String domain) throws MalformedCookieException {
/*  62 */     Args.notNull(cookie, "Cookie");
/*  63 */     if (domain == null) {
/*  64 */       throw new MalformedCookieException("Missing value for domain attribute");
/*     */     }
/*     */     
/*  67 */     if (domain.trim().isEmpty()) {
/*  68 */       throw new MalformedCookieException("Blank value for domain attribute");
/*     */     }
/*     */     
/*  71 */     String s = domain;
/*  72 */     s = s.toLowerCase(Locale.ROOT);
/*  73 */     if (!domain.startsWith("."))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  79 */       s = '.' + s;
/*     */     }
/*  81 */     cookie.setDomain(s);
/*     */   }
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
/*     */   public boolean domainMatch(String host, String domain) {
/* 101 */     boolean match = (host.equals(domain) || (domain.startsWith(".") && host.endsWith(domain)));
/*     */ 
/*     */     
/* 104 */     return match;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
/* 113 */     Args.notNull(cookie, "Cookie");
/* 114 */     Args.notNull(origin, "Cookie origin");
/* 115 */     String host = origin.getHost().toLowerCase(Locale.ROOT);
/* 116 */     if (cookie.getDomain() == null) {
/* 117 */       throw new CookieRestrictionViolationException("Invalid cookie state: domain not specified");
/*     */     }
/*     */     
/* 120 */     String cookieDomain = cookie.getDomain().toLowerCase(Locale.ROOT);
/*     */     
/* 122 */     if (cookie instanceof ClientCookie && ((ClientCookie)cookie).containsAttribute("domain")) {
/*     */ 
/*     */       
/* 125 */       if (!cookieDomain.startsWith(".")) {
/* 126 */         throw new CookieRestrictionViolationException("Domain attribute \"" + cookie.getDomain() + "\" violates RFC 2109: domain must start with a dot");
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 132 */       int dotIndex = cookieDomain.indexOf('.', 1);
/* 133 */       if ((dotIndex < 0 || dotIndex == cookieDomain.length() - 1) && !cookieDomain.equals(".local"))
/*     */       {
/* 135 */         throw new CookieRestrictionViolationException("Domain attribute \"" + cookie.getDomain() + "\" violates RFC 2965: the value contains no embedded dots " + "and the value is not .local");
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 142 */       if (!domainMatch(host, cookieDomain)) {
/* 143 */         throw new CookieRestrictionViolationException("Domain attribute \"" + cookie.getDomain() + "\" violates RFC 2965: effective host name does not " + "domain-match domain attribute.");
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 150 */       String effectiveHostWithoutDomain = host.substring(0, host.length() - cookieDomain.length());
/*     */       
/* 152 */       if (effectiveHostWithoutDomain.indexOf('.') != -1) {
/* 153 */         throw new CookieRestrictionViolationException("Domain attribute \"" + cookie.getDomain() + "\" violates RFC 2965: " + "effective host minus domain may not contain any dots");
/*     */ 
/*     */       
/*     */       }
/*     */ 
/*     */     
/*     */     }
/* 160 */     else if (!cookie.getDomain().equals(host)) {
/* 161 */       throw new CookieRestrictionViolationException("Illegal domain attribute: \"" + cookie.getDomain() + "\"." + "Domain of origin: \"" + host + "\"");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean match(Cookie cookie, CookieOrigin origin) {
/* 174 */     Args.notNull(cookie, "Cookie");
/* 175 */     Args.notNull(origin, "Cookie origin");
/* 176 */     String host = origin.getHost().toLowerCase(Locale.ROOT);
/* 177 */     String cookieDomain = cookie.getDomain();
/*     */ 
/*     */ 
/*     */     
/* 181 */     if (!domainMatch(host, cookieDomain)) {
/* 182 */       return false;
/*     */     }
/*     */     
/* 185 */     String effectiveHostWithoutDomain = host.substring(0, host.length() - cookieDomain.length());
/*     */     
/* 187 */     return (effectiveHostWithoutDomain.indexOf('.') == -1);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getAttributeName() {
/* 192 */     return "domain";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\cookie\RFC2965DomainAttributeHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */