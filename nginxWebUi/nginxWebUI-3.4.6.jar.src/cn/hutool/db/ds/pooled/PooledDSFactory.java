/*    */ package cn.hutool.db.ds.pooled;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.db.ds.AbstractDSFactory;
/*    */ import cn.hutool.setting.Setting;
/*    */ import javax.sql.DataSource;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PooledDSFactory
/*    */   extends AbstractDSFactory
/*    */ {
/*    */   private static final long serialVersionUID = 8093886210895248277L;
/*    */   public static final String DS_NAME = "Hutool-Pooled-DataSource";
/*    */   
/*    */   public PooledDSFactory() {
/* 21 */     this(null);
/*    */   }
/*    */   
/*    */   public PooledDSFactory(Setting setting) {
/* 25 */     super("Hutool-Pooled-DataSource", PooledDataSource.class, setting);
/*    */   }
/*    */ 
/*    */   
/*    */   protected DataSource createDataSource(String jdbcUrl, String driver, String user, String pass, Setting poolSetting) {
/* 30 */     DbConfig dbConfig = new DbConfig();
/* 31 */     dbConfig.setUrl(jdbcUrl);
/* 32 */     dbConfig.setDriver(driver);
/* 33 */     dbConfig.setUser(user);
/* 34 */     dbConfig.setPass(pass);
/*    */ 
/*    */     
/* 37 */     dbConfig.setInitialSize(poolSetting.getInt("initialSize", Integer.valueOf(0)).intValue());
/* 38 */     dbConfig.setMinIdle(poolSetting.getInt("minIdle", Integer.valueOf(0)).intValue());
/* 39 */     dbConfig.setMaxActive(poolSetting.getInt("maxActive", Integer.valueOf(8)).intValue());
/* 40 */     dbConfig.setMaxWait(poolSetting.getLong("maxWait", Long.valueOf(6000L)).longValue());
/*    */ 
/*    */ 
/*    */     
/* 44 */     for (String key : KEY_CONN_PROPS) {
/* 45 */       String connValue = poolSetting.get(key);
/* 46 */       if (StrUtil.isNotBlank(connValue)) {
/* 47 */         dbConfig.addConnProps(key, connValue);
/*    */       }
/*    */     } 
/*    */     
/* 51 */     return (DataSource)new PooledDataSource(dbConfig);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\ds\pooled\PooledDSFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */