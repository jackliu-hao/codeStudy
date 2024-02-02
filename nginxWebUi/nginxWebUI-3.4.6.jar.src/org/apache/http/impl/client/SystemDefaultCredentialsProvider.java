/*     */ package org.apache.http.impl.client;
/*     */ 
/*     */ import java.net.Authenticator;
/*     */ import java.net.PasswordAuthentication;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.auth.AuthScope;
/*     */ import org.apache.http.auth.Credentials;
/*     */ import org.apache.http.auth.NTCredentials;
/*     */ import org.apache.http.auth.UsernamePasswordCredentials;
/*     */ import org.apache.http.client.CredentialsProvider;
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
/*     */ @Contract(threading = ThreadingBehavior.SAFE)
/*     */ public class SystemDefaultCredentialsProvider
/*     */   implements CredentialsProvider
/*     */ {
/*  58 */   private static final Map<String, String> SCHEME_MAP = new ConcurrentHashMap<String, String>(); static {
/*  59 */     SCHEME_MAP.put("Basic".toUpperCase(Locale.ROOT), "Basic");
/*  60 */     SCHEME_MAP.put("Digest".toUpperCase(Locale.ROOT), "Digest");
/*  61 */     SCHEME_MAP.put("NTLM".toUpperCase(Locale.ROOT), "NTLM");
/*  62 */     SCHEME_MAP.put("Negotiate".toUpperCase(Locale.ROOT), "SPNEGO");
/*  63 */     SCHEME_MAP.put("Kerberos".toUpperCase(Locale.ROOT), "Kerberos");
/*     */   }
/*     */   private final BasicCredentialsProvider internal;
/*     */   private static String translateScheme(String key) {
/*  67 */     if (key == null) {
/*  68 */       return null;
/*     */     }
/*  70 */     String s = SCHEME_MAP.get(key);
/*  71 */     return (s != null) ? s : key;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SystemDefaultCredentialsProvider() {
/*  81 */     this.internal = new BasicCredentialsProvider();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCredentials(AuthScope authscope, Credentials credentials) {
/*  86 */     this.internal.setCredentials(authscope, credentials);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static PasswordAuthentication getSystemCreds(String protocol, AuthScope authscope, Authenticator.RequestorType requestorType) {
/*  93 */     return Authenticator.requestPasswordAuthentication(authscope.getHost(), null, authscope.getPort(), protocol, null, translateScheme(authscope.getScheme()), null, requestorType);
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
/*     */   public Credentials getCredentials(AuthScope authscope) {
/* 106 */     Args.notNull(authscope, "Auth scope");
/* 107 */     Credentials localcreds = this.internal.getCredentials(authscope);
/* 108 */     if (localcreds != null) {
/* 109 */       return localcreds;
/*     */     }
/* 111 */     String host = authscope.getHost();
/* 112 */     if (host != null) {
/* 113 */       HttpHost origin = authscope.getOrigin();
/* 114 */       String protocol = (origin != null) ? origin.getSchemeName() : ((authscope.getPort() == 443) ? "https" : "http");
/* 115 */       PasswordAuthentication systemcreds = getSystemCreds(protocol, authscope, Authenticator.RequestorType.SERVER);
/* 116 */       if (systemcreds == null) {
/* 117 */         systemcreds = getSystemCreds(protocol, authscope, Authenticator.RequestorType.PROXY);
/*     */       }
/* 119 */       if (systemcreds == null) {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 124 */         systemcreds = getProxyCredentials("http", authscope);
/* 125 */         if (systemcreds == null) {
/* 126 */           systemcreds = getProxyCredentials("https", authscope);
/*     */         }
/*     */       } 
/* 129 */       if (systemcreds != null) {
/* 130 */         String domain = System.getProperty("http.auth.ntlm.domain");
/* 131 */         if (domain != null) {
/* 132 */           return (Credentials)new NTCredentials(systemcreds.getUserName(), new String(systemcreds.getPassword()), null, domain);
/*     */         }
/*     */ 
/*     */ 
/*     */         
/* 137 */         return "NTLM".equalsIgnoreCase(authscope.getScheme()) ? (Credentials)new NTCredentials(systemcreds.getUserName(), new String(systemcreds.getPassword()), null, null) : (Credentials)new UsernamePasswordCredentials(systemcreds.getUserName(), new String(systemcreds.getPassword()));
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 145 */     return null;
/*     */   }
/*     */   
/*     */   private static PasswordAuthentication getProxyCredentials(String protocol, AuthScope authscope) {
/* 149 */     String proxyHost = System.getProperty(protocol + ".proxyHost");
/* 150 */     if (proxyHost == null) {
/* 151 */       return null;
/*     */     }
/* 153 */     String proxyPort = System.getProperty(protocol + ".proxyPort");
/* 154 */     if (proxyPort == null) {
/* 155 */       return null;
/*     */     }
/*     */     
/*     */     try {
/* 159 */       AuthScope systemScope = new AuthScope(proxyHost, Integer.parseInt(proxyPort));
/* 160 */       if (authscope.match(systemScope) >= 0) {
/* 161 */         String proxyUser = System.getProperty(protocol + ".proxyUser");
/* 162 */         if (proxyUser == null) {
/* 163 */           return null;
/*     */         }
/* 165 */         String proxyPassword = System.getProperty(protocol + ".proxyPassword");
/*     */         
/* 167 */         return new PasswordAuthentication(proxyUser, (proxyPassword != null) ? proxyPassword.toCharArray() : new char[0]);
/*     */       }
/*     */     
/* 170 */     } catch (NumberFormatException ex) {}
/*     */ 
/*     */     
/* 173 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/* 178 */     this.internal.clear();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\SystemDefaultCredentialsProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */