/*    */ package org.xnio;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FailedIoFuture<T>
/*    */   extends AbstractIoFuture<T>
/*    */ {
/*    */   public FailedIoFuture(IOException e) {
/* 18 */     setException(e);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public IoFuture<T> cancel() {
/* 27 */     return this;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\FailedIoFuture.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */