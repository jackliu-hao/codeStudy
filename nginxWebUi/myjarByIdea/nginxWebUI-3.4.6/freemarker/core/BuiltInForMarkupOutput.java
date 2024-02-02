package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

abstract class BuiltInForMarkupOutput extends BuiltIn {
   TemplateModel _eval(Environment env) throws TemplateException {
      TemplateModel model = this.target.eval(env);
      if (!(model instanceof TemplateMarkupOutputModel)) {
         throw new NonMarkupOutputException(this.target, model, env);
      } else {
         return this.calculateResult((TemplateMarkupOutputModel)model);
      }
   }

   protected abstract TemplateModel calculateResult(TemplateMarkupOutputModel var1) throws TemplateModelException;
}
