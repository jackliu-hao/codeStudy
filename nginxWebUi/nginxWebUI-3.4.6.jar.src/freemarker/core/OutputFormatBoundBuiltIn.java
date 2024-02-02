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
/*    */ abstract class OutputFormatBoundBuiltIn
/*    */   extends SpecialBuiltIn
/*    */ {
/*    */   protected OutputFormat outputFormat;
/*    */   protected int autoEscapingPolicy;
/*    */   
/*    */   void bindToOutputFormat(OutputFormat outputFormat, int autoEscapingPolicy) {
/* 31 */     NullArgumentException.check(outputFormat);
/* 32 */     this.outputFormat = outputFormat;
/* 33 */     this.autoEscapingPolicy = autoEscapingPolicy;
/*    */   }
/*    */ 
/*    */   
/*    */   TemplateModel _eval(Environment env) throws TemplateException {
/* 38 */     if (this.outputFormat == null)
/*    */     {
/* 40 */       throw new NullPointerException("outputFormat was null");
/*    */     }
/* 42 */     return calculateResult(env);
/*    */   }
/*    */   
/*    */   protected abstract TemplateModel calculateResult(Environment paramEnvironment) throws TemplateException;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\OutputFormatBoundBuiltIn.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */