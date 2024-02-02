/*    */ package org.antlr.v4.runtime.atn;
/*    */ 
/*    */ import java.util.BitSet;
/*    */ import org.antlr.v4.runtime.TokenStream;
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
/*    */ 
/*    */ public class AmbiguityInfo
/*    */   extends DecisionEventInfo
/*    */ {
/*    */   public BitSet ambigAlts;
/*    */   
/*    */   public AmbiguityInfo(int decision, ATNConfigSet configs, BitSet ambigAlts, TokenStream input, int startIndex, int stopIndex, boolean fullCtx) {
/* 94 */     super(decision, configs, input, startIndex, stopIndex, fullCtx);
/* 95 */     this.ambigAlts = ambigAlts;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\AmbiguityInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */