package com.mysql.cj.jdbc.result;

import com.mysql.cj.protocol.ColumnDefinition;

public interface CachedResultSetMetaData extends ColumnDefinition {
   java.sql.ResultSetMetaData getMetadata();

   void setMetadata(java.sql.ResultSetMetaData var1);
}
