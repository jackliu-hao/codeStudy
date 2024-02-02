package com.mysql.cj.protocol;

import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.CJOperationNotSupportedException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.result.Row;
import com.mysql.cj.result.RowList;

public interface ResultsetRows extends RowList, ProtocolEntity {
   default void addRow(Row row) {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, Messages.getString("OperationNotSupportedException.0"));
   }

   default void afterLast() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, Messages.getString("OperationNotSupportedException.0"));
   }

   default void beforeFirst() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, Messages.getString("OperationNotSupportedException.0"));
   }

   default void beforeLast() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, Messages.getString("OperationNotSupportedException.0"));
   }

   default void close() {
   }

   ResultsetRowsOwner getOwner();

   boolean isAfterLast();

   boolean isBeforeFirst();

   default boolean isDynamic() {
      return true;
   }

   default boolean isEmpty() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, Messages.getString("OperationNotSupportedException.0"));
   }

   default boolean isFirst() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, Messages.getString("OperationNotSupportedException.0"));
   }

   default boolean isLast() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, Messages.getString("OperationNotSupportedException.0"));
   }

   default void moveRowRelative(int rows) {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, Messages.getString("OperationNotSupportedException.0"));
   }

   default void setCurrentRow(int rowNumber) {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, Messages.getString("OperationNotSupportedException.0"));
   }

   void setOwner(ResultsetRowsOwner var1);

   boolean wasEmpty();

   void setMetadata(ColumnDefinition var1);

   ColumnDefinition getMetadata();
}
