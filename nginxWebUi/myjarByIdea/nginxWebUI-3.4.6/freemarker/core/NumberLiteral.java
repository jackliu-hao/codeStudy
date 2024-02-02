package freemarker.core;

import freemarker.template.SimpleNumber;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateNumberModel;

final class NumberLiteral extends Expression implements TemplateNumberModel {
   private final Number value;

   public NumberLiteral(Number value) {
      this.value = value;
   }

   TemplateModel _eval(Environment env) {
      return new SimpleNumber(this.value);
   }

   public String evalAndCoerceToPlainText(Environment env) throws TemplateException {
      return env.formatNumberToPlainText(this, this, false);
   }

   public Number getAsNumber() {
      return this.value;
   }

   String getName() {
      return "the number: '" + this.value + "'";
   }

   public String getCanonicalForm() {
      return this.value.toString();
   }

   String getNodeTypeSymbol() {
      return this.getCanonicalForm();
   }

   boolean isLiteral() {
      return true;
   }

   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
      return new NumberLiteral(this.value);
   }

   int getParameterCount() {
      return 0;
   }

   Object getParameterValue(int idx) {
      throw new IndexOutOfBoundsException();
   }

   ParameterRole getParameterRole(int idx) {
      throw new IndexOutOfBoundsException();
   }
}
