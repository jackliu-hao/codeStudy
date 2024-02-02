package cn.hutool.core.getter;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface GroupedTypeGetter {
  String getStrByGroup(String paramString1, String paramString2);
  
  Integer getIntByGroup(String paramString1, String paramString2);
  
  Short getShortByGroup(String paramString1, String paramString2);
  
  Boolean getBoolByGroup(String paramString1, String paramString2);
  
  Long getLongByGroup(String paramString1, String paramString2);
  
  Character getCharByGroup(String paramString1, String paramString2);
  
  Double getDoubleByGroup(String paramString1, String paramString2);
  
  Byte getByteByGroup(String paramString1, String paramString2);
  
  BigDecimal getBigDecimalByGroup(String paramString1, String paramString2);
  
  BigInteger getBigIntegerByGroup(String paramString1, String paramString2);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\getter\GroupedTypeGetter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */