package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.utility.NullArgumentException;

abstract class MarkupOutputFormatBoundBuiltIn extends SpecialBuiltIn {
   protected MarkupOutputFormat outputFormat;

   void bindToMarkupOutputFormat(MarkupOutputFormat outputFormat) {
      NullArgumentException.check(outputFormat);
      this.outputFormat = outputFormat;
   }

   TemplateModel _eval(Environment env) throws TemplateException {
      if (this.outputFormat == null) {
         throw new NullPointerException("outputFormat was null");
      } else {
         return this.calculateResult(env);
      }
   }

   protected abstract TemplateModel calculateResult(Environment var1) throws TemplateException;
}
