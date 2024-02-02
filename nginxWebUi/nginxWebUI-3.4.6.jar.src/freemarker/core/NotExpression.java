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
/*    */ final class NotExpression
/*    */   extends BooleanExpression
/*    */ {
/*    */   private final Expression target;
/*    */   
/*    */   NotExpression(Expression target) {
/* 29 */     this.target = target;
/*    */   }
/*    */ 
/*    */   
/*    */   boolean evalToBoolean(Environment env) throws TemplateException {
/* 34 */     return !this.target.evalToBoolean(env);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCanonicalForm() {
/* 39 */     return "!" + this.target.getCanonicalForm();
/*    */   }
/*    */ 
/*    */   
/*    */   String getNodeTypeSymbol() {
/* 44 */     return "!";
/*    */   }
/*    */ 
/*    */   
/*    */   boolean isLiteral() {
/* 49 */     return this.target.isLiteral();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
/* 55 */     return new NotExpression(this.target
/* 56 */         .deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
/*    */   }
/*    */ 
/*    */   
/*    */   int getParameterCount() {
/* 61 */     return 1;
/*    */   }
/*    */ 
/*    */   
/*    */   Object getParameterValue(int idx) {
/* 66 */     if (idx != 0) throw new IndexOutOfBoundsException(); 
/* 67 */     return this.target;
/*    */   }
/*    */ 
/*    */   
/*    */   ParameterRole getParameterRole(int idx) {
/* 72 */     if (idx != 0) throw new IndexOutOfBoundsException(); 
/* 73 */     return ParameterRole.RIGHT_HAND_OPERAND;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\NotExpression.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */