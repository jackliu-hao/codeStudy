/*    */ package cn.hutool.core.getter;
/*    */ 
/*    */ import cn.hutool.core.convert.Convert;
/*    */ import java.math.BigDecimal;
/*    */ import java.math.BigInteger;
/*    */ import java.util.Date;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface OptNullBasicTypeFromStringGetter<K>
/*    */   extends OptNullBasicTypeGetter<K>
/*    */ {
/*    */   default Object getObj(K key, Object defaultValue) {
/* 18 */     return getStr(key, (null == defaultValue) ? null : defaultValue.toString());
/*    */   }
/*    */ 
/*    */   
/*    */   default Integer getInt(K key, Integer defaultValue) {
/* 23 */     return Convert.toInt(getStr(key), defaultValue);
/*    */   }
/*    */ 
/*    */   
/*    */   default Short getShort(K key, Short defaultValue) {
/* 28 */     return Convert.toShort(getStr(key), defaultValue);
/*    */   }
/*    */ 
/*    */   
/*    */   default Boolean getBool(K key, Boolean defaultValue) {
/* 33 */     return Convert.toBool(getStr(key), defaultValue);
/*    */   }
/*    */ 
/*    */   
/*    */   default Long getLong(K key, Long defaultValue) {
/* 38 */     return Convert.toLong(getStr(key), defaultValue);
/*    */   }
/*    */ 
/*    */   
/*    */   default Character getChar(K key, Character defaultValue) {
/* 43 */     return Convert.toChar(getStr(key), defaultValue);
/*    */   }
/*    */ 
/*    */   
/*    */   default Float getFloat(K key, Float defaultValue) {
/* 48 */     return Convert.toFloat(getStr(key), defaultValue);
/*    */   }
/*    */ 
/*    */   
/*    */   default Double getDouble(K key, Double defaultValue) {
/* 53 */     return Convert.toDouble(getStr(key), defaultValue);
/*    */   }
/*    */ 
/*    */   
/*    */   default Byte getByte(K key, Byte defaultValue) {
/* 58 */     return Convert.toByte(getStr(key), defaultValue);
/*    */   }
/*    */ 
/*    */   
/*    */   default BigDecimal getBigDecimal(K key, BigDecimal defaultValue) {
/* 63 */     return Convert.toBigDecimal(getStr(key), defaultValue);
/*    */   }
/*    */ 
/*    */   
/*    */   default BigInteger getBigInteger(K key, BigInteger defaultValue) {
/* 68 */     return Convert.toBigInteger(getStr(key), defaultValue);
/*    */   }
/*    */ 
/*    */   
/*    */   default <E extends Enum<E>> E getEnum(Class<E> clazz, K key, E defaultValue) {
/* 73 */     return (E)Convert.toEnum(clazz, getStr(key), (Enum)defaultValue);
/*    */   }
/*    */ 
/*    */   
/*    */   default Date getDate(K key, Date defaultValue) {
/* 78 */     return Convert.toDate(getStr(key), defaultValue);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\getter\OptNullBasicTypeFromStringGetter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */