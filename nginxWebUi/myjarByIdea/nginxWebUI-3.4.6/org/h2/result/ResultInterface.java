package org.h2.result;

import org.h2.engine.Session;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

public interface ResultInterface extends AutoCloseable {
   void reset();

   Value[] currentRow();

   boolean next();

   long getRowId();

   boolean isAfterLast();

   int getVisibleColumnCount();

   long getRowCount();

   boolean hasNext();

   boolean needToClose();

   void close();

   String getAlias(int var1);

   String getSchemaName(int var1);

   String getTableName(int var1);

   String getColumnName(int var1);

   TypeInfo getColumnType(int var1);

   boolean isIdentity(int var1);

   int getNullable(int var1);

   void setFetchSize(int var1);

   int getFetchSize();

   boolean isLazy();

   boolean isClosed();

   ResultInterface createShallowCopy(Session var1);
}
