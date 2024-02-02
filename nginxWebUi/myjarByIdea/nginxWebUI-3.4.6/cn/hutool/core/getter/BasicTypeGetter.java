package cn.hutool.core.getter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public interface BasicTypeGetter<K> {
   Object getObj(K var1);

   String getStr(K var1);

   Integer getInt(K var1);

   Short getShort(K var1);

   Boolean getBool(K var1);

   Long getLong(K var1);

   Character getChar(K var1);

   Float getFloat(K var1);

   Double getDouble(K var1);

   Byte getByte(K var1);

   BigDecimal getBigDecimal(K var1);

   BigInteger getBigInteger(K var1);

   <E extends Enum<E>> E getEnum(Class<E> var1, K var2);

   Date getDate(K var1);
}
