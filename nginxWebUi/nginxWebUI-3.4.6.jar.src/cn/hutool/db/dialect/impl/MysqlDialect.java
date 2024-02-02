/*    */ package cn.hutool.db.dialect.impl;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.db.Entity;
/*    */ import cn.hutool.db.Page;
/*    */ import cn.hutool.db.StatementUtil;
/*    */ import cn.hutool.db.dialect.DialectName;
/*    */ import cn.hutool.db.sql.SqlBuilder;
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
/*    */ public class MysqlDialect
/*    */   extends AnsiSqlDialect
/*    */ {
/*    */   private static final long serialVersionUID = -3734718212043823636L;
/*    */   
/*    */   protected SqlBuilder wrapPageSql(SqlBuilder find, Page page) {
/* 29 */     return find.append(" LIMIT ").append(Integer.valueOf(page.getStartPosition())).append(", ").append(Integer.valueOf(page.getPageSize()));
/*    */   }
/*    */ 
/*    */   
/*    */   public String dialectName() {
/* 34 */     return DialectName.MYSQL.toString();
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public PreparedStatement psForUpsert(Connection conn, Entity entity, String... keys) throws SQLException {
/* 53 */     SqlBuilder.validateEntity(entity);
/* 54 */     SqlBuilder builder = SqlBuilder.create(this.wrapper);
/*    */     
/* 56 */     StringBuilder fieldsPart = new StringBuilder();
/* 57 */     StringBuilder placeHolder = new StringBuilder();
/* 58 */     StringBuilder updateHolder = new StringBuilder();
/*    */ 
/*    */     
/* 61 */     entity.forEach((field, value) -> {
/*    */           if (StrUtil.isNotBlank(field)) {
/*    */             if (fieldsPart.length() > 0) {
/*    */               fieldsPart.append(", ");
/*    */               
/*    */               placeHolder.append(", ");
/*    */               
/*    */               updateHolder.append(", ");
/*    */             } 
/*    */             
/*    */             field = (null != this.wrapper) ? this.wrapper.wrap(field) : field;
/*    */             fieldsPart.append(field);
/*    */             updateHolder.append(field).append("=values(").append(field).append(")");
/*    */             placeHolder.append("?");
/*    */             builder.addParams(new Object[] { value });
/*    */           } 
/*    */         });
/* 78 */     String tableName = entity.getTableName();
/* 79 */     if (null != this.wrapper) {
/* 80 */       tableName = this.wrapper.wrap(tableName);
/*    */     }
/* 82 */     builder.append("INSERT INTO ").append(tableName)
/*    */       
/* 84 */       .append(" (").append(fieldsPart)
/*    */       
/* 86 */       .append(") VALUES (").append(placeHolder)
/*    */       
/* 88 */       .append(") ON DUPLICATE KEY UPDATE ").append(updateHolder);
/*    */     
/* 90 */     return StatementUtil.prepareStatement(conn, builder);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\dialect\impl\MysqlDialect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */