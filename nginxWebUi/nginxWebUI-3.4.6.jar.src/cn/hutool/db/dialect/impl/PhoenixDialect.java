/*    */ package cn.hutool.db.dialect.impl;
/*    */ 
/*    */ import cn.hutool.db.Entity;
/*    */ import cn.hutool.db.dialect.DialectName;
/*    */ import cn.hutool.db.sql.Query;
/*    */ import java.sql.Connection;
/*    */ import java.sql.PreparedStatement;
/*    */ import java.sql.SQLException;
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
/*    */ public class PhoenixDialect
/*    */   extends AnsiSqlDialect
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public PreparedStatement psForUpdate(Connection conn, Entity entity, Query query) throws SQLException {
/* 28 */     return psForInsert(conn, entity);
/*    */   }
/*    */ 
/*    */   
/*    */   public String dialectName() {
/* 33 */     return DialectName.PHOENIX.name();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public PreparedStatement psForUpsert(Connection conn, Entity entity, String... keys) throws SQLException {
/* 39 */     return psForInsert(conn, entity);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\dialect\impl\PhoenixDialect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */