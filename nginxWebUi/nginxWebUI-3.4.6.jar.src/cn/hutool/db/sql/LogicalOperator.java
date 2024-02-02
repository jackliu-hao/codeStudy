/*    */ package cn.hutool.db.sql;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum LogicalOperator
/*    */ {
/* 12 */   AND,
/*    */   
/* 14 */   OR;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isSame(String logicalOperatorStr) {
/* 24 */     if (StrUtil.isBlank(logicalOperatorStr)) {
/* 25 */       return false;
/*    */     }
/* 27 */     return name().equalsIgnoreCase(logicalOperatorStr.trim());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\sql\LogicalOperator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */