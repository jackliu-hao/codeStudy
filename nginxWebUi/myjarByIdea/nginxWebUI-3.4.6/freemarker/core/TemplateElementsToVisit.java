package freemarker.core;

import java.util.Collection;
import java.util.Collections;

class TemplateElementsToVisit {
   private final Collection<TemplateElement> templateElements;

   TemplateElementsToVisit(Collection<TemplateElement> templateElements) {
      this.templateElements = (Collection)(null != templateElements ? templateElements : Collections.emptyList());
   }

   TemplateElementsToVisit(TemplateElement nestedBlock) {
      this((Collection)Collections.singleton(nestedBlock));
   }

   Collection<TemplateElement> getTemplateElements() {
      return this.templateElements;
   }
}
