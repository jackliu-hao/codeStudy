package freemarker.template;

import java.util.List;

/** @deprecated */
@Deprecated
public interface TemplateMethodModel extends TemplateModel {
   Object exec(List var1) throws TemplateModelException;
}
