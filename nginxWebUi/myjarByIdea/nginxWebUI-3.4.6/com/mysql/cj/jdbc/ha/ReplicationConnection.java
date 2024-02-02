package com.mysql.cj.jdbc.ha;

import com.mysql.cj.jdbc.JdbcConnection;
import java.sql.SQLException;

public interface ReplicationConnection extends JdbcConnection {
   long getConnectionGroupId();

   JdbcConnection getCurrentConnection();

   JdbcConnection getSourceConnection();

   /** @deprecated */
   @Deprecated
   default JdbcConnection getMasterConnection() {
      return this.getSourceConnection();
   }

   void promoteReplicaToSource(String var1) throws SQLException;

   /** @deprecated */
   @Deprecated
   default void promoteSlaveToMaster(String host) throws SQLException {
      this.promoteReplicaToSource(host);
   }

   void removeSourceHost(String var1) throws SQLException;

   /** @deprecated */
   @Deprecated
   default void removeMasterHost(String host) throws SQLException {
      this.removeSourceHost(host);
   }

   void removeSourceHost(String var1, boolean var2) throws SQLException;

   /** @deprecated */
   @Deprecated
   default void removeMasterHost(String host, boolean waitUntilNotInUse) throws SQLException {
      this.removeSourceHost(host, waitUntilNotInUse);
   }

   boolean isHostSource(String var1);

   /** @deprecated */
   @Deprecated
   default boolean isHostMaster(String host) {
      return this.isHostSource(host);
   }

   JdbcConnection getReplicaConnection();

   /** @deprecated */
   @Deprecated
   default JdbcConnection getSlavesConnection() {
      return this.getReplicaConnection();
   }

   void addReplicaHost(String var1) throws SQLException;

   /** @deprecated */
   @Deprecated
   default void addSlaveHost(String host) throws SQLException {
      this.addReplicaHost(host);
   }

   void removeReplica(String var1) throws SQLException;

   /** @deprecated */
   @Deprecated
   default void removeSlave(String host) throws SQLException {
      this.removeReplica(host);
   }

   void removeReplica(String var1, boolean var2) throws SQLException;

   /** @deprecated */
   @Deprecated
   default void removeSlave(String host, boolean closeGently) throws SQLException {
      this.removeReplica(host, closeGently);
   }

   boolean isHostReplica(String var1);

   /** @deprecated */
   @Deprecated
   default boolean isHostSlave(String host) {
      return this.isHostReplica(host);
   }
}
