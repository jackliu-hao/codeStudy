/*     */ package com.zaxxer.hikari.pool;
/*     */ 
/*     */ import java.sql.Connection;
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class ProxyDatabaseMetaData
/*     */   implements DatabaseMetaData
/*     */ {
/*     */   protected final ProxyConnection connection;
/*     */   protected final DatabaseMetaData delegate;
/*     */   
/*     */   ProxyDatabaseMetaData(ProxyConnection connection, DatabaseMetaData metaData) {
/*  18 */     this.connection = connection;
/*  19 */     this.delegate = metaData;
/*     */   }
/*     */ 
/*     */   
/*     */   final SQLException checkException(SQLException e) {
/*  24 */     return this.connection.checkException(e);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String toString() {
/*  31 */     String delegateToString = this.delegate.toString();
/*  32 */     return getClass().getSimpleName() + '@' + System.identityHashCode(this) + " wrapping " + delegateToString;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final Connection getConnection() {
/*  43 */     return this.connection;
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
/*  48 */     ResultSet resultSet = this.delegate.getProcedures(catalog, schemaPattern, procedureNamePattern);
/*  49 */     Statement statement = resultSet.getStatement();
/*  50 */     if (statement != null) {
/*  51 */       statement = ProxyFactory.getProxyStatement(this.connection, statement);
/*     */     }
/*  53 */     return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) throws SQLException {
/*  58 */     ResultSet resultSet = this.delegate.getProcedureColumns(catalog, schemaPattern, procedureNamePattern, columnNamePattern);
/*  59 */     Statement statement = resultSet.getStatement();
/*  60 */     if (statement != null) {
/*  61 */       statement = ProxyFactory.getProxyStatement(this.connection, statement);
/*     */     }
/*  63 */     return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
/*  68 */     ResultSet resultSet = this.delegate.getTables(catalog, schemaPattern, tableNamePattern, types);
/*  69 */     Statement statement = resultSet.getStatement();
/*  70 */     if (statement != null) {
/*  71 */       statement = ProxyFactory.getProxyStatement(this.connection, statement);
/*     */     }
/*  73 */     return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultSet getSchemas() throws SQLException {
/*  78 */     ResultSet resultSet = this.delegate.getSchemas();
/*  79 */     Statement statement = resultSet.getStatement();
/*  80 */     if (statement != null) {
/*  81 */       statement = ProxyFactory.getProxyStatement(this.connection, statement);
/*     */     }
/*  83 */     return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultSet getCatalogs() throws SQLException {
/*  88 */     ResultSet resultSet = this.delegate.getCatalogs();
/*  89 */     Statement statement = resultSet.getStatement();
/*  90 */     if (statement != null) {
/*  91 */       statement = ProxyFactory.getProxyStatement(this.connection, statement);
/*     */     }
/*  93 */     return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultSet getTableTypes() throws SQLException {
/*  98 */     ResultSet resultSet = this.delegate.getTableTypes();
/*  99 */     Statement statement = resultSet.getStatement();
/* 100 */     if (statement != null) {
/* 101 */       statement = ProxyFactory.getProxyStatement(this.connection, statement);
/*     */     }
/* 103 */     return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
/* 108 */     ResultSet resultSet = this.delegate.getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern);
/* 109 */     Statement statement = resultSet.getStatement();
/* 110 */     if (statement != null) {
/* 111 */       statement = ProxyFactory.getProxyStatement(this.connection, statement);
/*     */     }
/* 113 */     return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException {
/* 118 */     ResultSet resultSet = this.delegate.getColumnPrivileges(catalog, schema, table, columnNamePattern);
/* 119 */     Statement statement = resultSet.getStatement();
/* 120 */     if (statement != null) {
/* 121 */       statement = ProxyFactory.getProxyStatement(this.connection, statement);
/*     */     }
/* 123 */     return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
/* 128 */     ResultSet resultSet = this.delegate.getTablePrivileges(catalog, schemaPattern, tableNamePattern);
/* 129 */     Statement statement = resultSet.getStatement();
/* 130 */     if (statement != null) {
/* 131 */       statement = ProxyFactory.getProxyStatement(this.connection, statement);
/*     */     }
/* 133 */     return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable) throws SQLException {
/* 138 */     ResultSet resultSet = this.delegate.getBestRowIdentifier(catalog, schema, table, scope, nullable);
/* 139 */     Statement statement = resultSet.getStatement();
/* 140 */     if (statement != null) {
/* 141 */       statement = ProxyFactory.getProxyStatement(this.connection, statement);
/*     */     }
/* 143 */     return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException {
/* 148 */     ResultSet resultSet = this.delegate.getVersionColumns(catalog, schema, table);
/* 149 */     Statement statement = resultSet.getStatement();
/* 150 */     if (statement != null) {
/* 151 */       statement = ProxyFactory.getProxyStatement(this.connection, statement);
/*     */     }
/* 153 */     return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
/* 158 */     ResultSet resultSet = this.delegate.getPrimaryKeys(catalog, schema, table);
/* 159 */     Statement statement = resultSet.getStatement();
/* 160 */     if (statement != null) {
/* 161 */       statement = ProxyFactory.getProxyStatement(this.connection, statement);
/*     */     }
/* 163 */     return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException {
/* 168 */     ResultSet resultSet = this.delegate.getImportedKeys(catalog, schema, table);
/* 169 */     Statement statement = resultSet.getStatement();
/* 170 */     if (statement != null) {
/* 171 */       statement = ProxyFactory.getProxyStatement(this.connection, statement);
/*     */     }
/* 173 */     return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultSet getExportedKeys(String catalog, String schema, String table) throws SQLException {
/* 178 */     ResultSet resultSet = this.delegate.getExportedKeys(catalog, schema, table);
/* 179 */     Statement statement = resultSet.getStatement();
/* 180 */     if (statement != null) {
/* 181 */       statement = ProxyFactory.getProxyStatement(this.connection, statement);
/*     */     }
/* 183 */     return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultSet getCrossReference(String parentCatalog, String parentSchema, String parentTable, String foreignCatalog, String foreignSchema, String foreignTable) throws SQLException {
/* 188 */     ResultSet resultSet = this.delegate.getCrossReference(parentCatalog, parentSchema, parentTable, foreignCatalog, foreignSchema, foreignTable);
/* 189 */     Statement statement = resultSet.getStatement();
/* 190 */     if (statement != null) {
/* 191 */       statement = ProxyFactory.getProxyStatement(this.connection, statement);
/*     */     }
/* 193 */     return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultSet getTypeInfo() throws SQLException {
/* 198 */     ResultSet resultSet = this.delegate.getTypeInfo();
/* 199 */     Statement statement = resultSet.getStatement();
/* 200 */     if (statement != null) {
/* 201 */       statement = ProxyFactory.getProxyStatement(this.connection, statement);
/*     */     }
/* 203 */     return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException {
/* 208 */     ResultSet resultSet = this.delegate.getIndexInfo(catalog, schema, table, unique, approximate);
/* 209 */     Statement statement = resultSet.getStatement();
/* 210 */     if (statement != null) {
/* 211 */       statement = ProxyFactory.getProxyStatement(this.connection, statement);
/*     */     }
/* 213 */     return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types) throws SQLException {
/* 218 */     ResultSet resultSet = this.delegate.getUDTs(catalog, schemaPattern, typeNamePattern, types);
/* 219 */     Statement statement = resultSet.getStatement();
/* 220 */     if (statement != null) {
/* 221 */       statement = ProxyFactory.getProxyStatement(this.connection, statement);
/*     */     }
/* 223 */     return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern) throws SQLException {
/* 228 */     ResultSet resultSet = this.delegate.getSuperTypes(catalog, schemaPattern, typeNamePattern);
/* 229 */     Statement statement = resultSet.getStatement();
/* 230 */     if (statement != null) {
/* 231 */       statement = ProxyFactory.getProxyStatement(this.connection, statement);
/*     */     }
/* 233 */     return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
/* 238 */     ResultSet resultSet = this.delegate.getSuperTables(catalog, schemaPattern, tableNamePattern);
/* 239 */     Statement statement = resultSet.getStatement();
/* 240 */     if (statement != null) {
/* 241 */       statement = ProxyFactory.getProxyStatement(this.connection, statement);
/*     */     }
/* 243 */     return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultSet getAttributes(String catalog, String schemaPattern, String typeNamePattern, String attributeNamePattern) throws SQLException {
/* 248 */     ResultSet resultSet = this.delegate.getAttributes(catalog, schemaPattern, typeNamePattern, attributeNamePattern);
/* 249 */     Statement statement = resultSet.getStatement();
/* 250 */     if (statement != null) {
/* 251 */       statement = ProxyFactory.getProxyStatement(this.connection, statement);
/*     */     }
/* 253 */     return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
/* 258 */     ResultSet resultSet = this.delegate.getSchemas(catalog, schemaPattern);
/* 259 */     Statement statement = resultSet.getStatement();
/* 260 */     if (statement != null) {
/* 261 */       statement = ProxyFactory.getProxyStatement(this.connection, statement);
/*     */     }
/* 263 */     return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultSet getClientInfoProperties() throws SQLException {
/* 268 */     ResultSet resultSet = this.delegate.getClientInfoProperties();
/* 269 */     Statement statement = resultSet.getStatement();
/* 270 */     if (statement != null) {
/* 271 */       statement = ProxyFactory.getProxyStatement(this.connection, statement);
/*     */     }
/* 273 */     return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern) throws SQLException {
/* 278 */     ResultSet resultSet = this.delegate.getFunctions(catalog, schemaPattern, functionNamePattern);
/* 279 */     Statement statement = resultSet.getStatement();
/* 280 */     if (statement != null) {
/* 281 */       statement = ProxyFactory.getProxyStatement(this.connection, statement);
/*     */     }
/* 283 */     return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern) throws SQLException {
/* 288 */     ResultSet resultSet = this.delegate.getFunctionColumns(catalog, schemaPattern, functionNamePattern, columnNamePattern);
/* 289 */     Statement statement = resultSet.getStatement();
/* 290 */     if (statement != null) {
/* 291 */       statement = ProxyFactory.getProxyStatement(this.connection, statement);
/*     */     }
/* 293 */     return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultSet getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
/* 298 */     ResultSet resultSet = this.delegate.getPseudoColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern);
/* 299 */     Statement statement = resultSet.getStatement();
/* 300 */     if (statement != null) {
/* 301 */       statement = ProxyFactory.getProxyStatement(this.connection, statement);
/*     */     }
/* 303 */     return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)statement, resultSet);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final <T> T unwrap(Class<T> iface) throws SQLException {
/* 311 */     if (iface.isInstance(this.delegate)) {
/* 312 */       return (T)this.delegate;
/*     */     }
/* 314 */     if (this.delegate != null) {
/* 315 */       return this.delegate.unwrap(iface);
/*     */     }
/*     */     
/* 318 */     throw new SQLException("Wrapped DatabaseMetaData is not an instance of " + iface);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\zaxxer\hikari\pool\ProxyDatabaseMetaData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */