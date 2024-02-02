/*    */ package org.antlr.v4.runtime.atn;
/*    */ 
/*    */ import org.antlr.v4.runtime.TokenStream;
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
/*    */ public class LookaheadEventInfo
/*    */   extends DecisionEventInfo
/*    */ {
/*    */   public int predictedAlt;
/*    */   
/*    */   public LookaheadEventInfo(int decision, ATNConfigSet configs, int predictedAlt, TokenStream input, int startIndex, int stopIndex, boolean fullCtx) {
/* 70 */     super(decision, configs, input, startIndex, stopIndex, fullCtx);
/* 71 */     this.predictedAlt = predictedAlt;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\LookaheadEventInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */