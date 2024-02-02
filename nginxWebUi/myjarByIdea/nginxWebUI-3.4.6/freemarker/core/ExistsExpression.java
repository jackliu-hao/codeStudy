package freemarker.core;

import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

class ExistsExpression extends Expression {
   protected final Expression exp;

   ExistsExpression(Expression exp) {
      this.exp = exp;
   }

   TemplateModel _eval(Environment env) throws TemplateException {
      TemplateModel tm;
      if (this.exp instanceof ParentheticalExpression) {
         boolean lastFIRE = env.setFastInvalidReferenceExceptions(true);

         try {
            tm = this.exp.eval(env);
         } catch (InvalidReferenceException var8) {
            tm = null;
         } finally {
            env.setFastInvalidReferenceExceptions(lastFIRE);
         }
      } else {
         tm = this.exp.eval(env);
      }

      return tm == null ? TemplateBooleanModel.FALSE : TemplateBooleanModel.TRUE;
   }

   boolean isLiteral() {
      return false;
   }

   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
      return new ExistsExpression(this.exp.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
   }

   public String getCanonicalForm() {
      return this.exp.getCanonicalForm() + this.getNodeTypeSymbol();
   }

   String getNodeTypeSymbol() {
      return "??";
   }

   int getParameterCount() {
      return 1;
   }

   Object getParameterValue(int idx) {
      return this.exp;
   }

   ParameterRole getParameterRole(int idx) {
      return ParameterRole.LEFT_HAND_OPERAND;
   }
}
