package com.zaxxer.hikari.pool;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class ProxyDatabaseMetaData implements DatabaseMetaData {
   protected final ProxyConnection connection;
   protected final DatabaseMetaData delegate;

   ProxyDatabaseMetaData(ProxyConnection connection, DatabaseMetaData metaData) {
      this.connection = connection;
      this.delegate = metaData;
   }

   final SQLException checkException(SQLException e) {
      return this.connection.checkException(e);
   }

   public final String toString() {
      String delegateToString = this.delegate.toString();
      return this.getClass().getSimpleName() + '@' + System.identityHashCode(this) + " wrapping " + delegateToString;
   }

   public final Connection getConnection() {
      return this.connection;
   }

   public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
      ResultSet resultSet = this.delegate.getProcedures(catalog, schemaPattern, procedureNamePattern);
      Statement statement = resultSet.getStatement();
      if (statement != null) {
         statement = ProxyFactory.getProxyStatement(this.connection, statement);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
   }

   public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) throws SQLException {
      ResultSet resultSet = this.delegate.getProcedureColumns(catalog, schemaPattern, procedureNamePattern, columnNamePattern);
      Statement statement = resultSet.getStatement();
      if (statement != null) {
         statement = ProxyFactory.getProxyStatement(this.connection, statement);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
   }

   public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
      ResultSet resultSet = this.delegate.getTables(catalog, schemaPattern, tableNamePattern, types);
      Statement statement = resultSet.getStatement();
      if (statement != null) {
         statement = ProxyFactory.getProxyStatement(this.connection, statement);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
   }

   public ResultSet getSchemas() throws SQLException {
      ResultSet resultSet = this.delegate.getSchemas();
      Statement statement = resultSet.getStatement();
      if (statement != null) {
         statement = ProxyFactory.getProxyStatement(this.connection, statement);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
   }

   public ResultSet getCatalogs() throws SQLException {
      ResultSet resultSet = this.delegate.getCatalogs();
      Statement statement = resultSet.getStatement();
      if (statement != null) {
         statement = ProxyFactory.getProxyStatement(this.connection, statement);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
   }

   public ResultSet getTableTypes() throws SQLException {
      ResultSet resultSet = this.delegate.getTableTypes();
      Statement statement = resultSet.getStatement();
      if (statement != null) {
         statement = ProxyFactory.getProxyStatement(this.connection, statement);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
   }

   public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
      ResultSet resultSet = this.delegate.getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern);
      Statement statement = resultSet.getStatement();
      if (statement != null) {
         statement = ProxyFactory.getProxyStatement(this.connection, statement);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
   }

   public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException {
      ResultSet resultSet = this.delegate.getColumnPrivileges(catalog, schema, table, columnNamePattern);
      Statement statement = resultSet.getStatement();
      if (statement != null) {
         statement = ProxyFactory.getProxyStatement(this.connection, statement);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
   }

   public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
      ResultSet resultSet = this.delegate.getTablePrivileges(catalog, schemaPattern, tableNamePattern);
      Statement statement = resultSet.getStatement();
      if (statement != null) {
         statement = ProxyFactory.getProxyStatement(this.connection, statement);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
   }

   public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable) throws SQLException {
      ResultSet resultSet = this.delegate.getBestRowIdentifier(catalog, schema, table, scope, nullable);
      Statement statement = resultSet.getStatement();
      if (statement != null) {
         statement = ProxyFactory.getProxyStatement(this.connection, statement);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
   }

   public ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException {
      ResultSet resultSet = this.delegate.getVersionColumns(catalog, schema, table);
      Statement statement = resultSet.getStatement();
      if (statement != null) {
         statement = ProxyFactory.getProxyStatement(this.connection, statement);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
   }

   public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
      ResultSet resultSet = this.delegate.getPrimaryKeys(catalog, schema, table);
      Statement statement = resultSet.getStatement();
      if (statement != null) {
         statement = ProxyFactory.getProxyStatement(this.connection, statement);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
   }

   public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException {
      ResultSet resultSet = this.delegate.getImportedKeys(catalog, schema, table);
      Statement statement = resultSet.getStatement();
      if (statement != null) {
         statement = ProxyFactory.getProxyStatement(this.connection, statement);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
   }

   public ResultSet getExportedKeys(String catalog, String schema, String table) throws SQLException {
      ResultSet resultSet = this.delegate.getExportedKeys(catalog, schema, table);
      Statement statement = resultSet.getStatement();
      if (statement != null) {
         statement = ProxyFactory.getProxyStatement(this.connection, statement);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
   }

   public ResultSet getCrossReference(String parentCatalog, String parentSchema, String parentTable, String foreignCatalog, String foreignSchema, String foreignTable) throws SQLException {
      ResultSet resultSet = this.delegate.getCrossReference(parentCatalog, parentSchema, parentTable, foreignCatalog, foreignSchema, foreignTable);
      Statement statement = resultSet.getStatement();
      if (statement != null) {
         statement = ProxyFactory.getProxyStatement(this.connection, statement);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
   }

   public ResultSet getTypeInfo() throws SQLException {
      ResultSet resultSet = this.delegate.getTypeInfo();
      Statement statement = resultSet.getStatement();
      if (statement != null) {
         statement = ProxyFactory.getProxyStatement(this.connection, statement);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
   }

   public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException {
      ResultSet resultSet = this.delegate.getIndexInfo(catalog, schema, table, unique, approximate);
      Statement statement = resultSet.getStatement();
      if (statement != null) {
         statement = ProxyFactory.getProxyStatement(this.connection, statement);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
   }

   public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types) throws SQLException {
      ResultSet resultSet = this.delegate.getUDTs(catalog, schemaPattern, typeNamePattern, types);
      Statement statement = resultSet.getStatement();
      if (statement != null) {
         statement = ProxyFactory.getProxyStatement(this.connection, statement);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
   }

   public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern) throws SQLException {
      ResultSet resultSet = this.delegate.getSuperTypes(catalog, schemaPattern, typeNamePattern);
      Statement statement = resultSet.getStatement();
      if (statement != null) {
         statement = ProxyFactory.getProxyStatement(this.connection, statement);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
   }

   public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
      ResultSet resultSet = this.delegate.getSuperTables(catalog, schemaPattern, tableNamePattern);
      Statement statement = resultSet.getStatement();
      if (statement != null) {
         statement = ProxyFactory.getProxyStatement(this.connection, statement);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
   }

   public ResultSet getAttributes(String catalog, String schemaPattern, String typeNamePattern, String attributeNamePattern) throws SQLException {
      ResultSet resultSet = this.delegate.getAttributes(catalog, schemaPattern, typeNamePattern, attributeNamePattern);
      Statement statement = resultSet.getStatement();
      if (statement != null) {
         statement = ProxyFactory.getProxyStatement(this.connection, statement);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
   }

   public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
      ResultSet resultSet = this.delegate.getSchemas(catalog, schemaPattern);
      Statement statement = resultSet.getStatement();
      if (statement != null) {
         statement = ProxyFactory.getProxyStatement(this.connection, statement);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
   }

   public ResultSet getClientInfoProperties() throws SQLException {
      ResultSet resultSet = this.delegate.getClientInfoProperties();
      Statement statement = resultSet.getStatement();
      if (statement != null) {
         statement = ProxyFactory.getProxyStatement(this.connection, statement);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
   }

   public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern) throws SQLException {
      ResultSet resultSet = this.delegate.getFunctions(catalog, schemaPattern, functionNamePattern);
      Statement statement = resultSet.getStatement();
      if (statement != null) {
         statement = ProxyFactory.getProxyStatement(this.connection, statement);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
   }

   public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern) throws SQLException {
      ResultSet resultSet = this.delegate.getFunctionColumns(catalog, schemaPattern, functionNamePattern, columnNamePattern);
      Statement statement = resultSet.getStatement();
      if (statement != null) {
         statement = ProxyFactory.getProxyStatement(this.connection, statement);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
   }

   public ResultSet getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
      ResultSet resultSet = this.delegate.getPseudoColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern);
      Statement statement = resultSet.getStatement();
      if (statement != null) {
         statement = ProxyFactory.getProxyStatement(this.connection, statement);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
   }

   public final <T> T unwrap(Class<T> iface) throws SQLException {
      if (iface.isInstance(this.delegate)) {
         return this.delegate;
      } else if (this.delegate != null) {
         return this.delegate.unwrap(iface);
      } else {
         throw new SQLException("Wrapped DatabaseMetaData is not an instance of " + iface);
      }
   }
}
