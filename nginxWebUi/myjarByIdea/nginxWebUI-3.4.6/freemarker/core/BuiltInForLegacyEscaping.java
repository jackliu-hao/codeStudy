package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

abstract class BuiltInForLegacyEscaping extends BuiltInBannedWhenAutoEscaping {
   TemplateModel _eval(Environment env) throws TemplateException {
      TemplateModel tm = this.target.eval(env);
      Object moOrStr = EvalUtil.coerceModelToStringOrMarkup(tm, this.target, (String)null, env);
      if (moOrStr instanceof String) {
         return this.calculateResult((String)moOrStr, env);
      } else {
         TemplateMarkupOutputModel<?> mo = (TemplateMarkupOutputModel)moOrStr;
         if (mo.getOutputFormat().isLegacyBuiltInBypassed(this.key)) {
            return mo;
         } else {
            throw new NonStringException(this.target, tm, env);
         }
      }
   }

   abstract TemplateModel calculateResult(String var1, Environment var2) throws TemplateException;
}
