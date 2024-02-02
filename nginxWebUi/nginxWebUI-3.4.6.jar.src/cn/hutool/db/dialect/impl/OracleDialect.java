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
/*    */ public class OracleDialect
/*    */   extends AnsiSqlDialect
/*    */ {
/*    */   private static final long serialVersionUID = 6122761762247483015L;
/*    */   
/*    */   public static boolean isNextVal(Object value) {
/* 24 */     return (value instanceof CharSequence && StrUtil.endWithIgnoreCase(value.toString(), ".nextval"));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected SqlBuilder wrapPageSql(SqlBuilder find, Page page) {
/* 34 */     int[] startEnd = page.getStartEnd();
/* 35 */     return find
/* 36 */       .insertPreFragment("SELECT * FROM ( SELECT row_.*, rownum rownum_ from ( ")
/* 37 */       .append(" ) row_ where rownum <= ").append(Integer.valueOf(startEnd[1]))
/* 38 */       .append(") table_alias")
/* 39 */       .append(" where table_alias.rownum_ > ").append(Integer.valueOf(startEnd[0]));
/*    */   }
/*    */ 
/*    */   
/*    */   public String dialectName() {
/* 44 */     return DialectName.ORACLE.name();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\dialect\impl\OracleDialect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */