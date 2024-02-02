package freemarker.template;

final class FalseTemplateBooleanModel implements SerializableTemplateBooleanModel {
   public boolean getAsBoolean() {
      return false;
   }

   private Object readResolve() {
      return FALSE;
   }
}
