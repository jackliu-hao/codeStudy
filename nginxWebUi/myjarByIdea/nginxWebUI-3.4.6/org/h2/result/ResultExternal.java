package org.h2.result;

import java.util.Collection;
import org.h2.value.Value;

public interface ResultExternal {
   void reset();

   Value[] next();

   int addRow(Value[] var1);

   int addRows(Collection<Value[]> var1);

   void close();

   int removeRow(Value[] var1);

   boolean contains(Value[] var1);

   ResultExternal createShallowCopy();
}
