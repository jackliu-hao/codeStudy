/*    */ package org.antlr.v4.runtime.misc;
/*    */ 
/*    */ import java.util.concurrent.CancellationException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ParseCancellationException
/*    */   extends CancellationException
/*    */ {
/*    */   public ParseCancellationException() {}
/*    */   
/*    */   public ParseCancellationException(String message) {
/* 52 */     super(message);
/*    */   }
/*    */   
/*    */   public ParseCancellationException(Throwable cause) {
/* 56 */     initCause(cause);
/*    */   }
/*    */   
/*    */   public ParseCancellationException(String message, Throwable cause) {
/* 60 */     super(message);
/* 61 */     initCause(cause);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\misc\ParseCancellationException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */