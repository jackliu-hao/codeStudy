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
/*    */ public abstract class InvalidFormatStringException
/*    */   extends TemplateValueFormatException
/*    */ {
/*    */   public InvalidFormatStringException(String message, Throwable cause) {
/* 30 */     super(message, cause);
/*    */   }
/*    */   
/*    */   public InvalidFormatStringException(String message) {
/* 34 */     this(message, null);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\InvalidFormatStringException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */