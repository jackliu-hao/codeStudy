package cn.hutool.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

public enum ThreadLocalConnection {
   INSTANCE;

   private final ThreadLocal<GroupedConnection> threadLocal = new ThreadLocal();

   public Connection get(DataSource ds) throws SQLException {
      GroupedConnection groupedConnection = (GroupedConnection)this.threadLocal.get();
      if (null == groupedConnection) {
         groupedConnection = new GroupedConnection();
         this.threadLocal.set(groupedConnection);
      }

      return groupedConnection.get(ds);
   }

   public void close(DataSource ds) {
      GroupedConnection groupedConnection = (GroupedConnection)this.threadLocal.get();
      if (null != groupedConnection) {
         groupedConnection.close(ds);
         if (groupedConnection.isEmpty()) {
            this.threadLocal.remove();
         }
      }

   }

   public static class GroupedConnection {
      private final Map<DataSource, Connection> connMap = new HashMap(1, 1.0F);

      public Connection get(DataSource ds) throws SQLException {
         Connection conn = (Connection)this.connMap.get(ds);
         if (null == conn || conn.isClosed()) {
            conn = ds.getConnection();
            this.connMap.put(ds, conn);
         }

         return conn;
      }

      public GroupedConnection close(DataSource ds) {
         Connection conn = (Connection)this.connMap.get(ds);
         if (null != conn) {
            try {
               if (!conn.getAutoCommit()) {
                  return this;
               }
            } catch (SQLException var4) {
            }

            this.connMap.remove(ds);
            DbUtil.close(conn);
         }

         return this;
      }

      public boolean isEmpty() {
         return this.connMap.isEmpty();
      }
   }
}
