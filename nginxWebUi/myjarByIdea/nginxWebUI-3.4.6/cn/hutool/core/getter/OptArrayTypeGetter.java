package cn.hutool.core.getter;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface OptArrayTypeGetter {
   Object[] getObjs(String var1, Object[] var2);

   String[] getStrs(String var1, String[] var2);

   Integer[] getInts(String var1, Integer[] var2);

   Short[] getShorts(String var1, Short[] var2);

   Boolean[] getBools(String var1, Boolean[] var2);

   Long[] getLongs(String var1, Long[] var2);

   Character[] getChars(String var1, Character[] var2);

   Double[] getDoubles(String var1, Double[] var2);

   Byte[] getBytes(String var1, Byte[] var2);

   BigInteger[] getBigIntegers(String var1, BigInteger[] var2);

   BigDecimal[] getBigDecimals(String var1, BigDecimal[] var2);
}
