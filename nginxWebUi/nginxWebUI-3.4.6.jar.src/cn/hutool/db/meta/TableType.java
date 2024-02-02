/*    */ package cn.hutool.db.meta;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum TableType
/*    */ {
/*  9 */   TABLE("TABLE"),
/* 10 */   VIEW("VIEW"),
/* 11 */   SYSTEM_TABLE("SYSTEM TABLE"),
/* 12 */   GLOBAL_TEMPORARY("GLOBAL TEMPORARY"),
/* 13 */   LOCAL_TEMPORARY("LOCAL TEMPORARY"),
/* 14 */   ALIAS("ALIAS"),
/* 15 */   SYNONYM("SYNONYM");
/*    */ 
/*    */ 
/*    */   
/*    */   private final String value;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   TableType(String value) {
/* 25 */     this.value = value;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String value() {
/* 34 */     return this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 39 */     return value();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\meta\TableType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */