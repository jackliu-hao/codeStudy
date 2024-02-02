/*     */ package org.apache.http.impl.cookie;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HeaderElement;
/*     */ import org.apache.http.NameValuePair;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.Obsolete;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.cookie.ClientCookie;
/*     */ import org.apache.http.cookie.CommonCookieAttributeHandler;
/*     */ import org.apache.http.cookie.Cookie;
/*     */ import org.apache.http.cookie.CookieAttributeHandler;
/*     */ import org.apache.http.cookie.CookieOrigin;
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
/*     */ 
/*     */ 
/*     */ @Obsolete
/*     */ @Contract(threading = ThreadingBehavior.SAFE)
/*     */ public class RFC2965Spec
/*     */   extends RFC2109Spec
/*     */ {
/*     */   public RFC2965Spec() {
/*  69 */     this((String[])null, false);
/*     */   }
/*     */   
/*     */   public RFC2965Spec(String[] datepatterns, boolean oneHeader) {
/*  73 */     super(oneHeader, new CommonCookieAttributeHandler[] { new RFC2965VersionAttributeHandler(), new BasicPathHandler()
/*     */           {
/*     */ 
/*     */ 
/*     */             
/*     */             public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException
/*     */             {
/*  80 */               if (!match(cookie, origin)) {
/*  81 */                 throw new CookieRestrictionViolationException("Illegal 'path' attribute \"" + cookie.getPath() + "\". Path of origin: \"" + origin.getPath() + "\"");
/*     */               }
/*     */             }
/*     */           }, new RFC2965DomainAttributeHandler(), new RFC2965PortAttributeHandler(), new BasicMaxAgeHandler(), new BasicSecureHandler(), new BasicCommentHandler(), new BasicExpiresHandler((datepatterns != null) ? (String[])datepatterns.clone() : DATE_PATTERNS), new RFC2965CommentUrlAttributeHandler(), new RFC2965DiscardAttributeHandler() });
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
/*     */   RFC2965Spec(boolean oneHeader, CommonCookieAttributeHandler... handlers) {
/* 101 */     super(oneHeader, handlers);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Cookie> parse(Header header, CookieOrigin origin) throws MalformedCookieException {
/* 108 */     Args.notNull(header, "Header");
/* 109 */     Args.notNull(origin, "Cookie origin");
/* 110 */     if (!header.getName().equalsIgnoreCase("Set-Cookie2")) {
/* 111 */       throw new MalformedCookieException("Unrecognized cookie header '" + header.toString() + "'");
/*     */     }
/*     */     
/* 114 */     HeaderElement[] elems = header.getElements();
/* 115 */     return createCookies(elems, adjustEffectiveHost(origin));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected List<Cookie> parse(HeaderElement[] elems, CookieOrigin origin) throws MalformedCookieException {
/* 122 */     return createCookies(elems, adjustEffectiveHost(origin));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private List<Cookie> createCookies(HeaderElement[] elems, CookieOrigin origin) throws MalformedCookieException {
/* 128 */     List<Cookie> cookies = new ArrayList<Cookie>(elems.length);
/* 129 */     for (HeaderElement headerelement : elems) {
/* 130 */       String name = headerelement.getName();
/* 131 */       String value = headerelement.getValue();
/* 132 */       if (name == null || name.isEmpty()) {
/* 133 */         throw new MalformedCookieException("Cookie name may not be empty");
/*     */       }
/*     */       
/* 136 */       BasicClientCookie2 cookie = new BasicClientCookie2(name, value);
/* 137 */       cookie.setPath(getDefaultPath(origin));
/* 138 */       cookie.setDomain(getDefaultDomain(origin));
/* 139 */       cookie.setPorts(new int[] { origin.getPort() });
/*     */       
/* 141 */       NameValuePair[] attribs = headerelement.getParameters();
/*     */ 
/*     */ 
/*     */       
/* 145 */       Map<String, NameValuePair> attribmap = new HashMap<String, NameValuePair>(attribs.length);
/*     */       
/* 147 */       for (int j = attribs.length - 1; j >= 0; j--) {
/* 148 */         NameValuePair param = attribs[j];
/* 149 */         attribmap.put(param.getName().toLowerCase(Locale.ROOT), param);
/*     */       } 
/* 151 */       for (Map.Entry<String, NameValuePair> entry : attribmap.entrySet()) {
/* 152 */         NameValuePair attrib = entry.getValue();
/* 153 */         String s = attrib.getName().toLowerCase(Locale.ROOT);
/*     */         
/* 155 */         cookie.setAttribute(s, attrib.getValue());
/*     */         
/* 157 */         CookieAttributeHandler handler = findAttribHandler(s);
/* 158 */         if (handler != null) {
/* 159 */           handler.parse(cookie, attrib.getValue());
/*     */         }
/*     */       } 
/* 162 */       cookies.add(cookie);
/*     */     } 
/* 164 */     return cookies;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
/* 170 */     Args.notNull(cookie, "Cookie");
/* 171 */     Args.notNull(origin, "Cookie origin");
/* 172 */     super.validate(cookie, adjustEffectiveHost(origin));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean match(Cookie cookie, CookieOrigin origin) {
/* 177 */     Args.notNull(cookie, "Cookie");
/* 178 */     Args.notNull(origin, "Cookie origin");
/* 179 */     return super.match(cookie, adjustEffectiveHost(origin));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void formatCookieAsVer(CharArrayBuffer buffer, Cookie cookie, int version) {
/* 188 */     super.formatCookieAsVer(buffer, cookie, version);
/*     */     
/* 190 */     if (cookie instanceof ClientCookie) {
/*     */       
/* 192 */       String s = ((ClientCookie)cookie).getAttribute("port");
/* 193 */       if (s != null) {
/* 194 */         buffer.append("; $Port");
/* 195 */         buffer.append("=\"");
/* 196 */         if (!s.trim().isEmpty()) {
/* 197 */           int[] ports = cookie.getPorts();
/* 198 */           if (ports != null) {
/* 199 */             int len = ports.length;
/* 200 */             for (int i = 0; i < len; i++) {
/* 201 */               if (i > 0) {
/* 202 */                 buffer.append(",");
/*     */               }
/* 204 */               buffer.append(Integer.toString(ports[i]));
/*     */             } 
/*     */           } 
/*     */         } 
/* 208 */         buffer.append("\"");
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
/*     */   
/*     */   private static CookieOrigin adjustEffectiveHost(CookieOrigin origin) {
/* 224 */     String host = origin.getHost();
/*     */ 
/*     */ 
/*     */     
/* 228 */     boolean isLocalHost = true;
/* 229 */     for (int i = 0; i < host.length(); i++) {
/* 230 */       char ch = host.charAt(i);
/* 231 */       if (ch == '.' || ch == ':') {
/* 232 */         isLocalHost = false;
/*     */         break;
/*     */       } 
/*     */     } 
/* 236 */     return isLocalHost ? new CookieOrigin(host + ".local", origin.getPort(), origin.getPath(), origin.isSecure()) : origin;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getVersion() {
/* 247 */     return 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public Header getVersionHeader() {
/* 252 */     CharArrayBuffer buffer = new CharArrayBuffer(40);
/* 253 */     buffer.append("Cookie2");
/* 254 */     buffer.append(": ");
/* 255 */     buffer.append("$Version=");
/* 256 */     buffer.append(Integer.toString(getVersion()));
/* 257 */     return (Header)new BufferedHeader(buffer);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 262 */     return "rfc2965";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\cookie\RFC2965Spec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */