package org.h2.api;

import java.sql.Connection;
import java.sql.SQLException;

public interface Trigger {
   int INSERT = 1;
   int UPDATE = 2;
   int DELETE = 4;
   int SELECT = 8;

   default void init(Connection var1, String var2, String var3, String var4, boolean var5, int var6) throws SQLException {
   }

   void fire(Connection var1, Object[] var2, Object[] var3) throws SQLException;

   default void close() throws SQLException {
   }

   default void remove() throws SQLException {
   }
}
