package freemarker.template;

public interface TemplateCollectionModel extends TemplateModel {
   TemplateModelIterator iterator() throws TemplateModelException;
}
