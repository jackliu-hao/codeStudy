/*     */ package org.apache.http.impl.cookie;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import org.apache.http.FormattedHeader;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HeaderElement;
/*     */ import org.apache.http.NameValuePair;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.cookie.CommonCookieAttributeHandler;
/*     */ import org.apache.http.cookie.Cookie;
/*     */ import org.apache.http.cookie.CookieAttributeHandler;
/*     */ import org.apache.http.cookie.CookieOrigin;
/*     */ import org.apache.http.cookie.MalformedCookieException;
/*     */ import org.apache.http.message.BasicHeaderElement;
/*     */ import org.apache.http.message.BasicHeaderValueFormatter;
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
/*     */ @Deprecated
/*     */ @Contract(threading = ThreadingBehavior.SAFE)
/*     */ public class BrowserCompatSpec
/*     */   extends CookieSpecBase
/*     */ {
/*  69 */   private static final String[] DEFAULT_DATE_PATTERNS = new String[] { "EEE, dd MMM yyyy HH:mm:ss zzz", "EEE, dd-MMM-yy HH:mm:ss zzz", "EEE MMM d HH:mm:ss yyyy", "EEE, dd-MMM-yyyy HH:mm:ss z", "EEE, dd-MMM-yyyy HH-mm-ss z", "EEE, dd MMM yy HH:mm:ss z", "EEE dd-MMM-yyyy HH:mm:ss z", "EEE dd MMM yyyy HH:mm:ss z", "EEE dd-MMM-yyyy HH-mm-ss z", "EEE dd-MMM-yy HH:mm:ss z", "EEE dd MMM yy HH:mm:ss z", "EEE,dd-MMM-yy HH:mm:ss z", "EEE,dd-MMM-yyyy HH:mm:ss z", "EEE, dd-MM-yyyy HH:mm:ss z" };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BrowserCompatSpec(String[] datepatterns, BrowserCompatSpecFactory.SecurityLevel securityLevel) {
/*  88 */     super(new CommonCookieAttributeHandler[] { new BrowserCompatVersionAttributeHandler(), new BasicDomainHandler(), (securityLevel == BrowserCompatSpecFactory.SecurityLevel.SECURITYLEVEL_IE_MEDIUM) ? new BasicPathHandler() { public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {} } : new BasicPathHandler(), new BasicMaxAgeHandler(), new BasicSecureHandler(), new BasicCommentHandler(), new BasicExpiresHandler((datepatterns != null) ? (String[])datepatterns.clone() : DEFAULT_DATE_PATTERNS) });
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
/*     */   public BrowserCompatSpec(String[] datepatterns) {
/* 105 */     this(datepatterns, BrowserCompatSpecFactory.SecurityLevel.SECURITYLEVEL_DEFAULT);
/*     */   }
/*     */ 
/*     */   
/*     */   public BrowserCompatSpec() {
/* 110 */     this((String[])null, BrowserCompatSpecFactory.SecurityLevel.SECURITYLEVEL_DEFAULT);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Cookie> parse(Header header, CookieOrigin origin) throws MalformedCookieException {
/* 116 */     Args.notNull(header, "Header");
/* 117 */     Args.notNull(origin, "Cookie origin");
/* 118 */     String headername = header.getName();
/* 119 */     if (!headername.equalsIgnoreCase("Set-Cookie")) {
/* 120 */       throw new MalformedCookieException("Unrecognized cookie header '" + header.toString() + "'");
/*     */     }
/*     */     
/* 123 */     HeaderElement[] helems = header.getElements();
/* 124 */     boolean versioned = false;
/* 125 */     boolean netscape = false;
/* 126 */     for (HeaderElement helem : helems) {
/* 127 */       if (helem.getParameterByName("version") != null) {
/* 128 */         versioned = true;
/*     */       }
/* 130 */       if (helem.getParameterByName("expires") != null) {
/* 131 */         netscape = true;
/*     */       }
/*     */     } 
/* 134 */     if (netscape || !versioned) {
/*     */       CharArrayBuffer buffer;
/*     */       ParserCursor cursor;
/* 137 */       NetscapeDraftHeaderParser parser = NetscapeDraftHeaderParser.DEFAULT;
/*     */ 
/*     */       
/* 140 */       if (header instanceof FormattedHeader) {
/* 141 */         buffer = ((FormattedHeader)header).getBuffer();
/* 142 */         cursor = new ParserCursor(((FormattedHeader)header).getValuePos(), buffer.length());
/*     */       }
/*     */       else {
/*     */         
/* 146 */         String s = header.getValue();
/* 147 */         if (s == null) {
/* 148 */           throw new MalformedCookieException("Header value is null");
/*     */         }
/* 150 */         buffer = new CharArrayBuffer(s.length());
/* 151 */         buffer.append(s);
/* 152 */         cursor = new ParserCursor(0, buffer.length());
/*     */       } 
/* 154 */       HeaderElement elem = parser.parseHeader(buffer, cursor);
/* 155 */       String name = elem.getName();
/* 156 */       String value = elem.getValue();
/* 157 */       if (name == null || name.isEmpty()) {
/* 158 */         throw new MalformedCookieException("Cookie name may not be empty");
/*     */       }
/* 160 */       BasicClientCookie cookie = new BasicClientCookie(name, value);
/* 161 */       cookie.setPath(getDefaultPath(origin));
/* 162 */       cookie.setDomain(getDefaultDomain(origin));
/*     */ 
/*     */       
/* 165 */       NameValuePair[] attribs = elem.getParameters();
/* 166 */       for (int j = attribs.length - 1; j >= 0; j--) {
/* 167 */         NameValuePair attrib = attribs[j];
/* 168 */         String s = attrib.getName().toLowerCase(Locale.ROOT);
/* 169 */         cookie.setAttribute(s, attrib.getValue());
/* 170 */         CookieAttributeHandler handler = findAttribHandler(s);
/* 171 */         if (handler != null) {
/* 172 */           handler.parse(cookie, attrib.getValue());
/*     */         }
/*     */       } 
/*     */       
/* 176 */       if (netscape) {
/* 177 */         cookie.setVersion(0);
/*     */       }
/* 179 */       return (List)Collections.singletonList(cookie);
/*     */     } 
/* 181 */     return parse(helems, origin);
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean isQuoteEnclosed(String s) {
/* 186 */     return (s != null && s.startsWith("\"") && s.endsWith("\""));
/*     */   }
/*     */ 
/*     */   
/*     */   public List<Header> formatCookies(List<Cookie> cookies) {
/* 191 */     Args.notEmpty(cookies, "List of cookies");
/* 192 */     CharArrayBuffer buffer = new CharArrayBuffer(20 * cookies.size());
/* 193 */     buffer.append("Cookie");
/* 194 */     buffer.append(": ");
/* 195 */     for (int i = 0; i < cookies.size(); i++) {
/* 196 */       Cookie cookie = cookies.get(i);
/* 197 */       if (i > 0) {
/* 198 */         buffer.append("; ");
/*     */       }
/* 200 */       String cookieName = cookie.getName();
/* 201 */       String cookieValue = cookie.getValue();
/* 202 */       if (cookie.getVersion() > 0 && !isQuoteEnclosed(cookieValue)) {
/* 203 */         BasicHeaderValueFormatter.INSTANCE.formatHeaderElement(buffer, (HeaderElement)new BasicHeaderElement(cookieName, cookieValue), false);
/*     */       
/*     */       }
/*     */       else {
/*     */ 
/*     */         
/* 209 */         buffer.append(cookieName);
/* 210 */         buffer.append("=");
/* 211 */         if (cookieValue != null) {
/* 212 */           buffer.append(cookieValue);
/*     */         }
/*     */       } 
/*     */     } 
/* 216 */     List<Header> headers = new ArrayList<Header>(1);
/* 217 */     headers.add(new BufferedHeader(buffer));
/* 218 */     return headers;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getVersion() {
/* 223 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public Header getVersionHeader() {
/* 228 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 233 */     return "compatibility";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\cookie\BrowserCompatSpec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */