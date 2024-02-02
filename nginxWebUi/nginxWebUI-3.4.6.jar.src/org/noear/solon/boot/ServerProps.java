/*     */ package org.noear.solon.boot;
/*     */ 
/*     */ import org.noear.solon.Solon;
/*     */ import org.noear.solon.Utils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ServerProps
/*     */ {
/*     */   public static void init() {}
/*     */   
/*     */   static {
/*  64 */     String tmp = null;
/*  65 */   } public static final boolean output_meta = (Solon.cfg().getInt("solon.output.meta", 0) > 0);
/*     */   
/*  67 */   public static final String session_cookieName = System.getProperty("server.session.cookieName", "SOLONID"); @Deprecated
/*  68 */   public static final String session_cookieName2 = session_cookieName + "2"; public static final String request_encoding;
/*     */   public static final int request_maxHeaderSize;
/*     */   public static final int request_maxBodySize;
/*     */   public static final int request_maxFileSize;
/*     */   
/*     */   static {
/*  74 */     synProps("server.ssl.keyStore", "javax.net.ssl.keyStore");
/*  75 */     synProps("server.ssl.keyType", "javax.net.ssl.keyStoreType");
/*  76 */     synProps("server.ssl.keyPassword", "javax.net.ssl.keyStorePassword");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  83 */     tmp = Solon.cfg().get("server.request.maxHeaderSize", "").trim().toLowerCase();
/*  84 */     request_maxHeaderSize = getSize(tmp);
/*     */     
/*  86 */     tmp = Solon.cfg().get("server.request.maxBodySize", "").trim().toLowerCase();
/*  87 */     if (Utils.isEmpty(tmp))
/*     */     {
/*  89 */       tmp = Solon.cfg().get("server.request.maxRequestSize", "").trim().toLowerCase();
/*     */     }
/*  91 */     request_maxBodySize = getSize(tmp);
/*     */     
/*  93 */     tmp = Solon.cfg().get("server.request.maxFileSize", "").trim().toLowerCase();
/*  94 */     if (Utils.isEmpty(tmp)) {
/*  95 */       request_maxFileSize = request_maxBodySize;
/*     */     } else {
/*  97 */       request_maxFileSize = getSize(tmp);
/*     */     } 
/*     */     
/* 100 */     tmp = Solon.cfg().get("server.request.encoding", "").trim();
/* 101 */     if (Utils.isEmpty(tmp))
/*     */     {
/* 103 */       tmp = Solon.cfg().get("solon.encoding.request", "").trim();
/*     */     }
/* 105 */     if (Utils.isEmpty(tmp)) {
/* 106 */       request_encoding = Solon.encoding();
/*     */     } else {
/* 108 */       request_encoding = tmp;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 114 */   public static final int session_timeout = Solon.cfg().getInt("server.session.timeout", 0);
/* 115 */   public static final String session_state_domain = Solon.cfg().get("server.session.state.domain");
/*     */   
/*     */   public static final String response_encoding;
/*     */ 
/*     */   
/*     */   static {
/* 121 */     tmp = Solon.cfg().get("server.response.encoding", "").trim();
/* 122 */     if (Utils.isEmpty(tmp))
/*     */     {
/* 124 */       tmp = Solon.cfg().get("solon.encoding.response", "").trim();
/*     */     }
/* 126 */     if (Utils.isEmpty(tmp)) {
/* 127 */       response_encoding = Solon.encoding();
/*     */     } else {
/* 129 */       response_encoding = tmp;
/*     */     } 
/*     */   }
/*     */   
/*     */   static void synProps(String appProp, String sysProp) {
/* 134 */     String tmp = Solon.cfg().get(appProp);
/* 135 */     if (tmp != null) {
/* 136 */       System.setProperty(sysProp, tmp);
/*     */     }
/*     */   }
/*     */   
/*     */   static int getSize(String tmp) {
/* 141 */     if (tmp == null) {
/* 142 */       return 0;
/*     */     }
/*     */     
/* 145 */     if (tmp.endsWith("mb")) {
/* 146 */       int val = Integer.parseInt(tmp.substring(0, tmp.length() - 2));
/* 147 */       return val * 1204 * 1204;
/* 148 */     }  if (tmp.endsWith("kb")) {
/* 149 */       int val = Integer.parseInt(tmp.substring(0, tmp.length() - 2));
/* 150 */       return val * 1204;
/* 151 */     }  if (tmp.length() > 0) {
/* 152 */       return Integer.parseInt(tmp);
/*     */     }
/* 154 */     return 0;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\boot\ServerProps.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */