/*     */ package io.undertow.protocols.ajp;
/*     */ 
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.HttpString;
/*     */ import io.undertow.util.Methods;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class AjpConstants
/*     */ {
/*     */   public static final int FRAME_TYPE_SEND_HEADERS = 4;
/*     */   public static final int FRAME_TYPE_REQUEST_BODY_CHUNK = 6;
/*     */   public static final int FRAME_TYPE_SEND_BODY_CHUNK = 3;
/*     */   public static final int FRAME_TYPE_END_RESPONSE = 5;
/*     */   public static final int FRAME_TYPE_CPONG = 9;
/*     */   public static final int FRAME_TYPE_CPING = 10;
/*     */   public static final int FRAME_TYPE_SHUTDOWN = 7;
/*     */   static final Map<HttpString, Integer> HEADER_MAP;
/*     */   static final Map<HttpString, Integer> HTTP_METHODS_MAP;
/*     */   
/*     */   static {
/*  74 */     Map<HttpString, Integer> headers = new HashMap<>();
/*  75 */     headers.put(Headers.ACCEPT, Integer.valueOf(40961));
/*  76 */     headers.put(Headers.ACCEPT_CHARSET, Integer.valueOf(40962));
/*  77 */     headers.put(Headers.ACCEPT_ENCODING, Integer.valueOf(40963));
/*  78 */     headers.put(Headers.ACCEPT_LANGUAGE, Integer.valueOf(40964));
/*  79 */     headers.put(Headers.AUTHORIZATION, Integer.valueOf(40965));
/*  80 */     headers.put(Headers.CONNECTION, Integer.valueOf(40966));
/*  81 */     headers.put(Headers.CONTENT_TYPE, Integer.valueOf(40967));
/*  82 */     headers.put(Headers.CONTENT_LENGTH, Integer.valueOf(40968));
/*  83 */     headers.put(Headers.COOKIE, Integer.valueOf(40969));
/*  84 */     headers.put(Headers.COOKIE2, Integer.valueOf(40970));
/*  85 */     headers.put(Headers.HOST, Integer.valueOf(40971));
/*  86 */     headers.put(Headers.PRAGMA, Integer.valueOf(40972));
/*  87 */     headers.put(Headers.REFERER, Integer.valueOf(40973));
/*  88 */     headers.put(Headers.USER_AGENT, Integer.valueOf(40974));
/*     */     
/*  90 */     HEADER_MAP = Collections.unmodifiableMap(headers);
/*     */     
/*  92 */     Map<HttpString, Integer> methods = new HashMap<>();
/*  93 */     methods.put(Methods.OPTIONS, Integer.valueOf(1));
/*  94 */     methods.put(Methods.GET, Integer.valueOf(2));
/*  95 */     methods.put(Methods.HEAD, Integer.valueOf(3));
/*  96 */     methods.put(Methods.POST, Integer.valueOf(4));
/*  97 */     methods.put(Methods.PUT, Integer.valueOf(5));
/*  98 */     methods.put(Methods.DELETE, Integer.valueOf(6));
/*  99 */     methods.put(Methods.TRACE, Integer.valueOf(7));
/* 100 */     methods.put(Methods.PROPFIND, Integer.valueOf(8));
/* 101 */     methods.put(Methods.PROPPATCH, Integer.valueOf(9));
/* 102 */     methods.put(Methods.MKCOL, Integer.valueOf(10));
/* 103 */     methods.put(Methods.COPY, Integer.valueOf(11));
/* 104 */     methods.put(Methods.MOVE, Integer.valueOf(12));
/* 105 */     methods.put(Methods.LOCK, Integer.valueOf(13));
/* 106 */     methods.put(Methods.UNLOCK, Integer.valueOf(14));
/* 107 */     methods.put(Methods.ACL, Integer.valueOf(15));
/* 108 */     methods.put(Methods.REPORT, Integer.valueOf(16));
/* 109 */     methods.put(Methods.VERSION_CONTROL, Integer.valueOf(17));
/* 110 */     methods.put(Methods.CHECKIN, Integer.valueOf(18));
/* 111 */     methods.put(Methods.CHECKOUT, Integer.valueOf(19));
/* 112 */     methods.put(Methods.UNCHECKOUT, Integer.valueOf(20));
/* 113 */     methods.put(Methods.SEARCH, Integer.valueOf(21));
/* 114 */     methods.put(Methods.MKWORKSPACE, Integer.valueOf(22));
/* 115 */     methods.put(Methods.UPDATE, Integer.valueOf(23));
/* 116 */     methods.put(Methods.LABEL, Integer.valueOf(24));
/* 117 */     methods.put(Methods.MERGE, Integer.valueOf(25));
/* 118 */     methods.put(Methods.BASELINE_CONTROL, Integer.valueOf(26));
/* 119 */     methods.put(Methods.MKACTIVITY, Integer.valueOf(27));
/* 120 */     HTTP_METHODS_MAP = Collections.unmodifiableMap(methods);
/*     */   }
/* 122 */   static final HttpString[] HTTP_HEADERS_ARRAY = new HttpString[] { null, Headers.CONTENT_TYPE, Headers.CONTENT_LANGUAGE, Headers.CONTENT_LENGTH, Headers.DATE, Headers.LAST_MODIFIED, Headers.LOCATION, Headers.SET_COOKIE, Headers.SET_COOKIE2, Headers.SERVLET_ENGINE, Headers.STATUS, Headers.WWW_AUTHENTICATE };
/*     */   static final int ATTR_CONTEXT = 1;
/*     */   static final int ATTR_SERVLET_PATH = 2;
/*     */   static final int ATTR_REMOTE_USER = 3;
/*     */   static final int ATTR_AUTH_TYPE = 4;
/*     */   static final int ATTR_QUERY_STRING = 5;
/*     */   static final int ATTR_ROUTE = 6;
/*     */   static final int ATTR_SSL_CERT = 7;
/*     */   static final int ATTR_SSL_CIPHER = 8;
/*     */   static final int ATTR_SSL_SESSION = 9;
/*     */   static final int ATTR_REQ_ATTRIBUTE = 10;
/*     */   static final int ATTR_SSL_KEY_SIZE = 11;
/*     */   static final int ATTR_SECRET = 12;
/*     */   static final int ATTR_STORED_METHOD = 13;
/*     */   static final int ATTR_ARE_DONE = 255;
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\ajp\AjpConstants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */