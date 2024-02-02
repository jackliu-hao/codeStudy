package com.mysql.cj.protocol;

import com.mysql.cj.exceptions.CJOperationNotSupportedException;
import com.mysql.cj.exceptions.ExceptionFactory;

public interface MessageListener<M extends Message> {
   default boolean processMessage(M message) {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not allowed");
   }

   void error(Throwable var1);
}
