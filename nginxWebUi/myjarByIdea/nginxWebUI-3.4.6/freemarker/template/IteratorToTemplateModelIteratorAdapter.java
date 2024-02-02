package freemarker.template;

import java.util.Iterator;
import java.util.NoSuchElementException;

class IteratorToTemplateModelIteratorAdapter implements TemplateModelIterator {
   private final Iterator<?> it;
   private final ObjectWrapper wrapper;

   IteratorToTemplateModelIteratorAdapter(Iterator<?> it, ObjectWrapper wrapper) {
      this.it = it;
      this.wrapper = wrapper;
   }

   public TemplateModel next() throws TemplateModelException {
      try {
         return this.wrapper.wrap(this.it.next());
      } catch (NoSuchElementException var2) {
         throw new TemplateModelException("The collection has no more items.", var2);
      }
   }

   public boolean hasNext() throws TemplateModelException {
      return this.it.hasNext();
   }
}
