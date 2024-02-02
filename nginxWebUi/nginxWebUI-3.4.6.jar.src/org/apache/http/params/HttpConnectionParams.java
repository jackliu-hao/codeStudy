/*     */ package org.apache.http.params;
/*     */ 
/*     */ import org.apache.http.util.Args;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ public final class HttpConnectionParams
/*     */   implements CoreConnectionPNames
/*     */ {
/*     */   public static int getSoTimeout(HttpParams params) {
/*  55 */     Args.notNull(params, "HTTP parameters");
/*  56 */     return params.getIntParameter("http.socket.timeout", 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setSoTimeout(HttpParams params, int timeout) {
/*  66 */     Args.notNull(params, "HTTP parameters");
/*  67 */     params.setIntParameter("http.socket.timeout", timeout);
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
/*     */   public static boolean getSoReuseaddr(HttpParams params) {
/*  81 */     Args.notNull(params, "HTTP parameters");
/*  82 */     return params.getBooleanParameter("http.socket.reuseaddr", false);
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
/*     */   public static void setSoReuseaddr(HttpParams params, boolean reuseaddr) {
/*  94 */     Args.notNull(params, "HTTP parameters");
/*  95 */     params.setBooleanParameter("http.socket.reuseaddr", reuseaddr);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean getTcpNoDelay(HttpParams params) {
/* 106 */     Args.notNull(params, "HTTP parameters");
/* 107 */     return params.getBooleanParameter("http.tcp.nodelay", true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setTcpNoDelay(HttpParams params, boolean value) {
/* 117 */     Args.notNull(params, "HTTP parameters");
/* 118 */     params.setBooleanParameter("http.tcp.nodelay", value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getSocketBufferSize(HttpParams params) {
/* 129 */     Args.notNull(params, "HTTP parameters");
/* 130 */     return params.getIntParameter("http.socket.buffer-size", -1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setSocketBufferSize(HttpParams params, int size) {
/* 141 */     Args.notNull(params, "HTTP parameters");
/* 142 */     params.setIntParameter("http.socket.buffer-size", size);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getLinger(HttpParams params) {
/* 153 */     Args.notNull(params, "HTTP parameters");
/* 154 */     return params.getIntParameter("http.socket.linger", -1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setLinger(HttpParams params, int value) {
/* 164 */     Args.notNull(params, "HTTP parameters");
/* 165 */     params.setIntParameter("http.socket.linger", value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getConnectionTimeout(HttpParams params) {
/* 176 */     Args.notNull(params, "HTTP parameters");
/* 177 */     return params.getIntParameter("http.connection.timeout", 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setConnectionTimeout(HttpParams params, int timeout) {
/* 188 */     Args.notNull(params, "HTTP parameters");
/* 189 */     params.setIntParameter("http.connection.timeout", timeout);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isStaleCheckingEnabled(HttpParams params) {
/* 200 */     Args.notNull(params, "HTTP parameters");
/* 201 */     return params.getBooleanParameter("http.connection.stalecheck", true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setStaleCheckingEnabled(HttpParams params, boolean value) {
/* 212 */     Args.notNull(params, "HTTP parameters");
/* 213 */     params.setBooleanParameter("http.connection.stalecheck", value);
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
/*     */   public static boolean getSoKeepalive(HttpParams params) {
/* 226 */     Args.notNull(params, "HTTP parameters");
/* 227 */     return params.getBooleanParameter("http.socket.keepalive", false);
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
/*     */   public static void setSoKeepalive(HttpParams params, boolean enableKeepalive) {
/* 239 */     Args.notNull(params, "HTTP parameters");
/* 240 */     params.setBooleanParameter("http.socket.keepalive", enableKeepalive);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\params\HttpConnectionParams.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */