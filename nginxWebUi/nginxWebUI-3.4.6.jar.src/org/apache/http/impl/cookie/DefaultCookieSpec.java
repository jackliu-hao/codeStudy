/*     */ package org.apache.http.impl.cookie;
/*     */ 
/*     */ import java.util.List;
/*     */ import org.apache.http.FormattedHeader;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HeaderElement;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.cookie.CommonCookieAttributeHandler;
/*     */ import org.apache.http.cookie.Cookie;
/*     */ import org.apache.http.cookie.CookieOrigin;
/*     */ import org.apache.http.cookie.CookieSpec;
/*     */ import org.apache.http.cookie.MalformedCookieException;
/*     */ import org.apache.http.message.ParserCursor;
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
/*     */ @Contract(threading = ThreadingBehavior.SAFE)
/*     */ public class DefaultCookieSpec
/*     */   implements CookieSpec
/*     */ {
/*     */   private final RFC2965Spec strict;
/*     */   private final RFC2109Spec obsoleteStrict;
/*     */   private final NetscapeDraftSpec netscapeDraft;
/*     */   
/*     */   DefaultCookieSpec(RFC2965Spec strict, RFC2109Spec obsoleteStrict, NetscapeDraftSpec netscapeDraft) {
/*  64 */     this.strict = strict;
/*  65 */     this.obsoleteStrict = obsoleteStrict;
/*  66 */     this.netscapeDraft = netscapeDraft;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultCookieSpec(String[] datepatterns, boolean oneHeader) {
/*  72 */     this.strict = new RFC2965Spec(oneHeader, new CommonCookieAttributeHandler[] { new RFC2965VersionAttributeHandler(), new BasicPathHandler(), new RFC2965DomainAttributeHandler(), new RFC2965PortAttributeHandler(), new BasicMaxAgeHandler(), new BasicSecureHandler(), new BasicCommentHandler(), new RFC2965CommentUrlAttributeHandler(), new RFC2965DiscardAttributeHandler() });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  82 */     this.obsoleteStrict = new RFC2109Spec(oneHeader, new CommonCookieAttributeHandler[] { new RFC2109VersionHandler(), new BasicPathHandler(), new RFC2109DomainHandler(), new BasicMaxAgeHandler(), new BasicSecureHandler(), new BasicCommentHandler() });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  89 */     (new CommonCookieAttributeHandler[5])[0] = new BasicDomainHandler(); (new CommonCookieAttributeHandler[5])[1] = new BasicPathHandler(); (new CommonCookieAttributeHandler[5])[2] = new BasicSecureHandler(); (new CommonCookieAttributeHandler[5])[3] = new BasicCommentHandler(); (new String[1])[0] = "EEE, dd-MMM-yy HH:mm:ss z"; this.netscapeDraft = new NetscapeDraftSpec(new CommonCookieAttributeHandler[] { null, null, null, null, new BasicExpiresHandler((datepatterns != null) ? (String[])datepatterns.clone() : new String[1]) });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultCookieSpec() {
/*  99 */     this(null, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Cookie> parse(Header header, CookieOrigin origin) throws MalformedCookieException {
/* 106 */     Args.notNull(header, "Header");
/* 107 */     Args.notNull(origin, "Cookie origin");
/* 108 */     HeaderElement[] hElems = header.getElements();
/* 109 */     boolean versioned = false;
/* 110 */     boolean netscape = false;
/* 111 */     for (HeaderElement hElem : hElems) {
/* 112 */       if (hElem.getParameterByName("version") != null) {
/* 113 */         versioned = true;
/*     */       }
/* 115 */       if (hElem.getParameterByName("expires") != null) {
/* 116 */         netscape = true;
/*     */       }
/*     */     } 
/* 119 */     if (netscape || !versioned) {
/*     */       CharArrayBuffer buffer;
/*     */       ParserCursor cursor;
/* 122 */       NetscapeDraftHeaderParser parser = NetscapeDraftHeaderParser.DEFAULT;
/*     */ 
/*     */       
/* 125 */       if (header instanceof FormattedHeader) {
/* 126 */         buffer = ((FormattedHeader)header).getBuffer();
/* 127 */         cursor = new ParserCursor(((FormattedHeader)header).getValuePos(), buffer.length());
/*     */       }
/*     */       else {
/*     */         
/* 131 */         String hValue = header.getValue();
/* 132 */         if (hValue == null) {
/* 133 */           throw new MalformedCookieException("Header value is null");
/*     */         }
/* 135 */         buffer = new CharArrayBuffer(hValue.length());
/* 136 */         buffer.append(hValue);
/* 137 */         cursor = new ParserCursor(0, buffer.length());
/*     */       } 
/* 139 */       hElems = new HeaderElement[] { parser.parseHeader(buffer, cursor) };
/* 140 */       return this.netscapeDraft.parse(hElems, origin);
/*     */     } 
/* 142 */     return "Set-Cookie2".equals(header.getName()) ? this.strict.parse(hElems, origin) : this.obsoleteStrict.parse(hElems, origin);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
/* 151 */     Args.notNull(cookie, "Cookie");
/* 152 */     Args.notNull(origin, "Cookie origin");
/* 153 */     if (cookie.getVersion() > 0) {
/* 154 */       if (cookie instanceof org.apache.http.cookie.SetCookie2) {
/* 155 */         this.strict.validate(cookie, origin);
/*     */       } else {
/* 157 */         this.obsoleteStrict.validate(cookie, origin);
/*     */       } 
/*     */     } else {
/* 160 */       this.netscapeDraft.validate(cookie, origin);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean match(Cookie cookie, CookieOrigin origin) {
/* 166 */     Args.notNull(cookie, "Cookie");
/* 167 */     Args.notNull(origin, "Cookie origin");
/* 168 */     if (cookie.getVersion() > 0) {
/* 169 */       return (cookie instanceof org.apache.http.cookie.SetCookie2) ? this.strict.match(cookie, origin) : this.obsoleteStrict.match(cookie, origin);
/*     */     }
/*     */ 
/*     */     
/* 173 */     return this.netscapeDraft.match(cookie, origin);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<Header> formatCookies(List<Cookie> cookies) {
/* 178 */     Args.notNull(cookies, "List of cookies");
/* 179 */     int version = Integer.MAX_VALUE;
/* 180 */     boolean isSetCookie2 = true;
/* 181 */     for (Cookie cookie : cookies) {
/* 182 */       if (!(cookie instanceof org.apache.http.cookie.SetCookie2)) {
/* 183 */         isSetCookie2 = false;
/*     */       }
/* 185 */       if (cookie.getVersion() < version) {
/* 186 */         version = cookie.getVersion();
/*     */       }
/*     */     } 
/* 189 */     if (version > 0) {
/* 190 */       return isSetCookie2 ? this.strict.formatCookies(cookies) : this.obsoleteStrict.formatCookies(cookies);
/*     */     }
/*     */ 
/*     */     
/* 194 */     return this.netscapeDraft.formatCookies(cookies);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getVersion() {
/* 199 */     return this.strict.getVersion();
/*     */   }
/*     */ 
/*     */   
/*     */   public Header getVersionHeader() {
/* 204 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 209 */     return "default";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\cookie\DefaultCookieSpec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */