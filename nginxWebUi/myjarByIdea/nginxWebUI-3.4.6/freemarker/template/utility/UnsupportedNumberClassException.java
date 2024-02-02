package freemarker.template.utility;

public class UnsupportedNumberClassException extends RuntimeException {
   private final Class fClass;

   public UnsupportedNumberClassException(Class pClass) {
      super("Unsupported number class: " + pClass.getName());
      this.fClass = pClass;
   }

   public Class getUnsupportedClass() {
      return this.fClass;
   }
}
