/*    */ package cn.hutool.core.date.format;
/*    */ 
/*    */ import java.text.ParseException;
/*    */ import java.text.ParsePosition;
/*    */ import java.util.Calendar;
/*    */ import java.util.Date;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface DateParser
/*    */   extends DateBasic
/*    */ {
/*    */   Date parse(String paramString) throws ParseException;
/*    */   
/*    */   Date parse(String paramString, ParsePosition paramParsePosition);
/*    */   
/*    */   boolean parse(String paramString, ParsePosition paramParsePosition, Calendar paramCalendar);
/*    */   
/*    */   default Object parseObject(String source) throws ParseException {
/* 58 */     return parse(source);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   default Object parseObject(String source, ParsePosition pos) {
/* 70 */     return parse(source, pos);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\date\format\DateParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */