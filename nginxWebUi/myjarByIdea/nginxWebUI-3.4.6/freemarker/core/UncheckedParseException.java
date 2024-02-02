package freemarker.core;

final class UncheckedParseException extends RuntimeException {
   private final ParseException parseException;

   public UncheckedParseException(ParseException parseException) {
      this.parseException = parseException;
   }

   public ParseException getParseException() {
      return this.parseException;
   }
}
