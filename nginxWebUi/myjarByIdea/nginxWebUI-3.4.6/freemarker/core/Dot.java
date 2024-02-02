package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;

final class Dot extends Expression {
   private final Expression target;
   private final String key;

   Dot(Expression target, String key) {
      this.target = target;
      this.key = key;
   }

   TemplateModel _eval(Environment env) throws TemplateException {
      TemplateModel leftModel = this.target.eval(env);
      if (leftModel instanceof TemplateHashModel) {
         return ((TemplateHashModel)leftModel).get(this.key);
      } else if (leftModel == null && env.isClassicCompatible()) {
         return null;
      } else {
         throw new NonHashException(this.target, leftModel, env);
      }
   }

   public String getCanonicalForm() {
      return this.target.getCanonicalForm() + this.getNodeTypeSymbol() + _CoreStringUtils.toFTLIdentifierReferenceAfterDot(this.key);
   }

   String getNodeTypeSymbol() {
      return ".";
   }

   boolean isLiteral() {
      return this.target.isLiteral();
   }

   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
      return new Dot(this.target.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.key);
   }

   int getParameterCount() {
      return 2;
   }

   Object getParameterValue(int idx) {
      return idx == 0 ? this.target : this.key;
   }

   ParameterRole getParameterRole(int idx) {
      return ParameterRole.forBinaryOperatorOperand(idx);
   }

   String getRHO() {
      return this.key;
   }

   boolean onlyHasIdentifiers() {
      return this.target instanceof Identifier || this.target instanceof Dot && ((Dot)this.target).onlyHasIdentifiers();
   }
}
