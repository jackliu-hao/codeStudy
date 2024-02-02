/*     */ package org.apache.http.impl.cookie;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.BitSet;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Date;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.apache.http.FormattedHeader;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.cookie.CommonCookieAttributeHandler;
/*     */ import org.apache.http.cookie.Cookie;
/*     */ import org.apache.http.cookie.CookieAttributeHandler;
/*     */ import org.apache.http.cookie.CookieOrigin;
/*     */ import org.apache.http.cookie.CookiePriorityComparator;
/*     */ import org.apache.http.cookie.CookieSpec;
/*     */ import org.apache.http.cookie.MalformedCookieException;
/*     */ import org.apache.http.message.BufferedHeader;
/*     */ import org.apache.http.message.ParserCursor;
/*     */ import org.apache.http.message.TokenParser;
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
/*     */ @Contract(threading = ThreadingBehavior.SAFE)
/*     */ public class RFC6265CookieSpec
/*     */   implements CookieSpec
/*     */ {
/*     */   private static final char PARAM_DELIMITER = ';';
/*     */   private static final char COMMA_CHAR = ',';
/*     */   private static final char EQUAL_CHAR = '=';
/*     */   private static final char DQUOTE_CHAR = '"';
/*     */   private static final char ESCAPE_CHAR = '\\';
/*  75 */   private static final BitSet TOKEN_DELIMS = TokenParser.INIT_BITSET(new int[] { 61, 59 });
/*  76 */   private static final BitSet VALUE_DELIMS = TokenParser.INIT_BITSET(new int[] { 59 });
/*  77 */   private static final BitSet SPECIAL_CHARS = TokenParser.INIT_BITSET(new int[] { 32, 34, 44, 59, 92 });
/*     */   
/*     */   private final CookieAttributeHandler[] attribHandlers;
/*     */   
/*     */   private final Map<String, CookieAttributeHandler> attribHandlerMap;
/*     */   
/*     */   private final TokenParser tokenParser;
/*     */   
/*     */   protected RFC6265CookieSpec(CommonCookieAttributeHandler... handlers) {
/*  86 */     this.attribHandlers = (CookieAttributeHandler[])handlers.clone();
/*  87 */     this.attribHandlerMap = new ConcurrentHashMap<String, CookieAttributeHandler>(handlers.length);
/*  88 */     for (CommonCookieAttributeHandler handler : handlers) {
/*  89 */       this.attribHandlerMap.put(handler.getAttributeName().toLowerCase(Locale.ROOT), handler);
/*     */     }
/*  91 */     this.tokenParser = TokenParser.INSTANCE;
/*     */   }
/*     */   
/*     */   static String getDefaultPath(CookieOrigin origin) {
/*  95 */     String defaultPath = origin.getPath();
/*  96 */     int lastSlashIndex = defaultPath.lastIndexOf('/');
/*  97 */     if (lastSlashIndex >= 0) {
/*  98 */       if (lastSlashIndex == 0)
/*     */       {
/* 100 */         lastSlashIndex = 1;
/*     */       }
/* 102 */       defaultPath = defaultPath.substring(0, lastSlashIndex);
/*     */     } 
/* 104 */     return defaultPath;
/*     */   }
/*     */   
/*     */   static String getDefaultDomain(CookieOrigin origin) {
/* 108 */     return origin.getHost();
/*     */   }
/*     */   public final List<Cookie> parse(Header header, CookieOrigin origin) throws MalformedCookieException {
/*     */     CharArrayBuffer buffer;
/*     */     ParserCursor cursor;
/* 113 */     Args.notNull(header, "Header");
/* 114 */     Args.notNull(origin, "Cookie origin");
/* 115 */     if (!header.getName().equalsIgnoreCase("Set-Cookie")) {
/* 116 */       throw new MalformedCookieException("Unrecognized cookie header: '" + header.toString() + "'");
/*     */     }
/*     */ 
/*     */     
/* 120 */     if (header instanceof FormattedHeader) {
/* 121 */       buffer = ((FormattedHeader)header).getBuffer();
/* 122 */       cursor = new ParserCursor(((FormattedHeader)header).getValuePos(), buffer.length());
/*     */     } else {
/* 124 */       String s = header.getValue();
/* 125 */       if (s == null) {
/* 126 */         throw new MalformedCookieException("Header value is null");
/*     */       }
/* 128 */       buffer = new CharArrayBuffer(s.length());
/* 129 */       buffer.append(s);
/* 130 */       cursor = new ParserCursor(0, buffer.length());
/*     */     } 
/* 132 */     String name = this.tokenParser.parseToken(buffer, cursor, TOKEN_DELIMS);
/* 133 */     if (name.isEmpty()) {
/* 134 */       return Collections.emptyList();
/*     */     }
/* 136 */     if (cursor.atEnd()) {
/* 137 */       return Collections.emptyList();
/*     */     }
/* 139 */     int valueDelim = buffer.charAt(cursor.getPos());
/* 140 */     cursor.updatePos(cursor.getPos() + 1);
/* 141 */     if (valueDelim != 61) {
/* 142 */       throw new MalformedCookieException("Cookie value is invalid: '" + header.toString() + "'");
/*     */     }
/* 144 */     String value = this.tokenParser.parseValue(buffer, cursor, VALUE_DELIMS);
/* 145 */     if (!cursor.atEnd()) {
/* 146 */       cursor.updatePos(cursor.getPos() + 1);
/*     */     }
/* 148 */     BasicClientCookie cookie = new BasicClientCookie(name, value);
/* 149 */     cookie.setPath(getDefaultPath(origin));
/* 150 */     cookie.setDomain(getDefaultDomain(origin));
/* 151 */     cookie.setCreationDate(new Date());
/*     */     
/* 153 */     Map<String, String> attribMap = new LinkedHashMap<String, String>();
/* 154 */     while (!cursor.atEnd()) {
/* 155 */       String paramName = this.tokenParser.parseToken(buffer, cursor, TOKEN_DELIMS).toLowerCase(Locale.ROOT);
/*     */       
/* 157 */       String paramValue = null;
/* 158 */       if (!cursor.atEnd()) {
/* 159 */         int paramDelim = buffer.charAt(cursor.getPos());
/* 160 */         cursor.updatePos(cursor.getPos() + 1);
/* 161 */         if (paramDelim == 61) {
/* 162 */           paramValue = this.tokenParser.parseToken(buffer, cursor, VALUE_DELIMS);
/* 163 */           if (!cursor.atEnd()) {
/* 164 */             cursor.updatePos(cursor.getPos() + 1);
/*     */           }
/*     */         } 
/*     */       } 
/* 168 */       cookie.setAttribute(paramName, paramValue);
/* 169 */       attribMap.put(paramName, paramValue);
/*     */     } 
/*     */     
/* 172 */     if (attribMap.containsKey("max-age")) {
/* 173 */       attribMap.remove("expires");
/*     */     }
/*     */     
/* 176 */     for (Map.Entry<String, String> entry : attribMap.entrySet()) {
/* 177 */       String paramName = entry.getKey();
/* 178 */       String paramValue = entry.getValue();
/* 179 */       CookieAttributeHandler handler = this.attribHandlerMap.get(paramName);
/* 180 */       if (handler != null) {
/* 181 */         handler.parse(cookie, paramValue);
/*     */       }
/*     */     } 
/*     */     
/* 185 */     return (List)Collections.singletonList(cookie);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
/* 191 */     Args.notNull(cookie, "Cookie");
/* 192 */     Args.notNull(origin, "Cookie origin");
/* 193 */     for (CookieAttributeHandler handler : this.attribHandlers) {
/* 194 */       handler.validate(cookie, origin);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean match(Cookie cookie, CookieOrigin origin) {
/* 200 */     Args.notNull(cookie, "Cookie");
/* 201 */     Args.notNull(origin, "Cookie origin");
/* 202 */     for (CookieAttributeHandler handler : this.attribHandlers) {
/* 203 */       if (!handler.match(cookie, origin)) {
/* 204 */         return false;
/*     */       }
/*     */     } 
/* 207 */     return true;
/*     */   }
/*     */   
/*     */   public List<Header> formatCookies(List<Cookie> cookies) {
/*     */     List<? extends Cookie> sortedCookies;
/* 212 */     Args.notEmpty(cookies, "List of cookies");
/*     */     
/* 214 */     if (cookies.size() > 1) {
/*     */       
/* 216 */       sortedCookies = new ArrayList<Cookie>(cookies);
/* 217 */       Collections.sort(sortedCookies, (Comparator<? super Cookie>)CookiePriorityComparator.INSTANCE);
/*     */     } else {
/* 219 */       sortedCookies = cookies;
/*     */     } 
/* 221 */     CharArrayBuffer buffer = new CharArrayBuffer(20 * sortedCookies.size());
/* 222 */     buffer.append("Cookie");
/* 223 */     buffer.append(": ");
/* 224 */     for (int n = 0; n < sortedCookies.size(); n++) {
/* 225 */       Cookie cookie = sortedCookies.get(n);
/* 226 */       if (n > 0) {
/* 227 */         buffer.append(';');
/* 228 */         buffer.append(' ');
/*     */       } 
/* 230 */       buffer.append(cookie.getName());
/* 231 */       String s = cookie.getValue();
/* 232 */       if (s != null) {
/* 233 */         buffer.append('=');
/* 234 */         if (containsSpecialChar(s)) {
/* 235 */           buffer.append('"');
/* 236 */           for (int i = 0; i < s.length(); i++) {
/* 237 */             char ch = s.charAt(i);
/* 238 */             if (ch == '"' || ch == '\\') {
/* 239 */               buffer.append('\\');
/*     */             }
/* 241 */             buffer.append(ch);
/*     */           } 
/* 243 */           buffer.append('"');
/*     */         } else {
/* 245 */           buffer.append(s);
/*     */         } 
/*     */       } 
/*     */     } 
/* 249 */     List<Header> headers = new ArrayList<Header>(1);
/* 250 */     headers.add(new BufferedHeader(buffer));
/* 251 */     return headers;
/*     */   }
/*     */   
/*     */   boolean containsSpecialChar(CharSequence s) {
/* 255 */     return containsChars(s, SPECIAL_CHARS);
/*     */   }
/*     */   
/*     */   boolean containsChars(CharSequence s, BitSet chars) {
/* 259 */     for (int i = 0; i < s.length(); i++) {
/* 260 */       char ch = s.charAt(i);
/* 261 */       if (chars.get(ch)) {
/* 262 */         return true;
/*     */       }
/*     */     } 
/* 265 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public final int getVersion() {
/* 270 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public final Header getVersionHeader() {
/* 275 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\cookie\RFC6265CookieSpec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */