package freemarker.core;

import freemarker.template.TemplateException;

final class StopInstruction extends TemplateElement {
   private Expression exp;

   StopInstruction(Expression exp) {
      this.exp = exp;
   }

   TemplateElement[] accept(Environment env) throws TemplateException {
      if (this.exp == null) {
         throw new StopException(env);
      } else {
         throw new StopException(env, this.exp.evalAndCoerceToPlainText(env));
      }
   }

   protected String dump(boolean canonical) {
      StringBuilder sb = new StringBuilder();
      if (canonical) {
         sb.append('<');
      }

      sb.append(this.getNodeTypeSymbol());
      if (this.exp != null) {
         sb.append(' ');
         sb.append(this.exp.getCanonicalForm());
      }

      if (canonical) {
         sb.append("/>");
      }

      return sb.toString();
   }

   String getNodeTypeSymbol() {
      return "#stop";
   }

   int getParameterCount() {
      return 1;
   }

   Object getParameterValue(int idx) {
      if (idx != 0) {
         throw new IndexOutOfBoundsException();
      } else {
         return this.exp;
      }
   }

   ParameterRole getParameterRole(int idx) {
      if (idx != 0) {
         throw new IndexOutOfBoundsException();
      } else {
         return ParameterRole.MESSAGE;
      }
   }

   boolean isNestedBlockRepeater() {
      return false;
   }
}
