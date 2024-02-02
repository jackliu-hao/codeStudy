/*     */ package cn.hutool.db.ds.pooled;
/*     */ 
/*     */ import cn.hutool.db.DbRuntimeException;
/*     */ import cn.hutool.db.dialect.DriverUtil;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DbConfig
/*     */ {
/*     */   private String driver;
/*     */   private String url;
/*     */   private String user;
/*     */   private String pass;
/*     */   private int initialSize;
/*     */   private int minIdle;
/*     */   private int maxActive;
/*     */   private long maxWait;
/*     */   private Properties connProps;
/*     */   
/*     */   public DbConfig() {}
/*     */   
/*     */   public DbConfig(String url, String user, String pass) {
/*  42 */     init(url, user, pass);
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
/*     */   public void init(String url, String user, String pass) {
/*  54 */     this.url = url;
/*  55 */     this.user = user;
/*  56 */     this.pass = pass;
/*  57 */     this.driver = DriverUtil.identifyDriver(url);
/*     */     try {
/*  59 */       Class.forName(this.driver);
/*  60 */     } catch (ClassNotFoundException e) {
/*  61 */       throw new DbRuntimeException(e, "Get jdbc driver from [{}] error!", new Object[] { url });
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDriver() {
/*  67 */     return this.driver;
/*     */   }
/*     */   
/*     */   public void setDriver(String driver) {
/*  71 */     this.driver = driver;
/*     */   }
/*     */   
/*     */   public String getUrl() {
/*  75 */     return this.url;
/*     */   }
/*     */   
/*     */   public void setUrl(String url) {
/*  79 */     this.url = url;
/*     */   }
/*     */   
/*     */   public String getUser() {
/*  83 */     return this.user;
/*     */   }
/*     */   
/*     */   public void setUser(String user) {
/*  87 */     this.user = user;
/*     */   }
/*     */   
/*     */   public String getPass() {
/*  91 */     return this.pass;
/*     */   }
/*     */   
/*     */   public void setPass(String pass) {
/*  95 */     this.pass = pass;
/*     */   }
/*     */   
/*     */   public int getInitialSize() {
/*  99 */     return this.initialSize;
/*     */   }
/*     */   
/*     */   public void setInitialSize(int initialSize) {
/* 103 */     this.initialSize = initialSize;
/*     */   }
/*     */   
/*     */   public int getMinIdle() {
/* 107 */     return this.minIdle;
/*     */   }
/*     */   
/*     */   public void setMinIdle(int minIdle) {
/* 111 */     this.minIdle = minIdle;
/*     */   }
/*     */   
/*     */   public int getMaxActive() {
/* 115 */     return this.maxActive;
/*     */   }
/*     */   
/*     */   public void setMaxActive(int maxActive) {
/* 119 */     this.maxActive = maxActive;
/*     */   }
/*     */   
/*     */   public long getMaxWait() {
/* 123 */     return this.maxWait;
/*     */   }
/*     */   
/*     */   public void setMaxWait(long maxWait) {
/* 127 */     this.maxWait = maxWait;
/*     */   }
/*     */   
/*     */   public Properties getConnProps() {
/* 131 */     return this.connProps;
/*     */   }
/*     */   
/*     */   public void setConnProps(Properties connProps) {
/* 135 */     this.connProps = connProps;
/*     */   }
/*     */   
/*     */   public void addConnProps(String key, String value) {
/* 139 */     if (null == this.connProps) {
/* 140 */       this.connProps = new Properties();
/*     */     }
/* 142 */     this.connProps.setProperty(key, value);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\ds\pooled\DbConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */