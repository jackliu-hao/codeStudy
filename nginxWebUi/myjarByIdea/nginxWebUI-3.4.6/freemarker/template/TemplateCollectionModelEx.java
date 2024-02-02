package freemarker.template;

public interface TemplateCollectionModelEx extends TemplateCollectionModel {
   int size() throws TemplateModelException;

   boolean isEmpty() throws TemplateModelException;
}
