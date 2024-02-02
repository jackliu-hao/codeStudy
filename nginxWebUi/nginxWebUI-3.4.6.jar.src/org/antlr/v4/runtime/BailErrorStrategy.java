/*    */ package org.antlr.v4.runtime;
/*    */ 
/*    */ import org.antlr.v4.runtime.misc.ParseCancellationException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BailErrorStrategy
/*    */   extends DefaultErrorStrategy
/*    */ {
/*    */   public void recover(Parser recognizer, RecognitionException e) {
/* 71 */     for (ParserRuleContext context = recognizer.getContext(); context != null; context = context.getParent()) {
/* 72 */       context.exception = e;
/*    */     }
/*    */     
/* 75 */     throw new ParseCancellationException(e);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Token recoverInline(Parser recognizer) throws RecognitionException {
/* 85 */     InputMismatchException e = new InputMismatchException(recognizer);
/* 86 */     for (ParserRuleContext context = recognizer.getContext(); context != null; context = context.getParent()) {
/* 87 */       context.exception = e;
/*    */     }
/*    */     
/* 90 */     throw new ParseCancellationException(e);
/*    */   }
/*    */   
/*    */   public void sync(Parser recognizer) {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\BailErrorStrategy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */