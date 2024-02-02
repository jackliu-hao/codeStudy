package freemarker.core;

import freemarker.template.utility.StringUtil;

public class _DelayedJQuote extends _DelayedConversionToString {
   public _DelayedJQuote(Object object) {
      super(object);
   }

   protected String doConversion(Object obj) {
      return StringUtil.jQuote(_ErrorDescriptionBuilder.toString(obj));
   }
}
