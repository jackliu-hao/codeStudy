package cn.hutool.core.getter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public interface OptBasicTypeGetter<K> {
   Object getObj(K var1, Object var2);

   String getStr(K var1, String var2);

   Integer getInt(K var1, Integer var2);

   Short getShort(K var1, Short var2);

   Boolean getBool(K var1, Boolean var2);

   Long getLong(K var1, Long var2);

   Character getChar(K var1, Character var2);

   Float getFloat(K var1, Float var2);

   Double getDouble(K var1, Double var2);

   Byte getByte(K var1, Byte var2);

   BigDecimal getBigDecimal(K var1, BigDecimal var2);

   BigInteger getBigInteger(K var1, BigInteger var2);

   <E extends Enum<E>> E getEnum(Class<E> var1, K var2, E var3);

   Date getDate(K var1, Date var2);
}
