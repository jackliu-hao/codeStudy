/*     */ package cn.hutool.core.convert.impl;
/*     */ 
/*     */ import cn.hutool.core.codec.Base64;
/*     */ import cn.hutool.core.collection.IterUtil;
/*     */ import cn.hutool.core.convert.AbstractConverter;
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.ByteUtil;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.lang.reflect.Array;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ArrayConverter
/*     */   extends AbstractConverter<Object>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final Class<?> targetType;
/*     */   private final Class<?> targetComponentType;
/*     */   private boolean ignoreElementError;
/*     */   
/*     */   public ArrayConverter(Class<?> targetType) {
/*  44 */     this(targetType, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArrayConverter(Class<?> targetType, boolean ignoreElementError) {
/*  54 */     if (null == targetType)
/*     */     {
/*  56 */       targetType = Object[].class;
/*     */     }
/*     */     
/*  59 */     if (targetType.isArray()) {
/*  60 */       this.targetType = targetType;
/*  61 */       this.targetComponentType = targetType.getComponentType();
/*     */     } else {
/*     */       
/*  64 */       this.targetComponentType = targetType;
/*  65 */       this.targetType = ArrayUtil.getArrayType(targetType);
/*     */     } 
/*     */     
/*  68 */     this.ignoreElementError = ignoreElementError;
/*     */   }
/*     */ 
/*     */   
/*     */   protected Object convertInternal(Object value) {
/*  73 */     return value.getClass().isArray() ? convertArrayToArray(value) : convertObjectToArray(value);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Class getTargetType() {
/*  79 */     return this.targetType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIgnoreElementError(boolean ignoreElementError) {
/*  89 */     this.ignoreElementError = ignoreElementError;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Object convertArrayToArray(Object array) {
/* 101 */     Class<?> valueComponentType = ArrayUtil.getComponentType(array);
/*     */     
/* 103 */     if (valueComponentType == this.targetComponentType) {
/* 104 */       return array;
/*     */     }
/*     */     
/* 107 */     int len = ArrayUtil.length(array);
/* 108 */     Object result = Array.newInstance(this.targetComponentType, len);
/*     */     
/* 110 */     for (int i = 0; i < len; i++) {
/* 111 */       Array.set(result, i, convertComponentType(Array.get(array, i)));
/*     */     }
/* 113 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Object convertObjectToArray(Object value) {
/*     */     Object result;
/* 123 */     if (value instanceof CharSequence) {
/* 124 */       if (this.targetComponentType == char.class || this.targetComponentType == Character.class) {
/* 125 */         return convertArrayToArray(value.toString().toCharArray());
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 130 */       if (this.targetComponentType == byte.class) {
/* 131 */         String str = value.toString();
/* 132 */         if (Base64.isBase64(str)) {
/* 133 */           return Base64.decode(value.toString());
/*     */         }
/* 135 */         return str.getBytes();
/*     */       } 
/*     */ 
/*     */       
/* 139 */       String[] strings = StrUtil.splitToArray(value.toString(), ',');
/* 140 */       return convertArrayToArray(strings);
/*     */     } 
/*     */ 
/*     */     
/* 144 */     if (value instanceof List) {
/*     */       
/* 146 */       List<?> list = (List)value;
/* 147 */       result = Array.newInstance(this.targetComponentType, list.size());
/* 148 */       for (int i = 0; i < list.size(); i++) {
/* 149 */         Array.set(result, i, convertComponentType(list.get(i)));
/*     */       }
/* 151 */     } else if (value instanceof Collection) {
/*     */       
/* 153 */       Collection<?> collection = (Collection)value;
/* 154 */       result = Array.newInstance(this.targetComponentType, collection.size());
/*     */       
/* 156 */       int i = 0;
/* 157 */       for (Object element : collection) {
/* 158 */         Array.set(result, i, convertComponentType(element));
/* 159 */         i++;
/*     */       } 
/* 161 */     } else if (value instanceof Iterable) {
/*     */       
/* 163 */       List<?> list = IterUtil.toList((Iterable)value);
/* 164 */       result = Array.newInstance(this.targetComponentType, list.size());
/* 165 */       for (int i = 0; i < list.size(); i++) {
/* 166 */         Array.set(result, i, convertComponentType(list.get(i)));
/*     */       }
/* 168 */     } else if (value instanceof Iterator) {
/*     */       
/* 170 */       List<?> list = IterUtil.toList((Iterator)value);
/* 171 */       result = Array.newInstance(this.targetComponentType, list.size());
/* 172 */       for (int i = 0; i < list.size(); i++) {
/* 173 */         Array.set(result, i, convertComponentType(list.get(i)));
/*     */       }
/* 175 */     } else if (value instanceof Number && byte.class == this.targetComponentType) {
/*     */       
/* 177 */       result = ByteUtil.numberToBytes((Number)value);
/* 178 */     } else if (value instanceof java.io.Serializable && byte.class == this.targetComponentType) {
/*     */       
/* 180 */       result = ObjectUtil.serialize(value);
/*     */     } else {
/*     */       
/* 183 */       result = convertToSingleElementArray(value);
/*     */     } 
/*     */     
/* 186 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Object[] convertToSingleElementArray(Object value) {
/* 196 */     Object[] singleElementArray = ArrayUtil.newArray(this.targetComponentType, 1);
/* 197 */     singleElementArray[0] = convertComponentType(value);
/* 198 */     return singleElementArray;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Object convertComponentType(Object value) {
/* 209 */     return Convert.convertWithCheck(this.targetComponentType, value, null, this.ignoreElementError);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\impl\ArrayConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */