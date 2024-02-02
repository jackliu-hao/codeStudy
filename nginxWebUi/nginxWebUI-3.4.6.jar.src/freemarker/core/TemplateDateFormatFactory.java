package freemarker.core;

import java.util.Locale;
import java.util.TimeZone;

public abstract class TemplateDateFormatFactory extends TemplateValueFormatFactory {
  public abstract TemplateDateFormat get(String paramString, int paramInt, Locale paramLocale, TimeZone paramTimeZone, boolean paramBoolean, Environment paramEnvironment) throws TemplateValueFormatException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\TemplateDateFormatFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */