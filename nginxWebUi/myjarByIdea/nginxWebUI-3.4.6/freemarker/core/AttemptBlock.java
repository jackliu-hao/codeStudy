package freemarker.core;

import freemarker.template.TemplateException;
import java.io.IOException;

final class AttemptBlock extends TemplateElement {
   private TemplateElement attemptedSection;
   private RecoveryBlock recoverySection;

   AttemptBlock(TemplateElements attemptedSectionChildren, RecoveryBlock recoverySection) {
      TemplateElement attemptedSection = attemptedSectionChildren.asSingleElement();
      this.attemptedSection = attemptedSection;
      this.recoverySection = recoverySection;
      this.setChildBufferCapacity(2);
      this.addChild(attemptedSection);
      this.addChild(recoverySection);
   }

   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
      env.visitAttemptRecover(this, this.attemptedSection, this.recoverySection);
      return null;
   }

   protected String dump(boolean canonical) {
      if (!canonical) {
         return this.getNodeTypeSymbol();
      } else {
         StringBuilder buf = new StringBuilder();
         buf.append("<").append(this.getNodeTypeSymbol()).append(">");
         buf.append(this.getChildrenCanonicalForm());
         buf.append("</").append(this.getNodeTypeSymbol()).append(">");
         return buf.toString();
      }
   }

   int getParameterCount() {
      return 1;
   }

   Object getParameterValue(int idx) {
      if (idx != 0) {
         throw new IndexOutOfBoundsException();
      } else {
         return this.recoverySection;
      }
   }

   ParameterRole getParameterRole(int idx) {
      if (idx != 0) {
         throw new IndexOutOfBoundsException();
      } else {
         return ParameterRole.ERROR_HANDLER;
      }
   }

   String getNodeTypeSymbol() {
      return "#attempt";
   }

   boolean isNestedBlockRepeater() {
      return false;
   }
}
