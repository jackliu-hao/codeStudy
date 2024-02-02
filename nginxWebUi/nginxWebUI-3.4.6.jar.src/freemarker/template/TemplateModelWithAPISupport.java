package freemarker.template;

public interface TemplateModelWithAPISupport extends TemplateModel {
  TemplateModel getAPI() throws TemplateModelException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\TemplateModelWithAPISupport.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */