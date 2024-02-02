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
/*    */ class ExpressionWithFixedResult
/*    */   extends Expression
/*    */ {
/*    */   private final TemplateModel fixedResult;
/*    */   private final Expression sourceExpression;
/*    */   
/*    */   ExpressionWithFixedResult(TemplateModel fixedResult, Expression sourceExpression) {
/* 33 */     this.fixedResult = fixedResult;
/* 34 */     this.sourceExpression = sourceExpression;
/*    */   }
/*    */ 
/*    */   
/*    */   TemplateModel _eval(Environment env) throws TemplateException {
/* 39 */     return this.fixedResult;
/*    */   }
/*    */ 
/*    */   
/*    */   boolean isLiteral() {
/* 44 */     return this.sourceExpression.isLiteral();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
/* 50 */     return new ExpressionWithFixedResult(this.fixedResult, this.sourceExpression
/*    */         
/* 52 */         .deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCanonicalForm() {
/* 57 */     return this.sourceExpression.getCanonicalForm();
/*    */   }
/*    */ 
/*    */   
/*    */   String getNodeTypeSymbol() {
/* 62 */     return this.sourceExpression.getNodeTypeSymbol();
/*    */   }
/*    */ 
/*    */   
/*    */   int getParameterCount() {
/* 67 */     return this.sourceExpression.getParameterCount();
/*    */   }
/*    */ 
/*    */   
/*    */   Object getParameterValue(int idx) {
/* 72 */     return this.sourceExpression.getParameterValue(idx);
/*    */   }
/*    */ 
/*    */   
/*    */   ParameterRole getParameterRole(int idx) {
/* 77 */     return this.sourceExpression.getParameterRole(idx);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\ExpressionWithFixedResult.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */