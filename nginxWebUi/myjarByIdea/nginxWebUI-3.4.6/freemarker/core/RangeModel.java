package freemarker.core;

import freemarker.template.SimpleNumber;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;
import java.io.Serializable;

abstract class RangeModel implements TemplateSequenceModel, Serializable {
   private final int begin;

   public RangeModel(int begin) {
      this.begin = begin;
   }

   final int getBegining() {
      return this.begin;
   }

   public final TemplateModel get(int index) throws TemplateModelException {
      if (index >= 0 && index < this.size()) {
         long value = (long)this.begin + (long)this.getStep() * (long)index;
         return value <= 2147483647L ? new SimpleNumber((int)value) : new SimpleNumber(value);
      } else {
         throw new _TemplateModelException(new Object[]{"Range item index ", index, " is out of bounds."});
      }
   }

   abstract int getStep();

   abstract boolean isRightUnbounded();

   abstract boolean isRightAdaptive();

   abstract boolean isAffectedByStringSlicingBug();
}
