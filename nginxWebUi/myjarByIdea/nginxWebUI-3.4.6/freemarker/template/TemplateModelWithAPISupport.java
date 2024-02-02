package freemarker.template;

public interface TemplateModelWithAPISupport extends TemplateModel {
   TemplateModel getAPI() throws TemplateModelException;
}
