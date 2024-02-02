/*    */ package org.antlr.v4.runtime;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class InputMismatchException
/*    */   extends RecognitionException
/*    */ {
/*    */   public InputMismatchException(Parser recognizer) {
/* 37 */     super(recognizer, recognizer.getInputStream(), recognizer._ctx);
/* 38 */     setOffendingToken(recognizer.getCurrentToken());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\InputMismatchException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */