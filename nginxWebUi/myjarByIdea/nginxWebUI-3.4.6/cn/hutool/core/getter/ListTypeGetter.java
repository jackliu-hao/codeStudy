package cn.hutool.core.getter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public interface ListTypeGetter {
   List<Object> getObjList(String var1);

   List<String> getStrList(String var1);

   List<Integer> getIntList(String var1);

   List<Short> getShortList(String var1);

   List<Boolean> getBoolList(String var1);

   List<Long> getLongList(String var1);

   List<Character> getCharList(String var1);

   List<Double> getDoubleList(String var1);

   List<Byte> getByteList(String var1);

   List<BigDecimal> getBigDecimalList(String var1);

   List<BigInteger> getBigIntegerList(String var1);
}
