/*    */ package cn.hutool.db.ds.bee;
/*    */ 
/*    */ import cn.beecp.BeeDataSource;
/*    */ import cn.beecp.BeeDataSourceConfig;
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
/*    */ public class BeeDSFactory
/*    */   extends AbstractDSFactory
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   public static final String DS_NAME = "BeeCP";
/*    */   
/*    */   public BeeDSFactory() {
/* 23 */     this(null);
/*    */   }
/*    */   
/*    */   public BeeDSFactory(Setting setting) {
/* 27 */     super("BeeCP", BeeDataSource.class, setting);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected DataSource createDataSource(String jdbcUrl, String driver, String user, String pass, Setting poolSetting) {
/* 33 */     BeeDataSourceConfig beeConfig = new BeeDataSourceConfig(driver, jdbcUrl, user, pass);
/* 34 */     poolSetting.toBean(beeConfig);
/*    */ 
/*    */ 
/*    */     
/* 38 */     for (String key : KEY_CONN_PROPS) {
/* 39 */       String connValue = poolSetting.getAndRemoveStr(new String[] { key });
/* 40 */       if (StrUtil.isNotBlank(connValue)) {
/* 41 */         beeConfig.addConnectProperty(key, connValue);
/*    */       }
/*    */     } 
/*    */     
/* 45 */     return (DataSource)new BeeDataSource(beeConfig);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\ds\bee\BeeDSFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */