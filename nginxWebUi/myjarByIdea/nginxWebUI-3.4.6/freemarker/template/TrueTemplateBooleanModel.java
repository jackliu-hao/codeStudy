package freemarker.template;

final class TrueTemplateBooleanModel implements SerializableTemplateBooleanModel {
   public boolean getAsBoolean() {
      return true;
   }

   private Object readResolve() {
      return TRUE;
   }
}
