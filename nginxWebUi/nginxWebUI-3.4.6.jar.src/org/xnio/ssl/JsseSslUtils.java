/*     */ package org.xnio.ssl;
/*     */ 
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.security.KeyManagementException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.NoSuchProviderException;
/*     */ import java.security.SecureRandom;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.net.ssl.KeyManager;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import javax.net.ssl.SSLEngine;
/*     */ import javax.net.ssl.TrustManager;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.Options;
/*     */ import org.xnio.Sequence;
/*     */ import org.xnio._private.Messages;
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
/*     */ public final class JsseSslUtils
/*     */ {
/*     */   public static SSLContext createSSLContext(OptionMap optionMap) throws NoSuchProviderException, NoSuchAlgorithmException, KeyManagementException {
/*  65 */     return createSSLContext(null, null, null, optionMap);
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
/*     */   public static SSLContext createSSLContext(KeyManager[] keyManagers, TrustManager[] trustManagers, SecureRandom secureRandom, OptionMap optionMap) throws NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
/*     */     SSLContext sslContext;
/*  81 */     String provider = (String)optionMap.get(Options.SSL_PROVIDER);
/*  82 */     String protocol = (String)optionMap.get(Options.SSL_PROTOCOL);
/*     */     
/*  84 */     if (protocol == null)
/*     */     {
/*  86 */       return SSLContext.getDefault(); } 
/*  87 */     if (provider == null) {
/*  88 */       sslContext = SSLContext.getInstance(protocol);
/*     */     } else {
/*  90 */       sslContext = SSLContext.getInstance(protocol, provider);
/*     */     } 
/*  92 */     if (keyManagers == null) {
/*  93 */       Sequence<Class<? extends KeyManager>> keyManagerClasses = (Sequence<Class<? extends KeyManager>>)optionMap.get(Options.SSL_JSSE_KEY_MANAGER_CLASSES);
/*  94 */       if (keyManagerClasses != null) {
/*  95 */         int size = keyManagerClasses.size();
/*  96 */         keyManagers = new KeyManager[size];
/*  97 */         for (int i = 0; i < size; i++) {
/*  98 */           keyManagers[i] = instantiate((Class<KeyManager>)keyManagerClasses.get(i));
/*     */         }
/*     */       } 
/*     */     } 
/* 102 */     if (trustManagers == null) {
/* 103 */       Sequence<Class<? extends TrustManager>> trustManagerClasses = (Sequence<Class<? extends TrustManager>>)optionMap.get(Options.SSL_JSSE_TRUST_MANAGER_CLASSES);
/* 104 */       if (trustManagerClasses != null) {
/* 105 */         int size = trustManagerClasses.size();
/* 106 */         trustManagers = new TrustManager[size];
/* 107 */         for (int i = 0; i < size; i++) {
/* 108 */           trustManagers[i] = instantiate((Class<TrustManager>)trustManagerClasses.get(i));
/*     */         }
/*     */       } 
/*     */     } 
/* 112 */     sslContext.init(keyManagers, trustManagers, secureRandom);
/* 113 */     sslContext.getClientSessionContext().setSessionCacheSize(optionMap.get(Options.SSL_CLIENT_SESSION_CACHE_SIZE, 0));
/* 114 */     sslContext.getClientSessionContext().setSessionTimeout(optionMap.get(Options.SSL_CLIENT_SESSION_TIMEOUT, 0));
/* 115 */     sslContext.getServerSessionContext().setSessionCacheSize(optionMap.get(Options.SSL_SERVER_SESSION_CACHE_SIZE, 0));
/* 116 */     sslContext.getServerSessionContext().setSessionTimeout(optionMap.get(Options.SSL_SERVER_SESSION_TIMEOUT, 0));
/* 117 */     return sslContext;
/*     */   }
/*     */ 
/*     */   
/*     */   private static <T> T instantiate(Class<T> clazz) {
/*     */     try {
/* 123 */       return clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
/* 124 */     } catch (InstantiationException e) {
/* 125 */       throw Messages.msg.cantInstantiate(clazz, e);
/* 126 */     } catch (IllegalAccessException e) {
/* 127 */       throw Messages.msg.cantInstantiate(clazz, e);
/* 128 */     } catch (NoSuchMethodException e) {
/* 129 */       throw Messages.msg.cantInstantiate(clazz, e);
/* 130 */     } catch (InvocationTargetException e) {
/* 131 */       throw Messages.msg.cantInstantiate(clazz, e.getCause());
/*     */     } 
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
/*     */   public static SSLEngine createSSLEngine(SSLContext sslContext, OptionMap optionMap, InetSocketAddress peerAddress) {
/* 144 */     SSLEngine engine = sslContext.createSSLEngine((String)optionMap
/* 145 */         .get(Options.SSL_PEER_HOST_NAME, peerAddress.getHostString()), optionMap
/* 146 */         .get(Options.SSL_PEER_PORT, peerAddress.getPort()));
/*     */     
/* 148 */     engine.setUseClientMode(true);
/* 149 */     engine.setEnableSessionCreation(optionMap.get(Options.SSL_ENABLE_SESSION_CREATION, true));
/* 150 */     Sequence<String> cipherSuites = (Sequence<String>)optionMap.get(Options.SSL_ENABLED_CIPHER_SUITES);
/* 151 */     if (cipherSuites != null) {
/* 152 */       Set<String> supported = new HashSet<>(Arrays.asList(engine.getSupportedCipherSuites()));
/* 153 */       List<String> finalList = new ArrayList<>();
/* 154 */       for (String name : cipherSuites) {
/* 155 */         if (supported.contains(name)) {
/* 156 */           finalList.add(name);
/*     */         }
/*     */       } 
/* 159 */       engine.setEnabledCipherSuites(finalList.<String>toArray(new String[finalList.size()]));
/*     */     } 
/* 161 */     Sequence<String> protocols = (Sequence<String>)optionMap.get(Options.SSL_ENABLED_PROTOCOLS);
/* 162 */     if (protocols != null) {
/* 163 */       Set<String> supported = new HashSet<>(Arrays.asList(engine.getSupportedProtocols()));
/* 164 */       List<String> finalList = new ArrayList<>();
/* 165 */       for (String name : protocols) {
/* 166 */         if (supported.contains(name)) {
/* 167 */           finalList.add(name);
/*     */         }
/*     */       } 
/* 170 */       engine.setEnabledProtocols(finalList.<String>toArray(new String[finalList.size()]));
/*     */     } 
/* 172 */     return engine;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\ssl\JsseSslUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */