package freemarker.core;

import freemarker.template.TemplateException;
import java.io.IOException;

final class OutputFormatBlock extends TemplateElement {
   private final Expression paramExp;

   OutputFormatBlock(TemplateElements children, Expression paramExp) {
      this.paramExp = paramExp;
      this.setChildren(children);
   }

   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
      return this.getChildBuffer();
   }

   protected String dump(boolean canonical) {
      return canonical ? "<" + this.getNodeTypeSymbol() + " \"" + this.paramExp.getCanonicalForm() + "\">" + this.getChildrenCanonicalForm() + "</" + this.getNodeTypeSymbol() + ">" : this.getNodeTypeSymbol();
   }

   String getNodeTypeSymbol() {
      return "#outputformat";
   }

   int getParameterCount() {
      return 1;
   }

   Object getParameterValue(int idx) {
      if (idx == 0) {
         return this.paramExp;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   ParameterRole getParameterRole(int idx) {
      if (idx == 0) {
         return ParameterRole.VALUE;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   boolean isIgnorable(boolean stripWhitespace) {
      return this.getChildCount() == 0;
   }

   boolean isNestedBlockRepeater() {
      return false;
   }
}
