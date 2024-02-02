/*     */ package io.undertow.util;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.server.handlers.Cookie;
/*     */ import io.undertow.server.handlers.CookieImpl;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Cookies
/*     */ {
/*     */   public static final String DOMAIN = "$Domain";
/*     */   public static final String VERSION = "$Version";
/*     */   public static final String PATH = "$Path";
/*     */   
/*     */   public static Cookie parseSetCookieHeader(String headerValue) {
/*  76 */     String key = null;
/*  77 */     CookieImpl cookie = null;
/*  78 */     int state = 0;
/*  79 */     int current = 0;
/*  80 */     for (int i = 0; i < headerValue.length(); i++) {
/*  81 */       char c = headerValue.charAt(i);
/*  82 */       switch (state) {
/*     */         
/*     */         case 0:
/*  85 */           if (c == '=') {
/*  86 */             key = headerValue.substring(current, i);
/*  87 */             current = i + 1;
/*  88 */             state = 1; break;
/*  89 */           }  if ((c == ';' || c == ' ') && current == i) {
/*  90 */             current++; break;
/*  91 */           }  if (c == ';') {
/*  92 */             if (cookie == null) {
/*  93 */               throw UndertowMessages.MESSAGES.couldNotParseCookie(headerValue);
/*     */             }
/*  95 */             handleValue(cookie, headerValue.substring(current, i), null);
/*     */             
/*  97 */             current = i + 1;
/*     */           } 
/*     */           break;
/*     */         
/*     */         case 1:
/* 102 */           if (c == ';') {
/* 103 */             if (cookie == null) {
/* 104 */               cookie = new CookieImpl(key, headerValue.substring(current, i));
/*     */             } else {
/* 106 */               handleValue(cookie, key, headerValue.substring(current, i));
/*     */             } 
/* 108 */             state = 0;
/* 109 */             current = i + 1;
/* 110 */             key = null; break;
/* 111 */           }  if (c == '"' && current == i) {
/* 112 */             current++;
/* 113 */             state = 2;
/*     */           } 
/*     */           break;
/*     */         
/*     */         case 2:
/* 118 */           if (c == '"') {
/* 119 */             if (cookie == null) {
/* 120 */               cookie = new CookieImpl(key, headerValue.substring(current, i));
/*     */             } else {
/* 122 */               handleValue(cookie, key, headerValue.substring(current, i));
/*     */             } 
/* 124 */             state = 0;
/* 125 */             current = i + 1;
/* 126 */             key = null;
/*     */           } 
/*     */           break;
/*     */       } 
/*     */     
/*     */     } 
/* 132 */     if (key == null) {
/* 133 */       if (current != headerValue.length()) {
/* 134 */         handleValue(cookie, headerValue.substring(current, headerValue.length()), null);
/*     */       }
/*     */     }
/* 137 */     else if (current != headerValue.length()) {
/* 138 */       if (cookie == null) {
/* 139 */         cookie = new CookieImpl(key, headerValue.substring(current, headerValue.length()));
/*     */       } else {
/* 141 */         handleValue(cookie, key, headerValue.substring(current, headerValue.length()));
/*     */       } 
/*     */     } else {
/* 144 */       handleValue(cookie, key, null);
/*     */     } 
/*     */ 
/*     */     
/* 148 */     return (Cookie)cookie;
/*     */   }
/*     */   
/*     */   private static void handleValue(CookieImpl cookie, String key, String value) {
/* 152 */     if (key == null) {
/*     */       return;
/*     */     }
/* 155 */     if (key.equalsIgnoreCase("path")) {
/* 156 */       cookie.setPath(value);
/* 157 */     } else if (key.equalsIgnoreCase("domain")) {
/* 158 */       cookie.setDomain(value);
/* 159 */     } else if (key.equalsIgnoreCase("max-age")) {
/* 160 */       cookie.setMaxAge(Integer.valueOf(Integer.parseInt(value)));
/* 161 */     } else if (key.equalsIgnoreCase("expires")) {
/* 162 */       cookie.setExpires(DateUtils.parseDate(value));
/* 163 */     } else if (key.equalsIgnoreCase("discard")) {
/* 164 */       cookie.setDiscard(true);
/* 165 */     } else if (key.equalsIgnoreCase("secure")) {
/* 166 */       cookie.setSecure(true);
/* 167 */     } else if (key.equalsIgnoreCase("httpOnly")) {
/* 168 */       cookie.setHttpOnly(true);
/* 169 */     } else if (key.equalsIgnoreCase("version")) {
/* 170 */       cookie.setVersion(Integer.parseInt(value));
/* 171 */     } else if (key.equalsIgnoreCase("comment")) {
/* 172 */       cookie.setComment(value);
/* 173 */     } else if (key.equalsIgnoreCase("samesite")) {
/* 174 */       cookie.setSameSite(true);
/* 175 */       cookie.setSameSiteMode(value);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static Map<String, Cookie> parseRequestCookies(int maxCookies, boolean allowEqualInValue, List<String> cookies) {
/* 209 */     return parseRequestCookies(maxCookies, allowEqualInValue, cookies, LegacyCookieSupport.COMMA_IS_SEPARATOR);
/*     */   }
/*     */   
/*     */   public static void parseRequestCookies(int maxCookies, boolean allowEqualInValue, List<String> cookies, Set<Cookie> parsedCookies) {
/* 213 */     parseRequestCookies(maxCookies, allowEqualInValue, cookies, parsedCookies, LegacyCookieSupport.COMMA_IS_SEPARATOR);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   static Map<String, Cookie> parseRequestCookies(int maxCookies, boolean allowEqualInValue, List<String> cookies, boolean commaIsSeperator) {
/* 218 */     return parseRequestCookies(maxCookies, allowEqualInValue, cookies, commaIsSeperator, LegacyCookieSupport.ALLOW_HTTP_SEPARATORS_IN_V0);
/*     */   }
/*     */   
/*     */   static void parseRequestCookies(int maxCookies, boolean allowEqualInValue, List<String> cookies, Set<Cookie> parsedCookies, boolean commaIsSeperator) {
/* 222 */     parseRequestCookies(maxCookies, allowEqualInValue, cookies, parsedCookies, commaIsSeperator, LegacyCookieSupport.ALLOW_HTTP_SEPARATORS_IN_V0);
/*     */   }
/*     */   
/*     */   static Map<String, Cookie> parseRequestCookies(int maxCookies, boolean allowEqualInValue, List<String> cookies, boolean commaIsSeperator, boolean allowHttpSepartorsV0) {
/* 226 */     if (cookies == null) {
/* 227 */       return new TreeMap<>();
/*     */     }
/* 229 */     Set<Cookie> parsedCookies = new HashSet<>();
/* 230 */     for (String cookie : cookies) {
/* 231 */       parseCookie(cookie, parsedCookies, maxCookies, allowEqualInValue, commaIsSeperator, allowHttpSepartorsV0);
/*     */     }
/*     */     
/* 234 */     Map<String, Cookie> retVal = new TreeMap<>();
/* 235 */     for (Cookie cookie : parsedCookies) {
/* 236 */       retVal.put(cookie.getName(), cookie);
/*     */     }
/* 238 */     return retVal;
/*     */   }
/*     */   
/*     */   static void parseRequestCookies(int maxCookies, boolean allowEqualInValue, List<String> cookies, Set<Cookie> parsedCookies, boolean commaIsSeperator, boolean allowHttpSepartorsV0) {
/* 242 */     if (cookies != null) {
/* 243 */       for (String cookie : cookies) {
/* 244 */         parseCookie(cookie, parsedCookies, maxCookies, allowEqualInValue, commaIsSeperator, allowHttpSepartorsV0);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private static void parseCookie(String cookie, Set<Cookie> parsedCookies, int maxCookies, boolean allowEqualInValue, boolean commaIsSeperator, boolean allowHttpSepartorsV0) {
/* 250 */     int state = 0;
/* 251 */     String name = null;
/* 252 */     int start = 0;
/* 253 */     boolean containsEscapedQuotes = false;
/* 254 */     int cookieCount = parsedCookies.size();
/* 255 */     Map<String, String> cookies = new HashMap<>();
/* 256 */     Map<String, String> additional = new HashMap<>();
/* 257 */     for (int i = 0; i < cookie.length(); i++) {
/* 258 */       char c = cookie.charAt(i);
/* 259 */       switch (state) {
/*     */         
/*     */         case 0:
/* 262 */           if (c == ' ' || c == '\t' || c == ';') {
/* 263 */             start = i + 1;
/*     */             break;
/*     */           } 
/* 266 */           state = 1;
/*     */ 
/*     */ 
/*     */         
/*     */         case 1:
/* 271 */           if (c == '=') {
/* 272 */             name = cookie.substring(start, i);
/* 273 */             start = i + 1;
/* 274 */             state = 2; break;
/* 275 */           }  if (c == ';' || (commaIsSeperator && c == ',')) {
/* 276 */             if (name != null) {
/* 277 */               cookieCount = createCookie(name, cookie.substring(start, i), maxCookies, cookieCount, cookies, additional);
/* 278 */             } else if (UndertowLogger.REQUEST_LOGGER.isTraceEnabled()) {
/* 279 */               UndertowLogger.REQUEST_LOGGER.trace("Ignoring invalid cookies in header " + cookie);
/*     */             } 
/* 281 */             state = 0;
/* 282 */             start = i + 1;
/*     */           } 
/*     */           break;
/*     */ 
/*     */         
/*     */         case 2:
/* 288 */           if (c == ';' || (commaIsSeperator && c == ',')) {
/* 289 */             cookieCount = createCookie(name, cookie.substring(start, i), maxCookies, cookieCount, cookies, additional);
/* 290 */             state = 0;
/* 291 */             start = i + 1; break;
/* 292 */           }  if (c == '"' && start == i) {
/* 293 */             containsEscapedQuotes = false;
/* 294 */             state = 3;
/* 295 */             start = i + 1; break;
/* 296 */           }  if (c == '=') {
/* 297 */             if (!allowEqualInValue && !allowHttpSepartorsV0) {
/* 298 */               cookieCount = createCookie(name, cookie.substring(start, i), maxCookies, cookieCount, cookies, additional);
/* 299 */               state = 4;
/* 300 */               start = i + 1;
/*     */             }  break;
/* 302 */           }  if (c != ':' && !allowHttpSepartorsV0 && LegacyCookieSupport.isHttpSeparator(c)) {
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 307 */             cookieCount = createCookie(name, cookie.substring(start, i), maxCookies, cookieCount, cookies, additional);
/* 308 */             state = 4;
/* 309 */             start = i + 1;
/*     */           } 
/*     */           break;
/*     */ 
/*     */         
/*     */         case 3:
/* 315 */           if (c == '"') {
/* 316 */             cookieCount = createCookie(name, containsEscapedQuotes ? unescapeDoubleQuotes(cookie.substring(start, i)) : cookie.substring(start, i), maxCookies, cookieCount, cookies, additional);
/* 317 */             state = 0;
/* 318 */             start = i + 1;
/*     */           } 
/*     */           
/* 321 */           if (c == '\\' && i + 1 < cookie.length() && cookie.charAt(i + 1) == '"') {
/*     */             
/* 323 */             if (i + 2 == cookie.length()) {
/*     */               break;
/*     */             }
/* 326 */             if (i + 2 < cookie.length() && (cookie.charAt(i + 2) == ';' || (commaIsSeperator && cookie
/* 327 */               .charAt(i + 2) == ','))) {
/*     */               break;
/*     */             }
/*     */             
/* 331 */             i++;
/* 332 */             containsEscapedQuotes = true;
/*     */           } 
/*     */           break;
/*     */ 
/*     */         
/*     */         case 4:
/* 338 */           if (c == ';' || (commaIsSeperator && c == ',')) {
/* 339 */             state = 0;
/*     */           }
/* 341 */           start = i + 1;
/*     */           break;
/*     */       } 
/*     */     
/*     */     } 
/* 346 */     if (state == 2) {
/* 347 */       createCookie(name, cookie.substring(start), maxCookies, cookieCount, cookies, additional);
/*     */     }
/*     */     
/* 350 */     for (Map.Entry<String, String> entry : cookies.entrySet()) {
/* 351 */       CookieImpl cookieImpl = new CookieImpl(entry.getKey(), entry.getValue());
/* 352 */       String domain = additional.get("$Domain");
/* 353 */       if (domain != null) {
/* 354 */         cookieImpl.setDomain(domain);
/*     */       }
/* 356 */       String version = additional.get("$Version");
/* 357 */       if (version != null) {
/* 358 */         cookieImpl.setVersion(Integer.parseInt(version));
/*     */       }
/* 360 */       String path = additional.get("$Path");
/* 361 */       if (path != null) {
/* 362 */         cookieImpl.setPath(path);
/*     */       }
/* 364 */       parsedCookies.add(cookieImpl);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static int createCookie(String name, String value, int maxCookies, int cookieCount, Map<String, String> cookies, Map<String, String> additional) {
/* 370 */     if (!name.isEmpty() && name.charAt(0) == '$') {
/* 371 */       if (additional.containsKey(name)) {
/* 372 */         return cookieCount;
/*     */       }
/* 374 */       additional.put(name, value);
/* 375 */       return cookieCount;
/*     */     } 
/* 377 */     if (cookieCount == maxCookies) {
/* 378 */       throw UndertowMessages.MESSAGES.tooManyCookies(maxCookies);
/*     */     }
/* 380 */     if (cookies.containsKey(name)) {
/* 381 */       return cookieCount;
/*     */     }
/* 383 */     cookies.put(name, value);
/* 384 */     return ++cookieCount;
/*     */   }
/*     */ 
/*     */   
/*     */   private static String unescapeDoubleQuotes(String value) {
/* 389 */     if (value == null || value.isEmpty()) {
/* 390 */       return value;
/*     */     }
/*     */ 
/*     */     
/* 394 */     char[] tmp = new char[value.length()];
/* 395 */     int dest = 0;
/* 396 */     for (int i = 0; i < value.length(); i++) {
/* 397 */       if (value.charAt(i) == '\\' && i + 1 < value.length() && value.charAt(i + 1) == '"') {
/* 398 */         i++;
/*     */       }
/* 400 */       tmp[dest] = value.charAt(i);
/* 401 */       dest++;
/*     */     } 
/* 403 */     return new String(tmp, 0, dest);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\Cookies.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */