package freemarker.core;

import freemarker.template.TemplateException;

public final class ReturnInstruction extends TemplateElement {
   private Expression exp;

   ReturnInstruction(Expression exp) {
      this.exp = exp;
   }

   TemplateElement[] accept(Environment env) throws TemplateException {
      if (this.exp != null) {
         env.setLastReturnValue(this.exp.eval(env));
      }

      if (this.nextSibling() == null && this.getParentElement() instanceof Macro) {
         return null;
      } else {
         throw ReturnInstruction.Return.INSTANCE;
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
      return "#return";
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
         return ParameterRole.VALUE;
      }
   }

   boolean isNestedBlockRepeater() {
      return false;
   }

   public static class Return extends FlowControlException {
      static final Return INSTANCE = new Return();

      private Return() {
      }
   }
}
