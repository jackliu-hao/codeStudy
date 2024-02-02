package freemarker.template;

import java.io.IOException;
import java.io.Writer;

public interface TemplateDirectiveBody {
  void render(Writer paramWriter) throws TemplateException, IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\TemplateDirectiveBody.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */