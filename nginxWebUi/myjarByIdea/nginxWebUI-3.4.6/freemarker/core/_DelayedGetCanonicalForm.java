package freemarker.core;

public class _DelayedGetCanonicalForm extends _DelayedConversionToString {
   public _DelayedGetCanonicalForm(TemplateObject obj) {
      super(obj);
   }

   protected String doConversion(Object obj) {
      try {
         return ((TemplateObject)obj).getCanonicalForm();
      } catch (Exception var3) {
         return "{Error getting canonical form}";
      }
   }
}
