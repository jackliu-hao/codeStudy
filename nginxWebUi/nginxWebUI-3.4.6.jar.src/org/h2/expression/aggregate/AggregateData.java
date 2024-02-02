package org.h2.expression.aggregate;

import org.h2.engine.SessionLocal;
import org.h2.value.Value;

abstract class AggregateData {
  abstract void add(SessionLocal paramSessionLocal, Value paramValue);
  
  abstract Value getValue(SessionLocal paramSessionLocal);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\aggregate\AggregateData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */