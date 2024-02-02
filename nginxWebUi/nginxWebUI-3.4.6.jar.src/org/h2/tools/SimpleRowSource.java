package org.h2.tools;

import java.sql.SQLException;

public interface SimpleRowSource {
  Object[] readRow() throws SQLException;
  
  void close();
  
  void reset() throws SQLException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\tools\SimpleRowSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */