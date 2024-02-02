/*    */ package cn.hutool.db.ds.dbcp;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.db.ds.AbstractDSFactory;
/*    */ import cn.hutool.setting.Setting;
/*    */ import javax.sql.DataSource;
/*    */ import org.apache.commons.dbcp2.BasicDataSource;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DbcpDSFactory
/*    */   extends AbstractDSFactory
/*    */ {
/*    */   private static final long serialVersionUID = -9133501414334104548L;
/*    */   public static final String DS_NAME = "commons-dbcp2";
/*    */   
/*    */   public DbcpDSFactory() {
/* 22 */     this(null);
/*    */   }
/*    */   
/*    */   public DbcpDSFactory(Setting setting) {
/* 26 */     super("commons-dbcp2", BasicDataSource.class, setting);
/*    */   }
/*    */ 
/*    */   
/*    */   protected DataSource createDataSource(String jdbcUrl, String driver, String user, String pass, Setting poolSetting) {
/* 31 */     BasicDataSource ds = new BasicDataSource();
/*    */     
/* 33 */     ds.setUrl(jdbcUrl);
/* 34 */     ds.setDriverClassName(driver);
/* 35 */     ds.setUsername(user);
/* 36 */     ds.setPassword(pass);
/*    */ 
/*    */ 
/*    */     
/* 40 */     for (String key : KEY_CONN_PROPS) {
/* 41 */       String connValue = poolSetting.getAndRemoveStr(new String[] { key });
/* 42 */       if (StrUtil.isNotBlank(connValue)) {
/* 43 */         ds.addConnectionProperty(key, connValue);
/*    */       }
/*    */     } 
/*    */ 
/*    */     
/* 48 */     poolSetting.toBean(ds);
/*    */     
/* 50 */     return (DataSource)ds;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\ds\dbcp\DbcpDSFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */