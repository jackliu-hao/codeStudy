package freemarker.core;

import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import java.util.Date;

abstract class BuiltInForDate extends BuiltIn {
   TemplateModel _eval(Environment env) throws TemplateException {
      TemplateModel model = this.target.eval(env);
      if (model instanceof TemplateDateModel) {
         TemplateDateModel tdm = (TemplateDateModel)model;
         return this.calculateResult(EvalUtil.modelToDate(tdm, this.target), tdm.getDateType(), env);
      } else {
         throw newNonDateException(env, model, this.target);
      }
   }

   protected abstract TemplateModel calculateResult(Date var1, int var2, Environment var3) throws TemplateException;

   static TemplateException newNonDateException(Environment env, TemplateModel model, Expression target) throws InvalidReferenceException {
      Object e;
      if (model == null) {
         e = InvalidReferenceException.getInstance(target, env);
      } else {
         e = new NonDateException(target, model, "date", env);
      }

      return (TemplateException)e;
   }
}
