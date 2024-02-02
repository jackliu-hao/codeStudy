/*    */ package cn.hutool.db.dialect.impl;
/*    */ 
/*    */ import cn.hutool.db.dialect.DialectName;
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
/*    */ public class Sqlite3Dialect
/*    */   extends AnsiSqlDialect
/*    */ {
/*    */   private static final long serialVersionUID = -3527642408849291634L;
/*    */   
/*    */   public String dialectName() {
/* 20 */     return DialectName.SQLITE3.name();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\dialect\impl\Sqlite3Dialect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */