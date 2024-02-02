/*    */ package org.h2.expression.analysis;
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
/*    */ public enum WindowFrameUnits
/*    */ {
/* 16 */   ROWS,
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 21 */   RANGE,
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 26 */   GROUPS;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getSQL() {
/* 37 */     return name();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\analysis\WindowFrameUnits.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */