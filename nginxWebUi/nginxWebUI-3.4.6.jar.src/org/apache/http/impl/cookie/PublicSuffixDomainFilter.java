/*     */ package org.apache.http.impl.cookie;
/*     */ 
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.conn.util.PublicSuffixList;
/*     */ import org.apache.http.conn.util.PublicSuffixMatcher;
/*     */ import org.apache.http.cookie.CommonCookieAttributeHandler;
/*     */ import org.apache.http.cookie.Cookie;
/*     */ import org.apache.http.cookie.CookieOrigin;
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
/*     */ public class PublicSuffixDomainFilter
/*     */   implements CommonCookieAttributeHandler
/*     */ {
/*     */   private final CommonCookieAttributeHandler handler;
/*     */   private final PublicSuffixMatcher publicSuffixMatcher;
/*     */   private final Map<String, Boolean> localDomainMap;
/*     */   
/*     */   private static Map<String, Boolean> createLocalDomainMap() {
/*  62 */     ConcurrentHashMap<String, Boolean> map = new ConcurrentHashMap<String, Boolean>();
/*  63 */     map.put(".localhost.", Boolean.TRUE);
/*  64 */     map.put(".test.", Boolean.TRUE);
/*  65 */     map.put(".local.", Boolean.TRUE);
/*  66 */     map.put(".local", Boolean.TRUE);
/*  67 */     map.put(".localdomain", Boolean.TRUE);
/*  68 */     return map;
/*     */   }
/*     */ 
/*     */   
/*     */   public PublicSuffixDomainFilter(CommonCookieAttributeHandler handler, PublicSuffixMatcher publicSuffixMatcher) {
/*  73 */     this.handler = (CommonCookieAttributeHandler)Args.notNull(handler, "Cookie handler");
/*  74 */     this.publicSuffixMatcher = (PublicSuffixMatcher)Args.notNull(publicSuffixMatcher, "Public suffix matcher");
/*  75 */     this.localDomainMap = createLocalDomainMap();
/*     */   }
/*     */ 
/*     */   
/*     */   public PublicSuffixDomainFilter(CommonCookieAttributeHandler handler, PublicSuffixList suffixList) {
/*  80 */     Args.notNull(handler, "Cookie handler");
/*  81 */     Args.notNull(suffixList, "Public suffix list");
/*  82 */     this.handler = handler;
/*  83 */     this.publicSuffixMatcher = new PublicSuffixMatcher(suffixList.getRules(), suffixList.getExceptions());
/*  84 */     this.localDomainMap = createLocalDomainMap();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean match(Cookie cookie, CookieOrigin origin) {
/*  92 */     String host = cookie.getDomain();
/*  93 */     if (host == null) {
/*  94 */       return false;
/*     */     }
/*  96 */     int i = host.indexOf('.');
/*  97 */     if (i >= 0) {
/*  98 */       String domain = host.substring(i);
/*  99 */       if (!this.localDomainMap.containsKey(domain) && 
/* 100 */         this.publicSuffixMatcher.matches(host)) {
/* 101 */         return false;
/*     */       
/*     */       }
/*     */     }
/* 105 */     else if (!host.equalsIgnoreCase(origin.getHost()) && 
/* 106 */       this.publicSuffixMatcher.matches(host)) {
/* 107 */       return false;
/*     */     } 
/*     */ 
/*     */     
/* 111 */     return this.handler.match(cookie, origin);
/*     */   }
/*     */ 
/*     */   
/*     */   public void parse(SetCookie cookie, String value) throws MalformedCookieException {
/* 116 */     this.handler.parse(cookie, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
/* 121 */     this.handler.validate(cookie, origin);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getAttributeName() {
/* 126 */     return this.handler.getAttributeName();
/*     */   }
/*     */ 
/*     */   
/*     */   public static CommonCookieAttributeHandler decorate(CommonCookieAttributeHandler handler, PublicSuffixMatcher publicSuffixMatcher) {
/* 131 */     Args.notNull(handler, "Cookie attribute handler");
/* 132 */     return (publicSuffixMatcher != null) ? new PublicSuffixDomainFilter(handler, publicSuffixMatcher) : handler;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\cookie\PublicSuffixDomainFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */