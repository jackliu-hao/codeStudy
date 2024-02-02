package freemarker.template;

import java.util.List;

public interface TemplateMethodModelEx extends TemplateMethodModel {
  Object exec(List paramList) throws TemplateModelException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\TemplateMethodModelEx.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */