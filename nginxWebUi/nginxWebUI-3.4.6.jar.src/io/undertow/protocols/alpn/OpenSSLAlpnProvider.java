/*     */ package io.undertow.protocols.alpn;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import java.lang.reflect.Method;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import javax.net.ssl.SSLEngine;
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
/*     */ public class OpenSSLAlpnProvider
/*     */   implements ALPNProvider
/*     */ {
/*     */   private static volatile OpenSSLALPNMethods openSSLALPNMethods;
/*     */   private static volatile boolean initialized;
/*     */   public static final String OPENSSL_ENGINE = "org.wildfly.openssl.OpenSSLEngine";
/*     */   
/*     */   public static class OpenSSLALPNMethods
/*     */   {
/*     */     private final Method setApplicationProtocols;
/*     */     private final Method getApplicationProtocol;
/*     */     
/*     */     OpenSSLALPNMethods(Method setApplicationProtocols, Method getApplicationProtocol) {
/*  49 */       this.setApplicationProtocols = setApplicationProtocols;
/*  50 */       this.getApplicationProtocol = getApplicationProtocol;
/*     */     }
/*     */     
/*     */     public Method getApplicationProtocol() {
/*  54 */       return this.getApplicationProtocol;
/*     */     }
/*     */     
/*     */     public Method setApplicationProtocols() {
/*  58 */       return this.setApplicationProtocols;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEnabled(SSLEngine sslEngine) {
/*  64 */     return (sslEngine.getClass().getName().equals("org.wildfly.openssl.OpenSSLEngine") && getOpenSSLAlpnMethods() != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLEngine setProtocols(SSLEngine engine, String[] protocols) {
/*     */     try {
/*  70 */       getOpenSSLAlpnMethods().setApplicationProtocols().invoke(engine, new Object[] { protocols });
/*  71 */     } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
/*  72 */       throw new RuntimeException(e);
/*     */     } 
/*  74 */     return engine;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSelectedProtocol(SSLEngine engine) {
/*     */     try {
/*  80 */       return (String)getOpenSSLAlpnMethods().getApplicationProtocol().invoke(engine, new Object[0]);
/*  81 */     } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
/*  82 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static OpenSSLALPNMethods getOpenSSLAlpnMethods() {
/*  87 */     if (!initialized) {
/*  88 */       synchronized (OpenSSLAlpnProvider.class) {
/*  89 */         if (!initialized) {
/*  90 */           openSSLALPNMethods = AccessController.<OpenSSLALPNMethods>doPrivileged(new PrivilegedAction<OpenSSLALPNMethods>()
/*     */               {
/*     */                 public OpenSSLAlpnProvider.OpenSSLALPNMethods run() {
/*     */                   try {
/*  94 */                     Class<?> openSSLEngine = Class.forName("org.wildfly.openssl.OpenSSLEngine", true, OpenSSLAlpnProvider.class.getClassLoader());
/*  95 */                     Method setApplicationProtocols = openSSLEngine.getMethod("setApplicationProtocols", new Class[] { String[].class });
/*  96 */                     Method getApplicationProtocol = openSSLEngine.getMethod("getSelectedApplicationProtocol", new Class[0]);
/*  97 */                     UndertowLogger.ROOT_LOGGER.debug("OpenSSL ALPN Enabled");
/*  98 */                     return new OpenSSLAlpnProvider.OpenSSLALPNMethods(setApplicationProtocols, getApplicationProtocol);
/*  99 */                   } catch (Throwable e) {
/* 100 */                     UndertowLogger.ROOT_LOGGER.debug("OpenSSL ALPN disabled", e);
/* 101 */                     return null;
/*     */                   } 
/*     */                 }
/*     */               });
/* 105 */           initialized = true;
/*     */         } 
/*     */       } 
/*     */     }
/* 109 */     return openSSLALPNMethods;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getPriority() {
/* 114 */     return 400;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 119 */     return "OpenSSLAlpnProvider";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\alpn\OpenSSLAlpnProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */