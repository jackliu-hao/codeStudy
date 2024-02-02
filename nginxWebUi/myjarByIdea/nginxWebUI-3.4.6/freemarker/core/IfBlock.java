package freemarker.core;

import freemarker.template.TemplateException;
import java.io.IOException;

final class IfBlock extends TemplateElement {
   IfBlock(ConditionalBlock block) {
      this.setChildBufferCapacity(1);
      this.addBlock(block);
   }

   void addBlock(ConditionalBlock block) {
      this.addChild(block);
   }

   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
      int ln = this.getChildCount();

      for(int i = 0; i < ln; ++i) {
         ConditionalBlock cblock = (ConditionalBlock)this.getChild(i);
         Expression condition = cblock.condition;
         env.replaceElementStackTop(cblock);
         if (condition == null || condition.evalToBoolean(env)) {
            return cblock.getChildBuffer();
         }
      }

      return null;
   }

   TemplateElement postParseCleanup(boolean stripWhitespace) throws ParseException {
      if (this.getChildCount() == 1) {
         ConditionalBlock cblock = (ConditionalBlock)this.getChild(0);
         cblock.setLocation(this.getTemplate(), cblock, this);
         return cblock.postParseCleanup(stripWhitespace);
      } else {
         return super.postParseCleanup(stripWhitespace);
      }
   }

   protected String dump(boolean canonical) {
      if (!canonical) {
         return this.getNodeTypeSymbol();
      } else {
         StringBuilder buf = new StringBuilder();
         int ln = this.getChildCount();

         for(int i = 0; i < ln; ++i) {
            ConditionalBlock cblock = (ConditionalBlock)this.getChild(i);
            buf.append(cblock.dump(canonical));
         }

         buf.append("</#if>");
         return buf.toString();
      }
   }

   String getNodeTypeSymbol() {
      return "#if-#elseif-#else-container";
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
