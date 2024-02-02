/*    */ package freemarker.core;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UnregisteredOutputFormatException
/*    */   extends Exception
/*    */ {
/*    */   public UnregisteredOutputFormatException(String message) {
/* 27 */     this(message, null);
/*    */   }
/*    */   
/*    */   public UnregisteredOutputFormatException(String message, Throwable cause) {
/* 31 */     super(message, cause);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\UnregisteredOutputFormatException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */