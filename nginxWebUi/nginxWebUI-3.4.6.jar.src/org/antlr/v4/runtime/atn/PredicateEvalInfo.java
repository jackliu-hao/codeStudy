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
/*    */ public class PredicateEvalInfo
/*    */   extends DecisionEventInfo
/*    */ {
/*    */   public final SemanticContext semctx;
/*    */   public final int predictedAlt;
/*    */   public final boolean evalResult;
/*    */   
/*    */   public PredicateEvalInfo(int decision, TokenStream input, int startIndex, int stopIndex, SemanticContext semctx, boolean evalResult, int predictedAlt, boolean fullCtx) {
/* 92 */     super(decision, new ATNConfigSet(), input, startIndex, stopIndex, fullCtx);
/* 93 */     this.semctx = semctx;
/* 94 */     this.evalResult = evalResult;
/* 95 */     this.predictedAlt = predictedAlt;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\PredicateEvalInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */