package cn.hutool.core.getter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public interface OptNullBasicTypeGetter<K> extends BasicTypeGetter<K>, OptBasicTypeGetter<K> {
   default Object getObj(K key) {
      return this.getObj(key, (Object)null);
   }

   default String getStr(K key) {
      return this.getStr(key, (String)null);
   }

   default Integer getInt(K key) {
      return this.getInt(key, (Integer)null);
   }

   default Short getShort(K key) {
      return this.getShort(key, (Short)null);
   }

   default Boolean getBool(K key) {
      return this.getBool(key, (Boolean)null);
   }

   default Long getLong(K key) {
      return this.getLong(key, (Long)null);
   }

   default Character getChar(K key) {
      return this.getChar(key, (Character)null);
   }

   default Float getFloat(K key) {
      return this.getFloat(key, (Float)null);
   }

   default Double getDouble(K key) {
      return this.getDouble(key, (Double)null);
   }

   default Byte getByte(K key) {
      return this.getByte(key, (Byte)null);
   }

   default BigDecimal getBigDecimal(K key) {
      return this.getBigDecimal(key, (BigDecimal)null);
   }

   default BigInteger getBigInteger(K key) {
      return this.getBigInteger(key, (BigInteger)null);
   }

   default <E extends Enum<E>> E getEnum(Class<E> clazz, K key) {
      return this.getEnum(clazz, key, (Enum)null);
   }

   default Date getDate(K key) {
      return this.getDate(key, (Date)null);
   }
}
