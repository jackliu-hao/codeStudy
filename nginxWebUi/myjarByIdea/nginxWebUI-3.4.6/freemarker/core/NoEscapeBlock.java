package freemarker.core;

import freemarker.template.TemplateException;
import java.io.IOException;

class NoEscapeBlock extends TemplateElement {
   NoEscapeBlock(TemplateElements children) {
      this.setChildren(children);
   }

   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
      return this.getChildBuffer();
   }

   protected String dump(boolean canonical) {
      return canonical ? "<" + this.getNodeTypeSymbol() + '>' + this.getChildrenCanonicalForm() + "</" + this.getNodeTypeSymbol() + '>' : this.getNodeTypeSymbol();
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

   String getNodeTypeSymbol() {
      return "#noescape";
   }

   boolean isOutputCacheable() {
      return true;
   }

   boolean isNestedBlockRepeater() {
      return false;
   }
}
