package com.mysql.cj.jdbc;

import com.mysql.cj.MysqlType;
import com.mysql.cj.ParseInfo;
import com.mysql.cj.QueryBindings;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface JdbcPreparedStatement extends PreparedStatement, JdbcStatement {
   void realClose(boolean var1, boolean var2) throws SQLException;

   QueryBindings<?> getQueryBindings();

   byte[] getBytesRepresentation(int var1) throws SQLException;

   byte[] getOrigBytes(int var1) throws SQLException;

   ParseInfo getParseInfo();

   boolean isNull(int var1) throws SQLException;

   String getPreparedSql();

   void setBytes(int var1, byte[] var2, boolean var3, boolean var4) throws SQLException;

   void setBytesNoEscape(int var1, byte[] var2) throws SQLException;

   void setBytesNoEscapeNoQuotes(int var1, byte[] var2) throws SQLException;

   void setBigInteger(int var1, BigInteger var2) throws SQLException;

   void setNull(int var1, MysqlType var2) throws SQLException;

   ParameterBindings getParameterBindings() throws SQLException;
}
