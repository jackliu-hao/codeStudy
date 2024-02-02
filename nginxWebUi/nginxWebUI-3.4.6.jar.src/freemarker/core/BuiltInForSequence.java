/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateException;
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.TemplateModelException;
/*    */ import freemarker.template.TemplateSequenceModel;
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
/*    */ abstract class BuiltInForSequence
/*    */   extends BuiltIn
/*    */ {
/*    */   TemplateModel _eval(Environment env) throws TemplateException {
/* 31 */     TemplateModel model = this.target.eval(env);
/* 32 */     if (!(model instanceof TemplateSequenceModel)) {
/* 33 */       throw new NonSequenceException(this.target, model, env);
/*    */     }
/* 35 */     return calculateResult((TemplateSequenceModel)model);
/*    */   }
/*    */   
/*    */   abstract TemplateModel calculateResult(TemplateSequenceModel paramTemplateSequenceModel) throws TemplateModelException;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\BuiltInForSequence.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */