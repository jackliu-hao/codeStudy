/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateException;
/*    */ import freemarker.template.TemplateHashModelEx;
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
/*    */ abstract class BuiltInForHashEx
/*    */   extends BuiltIn
/*    */ {
/*    */   TemplateModel _eval(Environment env) throws TemplateException {
/* 31 */     TemplateModel model = this.target.eval(env);
/* 32 */     if (model instanceof TemplateHashModelEx) {
/* 33 */       return calculateResult((TemplateHashModelEx)model, env);
/*    */     }
/* 35 */     throw new NonExtendedHashException(this.target, model, env);
/*    */   }
/*    */ 
/*    */   
/*    */   abstract TemplateModel calculateResult(TemplateHashModelEx paramTemplateHashModelEx, Environment paramEnvironment) throws TemplateModelException, InvalidReferenceException;
/*    */ 
/*    */   
/*    */   protected InvalidReferenceException newNullPropertyException(String propertyName, TemplateModel tm, Environment env) {
/* 43 */     if (env.getFastInvalidReferenceExceptions()) {
/* 44 */       return InvalidReferenceException.FAST_INSTANCE;
/*    */     }
/* 46 */     return new InvalidReferenceException((new _ErrorDescriptionBuilder(new Object[] { "The exteneded hash (of class ", tm
/*    */             
/* 48 */             .getClass().getName(), ") has returned null for its \"", propertyName, "\" property. This is maybe a bug. The extended hash was returned by this expression:"
/*    */           },
/*    */         
/* 51 */         )).blame(this.target), env, this);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\BuiltInForHashEx.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */