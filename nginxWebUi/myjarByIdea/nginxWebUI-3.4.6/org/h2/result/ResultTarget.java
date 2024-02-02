package org.h2.result;

import org.h2.value.Value;

public interface ResultTarget {
   void addRow(Value... var1);

   long getRowCount();

   void limitsWereApplied();
}
