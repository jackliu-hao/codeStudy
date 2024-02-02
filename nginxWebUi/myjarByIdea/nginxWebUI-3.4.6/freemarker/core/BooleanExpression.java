package freemarker.core;

import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

abstract class BooleanExpression extends Expression {
   TemplateModel _eval(Environment env) throws TemplateException {
      return this.evalToBoolean(env) ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
   }
}
