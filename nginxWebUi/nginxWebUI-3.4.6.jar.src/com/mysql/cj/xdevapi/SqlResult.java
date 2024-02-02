/*    */ package com.mysql.cj.xdevapi;
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
/*    */ public interface SqlResult
/*    */   extends Result, InsertResult, RowResult
/*    */ {
/*    */   default boolean nextResult() {
/* 42 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   default Long getAutoIncrementValue() {
/* 47 */     throw new XDevAPIError("Method getAutoIncrementValue() is allowed only for insert statements.");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\SqlResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */