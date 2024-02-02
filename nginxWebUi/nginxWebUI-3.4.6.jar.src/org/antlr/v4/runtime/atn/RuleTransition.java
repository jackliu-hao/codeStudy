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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class RuleTransition
/*    */   extends Transition
/*    */ {
/*    */   public final int ruleIndex;
/*    */   public final int precedence;
/*    */   public ATNState followState;
/*    */   
/*    */   @Deprecated
/*    */   public RuleTransition(RuleStartState ruleStart, int ruleIndex, ATNState followState) {
/* 52 */     this(ruleStart, ruleIndex, 0, followState);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public RuleTransition(RuleStartState ruleStart, int ruleIndex, int precedence, ATNState followState) {
/* 60 */     super(ruleStart);
/* 61 */     this.ruleIndex = ruleIndex;
/* 62 */     this.precedence = precedence;
/* 63 */     this.followState = followState;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getSerializationType() {
/* 68 */     return 3;
/*    */   }
/*    */   
/*    */   public boolean isEpsilon() {
/* 72 */     return true;
/*    */   }
/*    */   
/*    */   public boolean matches(int symbol, int minVocabSymbol, int maxVocabSymbol) {
/* 76 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\RuleTransition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */