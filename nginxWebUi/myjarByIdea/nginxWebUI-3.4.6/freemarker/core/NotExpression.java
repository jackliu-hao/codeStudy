package freemarker.core;

import freemarker.template.TemplateException;

final class NotExpression extends BooleanExpression {
   private final Expression target;

   NotExpression(Expression target) {
      this.target = target;
   }

   boolean evalToBoolean(Environment env) throws TemplateException {
      return !this.target.evalToBoolean(env);
   }

   public String getCanonicalForm() {
      return "!" + this.target.getCanonicalForm();
   }

   String getNodeTypeSymbol() {
      return "!";
   }

   boolean isLiteral() {
      return this.target.isLiteral();
   }

   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
      return new NotExpression(this.target.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
   }

   int getParameterCount() {
      return 1;
   }

   Object getParameterValue(int idx) {
      if (idx != 0) {
         throw new IndexOutOfBoundsException();
      } else {
         return this.target;
      }
   }

   ParameterRole getParameterRole(int idx) {
      if (idx != 0) {
         throw new IndexOutOfBoundsException();
      } else {
         return ParameterRole.RIGHT_HAND_OPERAND;
      }
   }
}
