/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateException;
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
/*    */ final class AndExpression
/*    */   extends BooleanExpression
/*    */ {
/*    */   private final Expression lho;
/*    */   private final Expression rho;
/*    */   
/*    */   AndExpression(Expression lho, Expression rho) {
/* 30 */     this.lho = lho;
/* 31 */     this.rho = rho;
/*    */   }
/*    */ 
/*    */   
/*    */   boolean evalToBoolean(Environment env) throws TemplateException {
/* 36 */     return (this.lho.evalToBoolean(env) && this.rho.evalToBoolean(env));
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCanonicalForm() {
/* 41 */     return this.lho.getCanonicalForm() + " && " + this.rho.getCanonicalForm();
/*    */   }
/*    */ 
/*    */   
/*    */   String getNodeTypeSymbol() {
/* 46 */     return "&&";
/*    */   }
/*    */ 
/*    */   
/*    */   boolean isLiteral() {
/* 51 */     return (this.constantValue != null || (this.lho.isLiteral() && this.rho.isLiteral()));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
/* 57 */     return new AndExpression(this.lho
/* 58 */         .deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.rho
/* 59 */         .deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
/*    */   }
/*    */ 
/*    */   
/*    */   int getParameterCount() {
/* 64 */     return 2;
/*    */   }
/*    */ 
/*    */   
/*    */   Object getParameterValue(int idx) {
/* 69 */     switch (idx) { case 0:
/* 70 */         return this.lho;
/* 71 */       case 1: return this.rho; }
/* 72 */      throw new IndexOutOfBoundsException();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   ParameterRole getParameterRole(int idx) {
/* 78 */     return ParameterRole.forBinaryOperatorOperand(idx);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\AndExpression.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */