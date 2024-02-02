/*    */ package org.antlr.v4.runtime.atn;
/*    */ 
/*    */ import org.antlr.v4.runtime.misc.IntervalSet;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class NotSetTransition
/*    */   extends SetTransition
/*    */ {
/*    */   public NotSetTransition(ATNState target, IntervalSet set) {
/* 37 */     super(target, set);
/*    */   }
/*    */ 
/*    */   
/*    */   public int getSerializationType() {
/* 42 */     return 8;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean matches(int symbol, int minVocabSymbol, int maxVocabSymbol) {
/* 47 */     return (symbol >= minVocabSymbol && symbol <= maxVocabSymbol && !super.matches(symbol, minVocabSymbol, maxVocabSymbol));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 54 */     return '~' + super.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\NotSetTransition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */