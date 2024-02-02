package com.zaxxer.hikari.pool;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class ProxyResultSet implements ResultSet {
   protected final ProxyConnection connection;
   protected final ProxyStatement statement;
   final ResultSet delegate;

   protected ProxyResultSet(ProxyConnection connection, ProxyStatement statement, ResultSet resultSet) {
      this.connection = connection;
      this.statement = statement;
      this.delegate = resultSet;
   }

   final SQLException checkException(SQLException e) {
      return this.connection.checkException(e);
   }

   public String toString() {
      return this.getClass().getSimpleName() + '@' + System.identityHashCode(this) + " wrapping " + this.delegate;
   }

   public final Statement getStatement() throws SQLException {
      return this.statement;
   }

   public void updateRow() throws SQLException {
      this.connection.markCommitStateDirty();
      this.delegate.updateRow();
   }

   public void insertRow() throws SQLException {
      this.connection.markCommitStateDirty();
      this.delegate.insertRow();
   }

   public void deleteRow() throws SQLException {
      this.connection.markCommitStateDirty();
      this.delegate.deleteRow();
   }

   public final <T> T unwrap(Class<T> iface) throws SQLException {
      if (iface.isInstance(this.delegate)) {
         return this.delegate;
      } else if (this.delegate != null) {
         return this.delegate.unwrap(iface);
      } else {
         throw new SQLException("Wrapped ResultSet is not an instance of " + iface);
      }
   }
}
