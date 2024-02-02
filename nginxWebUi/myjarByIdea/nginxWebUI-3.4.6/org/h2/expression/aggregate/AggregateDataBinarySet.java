package org.h2.expression.aggregate;

import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.value.Value;

abstract class AggregateDataBinarySet extends AggregateData {
   abstract void add(SessionLocal var1, Value var2, Value var3);

   final void add(SessionLocal var1, Value var2) {
      throw DbException.getInternalError();
   }
}
