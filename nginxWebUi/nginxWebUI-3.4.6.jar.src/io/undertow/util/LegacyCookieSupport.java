/*     */ package io.undertow.util;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.server.handlers.Cookie;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class LegacyCookieSupport
/*     */ {
/*  45 */   static final boolean ALLOW_HTTP_SEPARATORS_IN_V0 = Boolean.getBoolean("io.undertow.legacy.cookie.ALLOW_HTTP_SEPARATORS_IN_V0");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  52 */   private static final boolean FWD_SLASH_IS_SEPARATOR = Boolean.getBoolean("io.undertow.legacy.cookie.FWD_SLASH_IS_SEPARATOR");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  59 */   static final boolean COMMA_IS_SEPARATOR = Boolean.getBoolean("io.undertow.legacy.cookie.COMMA_IS_SEPARATOR");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  66 */   private static final char[] V0_SEPARATORS = new char[] { ',', ';', ' ', '\t' };
/*  67 */   private static final boolean[] V0_SEPARATOR_FLAGS = new boolean[128];
/*     */ 
/*     */ 
/*     */   
/*     */   private static final char[] HTTP_SEPARATORS;
/*     */ 
/*     */ 
/*     */   
/*  75 */   private static final boolean[] HTTP_SEPARATOR_FLAGS = new boolean[128];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/*  85 */     if (FWD_SLASH_IS_SEPARATOR) {
/*  86 */       HTTP_SEPARATORS = new char[] { '\t', ' ', '"', '(', ')', ',', '/', ':', ';', '<', '=', '>', '?', '@', '[', '\\', ']', '{', '}' };
/*     */     } else {
/*     */       
/*  89 */       HTTP_SEPARATORS = new char[] { '\t', ' ', '"', '(', ')', ',', ':', ';', '<', '=', '>', '?', '@', '[', '\\', ']', '{', '}' };
/*     */     } 
/*     */     
/*  92 */     for (int i = 0; i < 128; i++) {
/*  93 */       V0_SEPARATOR_FLAGS[i] = false;
/*  94 */       HTTP_SEPARATOR_FLAGS[i] = false;
/*     */     } 
/*  96 */     for (char V0_SEPARATOR : V0_SEPARATORS) {
/*  97 */       V0_SEPARATOR_FLAGS[V0_SEPARATOR] = true;
/*     */     }
/*  99 */     for (char HTTP_SEPARATOR : HTTP_SEPARATORS) {
/* 100 */       HTTP_SEPARATOR_FLAGS[HTTP_SEPARATOR] = true;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isV0Separator(char c) {
/* 111 */     if ((c < ' ' || c >= '') && 
/* 112 */       c != '\t') {
/* 113 */       throw UndertowMessages.MESSAGES.invalidControlCharacter(Integer.toString(c));
/*     */     }
/*     */ 
/*     */     
/* 117 */     return V0_SEPARATOR_FLAGS[c];
/*     */   }
/*     */   
/*     */   private static boolean isV0Token(String value) {
/* 121 */     if (value == null) return false;
/*     */     
/* 123 */     int i = 0;
/* 124 */     int len = value.length();
/*     */     
/* 126 */     if (alreadyQuoted(value)) {
/* 127 */       i++;
/* 128 */       len--;
/*     */     } 
/*     */     
/* 131 */     for (; i < len; i++) {
/* 132 */       char c = value.charAt(i);
/*     */       
/* 134 */       if (isV0Separator(c))
/* 135 */         return true; 
/*     */     } 
/* 137 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static boolean isHttpSeparator(char c) {
/* 147 */     if ((c < ' ' || c >= '') && 
/* 148 */       c != '\t') {
/* 149 */       throw UndertowMessages.MESSAGES.invalidControlCharacter(Integer.toString(c));
/*     */     }
/*     */ 
/*     */     
/* 153 */     return HTTP_SEPARATOR_FLAGS[c];
/*     */   }
/*     */   
/*     */   private static boolean isHttpToken(String value) {
/* 157 */     if (value == null) return false;
/*     */     
/* 159 */     int i = 0;
/* 160 */     int len = value.length();
/*     */     
/* 162 */     if (alreadyQuoted(value)) {
/* 163 */       i++;
/* 164 */       len--;
/*     */     } 
/*     */     
/* 167 */     for (; i < len; i++) {
/* 168 */       char c = value.charAt(i);
/*     */       
/* 170 */       if (isHttpSeparator(c))
/* 171 */         return true; 
/*     */     } 
/* 173 */     return false;
/*     */   }
/*     */   
/*     */   private static boolean alreadyQuoted(String value) {
/* 177 */     if (value == null || value.length() < 2) return false; 
/* 178 */     return (value.charAt(0) == '"' && value.charAt(value.length() - 1) == '"');
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void maybeQuote(StringBuilder buf, String value) {
/* 187 */     if (value == null || value.length() == 0) {
/* 188 */       buf.append("\"\"");
/* 189 */     } else if (alreadyQuoted(value)) {
/* 190 */       buf.append('"');
/* 191 */       buf.append(escapeDoubleQuotes(value, 1, value.length() - 1));
/* 192 */       buf.append('"');
/* 193 */     } else if ((isHttpToken(value) && !ALLOW_HTTP_SEPARATORS_IN_V0) || (
/* 194 */       isV0Token(value) && ALLOW_HTTP_SEPARATORS_IN_V0)) {
/* 195 */       buf.append('"');
/* 196 */       buf.append(escapeDoubleQuotes(value, 0, value.length()));
/* 197 */       buf.append('"');
/*     */     } else {
/* 199 */       buf.append(value);
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
/*     */   private static String escapeDoubleQuotes(String s, int beginIndex, int endIndex) {
/* 213 */     if (s == null || s.length() == 0 || s.indexOf('"') == -1) {
/* 214 */       return s;
/*     */     }
/*     */     
/* 217 */     StringBuilder b = new StringBuilder();
/* 218 */     for (int i = beginIndex; i < endIndex; i++) {
/* 219 */       char c = s.charAt(i);
/* 220 */       if (c == '\\') {
/* 221 */         b.append(c);
/*     */         
/* 223 */         if (++i >= endIndex) throw UndertowMessages.MESSAGES.invalidEscapeCharacter(); 
/* 224 */         b.append(s.charAt(i));
/* 225 */       } else if (c == '"') {
/* 226 */         b.append('\\').append('"');
/*     */       } else {
/* 228 */         b.append(c);
/*     */       } 
/*     */     } 
/* 231 */     return b.toString();
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
/*     */   public static int adjustedCookieVersion(Cookie cookie) {
/* 246 */     int version = cookie.getVersion();
/*     */     
/* 248 */     String value = cookie.getValue();
/* 249 */     String path = cookie.getPath();
/* 250 */     String domain = cookie.getDomain();
/* 251 */     String comment = cookie.getComment();
/*     */ 
/*     */     
/* 254 */     if (version == 0 && ((!ALLOW_HTTP_SEPARATORS_IN_V0 && 
/* 255 */       isHttpToken(value)) || (ALLOW_HTTP_SEPARATORS_IN_V0 && 
/* 256 */       isV0Token(value))))
/*     */     {
/* 258 */       version = 1;
/*     */     }
/*     */     
/* 261 */     if (version == 0 && comment != null)
/*     */     {
/* 263 */       version = 1;
/*     */     }
/*     */     
/* 266 */     if (version == 0 && ((!ALLOW_HTTP_SEPARATORS_IN_V0 && 
/* 267 */       isHttpToken(path)) || (ALLOW_HTTP_SEPARATORS_IN_V0 && 
/* 268 */       isV0Token(path))))
/*     */     {
/* 270 */       version = 1;
/*     */     }
/*     */     
/* 273 */     if (version == 0 && ((!ALLOW_HTTP_SEPARATORS_IN_V0 && 
/* 274 */       isHttpToken(domain)) || (ALLOW_HTTP_SEPARATORS_IN_V0 && 
/* 275 */       isV0Token(domain))))
/*     */     {
/* 277 */       version = 1;
/*     */     }
/*     */     
/* 280 */     return version;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\LegacyCookieSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */