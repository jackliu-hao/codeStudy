package freemarker.core;

import java.io.IOException;

final class FlushInstruction extends TemplateElement {
   TemplateElement[] accept(Environment env) throws IOException {
      env.getOut().flush();
      return null;
   }

   protected String dump(boolean canonical) {
      return canonical ? "<" + this.getNodeTypeSymbol() + "/>" : this.getNodeTypeSymbol();
   }

   String getNodeTypeSymbol() {
      return "#flush";
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
