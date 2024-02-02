/*    */ package cn.hutool.core.convert.impl;
/*    */ 
/*    */ import cn.hutool.core.convert.AbstractConverter;
/*    */ import java.util.Currency;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CurrencyConverter
/*    */   extends AbstractConverter<Currency>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   protected Currency convertInternal(Object value) {
/* 18 */     return Currency.getInstance(convertToStr(value));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\impl\CurrencyConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */