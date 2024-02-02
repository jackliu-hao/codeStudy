package freemarker.core;

import java.util.Locale;

public abstract class TemplateNumberFormatFactory extends TemplateValueFormatFactory {
   public abstract TemplateNumberFormat get(String var1, Locale var2, Environment var3) throws TemplateValueFormatException;
}
