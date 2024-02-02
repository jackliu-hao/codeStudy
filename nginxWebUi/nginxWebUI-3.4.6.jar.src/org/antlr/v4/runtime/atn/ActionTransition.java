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
/*    */ public final class ActionTransition
/*    */   extends Transition
/*    */ {
/*    */   public final int ruleIndex;
/*    */   public final int actionIndex;
/*    */   public final boolean isCtxDependent;
/*    */   
/*    */   public ActionTransition(ATNState target, int ruleIndex) {
/* 39 */     this(target, ruleIndex, -1, false);
/*    */   }
/*    */   
/*    */   public ActionTransition(ATNState target, int ruleIndex, int actionIndex, boolean isCtxDependent) {
/* 43 */     super(target);
/* 44 */     this.ruleIndex = ruleIndex;
/* 45 */     this.actionIndex = actionIndex;
/* 46 */     this.isCtxDependent = isCtxDependent;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getSerializationType() {
/* 51 */     return 6;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEpsilon() {
/* 56 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean matches(int symbol, int minVocabSymbol, int maxVocabSymbol) {
/* 61 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 66 */     return "action_" + this.ruleIndex + ":" + this.actionIndex;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\ActionTransition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */