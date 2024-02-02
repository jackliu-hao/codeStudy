package freemarker.template;

import freemarker.core.Environment;
import java.io.IOException;
import java.util.Map;

public interface TemplateDirectiveModel extends TemplateModel {
  void execute(Environment paramEnvironment, Map paramMap, TemplateModel[] paramArrayOfTemplateModel, TemplateDirectiveBody paramTemplateDirectiveBody) throws TemplateException, IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\TemplateDirectiveModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */