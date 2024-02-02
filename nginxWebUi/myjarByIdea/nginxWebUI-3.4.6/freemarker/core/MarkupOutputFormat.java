package freemarker.core;

import freemarker.template.TemplateModelException;
import java.io.IOException;
import java.io.Writer;

public abstract class MarkupOutputFormat<MO extends TemplateMarkupOutputModel> extends OutputFormat {
   protected MarkupOutputFormat() {
   }

   public abstract MO fromPlainTextByEscaping(String var1) throws TemplateModelException;

   public abstract MO fromMarkup(String var1) throws TemplateModelException;

   public abstract void output(MO var1, Writer var2) throws IOException, TemplateModelException;

   public abstract void output(String var1, Writer var2) throws IOException, TemplateModelException;

   public abstract String getSourcePlainText(MO var1) throws TemplateModelException;

   public abstract String getMarkupString(MO var1) throws TemplateModelException;

   public abstract MO concat(MO var1, MO var2) throws TemplateModelException;

   public abstract String escapePlainText(String var1) throws TemplateModelException;

   public abstract boolean isEmpty(MO var1) throws TemplateModelException;

   public abstract boolean isLegacyBuiltInBypassed(String var1) throws TemplateModelException;

   public abstract boolean isAutoEscapedByDefault();
}
