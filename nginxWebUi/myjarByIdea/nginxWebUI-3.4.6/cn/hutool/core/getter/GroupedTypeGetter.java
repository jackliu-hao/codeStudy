package cn.hutool.core.getter;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface GroupedTypeGetter {
   String getStrByGroup(String var1, String var2);

   Integer getIntByGroup(String var1, String var2);

   Short getShortByGroup(String var1, String var2);

   Boolean getBoolByGroup(String var1, String var2);

   Long getLongByGroup(String var1, String var2);

   Character getCharByGroup(String var1, String var2);

   Double getDoubleByGroup(String var1, String var2);

   Byte getByteByGroup(String var1, String var2);

   BigDecimal getBigDecimalByGroup(String var1, String var2);

   BigInteger getBigIntegerByGroup(String var1, String var2);
}
