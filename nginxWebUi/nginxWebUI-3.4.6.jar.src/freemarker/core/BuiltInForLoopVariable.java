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
/*    */ abstract class BuiltInForLoopVariable
/*    */   extends SpecialBuiltIn
/*    */ {
/*    */   private String loopVarName;
/*    */   
/*    */   void bindToLoopVariable(String loopVarName) {
/* 31 */     this.loopVarName = loopVarName;
/*    */   }
/*    */ 
/*    */   
/*    */   TemplateModel _eval(Environment env) throws TemplateException {
/* 36 */     IteratorBlock.IterationContext iterCtx = env.findEnclosingIterationContextWithVisibleVariable(this.loopVarName);
/* 37 */     if (iterCtx == null)
/*    */     {
/* 39 */       throw new _MiscTemplateException(this, env, new Object[] { "There's no iteration in context that uses loop variable ", new _DelayedJQuote(this.loopVarName), "." });
/*    */     }
/*    */ 
/*    */ 
/*    */     
/* 44 */     return calculateResult(iterCtx, env);
/*    */   }
/*    */   
/*    */   abstract TemplateModel calculateResult(IteratorBlock.IterationContext paramIterationContext, Environment paramEnvironment) throws TemplateException;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\BuiltInForLoopVariable.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */