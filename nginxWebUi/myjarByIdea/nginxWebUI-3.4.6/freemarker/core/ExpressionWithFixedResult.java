package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

class ExpressionWithFixedResult extends Expression {
   private final TemplateModel fixedResult;
   private final Expression sourceExpression;

   ExpressionWithFixedResult(TemplateModel fixedResult, Expression sourceExpression) {
      this.fixedResult = fixedResult;
      this.sourceExpression = sourceExpression;
   }

   TemplateModel _eval(Environment env) throws TemplateException {
      return this.fixedResult;
   }

   boolean isLiteral() {
      return this.sourceExpression.isLiteral();
   }

   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
      return new ExpressionWithFixedResult(this.fixedResult, this.sourceExpression.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
   }

   public String getCanonicalForm() {
      return this.sourceExpression.getCanonicalForm();
   }

   String getNodeTypeSymbol() {
      return this.sourceExpression.getNodeTypeSymbol();
   }

   int getParameterCount() {
      return this.sourceExpression.getParameterCount();
   }

   Object getParameterValue(int idx) {
      return this.sourceExpression.getParameterValue(idx);
   }

   ParameterRole getParameterRole(int idx) {
      return this.sourceExpression.getParameterRole(idx);
   }
}
