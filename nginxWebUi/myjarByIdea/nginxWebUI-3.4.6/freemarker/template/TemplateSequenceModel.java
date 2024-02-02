package freemarker.template;

public interface TemplateSequenceModel extends TemplateModel {
   TemplateModel get(int var1) throws TemplateModelException;

   int size() throws TemplateModelException;
}
