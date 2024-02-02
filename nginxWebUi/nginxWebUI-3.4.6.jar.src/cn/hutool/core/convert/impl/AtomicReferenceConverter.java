/*    */ package cn.hutool.core.convert.impl;
/*    */ 
/*    */ import cn.hutool.core.convert.AbstractConverter;
/*    */ import cn.hutool.core.convert.ConverterRegistry;
/*    */ import cn.hutool.core.util.TypeUtil;
/*    */ import java.lang.reflect.Type;
/*    */ import java.util.concurrent.atomic.AtomicReference;
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
/*    */ public class AtomicReferenceConverter
/*    */   extends AbstractConverter<AtomicReference>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   protected AtomicReference<?> convertInternal(Object value) {
/* 24 */     Object targetValue = null;
/* 25 */     Type paramType = TypeUtil.getTypeArgument(AtomicReference.class);
/* 26 */     if (false == TypeUtil.isUnknown(paramType)) {
/* 27 */       targetValue = ConverterRegistry.getInstance().convert(paramType, value);
/*    */     }
/* 29 */     if (null == targetValue) {
/* 30 */       targetValue = value;
/*    */     }
/*    */     
/* 33 */     return new AtomicReference(targetValue);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\impl\AtomicReferenceConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */