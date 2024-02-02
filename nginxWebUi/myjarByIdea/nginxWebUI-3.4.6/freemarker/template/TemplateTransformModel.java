package freemarker.template;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public interface TemplateTransformModel extends TemplateModel {
   Writer getWriter(Writer var1, Map var2) throws TemplateModelException, IOException;
}
