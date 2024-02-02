package freemarker.template;

public interface TemplateBooleanModel extends TemplateModel {
   TemplateBooleanModel FALSE = new FalseTemplateBooleanModel();
   TemplateBooleanModel TRUE = new TrueTemplateBooleanModel();

   boolean getAsBoolean() throws TemplateModelException;
}
