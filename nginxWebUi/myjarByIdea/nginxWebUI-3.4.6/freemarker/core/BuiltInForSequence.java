package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;

abstract class BuiltInForSequence extends BuiltIn {
   TemplateModel _eval(Environment env) throws TemplateException {
      TemplateModel model = this.target.eval(env);
      if (!(model instanceof TemplateSequenceModel)) {
         throw new NonSequenceException(this.target, model, env);
      } else {
         return this.calculateResult((TemplateSequenceModel)model);
      }
   }

   abstract TemplateModel calculateResult(TemplateSequenceModel var1) throws TemplateModelException;
}
