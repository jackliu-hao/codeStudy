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
/*    */ public final class EpsilonTransition
/*    */   extends Transition
/*    */ {
/*    */   private final int outermostPrecedenceReturn;
/*    */   
/*    */   public EpsilonTransition(ATNState target) {
/* 38 */     this(target, -1);
/*    */   }
/*    */   
/*    */   public EpsilonTransition(ATNState target, int outermostPrecedenceReturn) {
/* 42 */     super(target);
/* 43 */     this.outermostPrecedenceReturn = outermostPrecedenceReturn;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int outermostPrecedenceReturn() {
/* 55 */     return this.outermostPrecedenceReturn;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getSerializationType() {
/* 60 */     return 1;
/*    */   }
/*    */   
/*    */   public boolean isEpsilon() {
/* 64 */     return true;
/*    */   }
/*    */   
/*    */   public boolean matches(int symbol, int minVocabSymbol, int maxVocabSymbol) {
/* 68 */     return false;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 74 */     return "epsilon";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\EpsilonTransition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */