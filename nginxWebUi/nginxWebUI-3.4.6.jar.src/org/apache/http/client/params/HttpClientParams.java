/*     */ package org.apache.http.client.params;
/*     */ 
/*     */ import org.apache.http.params.HttpConnectionParams;
/*     */ import org.apache.http.params.HttpParams;
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
/*     */ @Deprecated
/*     */ public class HttpClientParams
/*     */ {
/*     */   public static boolean isRedirecting(HttpParams params) {
/*  48 */     Args.notNull(params, "HTTP parameters");
/*  49 */     return params.getBooleanParameter("http.protocol.handle-redirects", true);
/*     */   }
/*     */ 
/*     */   
/*     */   public static void setRedirecting(HttpParams params, boolean value) {
/*  54 */     Args.notNull(params, "HTTP parameters");
/*  55 */     params.setBooleanParameter("http.protocol.handle-redirects", value);
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isAuthenticating(HttpParams params) {
/*  60 */     Args.notNull(params, "HTTP parameters");
/*  61 */     return params.getBooleanParameter("http.protocol.handle-authentication", true);
/*     */   }
/*     */ 
/*     */   
/*     */   public static void setAuthenticating(HttpParams params, boolean value) {
/*  66 */     Args.notNull(params, "HTTP parameters");
/*  67 */     params.setBooleanParameter("http.protocol.handle-authentication", value);
/*     */   }
/*     */ 
/*     */   
/*     */   public static String getCookiePolicy(HttpParams params) {
/*  72 */     Args.notNull(params, "HTTP parameters");
/*  73 */     String cookiePolicy = (String)params.getParameter("http.protocol.cookie-policy");
/*     */     
/*  75 */     if (cookiePolicy == null) {
/*  76 */       return "best-match";
/*     */     }
/*  78 */     return cookiePolicy;
/*     */   }
/*     */   
/*     */   public static void setCookiePolicy(HttpParams params, String cookiePolicy) {
/*  82 */     Args.notNull(params, "HTTP parameters");
/*  83 */     params.setParameter("http.protocol.cookie-policy", cookiePolicy);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setConnectionManagerTimeout(HttpParams params, long timeout) {
/*  92 */     Args.notNull(params, "HTTP parameters");
/*  93 */     params.setLongParameter("http.conn-manager.timeout", timeout);
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
/*     */   public static long getConnectionManagerTimeout(HttpParams params) {
/* 106 */     Args.notNull(params, "HTTP parameters");
/* 107 */     Long timeout = (Long)params.getParameter("http.conn-manager.timeout");
/* 108 */     if (timeout != null) {
/* 109 */       return timeout.longValue();
/*     */     }
/* 111 */     return HttpConnectionParams.getConnectionTimeout(params);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\client\params\HttpClientParams.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */