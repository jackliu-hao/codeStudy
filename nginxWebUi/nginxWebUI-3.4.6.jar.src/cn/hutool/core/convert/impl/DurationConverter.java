/*    */ package cn.hutool.core.convert.impl;
/*    */ 
/*    */ import cn.hutool.core.convert.AbstractConverter;
/*    */ import java.time.Duration;
/*    */ import java.time.temporal.TemporalAmount;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DurationConverter
/*    */   extends AbstractConverter<Duration>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   protected Duration convertInternal(Object value) {
/* 20 */     if (value instanceof TemporalAmount)
/* 21 */       return Duration.from((TemporalAmount)value); 
/* 22 */     if (value instanceof Long) {
/* 23 */       return Duration.ofMillis(((Long)value).longValue());
/*    */     }
/* 25 */     return Duration.parse(convertToStr(value));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\impl\DurationConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */