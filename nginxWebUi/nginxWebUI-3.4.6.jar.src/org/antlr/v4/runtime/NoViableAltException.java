/*    */ package org.antlr.v4.runtime;
/*    */ 
/*    */ import org.antlr.v4.runtime.atn.ATNConfigSet;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NoViableAltException
/*    */   extends RecognitionException
/*    */ {
/*    */   private final ATNConfigSet deadEndConfigs;
/*    */   private final Token startToken;
/*    */   
/*    */   public NoViableAltException(Parser recognizer) {
/* 53 */     this(recognizer, recognizer.getInputStream(), recognizer.getCurrentToken(), recognizer.getCurrentToken(), null, recognizer._ctx);
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
/*    */ 
/*    */ 
/*    */   
/*    */   public NoViableAltException(Parser recognizer, TokenStream input, Token startToken, Token offendingToken, ATNConfigSet deadEndConfigs, ParserRuleContext ctx) {
/* 68 */     super(recognizer, input, ctx);
/* 69 */     this.deadEndConfigs = deadEndConfigs;
/* 70 */     this.startToken = startToken;
/* 71 */     setOffendingToken(offendingToken);
/*    */   }
/*    */ 
/*    */   
/*    */   public Token getStartToken() {
/* 76 */     return this.startToken;
/*    */   }
/*    */ 
/*    */   
/*    */   public ATNConfigSet getDeadEndConfigs() {
/* 81 */     return this.deadEndConfigs;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\NoViableAltException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */