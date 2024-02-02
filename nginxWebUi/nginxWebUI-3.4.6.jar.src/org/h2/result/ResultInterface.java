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
  
  String getAlias(int paramInt);
  
  String getSchemaName(int paramInt);
  
  String getTableName(int paramInt);
  
  String getColumnName(int paramInt);
  
  TypeInfo getColumnType(int paramInt);
  
  boolean isIdentity(int paramInt);
  
  int getNullable(int paramInt);
  
  void setFetchSize(int paramInt);
  
  int getFetchSize();
  
  boolean isLazy();
  
  boolean isClosed();
  
  ResultInterface createShallowCopy(Session paramSession);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\result\ResultInterface.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */