/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.SimpleScalar;
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.TemplateModelException;
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
/*    */ class BuiltInsForMarkupOutputs
/*    */ {
/*    */   static class markup_stringBI
/*    */     extends BuiltInForMarkupOutput
/*    */   {
/*    */     protected TemplateModel calculateResult(TemplateMarkupOutputModel<TemplateMarkupOutputModel> model) throws TemplateModelException {
/* 35 */       return (TemplateModel)new SimpleScalar(model.getOutputFormat().getMarkupString(model));
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\BuiltInsForMarkupOutputs.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */