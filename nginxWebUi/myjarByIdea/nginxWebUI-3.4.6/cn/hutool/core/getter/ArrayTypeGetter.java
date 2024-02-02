package cn.hutool.core.getter;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface ArrayTypeGetter {
   String[] getObjs(String var1);

   String[] getStrs(String var1);

   Integer[] getInts(String var1);

   Short[] getShorts(String var1);

   Boolean[] getBools(String var1);

   Long[] getLongs(String var1);

   Character[] getChars(String var1);

   Double[] getDoubles(String var1);

   Byte[] getBytes(String var1);

   BigInteger[] getBigIntegers(String var1);

   BigDecimal[] getBigDecimals(String var1);
}
