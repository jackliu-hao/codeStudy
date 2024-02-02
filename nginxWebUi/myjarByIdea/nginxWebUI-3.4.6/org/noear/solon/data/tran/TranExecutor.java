package org.noear.solon.data.tran;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.noear.solon.data.annotation.Tran;
import org.noear.solon.ext.RunnableEx;

public interface TranExecutor {
   boolean inTrans();

   default boolean inTransAndReadOnly() {
      return false;
   }

   default Connection getConnection(DataSource ds) throws SQLException {
      return ds.getConnection();
   }

   default void execute(Tran meta, RunnableEx runnable) throws Throwable {
   }
}
