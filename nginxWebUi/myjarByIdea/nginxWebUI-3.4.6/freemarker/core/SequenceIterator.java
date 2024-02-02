package freemarker.core;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateSequenceModel;

class SequenceIterator implements TemplateModelIterator {
   private final TemplateSequenceModel sequence;
   private final int size;
   private int index = 0;

   SequenceIterator(TemplateSequenceModel sequence) throws TemplateModelException {
      this.sequence = sequence;
      this.size = sequence.size();
   }

   public TemplateModel next() throws TemplateModelException {
      return this.sequence.get(this.index++);
   }

   public boolean hasNext() {
      return this.index < this.size;
   }
}
