package org.h2.api;

import java.sql.Connection;
import java.sql.SQLException;

public interface Aggregate {
   default void init(Connection var1) throws SQLException {
   }

   int getInternalType(int[] var1) throws SQLException;

   void add(Object var1) throws SQLException;

   Object getResult() throws SQLException;
}
