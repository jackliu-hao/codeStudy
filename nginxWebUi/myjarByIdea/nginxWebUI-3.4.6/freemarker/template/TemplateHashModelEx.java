package freemarker.template;

public interface TemplateHashModelEx extends TemplateHashModel {
   int size() throws TemplateModelException;

   TemplateCollectionModel keys() throws TemplateModelException;

   TemplateCollectionModel values() throws TemplateModelException;
}
