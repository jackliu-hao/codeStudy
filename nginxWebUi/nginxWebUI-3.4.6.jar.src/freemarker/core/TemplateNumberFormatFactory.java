package freemarker.core;

import java.util.Locale;

public abstract class TemplateNumberFormatFactory extends TemplateValueFormatFactory {
  public abstract TemplateNumberFormat get(String paramString, Locale paramLocale, Environment paramEnvironment) throws TemplateValueFormatException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\TemplateNumberFormatFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */