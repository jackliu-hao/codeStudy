/*    */ package org.antlr.v4.runtime.atn;
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
/*    */ public final class StarLoopbackState
/*    */   extends ATNState
/*    */ {
/*    */   public final StarLoopEntryState getLoopEntryState() {
/* 35 */     return (StarLoopEntryState)(transition(0)).target;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getStateType() {
/* 40 */     return 9;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\StarLoopbackState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */