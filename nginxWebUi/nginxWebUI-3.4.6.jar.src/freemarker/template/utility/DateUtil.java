/*     */ package freemarker.template.utility;
/*     */ 
/*     */ import java.text.ParseException;
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
/*     */ public class DateUtil
/*     */ {
/*     */   public static final int ACCURACY_HOURS = 4;
/*     */   public static final int ACCURACY_MINUTES = 5;
/*     */   public static final int ACCURACY_SECONDS = 6;
/*     */   public static final int ACCURACY_MILLISECONDS = 7;
/*     */   public static final int ACCURACY_MILLISECONDS_FORCED = 8;
/*  61 */   public static final TimeZone UTC = TimeZone.getTimeZone("UTC");
/*     */ 
/*     */   
/*     */   private static final String REGEX_XS_TIME_ZONE = "Z|(?:[-+][0-9]{2}:[0-9]{2})";
/*     */ 
/*     */   
/*     */   private static final String REGEX_ISO8601_BASIC_TIME_ZONE = "Z|(?:[-+][0-9]{2}(?:[0-9]{2})?)";
/*     */ 
/*     */   
/*     */   private static final String REGEX_ISO8601_EXTENDED_TIME_ZONE = "Z|(?:[-+][0-9]{2}(?::[0-9]{2})?)";
/*     */ 
/*     */   
/*     */   private static final String REGEX_XS_OPTIONAL_TIME_ZONE = "(Z|(?:[-+][0-9]{2}:[0-9]{2}))?";
/*     */   
/*     */   private static final String REGEX_ISO8601_BASIC_OPTIONAL_TIME_ZONE = "(Z|(?:[-+][0-9]{2}(?:[0-9]{2})?))?";
/*     */   
/*     */   private static final String REGEX_ISO8601_EXTENDED_OPTIONAL_TIME_ZONE = "(Z|(?:[-+][0-9]{2}(?::[0-9]{2})?))?";
/*     */   
/*     */   private static final String REGEX_XS_DATE_BASE = "(-?[0-9]+)-([0-9]{2})-([0-9]{2})";
/*     */   
/*     */   private static final String REGEX_ISO8601_BASIC_DATE_BASE = "(-?[0-9]{4,}?)([0-9]{2})([0-9]{2})";
/*     */   
/*     */   private static final String REGEX_ISO8601_EXTENDED_DATE_BASE = "(-?[0-9]{4,})-([0-9]{2})-([0-9]{2})";
/*     */   
/*     */   private static final String REGEX_XS_TIME_BASE = "([0-9]{2}):([0-9]{2}):([0-9]{2})(?:\\.([0-9]+))?";
/*     */   
/*     */   private static final String REGEX_ISO8601_BASIC_TIME_BASE = "([0-9]{2})(?:([0-9]{2})(?:([0-9]{2})(?:[\\.,]([0-9]+))?)?)?";
/*     */   
/*     */   private static final String REGEX_ISO8601_EXTENDED_TIME_BASE = "([0-9]{2})(?::([0-9]{2})(?::([0-9]{2})(?:[\\.,]([0-9]+))?)?)?";
/*     */   
/*  91 */   private static final Pattern PATTERN_XS_DATE = Pattern.compile("(-?[0-9]+)-([0-9]{2})-([0-9]{2})(Z|(?:[-+][0-9]{2}:[0-9]{2}))?");
/*     */   
/*  93 */   private static final Pattern PATTERN_ISO8601_BASIC_DATE = Pattern.compile("(-?[0-9]{4,}?)([0-9]{2})([0-9]{2})");
/*     */   
/*  95 */   private static final Pattern PATTERN_ISO8601_EXTENDED_DATE = Pattern.compile("(-?[0-9]{4,})-([0-9]{2})-([0-9]{2})");
/*     */ 
/*     */   
/*  98 */   private static final Pattern PATTERN_XS_TIME = Pattern.compile("([0-9]{2}):([0-9]{2}):([0-9]{2})(?:\\.([0-9]+))?(Z|(?:[-+][0-9]{2}:[0-9]{2}))?");
/*     */   
/* 100 */   private static final Pattern PATTERN_ISO8601_BASIC_TIME = Pattern.compile("([0-9]{2})(?:([0-9]{2})(?:([0-9]{2})(?:[\\.,]([0-9]+))?)?)?(Z|(?:[-+][0-9]{2}(?:[0-9]{2})?))?");
/*     */   
/* 102 */   private static final Pattern PATTERN_ISO8601_EXTENDED_TIME = Pattern.compile("([0-9]{2})(?::([0-9]{2})(?::([0-9]{2})(?:[\\.,]([0-9]+))?)?)?(Z|(?:[-+][0-9]{2}(?::[0-9]{2})?))?");
/*     */ 
/*     */   
/* 105 */   private static final Pattern PATTERN_XS_DATE_TIME = Pattern.compile("(-?[0-9]+)-([0-9]{2})-([0-9]{2})T([0-9]{2}):([0-9]{2}):([0-9]{2})(?:\\.([0-9]+))?(Z|(?:[-+][0-9]{2}:[0-9]{2}))?");
/*     */ 
/*     */ 
/*     */   
/* 109 */   private static final Pattern PATTERN_ISO8601_BASIC_DATE_TIME = Pattern.compile("(-?[0-9]{4,}?)([0-9]{2})([0-9]{2})T([0-9]{2})(?:([0-9]{2})(?:([0-9]{2})(?:[\\.,]([0-9]+))?)?)?(Z|(?:[-+][0-9]{2}(?:[0-9]{2})?))?");
/*     */ 
/*     */ 
/*     */   
/* 113 */   private static final Pattern PATTERN_ISO8601_EXTENDED_DATE_TIME = Pattern.compile("(-?[0-9]{4,})-([0-9]{2})-([0-9]{2})T([0-9]{2})(?::([0-9]{2})(?::([0-9]{2})(?:[\\.,]([0-9]+))?)?)?(Z|(?:[-+][0-9]{2}(?::[0-9]{2})?))?");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 118 */   private static final Pattern PATTERN_XS_TIME_ZONE = Pattern.compile("Z|(?:[-+][0-9]{2}:[0-9]{2})");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String MSG_YEAR_0_NOT_ALLOWED = "Year 0 is not allowed in XML schema dates. BC 1 is -1, AD 1 is 1.";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static TimeZone getTimeZone(String name) throws UnrecognizedTimeZoneException {
/* 138 */     if (isGMTish(name)) {
/* 139 */       if (name.equalsIgnoreCase("UTC")) {
/* 140 */         return UTC;
/*     */       }
/* 142 */       return TimeZone.getTimeZone(name);
/*     */     } 
/* 144 */     TimeZone tz = TimeZone.getTimeZone(name);
/* 145 */     if (isGMTish(tz.getID())) {
/* 146 */       throw new UnrecognizedTimeZoneException(name);
/*     */     }
/* 148 */     return tz;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isGMTish(String name) {
/* 156 */     if (name.length() < 3) {
/* 157 */       return false;
/*     */     }
/* 159 */     char c1 = name.charAt(0);
/* 160 */     char c2 = name.charAt(1);
/* 161 */     char c3 = name.charAt(2);
/* 162 */     if (((c1 != 'G' && c1 != 'g') || (c2 != 'M' && c2 != 'm') || (c3 != 'T' && c3 != 't')) && ((c1 != 'U' && c1 != 'u') || (c2 != 'T' && c2 != 't') || (c3 != 'C' && c3 != 'c')) && ((c1 != 'U' && c1 != 'u') || (c2 != 'T' && c2 != 't') || c3 != '1'))
/*     */     {
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
/* 181 */       return false;
/*     */     }
/*     */     
/* 184 */     if (name.length() == 3) {
/* 185 */       return true;
/*     */     }
/*     */     
/* 188 */     String offset = name.substring(3);
/* 189 */     if (offset.startsWith("+")) {
/* 190 */       return (offset.equals("+0") || offset.equals("+00") || offset
/* 191 */         .equals("+00:00"));
/*     */     }
/* 193 */     return (offset.equals("-0") || offset.equals("-00") || offset
/* 194 */       .equals("-00:00"));
/*     */   }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String dateToISO8601String(Date date, boolean datePart, boolean timePart, boolean offsetPart, int accuracy, TimeZone timeZone, DateToISO8601CalendarFactory calendarFactory) {
/* 249 */     return dateToString(date, datePart, timePart, offsetPart, accuracy, timeZone, false, calendarFactory);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String dateToXSString(Date date, boolean datePart, boolean timePart, boolean offsetPart, int accuracy, TimeZone timeZone, DateToISO8601CalendarFactory calendarFactory) {
/* 261 */     return dateToString(date, datePart, timePart, offsetPart, accuracy, timeZone, true, calendarFactory);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String dateToString(Date date, boolean datePart, boolean timePart, boolean offsetPart, int accuracy, TimeZone timeZone, boolean xsMode, DateToISO8601CalendarFactory calendarFactory) {
/*     */     int maxLength;
/* 270 */     if (!xsMode && !timePart && offsetPart) {
/* 271 */       throw new IllegalArgumentException("ISO 8601:2004 doesn't specify any formats where the offset is shown but the time isn't.");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 276 */     if (timeZone == null) {
/* 277 */       timeZone = UTC;
/*     */     }
/*     */     
/* 280 */     GregorianCalendar cal = calendarFactory.get(timeZone, date);
/*     */ 
/*     */     
/* 283 */     if (!timePart) {
/* 284 */       maxLength = 10 + (xsMode ? 6 : 0);
/*     */     }
/* 286 */     else if (!datePart) {
/* 287 */       maxLength = 18;
/*     */     } else {
/* 289 */       maxLength = 29;
/*     */     } 
/*     */     
/* 292 */     char[] res = new char[maxLength];
/* 293 */     int dstIdx = 0;
/*     */     
/* 295 */     if (datePart) {
/* 296 */       int x = cal.get(1);
/* 297 */       if (x > 0 && cal.get(0) == 0) {
/* 298 */         x = -x + (xsMode ? 0 : 1);
/*     */       }
/* 300 */       if (x >= 0 && x < 9999) {
/* 301 */         res[dstIdx++] = (char)(48 + x / 1000);
/* 302 */         res[dstIdx++] = (char)(48 + x % 1000 / 100);
/* 303 */         res[dstIdx++] = (char)(48 + x % 100 / 10);
/* 304 */         res[dstIdx++] = (char)(48 + x % 10);
/*     */       } else {
/* 306 */         String yearString = String.valueOf(x);
/*     */ 
/*     */         
/* 309 */         maxLength = maxLength - 4 + yearString.length();
/* 310 */         res = new char[maxLength];
/*     */         
/* 312 */         for (int i = 0; i < yearString.length(); i++) {
/* 313 */           res[dstIdx++] = yearString.charAt(i);
/*     */         }
/*     */       } 
/*     */       
/* 317 */       res[dstIdx++] = '-';
/*     */       
/* 319 */       x = cal.get(2) + 1;
/* 320 */       dstIdx = append00(res, dstIdx, x);
/*     */       
/* 322 */       res[dstIdx++] = '-';
/*     */       
/* 324 */       x = cal.get(5);
/* 325 */       dstIdx = append00(res, dstIdx, x);
/*     */       
/* 327 */       if (timePart) {
/* 328 */         res[dstIdx++] = 'T';
/*     */       }
/*     */     } 
/*     */     
/* 332 */     if (timePart) {
/* 333 */       int x = cal.get(11);
/* 334 */       dstIdx = append00(res, dstIdx, x);
/*     */       
/* 336 */       if (accuracy >= 5) {
/* 337 */         res[dstIdx++] = ':';
/*     */         
/* 339 */         x = cal.get(12);
/* 340 */         dstIdx = append00(res, dstIdx, x);
/*     */         
/* 342 */         if (accuracy >= 6) {
/* 343 */           res[dstIdx++] = ':';
/*     */           
/* 345 */           x = cal.get(13);
/* 346 */           dstIdx = append00(res, dstIdx, x);
/*     */           
/* 348 */           if (accuracy >= 7) {
/* 349 */             x = cal.get(14);
/* 350 */             int forcedDigits = (accuracy == 8) ? 3 : 0;
/* 351 */             if (x != 0 || forcedDigits != 0) {
/* 352 */               if (x > 999)
/*     */               {
/* 354 */                 throw new RuntimeException("Calendar.MILLISECOND > 999");
/*     */               }
/*     */               
/* 357 */               res[dstIdx++] = '.';
/*     */               do {
/* 359 */                 res[dstIdx++] = (char)(48 + x / 100);
/* 360 */                 forcedDigits--;
/* 361 */                 x = x % 100 * 10;
/* 362 */               } while (x != 0 || forcedDigits > 0);
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 369 */     if (offsetPart) {
/* 370 */       if (timeZone == UTC) {
/* 371 */         res[dstIdx++] = 'Z';
/*     */       } else {
/* 373 */         boolean positive; int dt = timeZone.getOffset(date.getTime());
/*     */         
/* 375 */         if (dt < 0) {
/* 376 */           positive = false;
/* 377 */           dt = -dt;
/*     */         } else {
/* 379 */           positive = true;
/*     */         } 
/*     */         
/* 382 */         dt /= 1000;
/* 383 */         int offS = dt % 60;
/* 384 */         dt /= 60;
/* 385 */         int offM = dt % 60;
/* 386 */         dt /= 60;
/* 387 */         int offH = dt;
/*     */         
/* 389 */         if (offS == 0 && offM == 0 && offH == 0) {
/* 390 */           res[dstIdx++] = 'Z';
/*     */         } else {
/* 392 */           res[dstIdx++] = positive ? '+' : '-';
/* 393 */           dstIdx = append00(res, dstIdx, offH);
/* 394 */           res[dstIdx++] = ':';
/* 395 */           dstIdx = append00(res, dstIdx, offM);
/* 396 */           if (offS != 0) {
/* 397 */             res[dstIdx++] = ':';
/* 398 */             dstIdx = append00(res, dstIdx, offS);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/* 404 */     return new String(res, 0, dstIdx);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int append00(char[] res, int dstIdx, int x) {
/* 411 */     res[dstIdx++] = (char)(48 + x / 10);
/* 412 */     res[dstIdx++] = (char)(48 + x % 10);
/* 413 */     return dstIdx;
/*     */   }
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
/*     */   public static Date parseXSDate(String dateStr, TimeZone defaultTimeZone, CalendarFieldsToDateConverter calToDateConverter) throws DateParseException {
/* 435 */     Matcher m = PATTERN_XS_DATE.matcher(dateStr);
/* 436 */     if (!m.matches()) {
/* 437 */       throw new DateParseException("The value didn't match the expected pattern: " + PATTERN_XS_DATE);
/*     */     }
/* 439 */     return parseDate_parseMatcher(m, defaultTimeZone, true, calToDateConverter);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Date parseISO8601Date(String dateStr, TimeZone defaultTimeZone, CalendarFieldsToDateConverter calToDateConverter) throws DateParseException {
/* 450 */     Matcher m = PATTERN_ISO8601_EXTENDED_DATE.matcher(dateStr);
/* 451 */     if (!m.matches()) {
/* 452 */       m = PATTERN_ISO8601_BASIC_DATE.matcher(dateStr);
/* 453 */       if (!m.matches()) {
/* 454 */         throw new DateParseException("The value didn't match the expected pattern: " + PATTERN_ISO8601_EXTENDED_DATE + " or " + PATTERN_ISO8601_BASIC_DATE);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 459 */     return parseDate_parseMatcher(m, defaultTimeZone, false, calToDateConverter);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Date parseDate_parseMatcher(Matcher m, TimeZone defaultTZ, boolean xsMode, CalendarFieldsToDateConverter calToDateConverter) throws DateParseException {
/* 468 */     NullArgumentException.check("defaultTZ", defaultTZ);
/*     */     try {
/* 470 */       int era, year = groupToInt(m.group(1), "year", -2147483648, 2147483647);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 476 */       if (year <= 0) {
/* 477 */         era = 0;
/* 478 */         year = -year + (xsMode ? 0 : 1);
/* 479 */         if (year == 0) {
/* 480 */           throw new DateParseException("Year 0 is not allowed in XML schema dates. BC 1 is -1, AD 1 is 1.");
/*     */         }
/*     */       } else {
/* 483 */         era = 1;
/*     */       } 
/*     */       
/* 486 */       int month = groupToInt(m.group(2), "month", 1, 12) - 1;
/* 487 */       int day = groupToInt(m.group(3), "day-of-month", 1, 31);
/*     */       
/* 489 */       TimeZone tz = xsMode ? parseMatchingTimeZone(m.group(4), defaultTZ) : defaultTZ;
/*     */       
/* 491 */       return calToDateConverter.calculate(era, year, month, day, 0, 0, 0, 0, false, tz);
/* 492 */     } catch (IllegalArgumentException e) {
/*     */       
/* 494 */       throw new DateParseException("Date calculation faliure. Probably the date is formally correct, but refers to an unexistent date (like February 30).");
/*     */     } 
/*     */   }
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
/*     */   public static Date parseXSTime(String timeStr, TimeZone defaultTZ, CalendarFieldsToDateConverter calToDateConverter) throws DateParseException {
/* 509 */     Matcher m = PATTERN_XS_TIME.matcher(timeStr);
/* 510 */     if (!m.matches()) {
/* 511 */       throw new DateParseException("The value didn't match the expected pattern: " + PATTERN_XS_TIME);
/*     */     }
/* 513 */     return parseTime_parseMatcher(m, defaultTZ, calToDateConverter);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Date parseISO8601Time(String timeStr, TimeZone defaultTZ, CalendarFieldsToDateConverter calToDateConverter) throws DateParseException {
/* 522 */     Matcher m = PATTERN_ISO8601_EXTENDED_TIME.matcher(timeStr);
/* 523 */     if (!m.matches()) {
/* 524 */       m = PATTERN_ISO8601_BASIC_TIME.matcher(timeStr);
/* 525 */       if (!m.matches()) {
/* 526 */         throw new DateParseException("The value didn't match the expected pattern: " + PATTERN_ISO8601_EXTENDED_TIME + " or " + PATTERN_ISO8601_BASIC_TIME);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 531 */     return parseTime_parseMatcher(m, defaultTZ, calToDateConverter);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Date parseTime_parseMatcher(Matcher m, TimeZone defaultTZ, CalendarFieldsToDateConverter calToDateConverter) throws DateParseException {
/* 538 */     NullArgumentException.check("defaultTZ", defaultTZ);
/*     */     
/*     */     try {
/*     */       boolean hourWas24;
/* 542 */       int day, hours = groupToInt(m.group(1), "hour-of-day", 0, 24);
/*     */       
/* 544 */       if (hours == 24) {
/* 545 */         hours = 0;
/* 546 */         hourWas24 = true;
/*     */       } else {
/*     */         
/* 549 */         hourWas24 = false;
/*     */       } 
/*     */       
/* 552 */       String minutesStr = m.group(2);
/* 553 */       int minutes = (minutesStr != null) ? groupToInt(minutesStr, "minute", 0, 59) : 0;
/*     */       
/* 555 */       String secsStr = m.group(3);
/*     */       
/* 557 */       int secs = (secsStr != null) ? groupToInt(secsStr, "second", 0, 60) : 0;
/*     */       
/* 559 */       int millisecs = groupToMillisecond(m.group(4));
/*     */ 
/*     */ 
/*     */       
/* 563 */       TimeZone tz = parseMatchingTimeZone(m.group(5), defaultTZ);
/*     */ 
/*     */ 
/*     */       
/* 567 */       if (hourWas24) {
/* 568 */         if (minutes == 0 && secs == 0 && millisecs == 0) {
/* 569 */           day = 2;
/*     */         } else {
/* 571 */           throw new DateParseException("Hour 24 is only allowed in the case of midnight.");
/*     */         }
/*     */       
/*     */       } else {
/*     */         
/* 576 */         day = 1;
/*     */       } 
/*     */       
/* 579 */       return calToDateConverter.calculate(1, 1970, 0, day, hours, minutes, secs, millisecs, false, tz);
/*     */     }
/* 581 */     catch (IllegalArgumentException e) {
/*     */       
/* 583 */       throw new DateParseException("Unexpected time calculation faliure.");
/*     */     } 
/*     */   }
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
/*     */   public static Date parseXSDateTime(String dateTimeStr, TimeZone defaultTZ, CalendarFieldsToDateConverter calToDateConverter) throws DateParseException {
/* 601 */     Matcher m = PATTERN_XS_DATE_TIME.matcher(dateTimeStr);
/* 602 */     if (!m.matches()) {
/* 603 */       throw new DateParseException("The value didn't match the expected pattern: " + PATTERN_XS_DATE_TIME);
/*     */     }
/*     */     
/* 606 */     return parseDateTime_parseMatcher(m, defaultTZ, true, calToDateConverter);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Date parseISO8601DateTime(String dateTimeStr, TimeZone defaultTZ, CalendarFieldsToDateConverter calToDateConverter) throws DateParseException {
/* 616 */     Matcher m = PATTERN_ISO8601_EXTENDED_DATE_TIME.matcher(dateTimeStr);
/* 617 */     if (!m.matches()) {
/* 618 */       m = PATTERN_ISO8601_BASIC_DATE_TIME.matcher(dateTimeStr);
/* 619 */       if (!m.matches()) {
/* 620 */         throw new DateParseException("The value (" + dateTimeStr + ") didn't match the expected pattern: " + PATTERN_ISO8601_EXTENDED_DATE_TIME + " or " + PATTERN_ISO8601_BASIC_DATE_TIME);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 625 */     return parseDateTime_parseMatcher(m, defaultTZ, false, calToDateConverter);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Date parseDateTime_parseMatcher(Matcher m, TimeZone defaultTZ, boolean xsMode, CalendarFieldsToDateConverter calToDateConverter) throws DateParseException {
/* 634 */     NullArgumentException.check("defaultTZ", defaultTZ); try {
/*     */       int era; boolean hourWas24;
/* 636 */       int year = groupToInt(m.group(1), "year", -2147483648, 2147483647);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 642 */       if (year <= 0) {
/* 643 */         era = 0;
/* 644 */         year = -year + (xsMode ? 0 : 1);
/* 645 */         if (year == 0) {
/* 646 */           throw new DateParseException("Year 0 is not allowed in XML schema dates. BC 1 is -1, AD 1 is 1.");
/*     */         }
/*     */       } else {
/* 649 */         era = 1;
/*     */       } 
/*     */       
/* 652 */       int month = groupToInt(m.group(2), "month", 1, 12) - 1;
/* 653 */       int day = groupToInt(m.group(3), "day-of-month", 1, 31);
/*     */ 
/*     */ 
/*     */       
/* 657 */       int hours = groupToInt(m.group(4), "hour-of-day", 0, 24);
/*     */       
/* 659 */       if (hours == 24) {
/* 660 */         hours = 0;
/* 661 */         hourWas24 = true;
/*     */       } else {
/*     */         
/* 664 */         hourWas24 = false;
/*     */       } 
/*     */       
/* 667 */       String minutesStr = m.group(5);
/* 668 */       int minutes = (minutesStr != null) ? groupToInt(minutesStr, "minute", 0, 59) : 0;
/*     */       
/* 670 */       String secsStr = m.group(6);
/*     */       
/* 672 */       int secs = (secsStr != null) ? groupToInt(secsStr, "second", 0, 60) : 0;
/*     */       
/* 674 */       int millisecs = groupToMillisecond(m.group(7));
/*     */ 
/*     */ 
/*     */       
/* 678 */       TimeZone tz = parseMatchingTimeZone(m.group(8), defaultTZ);
/*     */ 
/*     */       
/* 681 */       if (hourWas24 && (
/* 682 */         minutes != 0 || secs != 0 || millisecs != 0)) {
/* 683 */         throw new DateParseException("Hour 24 is only allowed in the case of midnight.");
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 689 */       return calToDateConverter.calculate(era, year, month, day, hours, minutes, secs, millisecs, hourWas24, tz);
/*     */     }
/* 691 */     catch (IllegalArgumentException e) {
/*     */       
/* 693 */       throw new DateParseException("Date-time calculation faliure. Probably the date-time is formally correct, but refers to an unexistent date-time (like February 30).");
/*     */     } 
/*     */   }
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
/*     */   public static TimeZone parseXSTimeZone(String timeZoneStr) throws DateParseException {
/* 707 */     Matcher m = PATTERN_XS_TIME_ZONE.matcher(timeZoneStr);
/* 708 */     if (!m.matches()) {
/* 709 */       throw new DateParseException("The time zone offset didn't match the expected pattern: " + PATTERN_XS_TIME_ZONE);
/*     */     }
/*     */     
/* 712 */     return parseMatchingTimeZone(timeZoneStr, null);
/*     */   }
/*     */   
/*     */   private static int groupToInt(String g, String gName, int min, int max) throws DateParseException {
/*     */     int start;
/*     */     boolean negative;
/* 718 */     if (g == null) {
/* 719 */       throw new DateParseException("The " + gName + " part is missing.");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 727 */     if (g.startsWith("-")) {
/* 728 */       negative = true;
/* 729 */       start = 1;
/*     */     } else {
/* 731 */       negative = false;
/* 732 */       start = 0;
/*     */     } 
/*     */ 
/*     */     
/* 736 */     while (start < g.length() - 1 && g.charAt(start) == '0') {
/* 737 */       start++;
/*     */     }
/* 739 */     if (start != 0) {
/* 740 */       g = g.substring(start);
/*     */     }
/*     */     
/*     */     try {
/* 744 */       int r = Integer.parseInt(g);
/* 745 */       if (negative) {
/* 746 */         r = -r;
/*     */       }
/* 748 */       if (r < min) {
/* 749 */         throw new DateParseException("The " + gName + " part must be at least " + min + ".");
/*     */       }
/*     */       
/* 752 */       if (r > max) {
/* 753 */         throw new DateParseException("The " + gName + " part can't be more than " + max + ".");
/*     */       }
/*     */       
/* 756 */       return r;
/* 757 */     } catch (NumberFormatException e) {
/* 758 */       throw new DateParseException("The " + gName + " part is a malformed integer.");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static TimeZone parseMatchingTimeZone(String s, TimeZone defaultZone) throws DateParseException {
/* 766 */     if (s == null) {
/* 767 */       return defaultZone;
/*     */     }
/* 769 */     if (s.equals("Z")) {
/* 770 */       return UTC;
/*     */     }
/*     */     
/* 773 */     StringBuilder sb = new StringBuilder(9);
/* 774 */     sb.append("GMT");
/* 775 */     sb.append(s.charAt(0));
/*     */     
/* 777 */     String h = s.substring(1, 3);
/* 778 */     groupToInt(h, "offset-hours", 0, 23);
/* 779 */     sb.append(h);
/*     */ 
/*     */     
/* 782 */     int ln = s.length();
/* 783 */     if (ln > 3) {
/* 784 */       int startIdx = (s.charAt(3) == ':') ? 4 : 3;
/* 785 */       String m = s.substring(startIdx, startIdx + 2);
/* 786 */       groupToInt(m, "offset-minutes", 0, 59);
/* 787 */       sb.append(':');
/* 788 */       sb.append(m);
/*     */     } 
/*     */     
/* 791 */     return TimeZone.getTimeZone(sb.toString());
/*     */   }
/*     */ 
/*     */   
/*     */   private static int groupToMillisecond(String g) throws DateParseException {
/* 796 */     if (g == null) {
/* 797 */       return 0;
/*     */     }
/*     */     
/* 800 */     if (g.length() > 3) {
/* 801 */       g = g.substring(0, 3);
/*     */     }
/* 803 */     int i = groupToInt(g, "partial-seconds", 0, 2147483647);
/* 804 */     return (g.length() == 1) ? (i * 100) : ((g.length() == 2) ? (i * 10) : i);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface DateToISO8601CalendarFactory
/*     */   {
/*     */     GregorianCalendar get(TimeZone param1TimeZone, Date param1Date);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface CalendarFieldsToDateConverter
/*     */   {
/*     */     Date calculate(int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6, int param1Int7, int param1Int8, boolean param1Boolean, TimeZone param1TimeZone);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class TrivialDateToISO8601CalendarFactory
/*     */     implements DateToISO8601CalendarFactory
/*     */   {
/*     */     private GregorianCalendar calendar;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private TimeZone lastlySetTimeZone;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public GregorianCalendar get(TimeZone tz, Date date) {
/* 848 */       if (this.calendar == null) {
/* 849 */         this.calendar = new GregorianCalendar(tz, Locale.US);
/* 850 */         this.calendar.setGregorianChange(new Date(Long.MIN_VALUE));
/*     */       
/*     */       }
/* 853 */       else if (this.lastlySetTimeZone != tz) {
/* 854 */         this.calendar.setTimeZone(tz);
/* 855 */         this.lastlySetTimeZone = tz;
/*     */       } 
/*     */       
/* 858 */       this.calendar.setTime(date);
/* 859 */       return this.calendar;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class TrivialCalendarFieldsToDateConverter
/*     */     implements CalendarFieldsToDateConverter
/*     */   {
/*     */     private GregorianCalendar calendar;
/*     */ 
/*     */     
/*     */     private TimeZone lastlySetTimeZone;
/*     */ 
/*     */ 
/*     */     
/*     */     public Date calculate(int era, int year, int month, int day, int hours, int minutes, int secs, int millisecs, boolean addOneDay, TimeZone tz) {
/* 876 */       if (this.calendar == null) {
/* 877 */         this.calendar = new GregorianCalendar(tz, Locale.US);
/* 878 */         this.calendar.setLenient(false);
/* 879 */         this.calendar.setGregorianChange(new Date(Long.MIN_VALUE));
/*     */       
/*     */       }
/* 882 */       else if (this.lastlySetTimeZone != tz) {
/* 883 */         this.calendar.setTimeZone(tz);
/* 884 */         this.lastlySetTimeZone = tz;
/*     */       } 
/*     */ 
/*     */       
/* 888 */       this.calendar.set(0, era);
/* 889 */       this.calendar.set(1, year);
/* 890 */       this.calendar.set(2, month);
/* 891 */       this.calendar.set(5, day);
/* 892 */       this.calendar.set(11, hours);
/* 893 */       this.calendar.set(12, minutes);
/* 894 */       this.calendar.set(13, secs);
/* 895 */       this.calendar.set(14, millisecs);
/* 896 */       if (addOneDay) {
/* 897 */         this.calendar.add(5, 1);
/*     */       }
/*     */       
/* 900 */       return this.calendar.getTime();
/*     */     }
/*     */   }
/*     */   
/*     */   public static final class DateParseException
/*     */     extends ParseException
/*     */   {
/*     */     public DateParseException(String message) {
/* 908 */       super(message, 0);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\templat\\utility\DateUtil.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */