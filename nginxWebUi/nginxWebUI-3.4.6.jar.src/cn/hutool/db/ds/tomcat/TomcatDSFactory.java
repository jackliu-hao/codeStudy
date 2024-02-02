/*    */ package cn.hutool.db.ds.tomcat;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.db.ds.AbstractDSFactory;
/*    */ import cn.hutool.setting.Setting;
/*    */ import cn.hutool.setting.dialect.Props;
/*    */ import java.util.Properties;
/*    */ import javax.sql.DataSource;
/*    */ import org.apache.tomcat.jdbc.pool.DataSource;
/*    */ import org.apache.tomcat.jdbc.pool.PoolConfiguration;
/*    */ import org.apache.tomcat.jdbc.pool.PoolProperties;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TomcatDSFactory
/*    */   extends AbstractDSFactory
/*    */ {
/*    */   private static final long serialVersionUID = 4925514193275150156L;
/*    */   public static final String DS_NAME = "Tomcat-Jdbc-Pool";
/*    */   
/*    */   public TomcatDSFactory() {
/* 25 */     this(null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public TomcatDSFactory(Setting setting) {
/* 34 */     super("Tomcat-Jdbc-Pool", DataSource.class, setting);
/*    */   }
/*    */ 
/*    */   
/*    */   protected DataSource createDataSource(String jdbcUrl, String driver, String user, String pass, Setting poolSetting) {
/* 39 */     PoolProperties poolProps = new PoolProperties();
/* 40 */     poolProps.setUrl(jdbcUrl);
/* 41 */     poolProps.setDriverClassName(driver);
/* 42 */     poolProps.setUsername(user);
/* 43 */     poolProps.setPassword(pass);
/*    */ 
/*    */     
/* 46 */     Props connProps = new Props();
/*    */     
/* 48 */     for (String key : KEY_CONN_PROPS) {
/* 49 */       String connValue = poolSetting.getAndRemoveStr(new String[] { key });
/* 50 */       if (StrUtil.isNotBlank(connValue)) {
/* 51 */         connProps.setProperty(key, connValue);
/*    */       }
/*    */     } 
/* 54 */     poolProps.setDbProperties((Properties)connProps);
/*    */ 
/*    */     
/* 57 */     poolSetting.toBean(poolProps);
/*    */     
/* 59 */     return (DataSource)new DataSource((PoolConfiguration)poolProps);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\ds\tomcat\TomcatDSFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */