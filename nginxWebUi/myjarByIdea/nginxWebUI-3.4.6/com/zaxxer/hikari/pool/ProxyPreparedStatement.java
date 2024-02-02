package com.zaxxer.hikari.pool;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class ProxyPreparedStatement extends ProxyStatement implements PreparedStatement {
   ProxyPreparedStatement(ProxyConnection connection, PreparedStatement statement) {
      super(connection, statement);
   }

   public boolean execute() throws SQLException {
      this.connection.markCommitStateDirty();
      return ((PreparedStatement)this.delegate).execute();
   }

   public ResultSet executeQuery() throws SQLException {
      this.connection.markCommitStateDirty();
      ResultSet resultSet = ((PreparedStatement)this.delegate).executeQuery();
      return ProxyFactory.getProxyResultSet(this.connection, this, resultSet);
   }

   public int executeUpdate() throws SQLException {
      this.connection.markCommitStateDirty();
      return ((PreparedStatement)this.delegate).executeUpdate();
   }

   public long executeLargeUpdate() throws SQLException {
      this.connection.markCommitStateDirty();
      return ((PreparedStatement)this.delegate).executeLargeUpdate();
   }
}
