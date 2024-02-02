/*    */ package cn.hutool.core.convert.impl;
/*    */ 
/*    */ import cn.hutool.core.collection.CollUtil;
/*    */ import cn.hutool.core.convert.Converter;
/*    */ import cn.hutool.core.util.ObjectUtil;
/*    */ import cn.hutool.core.util.TypeUtil;
/*    */ import java.lang.reflect.Type;
/*    */ import java.util.Collection;
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
/*    */ public class CollectionConverter
/*    */   implements Converter<Collection<?>>
/*    */ {
/*    */   private final Type collectionType;
/*    */   private final Type elementType;
/*    */   
/*    */   public CollectionConverter() {
/* 28 */     this(Collection.class);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CollectionConverter(Type collectionType) {
/* 38 */     this(collectionType, TypeUtil.getTypeArgument(collectionType));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CollectionConverter(Class<?> collectionType) {
/* 47 */     this(collectionType, TypeUtil.getTypeArgument(collectionType));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CollectionConverter(Type collectionType, Type elementType) {
/* 57 */     this.collectionType = collectionType;
/* 58 */     this.elementType = elementType;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Collection<?> convert(Object value, Collection<?> defaultValue) throws IllegalArgumentException {
/* 64 */     Collection<?> result = convertInternal(value);
/* 65 */     return (Collection)ObjectUtil.defaultIfNull(result, defaultValue);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected Collection<?> convertInternal(Object value) {
/* 75 */     Collection<Object> collection = CollUtil.create(TypeUtil.getClass(this.collectionType));
/* 76 */     return CollUtil.addAll(collection, value, this.elementType);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\impl\CollectionConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */