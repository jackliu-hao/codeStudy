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
/*    */ public enum WindowFrameExclusion
/*    */ {
/* 16 */   EXCLUDE_CURRENT_ROW("EXCLUDE CURRENT ROW"),
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 21 */   EXCLUDE_GROUP("EXCLUDE GROUP"),
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 26 */   EXCLUDE_TIES("EXCLUDE TIES"),
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 31 */   EXCLUDE_NO_OTHERS("EXCLUDE NO OTHERS");
/*    */ 
/*    */   
/*    */   private final String sql;
/*    */ 
/*    */   
/*    */   WindowFrameExclusion(String paramString1) {
/* 38 */     this.sql = paramString1;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isGroupOrNoOthers() {
/* 49 */     return (this == EXCLUDE_GROUP || this == EXCLUDE_NO_OTHERS);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getSQL() {
/* 59 */     return this.sql;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\analysis\WindowFrameExclusion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */