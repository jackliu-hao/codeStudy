package freemarker.core;

import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.utility.NullArgumentException;

class SingleIterationCollectionModel implements TemplateCollectionModel {
   private TemplateModelIterator iterator;

   SingleIterationCollectionModel(TemplateModelIterator iterator) {
      NullArgumentException.check(iterator);
      this.iterator = iterator;
   }

   public TemplateModelIterator iterator() throws TemplateModelException {
      if (this.iterator == null) {
         throw new IllegalStateException("Can't return the iterator again, as this TemplateCollectionModel can only be iterated once.");
      } else {
         TemplateModelIterator result = this.iterator;
         this.iterator = null;
         return result;
      }
   }

   protected TemplateModelIterator getIterator() {
      return this.iterator;
   }
}
