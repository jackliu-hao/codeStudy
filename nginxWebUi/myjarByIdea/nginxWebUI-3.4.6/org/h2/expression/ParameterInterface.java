package org.h2.expression;

import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

public interface ParameterInterface {
   void setValue(Value var1, boolean var2);

   Value getParamValue();

   void checkSet() throws DbException;

   boolean isValueSet();

   TypeInfo getType();

   int getNullable();
}
