/*    */ package cn.hutool.db.dialect.impl;
/*    */ 
/*    */ import cn.hutool.core.lang.Assert;
/*    */ import cn.hutool.core.util.ArrayUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.db.Entity;
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
/*    */ 
/*    */ public class PostgresqlDialect
/*    */   extends AnsiSqlDialect
/*    */ {
/*    */   private static final long serialVersionUID = 3889210427543389642L;
/*    */   
/*    */   public String dialectName() {
/* 31 */     return DialectName.POSTGRESQL.name();
/*    */   }
/*    */ 
/*    */   
/*    */   public PreparedStatement psForUpsert(Connection conn, Entity entity, String... keys) throws SQLException {
/* 36 */     Assert.notEmpty((Object[])keys, "Keys must be not empty for Postgres.", new Object[0]);
/* 37 */     SqlBuilder.validateEntity(entity);
/* 38 */     SqlBuilder builder = SqlBuilder.create(this.wrapper);
/*    */     
/* 40 */     StringBuilder fieldsPart = new StringBuilder();
/* 41 */     StringBuilder placeHolder = new StringBuilder();
/* 42 */     StringBuilder updateHolder = new StringBuilder();
/*    */ 
/*    */     
/* 45 */     entity.forEach((field, value) -> {
/*    */           if (StrUtil.isNotBlank(field)) {
/*    */             if (fieldsPart.length() > 0) {
/*    */               fieldsPart.append(", ");
/*    */               
/*    */               placeHolder.append(", ");
/*    */               
/*    */               updateHolder.append(", ");
/*    */             } 
/*    */             
/*    */             String wrapedField = (null != this.wrapper) ? this.wrapper.wrap(field) : field;
/*    */             fieldsPart.append(wrapedField);
/*    */             updateHolder.append(wrapedField).append("=EXCLUDED.").append(field);
/*    */             placeHolder.append("?");
/*    */             builder.addParams(new Object[] { value });
/*    */           } 
/*    */         });
/* 62 */     String tableName = entity.getTableName();
/* 63 */     if (null != this.wrapper) {
/* 64 */       tableName = this.wrapper.wrap(tableName);
/*    */     }
/* 66 */     builder.append("INSERT INTO ").append(tableName)
/*    */       
/* 68 */       .append(" (").append(fieldsPart)
/*    */       
/* 70 */       .append(") VALUES (").append(placeHolder)
/*    */       
/* 72 */       .append(") ON CONFLICT (").append(ArrayUtil.join((Object[])keys, ", "))
/*    */       
/* 74 */       .append(") DO UPDATE SET ").append(updateHolder);
/*    */     
/* 76 */     return StatementUtil.prepareStatement(conn, builder);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\dialect\impl\PostgresqlDialect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */