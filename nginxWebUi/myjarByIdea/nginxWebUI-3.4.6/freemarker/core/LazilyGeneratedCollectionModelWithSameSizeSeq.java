package freemarker.core;

import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.utility.NullArgumentException;

class LazilyGeneratedCollectionModelWithSameSizeSeq extends LazilyGeneratedCollectionModelEx {
   private final TemplateSequenceModel sizeSourceSeq;

   public LazilyGeneratedCollectionModelWithSameSizeSeq(TemplateModelIterator iterator, TemplateSequenceModel sizeSourceSeq) {
      super(iterator, true);
      NullArgumentException.check(sizeSourceSeq);
      this.sizeSourceSeq = sizeSourceSeq;
   }

   public int size() throws TemplateModelException {
      return this.sizeSourceSeq.size();
   }

   public boolean isEmpty() throws TemplateModelException {
      return this.sizeSourceSeq.size() == 0;
   }

   protected LazilyGeneratedCollectionModelWithSameSizeSeq withIsSequenceFromFalseToTrue() {
      return this;
   }
}
