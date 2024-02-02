/*     */ package io.undertow.util;
/*     */ 
/*     */ import io.undertow.server.Connectors;
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
/*     */ public final class Methods
/*     */ {
/*     */   public static final String OPTIONS_STRING = "OPTIONS";
/*     */   public static final String GET_STRING = "GET";
/*     */   public static final String HEAD_STRING = "HEAD";
/*     */   public static final String POST_STRING = "POST";
/*     */   public static final String PUT_STRING = "PUT";
/*     */   public static final String DELETE_STRING = "DELETE";
/*     */   public static final String TRACE_STRING = "TRACE";
/*     */   public static final String CONNECT_STRING = "CONNECT";
/*     */   public static final String PATCH_STRING = "PATCH";
/*     */   public static final String PROPFIND_STRING = "PROPFIND";
/*     */   public static final String PROPPATCH_STRING = "PROPPATCH";
/*     */   public static final String MKCOL_STRING = "MKCOL";
/*     */   public static final String COPY_STRING = "COPY";
/*     */   public static final String MOVE_STRING = "MOVE";
/*     */   public static final String LOCK_STRING = "LOCK";
/*     */   public static final String UNLOCK_STRING = "UNLOCK";
/*     */   public static final String ACL_STRING = "ACL";
/*     */   public static final String REPORT_STRING = "REPORT";
/*     */   public static final String VERSION_CONTROL_STRING = "VERSION-CONTROL";
/*     */   public static final String CHECKIN_STRING = "CHECKIN";
/*     */   public static final String CHECKOUT_STRING = "CHECKOUT";
/*     */   public static final String UNCHECKOUT_STRING = "UNCHECKOUT";
/*     */   public static final String SEARCH_STRING = "SEARCH";
/*     */   public static final String MKWORKSPACE_STRING = "MKWORKSPACE";
/*     */   public static final String UPDATE_STRING = "UPDATE";
/*     */   public static final String LABEL_STRING = "LABEL";
/*     */   public static final String MERGE_STRING = "MERGE";
/*     */   public static final String BASELINE_CONTROL_STRING = "BASELINE_CONTROL";
/*     */   public static final String MKACTIVITY_STRING = "MKACTIVITY";
/*  68 */   public static final HttpString OPTIONS = new HttpString("OPTIONS");
/*  69 */   public static final HttpString GET = new HttpString("GET");
/*  70 */   public static final HttpString HEAD = new HttpString("HEAD");
/*  71 */   public static final HttpString POST = new HttpString("POST");
/*  72 */   public static final HttpString PUT = new HttpString("PUT");
/*  73 */   public static final HttpString DELETE = new HttpString("DELETE");
/*  74 */   public static final HttpString TRACE = new HttpString("TRACE");
/*  75 */   public static final HttpString CONNECT = new HttpString("CONNECT");
/*  76 */   public static final HttpString PATCH = new HttpString("PATCH");
/*  77 */   public static final HttpString PROPFIND = new HttpString("PROPFIND");
/*  78 */   public static final HttpString PROPPATCH = new HttpString("PROPPATCH");
/*  79 */   public static final HttpString MKCOL = new HttpString("MKCOL");
/*  80 */   public static final HttpString COPY = new HttpString("COPY");
/*  81 */   public static final HttpString MOVE = new HttpString("MOVE");
/*  82 */   public static final HttpString LOCK = new HttpString("LOCK");
/*  83 */   public static final HttpString UNLOCK = new HttpString("UNLOCK");
/*  84 */   public static final HttpString ACL = new HttpString("ACL");
/*  85 */   public static final HttpString REPORT = new HttpString("REPORT");
/*  86 */   public static final HttpString VERSION_CONTROL = new HttpString("VERSION-CONTROL");
/*  87 */   public static final HttpString CHECKIN = new HttpString("CHECKIN");
/*  88 */   public static final HttpString CHECKOUT = new HttpString("CHECKOUT");
/*  89 */   public static final HttpString UNCHECKOUT = new HttpString("UNCHECKOUT");
/*  90 */   public static final HttpString SEARCH = new HttpString("SEARCH");
/*  91 */   public static final HttpString MKWORKSPACE = new HttpString("MKWORKSPACE");
/*  92 */   public static final HttpString UPDATE = new HttpString("UPDATE");
/*  93 */   public static final HttpString LABEL = new HttpString("LABEL");
/*  94 */   public static final HttpString MERGE = new HttpString("MERGE");
/*  95 */   public static final HttpString BASELINE_CONTROL = new HttpString("BASELINE_CONTROL");
/*  96 */   public static final HttpString MKACTIVITY = new HttpString("MKACTIVITY");
/*     */   
/*     */   private static final Map<String, HttpString> METHODS;
/*     */   
/*     */   static {
/* 101 */     Map<String, HttpString> methods = new HashMap<>();
/* 102 */     putString(methods, OPTIONS);
/* 103 */     putString(methods, GET);
/* 104 */     putString(methods, HEAD);
/* 105 */     putString(methods, POST);
/* 106 */     putString(methods, PUT);
/* 107 */     putString(methods, DELETE);
/* 108 */     putString(methods, TRACE);
/* 109 */     putString(methods, CONNECT);
/* 110 */     putString(methods, PATCH);
/* 111 */     putString(methods, PROPFIND);
/* 112 */     putString(methods, PROPPATCH);
/* 113 */     putString(methods, MKCOL);
/* 114 */     putString(methods, COPY);
/* 115 */     putString(methods, MOVE);
/* 116 */     putString(methods, LOCK);
/* 117 */     putString(methods, UNLOCK);
/* 118 */     putString(methods, ACL);
/* 119 */     putString(methods, REPORT);
/* 120 */     putString(methods, VERSION_CONTROL);
/* 121 */     putString(methods, CHECKIN);
/* 122 */     putString(methods, CHECKOUT);
/* 123 */     putString(methods, UNCHECKOUT);
/* 124 */     putString(methods, SEARCH);
/* 125 */     putString(methods, MKWORKSPACE);
/* 126 */     putString(methods, UPDATE);
/* 127 */     putString(methods, LABEL);
/* 128 */     putString(methods, MERGE);
/* 129 */     putString(methods, BASELINE_CONTROL);
/* 130 */     putString(methods, MKACTIVITY);
/*     */     
/* 132 */     METHODS = Collections.unmodifiableMap(methods);
/*     */   }
/*     */   
/*     */   private static void putString(Map<String, HttpString> methods, HttpString options) {
/* 136 */     methods.put(options.toString(), options);
/*     */   }
/*     */ 
/*     */   
/*     */   public static HttpString fromString(String method) {
/* 141 */     HttpString res = METHODS.get(method);
/* 142 */     if (res == null) {
/* 143 */       HttpString httpString = new HttpString(method);
/* 144 */       Connectors.verifyToken(httpString);
/* 145 */       return httpString;
/*     */     } 
/* 147 */     return res;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\Methods.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */