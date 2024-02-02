package freemarker.core;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

class BuiltInsForMarkupOutputs {
   static class markup_stringBI extends BuiltInForMarkupOutput {
      protected TemplateModel calculateResult(TemplateMarkupOutputModel model) throws TemplateModelException {
         return new SimpleScalar(model.getOutputFormat().getMarkupString(model));
      }
   }
}
