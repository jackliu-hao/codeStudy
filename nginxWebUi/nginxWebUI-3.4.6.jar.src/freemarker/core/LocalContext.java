package freemarker.core;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.util.Collection;

public interface LocalContext {
  TemplateModel getLocalVariable(String paramString) throws TemplateModelException;
  
  Collection getLocalVariableNames() throws TemplateModelException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\LocalContext.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */