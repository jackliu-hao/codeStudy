/*    */ package cn.hutool.db.dialect;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum DialectName
/*    */ {
/* 12 */   ANSI, MYSQL, ORACLE, POSTGRESQL, SQLITE3, H2, SQLSERVER, SQLSERVER2012, PHOENIX;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean match(String dialectName) {
/* 22 */     return StrUtil.equalsIgnoreCase(dialectName, name());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\dialect\DialectName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */