package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.utility.StandardCompress;
import java.io.IOException;
import java.util.Map;

final class CompressedBlock extends TemplateElement {
   CompressedBlock(TemplateElements children) {
      this.setChildren(children);
   }

   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
      TemplateElement[] childBuffer = this.getChildBuffer();
      if (childBuffer != null) {
         env.visitAndTransform(childBuffer, StandardCompress.INSTANCE, (Map)null);
      }

      return null;
   }

   protected String dump(boolean canonical) {
      return canonical ? "<" + this.getNodeTypeSymbol() + ">" + this.getChildrenCanonicalForm() + "</" + this.getNodeTypeSymbol() + ">" : this.getNodeTypeSymbol();
   }

   String getNodeTypeSymbol() {
      return "#compress";
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
      return this.getChildCount() == 0 && this.getParameterCount() == 0;
   }

   boolean isNestedBlockRepeater() {
      return false;
   }
}
