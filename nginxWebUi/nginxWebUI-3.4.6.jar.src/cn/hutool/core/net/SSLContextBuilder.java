/*     */ package cn.hutool.core.net;
/*     */ 
/*     */ import cn.hutool.core.builder.Builder;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.security.GeneralSecurityException;
/*     */ import java.security.KeyManagementException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.SecureRandom;
/*     */ import javax.net.ssl.KeyManager;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import javax.net.ssl.TrustManager;
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
/*     */ public class SSLContextBuilder
/*     */   implements SSLProtocols, Builder<SSLContext>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  33 */   private String protocol = "TLS";
/*     */   private KeyManager[] keyManagers;
/*  35 */   private TrustManager[] trustManagers = new TrustManager[] { DefaultTrustManager.INSTANCE };
/*  36 */   private SecureRandom secureRandom = new SecureRandom();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SSLContextBuilder create() {
/*  45 */     return new SSLContextBuilder();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SSLContextBuilder setProtocol(String protocol) {
/*  55 */     if (StrUtil.isNotBlank(protocol)) {
/*  56 */       this.protocol = protocol;
/*     */     }
/*  58 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SSLContextBuilder setTrustManagers(TrustManager... trustManagers) {
/*  68 */     if (ArrayUtil.isNotEmpty((Object[])trustManagers)) {
/*  69 */       this.trustManagers = trustManagers;
/*     */     }
/*  71 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SSLContextBuilder setKeyManagers(KeyManager... keyManagers) {
/*  81 */     if (ArrayUtil.isNotEmpty((Object[])keyManagers)) {
/*  82 */       this.keyManagers = keyManagers;
/*     */     }
/*  84 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SSLContextBuilder setSecureRandom(SecureRandom secureRandom) {
/*  94 */     if (null != secureRandom) {
/*  95 */       this.secureRandom = secureRandom;
/*     */     }
/*  97 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SSLContext build() {
/* 107 */     return buildQuietly();
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
/*     */   public SSLContext buildChecked() throws NoSuchAlgorithmException, KeyManagementException {
/* 119 */     SSLContext sslContext = SSLContext.getInstance(this.protocol);
/* 120 */     sslContext.init(this.keyManagers, this.trustManagers, this.secureRandom);
/* 121 */     return sslContext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SSLContext buildQuietly() throws IORuntimeException {
/*     */     try {
/* 132 */       return buildChecked();
/* 133 */     } catch (GeneralSecurityException e) {
/* 134 */       throw new IORuntimeException(e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\net\SSLContextBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */