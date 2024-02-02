package freemarker.template;

import freemarker.ext.util.WrapperTemplateModel;
import freemarker.template.utility.ObjectWrapperWithAPISupport;
import java.io.Serializable;
import java.util.Enumeration;

public class DefaultEnumerationAdapter extends WrappingTemplateModel implements TemplateCollectionModel, AdapterTemplateModel, WrapperTemplateModel, TemplateModelWithAPISupport, Serializable {
   private final Enumeration<?> enumeration;
   private boolean enumerationOwnedBySomeone;

   public static DefaultEnumerationAdapter adapt(Enumeration<?> enumeration, ObjectWrapper wrapper) {
      return new DefaultEnumerationAdapter(enumeration, wrapper);
   }

   private DefaultEnumerationAdapter(Enumeration<?> enumeration, ObjectWrapper wrapper) {
      super(wrapper);
      this.enumeration = enumeration;
   }

   public Object getWrappedObject() {
      return this.enumeration;
   }

   public Object getAdaptedObject(Class<?> hint) {
      return this.getWrappedObject();
   }

   public TemplateModelIterator iterator() throws TemplateModelException {
      return new SimpleTemplateModelIterator();
   }

   public TemplateModel getAPI() throws TemplateModelException {
      return ((ObjectWrapperWithAPISupport)this.getObjectWrapper()).wrapAsAPI(this.enumeration);
   }

   private class SimpleTemplateModelIterator implements TemplateModelIterator {
      private boolean enumerationOwnedByMe;

      private SimpleTemplateModelIterator() {
      }

      public TemplateModel next() throws TemplateModelException {
         if (!this.enumerationOwnedByMe) {
            this.checkNotOwner();
            DefaultEnumerationAdapter.this.enumerationOwnedBySomeone = true;
            this.enumerationOwnedByMe = true;
         }

         if (!DefaultEnumerationAdapter.this.enumeration.hasMoreElements()) {
            throw new TemplateModelException("The collection has no more items.");
         } else {
            Object value = DefaultEnumerationAdapter.this.enumeration.nextElement();
            return value instanceof TemplateModel ? (TemplateModel)value : DefaultEnumerationAdapter.this.wrap(value);
         }
      }

      public boolean hasNext() throws TemplateModelException {
         if (!this.enumerationOwnedByMe) {
            this.checkNotOwner();
         }

         return DefaultEnumerationAdapter.this.enumeration.hasMoreElements();
      }

      private void checkNotOwner() throws TemplateModelException {
         if (DefaultEnumerationAdapter.this.enumerationOwnedBySomeone) {
            throw new TemplateModelException("This collection value wraps a java.util.Enumeration, thus it can be listed only once.");
         }
      }

      // $FF: synthetic method
      SimpleTemplateModelIterator(Object x1) {
         this();
      }
   }
}
