/*    */ package cn.hutool.core.convert.impl;
/*    */ 
/*    */ import cn.hutool.core.convert.AbstractConverter;
/*    */ import cn.hutool.core.util.BooleanUtil;
/*    */ import java.util.concurrent.atomic.AtomicBoolean;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AtomicBooleanConverter
/*    */   extends AbstractConverter<AtomicBoolean>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   protected AtomicBoolean convertInternal(Object value) {
/* 19 */     if (value instanceof Boolean) {
/* 20 */       return new AtomicBoolean(((Boolean)value).booleanValue());
/*    */     }
/* 22 */     String valueStr = convertToStr(value);
/* 23 */     return new AtomicBoolean(BooleanUtil.toBoolean(valueStr));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\impl\AtomicBooleanConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */