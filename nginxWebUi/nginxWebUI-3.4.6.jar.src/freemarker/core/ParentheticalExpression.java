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
/*    */ final class ParentheticalExpression
/*    */   extends Expression
/*    */ {
/*    */   private final Expression nested;
/*    */   
/*    */   ParentheticalExpression(Expression nested) {
/* 30 */     this.nested = nested;
/*    */   }
/*    */ 
/*    */   
/*    */   boolean evalToBoolean(Environment env) throws TemplateException {
/* 35 */     return this.nested.evalToBoolean(env);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCanonicalForm() {
/* 40 */     return "(" + this.nested.getCanonicalForm() + ")";
/*    */   }
/*    */ 
/*    */   
/*    */   String getNodeTypeSymbol() {
/* 45 */     return "(...)";
/*    */   }
/*    */ 
/*    */   
/*    */   TemplateModel _eval(Environment env) throws TemplateException {
/* 50 */     return this.nested.eval(env);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isLiteral() {
/* 55 */     return this.nested.isLiteral();
/*    */   }
/*    */   
/*    */   Expression getNestedExpression() {
/* 59 */     return this.nested;
/*    */   }
/*    */ 
/*    */   
/*    */   void enableLazilyGeneratedResult() {
/* 64 */     this.nested.enableLazilyGeneratedResult();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
/* 70 */     return new ParentheticalExpression(this.nested
/* 71 */         .deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
/*    */   }
/*    */ 
/*    */   
/*    */   int getParameterCount() {
/* 76 */     return 1;
/*    */   }
/*    */ 
/*    */   
/*    */   Object getParameterValue(int idx) {
/* 81 */     if (idx != 0) throw new IndexOutOfBoundsException(); 
/* 82 */     return this.nested;
/*    */   }
/*    */ 
/*    */   
/*    */   ParameterRole getParameterRole(int idx) {
/* 87 */     if (idx != 0) throw new IndexOutOfBoundsException(); 
/* 88 */     return ParameterRole.ENCLOSED_OPERAND;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\ParentheticalExpression.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */