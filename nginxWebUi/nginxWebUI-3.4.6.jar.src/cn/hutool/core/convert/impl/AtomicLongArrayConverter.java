/*    */ package cn.hutool.core.convert.impl;
/*    */ 
/*    */ import cn.hutool.core.convert.AbstractConverter;
/*    */ import cn.hutool.core.convert.Convert;
/*    */ import java.util.concurrent.atomic.AtomicLongArray;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AtomicLongArrayConverter
/*    */   extends AbstractConverter<AtomicLongArray>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   protected AtomicLongArray convertInternal(Object value) {
/* 19 */     return new AtomicLongArray((long[])Convert.convert(long[].class, value));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\impl\AtomicLongArrayConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */