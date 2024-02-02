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
/*    */ public class _DelayedGetMessage
/*    */   extends _DelayedConversionToString
/*    */ {
/*    */   public _DelayedGetMessage(Throwable exception) {
/* 26 */     super(exception);
/*    */   }
/*    */ 
/*    */   
/*    */   protected String doConversion(Object obj) {
/* 31 */     String message = ((Throwable)obj).getMessage();
/* 32 */     return (message == null || message.length() == 0) ? "[No exception message]" : message;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\_DelayedGetMessage.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */