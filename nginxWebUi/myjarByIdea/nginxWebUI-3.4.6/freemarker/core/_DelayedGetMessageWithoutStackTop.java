package freemarker.core;

import freemarker.template.TemplateException;

public class _DelayedGetMessageWithoutStackTop extends _DelayedConversionToString {
   public _DelayedGetMessageWithoutStackTop(TemplateException exception) {
      super(exception);
   }

   protected String doConversion(Object obj) {
      return ((TemplateException)obj).getMessageWithoutStackTop();
   }
}
