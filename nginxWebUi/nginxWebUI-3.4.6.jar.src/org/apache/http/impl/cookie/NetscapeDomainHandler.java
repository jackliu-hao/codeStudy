/*     */ package org.apache.http.impl.cookie;
/*     */ 
/*     */ import java.util.Locale;
/*     */ import java.util.StringTokenizer;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
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
/*     */ public class NetscapeDomainHandler
/*     */   extends BasicDomainHandler
/*     */ {
/*     */   public void parse(SetCookie cookie, String value) throws MalformedCookieException {
/*  56 */     Args.notNull(cookie, "Cookie");
/*  57 */     if (TextUtils.isBlank(value)) {
/*  58 */       throw new MalformedCookieException("Blank or null value for domain attribute");
/*     */     }
/*  60 */     cookie.setDomain(value);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
/*  66 */     String host = origin.getHost();
/*  67 */     String domain = cookie.getDomain();
/*  68 */     if (!host.equals(domain) && !BasicDomainHandler.domainMatch(domain, host)) {
/*  69 */       throw new CookieRestrictionViolationException("Illegal domain attribute \"" + domain + "\". Domain of origin: \"" + host + "\"");
/*     */     }
/*     */     
/*  72 */     if (host.contains(".")) {
/*  73 */       int domainParts = (new StringTokenizer(domain, ".")).countTokens();
/*     */       
/*  75 */       if (isSpecialDomain(domain)) {
/*  76 */         if (domainParts < 2) {
/*  77 */           throw new CookieRestrictionViolationException("Domain attribute \"" + domain + "\" violates the Netscape cookie specification for " + "special domains");
/*     */ 
/*     */         
/*     */         }
/*     */       
/*     */       }
/*  83 */       else if (domainParts < 3) {
/*  84 */         throw new CookieRestrictionViolationException("Domain attribute \"" + domain + "\" violates the Netscape cookie specification");
/*     */       } 
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
/*     */   
/*     */   private static boolean isSpecialDomain(String domain) {
/*  99 */     String ucDomain = domain.toUpperCase(Locale.ROOT);
/* 100 */     return (ucDomain.endsWith(".COM") || ucDomain.endsWith(".EDU") || ucDomain.endsWith(".NET") || ucDomain.endsWith(".GOV") || ucDomain.endsWith(".MIL") || ucDomain.endsWith(".ORG") || ucDomain.endsWith(".INT"));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean match(Cookie cookie, CookieOrigin origin) {
/* 111 */     Args.notNull(cookie, "Cookie");
/* 112 */     Args.notNull(origin, "Cookie origin");
/* 113 */     String host = origin.getHost();
/* 114 */     String domain = cookie.getDomain();
/* 115 */     if (domain == null) {
/* 116 */       return false;
/*     */     }
/* 118 */     return host.endsWith(domain);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getAttributeName() {
/* 123 */     return "domain";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\cookie\NetscapeDomainHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */