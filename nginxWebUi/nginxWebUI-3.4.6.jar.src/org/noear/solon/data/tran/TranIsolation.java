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
/*    */ public enum TranIsolation
/*    */ {
/* 13 */   unspecified(-1),
/*    */ 
/*    */ 
/*    */   
/* 17 */   read_uncommitted(1),
/*    */ 
/*    */ 
/*    */   
/* 21 */   read_committed(2),
/*    */ 
/*    */ 
/*    */   
/* 25 */   repeatable_read(4),
/*    */ 
/*    */ 
/*    */   
/* 29 */   serializable(8);
/*    */   
/*    */   public final int level;
/*    */   
/*    */   TranIsolation(int level) {
/* 34 */     this.level = level;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\data\tran\TranIsolation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */