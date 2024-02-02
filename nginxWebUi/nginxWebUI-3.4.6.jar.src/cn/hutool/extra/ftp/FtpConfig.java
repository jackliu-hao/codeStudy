/*     */ package cn.hutool.extra.ftp;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.nio.charset.Charset;
/*     */ 
/*     */ public class FtpConfig
/*     */   implements Serializable {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private String host;
/*     */   private int port;
/*     */   private String user;
/*     */   private String password;
/*     */   
/*     */   public static FtpConfig create() {
/*  15 */     return new FtpConfig();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Charset charset;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private long connectionTimeout;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private long soTimeout;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String serverLanguageCode;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String systemKey;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FtpConfig() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FtpConfig(String host, int port, String user, String password, Charset charset) {
/*  75 */     this(host, port, user, password, charset, null, null);
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
/*     */   
/*     */   public FtpConfig(String host, int port, String user, String password, Charset charset, String serverLanguageCode, String systemKey) {
/*  91 */     this.host = host;
/*  92 */     this.port = port;
/*  93 */     this.user = user;
/*  94 */     this.password = password;
/*  95 */     this.charset = charset;
/*  96 */     this.serverLanguageCode = serverLanguageCode;
/*  97 */     this.systemKey = systemKey;
/*     */   }
/*     */   
/*     */   public String getHost() {
/* 101 */     return this.host;
/*     */   }
/*     */   
/*     */   public FtpConfig setHost(String host) {
/* 105 */     this.host = host;
/* 106 */     return this;
/*     */   }
/*     */   
/*     */   public int getPort() {
/* 110 */     return this.port;
/*     */   }
/*     */   
/*     */   public FtpConfig setPort(int port) {
/* 114 */     this.port = port;
/* 115 */     return this;
/*     */   }
/*     */   
/*     */   public String getUser() {
/* 119 */     return this.user;
/*     */   }
/*     */   
/*     */   public FtpConfig setUser(String user) {
/* 123 */     this.user = user;
/* 124 */     return this;
/*     */   }
/*     */   
/*     */   public String getPassword() {
/* 128 */     return this.password;
/*     */   }
/*     */   
/*     */   public FtpConfig setPassword(String password) {
/* 132 */     this.password = password;
/* 133 */     return this;
/*     */   }
/*     */   
/*     */   public Charset getCharset() {
/* 137 */     return this.charset;
/*     */   }
/*     */   
/*     */   public FtpConfig setCharset(Charset charset) {
/* 141 */     this.charset = charset;
/* 142 */     return this;
/*     */   }
/*     */   
/*     */   public long getConnectionTimeout() {
/* 146 */     return this.connectionTimeout;
/*     */   }
/*     */   
/*     */   public FtpConfig setConnectionTimeout(long connectionTimeout) {
/* 150 */     this.connectionTimeout = connectionTimeout;
/* 151 */     return this;
/*     */   }
/*     */   
/*     */   public long getSoTimeout() {
/* 155 */     return this.soTimeout;
/*     */   }
/*     */   
/*     */   public FtpConfig setSoTimeout(long soTimeout) {
/* 159 */     this.soTimeout = soTimeout;
/* 160 */     return this;
/*     */   }
/*     */   
/*     */   public String getServerLanguageCode() {
/* 164 */     return this.serverLanguageCode;
/*     */   }
/*     */   
/*     */   public FtpConfig setServerLanguageCode(String serverLanguageCode) {
/* 168 */     this.serverLanguageCode = serverLanguageCode;
/* 169 */     return this;
/*     */   }
/*     */   
/*     */   public String getSystemKey() {
/* 173 */     return this.systemKey;
/*     */   }
/*     */   
/*     */   public FtpConfig setSystemKey(String systemKey) {
/* 177 */     this.systemKey = systemKey;
/* 178 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\ftp\FtpConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */