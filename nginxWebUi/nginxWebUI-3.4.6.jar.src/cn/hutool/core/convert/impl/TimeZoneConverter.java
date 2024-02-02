/*    */ package cn.hutool.core.convert.impl;
/*    */ 
/*    */ import cn.hutool.core.convert.AbstractConverter;
/*    */ import java.util.TimeZone;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TimeZoneConverter
/*    */   extends AbstractConverter<TimeZone>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   protected TimeZone convertInternal(Object value) {
/* 17 */     return TimeZone.getTimeZone(convertToStr(value));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\impl\TimeZoneConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */