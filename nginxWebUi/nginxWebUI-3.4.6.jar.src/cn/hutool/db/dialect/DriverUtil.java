/*    */ package cn.hutool.db.dialect;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.db.DbRuntimeException;
/*    */ import cn.hutool.db.DbUtil;
/*    */ import cn.hutool.db.ds.DataSourceWrapper;
/*    */ import java.sql.Connection;
/*    */ import java.sql.DatabaseMetaData;
/*    */ import java.sql.SQLException;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DriverUtil
/*    */ {
/*    */   public static String identifyDriver(String nameContainsProductInfo) {
/* 29 */     return DialectFactory.identifyDriver(nameContainsProductInfo);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String identifyDriver(DataSource ds) {
/*    */     String driver;
/* 39 */     if (ds instanceof DataSourceWrapper) {
/* 40 */       String str = ((DataSourceWrapper)ds).getDriver();
/* 41 */       if (StrUtil.isNotBlank(str)) {
/* 42 */         return str;
/*    */       }
/*    */     } 
/*    */     
/* 46 */     Connection conn = null;
/*    */     
/*    */     try {
/*    */       try {
/* 50 */         conn = ds.getConnection();
/* 51 */       } catch (SQLException e) {
/* 52 */         throw new DbRuntimeException("Get Connection error !", e);
/* 53 */       } catch (NullPointerException e) {
/* 54 */         throw new DbRuntimeException("Unexpected NullPointException, maybe [jdbcUrl] or [url] is empty!", e);
/*    */       } 
/* 56 */       driver = identifyDriver(conn);
/*    */     } finally {
/* 58 */       DbUtil.close(new Object[] { conn });
/*    */     } 
/*    */     
/* 61 */     return driver;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String identifyDriver(Connection conn) throws DbRuntimeException {
/*    */     String driver;
/*    */     try {
/* 75 */       DatabaseMetaData meta = conn.getMetaData();
/* 76 */       driver = identifyDriver(meta.getDatabaseProductName());
/* 77 */       if (StrUtil.isBlank(driver)) {
/* 78 */         driver = identifyDriver(meta.getDriverName());
/*    */       }
/* 80 */     } catch (SQLException e) {
/* 81 */       throw new DbRuntimeException("Identify driver error!", e);
/*    */     } 
/*    */     
/* 84 */     return driver;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\dialect\DriverUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */