package freemarker.core;

import freemarker.template.TemplateException;
import java.io.IOException;

class ListElseContainer extends TemplateElement {
   private final IteratorBlock listPart;
   private final ElseOfList elsePart;

   public ListElseContainer(IteratorBlock listPart, ElseOfList elsePart) {
      this.setChildBufferCapacity(2);
      this.addChild(listPart);
      this.addChild(elsePart);
      this.listPart = listPart;
      this.elsePart = elsePart;
   }

   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
      return !this.listPart.acceptWithResult(env) ? this.elsePart.accept(env) : null;
   }

   boolean isNestedBlockRepeater() {
      return false;
   }

   protected String dump(boolean canonical) {
      if (!canonical) {
         return this.getNodeTypeSymbol();
      } else {
         StringBuilder buf = new StringBuilder();
         int ln = this.getChildCount();

         for(int i = 0; i < ln; ++i) {
            TemplateElement element = this.getChild(i);
            buf.append(element.dump(canonical));
         }

         buf.append("</#list>");
         return buf.toString();
      }
   }

   String getNodeTypeSymbol() {
      return "#list-#else-container";
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
