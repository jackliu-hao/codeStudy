package org.h2.jdbc;

public interface JdbcException {
  int getErrorCode();
  
  String getOriginalMessage();
  
  String getSQL();
  
  void setSQL(String paramString);
  
  String toString();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\jdbc\JdbcException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */