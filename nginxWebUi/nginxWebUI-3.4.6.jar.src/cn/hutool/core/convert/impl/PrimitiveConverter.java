/*    */ package cn.hutool.core.convert.impl;
/*    */ 
/*    */ import cn.hutool.core.convert.AbstractConverter;
/*    */ import cn.hutool.core.convert.Convert;
/*    */ import cn.hutool.core.convert.ConvertException;
/*    */ import cn.hutool.core.util.ObjectUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import java.util.function.Function;
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
/*    */ public class PrimitiveConverter
/*    */   extends AbstractConverter<Object>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final Class<?> targetType;
/*    */   
/*    */   public PrimitiveConverter(Class<?> clazz) {
/* 39 */     if (null == clazz)
/* 40 */       throw new NullPointerException("PrimitiveConverter not allow null target type!"); 
/* 41 */     if (false == clazz.isPrimitive()) {
/* 42 */       throw new IllegalArgumentException("[" + clazz + "] is not a primitive class!");
/*    */     }
/* 44 */     this.targetType = clazz;
/*    */   }
/*    */ 
/*    */   
/*    */   protected Object convertInternal(Object value) {
/* 49 */     return convert(value, this.targetType, this::convertToStr);
/*    */   }
/*    */ 
/*    */   
/*    */   protected String convertToStr(Object value) {
/* 54 */     return StrUtil.trim(super.convertToStr(value));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Class<Object> getTargetType() {
/* 60 */     return (Class)this.targetType;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected static Object convert(Object value, Class<?> primitiveClass, Function<Object, String> toStringFunc) {
/* 72 */     if (byte.class == primitiveClass)
/* 73 */       return ObjectUtil.defaultIfNull(NumberConverter.convert(value, (Class)Byte.class, toStringFunc), Integer.valueOf(0)); 
/* 74 */     if (short.class == primitiveClass)
/* 75 */       return ObjectUtil.defaultIfNull(NumberConverter.convert(value, (Class)Short.class, toStringFunc), Integer.valueOf(0)); 
/* 76 */     if (int.class == primitiveClass)
/* 77 */       return ObjectUtil.defaultIfNull(NumberConverter.convert(value, (Class)Integer.class, toStringFunc), Integer.valueOf(0)); 
/* 78 */     if (long.class == primitiveClass)
/* 79 */       return ObjectUtil.defaultIfNull(NumberConverter.convert(value, (Class)Long.class, toStringFunc), Integer.valueOf(0)); 
/* 80 */     if (float.class == primitiveClass)
/* 81 */       return ObjectUtil.defaultIfNull(NumberConverter.convert(value, (Class)Float.class, toStringFunc), Integer.valueOf(0)); 
/* 82 */     if (double.class == primitiveClass)
/* 83 */       return ObjectUtil.defaultIfNull(NumberConverter.convert(value, (Class)Double.class, toStringFunc), Integer.valueOf(0)); 
/* 84 */     if (char.class == primitiveClass)
/* 85 */       return Convert.convert(Character.class, value); 
/* 86 */     if (boolean.class == primitiveClass) {
/* 87 */       return Convert.convert(Boolean.class, value);
/*    */     }
/*    */     
/* 90 */     throw new ConvertException("Unsupported target type: {}", new Object[] { primitiveClass });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\impl\PrimitiveConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */