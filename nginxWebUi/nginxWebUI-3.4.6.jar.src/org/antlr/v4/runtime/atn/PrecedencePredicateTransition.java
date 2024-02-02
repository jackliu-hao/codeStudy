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
/*    */ public final class PrecedencePredicateTransition
/*    */   extends AbstractPredicateTransition
/*    */ {
/*    */   public final int precedence;
/*    */   
/*    */   public PrecedencePredicateTransition(ATNState target, int precedence) {
/* 41 */     super(target);
/* 42 */     this.precedence = precedence;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getSerializationType() {
/* 47 */     return 10;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEpsilon() {
/* 52 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean matches(int symbol, int minVocabSymbol, int maxVocabSymbol) {
/* 57 */     return false;
/*    */   }
/*    */   
/*    */   public SemanticContext.PrecedencePredicate getPredicate() {
/* 61 */     return new SemanticContext.PrecedencePredicate(this.precedence);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 66 */     return this.precedence + " >= _p";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\PrecedencePredicateTransition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */