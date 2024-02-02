package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateScalarModel;

public abstract class TruncateBuiltinAlgorithm {
  public abstract TemplateModel truncateM(String paramString, int paramInt, TemplateModel paramTemplateModel, Integer paramInteger, Environment paramEnvironment) throws TemplateException;
  
  public abstract TemplateScalarModel truncate(String paramString, int paramInt, TemplateScalarModel paramTemplateScalarModel, Integer paramInteger, Environment paramEnvironment) throws TemplateException;
  
  public abstract TemplateScalarModel truncateW(String paramString, int paramInt, TemplateScalarModel paramTemplateScalarModel, Integer paramInteger, Environment paramEnvironment) throws TemplateException;
  
  public abstract TemplateModel truncateWM(String paramString, int paramInt, TemplateModel paramTemplateModel, Integer paramInteger, Environment paramEnvironment) throws TemplateException;
  
  public abstract TemplateScalarModel truncateC(String paramString, int paramInt, TemplateScalarModel paramTemplateScalarModel, Integer paramInteger, Environment paramEnvironment) throws TemplateException;
  
  public abstract TemplateModel truncateCM(String paramString, int paramInt, TemplateModel paramTemplateModel, Integer paramInteger, Environment paramEnvironment) throws TemplateException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\TruncateBuiltinAlgorithm.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */