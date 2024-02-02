package freemarker.ext.dom;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public interface XPathSupport {
   TemplateModel executeQuery(Object var1, String var2) throws TemplateModelException;
}
