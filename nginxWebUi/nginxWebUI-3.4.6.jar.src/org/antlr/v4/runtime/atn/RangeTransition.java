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
/*    */ public final class RangeTransition
/*    */   extends Transition
/*    */ {
/*    */   public final int from;
/*    */   public final int to;
/*    */   
/*    */   public RangeTransition(ATNState target, int from, int to) {
/* 40 */     super(target);
/* 41 */     this.from = from;
/* 42 */     this.to = to;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getSerializationType() {
/* 47 */     return 2;
/*    */   }
/*    */ 
/*    */   
/*    */   public IntervalSet label() {
/* 52 */     return IntervalSet.of(this.from, this.to);
/*    */   }
/*    */   
/*    */   public boolean matches(int symbol, int minVocabSymbol, int maxVocabSymbol) {
/* 56 */     return (symbol >= this.from && symbol <= this.to);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 61 */     return "'" + (char)this.from + "'..'" + (char)this.to + "'";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\RangeTransition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */