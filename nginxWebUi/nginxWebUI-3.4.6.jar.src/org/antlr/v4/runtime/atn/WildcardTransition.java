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
/*    */ public final class WildcardTransition
/*    */   extends Transition
/*    */ {
/*    */   public WildcardTransition(ATNState target) {
/* 34 */     super(target);
/*    */   }
/*    */   
/*    */   public int getSerializationType() {
/* 38 */     return 9;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean matches(int symbol, int minVocabSymbol, int maxVocabSymbol) {
/* 43 */     return (symbol >= minVocabSymbol && symbol <= maxVocabSymbol);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 48 */     return ".";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\WildcardTransition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */