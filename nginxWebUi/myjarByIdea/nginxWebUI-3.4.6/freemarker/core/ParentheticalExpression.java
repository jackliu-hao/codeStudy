package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

final class ParentheticalExpression extends Expression {
   private final Expression nested;

   ParentheticalExpression(Expression nested) {
      this.nested = nested;
   }

   boolean evalToBoolean(Environment env) throws TemplateException {
      return this.nested.evalToBoolean(env);
   }

   public String getCanonicalForm() {
      return "(" + this.nested.getCanonicalForm() + ")";
   }

   String getNodeTypeSymbol() {
      return "(...)";
   }

   TemplateModel _eval(Environment env) throws TemplateException {
      return this.nested.eval(env);
   }

   public boolean isLiteral() {
      return this.nested.isLiteral();
   }

   Expression getNestedExpression() {
      return this.nested;
   }

   void enableLazilyGeneratedResult() {
      this.nested.enableLazilyGeneratedResult();
   }

   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
      return new ParentheticalExpression(this.nested.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
   }

   int getParameterCount() {
      return 1;
   }

   Object getParameterValue(int idx) {
      if (idx != 0) {
         throw new IndexOutOfBoundsException();
      } else {
         return this.nested;
      }
   }

   ParameterRole getParameterRole(int idx) {
      if (idx != 0) {
         throw new IndexOutOfBoundsException();
      } else {
         return ParameterRole.ENCLOSED_OPERAND;
      }
   }
}
