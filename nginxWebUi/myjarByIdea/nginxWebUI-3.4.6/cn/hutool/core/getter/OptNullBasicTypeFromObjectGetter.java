package cn.hutool.core.getter;

import cn.hutool.core.convert.Convert;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public interface OptNullBasicTypeFromObjectGetter<K> extends OptNullBasicTypeGetter<K> {
   default String getStr(K key, String defaultValue) {
      Object obj = this.getObj(key);
      return null == obj ? defaultValue : Convert.toStr(obj, defaultValue);
   }

   default Integer getInt(K key, Integer defaultValue) {
      Object obj = this.getObj(key);
      return null == obj ? defaultValue : Convert.toInt(obj, defaultValue);
   }

   default Short getShort(K key, Short defaultValue) {
      Object obj = this.getObj(key);
      return null == obj ? defaultValue : Convert.toShort(obj, defaultValue);
   }

   default Boolean getBool(K key, Boolean defaultValue) {
      Object obj = this.getObj(key);
      return null == obj ? defaultValue : Convert.toBool(obj, defaultValue);
   }

   default Long getLong(K key, Long defaultValue) {
      Object obj = this.getObj(key);
      return null == obj ? defaultValue : Convert.toLong(obj, defaultValue);
   }

   default Character getChar(K key, Character defaultValue) {
      Object obj = this.getObj(key);
      return null == obj ? defaultValue : Convert.toChar(obj, defaultValue);
   }

   default Float getFloat(K key, Float defaultValue) {
      Object obj = this.getObj(key);
      return null == obj ? defaultValue : Convert.toFloat(obj, defaultValue);
   }

   default Double getDouble(K key, Double defaultValue) {
      Object obj = this.getObj(key);
      return null == obj ? defaultValue : Convert.toDouble(obj, defaultValue);
   }

   default Byte getByte(K key, Byte defaultValue) {
      Object obj = this.getObj(key);
      return null == obj ? defaultValue : Convert.toByte(obj, defaultValue);
   }

   default BigDecimal getBigDecimal(K key, BigDecimal defaultValue) {
      Object obj = this.getObj(key);
      return null == obj ? defaultValue : Convert.toBigDecimal(obj, defaultValue);
   }

   default BigInteger getBigInteger(K key, BigInteger defaultValue) {
      Object obj = this.getObj(key);
      return null == obj ? defaultValue : Convert.toBigInteger(obj, defaultValue);
   }

   default <E extends Enum<E>> E getEnum(Class<E> clazz, K key, E defaultValue) {
      Object obj = this.getObj(key);
      return null == obj ? defaultValue : Convert.toEnum(clazz, obj, defaultValue);
   }

   default Date getDate(K key, Date defaultValue) {
      Object obj = this.getObj(key);
      return null == obj ? defaultValue : Convert.toDate(obj, defaultValue);
   }
}
