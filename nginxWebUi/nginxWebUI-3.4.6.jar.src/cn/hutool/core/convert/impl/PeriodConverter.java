/*    */ package cn.hutool.core.convert.impl;
/*    */ 
/*    */ import cn.hutool.core.convert.AbstractConverter;
/*    */ import java.time.Period;
/*    */ import java.time.temporal.TemporalAmount;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PeriodConverter
/*    */   extends AbstractConverter<Period>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   protected Period convertInternal(Object value) {
/* 20 */     if (value instanceof TemporalAmount)
/* 21 */       return Period.from((TemporalAmount)value); 
/* 22 */     if (value instanceof Integer) {
/* 23 */       return Period.ofDays(((Integer)value).intValue());
/*    */     }
/* 25 */     return Period.parse(convertToStr(value));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\impl\PeriodConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */