/*     */ package com.google.zxing.client.result;
/*     */ 
/*     */ import java.text.DateFormat;
/*     */ import java.text.ParseException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.GregorianCalendar;
/*     */ import java.util.Locale;
/*     */ import java.util.TimeZone;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class CalendarParsedResult
/*     */   extends ParsedResult
/*     */ {
/*  39 */   private static final Pattern RFC2445_DURATION = Pattern.compile("P(?:(\\d+)W)?(?:(\\d+)D)?(?:T(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)S)?)?");
/*  40 */   private static final long[] RFC2445_DURATION_FIELD_UNITS = new long[] { 604800000L, 86400000L, 3600000L, 60000L, 1000L };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  48 */   private static final Pattern DATE_TIME = Pattern.compile("[0-9]{8}(T[0-9]{6}Z?)?");
/*     */   
/*     */   private final String summary;
/*     */   
/*     */   private final Date start;
/*     */   
/*     */   private final boolean startAllDay;
/*     */   
/*     */   private final Date end;
/*     */   
/*     */   private final boolean endAllDay;
/*     */   
/*     */   private final String location;
/*     */   
/*     */   private final String organizer;
/*     */   
/*     */   private final String[] attendees;
/*     */   
/*     */   private final String description;
/*     */   
/*     */   private final double latitude;
/*     */   private final double longitude;
/*     */   
/*     */   public CalendarParsedResult(String summary, String startString, String endString, String durationString, String location, String organizer, String[] attendees, String description, double latitude, double longitude) {
/*  72 */     super(ParsedResultType.CALENDAR);
/*  73 */     this.summary = summary;
/*     */     
/*     */     try {
/*  76 */       this.start = parseDate(startString);
/*  77 */     } catch (ParseException pe) {
/*  78 */       throw new IllegalArgumentException(pe.toString());
/*     */     } 
/*     */     
/*  81 */     if (endString == null) {
/*  82 */       long durationMS = parseDurationMS(durationString);
/*  83 */       this.end = (durationMS < 0L) ? null : new Date(this.start.getTime() + durationMS);
/*     */     } else {
/*     */       try {
/*  86 */         this.end = parseDate(endString);
/*  87 */       } catch (ParseException pe) {
/*  88 */         throw new IllegalArgumentException(pe.toString());
/*     */       } 
/*     */     } 
/*     */     
/*  92 */     this.startAllDay = (startString.length() == 8);
/*  93 */     this.endAllDay = (endString != null && endString.length() == 8);
/*     */     
/*  95 */     this.location = location;
/*  96 */     this.organizer = organizer;
/*  97 */     this.attendees = attendees;
/*  98 */     this.description = description;
/*  99 */     this.latitude = latitude;
/* 100 */     this.longitude = longitude;
/*     */   }
/*     */   
/*     */   public String getSummary() {
/* 104 */     return this.summary;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Date getStart() {
/* 111 */     return this.start;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isStartAllDay() {
/* 118 */     return this.startAllDay;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Date getEnd() {
/* 126 */     return this.end;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEndAllDay() {
/* 133 */     return this.endAllDay;
/*     */   }
/*     */   
/*     */   public String getLocation() {
/* 137 */     return this.location;
/*     */   }
/*     */   
/*     */   public String getOrganizer() {
/* 141 */     return this.organizer;
/*     */   }
/*     */   
/*     */   public String[] getAttendees() {
/* 145 */     return this.attendees;
/*     */   }
/*     */   
/*     */   public String getDescription() {
/* 149 */     return this.description;
/*     */   }
/*     */   
/*     */   public double getLatitude() {
/* 153 */     return this.latitude;
/*     */   }
/*     */   
/*     */   public double getLongitude() {
/* 157 */     return this.longitude;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDisplayResult() {
/* 162 */     StringBuilder result = new StringBuilder(100);
/* 163 */     maybeAppend(this.summary, result);
/* 164 */     maybeAppend(format(this.startAllDay, this.start), result);
/* 165 */     maybeAppend(format(this.endAllDay, this.end), result);
/* 166 */     maybeAppend(this.location, result);
/* 167 */     maybeAppend(this.organizer, result);
/* 168 */     maybeAppend(this.attendees, result);
/* 169 */     maybeAppend(this.description, result);
/* 170 */     return result.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Date parseDate(String when) throws ParseException {
/*     */     Date date;
/* 181 */     if (!DATE_TIME.matcher(when).matches()) {
/* 182 */       throw new ParseException(when, 0);
/*     */     }
/* 184 */     if (when.length() == 8)
/*     */     {
/* 186 */       return buildDateFormat().parse(when);
/*     */     }
/*     */ 
/*     */     
/* 190 */     if (when.length() == 16 && when.charAt(15) == 'Z') {
/* 191 */       date = buildDateTimeFormat().parse(when.substring(0, 15));
/* 192 */       Calendar calendar = new GregorianCalendar();
/*     */ 
/*     */       
/* 195 */       long milliseconds = date.getTime() + calendar.get(15);
/*     */ 
/*     */       
/* 198 */       calendar.setTime(new Date(milliseconds));
/* 199 */       milliseconds += calendar.get(16);
/* 200 */       date = new Date(milliseconds);
/*     */     } else {
/* 202 */       date = buildDateTimeFormat().parse(when);
/*     */     } 
/* 204 */     return date;
/*     */   }
/*     */ 
/*     */   
/*     */   private static String format(boolean allDay, Date date) {
/* 209 */     if (date == null) {
/* 210 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 215 */     return (allDay ? DateFormat.getDateInstance(2) : DateFormat.getDateTimeInstance(2, 2)).format(date);
/*     */   }
/*     */   
/*     */   private static long parseDurationMS(CharSequence durationString) {
/* 219 */     if (durationString == null) {
/* 220 */       return -1L;
/*     */     }
/*     */     Matcher m;
/* 223 */     if (!(m = RFC2445_DURATION.matcher(durationString)).matches()) {
/* 224 */       return -1L;
/*     */     }
/* 226 */     long durationMS = 0L;
/* 227 */     for (int i = 0; i < RFC2445_DURATION_FIELD_UNITS.length; i++) {
/*     */       String fieldValue;
/* 229 */       if ((fieldValue = m.group(i + 1)) != null) {
/* 230 */         durationMS += RFC2445_DURATION_FIELD_UNITS[i] * Integer.parseInt(fieldValue);
/*     */       }
/*     */     } 
/* 233 */     return durationMS;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static DateFormat buildDateFormat() {
/*     */     DateFormat format;
/* 241 */     (format = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH)).setTimeZone(TimeZone.getTimeZone("GMT"));
/* 242 */     return format;
/*     */   }
/*     */   
/*     */   private static DateFormat buildDateTimeFormat() {
/* 246 */     return new SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.ENGLISH);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\result\CalendarParsedResult.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */