package cn.hutool.core.getter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public interface ListTypeGetter {
  List<Object> getObjList(String paramString);
  
  List<String> getStrList(String paramString);
  
  List<Integer> getIntList(String paramString);
  
  List<Short> getShortList(String paramString);
  
  List<Boolean> getBoolList(String paramString);
  
  List<Long> getLongList(String paramString);
  
  List<Character> getCharList(String paramString);
  
  List<Double> getDoubleList(String paramString);
  
  List<Byte> getByteList(String paramString);
  
  List<BigDecimal> getBigDecimalList(String paramString);
  
  List<BigInteger> getBigIntegerList(String paramString);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\getter\ListTypeGetter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */