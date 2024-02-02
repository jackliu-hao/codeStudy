package freemarker.core;

import freemarker.template.TemplateException;

final class AndExpression extends BooleanExpression {
   private final Expression lho;
   private final Expression rho;

   AndExpression(Expression lho, Expression rho) {
      this.lho = lho;
      this.rho = rho;
   }

   boolean evalToBoolean(Environment env) throws TemplateException {
      return this.lho.evalToBoolean(env) && this.rho.evalToBoolean(env);
   }

   public String getCanonicalForm() {
      return this.lho.getCanonicalForm() + " && " + this.rho.getCanonicalForm();
   }

   String getNodeTypeSymbol() {
      return "&&";
   }

   boolean isLiteral() {
      return this.constantValue != null || this.lho.isLiteral() && this.rho.isLiteral();
   }

   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
      return new AndExpression(this.lho.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.rho.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
   }

   int getParameterCount() {
      return 2;
   }

   Object getParameterValue(int idx) {
      switch (idx) {
         case 0:
            return this.lho;
         case 1:
            return this.rho;
         default:
            throw new IndexOutOfBoundsException();
      }
   }

   ParameterRole getParameterRole(int idx) {
      return ParameterRole.forBinaryOperatorOperand(idx);
   }
}
