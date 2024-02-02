package freemarker.cache;

import java.io.IOException;
import java.util.Locale;

public abstract class TemplateLookupContext {
   private final String templateName;
   private final Locale templateLocale;
   private final Object customLookupCondition;

   public abstract TemplateLookupResult lookupWithAcquisitionStrategy(String var1) throws IOException;

   public abstract TemplateLookupResult lookupWithLocalizedThenAcquisitionStrategy(String var1, Locale var2) throws IOException;

   TemplateLookupContext(String templateName, Locale templateLocale, Object customLookupCondition) {
      this.templateName = templateName;
      this.templateLocale = templateLocale;
      this.customLookupCondition = customLookupCondition;
   }

   public String getTemplateName() {
      return this.templateName;
   }

   public Locale getTemplateLocale() {
      return this.templateLocale;
   }

   public Object getCustomLookupCondition() {
      return this.customLookupCondition;
   }

   public TemplateLookupResult createNegativeLookupResult() {
      return TemplateLookupResult.createNegativeResult();
   }
}
