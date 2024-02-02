package freemarker.core;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.util.Collection;

public interface LocalContext {
   TemplateModel getLocalVariable(String var1) throws TemplateModelException;

   Collection getLocalVariableNames() throws TemplateModelException;
}
