/*    */ package cn.hutool.core.convert.impl;
/*    */ 
/*    */ import cn.hutool.core.convert.AbstractConverter;
/*    */ import cn.hutool.core.convert.ConverterRegistry;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.core.util.TypeUtil;
/*    */ import java.lang.ref.Reference;
/*    */ import java.lang.ref.SoftReference;
/*    */ import java.lang.ref.WeakReference;
/*    */ import java.lang.reflect.Type;
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
/*    */ public class ReferenceConverter
/*    */   extends AbstractConverter<Reference>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final Class<? extends Reference> targetType;
/*    */   
/*    */   public ReferenceConverter(Class<? extends Reference> targetType) {
/* 30 */     this.targetType = targetType;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected Reference<?> convertInternal(Object value) {
/* 38 */     Object targetValue = null;
/* 39 */     Type paramType = TypeUtil.getTypeArgument(this.targetType);
/* 40 */     if (false == TypeUtil.isUnknown(paramType)) {
/* 41 */       targetValue = ConverterRegistry.getInstance().convert(paramType, value);
/*    */     }
/* 43 */     if (null == targetValue) {
/* 44 */       targetValue = value;
/*    */     }
/*    */     
/* 47 */     if (this.targetType == WeakReference.class)
/* 48 */       return new WeakReference(targetValue); 
/* 49 */     if (this.targetType == SoftReference.class) {
/* 50 */       return new SoftReference(targetValue);
/*    */     }
/*    */     
/* 53 */     throw new UnsupportedOperationException(StrUtil.format("Unsupport Reference type: {}", new Object[] { this.targetType.getName() }));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\impl\ReferenceConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */