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
/*    */ class BuiltInsForOutputFormatRelated
/*    */ {
/*    */   static class no_escBI
/*    */     extends AbstractConverterBI
/*    */   {
/*    */     protected TemplateModel calculateResult(String lho, MarkupOutputFormat<TemplateModel> outputFormat, Environment env) throws TemplateException {
/* 31 */       return outputFormat.fromMarkup(lho);
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   static class escBI
/*    */     extends AbstractConverterBI
/*    */   {
/*    */     protected TemplateModel calculateResult(String lho, MarkupOutputFormat<TemplateModel> outputFormat, Environment env) throws TemplateException {
/* 41 */       return outputFormat.fromPlainTextByEscaping(lho);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   static abstract class AbstractConverterBI
/*    */     extends MarkupOutputFormatBoundBuiltIn
/*    */   {
/*    */     protected TemplateModel calculateResult(Environment env) throws TemplateException {
/* 50 */       TemplateModel lhoTM = this.target.eval(env);
/* 51 */       Object lhoMOOrStr = EvalUtil.coerceModelToStringOrMarkup(lhoTM, this.target, null, env);
/* 52 */       MarkupOutputFormat<TemplateModel> contextOF = this.outputFormat;
/* 53 */       if (lhoMOOrStr instanceof String) {
/* 54 */         return calculateResult((String)lhoMOOrStr, contextOF, env);
/*    */       }
/* 56 */       TemplateMarkupOutputModel lhoMO = (TemplateMarkupOutputModel)lhoMOOrStr;
/* 57 */       MarkupOutputFormat<TemplateMarkupOutputModel> lhoOF = lhoMO.getOutputFormat();
/*    */       
/* 59 */       if (lhoOF == contextOF || contextOF.isOutputFormatMixingAllowed())
/*    */       {
/* 61 */         return lhoMO;
/*    */       }
/*    */       
/* 64 */       String lhoPlainTtext = lhoOF.getSourcePlainText(lhoMO);
/* 65 */       if (lhoPlainTtext == null) {
/* 66 */         throw new _TemplateModelException(this.target, new Object[] { "The left side operand of ?", this.key, " is in ", new _DelayedToString(lhoOF), " format, which differs from the current output format, ", new _DelayedToString(contextOF), ". Conversion wasn't possible." });
/*    */       }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 73 */       return contextOF.fromPlainTextByEscaping(lhoPlainTtext);
/*    */     }
/*    */     
/*    */     protected abstract TemplateModel calculateResult(String param1String, MarkupOutputFormat param1MarkupOutputFormat, Environment param1Environment) throws TemplateException;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\BuiltInsForOutputFormatRelated.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */