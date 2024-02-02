package freemarker.ext.dom;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public interface XPathSupport {
  TemplateModel executeQuery(Object paramObject, String paramString) throws TemplateModelException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\dom\XPathSupport.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */