package freemarker.template;

import freemarker.core.Environment;
import java.util.Locale;

public abstract class LocalizedString implements TemplateScalarModel {
   public String getAsString() throws TemplateModelException {
      Environment env = Environment.getCurrentEnvironment();
      Locale locale = env.getLocale();
      return this.getLocalizedString(locale);
   }

   public abstract String getLocalizedString(Locale var1) throws TemplateModelException;
}
