package freemarker.core;

import freemarker.template.TemplateException;

abstract class Interpolation extends TemplateElement {
   protected abstract String dump(boolean var1, boolean var2);

   protected final String dump(boolean canonical) {
      return this.dump(canonical, false);
   }

   final String getCanonicalFormInStringLiteral() {
      return this.dump(true, true);
   }

   protected abstract Object calculateInterpolatedStringOrMarkup(Environment var1) throws TemplateException;

   boolean isShownInStackTrace() {
      return true;
   }
}
