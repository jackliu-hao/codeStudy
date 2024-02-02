package com.google.zxing.client.result;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CalendarParsedResult extends ParsedResult {
   private static final Pattern RFC2445_DURATION = Pattern.compile("P(?:(\\d+)W)?(?:(\\d+)D)?(?:T(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)S)?)?");
   private static final long[] RFC2445_DURATION_FIELD_UNITS = new long[]{604800000L, 86400000L, 3600000L, 60000L, 1000L};
   private static final Pattern DATE_TIME = Pattern.compile("[0-9]{8}(T[0-9]{6}Z?)?");
   private final String summary;
   private final Date start;
   private final boolean startAllDay;
   private final Date end;
   private final boolean endAllDay;
   private final String location;
   private final String organizer;
   private final String[] attendees;
   private final String description;
   private final double latitude;
   private final double longitude;

   public CalendarParsedResult(String summary, String startString, String endString, String durationString, String location, String organizer, String[] attendees, String description, double latitude, double longitude) {
      super(ParsedResultType.CALENDAR);
      this.summary = summary;

      try {
         this.start = parseDate(startString);
      } catch (ParseException var16) {
         throw new IllegalArgumentException(var16.toString());
      }

      if (endString == null) {
         long durationMS = parseDurationMS(durationString);
         this.end = durationMS < 0L ? null : new Date(this.start.getTime() + durationMS);
      } else {
         try {
            this.end = parseDate(endString);
         } catch (ParseException var15) {
            throw new IllegalArgumentException(var15.toString());
         }
      }

      this.startAllDay = startString.length() == 8;
      this.endAllDay = endString != null && endString.length() == 8;
      this.location = location;
      this.organizer = organizer;
      this.attendees = attendees;
      this.description = description;
      this.latitude = latitude;
      this.longitude = longitude;
   }

   public String getSummary() {
      return this.summary;
   }

   public Date getStart() {
      return this.start;
   }

   public boolean isStartAllDay() {
      return this.startAllDay;
   }

   public Date getEnd() {
      return this.end;
   }

   public boolean isEndAllDay() {
      return this.endAllDay;
   }

   public String getLocation() {
      return this.location;
   }

   public String getOrganizer() {
      return this.organizer;
   }

   public String[] getAttendees() {
      return this.attendees;
   }

   public String getDescription() {
      return this.description;
   }

   public double getLatitude() {
      return this.latitude;
   }

   public double getLongitude() {
      return this.longitude;
   }

   public String getDisplayResult() {
      StringBuilder result = new StringBuilder(100);
      maybeAppend(this.summary, result);
      maybeAppend(format(this.startAllDay, this.start), result);
      maybeAppend(format(this.endAllDay, this.end), result);
      maybeAppend(this.location, result);
      maybeAppend(this.organizer, result);
      maybeAppend(this.attendees, result);
      maybeAppend(this.description, result);
      return result.toString();
   }

   private static Date parseDate(String when) throws ParseException {
      if (!DATE_TIME.matcher(when).matches()) {
         throw new ParseException(when, 0);
      } else if (when.length() == 8) {
         return buildDateFormat().parse(when);
      } else {
         Date date;
         if (when.length() == 16 && when.charAt(15) == 'Z') {
            date = buildDateTimeFormat().parse(when.substring(0, 15));
            Calendar calendar = new GregorianCalendar();
            long milliseconds = date.getTime() + (long)calendar.get(15);
            calendar.setTime(new Date(milliseconds));
            milliseconds += (long)calendar.get(16);
            date = new Date(milliseconds);
         } else {
            date = buildDateTimeFormat().parse(when);
         }

         return date;
      }
   }

   private static String format(boolean allDay, Date date) {
      return date == null ? null : (allDay ? DateFormat.getDateInstance(2) : DateFormat.getDateTimeInstance(2, 2)).format(date);
   }

   private static long parseDurationMS(CharSequence durationString) {
      if (durationString == null) {
         return -1L;
      } else {
         Matcher m;
         if (!(m = RFC2445_DURATION.matcher(durationString)).matches()) {
            return -1L;
         } else {
            long durationMS = 0L;

            for(int i = 0; i < RFC2445_DURATION_FIELD_UNITS.length; ++i) {
               String fieldValue;
               if ((fieldValue = m.group(i + 1)) != null) {
                  durationMS += RFC2445_DURATION_FIELD_UNITS[i] * (long)Integer.parseInt(fieldValue);
               }
            }

            return durationMS;
         }
      }
   }

   private static DateFormat buildDateFormat() {
      SimpleDateFormat format;
      (format = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH)).setTimeZone(TimeZone.getTimeZone("GMT"));
      return format;
   }

   private static DateFormat buildDateTimeFormat() {
      return new SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.ENGLISH);
   }
}
