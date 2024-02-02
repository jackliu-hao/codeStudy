/*    */ package cn.hutool.core.convert.impl;
/*    */ 
/*    */ import cn.hutool.core.convert.AbstractConverter;
/*    */ import cn.hutool.core.convert.Convert;
/*    */ import java.util.concurrent.atomic.AtomicIntegerArray;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AtomicIntegerArrayConverter
/*    */   extends AbstractConverter<AtomicIntegerArray>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   protected AtomicIntegerArray convertInternal(Object value) {
/* 19 */     return new AtomicIntegerArray((int[])Convert.convert(int[].class, value));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\impl\AtomicIntegerArrayConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */