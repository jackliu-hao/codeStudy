package org.h2.jdbc;

public interface JdbcException {
   int getErrorCode();

   String getOriginalMessage();

   String getSQL();

   void setSQL(String var1);

   String toString();
}
