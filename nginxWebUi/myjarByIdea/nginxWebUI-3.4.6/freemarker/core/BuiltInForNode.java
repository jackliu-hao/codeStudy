package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNodeModel;

abstract class BuiltInForNode extends BuiltIn {
   TemplateModel _eval(Environment env) throws TemplateException {
      TemplateModel model = this.target.eval(env);
      if (model instanceof TemplateNodeModel) {
         return this.calculateResult((TemplateNodeModel)model, env);
      } else {
         throw new NonNodeException(this.target, model, env);
      }
   }

   abstract TemplateModel calculateResult(TemplateNodeModel var1, Environment var2) throws TemplateModelException;
}
