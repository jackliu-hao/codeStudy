/*    */ package cn.hutool.db.dialect.impl;
/*    */ 
/*    */ import cn.hutool.core.lang.Assert;
/*    */ import cn.hutool.core.util.ArrayUtil;
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
/*    */ public class H2Dialect
/*    */   extends AnsiSqlDialect
/*    */ {
/*    */   private static final long serialVersionUID = 1490520247974768214L;
/*    */   
/*    */   public String dialectName() {
/* 30 */     return DialectName.H2.name();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected SqlBuilder wrapPageSql(SqlBuilder find, Page page) {
/* 36 */     return find.append(" limit ").append(Integer.valueOf(page.getStartPosition())).append(" , ").append(Integer.valueOf(page.getPageSize()));
/*    */   }
/*    */ 
/*    */   
/*    */   public PreparedStatement psForUpsert(Connection conn, Entity entity, String... keys) throws SQLException {
/* 41 */     Assert.notEmpty((Object[])keys, "Keys must be not empty for H2 MERGE SQL.", new Object[0]);
/* 42 */     SqlBuilder.validateEntity(entity);
/* 43 */     SqlBuilder builder = SqlBuilder.create(this.wrapper);
/*    */     
/* 45 */     StringBuilder fieldsPart = new StringBuilder();
/* 46 */     StringBuilder placeHolder = new StringBuilder();
/*    */ 
/*    */     
/* 49 */     entity.forEach((field, value) -> {
/*    */           if (StrUtil.isNotBlank(field)) {
/*    */             if (fieldsPart.length() > 0) {
/*    */               fieldsPart.append(", ");
/*    */               
/*    */               placeHolder.append(", ");
/*    */             } 
/*    */             
/*    */             fieldsPart.append((null != this.wrapper) ? this.wrapper.wrap(field) : field);
/*    */             
/*    */             placeHolder.append("?");
/*    */             builder.addParams(new Object[] { value });
/*    */           } 
/*    */         });
/* 63 */     String tableName = entity.getTableName();
/* 64 */     if (null != this.wrapper) {
/* 65 */       tableName = this.wrapper.wrap(tableName);
/*    */     }
/* 67 */     builder.append("MERGE INTO ").append(tableName)
/*    */       
/* 69 */       .append(" (").append(fieldsPart)
/*    */       
/* 71 */       .append(") KEY(").append(ArrayUtil.join((Object[])keys, ", "))
/*    */       
/* 73 */       .append(") VALUES (").append(placeHolder).append(")");
/*    */     
/* 75 */     return StatementUtil.prepareStatement(conn, builder);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\dialect\impl\H2Dialect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */