/*     */ package cn.hutool.http;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public enum Header
/*     */ {
/*  17 */   AUTHORIZATION("Authorization"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  24 */   PROXY_AUTHORIZATION("Proxy-Authorization"),
/*     */ 
/*     */ 
/*     */   
/*  28 */   DATE("Date"),
/*     */ 
/*     */ 
/*     */   
/*  32 */   CONNECTION("Connection"),
/*     */ 
/*     */ 
/*     */   
/*  36 */   MIME_VERSION("MIME-Version"),
/*     */ 
/*     */ 
/*     */   
/*  40 */   TRAILER("Trailer"),
/*     */ 
/*     */ 
/*     */   
/*  44 */   TRANSFER_ENCODING("Transfer-Encoding"),
/*     */ 
/*     */ 
/*     */   
/*  48 */   UPGRADE("Upgrade"),
/*     */ 
/*     */ 
/*     */   
/*  52 */   VIA("Via"),
/*     */ 
/*     */ 
/*     */   
/*  56 */   CACHE_CONTROL("Cache-Control"),
/*     */ 
/*     */ 
/*     */   
/*  60 */   PRAGMA("Pragma"),
/*     */ 
/*     */ 
/*     */   
/*  64 */   CONTENT_TYPE("Content-Type"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  70 */   HOST("Host"),
/*     */ 
/*     */ 
/*     */   
/*  74 */   REFERER("Referer"),
/*     */ 
/*     */ 
/*     */   
/*  78 */   ORIGIN("Origin"),
/*     */ 
/*     */ 
/*     */   
/*  82 */   USER_AGENT("User-Agent"),
/*     */ 
/*     */ 
/*     */   
/*  86 */   ACCEPT("Accept"),
/*     */ 
/*     */ 
/*     */   
/*  90 */   ACCEPT_LANGUAGE("Accept-Language"),
/*     */ 
/*     */ 
/*     */   
/*  94 */   ACCEPT_ENCODING("Accept-Encoding"),
/*     */ 
/*     */ 
/*     */   
/*  98 */   ACCEPT_CHARSET("Accept-Charset"),
/*     */ 
/*     */ 
/*     */   
/* 102 */   COOKIE("Cookie"),
/*     */ 
/*     */ 
/*     */   
/* 106 */   CONTENT_LENGTH("Content-Length"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 112 */   WWW_AUTHENTICATE("WWW-Authenticate"),
/*     */ 
/*     */ 
/*     */   
/* 116 */   SET_COOKIE("Set-Cookie"),
/*     */ 
/*     */ 
/*     */   
/* 120 */   CONTENT_ENCODING("Content-Encoding"),
/*     */ 
/*     */ 
/*     */   
/* 124 */   CONTENT_DISPOSITION("Content-Disposition"),
/*     */ 
/*     */ 
/*     */   
/* 128 */   ETAG("ETag"),
/*     */ 
/*     */ 
/*     */   
/* 132 */   LOCATION("Location");
/*     */   
/*     */   private final String value;
/*     */   
/*     */   Header(String value) {
/* 137 */     this.value = value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getValue() {
/* 146 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 151 */     return getValue();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\Header.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */