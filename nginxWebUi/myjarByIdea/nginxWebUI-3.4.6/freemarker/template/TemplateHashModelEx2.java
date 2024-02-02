package freemarker.template;

public interface TemplateHashModelEx2 extends TemplateHashModelEx {
   KeyValuePairIterator keyValuePairIterator() throws TemplateModelException;

   public interface KeyValuePairIterator {
      boolean hasNext() throws TemplateModelException;

      KeyValuePair next() throws TemplateModelException;
   }

   public interface KeyValuePair {
      TemplateModel getKey() throws TemplateModelException;

      TemplateModel getValue() throws TemplateModelException;
   }
}
