/*     */ package io.undertow.util;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class URLUtils
/*     */ {
/*     */   private static final char PATH_SEPARATOR = '/';
/*     */   
/*  37 */   private static final QueryStringParser QUERY_STRING_PARSER = new QueryStringParser('&', false)
/*     */     {
/*     */       void handle(HttpServerExchange exchange, String key, String value) {
/*  40 */         exchange.addQueryParam(key, value);
/*     */       }
/*     */     };
/*  43 */   private static final QueryStringParser PATH_PARAM_PARSER = new QueryStringParser(';', true)
/*     */     {
/*     */       void handle(HttpServerExchange exchange, String key, String value) {
/*  46 */         exchange.addPathParam(key, value);
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  54 */   private static final Pattern SCHEME_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9+-.]*:.*");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void parseQueryString(String string, HttpServerExchange exchange, String charset, boolean doDecode, int maxParameters) throws ParameterLimitException {
/*  61 */     QUERY_STRING_PARSER.parse(string, exchange, charset, doDecode, maxParameters);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public static void parsePathParms(String string, HttpServerExchange exchange, String charset, boolean doDecode, int maxParameters) throws ParameterLimitException {
/*  66 */     parsePathParams(string, exchange, charset, doDecode, maxParameters);
/*     */   }
/*     */   
/*     */   public static int parsePathParams(String string, HttpServerExchange exchange, String charset, boolean doDecode, int maxParameters) throws ParameterLimitException {
/*  70 */     return PATH_PARAM_PARSER.parse(string, exchange, charset, doDecode, maxParameters);
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
/*     */   public static String decode(String s, String enc, boolean decodeSlash, StringBuilder buffer) {
/*  83 */     return decode(s, enc, decodeSlash, true, buffer);
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
/*     */   public static String decode(String s, String enc, boolean decodeSlash, boolean formEncoding, StringBuilder buffer) {
/*  96 */     buffer.setLength(0);
/*  97 */     boolean needToChange = false;
/*  98 */     int numChars = s.length();
/*  99 */     int i = 0;
/*     */     
/* 101 */     while (i < numChars) {
/* 102 */       char c = s.charAt(i);
/* 103 */       if (c == '+') {
/* 104 */         if (formEncoding) {
/* 105 */           buffer.append(' ');
/* 106 */           i++;
/* 107 */           needToChange = true; continue;
/*     */         } 
/* 109 */         i++;
/* 110 */         buffer.append(c); continue;
/*     */       } 
/* 112 */       if (c == '%' || c > '') {
/*     */ 
/*     */         
/*     */         try {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 131 */           byte[] bytes = new byte[numChars - i + 1];
/*     */           
/* 133 */           int pos = 0;
/*     */           
/* 135 */           while (i < numChars) {
/* 136 */             if (c == '%') {
/*     */               
/* 138 */               if (i + 2 >= s.length()) {
/* 139 */                 throw UndertowMessages.MESSAGES.failedToDecodeURL(s, enc, null);
/*     */               }
/* 141 */               char p1 = Character.toLowerCase(s.charAt(i + 1));
/* 142 */               char p2 = Character.toLowerCase(s.charAt(i + 2));
/* 143 */               if (!decodeSlash && ((p1 == '2' && p2 == 'f') || (p1 == '5' && p2 == 'c'))) {
/* 144 */                 if (pos + 2 >= bytes.length) {
/* 145 */                   bytes = expandBytes(bytes);
/*     */                 }
/* 147 */                 bytes[pos++] = (byte)c;
/*     */                 
/* 149 */                 bytes[pos++] = (byte)s.charAt(i + 1);
/* 150 */                 bytes[pos++] = (byte)s.charAt(i + 2);
/* 151 */                 i += 3;
/*     */                 
/* 153 */                 if (i < numChars) {
/* 154 */                   c = s.charAt(i);
/*     */                 }
/*     */                 continue;
/*     */               } 
/* 158 */               int v = 0;
/* 159 */               if (p1 >= '0' && p1 <= '9') {
/* 160 */                 v = p1 - 48 << 4;
/* 161 */               } else if (p1 >= 'a' && p1 <= 'f') {
/* 162 */                 v = p1 - 97 + 10 << 4;
/*     */               } else {
/* 164 */                 throw UndertowMessages.MESSAGES.failedToDecodeURL(s, enc, null);
/*     */               } 
/* 166 */               if (p2 >= '0' && p2 <= '9') {
/* 167 */                 v += p2 - 48;
/* 168 */               } else if (p2 >= 'a' && p2 <= 'f') {
/* 169 */                 v += p2 - 97 + 10;
/*     */               } else {
/* 171 */                 throw UndertowMessages.MESSAGES.failedToDecodeURL(s, enc, null);
/*     */               } 
/* 173 */               if (v < 0) {
/* 174 */                 throw UndertowMessages.MESSAGES.failedToDecodeURL(s, enc, null);
/*     */               }
/*     */               
/* 177 */               if (pos == bytes.length) {
/* 178 */                 bytes = expandBytes(bytes);
/*     */               }
/* 180 */               bytes[pos++] = (byte)v;
/* 181 */               i += 3;
/* 182 */               if (i < numChars)
/* 183 */                 c = s.charAt(i);  continue;
/*     */             } 
/* 185 */             if (c == '+' && formEncoding) {
/* 186 */               if (pos == bytes.length) {
/* 187 */                 bytes = expandBytes(bytes);
/*     */               }
/* 189 */               bytes[pos++] = 32;
/* 190 */               i++;
/* 191 */               if (i < numChars)
/* 192 */                 c = s.charAt(i); 
/*     */               continue;
/*     */             } 
/* 195 */             if (pos == bytes.length) {
/* 196 */               bytes = expandBytes(bytes);
/*     */             }
/* 198 */             i++;
/* 199 */             if (c >> 8 != 0) {
/* 200 */               bytes[pos++] = (byte)(c >> 8);
/* 201 */               if (pos == bytes.length) {
/* 202 */                 bytes = expandBytes(bytes);
/*     */               }
/* 204 */               bytes[pos++] = (byte)c;
/*     */             } else {
/* 206 */               bytes[pos++] = (byte)c;
/*     */             } 
/*     */             
/* 209 */             if (i < numChars) {
/* 210 */               c = s.charAt(i);
/*     */             }
/*     */           } 
/*     */ 
/*     */           
/* 215 */           String decoded = new String(bytes, 0, pos, enc);
/* 216 */           buffer.append(decoded);
/* 217 */         } catch (NumberFormatException e) {
/* 218 */           throw UndertowMessages.MESSAGES.failedToDecodeURL(s, enc, e);
/* 219 */         } catch (UnsupportedEncodingException e) {
/* 220 */           throw UndertowMessages.MESSAGES.failedToDecodeURL(s, enc, e);
/*     */         } 
/* 222 */         needToChange = true;
/*     */         break;
/*     */       } 
/* 225 */       buffer.append(c);
/* 226 */       i++;
/*     */     } 
/*     */ 
/*     */     
/* 230 */     return needToChange ? buffer.toString() : s;
/*     */   }
/*     */   
/*     */   private static byte[] expandBytes(byte[] bytes) {
/* 234 */     byte[] newBytes = new byte[bytes.length + 10];
/* 235 */     System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
/* 236 */     return newBytes;
/*     */   }
/*     */   
/*     */   private static abstract class QueryStringParser
/*     */   {
/*     */     private final char separator;
/*     */     private final boolean parseUntilSeparator;
/*     */     
/*     */     QueryStringParser(char separator, boolean parseUntilSeparator) {
/* 245 */       this.separator = separator;
/* 246 */       this.parseUntilSeparator = parseUntilSeparator;
/*     */     }
/*     */     
/*     */     int parse(String string, HttpServerExchange exchange, String charset, boolean doDecode, int max) throws ParameterLimitException {
/* 250 */       int count = 0;
/* 251 */       int i = 0;
/*     */       try {
/* 253 */         int stringStart = 0;
/* 254 */         String attrName = null;
/* 255 */         for (i = 0; i < string.length(); i++) {
/* 256 */           char c = string.charAt(i);
/* 257 */           if (c == '=' && attrName == null) {
/* 258 */             attrName = string.substring(stringStart, i);
/* 259 */             stringStart = i + 1;
/* 260 */           } else if (c == this.separator) {
/* 261 */             if (attrName != null) {
/* 262 */               handle(exchange, decode(charset, attrName, doDecode), decode(charset, string.substring(stringStart, i), doDecode));
/* 263 */               if (++count > max) {
/* 264 */                 throw UndertowMessages.MESSAGES.tooManyParameters(max);
/*     */               }
/* 266 */             } else if (stringStart != i) {
/* 267 */               handle(exchange, decode(charset, string.substring(stringStart, i), doDecode), "");
/* 268 */               if (++count > max) {
/* 269 */                 throw UndertowMessages.MESSAGES.tooManyParameters(max);
/*     */               }
/*     */             } 
/* 272 */             stringStart = i + 1;
/* 273 */             attrName = null;
/* 274 */           } else if (this.parseUntilSeparator && (c == '?' || c == '/')) {
/*     */             break;
/*     */           } 
/*     */         } 
/* 278 */         if (attrName != null) {
/* 279 */           handle(exchange, decode(charset, attrName, doDecode), decode(charset, string.substring(stringStart, i), doDecode));
/* 280 */           if (++count > max) {
/* 281 */             throw UndertowMessages.MESSAGES.tooManyParameters(max);
/*     */           }
/* 283 */         } else if (string.length() != stringStart) {
/* 284 */           handle(exchange, decode(charset, string.substring(stringStart, i), doDecode), "");
/* 285 */           if (++count > max) {
/* 286 */             throw UndertowMessages.MESSAGES.tooManyParameters(max);
/*     */           }
/*     */         } 
/* 289 */       } catch (UnsupportedEncodingException e) {
/* 290 */         throw new RuntimeException(e);
/*     */       } 
/* 292 */       return i;
/*     */     }
/*     */     
/*     */     private String decode(String charset, String attrName, boolean doDecode) throws UnsupportedEncodingException {
/* 296 */       if (doDecode) {
/* 297 */         return URLUtils.decode(attrName, charset, true, true, new StringBuilder());
/*     */       }
/* 299 */       return attrName;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     abstract void handle(HttpServerExchange param1HttpServerExchange, String param1String1, String param1String2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String normalizeSlashes(String path) {
/* 315 */     StringBuilder builder = new StringBuilder(path);
/* 316 */     boolean modified = false;
/*     */ 
/*     */     
/* 319 */     while (builder.length() > 0 && builder.length() != 1 && '/' == builder.charAt(builder.length() - 1)) {
/* 320 */       builder.deleteCharAt(builder.length() - 1);
/* 321 */       modified = true;
/*     */     } 
/*     */ 
/*     */     
/* 325 */     if (builder.length() == 0 || '/' != builder.charAt(0)) {
/* 326 */       builder.insert(0, '/');
/* 327 */       modified = true;
/*     */     } 
/*     */ 
/*     */     
/* 331 */     if (modified) {
/* 332 */       return builder.toString();
/*     */     }
/*     */     
/* 335 */     return path;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isAbsoluteUrl(String location) {
/* 346 */     if (location != null && location.length() > 0 && location.contains(":"))
/*     */     {
/* 348 */       return SCHEME_PATTERN.matcher(location).matches();
/*     */     }
/* 350 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\URLUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */