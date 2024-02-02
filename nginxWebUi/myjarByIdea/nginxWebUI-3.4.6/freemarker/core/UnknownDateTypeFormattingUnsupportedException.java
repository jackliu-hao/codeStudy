package freemarker.core;

public final class UnknownDateTypeFormattingUnsupportedException extends UnformattableValueException {
   public UnknownDateTypeFormattingUnsupportedException() {
      super("Can't convert the date-like value to string because it isn't known if it's a date (no time part), time or date-time value.");
   }
}
