package com.mysql.cj.result;

import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.CJOperationNotSupportedException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.protocol.ColumnDefinition;
import com.mysql.cj.protocol.ProtocolEntity;

public interface Row extends ProtocolEntity {
   <T> T getValue(int var1, ValueFactory<T> var2);

   default Row setMetadata(ColumnDefinition columnDefinition) {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, Messages.getString("OperationNotSupportedException.0"));
   }

   default byte[] getBytes(int columnIndex) {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, Messages.getString("OperationNotSupportedException.0"));
   }

   default void setBytes(int columnIndex, byte[] value) {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, Messages.getString("OperationNotSupportedException.0"));
   }

   boolean getNull(int var1);

   boolean wasNull();
}
