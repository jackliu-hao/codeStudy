/*     */ package cn.hutool.db.ds.simple;
/*     */ 
/*     */ import cn.hutool.core.map.MapUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.db.DbRuntimeException;
/*     */ import cn.hutool.db.dialect.DriverUtil;
/*     */ import cn.hutool.db.ds.DSFactory;
/*     */ import cn.hutool.setting.Setting;
/*     */ import cn.hutool.setting.dialect.Props;
/*     */ import java.sql.Connection;
/*     */ import java.sql.DriverManager;
/*     */ import java.sql.SQLException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SimpleDataSource
/*     */   extends AbstractDataSource
/*     */ {
/*     */   public static final String DEFAULT_DB_CONFIG_PATH = "config/db.setting";
/*     */   private String driver;
/*     */   private String url;
/*     */   private String user;
/*     */   private String pass;
/*     */   private Properties connProps;
/*     */   
/*     */   public static synchronized SimpleDataSource getDataSource(String group) {
/*  44 */     return new SimpleDataSource(group);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized SimpleDataSource getDataSource() {
/*  53 */     return new SimpleDataSource();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleDataSource() {
/*  61 */     this(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleDataSource(String group) {
/*  70 */     this(null, group);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleDataSource(Setting setting, String group) {
/*  80 */     if (null == setting) {
/*  81 */       setting = new Setting("config/db.setting");
/*     */     }
/*  83 */     Setting config = setting.getSetting(group);
/*  84 */     if (MapUtil.isEmpty((Map)config)) {
/*  85 */       throw new DbRuntimeException("No DataSource config for group: [{}]", new Object[] { group });
/*     */     }
/*     */     
/*  88 */     init(config
/*  89 */         .getAndRemoveStr(DSFactory.KEY_ALIAS_URL), config
/*  90 */         .getAndRemoveStr(DSFactory.KEY_ALIAS_USER), config
/*  91 */         .getAndRemoveStr(DSFactory.KEY_ALIAS_PASSWORD), config
/*  92 */         .getAndRemoveStr(DSFactory.KEY_ALIAS_DRIVER));
/*     */ 
/*     */ 
/*     */     
/*  96 */     this.connProps = (Properties)config.getProps("");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleDataSource(String url, String user, String pass) {
/* 107 */     init(url, user, pass);
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
/*     */   public SimpleDataSource(String url, String user, String pass, String driver) {
/* 120 */     init(url, user, pass, driver);
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
/* 132 */     init(url, user, pass, null);
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
/*     */   public void init(String url, String user, String pass, String driver) {
/* 145 */     this.driver = StrUtil.isNotBlank(driver) ? driver : DriverUtil.identifyDriver(url);
/*     */     try {
/* 147 */       Class.forName(this.driver);
/* 148 */     } catch (ClassNotFoundException e) {
/* 149 */       throw new DbRuntimeException(e, "Get jdbc driver [{}] error!", new Object[] { driver });
/*     */     } 
/* 151 */     this.url = url;
/* 152 */     this.user = user;
/* 153 */     this.pass = pass;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDriver() {
/* 158 */     return this.driver;
/*     */   }
/*     */   
/*     */   public void setDriver(String driver) {
/* 162 */     this.driver = driver;
/*     */   }
/*     */   
/*     */   public String getUrl() {
/* 166 */     return this.url;
/*     */   }
/*     */   
/*     */   public void setUrl(String url) {
/* 170 */     this.url = url;
/*     */   }
/*     */   
/*     */   public String getUser() {
/* 174 */     return this.user;
/*     */   }
/*     */   
/*     */   public void setUser(String user) {
/* 178 */     this.user = user;
/*     */   }
/*     */   
/*     */   public String getPass() {
/* 182 */     return this.pass;
/*     */   }
/*     */   
/*     */   public void setPass(String pass) {
/* 186 */     this.pass = pass;
/*     */   }
/*     */   
/*     */   public Properties getConnProps() {
/* 190 */     return this.connProps;
/*     */   }
/*     */   
/*     */   public void setConnProps(Properties connProps) {
/* 194 */     this.connProps = connProps;
/*     */   }
/*     */   
/*     */   public void addConnProps(String key, String value) {
/* 198 */     if (null == this.connProps) {
/* 199 */       this.connProps = new Properties();
/*     */     }
/* 201 */     this.connProps.setProperty(key, value);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Connection getConnection() throws SQLException {
/* 207 */     Props info = new Props();
/* 208 */     if (this.user != null) {
/* 209 */       info.setProperty("user", this.user);
/*     */     }
/* 211 */     if (this.pass != null) {
/* 212 */       info.setProperty("password", this.pass);
/*     */     }
/*     */ 
/*     */     
/* 216 */     Properties connProps = this.connProps;
/* 217 */     if (MapUtil.isNotEmpty(connProps)) {
/* 218 */       info.putAll(connProps);
/*     */     }
/*     */     
/* 221 */     return DriverManager.getConnection(this.url, (Properties)info);
/*     */   }
/*     */ 
/*     */   
/*     */   public Connection getConnection(String username, String password) throws SQLException {
/* 226 */     return DriverManager.getConnection(this.url, username, password);
/*     */   }
/*     */   
/*     */   public void close() {}
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\ds\simple\SimpleDataSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */