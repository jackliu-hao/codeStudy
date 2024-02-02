/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateException;
/*    */ import freemarker.template.TemplateModel;
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
/*    */ abstract class BuiltInForString
/*    */   extends BuiltIn
/*    */ {
/*    */   TemplateModel _eval(Environment env) throws TemplateException {
/* 29 */     return calculateResult(getTargetString(this.target, env), env);
/*    */   }
/*    */ 
/*    */   
/*    */   static String getTargetString(Expression target, Environment env) throws TemplateException {
/* 34 */     return target.evalAndCoerceToStringOrUnsupportedMarkup(env);
/*    */   }
/*    */   
/*    */   abstract TemplateModel calculateResult(String paramString, Environment paramEnvironment) throws TemplateException;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\BuiltInForString.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */