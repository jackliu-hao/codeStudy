/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateException;
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.TemplateModelException;
/*    */ import freemarker.template.TemplateNodeModelEx;
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
/*    */ public abstract class BuiltInForNodeEx
/*    */   extends BuiltIn
/*    */ {
/*    */   abstract TemplateModel calculateResult(TemplateNodeModelEx paramTemplateNodeModelEx, Environment paramEnvironment) throws TemplateModelException;
/*    */   
/*    */   TemplateModel _eval(Environment env) throws TemplateException {
/* 29 */     TemplateModel model = this.target.eval(env);
/* 30 */     if (model instanceof TemplateNodeModelEx) {
/* 31 */       return calculateResult((TemplateNodeModelEx)model, env);
/*    */     }
/* 33 */     throw new NonExtendedNodeException(this.target, model, env);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\BuiltInForNodeEx.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */