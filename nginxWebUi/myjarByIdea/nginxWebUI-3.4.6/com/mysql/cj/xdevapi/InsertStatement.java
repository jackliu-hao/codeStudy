package com.mysql.cj.xdevapi;

import java.util.Arrays;
import java.util.List;

public interface InsertStatement extends Statement<InsertStatement, InsertResult> {
   InsertStatement values(List<Object> var1);

   default InsertStatement values(Object... values) {
      return this.values(Arrays.asList(values));
   }
}
