package freemarker.ext.beans;

import freemarker.ext.util.ModelFactory;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateSequenceModel;
import java.util.Collection;
import java.util.List;

public class CollectionModel extends StringModel implements TemplateCollectionModel, TemplateSequenceModel {
   static final ModelFactory FACTORY = new ModelFactory() {
      public TemplateModel create(Object object, ObjectWrapper wrapper) {
         return new CollectionModel((Collection)object, (BeansWrapper)wrapper);
      }
   };

   public CollectionModel(Collection collection, BeansWrapper wrapper) {
      super(collection, wrapper);
   }

   public TemplateModel get(int index) throws TemplateModelException {
      if (this.object instanceof List) {
         try {
            return this.wrap(((List)this.object).get(index));
         } catch (IndexOutOfBoundsException var3) {
            return null;
         }
      } else {
         throw new TemplateModelException("Underlying collection is not a list, it's " + this.object.getClass().getName());
      }
   }

   public boolean getSupportsIndexedAccess() {
      return this.object instanceof List;
   }

   public TemplateModelIterator iterator() {
      return new IteratorModel(((Collection)this.object).iterator(), this.wrapper);
   }

   public int size() {
      return ((Collection)this.object).size();
   }
}
