/*    */ package cn.hutool.core.convert.impl;
/*    */ 
/*    */ import cn.hutool.core.convert.AbstractConverter;
/*    */ import cn.hutool.core.date.DateUtil;
/*    */ import cn.hutool.core.util.StrUtil;
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
/*    */ public class CalendarConverter
/*    */   extends AbstractConverter<Calendar>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private String format;
/*    */   
/*    */   public String getFormat() {
/* 28 */     return this.format;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setFormat(String format) {
/* 37 */     this.format = format;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected Calendar convertInternal(Object value) {
/* 43 */     if (value instanceof Date) {
/* 44 */       return DateUtil.calendar((Date)value);
/*    */     }
/*    */ 
/*    */     
/* 48 */     if (value instanceof Long)
/*    */     {
/* 50 */       return DateUtil.calendar(((Long)value).longValue());
/*    */     }
/*    */     
/* 53 */     String valueStr = convertToStr(value);
/* 54 */     return DateUtil.calendar(StrUtil.isBlank(this.format) ? (Date)DateUtil.parse(valueStr) : (Date)DateUtil.parse(valueStr, this.format));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\impl\CalendarConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */