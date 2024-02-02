package freemarker.template.utility;

public class NullArgumentException extends IllegalArgumentException {
   public NullArgumentException() {
      super("The argument can't be null");
   }

   public NullArgumentException(String argumentName) {
      super("The \"" + argumentName + "\" argument can't be null");
   }

   public NullArgumentException(String argumentName, String details) {
      super("The \"" + argumentName + "\" argument can't be null. " + details);
   }

   public static void check(String argumentName, Object argumentValue) {
      if (argumentValue == null) {
         throw new NullArgumentException(argumentName);
      }
   }

   public static void check(Object argumentValue) {
      if (argumentValue == null) {
         throw new NullArgumentException();
      }
   }
}
