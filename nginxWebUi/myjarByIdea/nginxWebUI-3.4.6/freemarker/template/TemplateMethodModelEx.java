package freemarker.template;

import java.util.List;

public interface TemplateMethodModelEx extends TemplateMethodModel {
   Object exec(List var1) throws TemplateModelException;
}
