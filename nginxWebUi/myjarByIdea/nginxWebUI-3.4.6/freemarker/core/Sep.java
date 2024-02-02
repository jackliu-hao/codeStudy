package freemarker.core;

import freemarker.template.TemplateException;
import java.io.IOException;

class Sep extends TemplateElement {
   public Sep(TemplateElements children) {
      this.setChildren(children);
   }

   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
      IteratorBlock.IterationContext iterCtx = env.findClosestEnclosingIterationContext();
      if (iterCtx == null) {
         throw new _MiscTemplateException(env, new Object[]{this.getNodeTypeSymbol(), " without iteration in context"});
      } else {
         return iterCtx.hasNext() ? this.getChildBuffer() : null;
      }
   }

   boolean isNestedBlockRepeater() {
      return false;
   }

   protected String dump(boolean canonical) {
      StringBuilder sb = new StringBuilder();
      if (canonical) {
         sb.append('<');
      }

      sb.append(this.getNodeTypeSymbol());
      if (canonical) {
         sb.append('>');
         sb.append(this.getChildrenCanonicalForm());
         sb.append("</");
         sb.append(this.getNodeTypeSymbol());
         sb.append('>');
      }

      return sb.toString();
   }

   String getNodeTypeSymbol() {
      return "#sep";
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
}
