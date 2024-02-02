/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateBooleanModel;
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
/*    */ abstract class BooleanExpression
/*    */   extends Expression
/*    */ {
/*    */   TemplateModel _eval(Environment env) throws TemplateException {
/* 30 */     return evalToBoolean(env) ? (TemplateModel)TemplateBooleanModel.TRUE : (TemplateModel)TemplateBooleanModel.FALSE;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\BooleanExpression.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */