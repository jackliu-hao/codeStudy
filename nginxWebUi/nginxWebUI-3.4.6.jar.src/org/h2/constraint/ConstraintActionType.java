/*    */ package org.h2.constraint;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum ConstraintActionType
/*    */ {
/* 12 */   RESTRICT,
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 17 */   CASCADE,
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 22 */   SET_DEFAULT,
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 27 */   SET_NULL;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getSqlName() {
/* 35 */     if (this == SET_DEFAULT) {
/* 36 */       return "SET DEFAULT";
/*    */     }
/* 38 */     if (this == SET_NULL) {
/* 39 */       return "SET NULL";
/*    */     }
/* 41 */     return name();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\constraint\ConstraintActionType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */