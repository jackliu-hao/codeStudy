package cn.hutool.core.getter;

import cn.hutool.core.convert.Convert;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public interface OptNullBasicTypeFromStringGetter<K> extends OptNullBasicTypeGetter<K> {
   default Object getObj(K key, Object defaultValue) {
      return this.getStr(key, null == defaultValue ? null : defaultValue.toString());
   }

   default Integer getInt(K key, Integer defaultValue) {
      return Convert.toInt(this.getStr(key), defaultValue);
   }

   default Short getShort(K key, Short defaultValue) {
      return Convert.toShort(this.getStr(key), defaultValue);
   }

   default Boolean getBool(K key, Boolean defaultValue) {
      return Convert.toBool(this.getStr(key), defaultValue);
   }

   default Long getLong(K key, Long defaultValue) {
      return Convert.toLong(this.getStr(key), defaultValue);
   }

   default Character getChar(K key, Character defaultValue) {
      return Convert.toChar(this.getStr(key), defaultValue);
   }

   default Float getFloat(K key, Float defaultValue) {
      return Convert.toFloat(this.getStr(key), defaultValue);
   }

   default Double getDouble(K key, Double defaultValue) {
      return Convert.toDouble(this.getStr(key), defaultValue);
   }

   default Byte getByte(K key, Byte defaultValue) {
      return Convert.toByte(this.getStr(key), defaultValue);
   }

   default BigDecimal getBigDecimal(K key, BigDecimal defaultValue) {
      return Convert.toBigDecimal(this.getStr(key), defaultValue);
   }

   default BigInteger getBigInteger(K key, BigInteger defaultValue) {
      return Convert.toBigInteger(this.getStr(key), defaultValue);
   }

   default <E extends Enum<E>> E getEnum(Class<E> clazz, K key, E defaultValue) {
      return Convert.toEnum(clazz, this.getStr(key), defaultValue);
   }

   default Date getDate(K key, Date defaultValue) {
      return Convert.toDate(this.getStr(key), defaultValue);
   }
}
