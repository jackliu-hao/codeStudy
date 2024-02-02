package freemarker.core;

import freemarker.template.TemplateException;
import java.io.IOException;

final class MixedContent extends TemplateElement {
   /** @deprecated */
   @Deprecated
   void addElement(TemplateElement element) {
      this.addChild(element);
   }

   /** @deprecated */
   @Deprecated
   void addElement(int index, TemplateElement element) {
      this.addChild(index, element);
   }

   TemplateElement postParseCleanup(boolean stripWhitespace) throws ParseException {
      super.postParseCleanup(stripWhitespace);
      return (TemplateElement)(this.getChildCount() == 1 ? this.getChild(0) : this);
   }

   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
      return this.getChildBuffer();
   }

   protected String dump(boolean canonical) {
      if (canonical) {
         return this.getChildrenCanonicalForm();
      } else {
         return this.getParentElement() == null ? "root" : this.getNodeTypeSymbol();
      }
   }

   protected boolean isOutputCacheable() {
      int ln = this.getChildCount();

      for(int i = 0; i < ln; ++i) {
         if (!this.getChild(i).isOutputCacheable()) {
            return false;
         }
      }

      return true;
   }

   String getNodeTypeSymbol() {
      return "#mixed_content";
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
