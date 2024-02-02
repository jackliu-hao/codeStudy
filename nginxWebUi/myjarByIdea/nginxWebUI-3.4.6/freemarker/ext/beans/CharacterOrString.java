package freemarker.ext.beans;

final class CharacterOrString {
   private final String stringValue;

   CharacterOrString(String stringValue) {
      this.stringValue = stringValue;
   }

   String getAsString() {
      return this.stringValue;
   }

   char getAsChar() {
      return this.stringValue.charAt(0);
   }
}
