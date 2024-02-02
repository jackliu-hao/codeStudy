/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateDateModel;
/*    */ import freemarker.template.TemplateException;
/*    */ import freemarker.template.TemplateModel;
/*    */ import java.util.Date;
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
/*    */ abstract class BuiltInForDate
/*    */   extends BuiltIn
/*    */ {
/*    */   TemplateModel _eval(Environment env) throws TemplateException {
/* 32 */     TemplateModel model = this.target.eval(env);
/* 33 */     if (model instanceof TemplateDateModel) {
/* 34 */       TemplateDateModel tdm = (TemplateDateModel)model;
/* 35 */       return calculateResult(EvalUtil.modelToDate(tdm, this.target), tdm.getDateType(), env);
/*    */     } 
/* 37 */     throw newNonDateException(env, model, this.target);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected abstract TemplateModel calculateResult(Date paramDate, int paramInt, Environment paramEnvironment) throws TemplateException;
/*    */ 
/*    */ 
/*    */   
/*    */   static TemplateException newNonDateException(Environment env, TemplateModel model, Expression target) throws InvalidReferenceException {
/*    */     TemplateException e;
/* 49 */     if (model == null) {
/* 50 */       e = InvalidReferenceException.getInstance(target, env);
/*    */     } else {
/* 52 */       e = new NonDateException(target, model, "date", env);
/*    */     } 
/* 54 */     return e;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\BuiltInForDate.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */