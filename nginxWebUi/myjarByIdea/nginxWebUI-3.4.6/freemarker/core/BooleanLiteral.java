package freemarker.core;

import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateModel;

final class BooleanLiteral extends Expression {
   private final boolean val;

   public BooleanLiteral(boolean val) {
      this.val = val;
   }

   static TemplateBooleanModel getTemplateModel(boolean b) {
      return b ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
   }

   boolean evalToBoolean(Environment env) {
      return this.val;
   }

   public String getCanonicalForm() {
      return this.val ? "true" : "false";
   }

   String getNodeTypeSymbol() {
      return this.getCanonicalForm();
   }

   public String toString() {
      return this.val ? "true" : "false";
   }

   TemplateModel _eval(Environment env) {
      return this.val ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
   }

   boolean isLiteral() {
      return true;
   }

   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
      return new BooleanLiteral(this.val);
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
