package org.h2.result;

import java.util.Collection;
import org.h2.value.Value;

public interface ResultExternal {
  void reset();
  
  Value[] next();
  
  int addRow(Value[] paramArrayOfValue);
  
  int addRows(Collection<Value[]> paramCollection);
  
  void close();
  
  int removeRow(Value[] paramArrayOfValue);
  
  boolean contains(Value[] paramArrayOfValue);
  
  ResultExternal createShallowCopy();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\result\ResultExternal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */