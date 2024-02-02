/*    */ package ch.qos.logback.classic.db.names;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DefaultDBNameResolver
/*    */   implements DBNameResolver
/*    */ {
/*    */   public <N extends Enum<?>> String getTableName(N tableName) {
/* 27 */     return tableName.toString().toLowerCase();
/*    */   }
/*    */   
/*    */   public <N extends Enum<?>> String getColumnName(N columnName) {
/* 31 */     return columnName.toString().toLowerCase();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\db\names\DefaultDBNameResolver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */