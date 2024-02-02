package freemarker.template;

public interface TemplateSequenceModel extends TemplateModel {
  TemplateModel get(int paramInt) throws TemplateModelException;
  
  int size() throws TemplateModelException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\TemplateSequenceModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */