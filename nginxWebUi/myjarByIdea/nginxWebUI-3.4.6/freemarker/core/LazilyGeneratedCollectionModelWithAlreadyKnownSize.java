package freemarker.core;

import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;

final class LazilyGeneratedCollectionModelWithAlreadyKnownSize extends LazilyGeneratedCollectionModelEx {
   private final int size;

   LazilyGeneratedCollectionModelWithAlreadyKnownSize(TemplateModelIterator iterator, int size, boolean sequence) {
      super(iterator, sequence);
      this.size = size;
   }

   public int size() throws TemplateModelException {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   protected LazilyGeneratedCollectionModel withIsSequenceFromFalseToTrue() {
      return new LazilyGeneratedCollectionModelWithAlreadyKnownSize(this.getIterator(), this.size, true);
   }
}
