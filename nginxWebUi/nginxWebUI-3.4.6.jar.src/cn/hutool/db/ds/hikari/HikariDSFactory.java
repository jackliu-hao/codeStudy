/*    */ package cn.hutool.db.ds.hikari;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.db.ds.AbstractDSFactory;
/*    */ import cn.hutool.setting.Setting;
/*    */ import cn.hutool.setting.dialect.Props;
/*    */ import com.zaxxer.hikari.HikariConfig;
/*    */ import com.zaxxer.hikari.HikariDataSource;
/*    */ import java.util.Map;
/*    */ import java.util.Properties;
/*    */ import javax.sql.DataSource;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class HikariDSFactory
/*    */   extends AbstractDSFactory
/*    */ {
/*    */   private static final long serialVersionUID = -8834744983614749401L;
/*    */   public static final String DS_NAME = "HikariCP";
/*    */   
/*    */   public HikariDSFactory() {
/* 24 */     this(null);
/*    */   }
/*    */   
/*    */   public HikariDSFactory(Setting setting) {
/* 28 */     super("HikariCP", HikariDataSource.class, setting);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected DataSource createDataSource(String jdbcUrl, String driver, String user, String pass, Setting poolSetting) {
/* 34 */     Props connProps = new Props();
/*    */     
/* 36 */     for (String key : KEY_CONN_PROPS) {
/* 37 */       String connValue = poolSetting.getAndRemoveStr(new String[] { key });
/* 38 */       if (StrUtil.isNotBlank(connValue)) {
/* 39 */         connProps.setProperty(key, connValue);
/*    */       }
/*    */     } 
/*    */     
/* 43 */     Props config = new Props();
/* 44 */     config.putAll((Map)poolSetting);
/*    */     
/* 46 */     config.put("jdbcUrl", jdbcUrl);
/* 47 */     if (null != driver) {
/* 48 */       config.put("driverClassName", driver);
/*    */     }
/* 50 */     if (null != user) {
/* 51 */       config.put("username", user);
/*    */     }
/* 53 */     if (null != pass) {
/* 54 */       config.put("password", pass);
/*    */     }
/*    */     
/* 57 */     HikariConfig hikariConfig = new HikariConfig((Properties)config);
/* 58 */     hikariConfig.setDataSourceProperties((Properties)connProps);
/*    */     
/* 60 */     return (DataSource)new HikariDataSource(hikariConfig);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\ds\hikari\HikariDSFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */