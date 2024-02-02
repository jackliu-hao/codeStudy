/*    */ package cn.hutool.db.ds.c3p0;
/*    */ 
/*    */ import cn.hutool.core.map.MapUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.db.DbRuntimeException;
/*    */ import cn.hutool.db.ds.AbstractDSFactory;
/*    */ import cn.hutool.setting.Setting;
/*    */ import cn.hutool.setting.dialect.Props;
/*    */ import com.mchange.v2.c3p0.ComboPooledDataSource;
/*    */ import java.beans.PropertyVetoException;
/*    */ import java.util.Map;
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
/*    */ public class C3p0DSFactory
/*    */   extends AbstractDSFactory
/*    */ {
/*    */   private static final long serialVersionUID = -6090788225842047281L;
/*    */   public static final String DS_NAME = "C3P0";
/*    */   
/*    */   public C3p0DSFactory() {
/* 29 */     this(null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public C3p0DSFactory(Setting setting) {
/* 38 */     super("C3P0", ComboPooledDataSource.class, setting);
/*    */   }
/*    */ 
/*    */   
/*    */   protected DataSource createDataSource(String jdbcUrl, String driver, String user, String pass, Setting poolSetting) {
/* 43 */     ComboPooledDataSource ds = new ComboPooledDataSource();
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
/* 54 */     if (MapUtil.isNotEmpty((Map)connProps)) {
/* 55 */       ds.setProperties((Properties)connProps);
/*    */     }
/*    */     
/* 58 */     ds.setJdbcUrl(jdbcUrl);
/*    */     try {
/* 60 */       ds.setDriverClass(driver);
/* 61 */     } catch (PropertyVetoException e) {
/* 62 */       throw new DbRuntimeException(e);
/*    */     } 
/* 64 */     ds.setUser(user);
/* 65 */     ds.setPassword(pass);
/*    */ 
/*    */     
/* 68 */     poolSetting.toBean(ds);
/*    */     
/* 70 */     return (DataSource)ds;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\ds\c3p0\C3p0DSFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */