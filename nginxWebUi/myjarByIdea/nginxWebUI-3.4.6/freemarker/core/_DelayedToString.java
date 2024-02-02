package freemarker.core;

public class _DelayedToString extends _DelayedConversionToString {
   public _DelayedToString(Object object) {
      super(object);
   }

   public _DelayedToString(int object) {
      super(object);
   }

   protected String doConversion(Object obj) {
      return String.valueOf(obj);
   }
}
