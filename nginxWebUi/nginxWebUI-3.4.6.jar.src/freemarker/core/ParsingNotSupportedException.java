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
/*    */ public class ParsingNotSupportedException
/*    */   extends TemplateValueFormatException
/*    */ {
/*    */   public ParsingNotSupportedException(String message, Throwable cause) {
/* 30 */     super(message, cause);
/*    */   }
/*    */   
/*    */   public ParsingNotSupportedException(String message) {
/* 34 */     this(message, null);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\ParsingNotSupportedException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */