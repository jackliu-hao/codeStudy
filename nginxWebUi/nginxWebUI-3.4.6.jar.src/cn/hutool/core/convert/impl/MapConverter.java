/*    */ package cn.hutool.core.convert.impl;
/*    */ 
/*    */ import cn.hutool.core.bean.BeanUtil;
/*    */ import cn.hutool.core.convert.AbstractConverter;
/*    */ import cn.hutool.core.convert.ConverterRegistry;
/*    */ import cn.hutool.core.map.MapUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.core.util.TypeUtil;
/*    */ import java.lang.reflect.Type;
/*    */ import java.util.Map;
/*    */ import java.util.Objects;
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
/*    */ public class MapConverter
/*    */   extends AbstractConverter<Map<?, ?>>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final Type mapType;
/*    */   private final Type keyType;
/*    */   private final Type valueType;
/*    */   
/*    */   public MapConverter(Type mapType) {
/* 36 */     this(mapType, TypeUtil.getTypeArgument(mapType, 0), TypeUtil.getTypeArgument(mapType, 1));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MapConverter(Type mapType, Type keyType, Type valueType) {
/* 47 */     this.mapType = mapType;
/* 48 */     this.keyType = keyType;
/* 49 */     this.valueType = valueType;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected Map<?, ?> convertInternal(Object value) {
/*    */     Map<?, ?> map;
/* 56 */     if (value instanceof Map) {
/* 57 */       Class<?> valueClass = value.getClass();
/* 58 */       if (valueClass.equals(this.mapType)) {
/* 59 */         Type[] typeArguments = TypeUtil.getTypeArguments(valueClass);
/* 60 */         if (null != typeArguments && 2 == typeArguments.length && 
/*    */           
/* 62 */           Objects.equals(this.keyType, typeArguments[0]) && 
/* 63 */           Objects.equals(this.valueType, typeArguments[1]))
/*    */         {
/* 65 */           return (Map<?, ?>)value;
/*    */         }
/*    */       } 
/* 68 */       map = MapUtil.createMap(TypeUtil.getClass(this.mapType));
/* 69 */       convertMapToMap((Map<?, ?>)value, (Map)map);
/* 70 */     } else if (BeanUtil.isBean(value.getClass())) {
/* 71 */       map = BeanUtil.beanToMap(value, new String[0]);
/*    */       
/* 73 */       map = convertInternal(map);
/*    */     } else {
/* 75 */       throw new UnsupportedOperationException(StrUtil.format("Unsupport toMap value type: {}", new Object[] { value.getClass().getName() }));
/*    */     } 
/* 77 */     return map;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private void convertMapToMap(Map<?, ?> srcMap, Map<Object, Object> targetMap) {
/* 87 */     ConverterRegistry convert = ConverterRegistry.getInstance();
/* 88 */     srcMap.forEach((key, value) -> {
/*    */           key = TypeUtil.isUnknown(this.keyType) ? key : convert.convert(this.keyType, key);
/*    */           value = TypeUtil.isUnknown(this.valueType) ? value : convert.convert(this.valueType, value);
/*    */           targetMap.put(key, value);
/*    */         });
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Class<Map<?, ?>> getTargetType() {
/* 98 */     return TypeUtil.getClass(this.mapType);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\impl\MapConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */