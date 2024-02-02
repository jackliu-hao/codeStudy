package freemarker.template;

public interface TemplateNodeModelEx extends TemplateNodeModel {
   TemplateNodeModelEx getPreviousSibling() throws TemplateModelException;

   TemplateNodeModelEx getNextSibling() throws TemplateModelException;
}
