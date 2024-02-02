package freemarker.core;

import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DateUtil;
import freemarker.template.utility.StringUtil;
import java.util.Date;
import java.util.TimeZone;

abstract class ISOLikeTemplateDateFormat extends TemplateDateFormat {
   private static final String XS_LESS_THAN_SECONDS_ACCURACY_ERROR_MESSAGE = "Less than seconds accuracy isn't allowed by the XML Schema format";
   private final ISOLikeTemplateDateFormatFactory factory;
   private final Environment env;
   protected final int dateType;
   protected final boolean zonelessInput;
   protected final TimeZone timeZone;
   protected final Boolean forceUTC;
   protected final Boolean showZoneOffset;
   protected final int accuracy;

   public ISOLikeTemplateDateFormat(String formatString, int parsingStart, int dateType, boolean zonelessInput, TimeZone timeZone, ISOLikeTemplateDateFormatFactory factory, Environment env) throws InvalidFormatParametersException, UnknownDateTypeFormattingUnsupportedException {
      this.factory = factory;
      this.env = env;
      if (dateType == 0) {
         throw new UnknownDateTypeFormattingUnsupportedException();
      } else {
         this.dateType = dateType;
         this.zonelessInput = zonelessInput;
         int ln = formatString.length();
         boolean afterSeparator = false;
         int i = parsingStart;
         int accuracy = 7;
         Boolean showZoneOffset = null;
         Boolean forceUTC = Boolean.FALSE;

         while(true) {
            while(i < ln) {
               char c = formatString.charAt(i++);
               if (c != '_' && c != ' ') {
                  if (!afterSeparator) {
                     throw new InvalidFormatParametersException("Missing space or \"_\" before \"" + c + "\" (at char pos. " + i + ").");
                  }

                  label93:
                  switch (c) {
                     case 'f':
                        if (i < ln && formatString.charAt(i) == 'u') {
                           this.checkForceUTCNotSet(forceUTC);
                           ++i;
                           forceUTC = Boolean.TRUE;
                           break;
                        }
                     case 'n':
                        if (showZoneOffset != null) {
                           throw new InvalidFormatParametersException("Character \"" + c + "\" is unexpected as zone offset visibility was already specified earlier. (at char pos. " + i + ").");
                        }

                        switch (c) {
                           case 'f':
                              if (i >= ln || formatString.charAt(i) != 'z') {
                                 throw new InvalidFormatParametersException("\"f\" must be followed by \"z\" (at char pos. " + i + ").");
                              }

                              ++i;
                              showZoneOffset = Boolean.TRUE;
                              break label93;
                           case 'n':
                              if (i >= ln || formatString.charAt(i) != 'z') {
                                 throw new InvalidFormatParametersException("\"n\" must be followed by \"z\" (at char pos. " + i + ").");
                              }

                              ++i;
                              showZoneOffset = Boolean.FALSE;
                           default:
                              break label93;
                        }
                     case 'g':
                     case 'i':
                     case 'j':
                     case 'k':
                     case 'l':
                     case 'o':
                     case 'p':
                     case 'q':
                     case 'r':
                     case 't':
                     default:
                        throw new InvalidFormatParametersException("Unexpected character, " + StringUtil.jQuote(String.valueOf(c)) + ". Expected the beginning of one of: h, m, s, ms, nz, fz, u (at char pos. " + i + ").");
                     case 'h':
                     case 'm':
                     case 's':
                        if (accuracy != 7) {
                           throw new InvalidFormatParametersException("Character \"" + c + "\" is unexpected as accuracy was already specified earlier (at char pos. " + i + ").");
                        }

                        switch (c) {
                           case 'h':
                              if (this.isXSMode()) {
                                 throw new InvalidFormatParametersException("Less than seconds accuracy isn't allowed by the XML Schema format");
                              }

                              accuracy = 4;
                              break label93;
                           case 'm':
                              if (i < ln && formatString.charAt(i) == 's') {
                                 ++i;
                                 accuracy = 8;
                              } else {
                                 if (this.isXSMode()) {
                                    throw new InvalidFormatParametersException("Less than seconds accuracy isn't allowed by the XML Schema format");
                                 }

                                 accuracy = 5;
                              }
                              break label93;
                           case 's':
                              accuracy = 6;
                           default:
                              break label93;
                        }
                     case 'u':
                        this.checkForceUTCNotSet(forceUTC);
                        forceUTC = null;
                  }

                  afterSeparator = false;
               } else {
                  afterSeparator = true;
               }
            }

            this.accuracy = accuracy;
            this.showZoneOffset = showZoneOffset;
            this.forceUTC = forceUTC;
            this.timeZone = timeZone;
            return;
         }
      }
   }

   private void checkForceUTCNotSet(Boolean fourceUTC) throws InvalidFormatParametersException {
      if (fourceUTC != Boolean.FALSE) {
         throw new InvalidFormatParametersException("The UTC usage option was already set earlier.");
      }
   }

   public final String formatToPlainText(TemplateDateModel dateModel) throws TemplateModelException {
      Date date;
      boolean var10002;
      boolean var10003;
      boolean var10004;
      int var10005;
      TimeZone var10006;
      label50: {
         date = TemplateFormatUtil.getNonNullDate(dateModel);
         var10002 = this.dateType != 1;
         var10003 = this.dateType != 2;
         var10004 = this.showZoneOffset == null ? !this.zonelessInput : this.showZoneOffset;
         var10005 = this.accuracy;
         if (this.forceUTC == null) {
            if (!this.zonelessInput) {
               break label50;
            }
         } else if (this.forceUTC) {
            break label50;
         }

         var10006 = this.timeZone;
         return this.format(date, var10002, var10003, var10004, var10005, var10006, this.factory.getISOBuiltInCalendar(this.env));
      }

      var10006 = DateUtil.UTC;
      return this.format(date, var10002, var10003, var10004, var10005, var10006, this.factory.getISOBuiltInCalendar(this.env));
   }

   protected abstract String format(Date var1, boolean var2, boolean var3, boolean var4, int var5, TimeZone var6, DateUtil.DateToISO8601CalendarFactory var7);

   public final Date parse(String s, int dateType) throws UnparsableValueException {
      DateUtil.CalendarFieldsToDateConverter calToDateConverter = this.factory.getCalendarFieldsToDateCalculator(this.env);
      TimeZone tz = this.forceUTC != Boolean.FALSE ? DateUtil.UTC : this.timeZone;

      try {
         if (dateType == 2) {
            return this.parseDate(s, tz, calToDateConverter);
         } else if (dateType == 1) {
            return this.parseTime(s, tz, calToDateConverter);
         } else if (dateType == 3) {
            return this.parseDateTime(s, tz, calToDateConverter);
         } else {
            throw new BugException("Unexpected date type: " + dateType);
         }
      } catch (DateUtil.DateParseException var6) {
         throw new UnparsableValueException(var6.getMessage(), var6);
      }
   }

   protected abstract Date parseDate(String var1, TimeZone var2, DateUtil.CalendarFieldsToDateConverter var3) throws DateUtil.DateParseException;

   protected abstract Date parseTime(String var1, TimeZone var2, DateUtil.CalendarFieldsToDateConverter var3) throws DateUtil.DateParseException;

   protected abstract Date parseDateTime(String var1, TimeZone var2, DateUtil.CalendarFieldsToDateConverter var3) throws DateUtil.DateParseException;

   public final String getDescription() {
      switch (this.dateType) {
         case 1:
            return this.getTimeDescription();
         case 2:
            return this.getDateDescription();
         case 3:
            return this.getDateTimeDescription();
         default:
            return "<error: wrong format dateType>";
      }
   }

   protected abstract String getDateDescription();

   protected abstract String getTimeDescription();

   protected abstract String getDateTimeDescription();

   public final boolean isLocaleBound() {
      return false;
   }

   public boolean isTimeZoneBound() {
      return true;
   }

   protected abstract boolean isXSMode();
}
