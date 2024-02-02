/*    */ package org.h2.table;
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
/*    */ public enum TableType
/*    */ {
/* 16 */   TABLE_LINK,
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 21 */   SYSTEM_TABLE,
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 26 */   TABLE,
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 31 */   VIEW,
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 36 */   EXTERNAL_TABLE_ENGINE;
/*    */ 
/*    */   
/*    */   public String toString() {
/* 40 */     if (this == EXTERNAL_TABLE_ENGINE)
/* 41 */       return "EXTERNAL"; 
/* 42 */     if (this == SYSTEM_TABLE)
/* 43 */       return "SYSTEM TABLE"; 
/* 44 */     if (this == TABLE_LINK) {
/* 45 */       return "TABLE LINK";
/*    */     }
/* 47 */     return super.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\table\TableType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */