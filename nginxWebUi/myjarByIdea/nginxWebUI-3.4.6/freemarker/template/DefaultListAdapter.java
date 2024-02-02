package freemarker.template;

import freemarker.ext.util.WrapperTemplateModel;
import freemarker.template.utility.ObjectWrapperWithAPISupport;
import freemarker.template.utility.RichObjectWrapper;
import java.io.Serializable;
import java.util.AbstractSequentialList;
import java.util.List;

public class DefaultListAdapter extends WrappingTemplateModel implements TemplateSequenceModel, AdapterTemplateModel, WrapperTemplateModel, TemplateModelWithAPISupport, Serializable {
   protected final List list;

   public static DefaultListAdapter adapt(List list, RichObjectWrapper wrapper) {
      return (DefaultListAdapter)(list instanceof AbstractSequentialList ? new DefaultListAdapterWithCollectionSupport(list, wrapper) : new DefaultListAdapter(list, wrapper));
   }

   private DefaultListAdapter(List list, RichObjectWrapper wrapper) {
      super(wrapper);
      this.list = list;
   }

   public TemplateModel get(int index) throws TemplateModelException {
      return index >= 0 && index < this.list.size() ? this.wrap(this.list.get(index)) : null;
   }

   public int size() throws TemplateModelException {
      return this.list.size();
   }

   public Object getAdaptedObject(Class hint) {
      return this.getWrappedObject();
   }

   public Object getWrappedObject() {
      return this.list;
   }

   public TemplateModel getAPI() throws TemplateModelException {
      return ((ObjectWrapperWithAPISupport)this.getObjectWrapper()).wrapAsAPI(this.list);
   }

   // $FF: synthetic method
   DefaultListAdapter(List x0, RichObjectWrapper x1, Object x2) {
      this(x0, x1);
   }

   private static class DefaultListAdapterWithCollectionSupport extends DefaultListAdapter implements TemplateCollectionModel {
      private DefaultListAdapterWithCollectionSupport(List list, RichObjectWrapper wrapper) {
         super(list, wrapper, null);
      }

      public TemplateModelIterator iterator() throws TemplateModelException {
         return new IteratorToTemplateModelIteratorAdapter(this.list.iterator(), this.getObjectWrapper());
      }

      // $FF: synthetic method
      DefaultListAdapterWithCollectionSupport(List x0, RichObjectWrapper x1, Object x2) {
         this(x0, x1);
      }
   }
}
