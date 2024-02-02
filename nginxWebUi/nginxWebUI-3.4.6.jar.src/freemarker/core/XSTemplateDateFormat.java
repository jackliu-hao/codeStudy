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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class XSTemplateDateFormat
/*    */   extends ISOLikeTemplateDateFormat
/*    */ {
/*    */   XSTemplateDateFormat(String settingValue, int parsingStart, int dateType, boolean zonelessInput, TimeZone timeZone, ISOLikeTemplateDateFormatFactory factory, Environment env) throws UnknownDateTypeFormattingUnsupportedException, InvalidFormatParametersException {
/* 43 */     super(settingValue, parsingStart, dateType, zonelessInput, timeZone, factory, env);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected String format(Date date, boolean datePart, boolean timePart, boolean offsetPart, int accuracy, TimeZone timeZone, DateUtil.DateToISO8601CalendarFactory calendarFactory) {
/* 49 */     return DateUtil.dateToXSString(date, datePart, timePart, offsetPart, accuracy, timeZone, calendarFactory);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected Date parseDate(String s, TimeZone tz, DateUtil.CalendarFieldsToDateConverter calToDateConverter) throws DateUtil.DateParseException {
/* 56 */     return DateUtil.parseXSDate(s, tz, calToDateConverter);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected Date parseTime(String s, TimeZone tz, DateUtil.CalendarFieldsToDateConverter calToDateConverter) throws DateUtil.DateParseException {
/* 62 */     return DateUtil.parseXSTime(s, tz, calToDateConverter);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected Date parseDateTime(String s, TimeZone tz, DateUtil.CalendarFieldsToDateConverter calToDateConverter) throws DateUtil.DateParseException {
/* 68 */     return DateUtil.parseXSDateTime(s, tz, calToDateConverter);
/*    */   }
/*    */ 
/*    */   
/*    */   protected String getDateDescription() {
/* 73 */     return "W3C XML Schema date";
/*    */   }
/*    */ 
/*    */   
/*    */   protected String getTimeDescription() {
/* 78 */     return "W3C XML Schema time";
/*    */   }
/*    */ 
/*    */   
/*    */   protected String getDateTimeDescription() {
/* 83 */     return "W3C XML Schema dateTime";
/*    */   }
/*    */ 
/*    */   
/*    */   protected boolean isXSMode() {
/* 88 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\XSTemplateDateFormat.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */