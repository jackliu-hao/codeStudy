/*    */ package cn.hutool.db.ds.pooled;
/*    */ 
/*    */ import cn.hutool.core.map.MapUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.db.DbRuntimeException;
/*    */ import cn.hutool.db.dialect.DriverUtil;
/*    */ import cn.hutool.db.ds.DSFactory;
/*    */ import cn.hutool.setting.Setting;
/*    */ import java.util.Map;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DbSetting
/*    */ {
/*    */   public static final String DEFAULT_DB_CONFIG_PATH = "config/db.setting";
/*    */   private final Setting setting;
/*    */   
/*    */   public DbSetting() {
/* 26 */     this(null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DbSetting(Setting setting) {
/* 35 */     if (null == setting) {
/* 36 */       this.setting = new Setting("config/db.setting");
/*    */     } else {
/* 38 */       this.setting = setting;
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DbConfig getDbConfig(String group) {
/* 49 */     Setting config = this.setting.getSetting(group);
/* 50 */     if (MapUtil.isEmpty((Map)config)) {
/* 51 */       throw new DbRuntimeException("No Hutool pool config for group: [{}]", new Object[] { group });
/*    */     }
/*    */     
/* 54 */     DbConfig dbConfig = new DbConfig();
/*    */ 
/*    */     
/* 57 */     String url = config.getAndRemoveStr(DSFactory.KEY_ALIAS_URL);
/* 58 */     if (StrUtil.isBlank(url)) {
/* 59 */       throw new DbRuntimeException("No JDBC URL for group: [{}]", new Object[] { group });
/*    */     }
/* 61 */     dbConfig.setUrl(url);
/*    */     
/* 63 */     String driver = config.getAndRemoveStr(DSFactory.KEY_ALIAS_DRIVER);
/* 64 */     dbConfig.setDriver(StrUtil.isNotBlank(driver) ? driver : DriverUtil.identifyDriver(url));
/* 65 */     dbConfig.setUser(config.getAndRemoveStr(DSFactory.KEY_ALIAS_USER));
/* 66 */     dbConfig.setPass(config.getAndRemoveStr(DSFactory.KEY_ALIAS_PASSWORD));
/*    */ 
/*    */     
/* 69 */     dbConfig.setInitialSize(this.setting.getInt("initialSize", group, Integer.valueOf(0)).intValue());
/* 70 */     dbConfig.setMinIdle(this.setting.getInt("minIdle", group, Integer.valueOf(0)).intValue());
/* 71 */     dbConfig.setMaxActive(this.setting.getInt("maxActive", group, Integer.valueOf(8)).intValue());
/* 72 */     dbConfig.setMaxWait(this.setting.getLong("maxWait", group, Long.valueOf(6000L)).longValue());
/*    */ 
/*    */ 
/*    */     
/* 76 */     for (String key : DSFactory.KEY_CONN_PROPS) {
/* 77 */       String connValue = config.get(key);
/* 78 */       if (StrUtil.isNotBlank(connValue)) {
/* 79 */         dbConfig.addConnProps(key, connValue);
/*    */       }
/*    */     } 
/*    */     
/* 83 */     return dbConfig;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\ds\pooled\DbSetting.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */