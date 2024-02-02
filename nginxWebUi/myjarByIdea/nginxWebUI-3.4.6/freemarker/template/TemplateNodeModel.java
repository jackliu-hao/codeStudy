package freemarker.template;

public interface TemplateNodeModel extends TemplateModel {
   TemplateNodeModel getParentNode() throws TemplateModelException;

   TemplateSequenceModel getChildNodes() throws TemplateModelException;

   String getNodeName() throws TemplateModelException;

   String getNodeType() throws TemplateModelException;

   String getNodeNamespace() throws TemplateModelException;
}
