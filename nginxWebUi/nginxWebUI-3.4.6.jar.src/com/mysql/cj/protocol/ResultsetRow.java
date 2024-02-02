/*    */ package com.mysql.cj.protocol;
/*    */ 
/*    */ import com.mysql.cj.result.Row;
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
/*    */ public interface ResultsetRow
/*    */   extends Row, ProtocolEntity
/*    */ {
/*    */   default boolean isBinaryEncoded() {
/* 43 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\ResultsetRow.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */