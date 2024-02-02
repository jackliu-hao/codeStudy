/*    */ package cn.hutool.db.dialect.impl;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.db.Page;
/*    */ import cn.hutool.db.dialect.DialectName;
/*    */ import cn.hutool.db.sql.SqlBuilder;
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
/*    */ public class SqlServer2012Dialect
/*    */   extends AnsiSqlDialect
/*    */ {
/*    */   private static final long serialVersionUID = -37598166015777797L;
/*    */   
/*    */   protected SqlBuilder wrapPageSql(SqlBuilder find, Page page) {
/* 25 */     if (false == StrUtil.containsIgnoreCase(find.toString(), "order by"))
/*    */     {
/* 27 */       find.append(" order by current_timestamp");
/*    */     }
/* 29 */     return find.append(" offset ")
/* 30 */       .append(Integer.valueOf(page.getStartPosition()))
/* 31 */       .append(" row fetch next ")
/* 32 */       .append(Integer.valueOf(page.getPageSize()))
/* 33 */       .append(" row only");
/*    */   }
/*    */ 
/*    */   
/*    */   public String dialectName() {
/* 38 */     return DialectName.SQLSERVER2012.name();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\dialect\impl\SqlServer2012Dialect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */