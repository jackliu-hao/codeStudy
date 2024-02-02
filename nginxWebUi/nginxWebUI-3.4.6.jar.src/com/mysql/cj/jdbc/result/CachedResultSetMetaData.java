package com.mysql.cj.jdbc.result;

import com.mysql.cj.protocol.ColumnDefinition;
import java.sql.ResultSetMetaData;

public interface CachedResultSetMetaData extends ColumnDefinition {
  ResultSetMetaData getMetadata();
  
  void setMetadata(ResultSetMetaData paramResultSetMetaData);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\result\CachedResultSetMetaData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */