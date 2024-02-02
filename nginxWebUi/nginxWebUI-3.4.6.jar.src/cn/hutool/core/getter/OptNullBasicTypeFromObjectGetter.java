/*     */ package cn.hutool.core.getter;
/*     */ 
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.util.Date;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface OptNullBasicTypeFromObjectGetter<K>
/*     */   extends OptNullBasicTypeGetter<K>
/*     */ {
/*     */   default String getStr(K key, String defaultValue) {
/*  19 */     Object obj = getObj(key);
/*  20 */     if (null == obj) {
/*  21 */       return defaultValue;
/*     */     }
/*  23 */     return Convert.toStr(obj, defaultValue);
/*     */   }
/*     */ 
/*     */   
/*     */   default Integer getInt(K key, Integer defaultValue) {
/*  28 */     Object obj = getObj(key);
/*  29 */     if (null == obj) {
/*  30 */       return defaultValue;
/*     */     }
/*  32 */     return Convert.toInt(obj, defaultValue);
/*     */   }
/*     */ 
/*     */   
/*     */   default Short getShort(K key, Short defaultValue) {
/*  37 */     Object obj = getObj(key);
/*  38 */     if (null == obj) {
/*  39 */       return defaultValue;
/*     */     }
/*  41 */     return Convert.toShort(obj, defaultValue);
/*     */   }
/*     */ 
/*     */   
/*     */   default Boolean getBool(K key, Boolean defaultValue) {
/*  46 */     Object obj = getObj(key);
/*  47 */     if (null == obj) {
/*  48 */       return defaultValue;
/*     */     }
/*  50 */     return Convert.toBool(obj, defaultValue);
/*     */   }
/*     */ 
/*     */   
/*     */   default Long getLong(K key, Long defaultValue) {
/*  55 */     Object obj = getObj(key);
/*  56 */     if (null == obj) {
/*  57 */       return defaultValue;
/*     */     }
/*  59 */     return Convert.toLong(obj, defaultValue);
/*     */   }
/*     */ 
/*     */   
/*     */   default Character getChar(K key, Character defaultValue) {
/*  64 */     Object obj = getObj(key);
/*  65 */     if (null == obj) {
/*  66 */       return defaultValue;
/*     */     }
/*  68 */     return Convert.toChar(obj, defaultValue);
/*     */   }
/*     */ 
/*     */   
/*     */   default Float getFloat(K key, Float defaultValue) {
/*  73 */     Object obj = getObj(key);
/*  74 */     if (null == obj) {
/*  75 */       return defaultValue;
/*     */     }
/*  77 */     return Convert.toFloat(obj, defaultValue);
/*     */   }
/*     */ 
/*     */   
/*     */   default Double getDouble(K key, Double defaultValue) {
/*  82 */     Object obj = getObj(key);
/*  83 */     if (null == obj) {
/*  84 */       return defaultValue;
/*     */     }
/*  86 */     return Convert.toDouble(obj, defaultValue);
/*     */   }
/*     */ 
/*     */   
/*     */   default Byte getByte(K key, Byte defaultValue) {
/*  91 */     Object obj = getObj(key);
/*  92 */     if (null == obj) {
/*  93 */       return defaultValue;
/*     */     }
/*  95 */     return Convert.toByte(obj, defaultValue);
/*     */   }
/*     */ 
/*     */   
/*     */   default BigDecimal getBigDecimal(K key, BigDecimal defaultValue) {
/* 100 */     Object obj = getObj(key);
/* 101 */     if (null == obj) {
/* 102 */       return defaultValue;
/*     */     }
/* 104 */     return Convert.toBigDecimal(obj, defaultValue);
/*     */   }
/*     */ 
/*     */   
/*     */   default BigInteger getBigInteger(K key, BigInteger defaultValue) {
/* 109 */     Object obj = getObj(key);
/* 110 */     if (null == obj) {
/* 111 */       return defaultValue;
/*     */     }
/* 113 */     return Convert.toBigInteger(obj, defaultValue);
/*     */   }
/*     */ 
/*     */   
/*     */   default <E extends Enum<E>> E getEnum(Class<E> clazz, K key, E defaultValue) {
/* 118 */     Object obj = getObj(key);
/* 119 */     if (null == obj) {
/* 120 */       return defaultValue;
/*     */     }
/* 122 */     return (E)Convert.toEnum(clazz, obj, (Enum)defaultValue);
/*     */   }
/*     */ 
/*     */   
/*     */   default Date getDate(K key, Date defaultValue) {
/* 127 */     Object obj = getObj(key);
/* 128 */     if (null == obj) {
/* 129 */       return defaultValue;
/*     */     }
/* 131 */     return Convert.toDate(obj, defaultValue);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\getter\OptNullBasicTypeFromObjectGetter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */