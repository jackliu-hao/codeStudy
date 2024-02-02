package freemarker.core;

import freemarker.template.TemplateCollectionModelEx;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.utility.NullArgumentException;

class LazilyGeneratedCollectionModelWithSameSizeCollEx extends LazilyGeneratedCollectionModelEx {
   private final TemplateCollectionModelEx sizeSourceCollEx;

   public LazilyGeneratedCollectionModelWithSameSizeCollEx(TemplateModelIterator iterator, TemplateCollectionModelEx sizeSourceCollEx, boolean sequenceSourced) {
      super(iterator, sequenceSourced);
      NullArgumentException.check(sizeSourceCollEx);
      this.sizeSourceCollEx = sizeSourceCollEx;
   }

   public int size() throws TemplateModelException {
      return this.sizeSourceCollEx.size();
   }

   public boolean isEmpty() throws TemplateModelException {
      return this.sizeSourceCollEx.isEmpty();
   }

   protected LazilyGeneratedCollectionModelWithSameSizeCollEx withIsSequenceFromFalseToTrue() {
      return new LazilyGeneratedCollectionModelWithSameSizeCollEx(this.getIterator(), this.sizeSourceCollEx, true);
   }
}
