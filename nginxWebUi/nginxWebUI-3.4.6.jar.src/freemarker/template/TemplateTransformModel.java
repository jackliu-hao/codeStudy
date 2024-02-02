package freemarker.template;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public interface TemplateTransformModel extends TemplateModel {
  Writer getWriter(Writer paramWriter, Map paramMap) throws TemplateModelException, IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\TemplateTransformModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */