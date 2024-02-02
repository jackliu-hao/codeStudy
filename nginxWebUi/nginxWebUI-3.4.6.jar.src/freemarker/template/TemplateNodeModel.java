package freemarker.template;

public interface TemplateNodeModel extends TemplateModel {
  TemplateNodeModel getParentNode() throws TemplateModelException;
  
  TemplateSequenceModel getChildNodes() throws TemplateModelException;
  
  String getNodeName() throws TemplateModelException;
  
  String getNodeType() throws TemplateModelException;
  
  String getNodeNamespace() throws TemplateModelException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\TemplateNodeModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */