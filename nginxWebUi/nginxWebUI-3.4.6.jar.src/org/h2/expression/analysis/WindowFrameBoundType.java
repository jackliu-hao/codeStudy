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
/*    */ public enum WindowFrameBoundType
/*    */ {
/* 16 */   UNBOUNDED_PRECEDING("UNBOUNDED PRECEDING"),
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 21 */   PRECEDING("PRECEDING"),
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 26 */   CURRENT_ROW("CURRENT ROW"),
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 31 */   FOLLOWING("FOLLOWING"),
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 36 */   UNBOUNDED_FOLLOWING("UNBOUNDED FOLLOWING");
/*    */   
/*    */   private final String sql;
/*    */   
/*    */   WindowFrameBoundType(String paramString1) {
/* 41 */     this.sql = paramString1;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getSQL() {
/* 51 */     return this.sql;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\analysis\WindowFrameBoundType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */