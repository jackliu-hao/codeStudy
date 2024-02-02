/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateException;
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.TemplateModelException;
/*    */ import freemarker.template.TemplateNodeModel;
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
/*    */ abstract class BuiltInForNode
/*    */   extends BuiltIn
/*    */ {
/*    */   TemplateModel _eval(Environment env) throws TemplateException {
/* 31 */     TemplateModel model = this.target.eval(env);
/* 32 */     if (model instanceof TemplateNodeModel) {
/* 33 */       return calculateResult((TemplateNodeModel)model, env);
/*    */     }
/* 35 */     throw new NonNodeException(this.target, model, env);
/*    */   }
/*    */   
/*    */   abstract TemplateModel calculateResult(TemplateNodeModel paramTemplateNodeModel, Environment paramEnvironment) throws TemplateModelException;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\BuiltInForNode.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */