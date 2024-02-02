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
/*    */ public class JoranException
/*    */   extends Exception
/*    */ {
/*    */   private static final long serialVersionUID = 1112493363728774021L;
/*    */   
/*    */   public JoranException(String msg) {
/* 21 */     super(msg);
/*    */   }
/*    */   
/*    */   public JoranException(String msg, Throwable cause) {
/* 25 */     super(msg, cause);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\spi\JoranException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */