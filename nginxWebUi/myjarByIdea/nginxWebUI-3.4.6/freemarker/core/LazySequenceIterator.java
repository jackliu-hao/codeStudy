package freemarker.core;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateSequenceModel;

class LazySequenceIterator implements TemplateModelIterator {
   private final TemplateSequenceModel sequence;
   private Integer size;
   private int index = 0;

   LazySequenceIterator(TemplateSequenceModel sequence) throws TemplateModelException {
      this.sequence = sequence;
   }

   public TemplateModel next() throws TemplateModelException {
      return this.sequence.get(this.index++);
   }

   public boolean hasNext() {
      if (this.size == null) {
         try {
            this.size = this.sequence.size();
         } catch (TemplateModelException var2) {
            throw new RuntimeException("Error when getting sequence size", var2);
         }
      }

      return this.index < this.size;
   }
}
