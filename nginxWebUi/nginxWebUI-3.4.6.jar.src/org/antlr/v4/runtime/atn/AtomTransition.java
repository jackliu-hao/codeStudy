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
/*    */ 
/*    */ 
/*    */ public final class AtomTransition
/*    */   extends Transition
/*    */ {
/*    */   public final int label;
/*    */   
/*    */   public AtomTransition(ATNState target, int label) {
/* 41 */     super(target);
/* 42 */     this.label = label;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getSerializationType() {
/* 47 */     return 5;
/*    */   }
/*    */ 
/*    */   
/*    */   public IntervalSet label() {
/* 52 */     return IntervalSet.of(this.label);
/*    */   }
/*    */   
/*    */   public boolean matches(int symbol, int minVocabSymbol, int maxVocabSymbol) {
/* 56 */     return (this.label == symbol);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 61 */     return String.valueOf(this.label);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\AtomTransition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */