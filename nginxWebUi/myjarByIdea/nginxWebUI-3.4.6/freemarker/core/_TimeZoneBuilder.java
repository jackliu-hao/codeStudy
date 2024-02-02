package freemarker.core;

import java.util.TimeZone;

public class _TimeZoneBuilder {
   private final String timeZoneId;

   public _TimeZoneBuilder(String timeZoneId) {
      this.timeZoneId = timeZoneId;
   }

   public TimeZone build() {
      TimeZone timeZone = TimeZone.getTimeZone(this.timeZoneId);
      if (timeZone.getID().equals("GMT") && !this.timeZoneId.equals("GMT") && !this.timeZoneId.equals("UTC") && !this.timeZoneId.equals("GMT+00") && !this.timeZoneId.equals("GMT+00:00") && !this.timeZoneId.equals("GMT+0000")) {
         throw new IllegalArgumentException("Unrecognized time zone: " + this.timeZoneId);
      } else {
         return timeZone;
      }
   }
}
