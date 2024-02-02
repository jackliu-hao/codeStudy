/*    */ package cn.hutool.db.ds.simple;
/*    */ 
/*    */ import cn.hutool.db.ds.AbstractDSFactory;
/*    */ import cn.hutool.setting.Setting;
/*    */ import java.util.Properties;
/*    */ import javax.sql.DataSource;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SimpleDSFactory
/*    */   extends AbstractDSFactory
/*    */ {
/*    */   private static final long serialVersionUID = 4738029988261034743L;
/*    */   public static final String DS_NAME = "Hutool-Simple-DataSource";
/*    */   
/*    */   public SimpleDSFactory() {
/* 20 */     this(null);
/*    */   }
/*    */   
/*    */   public SimpleDSFactory(Setting setting) {
/* 24 */     super("Hutool-Simple-DataSource", SimpleDataSource.class, setting);
/*    */   }
/*    */ 
/*    */   
/*    */   protected DataSource createDataSource(String jdbcUrl, String driver, String user, String pass, Setting poolSetting) {
/* 29 */     SimpleDataSource ds = new SimpleDataSource(jdbcUrl, user, pass, driver);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 35 */     ds.setConnProps((Properties)poolSetting.getProps(""));
/* 36 */     return ds;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\ds\simple\SimpleDSFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */