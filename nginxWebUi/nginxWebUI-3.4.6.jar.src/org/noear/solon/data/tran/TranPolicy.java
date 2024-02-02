/*    */ package org.noear.solon.data.tran;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum TranPolicy
/*    */ {
/* 13 */   required(1),
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 18 */   requires_new(2),
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 23 */   nested(3),
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 28 */   mandatory(4),
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 33 */   supports(5),
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 38 */   not_supported(6),
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 43 */   never(7);
/*    */   
/*    */   public final int code;
/*    */ 
/*    */   
/*    */   TranPolicy(int code) {
/* 49 */     this.code = code;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\data\tran\TranPolicy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */