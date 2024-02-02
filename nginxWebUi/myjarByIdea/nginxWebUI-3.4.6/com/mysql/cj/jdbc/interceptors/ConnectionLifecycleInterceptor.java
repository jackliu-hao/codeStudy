package com.mysql.cj.jdbc.interceptors;

import com.mysql.cj.MysqlConnection;
import com.mysql.cj.log.Log;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Properties;

public interface ConnectionLifecycleInterceptor {
   ConnectionLifecycleInterceptor init(MysqlConnection var1, Properties var2, Log var3);

   void destroy();

   void close() throws SQLException;

   boolean commit() throws SQLException;

   boolean rollback() throws SQLException;

   boolean rollback(Savepoint var1) throws SQLException;

   boolean setAutoCommit(boolean var1) throws SQLException;

   boolean setDatabase(String var1) throws SQLException;

   boolean transactionBegun();

   boolean transactionCompleted();
}
