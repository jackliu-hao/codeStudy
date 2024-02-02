package freemarker.template;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ResourceBundleLocalizedString extends LocalizedString {
   private String resourceKey;
   private String resourceBundleLookupKey;

   public ResourceBundleLocalizedString(String resourceBundleLookupKey, String resourceKey) {
      this.resourceBundleLookupKey = resourceBundleLookupKey;
      this.resourceKey = resourceKey;
   }

   public String getLocalizedString(Locale locale) throws TemplateModelException {
      try {
         ResourceBundle rb = ResourceBundle.getBundle(this.resourceBundleLookupKey, locale);
         return rb.getString(this.resourceKey);
      } catch (MissingResourceException var3) {
         throw new TemplateModelException("missing resource", var3);
      }
   }
}
