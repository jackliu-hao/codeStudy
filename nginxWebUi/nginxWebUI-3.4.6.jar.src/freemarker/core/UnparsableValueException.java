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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UnparsableValueException
/*    */   extends TemplateValueFormatException
/*    */ {
/*    */   public UnparsableValueException(String message, Throwable cause) {
/* 31 */     super(message, cause);
/*    */   }
/*    */   
/*    */   public UnparsableValueException(String message) {
/* 35 */     this(message, null);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\UnparsableValueException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */