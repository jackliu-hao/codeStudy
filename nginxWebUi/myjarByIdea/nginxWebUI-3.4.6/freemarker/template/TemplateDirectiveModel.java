package freemarker.template;

import freemarker.core.Environment;
import java.io.IOException;
import java.util.Map;

public interface TemplateDirectiveModel extends TemplateModel {
   void execute(Environment var1, Map var2, TemplateModel[] var3, TemplateDirectiveBody var4) throws TemplateException, IOException;
}
