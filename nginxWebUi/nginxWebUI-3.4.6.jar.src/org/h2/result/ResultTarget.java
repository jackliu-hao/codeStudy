package org.h2.result;

import org.h2.value.Value;

public interface ResultTarget {
  void addRow(Value... paramVarArgs);
  
  long getRowCount();
  
  void limitsWereApplied();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\result\ResultTarget.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */