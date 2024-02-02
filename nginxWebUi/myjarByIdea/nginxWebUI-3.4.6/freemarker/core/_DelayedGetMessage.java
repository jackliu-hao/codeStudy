package freemarker.core;

public class _DelayedGetMessage extends _DelayedConversionToString {
   public _DelayedGetMessage(Throwable exception) {
      super(exception);
   }

   protected String doConversion(Object obj) {
      String message = ((Throwable)obj).getMessage();
      return message != null && message.length() != 0 ? message : "[No exception message]";
   }
}
