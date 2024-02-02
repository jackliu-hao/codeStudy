package com.mysql.cj.jdbc;

import com.mysql.cj.MysqlType;
import com.mysql.cj.ParseInfo;
import com.mysql.cj.QueryBindings;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface JdbcPreparedStatement extends PreparedStatement, JdbcStatement {
  void realClose(boolean paramBoolean1, boolean paramBoolean2) throws SQLException;
  
  QueryBindings<?> getQueryBindings();
  
  byte[] getBytesRepresentation(int paramInt) throws SQLException;
  
  byte[] getOrigBytes(int paramInt) throws SQLException;
  
  ParseInfo getParseInfo();
  
  boolean isNull(int paramInt) throws SQLException;
  
  String getPreparedSql();
  
  void setBytes(int paramInt, byte[] paramArrayOfbyte, boolean paramBoolean1, boolean paramBoolean2) throws SQLException;
  
  void setBytesNoEscape(int paramInt, byte[] paramArrayOfbyte) throws SQLException;
  
  void setBytesNoEscapeNoQuotes(int paramInt, byte[] paramArrayOfbyte) throws SQLException;
  
  void setBigInteger(int paramInt, BigInteger paramBigInteger) throws SQLException;
  
  void setNull(int paramInt, MysqlType paramMysqlType) throws SQLException;
  
  ParameterBindings getParameterBindings() throws SQLException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\JdbcPreparedStatement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */