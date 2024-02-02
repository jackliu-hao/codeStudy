package org.h2.jdbc;

import java.sql.SQLException;

public interface JdbcStatementBackwardsCompat {
  String enquoteIdentifier(String paramString, boolean paramBoolean) throws SQLException;
  
  boolean isSimpleIdentifier(String paramString) throws SQLException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\jdbc\JdbcStatementBackwardsCompat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */