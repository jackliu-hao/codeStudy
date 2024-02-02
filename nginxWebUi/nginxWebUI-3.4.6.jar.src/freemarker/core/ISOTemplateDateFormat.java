/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.utility.DateUtil;
/*    */ import java.util.Date;
/*    */ import java.util.TimeZone;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class ISOTemplateDateFormat
/*    */   extends ISOLikeTemplateDateFormat
/*    */ {
/*    */   ISOTemplateDateFormat(String settingValue, int parsingStart, int dateType, boolean zonelessInput, TimeZone timeZone, ISOLikeTemplateDateFormatFactory factory, Environment env) throws InvalidFormatParametersException, UnknownDateTypeFormattingUnsupportedException {
/* 39 */     super(settingValue, parsingStart, dateType, zonelessInput, timeZone, factory, env);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected String format(Date date, boolean datePart, boolean timePart, boolean offsetPart, int accuracy, TimeZone timeZone, DateUtil.DateToISO8601CalendarFactory calendarFactory) {
/* 45 */     return DateUtil.dateToISO8601String(date, datePart, timePart, (timePart && offsetPart), accuracy, timeZone, calendarFactory);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected Date parseDate(String s, TimeZone tz, DateUtil.CalendarFieldsToDateConverter calToDateConverter) throws DateUtil.DateParseException {
/* 52 */     return DateUtil.parseISO8601Date(s, tz, calToDateConverter);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected Date parseTime(String s, TimeZone tz, DateUtil.CalendarFieldsToDateConverter calToDateConverter) throws DateUtil.DateParseException {
/* 58 */     return DateUtil.parseISO8601Time(s, tz, calToDateConverter);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected Date parseDateTime(String s, TimeZone tz, DateUtil.CalendarFieldsToDateConverter calToDateConverter) throws DateUtil.DateParseException {
/* 64 */     return DateUtil.parseISO8601DateTime(s, tz, calToDateConverter);
/*    */   }
/*    */ 
/*    */   
/*    */   protected String getDateDescription() {
/* 69 */     return "ISO 8601 (subset) date";
/*    */   }
/*    */ 
/*    */   
/*    */   protected String getTimeDescription() {
/* 74 */     return "ISO 8601 (subset) time";
/*    */   }
/*    */ 
/*    */   
/*    */   protected String getDateTimeDescription() {
/* 79 */     return "ISO 8601 (subset) date-time";
/*    */   }
/*    */ 
/*    */   
/*    */   protected boolean isXSMode() {
/* 84 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\ISOTemplateDateFormat.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */