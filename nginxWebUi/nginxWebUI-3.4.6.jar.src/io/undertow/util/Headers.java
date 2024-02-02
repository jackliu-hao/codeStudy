/*     */ package io.undertow.util;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.net.URLDecoder;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Headers
/*     */ {
/*     */   public static final String ACCEPT_STRING = "Accept";
/*     */   public static final String ACCEPT_CHARSET_STRING = "Accept-Charset";
/*     */   public static final String ACCEPT_ENCODING_STRING = "Accept-Encoding";
/*     */   public static final String ACCEPT_LANGUAGE_STRING = "Accept-Language";
/*     */   public static final String ACCEPT_RANGES_STRING = "Accept-Ranges";
/*     */   public static final String AGE_STRING = "Age";
/*     */   public static final String ALLOW_STRING = "Allow";
/*     */   public static final String AUTHENTICATION_INFO_STRING = "Authentication-Info";
/*     */   public static final String AUTHORIZATION_STRING = "Authorization";
/*     */   public static final String CACHE_CONTROL_STRING = "Cache-Control";
/*     */   public static final String COOKIE_STRING = "Cookie";
/*     */   public static final String COOKIE2_STRING = "Cookie2";
/*     */   public static final String CONNECTION_STRING = "Connection";
/*     */   public static final String CONTENT_DISPOSITION_STRING = "Content-Disposition";
/*     */   public static final String CONTENT_ENCODING_STRING = "Content-Encoding";
/*     */   public static final String CONTENT_LANGUAGE_STRING = "Content-Language";
/*     */   public static final String CONTENT_LENGTH_STRING = "Content-Length";
/*     */   public static final String CONTENT_LOCATION_STRING = "Content-Location";
/*     */   public static final String CONTENT_MD5_STRING = "Content-MD5";
/*     */   public static final String CONTENT_RANGE_STRING = "Content-Range";
/*     */   public static final String CONTENT_SECURITY_POLICY_STRING = "Content-Security-Policy";
/*     */   public static final String CONTENT_TYPE_STRING = "Content-Type";
/*     */   public static final String DATE_STRING = "Date";
/*     */   public static final String ETAG_STRING = "ETag";
/*     */   public static final String EXPECT_STRING = "Expect";
/*     */   public static final String EXPIRES_STRING = "Expires";
/*     */   public static final String FORWARDED_STRING = "Forwarded";
/*     */   public static final String FROM_STRING = "From";
/*     */   public static final String HOST_STRING = "Host";
/*     */   public static final String IF_MATCH_STRING = "If-Match";
/*     */   public static final String IF_MODIFIED_SINCE_STRING = "If-Modified-Since";
/*     */   public static final String IF_NONE_MATCH_STRING = "If-None-Match";
/*     */   public static final String IF_RANGE_STRING = "If-Range";
/*     */   public static final String IF_UNMODIFIED_SINCE_STRING = "If-Unmodified-Since";
/*     */   public static final String LAST_MODIFIED_STRING = "Last-Modified";
/*     */   public static final String LOCATION_STRING = "Location";
/*     */   public static final String MAX_FORWARDS_STRING = "Max-Forwards";
/*     */   public static final String ORIGIN_STRING = "Origin";
/*     */   public static final String PRAGMA_STRING = "Pragma";
/*     */   public static final String PROXY_AUTHENTICATE_STRING = "Proxy-Authenticate";
/*     */   public static final String PROXY_AUTHORIZATION_STRING = "Proxy-Authorization";
/*     */   public static final String RANGE_STRING = "Range";
/*     */   public static final String REFERER_STRING = "Referer";
/*     */   public static final String REFERRER_POLICY_STRING = "Referrer-Policy";
/*     */   public static final String REFRESH_STRING = "Refresh";
/*     */   public static final String RETRY_AFTER_STRING = "Retry-After";
/*     */   public static final String SEC_WEB_SOCKET_ACCEPT_STRING = "Sec-WebSocket-Accept";
/*     */   public static final String SEC_WEB_SOCKET_EXTENSIONS_STRING = "Sec-WebSocket-Extensions";
/*     */   public static final String SEC_WEB_SOCKET_KEY_STRING = "Sec-WebSocket-Key";
/*     */   public static final String SEC_WEB_SOCKET_KEY1_STRING = "Sec-WebSocket-Key1";
/*     */   public static final String SEC_WEB_SOCKET_KEY2_STRING = "Sec-WebSocket-Key2";
/*     */   public static final String SEC_WEB_SOCKET_LOCATION_STRING = "Sec-WebSocket-Location";
/*     */   public static final String SEC_WEB_SOCKET_ORIGIN_STRING = "Sec-WebSocket-Origin";
/*     */   public static final String SEC_WEB_SOCKET_PROTOCOL_STRING = "Sec-WebSocket-Protocol";
/*     */   public static final String SEC_WEB_SOCKET_VERSION_STRING = "Sec-WebSocket-Version";
/*     */   public static final String SERVER_STRING = "Server";
/*     */   public static final String SERVLET_ENGINE_STRING = "Servlet-Engine";
/*     */   public static final String SET_COOKIE_STRING = "Set-Cookie";
/*     */   public static final String SET_COOKIE2_STRING = "Set-Cookie2";
/*     */   public static final String SSL_CLIENT_CERT_STRING = "SSL_CLIENT_CERT";
/*     */   public static final String SSL_CIPHER_STRING = "SSL_CIPHER";
/*     */   public static final String SSL_SESSION_ID_STRING = "SSL_SESSION_ID";
/*     */   public static final String SSL_CIPHER_USEKEYSIZE_STRING = "SSL_CIPHER_USEKEYSIZE";
/*     */   public static final String STATUS_STRING = "Status";
/*     */   public static final String STRICT_TRANSPORT_SECURITY_STRING = "Strict-Transport-Security";
/*     */   public static final String TE_STRING = "TE";
/*     */   public static final String TRAILER_STRING = "Trailer";
/*     */   public static final String TRANSFER_ENCODING_STRING = "Transfer-Encoding";
/*     */   public static final String UPGRADE_STRING = "Upgrade";
/*     */   public static final String USER_AGENT_STRING = "User-Agent";
/*     */   public static final String VARY_STRING = "Vary";
/*     */   public static final String VIA_STRING = "Via";
/*     */   public static final String WARNING_STRING = "Warning";
/*     */   public static final String WWW_AUTHENTICATE_STRING = "WWW-Authenticate";
/*     */   public static final String X_CONTENT_TYPE_OPTIONS_STRING = "X-Content-Type-Options";
/*     */   public static final String X_DISABLE_PUSH_STRING = "X-Disable-Push";
/*     */   public static final String X_FORWARDED_FOR_STRING = "X-Forwarded-For";
/*     */   public static final String X_FORWARDED_PROTO_STRING = "X-Forwarded-Proto";
/*     */   public static final String X_FORWARDED_HOST_STRING = "X-Forwarded-Host";
/*     */   public static final String X_FORWARDED_PORT_STRING = "X-Forwarded-Port";
/*     */   public static final String X_FORWARDED_SERVER_STRING = "X-Forwarded-Server";
/*     */   public static final String X_FRAME_OPTIONS_STRING = "X-Frame-Options";
/*     */   public static final String X_XSS_PROTECTION_STRING = "X-Xss-Protection";
/* 129 */   public static final HttpString ACCEPT = new HttpString("Accept", 1);
/* 130 */   public static final HttpString ACCEPT_CHARSET = new HttpString("Accept-Charset", 2);
/* 131 */   public static final HttpString ACCEPT_ENCODING = new HttpString("Accept-Encoding", 3);
/* 132 */   public static final HttpString ACCEPT_LANGUAGE = new HttpString("Accept-Language", 4);
/* 133 */   public static final HttpString ACCEPT_RANGES = new HttpString("Accept-Ranges", 5);
/* 134 */   public static final HttpString AGE = new HttpString("Age", 6);
/* 135 */   public static final HttpString ALLOW = new HttpString("Allow", 7);
/* 136 */   public static final HttpString AUTHENTICATION_INFO = new HttpString("Authentication-Info", 8);
/* 137 */   public static final HttpString AUTHORIZATION = new HttpString("Authorization", 9);
/* 138 */   public static final HttpString CACHE_CONTROL = new HttpString("Cache-Control", 10);
/* 139 */   public static final HttpString CONNECTION = new HttpString("Connection", 11);
/* 140 */   public static final HttpString CONTENT_DISPOSITION = new HttpString("Content-Disposition", 12);
/* 141 */   public static final HttpString CONTENT_ENCODING = new HttpString("Content-Encoding", 13);
/* 142 */   public static final HttpString CONTENT_LANGUAGE = new HttpString("Content-Language", 14);
/* 143 */   public static final HttpString CONTENT_LENGTH = new HttpString("Content-Length", 15);
/* 144 */   public static final HttpString CONTENT_LOCATION = new HttpString("Content-Location", 16);
/* 145 */   public static final HttpString CONTENT_MD5 = new HttpString("Content-MD5", 17);
/* 146 */   public static final HttpString CONTENT_RANGE = new HttpString("Content-Range", 18);
/* 147 */   public static final HttpString CONTENT_SECURITY_POLICY = new HttpString("Content-Security-Policy", 19);
/* 148 */   public static final HttpString CONTENT_TYPE = new HttpString("Content-Type", 20);
/* 149 */   public static final HttpString COOKIE = new HttpString("Cookie", 21);
/* 150 */   public static final HttpString COOKIE2 = new HttpString("Cookie2", 22);
/* 151 */   public static final HttpString DATE = new HttpString("Date", 23);
/* 152 */   public static final HttpString ETAG = new HttpString("ETag", 24);
/* 153 */   public static final HttpString EXPECT = new HttpString("Expect", 25);
/* 154 */   public static final HttpString EXPIRES = new HttpString("Expires", 26);
/* 155 */   public static final HttpString FORWARDED = new HttpString("Forwarded", 27);
/* 156 */   public static final HttpString FROM = new HttpString("From", 28);
/* 157 */   public static final HttpString HOST = new HttpString("Host", 29);
/* 158 */   public static final HttpString IF_MATCH = new HttpString("If-Match", 30);
/* 159 */   public static final HttpString IF_MODIFIED_SINCE = new HttpString("If-Modified-Since", 31);
/* 160 */   public static final HttpString IF_NONE_MATCH = new HttpString("If-None-Match", 32);
/* 161 */   public static final HttpString IF_RANGE = new HttpString("If-Range", 33);
/* 162 */   public static final HttpString IF_UNMODIFIED_SINCE = new HttpString("If-Unmodified-Since", 34);
/* 163 */   public static final HttpString LAST_MODIFIED = new HttpString("Last-Modified", 35);
/* 164 */   public static final HttpString LOCATION = new HttpString("Location", 36);
/* 165 */   public static final HttpString MAX_FORWARDS = new HttpString("Max-Forwards", 37);
/* 166 */   public static final HttpString ORIGIN = new HttpString("Origin", 38);
/* 167 */   public static final HttpString PRAGMA = new HttpString("Pragma", 39);
/* 168 */   public static final HttpString PROXY_AUTHENTICATE = new HttpString("Proxy-Authenticate", 40);
/* 169 */   public static final HttpString PROXY_AUTHORIZATION = new HttpString("Proxy-Authorization", 41);
/* 170 */   public static final HttpString RANGE = new HttpString("Range", 42);
/* 171 */   public static final HttpString REFERER = new HttpString("Referer", 43);
/* 172 */   public static final HttpString REFERRER_POLICY = new HttpString("Referrer-Policy", 44);
/* 173 */   public static final HttpString REFRESH = new HttpString("Refresh", 45);
/* 174 */   public static final HttpString RETRY_AFTER = new HttpString("Retry-After", 46);
/* 175 */   public static final HttpString SEC_WEB_SOCKET_ACCEPT = new HttpString("Sec-WebSocket-Accept", 47);
/* 176 */   public static final HttpString SEC_WEB_SOCKET_EXTENSIONS = new HttpString("Sec-WebSocket-Extensions", 48);
/* 177 */   public static final HttpString SEC_WEB_SOCKET_KEY = new HttpString("Sec-WebSocket-Key", 49);
/* 178 */   public static final HttpString SEC_WEB_SOCKET_KEY1 = new HttpString("Sec-WebSocket-Key1", 50);
/* 179 */   public static final HttpString SEC_WEB_SOCKET_KEY2 = new HttpString("Sec-WebSocket-Key2", 51);
/* 180 */   public static final HttpString SEC_WEB_SOCKET_LOCATION = new HttpString("Sec-WebSocket-Location", 52);
/* 181 */   public static final HttpString SEC_WEB_SOCKET_ORIGIN = new HttpString("Sec-WebSocket-Origin", 53);
/* 182 */   public static final HttpString SEC_WEB_SOCKET_PROTOCOL = new HttpString("Sec-WebSocket-Protocol", 54);
/* 183 */   public static final HttpString SEC_WEB_SOCKET_VERSION = new HttpString("Sec-WebSocket-Version", 55);
/* 184 */   public static final HttpString SERVER = new HttpString("Server", 56);
/* 185 */   public static final HttpString SERVLET_ENGINE = new HttpString("Servlet-Engine", 57);
/* 186 */   public static final HttpString SET_COOKIE = new HttpString("Set-Cookie", 58);
/* 187 */   public static final HttpString SET_COOKIE2 = new HttpString("Set-Cookie2", 59);
/* 188 */   public static final HttpString SSL_CIPHER = new HttpString("SSL_CIPHER", 60);
/* 189 */   public static final HttpString SSL_CIPHER_USEKEYSIZE = new HttpString("SSL_CIPHER_USEKEYSIZE", 61);
/* 190 */   public static final HttpString SSL_CLIENT_CERT = new HttpString("SSL_CLIENT_CERT", 62);
/* 191 */   public static final HttpString SSL_SESSION_ID = new HttpString("SSL_SESSION_ID", 63);
/* 192 */   public static final HttpString STATUS = new HttpString("Status", 64);
/* 193 */   public static final HttpString STRICT_TRANSPORT_SECURITY = new HttpString("Strict-Transport-Security", 65);
/* 194 */   public static final HttpString TE = new HttpString("TE", 66);
/* 195 */   public static final HttpString TRAILER = new HttpString("Trailer", 67);
/* 196 */   public static final HttpString TRANSFER_ENCODING = new HttpString("Transfer-Encoding", 68);
/* 197 */   public static final HttpString UPGRADE = new HttpString("Upgrade", 69);
/* 198 */   public static final HttpString USER_AGENT = new HttpString("User-Agent", 70);
/* 199 */   public static final HttpString VARY = new HttpString("Vary", 71);
/* 200 */   public static final HttpString VIA = new HttpString("Via", 72);
/* 201 */   public static final HttpString WARNING = new HttpString("Warning", 73);
/* 202 */   public static final HttpString WWW_AUTHENTICATE = new HttpString("WWW-Authenticate", 74);
/* 203 */   public static final HttpString X_CONTENT_TYPE_OPTIONS = new HttpString("X-Content-Type-Options", 75);
/* 204 */   public static final HttpString X_DISABLE_PUSH = new HttpString("X-Disable-Push", 76);
/* 205 */   public static final HttpString X_FORWARDED_FOR = new HttpString("X-Forwarded-For", 77);
/* 206 */   public static final HttpString X_FORWARDED_HOST = new HttpString("X-Forwarded-Host", 78);
/* 207 */   public static final HttpString X_FORWARDED_PORT = new HttpString("X-Forwarded-Port", 79);
/* 208 */   public static final HttpString X_FORWARDED_PROTO = new HttpString("X-Forwarded-Proto", 80);
/* 209 */   public static final HttpString X_FORWARDED_SERVER = new HttpString("X-Forwarded-Server", 81);
/* 210 */   public static final HttpString X_FRAME_OPTIONS = new HttpString("X-Frame-Options", 82);
/* 211 */   public static final HttpString X_XSS_PROTECTION = new HttpString("X-Xss-Protection", 83);
/*     */ 
/*     */   
/* 214 */   public static final HttpString COMPRESS = new HttpString("compress");
/* 215 */   public static final HttpString X_COMPRESS = new HttpString("x-compress");
/* 216 */   public static final HttpString DEFLATE = new HttpString("deflate");
/* 217 */   public static final HttpString IDENTITY = new HttpString("identity");
/* 218 */   public static final HttpString GZIP = new HttpString("gzip");
/* 219 */   public static final HttpString X_GZIP = new HttpString("x-gzip");
/*     */ 
/*     */ 
/*     */   
/* 223 */   public static final HttpString CHUNKED = new HttpString("chunked");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 230 */   public static final HttpString KEEP_ALIVE = new HttpString("keep-alive");
/* 231 */   public static final HttpString CLOSE = new HttpString("close");
/*     */   
/*     */   public static final String CONTENT_TRANSFER_ENCODING_STRING = "Content-Transfer-Encoding";
/*     */   
/* 235 */   public static final HttpString CONTENT_TRANSFER_ENCODING = new HttpString("Content-Transfer-Encoding");
/*     */ 
/*     */   
/* 238 */   public static final HttpString BASIC = new HttpString("Basic");
/* 239 */   public static final HttpString DIGEST = new HttpString("Digest");
/* 240 */   public static final HttpString NEGOTIATE = new HttpString("Negotiate");
/*     */ 
/*     */   
/* 243 */   public static final HttpString ALGORITHM = new HttpString("algorithm");
/* 244 */   public static final HttpString AUTH_PARAM = new HttpString("auth-param");
/* 245 */   public static final HttpString CNONCE = new HttpString("cnonce");
/* 246 */   public static final HttpString DOMAIN = new HttpString("domain");
/* 247 */   public static final HttpString NEXT_NONCE = new HttpString("nextnonce");
/* 248 */   public static final HttpString NONCE = new HttpString("nonce");
/* 249 */   public static final HttpString NONCE_COUNT = new HttpString("nc");
/* 250 */   public static final HttpString OPAQUE = new HttpString("opaque");
/* 251 */   public static final HttpString QOP = new HttpString("qop");
/* 252 */   public static final HttpString REALM = new HttpString("realm");
/* 253 */   public static final HttpString RESPONSE = new HttpString("response");
/* 254 */   public static final HttpString RESPONSE_AUTH = new HttpString("rspauth");
/* 255 */   public static final HttpString STALE = new HttpString("stale");
/* 256 */   public static final HttpString URI = new HttpString("uri");
/* 257 */   public static final HttpString USERNAME = new HttpString("username");
/*     */   
/*     */   private static final Map<String, HttpString> HTTP_STRING_MAP;
/*     */ 
/*     */   
/*     */   static {
/* 263 */     Map<String, HttpString> map = AccessController.<Map<String, HttpString>>doPrivileged(new PrivilegedAction<Map<String, HttpString>>()
/*     */         {
/*     */           public Map<String, HttpString> run() {
/* 266 */             Map<String, HttpString> map = new HashMap<>();
/* 267 */             Field[] fields = Headers.class.getDeclaredFields();
/* 268 */             for (Field field : fields) {
/* 269 */               if (Modifier.isStatic(field.getModifiers()) && field.getType() == HttpString.class) {
/* 270 */                 field.setAccessible(true);
/*     */                 try {
/* 272 */                   HttpString result = (HttpString)field.get(null);
/* 273 */                   map.put(result.toString(), result);
/* 274 */                 } catch (IllegalAccessException e) {
/* 275 */                   throw new RuntimeException(e);
/*     */                 } 
/*     */               } 
/*     */             } 
/* 279 */             return map;
/*     */           }
/*     */         });
/* 282 */     HTTP_STRING_MAP = Collections.unmodifiableMap(map);
/*     */   }
/*     */   
/*     */   public static HttpString fromCache(String string) {
/* 286 */     return HTTP_STRING_MAP.get(string);
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
/*     */   @Deprecated
/*     */   public static String extractTokenFromHeader(String header, String key) {
/* 301 */     int pos = header.indexOf(' ' + key + '=');
/* 302 */     if (pos == -1) {
/* 303 */       if (!header.startsWith(key + '=')) {
/* 304 */         return null;
/*     */       }
/* 306 */       pos = 0;
/*     */     } else {
/* 308 */       pos++;
/*     */     } 
/*     */     
/* 311 */     int start = pos + key.length() + 1; int end;
/* 312 */     for (end = start; end < header.length(); end++) {
/* 313 */       char c = header.charAt(end);
/* 314 */       if (c == ' ' || c == '\t' || c == ';') {
/*     */         break;
/*     */       }
/*     */     } 
/* 318 */     return header.substring(start, end);
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
/*     */   public static String extractQuotedValueFromHeader(String header, String key) {
/* 334 */     int keypos = 0;
/* 335 */     int pos = -1;
/* 336 */     boolean whiteSpace = true;
/* 337 */     boolean inQuotes = false;
/* 338 */     for (int i = 0; i < header.length() - 1; i++) {
/*     */       
/* 340 */       char c = header.charAt(i);
/* 341 */       if (inQuotes) {
/* 342 */         if (c == '"') {
/* 343 */           inQuotes = false;
/*     */         }
/*     */       } else {
/* 346 */         if (key.charAt(keypos) == c && (whiteSpace || keypos > 0)) {
/* 347 */           keypos++;
/* 348 */           whiteSpace = false;
/* 349 */         } else if (c == '"') {
/* 350 */           keypos = 0;
/* 351 */           inQuotes = true;
/* 352 */           whiteSpace = false;
/*     */         } else {
/* 354 */           keypos = 0;
/* 355 */           whiteSpace = (c == ' ' || c == ';' || c == '\t');
/*     */         } 
/* 357 */         if (keypos == key.length()) {
/* 358 */           if (header.charAt(i + 1) == '=') {
/* 359 */             pos = i + 2;
/*     */             break;
/*     */           } 
/* 362 */           keypos = 0;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 368 */     if (pos == -1) {
/* 369 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 373 */     int start = pos;
/* 374 */     if (header.charAt(start) == '"') {
/*     */       int j;
/* 376 */       for (j = ++start; j < header.length(); j++) {
/* 377 */         char c = header.charAt(j);
/* 378 */         if (c == '"') {
/*     */           break;
/*     */         }
/*     */       } 
/* 382 */       return header.substring(start, j);
/*     */     } 
/*     */     
/*     */     int end;
/* 386 */     for (end = start; end < header.length(); end++) {
/* 387 */       char c = header.charAt(end);
/* 388 */       if (c == ' ' || c == '\t' || c == ';') {
/*     */         break;
/*     */       }
/*     */     } 
/* 392 */     return header.substring(start, end);
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
/*     */   public static String extractQuotedValueFromHeaderWithEncoding(String header, String key) {
/* 408 */     String value = extractQuotedValueFromHeader(header, key);
/* 409 */     if (value != null) {
/* 410 */       return value;
/*     */     }
/* 412 */     value = extractQuotedValueFromHeader(header, key + "*");
/* 413 */     if (value != null) {
/* 414 */       int characterSetDelimiter = value.indexOf('\'');
/* 415 */       int languageDelimiter = value.lastIndexOf('\'', characterSetDelimiter + 1);
/* 416 */       String characterSet = value.substring(0, characterSetDelimiter);
/*     */       try {
/* 418 */         String fileNameURLEncoded = value.substring(languageDelimiter + 1);
/* 419 */         return URLDecoder.decode(fileNameURLEncoded, characterSet);
/* 420 */       } catch (UnsupportedEncodingException e) {
/* 421 */         throw new RuntimeException(e);
/*     */       } 
/*     */     } 
/* 424 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\Headers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */