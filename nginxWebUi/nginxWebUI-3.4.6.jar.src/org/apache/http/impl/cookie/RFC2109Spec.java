/*     */ package org.apache.http.impl.cookie;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HeaderElement;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.Obsolete;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.cookie.ClientCookie;
/*     */ import org.apache.http.cookie.CommonCookieAttributeHandler;
/*     */ import org.apache.http.cookie.Cookie;
/*     */ import org.apache.http.cookie.CookieOrigin;
/*     */ import org.apache.http.cookie.CookiePathComparator;
/*     */ import org.apache.http.cookie.CookieRestrictionViolationException;
/*     */ import org.apache.http.cookie.MalformedCookieException;
/*     */ import org.apache.http.message.BufferedHeader;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.CharArrayBuffer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Obsolete
/*     */ @Contract(threading = ThreadingBehavior.SAFE)
/*     */ public class RFC2109Spec
/*     */   extends CookieSpecBase
/*     */ {
/*  64 */   static final String[] DATE_PATTERNS = new String[] { "EEE, dd MMM yyyy HH:mm:ss zzz", "EEE, dd-MMM-yy HH:mm:ss zzz", "EEE MMM d HH:mm:ss yyyy" };
/*     */ 
/*     */ 
/*     */   
/*     */   private final boolean oneHeader;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RFC2109Spec(String[] datepatterns, boolean oneHeader) {
/*  74 */     super(new CommonCookieAttributeHandler[] { new RFC2109VersionHandler(), new BasicPathHandler()
/*     */           {
/*     */ 
/*     */             
/*     */             public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException
/*     */             {
/*  80 */               if (!match(cookie, origin)) {
/*  81 */                 throw new CookieRestrictionViolationException("Illegal 'path' attribute \"" + cookie.getPath() + "\". Path of origin: \"" + origin.getPath() + "\"");
/*     */               }
/*     */             }
/*     */           }, new RFC2109DomainHandler(), new BasicMaxAgeHandler(), new BasicSecureHandler(), new BasicCommentHandler(), new BasicExpiresHandler((datepatterns != null) ? (String[])datepatterns.clone() : DATE_PATTERNS) });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  94 */     this.oneHeader = oneHeader;
/*     */   }
/*     */ 
/*     */   
/*     */   public RFC2109Spec() {
/*  99 */     this((String[])null, false);
/*     */   }
/*     */ 
/*     */   
/*     */   protected RFC2109Spec(boolean oneHeader, CommonCookieAttributeHandler... handlers) {
/* 104 */     super(handlers);
/* 105 */     this.oneHeader = oneHeader;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Cookie> parse(Header header, CookieOrigin origin) throws MalformedCookieException {
/* 111 */     Args.notNull(header, "Header");
/* 112 */     Args.notNull(origin, "Cookie origin");
/* 113 */     if (!header.getName().equalsIgnoreCase("Set-Cookie")) {
/* 114 */       throw new MalformedCookieException("Unrecognized cookie header '" + header.toString() + "'");
/*     */     }
/*     */     
/* 117 */     HeaderElement[] elems = header.getElements();
/* 118 */     return parse(elems, origin);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
/* 124 */     Args.notNull(cookie, "Cookie");
/* 125 */     String name = cookie.getName();
/* 126 */     if (name.indexOf(' ') != -1) {
/* 127 */       throw new CookieRestrictionViolationException("Cookie name may not contain blanks");
/*     */     }
/* 129 */     if (name.startsWith("$")) {
/* 130 */       throw new CookieRestrictionViolationException("Cookie name may not start with $");
/*     */     }
/* 132 */     super.validate(cookie, origin);
/*     */   }
/*     */   
/*     */   public List<Header> formatCookies(List<Cookie> cookies) {
/*     */     List<Cookie> cookieList;
/* 137 */     Args.notEmpty(cookies, "List of cookies");
/*     */     
/* 139 */     if (cookies.size() > 1) {
/*     */       
/* 141 */       cookieList = new ArrayList<Cookie>(cookies);
/* 142 */       Collections.sort(cookieList, (Comparator<? super Cookie>)CookiePathComparator.INSTANCE);
/*     */     } else {
/* 144 */       cookieList = cookies;
/*     */     } 
/* 146 */     return this.oneHeader ? doFormatOneHeader(cookieList) : doFormatManyHeaders(cookieList);
/*     */   }
/*     */   
/*     */   private List<Header> doFormatOneHeader(List<Cookie> cookies) {
/* 150 */     int version = Integer.MAX_VALUE;
/*     */     
/* 152 */     for (Cookie cookie : cookies) {
/* 153 */       if (cookie.getVersion() < version) {
/* 154 */         version = cookie.getVersion();
/*     */       }
/*     */     } 
/* 157 */     CharArrayBuffer buffer = new CharArrayBuffer(40 * cookies.size());
/* 158 */     buffer.append("Cookie");
/* 159 */     buffer.append(": ");
/* 160 */     buffer.append("$Version=");
/* 161 */     buffer.append(Integer.toString(version));
/* 162 */     for (Cookie cooky : cookies) {
/* 163 */       buffer.append("; ");
/* 164 */       Cookie cookie = cooky;
/* 165 */       formatCookieAsVer(buffer, cookie, version);
/*     */     } 
/* 167 */     List<Header> headers = new ArrayList<Header>(1);
/* 168 */     headers.add(new BufferedHeader(buffer));
/* 169 */     return headers;
/*     */   }
/*     */   
/*     */   private List<Header> doFormatManyHeaders(List<Cookie> cookies) {
/* 173 */     List<Header> headers = new ArrayList<Header>(cookies.size());
/* 174 */     for (Cookie cookie : cookies) {
/* 175 */       int version = cookie.getVersion();
/* 176 */       CharArrayBuffer buffer = new CharArrayBuffer(40);
/* 177 */       buffer.append("Cookie: ");
/* 178 */       buffer.append("$Version=");
/* 179 */       buffer.append(Integer.toString(version));
/* 180 */       buffer.append("; ");
/* 181 */       formatCookieAsVer(buffer, cookie, version);
/* 182 */       headers.add(new BufferedHeader(buffer));
/*     */     } 
/* 184 */     return headers;
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
/*     */   protected void formatParamAsVer(CharArrayBuffer buffer, String name, String value, int version) {
/* 198 */     buffer.append(name);
/* 199 */     buffer.append("=");
/* 200 */     if (value != null) {
/* 201 */       if (version > 0) {
/* 202 */         buffer.append('"');
/* 203 */         buffer.append(value);
/* 204 */         buffer.append('"');
/*     */       } else {
/* 206 */         buffer.append(value);
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
/*     */   protected void formatCookieAsVer(CharArrayBuffer buffer, Cookie cookie, int version) {
/* 220 */     formatParamAsVer(buffer, cookie.getName(), cookie.getValue(), version);
/* 221 */     if (cookie.getPath() != null && 
/* 222 */       cookie instanceof ClientCookie && ((ClientCookie)cookie).containsAttribute("path")) {
/*     */       
/* 224 */       buffer.append("; ");
/* 225 */       formatParamAsVer(buffer, "$Path", cookie.getPath(), version);
/*     */     } 
/*     */     
/* 228 */     if (cookie.getDomain() != null && 
/* 229 */       cookie instanceof ClientCookie && ((ClientCookie)cookie).containsAttribute("domain")) {
/*     */       
/* 231 */       buffer.append("; ");
/* 232 */       formatParamAsVer(buffer, "$Domain", cookie.getDomain(), version);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getVersion() {
/* 239 */     return 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public Header getVersionHeader() {
/* 244 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 249 */     return "rfc2109";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\cookie\RFC2109Spec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */