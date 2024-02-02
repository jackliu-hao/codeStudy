package org.h2.tools;

import java.sql.SQLException;

public interface SimpleRowSource {
   Object[] readRow() throws SQLException;

   void close();

   void reset() throws SQLException;
}
