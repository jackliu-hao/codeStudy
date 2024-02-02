package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

abstract class BuiltInForHashEx extends BuiltIn {
   TemplateModel _eval(Environment env) throws TemplateException {
      TemplateModel model = this.target.eval(env);
      if (model instanceof TemplateHashModelEx) {
         return this.calculateResult((TemplateHashModelEx)model, env);
      } else {
         throw new NonExtendedHashException(this.target, model, env);
      }
   }

   abstract TemplateModel calculateResult(TemplateHashModelEx var1, Environment var2) throws TemplateModelException, InvalidReferenceException;

   protected InvalidReferenceException newNullPropertyException(String propertyName, TemplateModel tm, Environment env) {
      return env.getFastInvalidReferenceExceptions() ? InvalidReferenceException.FAST_INSTANCE : new InvalidReferenceException((new _ErrorDescriptionBuilder(new Object[]{"The exteneded hash (of class ", tm.getClass().getName(), ") has returned null for its \"", propertyName, "\" property. This is maybe a bug. The extended hash was returned by this expression:"})).blame(this.target), env, this);
   }
}
