package freemarker.core;

import freemarker.template.TemplateException;
import java.io.IOException;

final class AutoEscBlock extends TemplateElement {
   AutoEscBlock(TemplateElements children) {
      this.setChildren(children);
   }

   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
      return this.getChildBuffer();
   }

   protected String dump(boolean canonical) {
      return canonical ? "<" + this.getNodeTypeSymbol() + "\">" + this.getChildrenCanonicalForm() + "</" + this.getNodeTypeSymbol() + ">" : this.getNodeTypeSymbol();
   }

   String getNodeTypeSymbol() {
      return "#autoesc";
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

   boolean isIgnorable(boolean stripWhitespace) {
      return this.getChildCount() == 0;
   }

   boolean isNestedBlockRepeater() {
      return false;
   }
}
