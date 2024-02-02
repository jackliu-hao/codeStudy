package freemarker.core;

import freemarker.template.TemplateException;
import java.io.IOException;

final class FallbackInstruction extends TemplateElement {
   TemplateElement[] accept(Environment env) throws IOException, TemplateException {
      env.fallback();
      return null;
   }

   protected String dump(boolean canonical) {
      return canonical ? "<" + this.getNodeTypeSymbol() + "/>" : this.getNodeTypeSymbol();
   }

   String getNodeTypeSymbol() {
      return "#fallback";
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

   boolean isShownInStackTrace() {
      return true;
   }
}
