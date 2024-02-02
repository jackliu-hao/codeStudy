/*     */ package cn.hutool.http;
/*     */ 
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.RandomUtil;
/*     */ import cn.hutool.core.util.ReflectUtil;
/*     */ import cn.hutool.http.cookie.GlobalCookieManager;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Field;
/*     */ import java.net.CookieManager;
/*     */ import java.net.HttpURLConnection;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HttpGlobalConfig
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  31 */   private static int timeout = -1;
/*     */   private static boolean isAllowPatch = false;
/*  33 */   private static String boundary = "--------------------Hutool_" + RandomUtil.randomString(16);
/*  34 */   private static int maxRedirectCount = 0;
/*     */ 
/*     */   
/*     */   private static boolean ignoreEOFError = true;
/*     */ 
/*     */   
/*     */   private static boolean decodeUrl = false;
/*     */ 
/*     */   
/*     */   public static int getTimeout() {
/*  44 */     return timeout;
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
/*     */   public static synchronized void setTimeout(int customTimeout) {
/*  59 */     timeout = customTimeout;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getBoundary() {
/*  69 */     return boundary;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized void setBoundary(String customBoundary) {
/*  79 */     boundary = customBoundary;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getMaxRedirectCount() {
/*  90 */     return maxRedirectCount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized void setMaxRedirectCount(int customMaxRedirectCount) {
/* 101 */     maxRedirectCount = customMaxRedirectCount;
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
/*     */   public static boolean isIgnoreEOFError() {
/* 113 */     return ignoreEOFError;
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
/*     */   public static synchronized void setIgnoreEOFError(boolean customIgnoreEOFError) {
/* 125 */     ignoreEOFError = customIgnoreEOFError;
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
/*     */   public static boolean isDecodeUrl() {
/* 137 */     return decodeUrl;
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
/*     */   public static synchronized void setDecodeUrl(boolean customDecodeUrl) {
/* 149 */     decodeUrl = customDecodeUrl;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CookieManager getCookieManager() {
/* 160 */     return GlobalCookieManager.getCookieManager();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized void setCookieManager(CookieManager customCookieManager) {
/* 171 */     GlobalCookieManager.setCookieManager(customCookieManager);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized void closeCookie() {
/* 181 */     GlobalCookieManager.setCookieManager(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized void allowPatch() {
/* 192 */     if (isAllowPatch) {
/*     */       return;
/*     */     }
/* 195 */     Field methodsField = ReflectUtil.getField(HttpURLConnection.class, "methods");
/* 196 */     if (null == methodsField) {
/* 197 */       throw new HttpException("None static field [methods] with Java version: [{}]", new Object[] { System.getProperty("java.version") });
/*     */     }
/*     */ 
/*     */     
/* 201 */     ReflectUtil.setFieldValue(methodsField, "modifiers", Integer.valueOf(methodsField.getModifiers() & 0xFFFFFFEF));
/* 202 */     String[] methods = { "GET", "POST", "HEAD", "OPTIONS", "PUT", "DELETE", "TRACE", "PATCH" };
/*     */ 
/*     */     
/* 205 */     ReflectUtil.setFieldValue(null, methodsField, methods);
/*     */ 
/*     */     
/* 208 */     Object staticFieldValue = ReflectUtil.getStaticFieldValue(methodsField);
/* 209 */     if (false == ArrayUtil.equals(methods, staticFieldValue)) {
/* 210 */       throw new HttpException("Inject value to field [methods] failed!");
/*     */     }
/*     */     
/* 213 */     isAllowPatch = true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\HttpGlobalConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */