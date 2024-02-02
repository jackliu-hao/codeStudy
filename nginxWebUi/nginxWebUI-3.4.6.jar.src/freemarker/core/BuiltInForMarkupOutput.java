/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateException;
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
/*    */ abstract class BuiltInForMarkupOutput
/*    */   extends BuiltIn
/*    */ {
/*    */   TemplateModel _eval(Environment env) throws TemplateException {
/* 31 */     TemplateModel model = this.target.eval(env);
/* 32 */     if (!(model instanceof TemplateMarkupOutputModel)) {
/* 33 */       throw new NonMarkupOutputException(this.target, model, env);
/*    */     }
/* 35 */     return calculateResult((TemplateMarkupOutputModel)model);
/*    */   }
/*    */   
/*    */   protected abstract TemplateModel calculateResult(TemplateMarkupOutputModel paramTemplateMarkupOutputModel) throws TemplateModelException;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\BuiltInForMarkupOutput.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */