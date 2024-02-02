package org.h2.api;

import java.sql.Connection;
import java.sql.SQLException;

public interface Aggregate {
  default void init(Connection paramConnection) throws SQLException {}
  
  int getInternalType(int[] paramArrayOfint) throws SQLException;
  
  void add(Object paramObject) throws SQLException;
  
  Object getResult() throws SQLException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\api\Aggregate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */