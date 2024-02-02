package freemarker.template;

public interface TemplateScalarModel extends TemplateModel {
   TemplateModel EMPTY_STRING = new SimpleScalar("");

   String getAsString() throws TemplateModelException;
}
