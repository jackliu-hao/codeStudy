package freemarker.template;

import java.io.FileNotFoundException;

public final class TemplateNotFoundException extends FileNotFoundException {
   private final String templateName;
   private final Object customLookupCondition;

   public TemplateNotFoundException(String templateName, Object customLookupCondition, String message) {
      super(message);
      this.templateName = templateName;
      this.customLookupCondition = customLookupCondition;
   }

   public String getTemplateName() {
      return this.templateName;
   }

   public Object getCustomLookupCondition() {
      return this.customLookupCondition;
   }
}
