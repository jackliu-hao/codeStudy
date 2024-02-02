package cn.hutool.core.getter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public interface OptBasicTypeGetter<K> {
  Object getObj(K paramK, Object paramObject);
  
  String getStr(K paramK, String paramString);
  
  Integer getInt(K paramK, Integer paramInteger);
  
  Short getShort(K paramK, Short paramShort);
  
  Boolean getBool(K paramK, Boolean paramBoolean);
  
  Long getLong(K paramK, Long paramLong);
  
  Character getChar(K paramK, Character paramCharacter);
  
  Float getFloat(K paramK, Float paramFloat);
  
  Double getDouble(K paramK, Double paramDouble);
  
  Byte getByte(K paramK, Byte paramByte);
  
  BigDecimal getBigDecimal(K paramK, BigDecimal paramBigDecimal);
  
  BigInteger getBigInteger(K paramK, BigInteger paramBigInteger);
  
  <E extends Enum<E>> E getEnum(Class<E> paramClass, K paramK, E paramE);
  
  Date getDate(K paramK, Date paramDate);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\getter\OptBasicTypeGetter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */