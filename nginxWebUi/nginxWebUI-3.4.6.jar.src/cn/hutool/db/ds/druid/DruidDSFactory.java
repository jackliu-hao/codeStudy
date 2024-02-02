/*    */ package cn.hutool.db.ds.druid;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.db.ds.AbstractDSFactory;
/*    */ import cn.hutool.setting.Setting;
/*    */ import cn.hutool.setting.dialect.Props;
/*    */ import com.alibaba.druid.pool.DruidDataSource;
/*    */ import java.util.Properties;
/*    */ import javax.sql.DataSource;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DruidDSFactory
/*    */   extends AbstractDSFactory
/*    */ {
/*    */   private static final long serialVersionUID = 4680621702534433222L;
/*    */   public static final String DS_NAME = "Druid";
/*    */   
/*    */   public DruidDSFactory() {
/* 26 */     this(null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DruidDSFactory(Setting setting) {
/* 35 */     super("Druid", DruidDataSource.class, setting);
/*    */   }
/*    */ 
/*    */   
/*    */   protected DataSource createDataSource(String jdbcUrl, String driver, String user, String pass, Setting poolSetting) {
/* 40 */     DruidDataSource ds = new DruidDataSource();
/*    */ 
/*    */     
/* 43 */     ds.setUrl(jdbcUrl);
/* 44 */     ds.setDriverClassName(driver);
/* 45 */     ds.setUsername(user);
/* 46 */     ds.setPassword(pass);
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 51 */     for (String key : KEY_CONN_PROPS) {
/* 52 */       String connValue = poolSetting.getAndRemoveStr(new String[] { key });
/* 53 */       if (StrUtil.isNotBlank(connValue)) {
/* 54 */         ds.addConnectionProperty(key, connValue);
/*    */       }
/*    */     } 
/*    */ 
/*    */     
/* 59 */     Props druidProps = new Props();
/* 60 */     poolSetting.forEach((key, value) -> druidProps.put(StrUtil.addPrefixIfNot(key, "druid."), value));
/* 61 */     ds.configFromPropety((Properties)druidProps);
/*    */ 
/*    */     
/* 64 */     String[] specialKeys = { "druid.connectionErrorRetryAttempts", "druid.breakAfterAcquireFailure" };
/*    */ 
/*    */     
/* 67 */     String connectionErrorRetryAttemptsKey = "druid.connectionErrorRetryAttempts";
/* 68 */     if (druidProps.containsKey("druid.connectionErrorRetryAttempts")) {
/* 69 */       ds.setConnectionErrorRetryAttempts(druidProps.getInt("druid.connectionErrorRetryAttempts").intValue());
/*    */     }
/*    */     
/* 72 */     String timeBetweenConnectErrorMillisKey = "druid.timeBetweenConnectErrorMillis";
/* 73 */     if (druidProps.containsKey("druid.timeBetweenConnectErrorMillis")) {
/* 74 */       ds.setTimeBetweenConnectErrorMillis(druidProps.getInt("druid.timeBetweenConnectErrorMillis").intValue());
/*    */     }
/*    */     
/* 77 */     String breakAfterAcquireFailureKey = "druid.breakAfterAcquireFailure";
/* 78 */     if (druidProps.containsKey("druid.breakAfterAcquireFailure")) {
/* 79 */       ds.setBreakAfterAcquireFailure(druidProps.getBool("druid.breakAfterAcquireFailure").booleanValue());
/*    */     }
/*    */ 
/*    */     
/* 83 */     if (null == ds.getValidationQuery()) {
/*    */       
/* 85 */       ds.setTestOnBorrow(false);
/* 86 */       ds.setTestOnReturn(false);
/* 87 */       ds.setTestWhileIdle(false);
/*    */     } 
/*    */     
/* 90 */     return (DataSource)ds;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\ds\druid\DruidDSFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */