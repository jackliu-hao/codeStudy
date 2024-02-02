package com.mysql.cj.xdevapi;

public interface SqlResult extends Result, InsertResult, RowResult {
   default boolean nextResult() {
      return false;
   }

   default Long getAutoIncrementValue() {
      throw new XDevAPIError("Method getAutoIncrementValue() is allowed only for insert statements.");
   }
}
