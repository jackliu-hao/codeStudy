package freemarker.template;

public interface TemplateModelIterator {
   TemplateModel next() throws TemplateModelException;

   boolean hasNext() throws TemplateModelException;
}
