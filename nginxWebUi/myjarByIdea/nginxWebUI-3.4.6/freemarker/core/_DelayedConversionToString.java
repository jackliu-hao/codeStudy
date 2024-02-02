package freemarker.core;

public abstract class _DelayedConversionToString {
   private static final String NOT_SET = new String();
   private Object object;
   private volatile String stringValue;

   public _DelayedConversionToString(Object object) {
      this.stringValue = NOT_SET;
      this.object = object;
   }

   public String toString() {
      String stringValue = this.stringValue;
      if (stringValue == NOT_SET) {
         synchronized(this) {
            stringValue = this.stringValue;
            if (stringValue == NOT_SET) {
               stringValue = this.doConversion(this.object);
               this.stringValue = stringValue;
               this.object = null;
            }
         }
      }

      return stringValue;
   }

   protected abstract String doConversion(Object var1);
}
