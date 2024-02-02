package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

abstract class BuiltInForString extends BuiltIn {
   TemplateModel _eval(Environment env) throws TemplateException {
      return this.calculateResult(getTargetString(this.target, env), env);
   }

   abstract TemplateModel calculateResult(String var1, Environment var2) throws TemplateException;

   static String getTargetString(Expression target, Environment env) throws TemplateException {
      return target.evalAndCoerceToStringOrUnsupportedMarkup(env);
   }
}
