/*     */ package cn.hutool.db.ds.pooled;
/*     */ 
/*     */ import java.sql.Array;
/*     */ import java.sql.Blob;
/*     */ import java.sql.CallableStatement;
/*     */ import java.sql.Clob;
/*     */ import java.sql.Connection;
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.NClob;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.SQLClientInfoException;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLWarning;
/*     */ import java.sql.SQLXML;
/*     */ import java.sql.Savepoint;
/*     */ import java.sql.Statement;
/*     */ import java.sql.Struct;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.concurrent.Executor;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class ConnectionWraper
/*     */   implements Connection
/*     */ {
/*     */   protected Connection raw;
/*     */   
/*     */   public <T> T unwrap(Class<T> iface) throws SQLException {
/*  33 */     return this.raw.unwrap(iface);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isWrapperFor(Class<?> iface) throws SQLException {
/*  38 */     return this.raw.isWrapperFor(iface);
/*     */   }
/*     */ 
/*     */   
/*     */   public Statement createStatement() throws SQLException {
/*  43 */     return this.raw.createStatement();
/*     */   }
/*     */ 
/*     */   
/*     */   public PreparedStatement prepareStatement(String sql) throws SQLException {
/*  48 */     return this.raw.prepareStatement(sql);
/*     */   }
/*     */ 
/*     */   
/*     */   public CallableStatement prepareCall(String sql) throws SQLException {
/*  53 */     return this.raw.prepareCall(sql);
/*     */   }
/*     */ 
/*     */   
/*     */   public String nativeSQL(String sql) throws SQLException {
/*  58 */     return this.raw.nativeSQL(sql);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAutoCommit(boolean autoCommit) throws SQLException {
/*  63 */     this.raw.setAutoCommit(autoCommit);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getAutoCommit() throws SQLException {
/*  68 */     return this.raw.getAutoCommit();
/*     */   }
/*     */ 
/*     */   
/*     */   public void commit() throws SQLException {
/*  73 */     this.raw.commit();
/*     */   }
/*     */ 
/*     */   
/*     */   public void rollback() throws SQLException {
/*  78 */     this.raw.rollback();
/*     */   }
/*     */ 
/*     */   
/*     */   public DatabaseMetaData getMetaData() throws SQLException {
/*  83 */     return this.raw.getMetaData();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setReadOnly(boolean readOnly) throws SQLException {
/*  88 */     this.raw.setReadOnly(readOnly);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isReadOnly() throws SQLException {
/*  93 */     return this.raw.isReadOnly();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCatalog(String catalog) throws SQLException {
/*  98 */     this.raw.setCatalog(catalog);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCatalog() throws SQLException {
/* 103 */     return this.raw.getCatalog();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setTransactionIsolation(int level) throws SQLException {
/* 108 */     this.raw.setTransactionIsolation(level);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getTransactionIsolation() throws SQLException {
/* 113 */     return this.raw.getTransactionIsolation();
/*     */   }
/*     */ 
/*     */   
/*     */   public SQLWarning getWarnings() throws SQLException {
/* 118 */     return this.raw.getWarnings();
/*     */   }
/*     */ 
/*     */   
/*     */   public void clearWarnings() throws SQLException {
/* 123 */     this.raw.clearWarnings();
/*     */   }
/*     */ 
/*     */   
/*     */   public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
/* 128 */     return this.raw.createStatement(resultSetType, resultSetConcurrency);
/*     */   }
/*     */ 
/*     */   
/*     */   public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
/* 133 */     return this.raw.prepareStatement(sql, resultSetType, resultSetConcurrency);
/*     */   }
/*     */ 
/*     */   
/*     */   public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
/* 138 */     return this.raw.prepareCall(sql, resultSetType, resultSetConcurrency);
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<String, Class<?>> getTypeMap() throws SQLException {
/* 143 */     return this.raw.getTypeMap();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
/* 148 */     this.raw.setTypeMap(map);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setHoldability(int holdability) throws SQLException {
/* 153 */     this.raw.setHoldability(holdability);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getHoldability() throws SQLException {
/* 158 */     return this.raw.getHoldability();
/*     */   }
/*     */ 
/*     */   
/*     */   public Savepoint setSavepoint() throws SQLException {
/* 163 */     return this.raw.setSavepoint();
/*     */   }
/*     */ 
/*     */   
/*     */   public Savepoint setSavepoint(String name) throws SQLException {
/* 168 */     return this.raw.setSavepoint(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public void rollback(Savepoint savepoint) throws SQLException {
/* 173 */     this.raw.rollback(savepoint);
/*     */   }
/*     */ 
/*     */   
/*     */   public void releaseSavepoint(Savepoint savepoint) throws SQLException {
/* 178 */     this.raw.releaseSavepoint(savepoint);
/*     */   }
/*     */ 
/*     */   
/*     */   public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
/* 183 */     return this.raw.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
/*     */   }
/*     */ 
/*     */   
/*     */   public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
/* 188 */     return this.raw.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
/*     */   }
/*     */ 
/*     */   
/*     */   public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
/* 193 */     return this.raw.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
/*     */   }
/*     */ 
/*     */   
/*     */   public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
/* 198 */     return this.raw.prepareStatement(sql, autoGeneratedKeys);
/*     */   }
/*     */ 
/*     */   
/*     */   public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
/* 203 */     return this.raw.prepareStatement(sql, columnIndexes);
/*     */   }
/*     */ 
/*     */   
/*     */   public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
/* 208 */     return this.raw.prepareStatement(sql, columnNames);
/*     */   }
/*     */ 
/*     */   
/*     */   public Clob createClob() throws SQLException {
/* 213 */     return this.raw.createClob();
/*     */   }
/*     */ 
/*     */   
/*     */   public Blob createBlob() throws SQLException {
/* 218 */     return this.raw.createBlob();
/*     */   }
/*     */ 
/*     */   
/*     */   public NClob createNClob() throws SQLException {
/* 223 */     return this.raw.createNClob();
/*     */   }
/*     */ 
/*     */   
/*     */   public SQLXML createSQLXML() throws SQLException {
/* 228 */     return this.raw.createSQLXML();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isValid(int timeout) throws SQLException {
/* 233 */     return this.raw.isValid(timeout);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setClientInfo(String name, String value) throws SQLClientInfoException {
/* 238 */     this.raw.setClientInfo(name, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setClientInfo(Properties properties) throws SQLClientInfoException {
/* 243 */     this.raw.setClientInfo(properties);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getClientInfo(String name) throws SQLException {
/* 248 */     return this.raw.getClientInfo(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public Properties getClientInfo() throws SQLException {
/* 253 */     return this.raw.getClientInfo();
/*     */   }
/*     */ 
/*     */   
/*     */   public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
/* 258 */     return this.raw.createArrayOf(typeName, elements);
/*     */   }
/*     */ 
/*     */   
/*     */   public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
/* 263 */     return this.raw.createStruct(typeName, attributes);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSchema(String schema) throws SQLException {
/* 268 */     this.raw.setSchema(schema);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSchema() throws SQLException {
/* 273 */     return this.raw.getSchema();
/*     */   }
/*     */ 
/*     */   
/*     */   public void abort(Executor executor) throws SQLException {
/* 278 */     this.raw.abort(executor);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
/* 283 */     this.raw.setNetworkTimeout(executor, milliseconds);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getNetworkTimeout() throws SQLException {
/* 288 */     return this.raw.getNetworkTimeout();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Connection getRaw() {
/* 295 */     return this.raw;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\ds\pooled\ConnectionWraper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */