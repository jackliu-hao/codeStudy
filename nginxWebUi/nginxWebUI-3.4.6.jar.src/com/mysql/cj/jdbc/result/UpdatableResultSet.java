/*      */ package com.mysql.cj.jdbc.result;
/*      */ 
/*      */ import com.mysql.cj.Messages;
/*      */ import com.mysql.cj.MysqlType;
/*      */ import com.mysql.cj.PreparedQuery;
/*      */ import com.mysql.cj.Query;
/*      */ import com.mysql.cj.Session;
/*      */ import com.mysql.cj.conf.PropertyDefinitions;
/*      */ import com.mysql.cj.conf.PropertyKey;
/*      */ import com.mysql.cj.exceptions.AssertionFailedException;
/*      */ import com.mysql.cj.exceptions.CJException;
/*      */ import com.mysql.cj.exceptions.ExceptionFactory;
/*      */ import com.mysql.cj.exceptions.FeatureNotAvailableException;
/*      */ import com.mysql.cj.jdbc.ClientPreparedStatement;
/*      */ import com.mysql.cj.jdbc.JdbcConnection;
/*      */ import com.mysql.cj.jdbc.MysqlSQLXML;
/*      */ import com.mysql.cj.jdbc.NClob;
/*      */ import com.mysql.cj.jdbc.StatementImpl;
/*      */ import com.mysql.cj.jdbc.exceptions.NotUpdatable;
/*      */ import com.mysql.cj.jdbc.exceptions.SQLError;
/*      */ import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
/*      */ import com.mysql.cj.protocol.ResultsetRow;
/*      */ import com.mysql.cj.protocol.ResultsetRows;
/*      */ import com.mysql.cj.protocol.a.result.ByteArrayRow;
/*      */ import com.mysql.cj.result.Field;
/*      */ import com.mysql.cj.result.Row;
/*      */ import com.mysql.cj.util.StringUtils;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.math.BigDecimal;
/*      */ import java.sql.Blob;
/*      */ import java.sql.Clob;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.Date;
/*      */ import java.sql.JDBCType;
/*      */ import java.sql.NClob;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLType;
/*      */ import java.sql.SQLXML;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.time.LocalDateTime;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.TreeMap;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class UpdatableResultSet
/*      */   extends ResultSetImpl
/*      */ {
/*   78 */   static final byte[] STREAM_DATA_MARKER = StringUtils.getBytes("** STREAM DATA **");
/*      */ 
/*      */   
/*      */   private String charEncoding;
/*      */ 
/*      */   
/*      */   private byte[][] defaultColumnValue;
/*      */   
/*   86 */   private ClientPreparedStatement deleter = null;
/*      */   
/*   88 */   private String deleteSQL = null;
/*      */ 
/*      */   
/*   91 */   protected ClientPreparedStatement inserter = null;
/*      */   
/*   93 */   private String insertSQL = null;
/*      */ 
/*      */   
/*      */   private boolean isUpdatable = false;
/*      */ 
/*      */   
/*   99 */   private String notUpdatableReason = null;
/*      */ 
/*      */   
/*  102 */   private List<Integer> primaryKeyIndicies = null;
/*      */   
/*      */   private String qualifiedAndQuotedTableName;
/*      */   
/*  106 */   private String quotedIdChar = null;
/*      */ 
/*      */   
/*      */   private ClientPreparedStatement refresher;
/*      */   
/*  111 */   private String refreshSQL = null;
/*      */ 
/*      */   
/*      */   private Row savedCurrentRow;
/*      */ 
/*      */   
/*  117 */   protected ClientPreparedStatement updater = null;
/*      */ 
/*      */   
/*  120 */   private String updateSQL = null;
/*      */   
/*      */   private boolean populateInserterWithDefaultValues = false;
/*      */   
/*      */   private boolean pedantic;
/*      */   
/*      */   private boolean hasLongColumnInfo = false;
/*  127 */   private Map<String, Map<String, Map<String, Integer>>> databasesUsedToTablesUsed = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean onInsertRow = false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean doingUpdates = false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public UpdatableResultSet(ResultsetRows tuples, JdbcConnection conn, StatementImpl creatorStmt) throws SQLException {
/*  149 */     super(tuples, conn, creatorStmt);
/*  150 */     checkUpdatability();
/*      */     
/*  152 */     this.charEncoding = (String)this.session.getPropertySet().getStringProperty(PropertyKey.characterEncoding).getValue();
/*  153 */     this
/*  154 */       .populateInserterWithDefaultValues = ((Boolean)getSession().getPropertySet().getBooleanProperty(PropertyKey.populateInsertRowWithDefaultValues).getValue()).booleanValue();
/*  155 */     this.pedantic = ((Boolean)getSession().getPropertySet().getBooleanProperty(PropertyKey.pedantic).getValue()).booleanValue();
/*  156 */     this.hasLongColumnInfo = getSession().getServerSession().hasLongColumnInfo();
/*      */   }
/*      */   
/*      */   public boolean absolute(int row) throws SQLException {
/*      */     
/*  161 */     try { boolean ret = super.absolute(row);
/*  162 */       if (this.onInsertRow) {
/*  163 */         this.onInsertRow = false;
/*      */       }
/*  165 */       if (this.doingUpdates) {
/*  166 */         this.doingUpdates = false;
/*      */       }
/*  168 */       return ret; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void afterLast() throws SQLException {
/*      */     try {
/*  173 */       super.afterLast();
/*  174 */       if (this.onInsertRow) {
/*  175 */         this.onInsertRow = false;
/*      */       }
/*  177 */       if (this.doingUpdates)
/*  178 */         this.doingUpdates = false;  return;
/*      */     } catch (CJException cJException) {
/*  180 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public void beforeFirst() throws SQLException {
/*      */     try {
/*  184 */       super.beforeFirst();
/*  185 */       if (this.onInsertRow) {
/*  186 */         this.onInsertRow = false;
/*      */       }
/*  188 */       if (this.doingUpdates)
/*  189 */         this.doingUpdates = false;  return;
/*      */     } catch (CJException cJException) {
/*  191 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public void cancelRowUpdates() throws SQLException {
/*      */     
/*  195 */     try { if (this.doingUpdates) {
/*  196 */         this.doingUpdates = false;
/*  197 */         this.updater.clearParameters();
/*      */       }  return; }
/*  199 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   protected void checkRowPos() throws SQLException {
/*  203 */     if (!this.onInsertRow) {
/*  204 */       super.checkRowPos();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void checkUpdatability() throws SQLException {
/*      */     try {
/*  216 */       if (getMetadata() == null) {
/*      */         return;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  223 */       String singleTableName = null;
/*  224 */       String dbName = null;
/*      */       
/*  226 */       int primaryKeyCount = 0;
/*      */       
/*  228 */       Field[] fields = getMetadata().getFields();
/*      */ 
/*      */       
/*  231 */       if (this.db == null || this.db.length() == 0) {
/*  232 */         this.db = fields[0].getDatabaseName();
/*      */         
/*  234 */         if (this.db == null || this.db.length() == 0) {
/*  235 */           throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.43"), "S1009", 
/*  236 */               getExceptionInterceptor());
/*      */         }
/*      */       } 
/*      */       
/*  240 */       if (fields.length > 0) {
/*  241 */         singleTableName = fields[0].getOriginalTableName();
/*  242 */         dbName = fields[0].getDatabaseName();
/*      */         
/*  244 */         if (singleTableName == null) {
/*  245 */           singleTableName = fields[0].getTableName();
/*  246 */           dbName = this.db;
/*      */         } 
/*      */         
/*  249 */         if (singleTableName == null) {
/*  250 */           this.isUpdatable = false;
/*  251 */           this.notUpdatableReason = Messages.getString("NotUpdatableReason.3");
/*      */           
/*      */           return;
/*      */         } 
/*      */         
/*  256 */         if (fields[0].isPrimaryKey()) {
/*  257 */           primaryKeyCount++;
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  263 */         for (int i = 1; i < fields.length; i++) {
/*  264 */           String otherTableName = fields[i].getOriginalTableName();
/*  265 */           String otherDbName = fields[i].getDatabaseName();
/*      */           
/*  267 */           if (otherTableName == null) {
/*  268 */             otherTableName = fields[i].getTableName();
/*  269 */             otherDbName = this.db;
/*      */           } 
/*      */           
/*  272 */           if (otherTableName == null) {
/*  273 */             this.isUpdatable = false;
/*  274 */             this.notUpdatableReason = Messages.getString("NotUpdatableReason.3");
/*      */             
/*      */             return;
/*      */           } 
/*      */           
/*  279 */           if (!otherTableName.equals(singleTableName)) {
/*  280 */             this.isUpdatable = false;
/*  281 */             this.notUpdatableReason = Messages.getString("NotUpdatableReason.0");
/*      */ 
/*      */             
/*      */             return;
/*      */           } 
/*      */           
/*  287 */           if (dbName == null || !dbName.equals(otherDbName)) {
/*  288 */             this.isUpdatable = false;
/*  289 */             this.notUpdatableReason = Messages.getString("NotUpdatableReason.1");
/*      */             
/*      */             return;
/*      */           } 
/*      */           
/*  294 */           if (fields[i].isPrimaryKey()) {
/*  295 */             primaryKeyCount++;
/*      */           }
/*      */         } 
/*      */       } else {
/*  299 */         this.isUpdatable = false;
/*  300 */         this.notUpdatableReason = Messages.getString("NotUpdatableReason.3");
/*      */         
/*      */         return;
/*      */       } 
/*      */       
/*  305 */       if (((Boolean)getSession().getPropertySet().getBooleanProperty(PropertyKey.strictUpdates).getValue()).booleanValue()) {
/*  306 */         DatabaseMetaData dbmd = getConnection().getMetaData();
/*      */         
/*  308 */         ResultSet rs = null;
/*  309 */         HashMap<String, String> primaryKeyNames = new HashMap<>();
/*      */ 
/*      */ 
/*      */         
/*      */         try {
/*  314 */           rs = (this.session.getPropertySet().getEnumProperty(PropertyKey.databaseTerm).getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? dbmd.getPrimaryKeys(null, dbName, singleTableName) : dbmd.getPrimaryKeys(dbName, null, singleTableName);
/*      */           
/*  316 */           while (rs.next()) {
/*  317 */             String keyName = rs.getString(4);
/*  318 */             keyName = keyName.toUpperCase();
/*  319 */             primaryKeyNames.put(keyName, keyName);
/*      */           } 
/*      */         } finally {
/*  322 */           if (rs != null) {
/*      */             try {
/*  324 */               rs.close();
/*  325 */             } catch (Exception ex) {
/*  326 */               AssertionFailedException.shouldNotHappen(ex);
/*      */             } 
/*      */             
/*  329 */             rs = null;
/*      */           } 
/*      */         } 
/*      */         
/*  333 */         int existingPrimaryKeysCount = primaryKeyNames.size();
/*      */         
/*  335 */         if (existingPrimaryKeysCount == 0) {
/*  336 */           this.isUpdatable = false;
/*  337 */           this.notUpdatableReason = Messages.getString("NotUpdatableReason.5");
/*      */ 
/*      */ 
/*      */           
/*      */           return;
/*      */         } 
/*      */ 
/*      */         
/*  345 */         for (int i = 0; i < fields.length; i++) {
/*  346 */           if (fields[i].isPrimaryKey()) {
/*  347 */             String columnNameUC = fields[i].getName().toUpperCase();
/*      */             
/*  349 */             if (primaryKeyNames.remove(columnNameUC) == null) {
/*      */               
/*  351 */               String originalName = fields[i].getOriginalName();
/*      */               
/*  353 */               if (originalName != null && 
/*  354 */                 primaryKeyNames.remove(originalName.toUpperCase()) == null) {
/*      */                 
/*  356 */                 this.isUpdatable = false;
/*  357 */                 this.notUpdatableReason = Messages.getString("NotUpdatableReason.6", new Object[] { originalName });
/*      */ 
/*      */                 
/*      */                 return;
/*      */               } 
/*      */             } 
/*      */           } 
/*      */         } 
/*      */         
/*  366 */         this.isUpdatable = primaryKeyNames.isEmpty();
/*      */         
/*  368 */         if (!this.isUpdatable) {
/*  369 */           this
/*  370 */             .notUpdatableReason = (existingPrimaryKeysCount > 1) ? Messages.getString("NotUpdatableReason.7") : Messages.getString("NotUpdatableReason.4");
/*      */ 
/*      */           
/*      */           return;
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/*  378 */       if (primaryKeyCount == 0) {
/*  379 */         this.isUpdatable = false;
/*  380 */         this.notUpdatableReason = Messages.getString("NotUpdatableReason.4");
/*      */         
/*      */         return;
/*      */       } 
/*      */       
/*  385 */       this.isUpdatable = true;
/*  386 */       this.notUpdatableReason = null;
/*      */       
/*      */       return;
/*  389 */     } catch (SQLException sqlEx) {
/*  390 */       this.isUpdatable = false;
/*  391 */       this.notUpdatableReason = sqlEx.getMessage();
/*      */       return;
/*      */     } 
/*      */   }
/*      */   public void deleteRow() throws SQLException {
/*      */     
/*  397 */     try { synchronized (checkClosed().getConnectionMutex()) {
/*  398 */         if (!this.isUpdatable) {
/*  399 */           throw new NotUpdatable(this.notUpdatableReason);
/*      */         }
/*      */         
/*  402 */         if (this.onInsertRow)
/*  403 */           throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.1"), getExceptionInterceptor()); 
/*  404 */         if (this.rowData.size() == 0)
/*  405 */           throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.2"), getExceptionInterceptor()); 
/*  406 */         if (isBeforeFirst())
/*  407 */           throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.3"), getExceptionInterceptor()); 
/*  408 */         if (isAfterLast()) {
/*  409 */           throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.4"), getExceptionInterceptor());
/*      */         }
/*      */         
/*  412 */         if (this.deleter == null) {
/*  413 */           if (this.deleteSQL == null) {
/*  414 */             generateStatements();
/*      */           }
/*      */           
/*  417 */           this.deleter = (ClientPreparedStatement)this.connection.clientPrepareStatement(this.deleteSQL);
/*      */         } 
/*      */         
/*  420 */         this.deleter.clearParameters();
/*      */         
/*  422 */         int numKeys = this.primaryKeyIndicies.size();
/*  423 */         for (int i = 0; i < numKeys; i++) {
/*  424 */           int index = ((Integer)this.primaryKeyIndicies.get(i)).intValue();
/*  425 */           setParamValue(this.deleter, i + 1, this.thisRow, index, getMetadata().getFields()[index]);
/*      */         } 
/*      */         
/*  428 */         this.deleter.executeUpdate();
/*  429 */         this.rowData.remove();
/*      */         
/*  431 */         prev();
/*      */       }  return; }
/*  433 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } private void setParamValue(ClientPreparedStatement ps, int psIdx, Row row, int rsIdx, Field field) throws SQLException {
/*  436 */     byte[] val = row.getBytes(rsIdx);
/*  437 */     if (val == null) {
/*  438 */       ps.setNull(psIdx, MysqlType.NULL);
/*      */       return;
/*      */     } 
/*  441 */     switch (field.getMysqlType()) {
/*      */       case NULL:
/*  443 */         ps.setNull(psIdx, MysqlType.NULL);
/*      */         return;
/*      */       case TINYINT:
/*      */       case TINYINT_UNSIGNED:
/*      */       case SMALLINT:
/*      */       case SMALLINT_UNSIGNED:
/*      */       case MEDIUMINT:
/*      */       case MEDIUMINT_UNSIGNED:
/*      */       case INT:
/*      */       case INT_UNSIGNED:
/*      */       case YEAR:
/*  454 */         ps.setInt(psIdx, getInt(rsIdx + 1));
/*      */         return;
/*      */       case BIGINT:
/*  457 */         ps.setLong(psIdx, getLong(rsIdx + 1));
/*      */         return;
/*      */       case BIGINT_UNSIGNED:
/*  460 */         ps.setBigInteger(psIdx, getBigInteger(rsIdx + 1));
/*      */         return;
/*      */       case CHAR:
/*      */       case ENUM:
/*      */       case SET:
/*      */       case VARCHAR:
/*      */       case JSON:
/*      */       case TINYTEXT:
/*      */       case TEXT:
/*      */       case MEDIUMTEXT:
/*      */       case LONGTEXT:
/*      */       case DECIMAL:
/*      */       case DECIMAL_UNSIGNED:
/*  473 */         ps.setString(psIdx, getString(rsIdx + 1));
/*      */         return;
/*      */       case DATE:
/*  476 */         ps.setDate(psIdx, getDate(rsIdx + 1));
/*      */         return;
/*      */       case TIMESTAMP:
/*  479 */         ((PreparedQuery)ps.getQuery()).getQueryBindings().bindTimestamp(ps.getCoreParameterIndex(psIdx), getTimestamp(rsIdx + 1), null, field
/*  480 */             .getDecimals(), MysqlType.TIMESTAMP);
/*      */         return;
/*      */       case DATETIME:
/*  483 */         ps.setObject(psIdx, getObject(rsIdx + 1, LocalDateTime.class), (SQLType)MysqlType.DATETIME, field.getDecimals());
/*      */         return;
/*      */       
/*      */       case TIME:
/*  487 */         ps.setTime(psIdx, getTime(rsIdx + 1));
/*      */         return;
/*      */       case DOUBLE:
/*      */       case DOUBLE_UNSIGNED:
/*      */       case FLOAT:
/*      */       case FLOAT_UNSIGNED:
/*      */       case BOOLEAN:
/*      */       case BIT:
/*  495 */         ps.setBytesNoEscapeNoQuotes(psIdx, val);
/*      */         return;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  503 */     ps.setBytes(psIdx, val);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void extractDefaultValues() throws SQLException {
/*  510 */     DatabaseMetaData dbmd = getConnection().getMetaData();
/*  511 */     this.defaultColumnValue = new byte[(getMetadata().getFields()).length][];
/*      */     
/*  513 */     ResultSet columnsResultSet = null;
/*      */     
/*  515 */     for (Map.Entry<String, Map<String, Map<String, Integer>>> dbEntry : this.databasesUsedToTablesUsed.entrySet()) {
/*  516 */       for (Map.Entry<String, Map<String, Integer>> tableEntry : (Iterable<Map.Entry<String, Map<String, Integer>>>)((Map)dbEntry.getValue()).entrySet()) {
/*  517 */         String tableName = tableEntry.getKey();
/*  518 */         Map<String, Integer> columnNamesToIndices = tableEntry.getValue();
/*      */ 
/*      */ 
/*      */         
/*      */         try {
/*  523 */           columnsResultSet = (this.session.getPropertySet().getEnumProperty(PropertyKey.databaseTerm).getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? dbmd.getColumns(null, this.db, tableName, "%") : dbmd.getColumns(this.db, null, tableName, "%");
/*      */           
/*  525 */           while (columnsResultSet.next()) {
/*  526 */             String columnName = columnsResultSet.getString("COLUMN_NAME");
/*  527 */             byte[] defaultValue = columnsResultSet.getBytes("COLUMN_DEF");
/*      */             
/*  529 */             if (columnNamesToIndices.containsKey(columnName)) {
/*  530 */               int localColumnIndex = ((Integer)columnNamesToIndices.get(columnName)).intValue();
/*  531 */               this.defaultColumnValue[localColumnIndex] = defaultValue;
/*      */             } 
/*      */           } 
/*      */         } finally {
/*  535 */           if (columnsResultSet != null) {
/*  536 */             columnsResultSet.close();
/*  537 */             columnsResultSet = null;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public boolean first() throws SQLException {
/*      */     
/*  546 */     try { boolean ret = super.first();
/*  547 */       if (this.onInsertRow) {
/*  548 */         this.onInsertRow = false;
/*      */       }
/*  550 */       if (this.doingUpdates) {
/*  551 */         this.doingUpdates = false;
/*      */       }
/*  553 */       return ret; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void generateStatements() throws SQLException {
/*      */     
/*  566 */     try { if (!this.isUpdatable) {
/*  567 */         this.doingUpdates = false;
/*  568 */         this.onInsertRow = false;
/*      */         
/*  570 */         throw new NotUpdatable(this.notUpdatableReason);
/*      */       } 
/*      */       
/*  573 */       String quotedId = getQuotedIdChar();
/*      */       
/*  575 */       Map<String, String> tableNamesSoFar = null;
/*      */       
/*  577 */       if (this.session.getServerSession().isLowerCaseTableNames()) {
/*  578 */         tableNamesSoFar = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
/*  579 */         this.databasesUsedToTablesUsed = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
/*      */       } else {
/*  581 */         tableNamesSoFar = new TreeMap<>();
/*  582 */         this.databasesUsedToTablesUsed = new TreeMap<>();
/*      */       } 
/*      */       
/*  585 */       this.primaryKeyIndicies = new ArrayList<>();
/*      */       
/*  587 */       StringBuilder fieldValues = new StringBuilder();
/*  588 */       StringBuilder keyValues = new StringBuilder();
/*  589 */       StringBuilder columnNames = new StringBuilder();
/*  590 */       StringBuilder insertPlaceHolders = new StringBuilder();
/*  591 */       StringBuilder allTablesBuf = new StringBuilder();
/*  592 */       Map<Integer, String> columnIndicesToTable = new HashMap<>();
/*      */       
/*  594 */       Field[] fields = getMetadata().getFields();
/*      */       
/*  596 */       for (int i = 0; i < fields.length; i++) {
/*  597 */         Map<String, Integer> updColumnNameToIndex = null;
/*      */ 
/*      */         
/*  600 */         if (fields[i].getOriginalTableName() != null) {
/*      */           
/*  602 */           String str1 = fields[i].getDatabaseName();
/*  603 */           String tableOnlyName = fields[i].getOriginalTableName();
/*      */           
/*  605 */           String fqTableName = StringUtils.getFullyQualifiedName(str1, tableOnlyName, quotedId, this.pedantic);
/*      */           
/*  607 */           if (!tableNamesSoFar.containsKey(fqTableName)) {
/*  608 */             if (!tableNamesSoFar.isEmpty()) {
/*  609 */               allTablesBuf.append(',');
/*      */             }
/*      */             
/*  612 */             allTablesBuf.append(fqTableName);
/*  613 */             tableNamesSoFar.put(fqTableName, fqTableName);
/*      */           } 
/*      */           
/*  616 */           columnIndicesToTable.put(Integer.valueOf(i), fqTableName);
/*      */           
/*  618 */           updColumnNameToIndex = getColumnsToIndexMapForTableAndDB(str1, tableOnlyName);
/*      */         } else {
/*  620 */           String tableOnlyName = fields[i].getTableName();
/*      */           
/*  622 */           if (tableOnlyName != null) {
/*      */             
/*  624 */             String fqTableName = StringUtils.quoteIdentifier(tableOnlyName, quotedId, this.pedantic);
/*      */             
/*  626 */             if (!tableNamesSoFar.containsKey(fqTableName)) {
/*  627 */               if (!tableNamesSoFar.isEmpty()) {
/*  628 */                 allTablesBuf.append(',');
/*      */               }
/*      */               
/*  631 */               allTablesBuf.append(fqTableName);
/*  632 */               tableNamesSoFar.put(fqTableName, fqTableName);
/*      */             } 
/*      */             
/*  635 */             columnIndicesToTable.put(Integer.valueOf(i), fqTableName);
/*      */             
/*  637 */             updColumnNameToIndex = getColumnsToIndexMapForTableAndDB(this.db, tableOnlyName);
/*      */           } 
/*      */         } 
/*      */         
/*  641 */         String originalColumnName = fields[i].getOriginalName();
/*      */         
/*  643 */         String columnName = (this.hasLongColumnInfo && originalColumnName != null && originalColumnName.length() > 0) ? originalColumnName : fields[i].getName();
/*      */         
/*  645 */         if (updColumnNameToIndex != null && columnName != null) {
/*  646 */           updColumnNameToIndex.put(columnName, Integer.valueOf(i));
/*      */         }
/*      */         
/*  649 */         String originalTableName = fields[i].getOriginalTableName();
/*      */         
/*  651 */         String tableName = (this.hasLongColumnInfo && originalTableName != null && originalTableName.length() > 0) ? originalTableName : fields[i].getTableName();
/*      */         
/*  653 */         String databaseName = fields[i].getDatabaseName();
/*      */ 
/*      */ 
/*      */         
/*  657 */         String qualifiedColumnName = StringUtils.getFullyQualifiedName(databaseName, tableName, quotedId, this.pedantic) + '.' + StringUtils.quoteIdentifier(columnName, quotedId, this.pedantic);
/*      */         
/*  659 */         if (fields[i].isPrimaryKey()) {
/*  660 */           this.primaryKeyIndicies.add(Integer.valueOf(i));
/*      */           
/*  662 */           if (keyValues.length() > 0) {
/*  663 */             keyValues.append(" AND ");
/*      */           }
/*      */           
/*  666 */           keyValues.append(qualifiedColumnName);
/*  667 */           keyValues.append("<=>");
/*  668 */           keyValues.append("?");
/*      */         } 
/*      */         
/*  671 */         if (fieldValues.length() == 0) {
/*  672 */           fieldValues.append("SET ");
/*      */         } else {
/*  674 */           fieldValues.append(",");
/*  675 */           columnNames.append(",");
/*  676 */           insertPlaceHolders.append(",");
/*      */         } 
/*      */         
/*  679 */         insertPlaceHolders.append("?");
/*      */         
/*  681 */         columnNames.append(qualifiedColumnName);
/*      */         
/*  683 */         fieldValues.append(qualifiedColumnName);
/*  684 */         fieldValues.append("=?");
/*      */       } 
/*      */       
/*  687 */       this.qualifiedAndQuotedTableName = allTablesBuf.toString();
/*      */       
/*  689 */       this.updateSQL = "UPDATE " + this.qualifiedAndQuotedTableName + " " + fieldValues.toString() + " WHERE " + keyValues.toString();
/*  690 */       this.insertSQL = "INSERT INTO " + this.qualifiedAndQuotedTableName + " (" + columnNames.toString() + ") VALUES (" + insertPlaceHolders.toString() + ")";
/*  691 */       this.refreshSQL = "SELECT " + columnNames.toString() + " FROM " + this.qualifiedAndQuotedTableName + " WHERE " + keyValues.toString();
/*  692 */       this.deleteSQL = "DELETE FROM " + this.qualifiedAndQuotedTableName + " WHERE " + keyValues.toString(); return; }
/*  693 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   private Map<String, Integer> getColumnsToIndexMapForTableAndDB(String databaseName, String tableName) {
/*  697 */     Map<String, Map<String, Integer>> tablesUsedToColumnsMap = this.databasesUsedToTablesUsed.get(databaseName);
/*      */     
/*  699 */     if (tablesUsedToColumnsMap == null) {
/*  700 */       tablesUsedToColumnsMap = this.session.getServerSession().isLowerCaseTableNames() ? new TreeMap<>(String.CASE_INSENSITIVE_ORDER) : new TreeMap<>();
/*  701 */       this.databasesUsedToTablesUsed.put(databaseName, tablesUsedToColumnsMap);
/*      */     } 
/*      */     
/*  704 */     Map<String, Integer> nameToIndex = tablesUsedToColumnsMap.get(tableName);
/*      */     
/*  706 */     if (nameToIndex == null) {
/*  707 */       nameToIndex = new HashMap<>();
/*  708 */       tablesUsedToColumnsMap.put(tableName, nameToIndex);
/*      */     } 
/*      */     
/*  711 */     return nameToIndex;
/*      */   }
/*      */   
/*      */   public int getConcurrency() throws SQLException {
/*      */     
/*  716 */     try { synchronized (checkClosed().getConnectionMutex())
/*  717 */       { return this.isUpdatable ? 1008 : 1007; }  }
/*  718 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   private String getQuotedIdChar() throws SQLException {
/*  722 */     if (this.quotedIdChar == null) {
/*  723 */       this.quotedIdChar = this.session.getIdentifierQuoteString();
/*      */     }
/*      */     
/*  726 */     return this.quotedIdChar;
/*      */   }
/*      */   
/*      */   public void insertRow() throws SQLException {
/*      */     
/*  731 */     try { synchronized (checkClosed().getConnectionMutex()) {
/*  732 */         if (!this.onInsertRow) {
/*  733 */           throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.7"), getExceptionInterceptor());
/*      */         }
/*      */         
/*  736 */         this.inserter.executeUpdate();
/*      */         
/*  738 */         long autoIncrementId = this.inserter.getLastInsertID();
/*  739 */         Field[] fields = getMetadata().getFields();
/*  740 */         byte[][] newRow = new byte[fields.length][];
/*      */         
/*  742 */         for (int i = 0; i < fields.length; i++) {
/*  743 */           newRow[i] = this.inserter.isNull(i + 1) ? null : this.inserter.getBytesRepresentation(i + 1);
/*      */ 
/*      */           
/*  746 */           if (fields[i].isAutoIncrement() && autoIncrementId > 0L) {
/*  747 */             newRow[i] = StringUtils.getBytes(String.valueOf(autoIncrementId));
/*  748 */             this.inserter.setBytesNoEscapeNoQuotes(i + 1, newRow[i]);
/*      */           } 
/*      */         } 
/*      */         
/*  752 */         ByteArrayRow byteArrayRow = new ByteArrayRow(newRow, getExceptionInterceptor());
/*      */ 
/*      */         
/*  755 */         refreshRow(this.inserter, (Row)byteArrayRow);
/*      */         
/*  757 */         this.rowData.addRow((Row)byteArrayRow);
/*  758 */         resetInserter();
/*      */       }  return; }
/*  760 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public boolean isAfterLast() throws SQLException {
/*      */     
/*  764 */     try { return super.isAfterLast(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean isBeforeFirst() throws SQLException {
/*      */     
/*  769 */     try { return super.isBeforeFirst(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean isFirst() throws SQLException {
/*      */     
/*  774 */     try { return super.isFirst(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean isLast() throws SQLException {
/*      */     
/*  779 */     try { return super.isLast(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   boolean isUpdatable() {
/*  783 */     return this.isUpdatable;
/*      */   }
/*      */   
/*      */   public boolean last() throws SQLException {
/*      */     
/*  788 */     try { boolean ret = super.last();
/*  789 */       if (this.onInsertRow) {
/*  790 */         this.onInsertRow = false;
/*      */       }
/*  792 */       if (this.doingUpdates) {
/*  793 */         this.doingUpdates = false;
/*      */       }
/*  795 */       return ret; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void moveToCurrentRow() throws SQLException {
/*      */     
/*  800 */     try { synchronized (checkClosed().getConnectionMutex()) {
/*  801 */         if (!this.isUpdatable) {
/*  802 */           throw new NotUpdatable(this.notUpdatableReason);
/*      */         }
/*      */         
/*  805 */         if (this.onInsertRow) {
/*  806 */           this.onInsertRow = false;
/*  807 */           this.thisRow = this.savedCurrentRow;
/*      */         } 
/*      */       }  return; }
/*  810 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void moveToInsertRow() throws SQLException {
/*      */     try {
/*  814 */       synchronized (checkClosed().getConnectionMutex()) {
/*  815 */         if (!this.isUpdatable) {
/*  816 */           throw new NotUpdatable(this.notUpdatableReason);
/*      */         }
/*      */         
/*  819 */         if (this.inserter == null) {
/*  820 */           if (this.insertSQL == null) {
/*  821 */             generateStatements();
/*      */           }
/*      */           
/*  824 */           this.inserter = (ClientPreparedStatement)getConnection().clientPrepareStatement(this.insertSQL);
/*  825 */           this.inserter.getQueryBindings().setColumnDefinition(getMetadata());
/*      */           
/*  827 */           if (this.populateInserterWithDefaultValues) {
/*  828 */             extractDefaultValues();
/*      */           }
/*      */         } 
/*  831 */         resetInserter();
/*      */         
/*  833 */         Field[] fields = getMetadata().getFields();
/*  834 */         int numFields = fields.length;
/*      */         
/*  836 */         this.onInsertRow = true;
/*  837 */         this.doingUpdates = false;
/*  838 */         this.savedCurrentRow = this.thisRow;
/*  839 */         byte[][] newRowData = new byte[numFields][];
/*  840 */         this.thisRow = (Row)new ByteArrayRow(newRowData, getExceptionInterceptor());
/*  841 */         this.thisRow.setMetadata(getMetadata());
/*      */         
/*  843 */         for (int i = 0; i < numFields; i++) {
/*  844 */           if (!this.populateInserterWithDefaultValues) {
/*  845 */             this.inserter.setBytesNoEscapeNoQuotes(i + 1, StringUtils.getBytes("DEFAULT"));
/*  846 */             newRowData = (byte[][])null;
/*      */           }
/*  848 */           else if (this.defaultColumnValue[i] != null) {
/*  849 */             Field f = fields[i];
/*      */             
/*  851 */             switch (f.getMysqlTypeId()) {
/*      */               
/*      */               case 7:
/*      */               case 10:
/*      */               case 11:
/*      */               case 12:
/*  857 */                 if ((this.defaultColumnValue[i]).length > 7 && this.defaultColumnValue[i][0] == 67 && this.defaultColumnValue[i][1] == 85 && this.defaultColumnValue[i][2] == 82 && this.defaultColumnValue[i][3] == 82 && this.defaultColumnValue[i][4] == 69 && this.defaultColumnValue[i][5] == 78 && this.defaultColumnValue[i][6] == 84 && this.defaultColumnValue[i][7] == 95) {
/*      */ 
/*      */ 
/*      */ 
/*      */                   
/*  862 */                   this.inserter.setBytesNoEscapeNoQuotes(i + 1, this.defaultColumnValue[i]);
/*      */                   break;
/*      */                 } 
/*  865 */                 this.inserter.setBytes(i + 1, this.defaultColumnValue[i], false, false);
/*      */                 break;
/*      */ 
/*      */               
/*      */               default:
/*  870 */                 this.inserter.setBytes(i + 1, this.defaultColumnValue[i], false, false);
/*      */                 break;
/*      */             } 
/*      */             
/*  874 */             byte[] defaultValueCopy = new byte[(this.defaultColumnValue[i]).length];
/*  875 */             System.arraycopy(this.defaultColumnValue[i], 0, defaultValueCopy, 0, defaultValueCopy.length);
/*  876 */             newRowData[i] = defaultValueCopy;
/*      */           } else {
/*  878 */             this.inserter.setNull(i + 1, MysqlType.NULL);
/*  879 */             newRowData[i] = null;
/*      */           } 
/*      */         } 
/*      */       }  return;
/*      */     } catch (CJException cJException) {
/*  884 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public boolean next() throws SQLException {
/*      */     
/*  888 */     try { boolean ret = super.next();
/*  889 */       if (this.onInsertRow) {
/*  890 */         this.onInsertRow = false;
/*      */       }
/*  892 */       if (this.doingUpdates) {
/*  893 */         this.doingUpdates = false;
/*      */       }
/*  895 */       return ret; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public boolean prev() throws SQLException {
/*  900 */     boolean ret = super.prev();
/*  901 */     if (this.onInsertRow) {
/*  902 */       this.onInsertRow = false;
/*      */     }
/*  904 */     if (this.doingUpdates) {
/*  905 */       this.doingUpdates = false;
/*      */     }
/*  907 */     return ret;
/*      */   }
/*      */   
/*      */   public boolean previous() throws SQLException {
/*      */     
/*  912 */     try { boolean ret = super.previous();
/*  913 */       if (this.onInsertRow) {
/*  914 */         this.onInsertRow = false;
/*      */       }
/*  916 */       if (this.doingUpdates) {
/*  917 */         this.doingUpdates = false;
/*      */       }
/*  919 */       return ret; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void realClose(boolean calledExplicitly) throws SQLException {
/*      */     try {
/*  924 */       if (this.isClosed) {
/*      */         return;
/*      */       }
/*      */       
/*  928 */       synchronized (checkClosed().getConnectionMutex()) {
/*  929 */         SQLException sqlEx = null;
/*      */         
/*  931 */         if (this.useUsageAdvisor && 
/*  932 */           this.deleter == null && this.inserter == null && this.refresher == null && this.updater == null) {
/*  933 */           this.eventSink.processEvent((byte)0, (Session)this.session, (Query)getOwningStatement(), this, 0L, new Throwable(), 
/*  934 */               Messages.getString("UpdatableResultSet.34"));
/*      */         }
/*      */ 
/*      */         
/*      */         try {
/*  939 */           if (this.deleter != null) {
/*  940 */             this.deleter.close();
/*      */           }
/*  942 */         } catch (SQLException ex) {
/*  943 */           sqlEx = ex;
/*      */         } 
/*      */         
/*      */         try {
/*  947 */           if (this.inserter != null) {
/*  948 */             this.inserter.close();
/*      */           }
/*  950 */         } catch (SQLException ex) {
/*  951 */           sqlEx = ex;
/*      */         } 
/*      */         
/*      */         try {
/*  955 */           if (this.refresher != null) {
/*  956 */             this.refresher.close();
/*      */           }
/*  958 */         } catch (SQLException ex) {
/*  959 */           sqlEx = ex;
/*      */         } 
/*      */         
/*      */         try {
/*  963 */           if (this.updater != null) {
/*  964 */             this.updater.close();
/*      */           }
/*  966 */         } catch (SQLException ex) {
/*  967 */           sqlEx = ex;
/*      */         } 
/*      */         
/*  970 */         super.realClose(calledExplicitly);
/*      */         
/*  972 */         if (sqlEx != null)
/*  973 */           throw sqlEx; 
/*      */       }  return;
/*      */     } catch (CJException cJException) {
/*  976 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public void refreshRow() throws SQLException {
/*      */     
/*  980 */     try { synchronized (checkClosed().getConnectionMutex()) {
/*  981 */         if (isStrictlyForwardOnly()) {
/*  982 */           throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly"));
/*      */         }
/*      */         
/*  985 */         if (!this.isUpdatable) {
/*  986 */           throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
/*      */         }
/*      */         
/*  989 */         if (this.onInsertRow)
/*  990 */           throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.8"), getExceptionInterceptor()); 
/*  991 */         if (this.rowData.size() == 0)
/*  992 */           throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.9"), getExceptionInterceptor()); 
/*  993 */         if (isBeforeFirst())
/*  994 */           throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.10"), getExceptionInterceptor()); 
/*  995 */         if (isAfterLast()) {
/*  996 */           throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.11"), getExceptionInterceptor());
/*      */         }
/*      */         
/*  999 */         refreshRow(this.updater, this.thisRow);
/*      */       }  return; }
/* 1001 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } private void refreshRow(ClientPreparedStatement updateInsertStmt, Row rowToRefresh) throws SQLException {
/* 1004 */     if (this.refresher == null) {
/* 1005 */       if (this.refreshSQL == null) {
/* 1006 */         generateStatements();
/*      */       }
/*      */ 
/*      */ 
/*      */       
/* 1011 */       this
/*      */         
/* 1013 */         .refresher = ((ResultsetRow)this.thisRow).isBinaryEncoded() ? (ClientPreparedStatement)getConnection().serverPrepareStatement(this.refreshSQL) : (ClientPreparedStatement)getConnection().clientPrepareStatement(this.refreshSQL);
/*      */       
/* 1015 */       this.refresher.getQueryBindings().setColumnDefinition(getMetadata());
/*      */     } 
/*      */     
/* 1018 */     this.refresher.clearParameters();
/*      */     
/* 1020 */     int numKeys = this.primaryKeyIndicies.size();
/*      */     
/* 1022 */     for (int i = 0; i < numKeys; i++) {
/* 1023 */       byte[] dataFrom = null;
/* 1024 */       int index = ((Integer)this.primaryKeyIndicies.get(i)).intValue();
/*      */       
/* 1026 */       if (!this.doingUpdates && !this.onInsertRow) {
/* 1027 */         setParamValue(this.refresher, i + 1, this.thisRow, index, getMetadata().getFields()[index]);
/*      */       }
/*      */       else {
/*      */         
/* 1031 */         dataFrom = updateInsertStmt.getBytesRepresentation(index + 1);
/*      */ 
/*      */         
/* 1034 */         if (updateInsertStmt.isNull(index + 1) || dataFrom.length == 0) {
/* 1035 */           setParamValue(this.refresher, i + 1, this.thisRow, index, getMetadata().getFields()[index]);
/*      */         } else {
/*      */           
/* 1038 */           dataFrom = StringUtils.stripEnclosure(dataFrom, "_binary'", "'");
/*      */           
/* 1040 */           byte[] origBytes = updateInsertStmt.getOrigBytes(i + 1);
/* 1041 */           if (origBytes != null) {
/*      */ 
/*      */             
/* 1044 */             if (this.refresher instanceof com.mysql.cj.jdbc.ServerPreparedStatement) {
/*      */ 
/*      */               
/* 1047 */               this.refresher.setBytesNoEscapeNoQuotes(i + 1, origBytes);
/*      */             } else {
/* 1049 */               this.refresher.setBytesNoEscapeNoQuotes(i + 1, dataFrom);
/*      */             } 
/*      */           } else {
/* 1052 */             this.refresher.setBytesNoEscape(i + 1, dataFrom);
/*      */           } 
/*      */         } 
/*      */       } 
/* 1056 */     }  ResultSet rs = null;
/*      */     
/*      */     try {
/* 1059 */       rs = this.refresher.executeQuery();
/*      */       
/* 1061 */       int numCols = rs.getMetaData().getColumnCount();
/*      */       
/* 1063 */       if (rs.next()) {
/* 1064 */         for (int j = 0; j < numCols; j++) {
/* 1065 */           byte[] val = rs.getBytes(j + 1);
/* 1066 */           rowToRefresh.setBytes(j, (val == null || rs.wasNull()) ? null : val);
/*      */         } 
/*      */       } else {
/* 1069 */         throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.12"), "S1000", 
/* 1070 */             getExceptionInterceptor());
/*      */       } 
/*      */     } finally {
/* 1073 */       if (rs != null) {
/*      */         try {
/* 1075 */           rs.close();
/* 1076 */         } catch (SQLException sQLException) {}
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean relative(int rows) throws SQLException {
/*      */     
/* 1085 */     try { boolean ret = super.relative(rows);
/* 1086 */       if (this.onInsertRow) {
/* 1087 */         this.onInsertRow = false;
/*      */       }
/* 1089 */       if (this.doingUpdates) {
/* 1090 */         this.doingUpdates = false;
/*      */       }
/* 1092 */       return ret; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   private void resetInserter() throws SQLException {
/* 1096 */     this.inserter.clearParameters();
/*      */     
/* 1098 */     for (int i = 0; i < (getMetadata().getFields()).length; i++) {
/* 1099 */       this.inserter.setNull(i + 1, 0);
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean rowDeleted() throws SQLException {
/*      */     
/* 1105 */     try { throw SQLError.createSQLFeatureNotSupportedException(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean rowInserted() throws SQLException {
/*      */     
/* 1110 */     try { throw SQLError.createSQLFeatureNotSupportedException(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean rowUpdated() throws SQLException {
/*      */     
/* 1115 */     try { throw SQLError.createSQLFeatureNotSupportedException(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public void setResultSetConcurrency(int concurrencyFlag) {
/* 1120 */     super.setResultSetConcurrency(concurrencyFlag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void syncUpdate() throws SQLException {
/* 1138 */     if (this.updater == null) {
/* 1139 */       if (this.updateSQL == null) {
/* 1140 */         generateStatements();
/*      */       }
/*      */       
/* 1143 */       this.updater = (ClientPreparedStatement)getConnection().clientPrepareStatement(this.updateSQL);
/* 1144 */       this.updater.getQueryBindings().setColumnDefinition(getMetadata());
/*      */     } 
/*      */     
/* 1147 */     Field[] fields = getMetadata().getFields();
/* 1148 */     int numFields = fields.length;
/* 1149 */     this.updater.clearParameters();
/*      */     
/* 1151 */     for (int i = 0; i < numFields; i++) {
/* 1152 */       if (this.thisRow.getBytes(i) != null) {
/* 1153 */         switch (fields[i].getMysqlType()) {
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           case DATE:
/*      */           case TIMESTAMP:
/*      */           case DATETIME:
/*      */           case TIME:
/* 1162 */             this.updater.setString(i + 1, getString(i + 1));
/*      */             break;
/*      */           default:
/* 1165 */             this.updater.setObject(i + 1, getObject(i + 1), (SQLType)fields[i].getMysqlType());
/*      */             break;
/*      */         } 
/*      */       
/*      */       } else {
/* 1170 */         this.updater.setNull(i + 1, 0);
/*      */       } 
/*      */     } 
/*      */     
/* 1174 */     int numKeys = this.primaryKeyIndicies.size();
/* 1175 */     for (int j = 0; j < numKeys; j++) {
/* 1176 */       int idx = ((Integer)this.primaryKeyIndicies.get(j)).intValue();
/* 1177 */       setParamValue(this.updater, numFields + j + 1, this.thisRow, idx, fields[idx]);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void updateRow() throws SQLException {
/*      */     
/* 1183 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1184 */         if (!this.isUpdatable) {
/* 1185 */           throw new NotUpdatable(this.notUpdatableReason);
/*      */         }
/*      */         
/* 1188 */         if (this.doingUpdates) {
/* 1189 */           this.updater.executeUpdate();
/* 1190 */           refreshRow(this.updater, this.thisRow);
/* 1191 */           this.doingUpdates = false;
/* 1192 */         } else if (this.onInsertRow) {
/* 1193 */           throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.44"), getExceptionInterceptor());
/*      */         } 
/*      */ 
/*      */         
/* 1197 */         syncUpdate();
/*      */       }  return; }
/* 1199 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public int getHoldability() throws SQLException {
/*      */     
/* 1203 */     try { throw SQLError.createSQLFeatureNotSupportedException(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
/*      */     
/* 1208 */     try { updateAsciiStream(findColumn(columnLabel), x, length); return; }
/* 1209 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
/*      */     
/* 1213 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1214 */         if (!this.onInsertRow) {
/* 1215 */           if (!this.doingUpdates) {
/* 1216 */             this.doingUpdates = true;
/* 1217 */             syncUpdate();
/*      */           } 
/*      */           
/* 1220 */           this.updater.setAsciiStream(columnIndex, x, length);
/*      */         } else {
/* 1222 */           this.inserter.setAsciiStream(columnIndex, x, length);
/* 1223 */           this.thisRow.setBytes(columnIndex - 1, STREAM_DATA_MARKER);
/*      */         } 
/*      */       }  return; }
/* 1226 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
/*      */     
/* 1230 */     try { updateBigDecimal(findColumn(columnLabel), x); return; }
/* 1231 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
/*      */     
/* 1235 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1236 */         if (!this.onInsertRow) {
/* 1237 */           if (!this.doingUpdates) {
/* 1238 */             this.doingUpdates = true;
/* 1239 */             syncUpdate();
/*      */           } 
/*      */           
/* 1242 */           this.updater.setBigDecimal(columnIndex, x);
/*      */         } else {
/* 1244 */           this.inserter.setBigDecimal(columnIndex, x);
/* 1245 */           this.thisRow.setBytes(columnIndex - 1, (x == null) ? null : StringUtils.getBytes(x.toString()));
/*      */         } 
/*      */       }  return; }
/* 1248 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
/*      */     
/* 1252 */     try { updateBinaryStream(findColumn(columnLabel), x, length); return; }
/* 1253 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
/*      */     
/* 1257 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1258 */         if (!this.onInsertRow) {
/* 1259 */           if (!this.doingUpdates) {
/* 1260 */             this.doingUpdates = true;
/* 1261 */             syncUpdate();
/*      */           } 
/*      */           
/* 1264 */           this.updater.setBinaryStream(columnIndex, x, length);
/*      */         } else {
/* 1266 */           this.inserter.setBinaryStream(columnIndex, x, length);
/* 1267 */           this.thisRow.setBytes(columnIndex - 1, (x == null) ? null : STREAM_DATA_MARKER);
/*      */         } 
/*      */       }  return; }
/* 1270 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateBlob(String columnLabel, Blob blob) throws SQLException {
/*      */     
/* 1274 */     try { updateBlob(findColumn(columnLabel), blob); return; }
/* 1275 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateBlob(int columnIndex, Blob blob) throws SQLException {
/*      */     
/* 1279 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1280 */         if (!this.onInsertRow) {
/* 1281 */           if (!this.doingUpdates) {
/* 1282 */             this.doingUpdates = true;
/* 1283 */             syncUpdate();
/*      */           } 
/*      */           
/* 1286 */           this.updater.setBlob(columnIndex, blob);
/*      */         } else {
/* 1288 */           this.inserter.setBlob(columnIndex, blob);
/* 1289 */           this.thisRow.setBytes(columnIndex - 1, (blob == null) ? null : STREAM_DATA_MARKER);
/*      */         } 
/*      */       }  return; }
/* 1292 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateBoolean(String columnLabel, boolean x) throws SQLException {
/*      */     
/* 1296 */     try { updateBoolean(findColumn(columnLabel), x); return; }
/* 1297 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateBoolean(int columnIndex, boolean x) throws SQLException {
/*      */     
/* 1301 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1302 */         if (!this.onInsertRow) {
/* 1303 */           if (!this.doingUpdates) {
/* 1304 */             this.doingUpdates = true;
/* 1305 */             syncUpdate();
/*      */           } 
/*      */           
/* 1308 */           this.updater.setBoolean(columnIndex, x);
/*      */         } else {
/* 1310 */           this.inserter.setBoolean(columnIndex, x);
/* 1311 */           this.thisRow.setBytes(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex));
/*      */         } 
/*      */       }  return; }
/* 1314 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateByte(String columnLabel, byte x) throws SQLException {
/*      */     
/* 1318 */     try { updateByte(findColumn(columnLabel), x); return; }
/* 1319 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateByte(int columnIndex, byte x) throws SQLException {
/*      */     
/* 1323 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1324 */         if (!this.onInsertRow) {
/* 1325 */           if (!this.doingUpdates) {
/* 1326 */             this.doingUpdates = true;
/* 1327 */             syncUpdate();
/*      */           } 
/*      */           
/* 1330 */           this.updater.setByte(columnIndex, x);
/*      */         } else {
/* 1332 */           this.inserter.setByte(columnIndex, x);
/* 1333 */           this.thisRow.setBytes(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex));
/*      */         } 
/*      */       }  return; }
/* 1336 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateBytes(String columnLabel, byte[] x) throws SQLException {
/*      */     
/* 1340 */     try { updateBytes(findColumn(columnLabel), x); return; }
/* 1341 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateBytes(int columnIndex, byte[] x) throws SQLException {
/*      */     
/* 1345 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1346 */         if (!this.onInsertRow) {
/* 1347 */           if (!this.doingUpdates) {
/* 1348 */             this.doingUpdates = true;
/* 1349 */             syncUpdate();
/*      */           } 
/*      */           
/* 1352 */           this.updater.setBytes(columnIndex, x);
/*      */         } else {
/* 1354 */           this.inserter.setBytes(columnIndex, x);
/* 1355 */           this.thisRow.setBytes(columnIndex - 1, x);
/*      */         } 
/*      */       }  return; }
/* 1358 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
/*      */     
/* 1362 */     try { updateCharacterStream(findColumn(columnLabel), reader, length); return; }
/* 1363 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
/*      */     
/* 1367 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1368 */         if (!this.onInsertRow) {
/* 1369 */           if (!this.doingUpdates) {
/* 1370 */             this.doingUpdates = true;
/* 1371 */             syncUpdate();
/*      */           } 
/*      */           
/* 1374 */           this.updater.setCharacterStream(columnIndex, x, length);
/*      */         } else {
/* 1376 */           this.inserter.setCharacterStream(columnIndex, x, length);
/* 1377 */           this.thisRow.setBytes(columnIndex - 1, (x == null) ? null : STREAM_DATA_MARKER);
/*      */         } 
/*      */       }  return; }
/* 1380 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateClob(String columnLabel, Clob clob) throws SQLException {
/*      */     
/* 1384 */     try { updateClob(findColumn(columnLabel), clob); return; }
/* 1385 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateClob(int columnIndex, Clob clob) throws SQLException {
/*      */     
/* 1389 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1390 */         if (clob == null) {
/* 1391 */           updateNull(columnIndex);
/*      */         } else {
/* 1393 */           updateCharacterStream(columnIndex, clob.getCharacterStream(), (int)clob.length());
/*      */         } 
/*      */       }  return; }
/* 1396 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateDate(String columnLabel, Date x) throws SQLException {
/*      */     
/* 1400 */     try { updateDate(findColumn(columnLabel), x); return; }
/* 1401 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateDate(int columnIndex, Date x) throws SQLException {
/*      */     
/* 1405 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1406 */         if (!this.onInsertRow) {
/* 1407 */           if (!this.doingUpdates) {
/* 1408 */             this.doingUpdates = true;
/* 1409 */             syncUpdate();
/*      */           } 
/*      */           
/* 1412 */           this.updater.setDate(columnIndex, x);
/*      */         } else {
/* 1414 */           this.inserter.setDate(columnIndex, x);
/* 1415 */           this.thisRow.setBytes(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex));
/*      */         } 
/*      */       }  return; }
/* 1418 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateDouble(String columnLabel, double x) throws SQLException {
/*      */     
/* 1422 */     try { updateDouble(findColumn(columnLabel), x); return; }
/* 1423 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateDouble(int columnIndex, double x) throws SQLException {
/*      */     
/* 1427 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1428 */         if (!this.onInsertRow) {
/* 1429 */           if (!this.doingUpdates) {
/* 1430 */             this.doingUpdates = true;
/* 1431 */             syncUpdate();
/*      */           } 
/*      */           
/* 1434 */           this.updater.setDouble(columnIndex, x);
/*      */         } else {
/* 1436 */           this.inserter.setDouble(columnIndex, x);
/* 1437 */           this.thisRow.setBytes(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex));
/*      */         } 
/*      */       }  return; }
/* 1440 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateFloat(String columnLabel, float x) throws SQLException {
/*      */     
/* 1444 */     try { updateFloat(findColumn(columnLabel), x); return; }
/* 1445 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateFloat(int columnIndex, float x) throws SQLException {
/*      */     
/* 1449 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1450 */         if (!this.onInsertRow) {
/* 1451 */           if (!this.doingUpdates) {
/* 1452 */             this.doingUpdates = true;
/* 1453 */             syncUpdate();
/*      */           } 
/*      */           
/* 1456 */           this.updater.setFloat(columnIndex, x);
/*      */         } else {
/* 1458 */           this.inserter.setFloat(columnIndex, x);
/* 1459 */           this.thisRow.setBytes(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex));
/*      */         } 
/*      */       }  return; }
/* 1462 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateInt(String columnLabel, int x) throws SQLException {
/*      */     
/* 1466 */     try { updateInt(findColumn(columnLabel), x); return; }
/* 1467 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateInt(int columnIndex, int x) throws SQLException {
/*      */     
/* 1471 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1472 */         if (!this.onInsertRow) {
/* 1473 */           if (!this.doingUpdates) {
/* 1474 */             this.doingUpdates = true;
/* 1475 */             syncUpdate();
/*      */           } 
/*      */           
/* 1478 */           this.updater.setInt(columnIndex, x);
/*      */         } else {
/* 1480 */           this.inserter.setInt(columnIndex, x);
/* 1481 */           this.thisRow.setBytes(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex));
/*      */         } 
/*      */       }  return; }
/* 1484 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateLong(String columnLabel, long x) throws SQLException {
/*      */     
/* 1488 */     try { updateLong(findColumn(columnLabel), x); return; }
/* 1489 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateLong(int columnIndex, long x) throws SQLException {
/*      */     
/* 1493 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1494 */         if (!this.onInsertRow) {
/* 1495 */           if (!this.doingUpdates) {
/* 1496 */             this.doingUpdates = true;
/* 1497 */             syncUpdate();
/*      */           } 
/*      */           
/* 1500 */           this.updater.setLong(columnIndex, x);
/*      */         } else {
/* 1502 */           this.inserter.setLong(columnIndex, x);
/* 1503 */           this.thisRow.setBytes(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex));
/*      */         } 
/*      */       }  return; }
/* 1506 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateNull(String columnLabel) throws SQLException {
/*      */     
/* 1510 */     try { updateNull(findColumn(columnLabel)); return; }
/* 1511 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateNull(int columnIndex) throws SQLException {
/*      */     
/* 1515 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1516 */         if (!this.onInsertRow) {
/* 1517 */           if (!this.doingUpdates) {
/* 1518 */             this.doingUpdates = true;
/* 1519 */             syncUpdate();
/*      */           } 
/*      */           
/* 1522 */           this.updater.setNull(columnIndex, 0);
/*      */         } else {
/* 1524 */           this.inserter.setNull(columnIndex, 0);
/* 1525 */           this.thisRow.setBytes(columnIndex - 1, null);
/*      */         } 
/*      */       }  return; }
/* 1528 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateObject(String columnLabel, Object x) throws SQLException {
/*      */     
/* 1532 */     try { updateObject(findColumn(columnLabel), x); return; }
/* 1533 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateObject(int columnIndex, Object x) throws SQLException {
/*      */     
/* 1537 */     try { updateObjectInternal(columnIndex, x, (Integer)null, 0); return; }
/* 1538 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateObject(String columnLabel, Object x, int scale) throws SQLException {
/*      */     
/* 1542 */     try { updateObject(findColumn(columnLabel), x, scale); return; }
/* 1543 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateObject(int columnIndex, Object x, int scale) throws SQLException {
/*      */     
/* 1547 */     try { updateObjectInternal(columnIndex, x, (Integer)null, scale); return; }
/* 1548 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void updateObjectInternal(int columnIndex, Object x, Integer targetType, int scaleOrLength) throws SQLException {
/*      */     try {
/* 1567 */       MysqlType targetMysqlType = (targetType == null) ? null : MysqlType.getByJdbcType(targetType.intValue());
/* 1568 */       updateObjectInternal(columnIndex, x, (SQLType)targetMysqlType, scaleOrLength);
/*      */     }
/* 1570 */     catch (FeatureNotAvailableException nae) {
/* 1571 */       throw SQLError.createSQLFeatureNotSupportedException(Messages.getString("Statement.UnsupportedSQLType") + JDBCType.valueOf(targetType.intValue()), "S1C00", 
/* 1572 */           getExceptionInterceptor());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void updateObjectInternal(int columnIndex, Object x, SQLType targetType, int scaleOrLength) throws SQLException {
/* 1591 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1592 */       if (!this.onInsertRow) {
/* 1593 */         if (!this.doingUpdates) {
/* 1594 */           this.doingUpdates = true;
/* 1595 */           syncUpdate();
/*      */         } 
/*      */         
/* 1598 */         if (targetType == null) {
/* 1599 */           this.updater.setObject(columnIndex, x);
/*      */         } else {
/* 1601 */           this.updater.setObject(columnIndex, x, targetType);
/*      */         } 
/*      */       } else {
/* 1604 */         if (targetType == null) {
/* 1605 */           this.inserter.setObject(columnIndex, x);
/*      */         } else {
/* 1607 */           this.inserter.setObject(columnIndex, x, targetType);
/*      */         } 
/*      */         
/* 1610 */         this.thisRow.setBytes(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex));
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public void updateObject(String columnLabel, Object x, SQLType targetSqlType) throws SQLException {
/*      */     
/* 1617 */     try { updateObject(findColumn(columnLabel), x, targetSqlType); return; }
/* 1618 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateObject(int columnIndex, Object x, SQLType targetSqlType) throws SQLException {
/*      */     
/* 1622 */     try { updateObjectInternal(columnIndex, x, targetSqlType, 0); return; }
/* 1623 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateObject(String columnLabel, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
/*      */     
/* 1627 */     try { updateObject(findColumn(columnLabel), x, targetSqlType, scaleOrLength); return; }
/* 1628 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateObject(int columnIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
/*      */     
/* 1632 */     try { updateObjectInternal(columnIndex, x, targetSqlType, scaleOrLength); return; }
/* 1633 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateShort(String columnLabel, short x) throws SQLException {
/*      */     
/* 1637 */     try { updateShort(findColumn(columnLabel), x); return; }
/* 1638 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateShort(int columnIndex, short x) throws SQLException {
/*      */     
/* 1642 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1643 */         if (!this.onInsertRow) {
/* 1644 */           if (!this.doingUpdates) {
/* 1645 */             this.doingUpdates = true;
/* 1646 */             syncUpdate();
/*      */           } 
/*      */           
/* 1649 */           this.updater.setShort(columnIndex, x);
/*      */         } else {
/* 1651 */           this.inserter.setShort(columnIndex, x);
/* 1652 */           this.thisRow.setBytes(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex));
/*      */         } 
/*      */       }  return; }
/* 1655 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateString(String columnLabel, String x) throws SQLException {
/*      */     
/* 1659 */     try { updateString(findColumn(columnLabel), x); return; }
/* 1660 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateString(int columnIndex, String x) throws SQLException {
/*      */     
/* 1664 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1665 */         if (!this.onInsertRow) {
/* 1666 */           if (!this.doingUpdates) {
/* 1667 */             this.doingUpdates = true;
/* 1668 */             syncUpdate();
/*      */           } 
/*      */           
/* 1671 */           this.updater.setString(columnIndex, x);
/*      */         } else {
/* 1673 */           this.inserter.setString(columnIndex, x);
/* 1674 */           this.thisRow.setBytes(columnIndex - 1, (x == null) ? null : StringUtils.getBytes(x, this.charEncoding));
/*      */         } 
/*      */       }  return; }
/* 1677 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateTime(String columnLabel, Time x) throws SQLException {
/*      */     
/* 1681 */     try { updateTime(findColumn(columnLabel), x); return; }
/* 1682 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateTime(int columnIndex, Time x) throws SQLException {
/*      */     
/* 1686 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1687 */         if (!this.onInsertRow) {
/* 1688 */           if (!this.doingUpdates) {
/* 1689 */             this.doingUpdates = true;
/* 1690 */             syncUpdate();
/*      */           } 
/*      */           
/* 1693 */           this.updater.setTime(columnIndex, x);
/*      */         } else {
/* 1695 */           this.inserter.setTime(columnIndex, x);
/* 1696 */           this.thisRow.setBytes(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex));
/*      */         } 
/*      */       }  return; }
/* 1699 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
/*      */     
/* 1703 */     try { updateTimestamp(findColumn(columnLabel), x); return; }
/* 1704 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
/*      */     
/* 1708 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1709 */         if (!this.onInsertRow) {
/* 1710 */           if (!this.doingUpdates) {
/* 1711 */             this.doingUpdates = true;
/* 1712 */             syncUpdate();
/*      */           } 
/*      */           
/* 1715 */           this.updater.setTimestamp(columnIndex, x);
/*      */         } else {
/* 1717 */           this.inserter.setTimestamp(columnIndex, x);
/* 1718 */           this.thisRow.setBytes(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex));
/*      */         } 
/*      */       }  return; }
/* 1721 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
/*      */     
/* 1725 */     try { updateAsciiStream(findColumn(columnLabel), x); return; }
/* 1726 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
/*      */     
/* 1730 */     try { if (!this.onInsertRow) {
/* 1731 */         if (!this.doingUpdates) {
/* 1732 */           this.doingUpdates = true;
/* 1733 */           syncUpdate();
/*      */         } 
/*      */         
/* 1736 */         this.updater.setAsciiStream(columnIndex, x);
/*      */       } else {
/* 1738 */         this.inserter.setAsciiStream(columnIndex, x);
/* 1739 */         this.thisRow.setBytes(columnIndex - 1, STREAM_DATA_MARKER);
/*      */       }  return; }
/* 1741 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
/*      */     
/* 1745 */     try { updateAsciiStream(findColumn(columnLabel), x, length); return; }
/* 1746 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
/*      */     
/* 1750 */     try { if (!this.onInsertRow) {
/* 1751 */         if (!this.doingUpdates) {
/* 1752 */           this.doingUpdates = true;
/* 1753 */           syncUpdate();
/*      */         } 
/*      */         
/* 1756 */         this.updater.setAsciiStream(columnIndex, x, length);
/*      */       } else {
/* 1758 */         this.inserter.setAsciiStream(columnIndex, x, length);
/* 1759 */         this.thisRow.setBytes(columnIndex - 1, STREAM_DATA_MARKER);
/*      */       }  return; }
/* 1761 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
/*      */     
/* 1765 */     try { updateBinaryStream(findColumn(columnLabel), x); return; }
/* 1766 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
/*      */     
/* 1770 */     try { if (!this.onInsertRow) {
/* 1771 */         if (!this.doingUpdates) {
/* 1772 */           this.doingUpdates = true;
/* 1773 */           syncUpdate();
/*      */         } 
/*      */         
/* 1776 */         this.updater.setBinaryStream(columnIndex, x);
/*      */       } else {
/* 1778 */         this.inserter.setBinaryStream(columnIndex, x);
/* 1779 */         this.thisRow.setBytes(columnIndex - 1, (x == null) ? null : STREAM_DATA_MARKER);
/*      */       }  return; }
/* 1781 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
/*      */     
/* 1785 */     try { updateBinaryStream(findColumn(columnLabel), x, length); return; }
/* 1786 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
/*      */     
/* 1790 */     try { if (!this.onInsertRow) {
/* 1791 */         if (!this.doingUpdates) {
/* 1792 */           this.doingUpdates = true;
/* 1793 */           syncUpdate();
/*      */         } 
/*      */         
/* 1796 */         this.updater.setBinaryStream(columnIndex, x, length);
/*      */       } else {
/* 1798 */         this.inserter.setBinaryStream(columnIndex, x, length);
/* 1799 */         this.thisRow.setBytes(columnIndex - 1, (x == null) ? null : STREAM_DATA_MARKER);
/*      */       }  return; }
/* 1801 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
/*      */     
/* 1805 */     try { updateBlob(findColumn(columnLabel), inputStream); return; }
/* 1806 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
/*      */     
/* 1810 */     try { if (!this.onInsertRow) {
/* 1811 */         if (!this.doingUpdates) {
/* 1812 */           this.doingUpdates = true;
/* 1813 */           syncUpdate();
/*      */         } 
/*      */         
/* 1816 */         this.updater.setBlob(columnIndex, inputStream);
/*      */       } else {
/* 1818 */         this.inserter.setBlob(columnIndex, inputStream);
/* 1819 */         this.thisRow.setBytes(columnIndex - 1, (inputStream == null) ? null : STREAM_DATA_MARKER);
/*      */       }  return; }
/* 1821 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
/*      */     
/* 1825 */     try { updateBlob(findColumn(columnLabel), inputStream, length); return; }
/* 1826 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
/*      */     
/* 1830 */     try { if (!this.onInsertRow) {
/* 1831 */         if (!this.doingUpdates) {
/* 1832 */           this.doingUpdates = true;
/* 1833 */           syncUpdate();
/*      */         } 
/*      */         
/* 1836 */         this.updater.setBlob(columnIndex, inputStream, length);
/*      */       } else {
/* 1838 */         this.inserter.setBlob(columnIndex, inputStream, length);
/* 1839 */         this.thisRow.setBytes(columnIndex - 1, (inputStream == null) ? null : STREAM_DATA_MARKER);
/*      */       }  return; }
/* 1841 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
/*      */     
/* 1845 */     try { updateCharacterStream(findColumn(columnLabel), reader); return; }
/* 1846 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
/*      */     
/* 1850 */     try { if (!this.onInsertRow) {
/* 1851 */         if (!this.doingUpdates) {
/* 1852 */           this.doingUpdates = true;
/* 1853 */           syncUpdate();
/*      */         } 
/*      */         
/* 1856 */         this.updater.setCharacterStream(columnIndex, x);
/*      */       } else {
/* 1858 */         this.inserter.setCharacterStream(columnIndex, x);
/* 1859 */         this.thisRow.setBytes(columnIndex - 1, (x == null) ? null : STREAM_DATA_MARKER);
/*      */       }  return; }
/* 1861 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
/*      */     
/* 1865 */     try { updateCharacterStream(findColumn(columnLabel), reader, length); return; }
/* 1866 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
/*      */     
/* 1870 */     try { if (!this.onInsertRow) {
/* 1871 */         if (!this.doingUpdates) {
/* 1872 */           this.doingUpdates = true;
/* 1873 */           syncUpdate();
/*      */         } 
/*      */         
/* 1876 */         this.updater.setCharacterStream(columnIndex, x, length);
/*      */       } else {
/* 1878 */         this.inserter.setCharacterStream(columnIndex, x, length);
/* 1879 */         this.thisRow.setBytes(columnIndex - 1, (x == null) ? null : STREAM_DATA_MARKER);
/*      */       }  return; }
/* 1881 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateClob(String columnLabel, Reader reader) throws SQLException {
/*      */     
/* 1885 */     try { updateClob(findColumn(columnLabel), reader); return; }
/* 1886 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateClob(int columnIndex, Reader reader) throws SQLException {
/*      */     
/* 1890 */     try { updateCharacterStream(columnIndex, reader); return; }
/* 1891 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
/*      */     
/* 1895 */     try { updateClob(findColumn(columnLabel), reader, length); return; }
/* 1896 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
/*      */     
/* 1900 */     try { updateCharacterStream(columnIndex, reader, length); return; }
/* 1901 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
/*      */     
/* 1905 */     try { updateNCharacterStream(findColumn(columnLabel), reader); return; }
/* 1906 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
/*      */     
/* 1910 */     try { String fieldEncoding = getMetadata().getFields()[columnIndex - 1].getEncoding();
/* 1911 */       if (fieldEncoding == null || !fieldEncoding.equals("UTF-8")) {
/* 1912 */         throw new SQLException(Messages.getString("ResultSet.16"));
/*      */       }
/*      */       
/* 1915 */       if (!this.onInsertRow) {
/* 1916 */         if (!this.doingUpdates) {
/* 1917 */           this.doingUpdates = true;
/* 1918 */           syncUpdate();
/*      */         } 
/*      */         
/* 1921 */         this.updater.setNCharacterStream(columnIndex, x);
/*      */       } else {
/* 1923 */         this.inserter.setNCharacterStream(columnIndex, x);
/* 1924 */         this.thisRow.setBytes(columnIndex - 1, (x == null) ? null : STREAM_DATA_MARKER);
/*      */       }  return; }
/* 1926 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
/*      */     
/* 1930 */     try { updateNCharacterStream(findColumn(columnLabel), reader, length); return; }
/* 1931 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
/*      */     
/* 1935 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1936 */         String fieldEncoding = getMetadata().getFields()[columnIndex - 1].getEncoding();
/* 1937 */         if (fieldEncoding == null || !fieldEncoding.equals("UTF-8")) {
/* 1938 */           throw new SQLException(Messages.getString("ResultSet.16"));
/*      */         }
/*      */         
/* 1941 */         if (!this.onInsertRow) {
/* 1942 */           if (!this.doingUpdates) {
/* 1943 */             this.doingUpdates = true;
/* 1944 */             syncUpdate();
/*      */           } 
/*      */           
/* 1947 */           this.updater.setNCharacterStream(columnIndex, x, length);
/*      */         } else {
/* 1949 */           this.inserter.setNCharacterStream(columnIndex, x, length);
/* 1950 */           this.thisRow.setBytes(columnIndex - 1, (x == null) ? null : STREAM_DATA_MARKER);
/*      */         } 
/*      */       }  return; }
/* 1953 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateNClob(String columnLabel, Reader reader) throws SQLException {
/*      */     
/* 1957 */     try { updateNClob(findColumn(columnLabel), reader); return; }
/* 1958 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateNClob(int columnIndex, Reader reader) throws SQLException {
/*      */     
/* 1962 */     try { String fieldEncoding = getMetadata().getFields()[columnIndex - 1].getEncoding();
/* 1963 */       if (fieldEncoding == null || !fieldEncoding.equals("UTF-8")) {
/* 1964 */         throw new SQLException(Messages.getString("ResultSet.17"));
/*      */       }
/* 1966 */       updateCharacterStream(columnIndex, reader); return; }
/* 1967 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
/*      */     
/* 1971 */     try { updateNClob(findColumn(columnLabel), reader, length); return; }
/* 1972 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
/*      */     
/* 1976 */     try { String fieldEncoding = getMetadata().getFields()[columnIndex - 1].getEncoding();
/* 1977 */       if (fieldEncoding == null || !fieldEncoding.equals("UTF-8")) {
/* 1978 */         throw new SQLException(Messages.getString("ResultSet.17"));
/*      */       }
/* 1980 */       updateCharacterStream(columnIndex, reader, length); return; }
/* 1981 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
/*      */     
/* 1985 */     try { updateNClob(findColumn(columnLabel), nClob); return; }
/* 1986 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
/*      */     
/* 1990 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1991 */         String fieldEncoding = getMetadata().getFields()[columnIndex - 1].getEncoding();
/* 1992 */         if (fieldEncoding == null || !fieldEncoding.equals("UTF-8")) {
/* 1993 */           throw new SQLException(Messages.getString("ResultSet.17"));
/*      */         }
/*      */         
/* 1996 */         if (nClob == null) {
/* 1997 */           updateNull(columnIndex);
/*      */         } else {
/* 1999 */           updateNCharacterStream(columnIndex, nClob.getCharacterStream(), (int)nClob.length());
/*      */         } 
/*      */       }  return; }
/* 2002 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
/*      */     
/* 2006 */     try { updateSQLXML(findColumn(columnLabel), xmlObject); return; }
/* 2007 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
/*      */     
/* 2011 */     try { updateString(columnIndex, ((MysqlSQLXML)xmlObject).getString()); return; }
/* 2012 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateNString(String columnLabel, String x) throws SQLException {
/*      */     
/* 2016 */     try { updateNString(findColumn(columnLabel), x); return; }
/* 2017 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void updateNString(int columnIndex, String x) throws SQLException {
/*      */     
/* 2021 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 2022 */         String fieldEncoding = getMetadata().getFields()[columnIndex - 1].getEncoding();
/* 2023 */         if (fieldEncoding == null || !fieldEncoding.equals("UTF-8")) {
/* 2024 */           throw new SQLException(Messages.getString("ResultSet.18"));
/*      */         }
/*      */         
/* 2027 */         if (!this.onInsertRow) {
/* 2028 */           if (!this.doingUpdates) {
/* 2029 */             this.doingUpdates = true;
/* 2030 */             syncUpdate();
/*      */           } 
/*      */           
/* 2033 */           this.updater.setNString(columnIndex, x);
/*      */         } else {
/* 2035 */           this.inserter.setNString(columnIndex, x);
/* 2036 */           this.thisRow.setBytes(columnIndex - 1, (x == null) ? null : StringUtils.getBytes(x, fieldEncoding));
/*      */         } 
/*      */       }  return; }
/* 2039 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public Reader getNCharacterStream(String columnLabel) throws SQLException {
/*      */     
/* 2043 */     try { return getNCharacterStream(findColumn(columnLabel)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Reader getNCharacterStream(int columnIndex) throws SQLException {
/*      */     
/* 2048 */     try { String fieldEncoding = getMetadata().getFields()[columnIndex - 1].getEncoding();
/* 2049 */       if (fieldEncoding == null || !fieldEncoding.equals("UTF-8")) {
/* 2050 */         throw new SQLException(Messages.getString("ResultSet.11"));
/*      */       }
/*      */       
/* 2053 */       return getCharacterStream(columnIndex); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public NClob getNClob(String columnLabel) throws SQLException {
/*      */     
/* 2058 */     try { return getNClob(findColumn(columnLabel)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public NClob getNClob(int columnIndex) throws SQLException {
/*      */     
/* 2063 */     try { String fieldEncoding = getMetadata().getFields()[columnIndex - 1].getEncoding();
/*      */       
/* 2065 */       if (fieldEncoding == null || !fieldEncoding.equals("UTF-8")) {
/* 2066 */         throw new SQLException("Can not call getNClob() when field's charset isn't UTF-8");
/*      */       }
/*      */       
/* 2069 */       String asString = getStringForNClob(columnIndex);
/*      */       
/* 2071 */       if (asString == null) {
/* 2072 */         return null;
/*      */       }
/*      */       
/* 2075 */       return (NClob)new NClob(asString, getExceptionInterceptor()); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public String getNString(String columnLabel) throws SQLException {
/*      */     
/* 2080 */     try { return getNString(findColumn(columnLabel)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public String getNString(int columnIndex) throws SQLException {
/*      */     
/* 2085 */     try { String fieldEncoding = getMetadata().getFields()[columnIndex - 1].getEncoding();
/*      */       
/* 2087 */       if (fieldEncoding == null || !fieldEncoding.equals("UTF-8")) {
/* 2088 */         throw new SQLException("Can not call getNString() when field's charset isn't UTF-8");
/*      */       }
/*      */       
/* 2091 */       return getString(columnIndex); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public SQLXML getSQLXML(String columnLabel) throws SQLException {
/*      */     
/* 2096 */     try { return getSQLXML(findColumn(columnLabel)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public SQLXML getSQLXML(int columnIndex) throws SQLException {
/*      */     
/* 2101 */     try { return (SQLXML)new MysqlSQLXML(this, columnIndex, getExceptionInterceptor()); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   private String getStringForNClob(int columnIndex) throws SQLException {
/* 2105 */     String asString = null;
/*      */     
/* 2107 */     String forcedEncoding = "UTF-8";
/*      */     
/*      */     try {
/* 2110 */       byte[] asBytes = null;
/*      */       
/* 2112 */       asBytes = getBytes(columnIndex);
/*      */       
/* 2114 */       if (asBytes != null) {
/* 2115 */         asString = new String(asBytes, forcedEncoding);
/*      */       }
/* 2117 */     } catch (UnsupportedEncodingException uee) {
/* 2118 */       throw SQLError.createSQLException("Unsupported character encoding " + forcedEncoding, "S1009", 
/* 2119 */           getExceptionInterceptor());
/*      */     } 
/*      */     
/* 2122 */     return asString;
/*      */   }
/*      */   
/*      */   public boolean isClosed() throws SQLException {
/*      */     
/* 2127 */     try { return this.isClosed; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean isWrapperFor(Class<?> iface) throws SQLException {
/*      */     
/* 2132 */     try { checkClosed();
/*      */ 
/*      */       
/* 2135 */       return iface.isInstance(this); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public <T> T unwrap(Class<T> iface) throws SQLException {
/*      */     try {
/*      */       try {
/* 2142 */         return iface.cast(this);
/* 2143 */       } catch (ClassCastException cce) {
/* 2144 */         throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009", 
/* 2145 */             getExceptionInterceptor());
/*      */       } 
/*      */     } catch (CJException cJException) {
/*      */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\result\UpdatableResultSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */