/*    */ package org.wildfly.common.function;
/*    */ 
/*    */ import org.wildfly.common.Assert;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface ExceptionRunnable<E extends Exception>
/*    */ {
/*    */   default ExceptionRunnable<E> andThen(ExceptionRunnable<? extends E> after) {
/* 37 */     Assert.checkNotNullParam("after", after);
/* 38 */     return () -> {
/*    */         run();
/*    */         after.run();
/*    */       };
/*    */   }
/*    */   
/*    */   default ExceptionRunnable<E> compose(ExceptionRunnable<? extends E> before) {
/* 45 */     Assert.checkNotNullParam("before", before);
/* 46 */     return () -> {
/*    */         before.run();
/*    */         run();
/*    */       };
/*    */   }
/*    */   
/*    */   void run() throws E;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\function\ExceptionRunnable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */