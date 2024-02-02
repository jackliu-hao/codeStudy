package com.mysql.cj.protocol;

import com.mysql.cj.MysqlConnection;
import com.mysql.cj.Query;
import com.mysql.cj.Session;

public interface ResultsetRowsOwner {
   void closeOwner(boolean var1);

   MysqlConnection getConnection();

   Session getSession();

   Object getSyncMutex();

   String getPointOfOrigin();

   int getOwnerFetchSize();

   Query getOwningQuery();

   int getOwningStatementMaxRows();

   int getOwningStatementFetchSize();

   long getOwningStatementServerId();
}
