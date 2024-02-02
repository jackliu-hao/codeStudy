package freemarker.core;

import freemarker.template.TemplateException;
import java.io.IOException;

final class RecoveryBlock extends TemplateElement {
   RecoveryBlock(TemplateElements children) {
      this.setChildren(children);
   }

   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
      return this.getChildBuffer();
   }

   protected String dump(boolean canonical) {
      if (canonical) {
         StringBuilder buf = new StringBuilder();
         buf.append('<').append(this.getNodeTypeSymbol()).append('>');
         buf.append(this.getChildrenCanonicalForm());
         return buf.toString();
      } else {
         return this.getNodeTypeSymbol();
      }
   }

   String getNodeTypeSymbol() {
      return "#recover";
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
