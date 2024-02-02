/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.utility.DateUtil;
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
/*    */ abstract class ISOLikeTemplateDateFormatFactory
/*    */   extends TemplateDateFormatFactory
/*    */ {
/* 29 */   private static final Object DATE_TO_CAL_CONVERTER_KEY = new Object();
/* 30 */   private static final Object CAL_TO_DATE_CONVERTER_KEY = new Object();
/*    */ 
/*    */   
/*    */   public DateUtil.DateToISO8601CalendarFactory getISOBuiltInCalendar(Environment env) {
/*    */     DateUtil.TrivialDateToISO8601CalendarFactory trivialDateToISO8601CalendarFactory;
/* 35 */     DateUtil.DateToISO8601CalendarFactory r = (DateUtil.DateToISO8601CalendarFactory)env.getCustomState(DATE_TO_CAL_CONVERTER_KEY);
/* 36 */     if (r == null) {
/* 37 */       trivialDateToISO8601CalendarFactory = new DateUtil.TrivialDateToISO8601CalendarFactory();
/* 38 */       env.setCustomState(DATE_TO_CAL_CONVERTER_KEY, trivialDateToISO8601CalendarFactory);
/*    */     } 
/* 40 */     return (DateUtil.DateToISO8601CalendarFactory)trivialDateToISO8601CalendarFactory;
/*    */   }
/*    */   public DateUtil.CalendarFieldsToDateConverter getCalendarFieldsToDateCalculator(Environment env) {
/*    */     DateUtil.TrivialCalendarFieldsToDateConverter trivialCalendarFieldsToDateConverter;
/* 44 */     DateUtil.CalendarFieldsToDateConverter r = (DateUtil.CalendarFieldsToDateConverter)env.getCustomState(CAL_TO_DATE_CONVERTER_KEY);
/* 45 */     if (r == null) {
/* 46 */       trivialCalendarFieldsToDateConverter = new DateUtil.TrivialCalendarFieldsToDateConverter();
/* 47 */       env.setCustomState(CAL_TO_DATE_CONVERTER_KEY, trivialCalendarFieldsToDateConverter);
/*    */     } 
/* 49 */     return (DateUtil.CalendarFieldsToDateConverter)trivialCalendarFieldsToDateConverter;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\ISOLikeTemplateDateFormatFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */