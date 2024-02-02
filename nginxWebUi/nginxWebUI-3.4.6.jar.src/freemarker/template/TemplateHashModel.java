package freemarker.template;

public interface TemplateHashModel extends TemplateModel {
  TemplateModel get(String paramString) throws TemplateModelException;
  
  boolean isEmpty() throws TemplateModelException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\TemplateHashModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */