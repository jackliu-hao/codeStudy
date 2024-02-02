package freemarker.core;

import freemarker.template.TemplateModelException;

final class NonListableRightUnboundedRangeModel extends RightUnboundedRangeModel {
   NonListableRightUnboundedRangeModel(int begin) {
      super(begin);
   }

   public int size() throws TemplateModelException {
      return 0;
   }
}
