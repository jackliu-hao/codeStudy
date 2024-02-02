/*    */ package cn.hutool.db.ds.jndi;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.db.DbRuntimeException;
/*    */ import cn.hutool.db.DbUtil;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JndiDSFactory
/*    */   extends AbstractDSFactory
/*    */ {
/*    */   private static final long serialVersionUID = 1573625812927370432L;
/*    */   public static final String DS_NAME = "JNDI DataSource";
/*    */   
/*    */   public JndiDSFactory() {
/* 28 */     this(null);
/*    */   }
/*    */   
/*    */   public JndiDSFactory(Setting setting) {
/* 32 */     super("JNDI DataSource", null, setting);
/*    */   }
/*    */ 
/*    */   
/*    */   protected DataSource createDataSource(String jdbcUrl, String driver, String user, String pass, Setting poolSetting) {
/* 37 */     String jndiName = poolSetting.getStr("jndi");
/* 38 */     if (StrUtil.isEmpty(jndiName)) {
/* 39 */       throw new DbRuntimeException("No setting name [jndi] for this group.");
/*    */     }
/* 41 */     return DbUtil.getJndiDs(jndiName);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\ds\jndi\JndiDSFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */