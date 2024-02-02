/*     */ package org.apache.http.impl.cookie;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import org.apache.http.HeaderElement;
/*     */ import org.apache.http.NameValuePair;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.cookie.CommonCookieAttributeHandler;
/*     */ import org.apache.http.cookie.Cookie;
/*     */ import org.apache.http.cookie.CookieAttributeHandler;
/*     */ import org.apache.http.cookie.CookieOrigin;
/*     */ import org.apache.http.cookie.MalformedCookieException;
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
/*     */ @Contract(threading = ThreadingBehavior.SAFE)
/*     */ public abstract class CookieSpecBase
/*     */   extends AbstractCookieSpec
/*     */ {
/*     */   public CookieSpecBase() {}
/*     */   
/*     */   protected CookieSpecBase(HashMap<String, CookieAttributeHandler> map) {
/*  62 */     super(map);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected CookieSpecBase(CommonCookieAttributeHandler... handlers) {
/*  69 */     super(handlers);
/*     */   }
/*     */   
/*     */   protected static String getDefaultPath(CookieOrigin origin) {
/*  73 */     String defaultPath = origin.getPath();
/*  74 */     int lastSlashIndex = defaultPath.lastIndexOf('/');
/*  75 */     if (lastSlashIndex >= 0) {
/*  76 */       if (lastSlashIndex == 0)
/*     */       {
/*  78 */         lastSlashIndex = 1;
/*     */       }
/*  80 */       defaultPath = defaultPath.substring(0, lastSlashIndex);
/*     */     } 
/*  82 */     return defaultPath;
/*     */   }
/*     */   
/*     */   protected static String getDefaultDomain(CookieOrigin origin) {
/*  86 */     return origin.getHost();
/*     */   }
/*     */ 
/*     */   
/*     */   protected List<Cookie> parse(HeaderElement[] elems, CookieOrigin origin) throws MalformedCookieException {
/*  91 */     List<Cookie> cookies = new ArrayList<Cookie>(elems.length);
/*  92 */     for (HeaderElement headerelement : elems) {
/*  93 */       String name = headerelement.getName();
/*  94 */       String value = headerelement.getValue();
/*  95 */       if (name != null && !name.isEmpty()) {
/*     */ 
/*     */ 
/*     */         
/*  99 */         BasicClientCookie cookie = new BasicClientCookie(name, value);
/* 100 */         cookie.setPath(getDefaultPath(origin));
/* 101 */         cookie.setDomain(getDefaultDomain(origin));
/*     */ 
/*     */         
/* 104 */         NameValuePair[] attribs = headerelement.getParameters();
/* 105 */         for (int j = attribs.length - 1; j >= 0; j--) {
/* 106 */           NameValuePair attrib = attribs[j];
/* 107 */           String s = attrib.getName().toLowerCase(Locale.ROOT);
/*     */           
/* 109 */           cookie.setAttribute(s, attrib.getValue());
/*     */           
/* 111 */           CookieAttributeHandler handler = findAttribHandler(s);
/* 112 */           if (handler != null) {
/* 113 */             handler.parse(cookie, attrib.getValue());
/*     */           }
/*     */         } 
/* 116 */         cookies.add(cookie);
/*     */       } 
/* 118 */     }  return cookies;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
/* 124 */     Args.notNull(cookie, "Cookie");
/* 125 */     Args.notNull(origin, "Cookie origin");
/* 126 */     for (CookieAttributeHandler handler : getAttribHandlers()) {
/* 127 */       handler.validate(cookie, origin);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean match(Cookie cookie, CookieOrigin origin) {
/* 133 */     Args.notNull(cookie, "Cookie");
/* 134 */     Args.notNull(origin, "Cookie origin");
/* 135 */     for (CookieAttributeHandler handler : getAttribHandlers()) {
/* 136 */       if (!handler.match(cookie, origin)) {
/* 137 */         return false;
/*     */       }
/*     */     } 
/* 140 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\cookie\CookieSpecBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */