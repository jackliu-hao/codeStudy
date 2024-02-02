package org.h2.expression;

import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

public interface ParameterInterface {
  void setValue(Value paramValue, boolean paramBoolean);
  
  Value getParamValue();
  
  void checkSet() throws DbException;
  
  boolean isValueSet();
  
  TypeInfo getType();
  
  int getNullable();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\ParameterInterface.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */