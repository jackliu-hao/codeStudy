package org.h2.expression.aggregate;

import org.h2.engine.SessionLocal;
import org.h2.value.Value;

abstract class AggregateData {
   abstract void add(SessionLocal var1, Value var2);

   abstract Value getValue(SessionLocal var1);
}
