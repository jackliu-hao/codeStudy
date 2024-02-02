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
/*    */ 
/*    */ public class SetTransition
/*    */   extends Transition
/*    */ {
/*    */   public final IntervalSet set;
/*    */   
/*    */   public SetTransition(ATNState target, IntervalSet set) {
/* 42 */     super(target);
/* 43 */     if (set == null) set = IntervalSet.of(0); 
/* 44 */     this.set = set;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getSerializationType() {
/* 49 */     return 7;
/*    */   }
/*    */ 
/*    */   
/*    */   public IntervalSet label() {
/* 54 */     return this.set;
/*    */   }
/*    */   
/*    */   public boolean matches(int symbol, int minVocabSymbol, int maxVocabSymbol) {
/* 58 */     return this.set.contains(symbol);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 64 */     return this.set.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\SetTransition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */