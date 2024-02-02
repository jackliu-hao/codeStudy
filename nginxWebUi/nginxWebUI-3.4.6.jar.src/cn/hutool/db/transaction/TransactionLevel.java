/*    */ package cn.hutool.db.transaction;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum TransactionLevel
/*    */ {
/* 28 */   NONE(0),
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 35 */   READ_UNCOMMITTED(1),
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 43 */   READ_COMMITTED(2),
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 51 */   REPEATABLE_READ(4),
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 58 */   SERIALIZABLE(8);
/*    */   
/*    */   private final int level;
/*    */ 
/*    */   
/*    */   TransactionLevel(int level) {
/* 64 */     this.level = level;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getLevel() {
/* 73 */     return this.level;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\transaction\TransactionLevel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */