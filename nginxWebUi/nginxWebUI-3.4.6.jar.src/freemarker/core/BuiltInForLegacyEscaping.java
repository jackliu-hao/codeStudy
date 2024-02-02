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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ abstract class BuiltInForLegacyEscaping
/*    */   extends BuiltInBannedWhenAutoEscaping
/*    */ {
/*    */   TemplateModel _eval(Environment env) throws TemplateException {
/* 33 */     TemplateModel tm = this.target.eval(env);
/* 34 */     Object moOrStr = EvalUtil.coerceModelToStringOrMarkup(tm, this.target, null, env);
/* 35 */     if (moOrStr instanceof String) {
/* 36 */       return calculateResult((String)moOrStr, env);
/*    */     }
/* 38 */     TemplateMarkupOutputModel<?> mo = (TemplateMarkupOutputModel)moOrStr;
/* 39 */     if (mo.getOutputFormat().isLegacyBuiltInBypassed(this.key)) {
/* 40 */       return mo;
/*    */     }
/* 42 */     throw new NonStringException(this.target, tm, env);
/*    */   }
/*    */   
/*    */   abstract TemplateModel calculateResult(String paramString, Environment paramEnvironment) throws TemplateException;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\BuiltInForLegacyEscaping.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */