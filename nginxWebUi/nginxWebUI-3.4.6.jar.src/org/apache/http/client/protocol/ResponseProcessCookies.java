/*     */ package org.apache.http.client.protocol;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.List;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HeaderIterator;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.HttpResponseInterceptor;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.client.CookieStore;
/*     */ import org.apache.http.cookie.Cookie;
/*     */ import org.apache.http.cookie.CookieOrigin;
/*     */ import org.apache.http.cookie.CookieSpec;
/*     */ import org.apache.http.cookie.MalformedCookieException;
/*     */ import org.apache.http.protocol.HttpContext;
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class ResponseProcessCookies
/*     */   implements HttpResponseInterceptor
/*     */ {
/*  60 */   private final Log log = LogFactory.getLog(getClass());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
/*  69 */     Args.notNull(response, "HTTP request");
/*  70 */     Args.notNull(context, "HTTP context");
/*     */     
/*  72 */     HttpClientContext clientContext = HttpClientContext.adapt(context);
/*     */ 
/*     */     
/*  75 */     CookieSpec cookieSpec = clientContext.getCookieSpec();
/*  76 */     if (cookieSpec == null) {
/*  77 */       this.log.debug("Cookie spec not specified in HTTP context");
/*     */       
/*     */       return;
/*     */     } 
/*  81 */     CookieStore cookieStore = clientContext.getCookieStore();
/*  82 */     if (cookieStore == null) {
/*  83 */       this.log.debug("Cookie store not specified in HTTP context");
/*     */       
/*     */       return;
/*     */     } 
/*  87 */     CookieOrigin cookieOrigin = clientContext.getCookieOrigin();
/*  88 */     if (cookieOrigin == null) {
/*  89 */       this.log.debug("Cookie origin not specified in HTTP context");
/*     */       return;
/*     */     } 
/*  92 */     HeaderIterator it = response.headerIterator("Set-Cookie");
/*  93 */     processCookies(it, cookieSpec, cookieOrigin, cookieStore);
/*     */ 
/*     */     
/*  96 */     if (cookieSpec.getVersion() > 0) {
/*     */ 
/*     */       
/*  99 */       it = response.headerIterator("Set-Cookie2");
/* 100 */       processCookies(it, cookieSpec, cookieOrigin, cookieStore);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void processCookies(HeaderIterator iterator, CookieSpec cookieSpec, CookieOrigin cookieOrigin, CookieStore cookieStore) {
/* 109 */     while (iterator.hasNext()) {
/* 110 */       Header header = iterator.nextHeader();
/*     */       try {
/* 112 */         List<Cookie> cookies = cookieSpec.parse(header, cookieOrigin);
/* 113 */         for (Cookie cookie : cookies) {
/*     */           try {
/* 115 */             cookieSpec.validate(cookie, cookieOrigin);
/* 116 */             cookieStore.addCookie(cookie);
/*     */             
/* 118 */             if (this.log.isDebugEnabled()) {
/* 119 */               this.log.debug("Cookie accepted [" + formatCooke(cookie) + "]");
/*     */             }
/* 121 */           } catch (MalformedCookieException ex) {
/* 122 */             if (this.log.isWarnEnabled()) {
/* 123 */               this.log.warn("Cookie rejected [" + formatCooke(cookie) + "] " + ex.getMessage());
/*     */             }
/*     */           }
/*     */         
/*     */         } 
/* 128 */       } catch (MalformedCookieException ex) {
/* 129 */         if (this.log.isWarnEnabled()) {
/* 130 */           this.log.warn("Invalid cookie header: \"" + header + "\". " + ex.getMessage());
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static String formatCooke(Cookie cookie) {
/* 138 */     StringBuilder buf = new StringBuilder();
/* 139 */     buf.append(cookie.getName());
/* 140 */     buf.append("=\"");
/* 141 */     String v = cookie.getValue();
/* 142 */     if (v != null) {
/* 143 */       if (v.length() > 100) {
/* 144 */         v = v.substring(0, 100) + "...";
/*     */       }
/* 146 */       buf.append(v);
/*     */     } 
/* 148 */     buf.append("\"");
/* 149 */     buf.append(", version:");
/* 150 */     buf.append(Integer.toString(cookie.getVersion()));
/* 151 */     buf.append(", domain:");
/* 152 */     buf.append(cookie.getDomain());
/* 153 */     buf.append(", path:");
/* 154 */     buf.append(cookie.getPath());
/* 155 */     buf.append(", expiry:");
/* 156 */     buf.append(cookie.getExpiryDate());
/* 157 */     return buf.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\client\protocol\ResponseProcessCookies.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */