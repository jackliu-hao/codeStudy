/*     */ package cn.hutool.http.cookie;
/*     */ 
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.util.URLUtil;
/*     */ import cn.hutool.http.HttpConnection;
/*     */ import java.io.IOException;
/*     */ import java.net.CookieManager;
/*     */ import java.net.CookiePolicy;
/*     */ import java.net.HttpCookie;
/*     */ import java.net.URI;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
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
/*     */ public class GlobalCookieManager
/*     */ {
/*  27 */   private static CookieManager cookieManager = new CookieManager(new ThreadLocalCookieStore(), CookiePolicy.ACCEPT_ALL);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setCookieManager(CookieManager customCookieManager) {
/*  36 */     cookieManager = customCookieManager;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CookieManager getCookieManager() {
/*  45 */     return cookieManager;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<HttpCookie> getCookies(HttpConnection conn) {
/*  56 */     return cookieManager.getCookieStore().get(getURI(conn));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void add(HttpConnection conn) {
/*     */     Map<String, List<String>> cookieHeader;
/*  65 */     if (null == cookieManager) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*  72 */       cookieHeader = cookieManager.get(getURI(conn), new HashMap<>(0));
/*  73 */     } catch (IOException e) {
/*  74 */       throw new IORuntimeException(e);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*  79 */     conn.header(cookieHeader, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void store(HttpConnection conn) {
/*  88 */     if (null == cookieManager) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/*  94 */       cookieManager.put(getURI(conn), conn.headers());
/*  95 */     } catch (IOException e) {
/*  96 */       throw new IORuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static URI getURI(HttpConnection conn) {
/* 106 */     return URLUtil.toURI(conn.getUrl());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\cookie\GlobalCookieManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */