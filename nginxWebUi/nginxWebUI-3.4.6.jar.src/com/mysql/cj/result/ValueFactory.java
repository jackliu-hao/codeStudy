package com.mysql.cj.result;

import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.protocol.InternalDate;
import com.mysql.cj.protocol.InternalTime;
import com.mysql.cj.protocol.InternalTimestamp;
import java.math.BigDecimal;
import java.math.BigInteger;

public interface ValueFactory<T> {
  void setPropertySet(PropertySet paramPropertySet);
  
  T createFromDate(InternalDate paramInternalDate);
  
  T createFromTime(InternalTime paramInternalTime);
  
  T createFromTimestamp(InternalTimestamp paramInternalTimestamp);
  
  T createFromDatetime(InternalTimestamp paramInternalTimestamp);
  
  T createFromLong(long paramLong);
  
  T createFromBigInteger(BigInteger paramBigInteger);
  
  T createFromDouble(double paramDouble);
  
  T createFromBigDecimal(BigDecimal paramBigDecimal);
  
  T createFromBytes(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, Field paramField);
  
  T createFromBit(byte[] paramArrayOfbyte, int paramInt1, int paramInt2);
  
  T createFromYear(long paramLong);
  
  T createFromNull();
  
  String getTargetTypeName();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\result\ValueFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */