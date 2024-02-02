package freemarker.ext.beans;

import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class IteratorModel extends BeanModel implements TemplateModelIterator, TemplateCollectionModel {
   private boolean accessed = false;

   public IteratorModel(Iterator iterator, BeansWrapper wrapper) {
      super(iterator, wrapper);
   }

   public TemplateModelIterator iterator() throws TemplateModelException {
      synchronized(this) {
         if (this.accessed) {
            throw new TemplateModelException("This collection is stateful and can not be iterated over the second time.");
         } else {
            this.accessed = true;
            return this;
         }
      }
   }

   public boolean hasNext() {
      return ((Iterator)this.object).hasNext();
   }

   public TemplateModel next() throws TemplateModelException {
      try {
         return this.wrap(((Iterator)this.object).next());
      } catch (NoSuchElementException var2) {
         throw new TemplateModelException("No more elements in the iterator.", var2);
      }
   }

   public boolean getAsBoolean() {
      return this.hasNext();
   }
}
