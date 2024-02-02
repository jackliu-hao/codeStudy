/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateException;
/*    */ import freemarker.template.TemplateHashModel;
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
/*    */ final class Dot
/*    */   extends Expression
/*    */ {
/*    */   private final Expression target;
/*    */   private final String key;
/*    */   
/*    */   Dot(Expression target, String key) {
/* 35 */     this.target = target;
/* 36 */     this.key = key;
/*    */   }
/*    */ 
/*    */   
/*    */   TemplateModel _eval(Environment env) throws TemplateException {
/* 41 */     TemplateModel leftModel = this.target.eval(env);
/* 42 */     if (leftModel instanceof TemplateHashModel) {
/* 43 */       return ((TemplateHashModel)leftModel).get(this.key);
/*    */     }
/* 45 */     if (leftModel == null && env.isClassicCompatible()) {
/* 46 */       return null;
/*    */     }
/* 48 */     throw new NonHashException(this.target, leftModel, env);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCanonicalForm() {
/* 53 */     return this.target.getCanonicalForm() + getNodeTypeSymbol() + _CoreStringUtils.toFTLIdentifierReferenceAfterDot(this.key);
/*    */   }
/*    */ 
/*    */   
/*    */   String getNodeTypeSymbol() {
/* 58 */     return ".";
/*    */   }
/*    */ 
/*    */   
/*    */   boolean isLiteral() {
/* 63 */     return this.target.isLiteral();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
/* 69 */     return new Dot(this.target
/* 70 */         .deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.key);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   int getParameterCount() {
/* 76 */     return 2;
/*    */   }
/*    */ 
/*    */   
/*    */   Object getParameterValue(int idx) {
/* 81 */     return (idx == 0) ? this.target : this.key;
/*    */   }
/*    */ 
/*    */   
/*    */   ParameterRole getParameterRole(int idx) {
/* 86 */     return ParameterRole.forBinaryOperatorOperand(idx);
/*    */   }
/*    */   
/*    */   String getRHO() {
/* 90 */     return this.key;
/*    */   }
/*    */   
/*    */   boolean onlyHasIdentifiers() {
/* 94 */     return (this.target instanceof Identifier || (this.target instanceof Dot && ((Dot)this.target).onlyHasIdentifiers()));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\Dot.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */