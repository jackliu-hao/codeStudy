package com.mysql.cj.protocol;

import com.mysql.cj.result.Field;
import com.mysql.cj.result.ValueFactory;

public interface ValueDecoder {
  <T> T decodeDate(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, ValueFactory<T> paramValueFactory);
  
  <T> T decodeTime(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3, ValueFactory<T> paramValueFactory);
  
  <T> T decodeTimestamp(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3, ValueFactory<T> paramValueFactory);
  
  <T> T decodeDatetime(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3, ValueFactory<T> paramValueFactory);
  
  <T> T decodeInt1(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, ValueFactory<T> paramValueFactory);
  
  <T> T decodeUInt1(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, ValueFactory<T> paramValueFactory);
  
  <T> T decodeInt2(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, ValueFactory<T> paramValueFactory);
  
  <T> T decodeUInt2(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, ValueFactory<T> paramValueFactory);
  
  <T> T decodeInt4(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, ValueFactory<T> paramValueFactory);
  
  <T> T decodeUInt4(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, ValueFactory<T> paramValueFactory);
  
  <T> T decodeInt8(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, ValueFactory<T> paramValueFactory);
  
  <T> T decodeUInt8(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, ValueFactory<T> paramValueFactory);
  
  <T> T decodeFloat(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, ValueFactory<T> paramValueFactory);
  
  <T> T decodeDouble(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, ValueFactory<T> paramValueFactory);
  
  <T> T decodeDecimal(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, ValueFactory<T> paramValueFactory);
  
  <T> T decodeByteArray(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, Field paramField, ValueFactory<T> paramValueFactory);
  
  <T> T decodeBit(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, ValueFactory<T> paramValueFactory);
  
  <T> T decodeSet(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, Field paramField, ValueFactory<T> paramValueFactory);
  
  <T> T decodeYear(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, ValueFactory<T> paramValueFactory);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\ValueDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */