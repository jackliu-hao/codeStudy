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
/*    */ abstract class BuiltInForNumber
/*    */   extends BuiltIn
/*    */ {
/*    */   TemplateModel _eval(Environment env) throws TemplateException {
/* 30 */     TemplateModel model = this.target.eval(env);
/* 31 */     return calculateResult(this.target.modelToNumber(model, env), model);
/*    */   }
/*    */   
/*    */   abstract TemplateModel calculateResult(Number paramNumber, TemplateModel paramTemplateModel) throws TemplateModelException;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\BuiltInForNumber.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */