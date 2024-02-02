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
/*    */ public class DecisionEventInfo
/*    */ {
/*    */   public final int decision;
/*    */   public final ATNConfigSet configs;
/*    */   public final TokenStream input;
/*    */   public final int startIndex;
/*    */   public final int stopIndex;
/*    */   public final boolean fullCtx;
/*    */   
/*    */   public DecisionEventInfo(int decision, ATNConfigSet configs, TokenStream input, int startIndex, int stopIndex, boolean fullCtx) {
/* 92 */     this.decision = decision;
/* 93 */     this.fullCtx = fullCtx;
/* 94 */     this.stopIndex = stopIndex;
/* 95 */     this.input = input;
/* 96 */     this.startIndex = startIndex;
/* 97 */     this.configs = configs;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\DecisionEventInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */