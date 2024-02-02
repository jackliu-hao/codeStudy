/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateException;
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.utility.NullArgumentException;
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
/*    */ abstract class MarkupOutputFormatBoundBuiltIn
/*    */   extends SpecialBuiltIn
/*    */ {
/*    */   protected MarkupOutputFormat outputFormat;
/*    */   
/*    */   void bindToMarkupOutputFormat(MarkupOutputFormat outputFormat) {
/* 30 */     NullArgumentException.check(outputFormat);
/* 31 */     this.outputFormat = outputFormat;
/*    */   }
/*    */ 
/*    */   
/*    */   TemplateModel _eval(Environment env) throws TemplateException {
/* 36 */     if (this.outputFormat == null)
/*    */     {
/* 38 */       throw new NullPointerException("outputFormat was null");
/*    */     }
/* 40 */     return calculateResult(env);
/*    */   }
/*    */   
/*    */   protected abstract TemplateModel calculateResult(Environment paramEnvironment) throws TemplateException;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\MarkupOutputFormatBoundBuiltIn.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */