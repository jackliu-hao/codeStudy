package freemarker.core;

import freemarker.template.TemplateModelException;
import java.io.IOException;
import java.io.Writer;

public abstract class MarkupOutputFormat<MO extends TemplateMarkupOutputModel> extends OutputFormat {
  public abstract MO fromPlainTextByEscaping(String paramString) throws TemplateModelException;
  
  public abstract MO fromMarkup(String paramString) throws TemplateModelException;
  
  public abstract void output(MO paramMO, Writer paramWriter) throws IOException, TemplateModelException;
  
  public abstract void output(String paramString, Writer paramWriter) throws IOException, TemplateModelException;
  
  public abstract String getSourcePlainText(MO paramMO) throws TemplateModelException;
  
  public abstract String getMarkupString(MO paramMO) throws TemplateModelException;
  
  public abstract MO concat(MO paramMO1, MO paramMO2) throws TemplateModelException;
  
  public abstract String escapePlainText(String paramString) throws TemplateModelException;
  
  public abstract boolean isEmpty(MO paramMO) throws TemplateModelException;
  
  public abstract boolean isLegacyBuiltInBypassed(String paramString) throws TemplateModelException;
  
  public abstract boolean isAutoEscapedByDefault();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\MarkupOutputFormat.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */