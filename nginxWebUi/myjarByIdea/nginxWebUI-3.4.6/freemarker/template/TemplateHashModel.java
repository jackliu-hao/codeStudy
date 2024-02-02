package freemarker.template;

public interface TemplateHashModel extends TemplateModel {
   TemplateModel get(String var1) throws TemplateModelException;

   boolean isEmpty() throws TemplateModelException;
}
