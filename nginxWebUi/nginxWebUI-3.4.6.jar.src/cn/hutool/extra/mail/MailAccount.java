/*     */ package cn.hutool.extra.mail;
/*     */ 
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.setting.Setting;
/*     */ import java.io.Serializable;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
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
/*     */ public class MailAccount
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -6937313421815719204L;
/*     */   private static final String MAIL_PROTOCOL = "mail.transport.protocol";
/*     */   private static final String SMTP_HOST = "mail.smtp.host";
/*     */   private static final String SMTP_PORT = "mail.smtp.port";
/*     */   private static final String SMTP_AUTH = "mail.smtp.auth";
/*     */   private static final String SMTP_TIMEOUT = "mail.smtp.timeout";
/*     */   private static final String SMTP_CONNECTION_TIMEOUT = "mail.smtp.connectiontimeout";
/*     */   private static final String SMTP_WRITE_TIMEOUT = "mail.smtp.writetimeout";
/*     */   private static final String STARTTLS_ENABLE = "mail.smtp.starttls.enable";
/*     */   private static final String SSL_ENABLE = "mail.smtp.ssl.enable";
/*     */   private static final String SSL_PROTOCOLS = "mail.smtp.ssl.protocols";
/*     */   private static final String SOCKET_FACTORY = "mail.smtp.socketFactory.class";
/*     */   private static final String SOCKET_FACTORY_FALLBACK = "mail.smtp.socketFactory.fallback";
/*     */   private static final String SOCKET_FACTORY_PORT = "smtp.socketFactory.port";
/*     */   private static final String SPLIT_LONG_PARAMS = "mail.mime.splitlongparameters";
/*     */   private static final String MAIL_DEBUG = "mail.debug";
/*  46 */   public static final String[] MAIL_SETTING_PATHS = new String[] { "config/mail.setting", "config/mailAccount.setting", "mail.setting" };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String host;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Integer port;
/*     */ 
/*     */ 
/*     */   
/*     */   private Boolean auth;
/*     */ 
/*     */ 
/*     */   
/*     */   private String user;
/*     */ 
/*     */ 
/*     */   
/*     */   private String pass;
/*     */ 
/*     */ 
/*     */   
/*     */   private String from;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean debug;
/*     */ 
/*     */ 
/*     */   
/*  80 */   private Charset charset = CharsetUtil.CHARSET_UTF_8;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean splitlongparameters = false;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean encodefilename = true;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean starttlsEnable = false;
/*     */ 
/*     */ 
/*     */   
/*     */   private Boolean sslEnable;
/*     */ 
/*     */ 
/*     */   
/*     */   private String sslProtocols;
/*     */ 
/*     */ 
/*     */   
/* 107 */   private String socketFactoryClass = "javax.net.ssl.SSLSocketFactory";
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean socketFactoryFallback;
/*     */ 
/*     */ 
/*     */   
/* 115 */   private int socketFactoryPort = 465;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private long timeout;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private long connectionTimeout;
/*     */ 
/*     */ 
/*     */   
/*     */   private long writeTimeout;
/*     */ 
/*     */ 
/*     */   
/* 133 */   private final Map<String, Object> customProperty = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MailAccount() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MailAccount(String settingPath) {
/* 149 */     this(new Setting(settingPath));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MailAccount(Setting setting) {
/* 158 */     setting.toBean(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getHost() {
/* 169 */     return this.host;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MailAccount setHost(String host) {
/* 179 */     this.host = host;
/* 180 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Integer getPort() {
/* 189 */     return this.port;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MailAccount setPort(Integer port) {
/* 199 */     this.port = port;
/* 200 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Boolean isAuth() {
/* 209 */     return this.auth;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MailAccount setAuth(boolean isAuth) {
/* 219 */     this.auth = Boolean.valueOf(isAuth);
/* 220 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getUser() {
/* 229 */     return this.user;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MailAccount setUser(String user) {
/* 239 */     this.user = user;
/* 240 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPass() {
/* 249 */     return this.pass;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MailAccount setPass(String pass) {
/* 259 */     this.pass = pass;
/* 260 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getFrom() {
/* 269 */     return this.from;
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
/*     */   public MailAccount setFrom(String from) {
/* 285 */     this.from = from;
/* 286 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDebug() {
/* 296 */     return this.debug;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MailAccount setDebug(boolean debug) {
/* 307 */     this.debug = debug;
/* 308 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Charset getCharset() {
/* 317 */     return this.charset;
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
/*     */   public MailAccount setCharset(Charset charset) {
/* 330 */     this.charset = charset;
/* 331 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSplitlongparameters() {
/* 340 */     return this.splitlongparameters;
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
/*     */   public void setSplitlongparameters(boolean splitlongparameters) {
/* 353 */     this.splitlongparameters = splitlongparameters;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEncodefilename() {
/* 364 */     return this.encodefilename;
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
/*     */   public void setEncodefilename(boolean encodefilename) {
/* 379 */     this.encodefilename = encodefilename;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isStarttlsEnable() {
/* 388 */     return this.starttlsEnable;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MailAccount setStarttlsEnable(boolean startttlsEnable) {
/* 398 */     this.starttlsEnable = startttlsEnable;
/* 399 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Boolean isSslEnable() {
/* 408 */     return this.sslEnable;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MailAccount setSslEnable(Boolean sslEnable) {
/* 418 */     this.sslEnable = sslEnable;
/* 419 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getSslProtocols() {
/* 429 */     return this.sslProtocols;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSslProtocols(String sslProtocols) {
/* 439 */     this.sslProtocols = sslProtocols;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getSocketFactoryClass() {
/* 448 */     return this.socketFactoryClass;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MailAccount setSocketFactoryClass(String socketFactoryClass) {
/* 458 */     this.socketFactoryClass = socketFactoryClass;
/* 459 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSocketFactoryFallback() {
/* 468 */     return this.socketFactoryFallback;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MailAccount setSocketFactoryFallback(boolean socketFactoryFallback) {
/* 478 */     this.socketFactoryFallback = socketFactoryFallback;
/* 479 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSocketFactoryPort() {
/* 488 */     return this.socketFactoryPort;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MailAccount setSocketFactoryPort(int socketFactoryPort) {
/* 498 */     this.socketFactoryPort = socketFactoryPort;
/* 499 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MailAccount setTimeout(long timeout) {
/* 510 */     this.timeout = timeout;
/* 511 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MailAccount setConnectionTimeout(long connectionTimeout) {
/* 522 */     this.connectionTimeout = connectionTimeout;
/* 523 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MailAccount setWriteTimeout(long writeTimeout) {
/* 534 */     this.writeTimeout = writeTimeout;
/* 535 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, Object> getCustomProperty() {
/* 545 */     return this.customProperty;
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
/*     */   public MailAccount setCustomProperty(String key, Object value) {
/* 557 */     if (StrUtil.isNotBlank(key) && ObjectUtil.isNotNull(value)) {
/* 558 */       this.customProperty.put(key, value);
/*     */     }
/* 560 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Properties getSmtpProps() {
/* 570 */     System.setProperty("mail.mime.splitlongparameters", String.valueOf(this.splitlongparameters));
/*     */     
/* 572 */     Properties p = new Properties();
/* 573 */     p.put("mail.transport.protocol", "smtp");
/* 574 */     p.put("mail.smtp.host", this.host);
/* 575 */     p.put("mail.smtp.port", String.valueOf(this.port));
/* 576 */     p.put("mail.smtp.auth", String.valueOf(this.auth));
/* 577 */     if (this.timeout > 0L) {
/* 578 */       p.put("mail.smtp.timeout", String.valueOf(this.timeout));
/*     */     }
/* 580 */     if (this.connectionTimeout > 0L) {
/* 581 */       p.put("mail.smtp.connectiontimeout", String.valueOf(this.connectionTimeout));
/*     */     }
/*     */     
/* 584 */     if (this.writeTimeout > 0L) {
/* 585 */       p.put("mail.smtp.writetimeout", String.valueOf(this.writeTimeout));
/*     */     }
/*     */     
/* 588 */     p.put("mail.debug", String.valueOf(this.debug));
/*     */     
/* 590 */     if (this.starttlsEnable) {
/*     */       
/* 592 */       p.put("mail.smtp.starttls.enable", "true");
/*     */       
/* 594 */       if (null == this.sslEnable)
/*     */       {
/* 596 */         this.sslEnable = Boolean.valueOf(true);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 601 */     if (null != this.sslEnable && this.sslEnable.booleanValue()) {
/* 602 */       p.put("mail.smtp.ssl.enable", "true");
/* 603 */       p.put("mail.smtp.socketFactory.class", this.socketFactoryClass);
/* 604 */       p.put("mail.smtp.socketFactory.fallback", String.valueOf(this.socketFactoryFallback));
/* 605 */       p.put("smtp.socketFactory.port", String.valueOf(this.socketFactoryPort));
/*     */       
/* 607 */       if (StrUtil.isNotBlank(this.sslProtocols)) {
/* 608 */         p.put("mail.smtp.ssl.protocols", this.sslProtocols);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 613 */     p.putAll(this.customProperty);
/*     */     
/* 615 */     return p;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MailAccount defaultIfEmpty() {
/* 625 */     String fromAddress = InternalMailUtil.parseFirstAddress(this.from, this.charset).getAddress();
/*     */     
/* 627 */     if (StrUtil.isBlank(this.host))
/*     */     {
/* 629 */       this.host = StrUtil.format("smtp.{}", new Object[] { StrUtil.subSuf(fromAddress, fromAddress.indexOf('@') + 1) });
/*     */     }
/* 631 */     if (StrUtil.isBlank(this.user))
/*     */     {
/*     */       
/* 634 */       this.user = fromAddress;
/*     */     }
/* 636 */     if (null == this.auth)
/*     */     {
/* 638 */       this.auth = Boolean.valueOf((false == StrUtil.isBlank(this.pass)));
/*     */     }
/* 640 */     if (null == this.port)
/*     */     {
/* 642 */       this.port = Integer.valueOf((null != this.sslEnable && this.sslEnable.booleanValue()) ? this.socketFactoryPort : 25);
/*     */     }
/* 644 */     if (null == this.charset)
/*     */     {
/* 646 */       this.charset = CharsetUtil.CHARSET_UTF_8;
/*     */     }
/*     */     
/* 649 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 654 */     return "MailAccount [host=" + this.host + ", port=" + this.port + ", auth=" + this.auth + ", user=" + this.user + ", pass=" + (StrUtil.isEmpty(this.pass) ? "" : "******") + ", from=" + this.from + ", startttlsEnable=" + this.starttlsEnable + ", socketFactoryClass=" + this.socketFactoryClass + ", socketFactoryFallback=" + this.socketFactoryFallback + ", socketFactoryPort=" + this.socketFactoryPort + "]";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\mail\MailAccount.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */