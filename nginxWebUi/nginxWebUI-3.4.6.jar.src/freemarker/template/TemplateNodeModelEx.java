package freemarker.template;

public interface TemplateNodeModelEx extends TemplateNodeModel {
  TemplateNodeModelEx getPreviousSibling() throws TemplateModelException;
  
  TemplateNodeModelEx getNextSibling() throws TemplateModelException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\TemplateNodeModelEx.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */