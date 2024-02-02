/*    */ package cn.hutool.core.convert;
/*    */ 
/*    */ import java.util.Map;
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum BasicType
/*    */ {
/* 12 */   BYTE, SHORT, INT, INTEGER, LONG, DOUBLE, FLOAT, BOOLEAN, CHAR, CHARACTER, STRING;
/*    */   
/*    */   static {
/* 15 */     WRAPPER_PRIMITIVE_MAP = new ConcurrentHashMap<>(8);
/*    */     
/* 17 */     PRIMITIVE_WRAPPER_MAP = new ConcurrentHashMap<>(8);
/*    */ 
/*    */     
/* 20 */     WRAPPER_PRIMITIVE_MAP.put(Boolean.class, boolean.class);
/* 21 */     WRAPPER_PRIMITIVE_MAP.put(Byte.class, byte.class);
/* 22 */     WRAPPER_PRIMITIVE_MAP.put(Character.class, char.class);
/* 23 */     WRAPPER_PRIMITIVE_MAP.put(Double.class, double.class);
/* 24 */     WRAPPER_PRIMITIVE_MAP.put(Float.class, float.class);
/* 25 */     WRAPPER_PRIMITIVE_MAP.put(Integer.class, int.class);
/* 26 */     WRAPPER_PRIMITIVE_MAP.put(Long.class, long.class);
/* 27 */     WRAPPER_PRIMITIVE_MAP.put(Short.class, short.class);
/*    */     
/* 29 */     for (Map.Entry<Class<?>, Class<?>> entry : WRAPPER_PRIMITIVE_MAP.entrySet()) {
/* 30 */       PRIMITIVE_WRAPPER_MAP.put(entry.getValue(), entry.getKey());
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPER_MAP;
/*    */   
/*    */   public static final Map<Class<?>, Class<?>> WRAPPER_PRIMITIVE_MAP;
/*    */   
/*    */   public static Class<?> wrap(Class<?> clazz) {
/* 40 */     if (null == clazz || false == clazz.isPrimitive()) {
/* 41 */       return clazz;
/*    */     }
/* 43 */     Class<?> result = PRIMITIVE_WRAPPER_MAP.get(clazz);
/* 44 */     return (null == result) ? clazz : result;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Class<?> unWrap(Class<?> clazz) {
/* 53 */     if (null == clazz || clazz.isPrimitive()) {
/* 54 */       return clazz;
/*    */     }
/* 56 */     Class<?> result = WRAPPER_PRIMITIVE_MAP.get(clazz);
/* 57 */     return (null == result) ? clazz : result;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\BasicType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */