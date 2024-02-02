package freemarker.template;

public interface TemplateHashModelEx extends TemplateHashModel {
  int size() throws TemplateModelException;
  
  TemplateCollectionModel keys() throws TemplateModelException;
  
  TemplateCollectionModel values() throws TemplateModelException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\TemplateHashModelEx.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */