/*     */ package ch.qos.logback.core.net.ssl;
/*     */ 
/*     */ import ch.qos.logback.core.spi.ContextAwareBase;
/*     */ import ch.qos.logback.core.util.OptionHelper;
/*     */ import ch.qos.logback.core.util.StringCollectionUtil;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
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
/*     */ public class SSLParametersConfiguration
/*     */   extends ContextAwareBase
/*     */ {
/*     */   private String includedProtocols;
/*     */   private String excludedProtocols;
/*     */   private String includedCipherSuites;
/*     */   private String excludedCipherSuites;
/*     */   private Boolean needClientAuth;
/*     */   private Boolean wantClientAuth;
/*     */   private String[] enabledProtocols;
/*     */   private String[] enabledCipherSuites;
/*     */   private Boolean hostnameVerification;
/*     */   
/*     */   public void configure(SSLConfigurable socket) {
/*  51 */     socket.setEnabledProtocols(enabledProtocols(socket.getSupportedProtocols(), socket.getDefaultProtocols()));
/*  52 */     socket.setEnabledCipherSuites(enabledCipherSuites(socket.getSupportedCipherSuites(), socket.getDefaultCipherSuites()));
/*  53 */     if (isNeedClientAuth() != null) {
/*  54 */       socket.setNeedClientAuth(isNeedClientAuth().booleanValue());
/*     */     }
/*  56 */     if (isWantClientAuth() != null) {
/*  57 */       socket.setWantClientAuth(isWantClientAuth().booleanValue());
/*     */     }
/*  59 */     if (this.hostnameVerification != null) {
/*  60 */       addInfo("hostnameVerification=" + this.hostnameVerification);
/*  61 */       socket.setHostnameVerification(this.hostnameVerification.booleanValue());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getHostnameVerification() {
/*  67 */     if (this.hostnameVerification == null)
/*  68 */       return false; 
/*  69 */     return this.hostnameVerification.booleanValue();
/*     */   }
/*     */   
/*     */   public void setHostnameVerification(boolean hostnameVerification) {
/*  73 */     this.hostnameVerification = Boolean.valueOf(hostnameVerification);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String[] enabledProtocols(String[] supportedProtocols, String[] defaultProtocols) {
/*  83 */     if (this.enabledProtocols == null) {
/*     */ 
/*     */       
/*  86 */       if (OptionHelper.isEmpty(getIncludedProtocols()) && OptionHelper.isEmpty(getExcludedProtocols())) {
/*  87 */         this.enabledProtocols = Arrays.<String>copyOf(defaultProtocols, defaultProtocols.length);
/*     */       } else {
/*  89 */         this.enabledProtocols = includedStrings(supportedProtocols, getIncludedProtocols(), getExcludedProtocols());
/*     */       } 
/*  91 */       for (String protocol : this.enabledProtocols) {
/*  92 */         addInfo("enabled protocol: " + protocol);
/*     */       }
/*     */     } 
/*  95 */     return this.enabledProtocols;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String[] enabledCipherSuites(String[] supportedCipherSuites, String[] defaultCipherSuites) {
/* 105 */     if (this.enabledCipherSuites == null) {
/*     */ 
/*     */       
/* 108 */       if (OptionHelper.isEmpty(getIncludedCipherSuites()) && OptionHelper.isEmpty(getExcludedCipherSuites())) {
/* 109 */         this.enabledCipherSuites = Arrays.<String>copyOf(defaultCipherSuites, defaultCipherSuites.length);
/*     */       } else {
/* 111 */         this.enabledCipherSuites = includedStrings(supportedCipherSuites, getIncludedCipherSuites(), getExcludedCipherSuites());
/*     */       } 
/* 113 */       for (String cipherSuite : this.enabledCipherSuites) {
/* 114 */         addInfo("enabled cipher suite: " + cipherSuite);
/*     */       }
/*     */     } 
/* 117 */     return this.enabledCipherSuites;
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
/*     */   private String[] includedStrings(String[] defaults, String included, String excluded) {
/* 131 */     List<String> values = new ArrayList<String>(defaults.length);
/* 132 */     values.addAll(Arrays.asList(defaults));
/* 133 */     if (included != null) {
/* 134 */       StringCollectionUtil.retainMatching(values, stringToArray(included));
/*     */     }
/* 136 */     if (excluded != null) {
/* 137 */       StringCollectionUtil.removeMatching(values, stringToArray(excluded));
/*     */     }
/* 139 */     return values.<String>toArray(new String[values.size()]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String[] stringToArray(String s) {
/* 148 */     return s.split("\\s*,\\s*");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getIncludedProtocols() {
/* 157 */     return this.includedProtocols;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIncludedProtocols(String protocols) {
/* 167 */     this.includedProtocols = protocols;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getExcludedProtocols() {
/* 176 */     return this.excludedProtocols;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setExcludedProtocols(String protocols) {
/* 186 */     this.excludedProtocols = protocols;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getIncludedCipherSuites() {
/* 195 */     return this.includedCipherSuites;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIncludedCipherSuites(String cipherSuites) {
/* 205 */     this.includedCipherSuites = cipherSuites;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getExcludedCipherSuites() {
/* 214 */     return this.excludedCipherSuites;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setExcludedCipherSuites(String cipherSuites) {
/* 224 */     this.excludedCipherSuites = cipherSuites;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Boolean isNeedClientAuth() {
/* 232 */     return this.needClientAuth;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNeedClientAuth(Boolean needClientAuth) {
/* 240 */     this.needClientAuth = needClientAuth;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Boolean isWantClientAuth() {
/* 248 */     return this.wantClientAuth;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWantClientAuth(Boolean wantClientAuth) {
/* 256 */     this.wantClientAuth = wantClientAuth;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\net\ssl\SSLParametersConfiguration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */