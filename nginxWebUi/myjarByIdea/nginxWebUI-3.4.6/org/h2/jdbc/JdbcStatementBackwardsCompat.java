package org.h2.jdbc;

import java.sql.SQLException;

public interface JdbcStatementBackwardsCompat {
   String enquoteIdentifier(String var1, boolean var2) throws SQLException;

   boolean isSimpleIdentifier(String var1) throws SQLException;
}
