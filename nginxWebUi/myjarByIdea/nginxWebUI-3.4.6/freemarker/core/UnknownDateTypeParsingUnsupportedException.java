package freemarker.core;

public final class UnknownDateTypeParsingUnsupportedException extends UnformattableValueException {
   public UnknownDateTypeParsingUnsupportedException() {
      super("Can't parse the string to date-like value because it isn't known if it's desired result should be a date (no time part), a time, or a date-time value.");
   }
}
