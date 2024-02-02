package freemarker.core;

final class BreakInstruction extends TemplateElement {
   TemplateElement[] accept(Environment env) {
      throw BreakOrContinueException.BREAK_INSTANCE;
   }

   protected String dump(boolean canonical) {
      return canonical ? "<" + this.getNodeTypeSymbol() + "/>" : this.getNodeTypeSymbol();
   }

   String getNodeTypeSymbol() {
      return "#break";
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
