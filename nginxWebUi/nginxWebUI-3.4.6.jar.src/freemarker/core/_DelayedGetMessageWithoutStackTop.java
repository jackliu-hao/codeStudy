/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateException;
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
/*    */ public class _DelayedGetMessageWithoutStackTop
/*    */   extends _DelayedConversionToString
/*    */ {
/*    */   public _DelayedGetMessageWithoutStackTop(TemplateException exception) {
/* 28 */     super(exception);
/*    */   }
/*    */ 
/*    */   
/*    */   protected String doConversion(Object obj) {
/* 33 */     return ((TemplateException)obj).getMessageWithoutStackTop();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\_DelayedGetMessageWithoutStackTop.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */