package freemarker.template;

public interface TemplateNumberModel extends TemplateModel {
   Number getAsNumber() throws TemplateModelException;
}
