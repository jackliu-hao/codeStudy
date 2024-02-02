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
/*    */ public final class PredicateTransition
/*    */   extends AbstractPredicateTransition
/*    */ {
/*    */   public final int ruleIndex;
/*    */   public final int predIndex;
/*    */   public final boolean isCtxDependent;
/*    */   
/*    */   public PredicateTransition(ATNState target, int ruleIndex, int predIndex, boolean isCtxDependent) {
/* 45 */     super(target);
/* 46 */     this.ruleIndex = ruleIndex;
/* 47 */     this.predIndex = predIndex;
/* 48 */     this.isCtxDependent = isCtxDependent;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getSerializationType() {
/* 53 */     return 4;
/*    */   }
/*    */   
/*    */   public boolean isEpsilon() {
/* 57 */     return true;
/*    */   }
/*    */   
/*    */   public boolean matches(int symbol, int minVocabSymbol, int maxVocabSymbol) {
/* 61 */     return false;
/*    */   }
/*    */   
/*    */   public SemanticContext.Predicate getPredicate() {
/* 65 */     return new SemanticContext.Predicate(this.ruleIndex, this.predIndex, this.isCtxDependent);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 70 */     return "pred_" + this.ruleIndex + ":" + this.predIndex;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\PredicateTransition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */