/*     */ package org.apache.http.impl.cookie;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.apache.http.FormattedHeader;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HeaderElement;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.Obsolete;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.cookie.CommonCookieAttributeHandler;
/*     */ import org.apache.http.cookie.Cookie;
/*     */ import org.apache.http.cookie.CookieOrigin;
/*     */ import org.apache.http.cookie.MalformedCookieException;
/*     */ import org.apache.http.message.BufferedHeader;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ @Obsolete
/*     */ @Contract(threading = ThreadingBehavior.SAFE)
/*     */ public class NetscapeDraftSpec
/*     */   extends CookieSpecBase
/*     */ {
/*     */   protected static final String EXPIRES_PATTERN = "EEE, dd-MMM-yy HH:mm:ss z";
/*     */   
/*     */   public NetscapeDraftSpec(String[] datepatterns) {
/*  68 */     super(new CommonCookieAttributeHandler[] { null, null, null, null, new BasicExpiresHandler((datepatterns != null) ? (String[])datepatterns.clone() : new String[1]) });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   NetscapeDraftSpec(CommonCookieAttributeHandler... handlers) {
/*  77 */     super(handlers);
/*     */   }
/*     */   
/*     */   public NetscapeDraftSpec() {
/*  81 */     this((String[])null);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Cookie> parse(Header header, CookieOrigin origin) throws MalformedCookieException {
/*     */     CharArrayBuffer buffer;
/*     */     ParserCursor cursor;
/* 111 */     Args.notNull(header, "Header");
/* 112 */     Args.notNull(origin, "Cookie origin");
/* 113 */     if (!header.getName().equalsIgnoreCase("Set-Cookie")) {
/* 114 */       throw new MalformedCookieException("Unrecognized cookie header '" + header.toString() + "'");
/*     */     }
/*     */     
/* 117 */     NetscapeDraftHeaderParser parser = NetscapeDraftHeaderParser.DEFAULT;
/*     */ 
/*     */     
/* 120 */     if (header instanceof FormattedHeader) {
/* 121 */       buffer = ((FormattedHeader)header).getBuffer();
/* 122 */       cursor = new ParserCursor(((FormattedHeader)header).getValuePos(), buffer.length());
/*     */     }
/*     */     else {
/*     */       
/* 126 */       String s = header.getValue();
/* 127 */       if (s == null) {
/* 128 */         throw new MalformedCookieException("Header value is null");
/*     */       }
/* 130 */       buffer = new CharArrayBuffer(s.length());
/* 131 */       buffer.append(s);
/* 132 */       cursor = new ParserCursor(0, buffer.length());
/*     */     } 
/* 134 */     return parse(new HeaderElement[] { parser.parseHeader(buffer, cursor) }origin);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<Header> formatCookies(List<Cookie> cookies) {
/* 139 */     Args.notEmpty(cookies, "List of cookies");
/* 140 */     CharArrayBuffer buffer = new CharArrayBuffer(20 * cookies.size());
/* 141 */     buffer.append("Cookie");
/* 142 */     buffer.append(": ");
/* 143 */     for (int i = 0; i < cookies.size(); i++) {
/* 144 */       Cookie cookie = cookies.get(i);
/* 145 */       if (i > 0) {
/* 146 */         buffer.append("; ");
/*     */       }
/* 148 */       buffer.append(cookie.getName());
/* 149 */       String s = cookie.getValue();
/* 150 */       if (s != null) {
/* 151 */         buffer.append("=");
/* 152 */         buffer.append(s);
/*     */       } 
/*     */     } 
/* 155 */     List<Header> headers = new ArrayList<Header>(1);
/* 156 */     headers.add(new BufferedHeader(buffer));
/* 157 */     return headers;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getVersion() {
/* 162 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public Header getVersionHeader() {
/* 167 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 172 */     return "netscape";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\cookie\NetscapeDraftSpec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */