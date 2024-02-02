package freemarker.template;

import freemarker.ext.util.WrapperTemplateModel;
import freemarker.template.utility.ObjectWrapperWithAPISupport;
import java.io.Serializable;

public class DefaultIterableAdapter extends WrappingTemplateModel implements TemplateCollectionModel, AdapterTemplateModel, WrapperTemplateModel, TemplateModelWithAPISupport, Serializable {
   private final Iterable<?> iterable;

   public static DefaultIterableAdapter adapt(Iterable<?> iterable, ObjectWrapperWithAPISupport wrapper) {
      return new DefaultIterableAdapter(iterable, wrapper);
   }

   private DefaultIterableAdapter(Iterable<?> iterable, ObjectWrapperWithAPISupport wrapper) {
      super(wrapper);
      this.iterable = iterable;
   }

   public TemplateModelIterator iterator() throws TemplateModelException {
      return new IteratorToTemplateModelIteratorAdapter(this.iterable.iterator(), this.getObjectWrapper());
   }

   public Object getWrappedObject() {
      return this.iterable;
   }

   public Object getAdaptedObject(Class hint) {
      return this.getWrappedObject();
   }

   public TemplateModel getAPI() throws TemplateModelException {
      return ((ObjectWrapperWithAPISupport)this.getObjectWrapper()).wrapAsAPI(this.iterable);
   }
}
