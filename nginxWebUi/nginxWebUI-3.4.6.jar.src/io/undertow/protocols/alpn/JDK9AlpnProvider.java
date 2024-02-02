/*     */ package io.undertow.protocols.alpn;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import java.lang.reflect.Method;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import javax.net.ssl.SSLEngine;
/*     */ import javax.net.ssl.SSLParameters;
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
/*     */ public class JDK9AlpnProvider
/*     */   implements ALPNProvider
/*     */ {
/*  46 */   public static final JDK9ALPNMethods JDK_9_ALPN_METHODS = AccessController.<JDK9ALPNMethods>doPrivileged(new PrivilegedAction<JDK9ALPNMethods>()
/*     */       {
/*     */         public JDK9AlpnProvider.JDK9ALPNMethods run() {
/*     */           try {
/*  50 */             String javaVersion = System.getProperty("java.specification.version");
/*  51 */             int vmVersion = 8;
/*     */             try {
/*  53 */               Matcher matcher = Pattern.compile("^(?:1\\.)?(\\d+)$").matcher(javaVersion);
/*  54 */               if (matcher.find()) {
/*  55 */                 vmVersion = Integer.parseInt(matcher.group(1));
/*     */               }
/*  57 */             } catch (Exception exception) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/*  65 */             String value = System.getProperty("io.undertow.protocols.alpn.jdk8");
/*  66 */             boolean addSupportIfExists = (value == null || value.trim().isEmpty() || Boolean.parseBoolean(value));
/*  67 */             if (vmVersion > 8 || addSupportIfExists) {
/*  68 */               Method setApplicationProtocols = SSLParameters.class.getMethod("setApplicationProtocols", new Class[] { String[].class });
/*  69 */               Method getApplicationProtocol = SSLEngine.class.getMethod("getApplicationProtocol", new Class[0]);
/*  70 */               UndertowLogger.ROOT_LOGGER.debug("Using JDK9 ALPN");
/*  71 */               return new JDK9AlpnProvider.JDK9ALPNMethods(setApplicationProtocols, getApplicationProtocol);
/*     */             } 
/*  73 */             UndertowLogger.ROOT_LOGGER.debugf("It's not certain ALPN support was found. Provider %s will be disabled.", JDK9AlpnProvider.class
/*  74 */                 .getName());
/*  75 */             return null;
/*  76 */           } catch (Exception e) {
/*  77 */             UndertowLogger.ROOT_LOGGER.debug("JDK9 ALPN not supported");
/*  78 */             return null;
/*     */           } 
/*     */         }
/*     */       });
/*     */   
/*     */   private static final String JDK8_SUPPORT_PROPERTY = "io.undertow.protocols.alpn.jdk8";
/*     */   
/*     */   public static class JDK9ALPNMethods {
/*     */     private final Method setApplicationProtocols;
/*     */     
/*     */     JDK9ALPNMethods(Method setApplicationProtocols, Method getApplicationProtocol) {
/*  89 */       this.setApplicationProtocols = setApplicationProtocols;
/*  90 */       this.getApplicationProtocol = getApplicationProtocol;
/*     */     }
/*     */     private final Method getApplicationProtocol;
/*     */     public Method getApplicationProtocol() {
/*  94 */       return this.getApplicationProtocol;
/*     */     }
/*     */     
/*     */     public Method setApplicationProtocols() {
/*  98 */       return this.setApplicationProtocols;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEnabled(SSLEngine sslEngine) {
/* 104 */     return (JDK_9_ALPN_METHODS != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLEngine setProtocols(SSLEngine engine, String[] protocols) {
/* 109 */     SSLParameters sslParameters = engine.getSSLParameters();
/*     */     try {
/* 111 */       JDK_9_ALPN_METHODS.setApplicationProtocols().invoke(sslParameters, new Object[] { protocols });
/* 112 */     } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
/* 113 */       throw new RuntimeException(e);
/*     */     } 
/* 115 */     engine.setSSLParameters(sslParameters);
/* 116 */     return engine;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSelectedProtocol(SSLEngine engine) {
/*     */     try {
/* 122 */       return (String)JDK_9_ALPN_METHODS.getApplicationProtocol().invoke(engine, new Object[0]);
/* 123 */     } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
/* 124 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int getPriority() {
/* 130 */     return 200;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 135 */     return "JDK9AlpnProvider";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\alpn\JDK9AlpnProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */