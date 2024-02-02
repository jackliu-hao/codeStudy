package freemarker.core;

final class ContinueInstruction extends TemplateElement {
   TemplateElement[] accept(Environment env) {
      throw BreakOrContinueException.CONTINUE_INSTANCE;
   }

   protected String dump(boolean canonical) {
      return canonical ? "<" + this.getNodeTypeSymbol() + "/>" : this.getNodeTypeSymbol();
   }

   String getNodeTypeSymbol() {
      return "#continue";
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

   boolean isNestedBlockRepeater() {
      return false;
   }
}
