package freemarker.template.utility;

public class UnrecognizedTimeZoneException extends Exception {
   private final String timeZoneName;

   public UnrecognizedTimeZoneException(String timeZoneName) {
      super("Unrecognized time zone: " + StringUtil.jQuote(timeZoneName));
      this.timeZoneName = timeZoneName;
   }

   public String getTimeZoneName() {
      return this.timeZoneName;
   }
}
