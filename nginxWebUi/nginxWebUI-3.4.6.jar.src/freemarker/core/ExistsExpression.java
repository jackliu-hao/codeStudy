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
/*    */ 
/*    */ class ExistsExpression
/*    */   extends Expression
/*    */ {
/*    */   protected final Expression exp;
/*    */   
/*    */   ExistsExpression(Expression exp) {
/* 33 */     this.exp = exp;
/*    */   }
/*    */ 
/*    */   
/*    */   TemplateModel _eval(Environment env) throws TemplateException {
/*    */     TemplateModel tm;
/* 39 */     if (this.exp instanceof ParentheticalExpression) {
/* 40 */       boolean lastFIRE = env.setFastInvalidReferenceExceptions(true);
/*    */       try {
/* 42 */         tm = this.exp.eval(env);
/* 43 */       } catch (InvalidReferenceException ire) {
/* 44 */         tm = null;
/*    */       } finally {
/* 46 */         env.setFastInvalidReferenceExceptions(lastFIRE);
/*    */       } 
/*    */     } else {
/* 49 */       tm = this.exp.eval(env);
/*    */     } 
/* 51 */     return (tm == null) ? (TemplateModel)TemplateBooleanModel.FALSE : (TemplateModel)TemplateBooleanModel.TRUE;
/*    */   }
/*    */ 
/*    */   
/*    */   boolean isLiteral() {
/* 56 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
/* 61 */     return new ExistsExpression(this.exp
/* 62 */         .deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCanonicalForm() {
/* 67 */     return this.exp.getCanonicalForm() + getNodeTypeSymbol();
/*    */   }
/*    */ 
/*    */   
/*    */   String getNodeTypeSymbol() {
/* 72 */     return "??";
/*    */   }
/*    */ 
/*    */   
/*    */   int getParameterCount() {
/* 77 */     return 1;
/*    */   }
/*    */ 
/*    */   
/*    */   Object getParameterValue(int idx) {
/* 82 */     return this.exp;
/*    */   }
/*    */ 
/*    */   
/*    */   ParameterRole getParameterRole(int idx) {
/* 87 */     return ParameterRole.LEFT_HAND_OPERAND;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\ExistsExpression.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */