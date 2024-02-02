package com.mysql.cj.protocol;

import com.mysql.cj.exceptions.CJOperationNotSupportedException;
import com.mysql.cj.exceptions.ExceptionFactory;
import java.io.IOException;

public interface ProtocolEntityReader<T extends ProtocolEntity, M extends Message> {
   default T read(ProtocolEntityFactory<T, M> sf) throws IOException {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not allowed");
   }

   default T read(int maxRows, boolean streamResults, M resultPacket, ColumnDefinition metadata, ProtocolEntityFactory<T, M> protocolEntityFactory) throws IOException {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not allowed");
   }
}
