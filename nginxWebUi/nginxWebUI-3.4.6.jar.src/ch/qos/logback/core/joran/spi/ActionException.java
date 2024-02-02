/*    */ package ch.qos.logback.core.joran.spi;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ActionException
/*    */   extends Exception
/*    */ {
/*    */   private static final long serialVersionUID = 2743349809995319806L;
/*    */   
/*    */   public ActionException() {}
/*    */   
/*    */   public ActionException(Throwable rootCause) {
/* 31 */     super(rootCause);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\spi\ActionException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */