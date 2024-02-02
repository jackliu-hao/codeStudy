package com.mysql.cj.jdbc.result;

import com.mysql.cj.Messages;
import com.mysql.cj.MysqlType;
import com.mysql.cj.PreparedQuery;
import com.mysql.cj.conf.PropertyDefinitions;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.exceptions.AssertionFailedException;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.FeatureNotAvailableException;
import com.mysql.cj.jdbc.ClientPreparedStatement;
import com.mysql.cj.jdbc.JdbcConnection;
import com.mysql.cj.jdbc.MysqlSQLXML;
import com.mysql.cj.jdbc.ServerPreparedStatement;
import com.mysql.cj.jdbc.StatementImpl;
import com.mysql.cj.jdbc.exceptions.NotUpdatable;
import com.mysql.cj.jdbc.exceptions.SQLError;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import com.mysql.cj.protocol.ResultsetRow;
import com.mysql.cj.protocol.ResultsetRows;
import com.mysql.cj.protocol.a.result.ByteArrayRow;
import com.mysql.cj.result.Field;
import com.mysql.cj.result.Row;
import com.mysql.cj.util.StringUtils;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.JDBCType;
import java.sql.NClob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class UpdatableResultSet extends ResultSetImpl {
   static final byte[] STREAM_DATA_MARKER = StringUtils.getBytes("** STREAM DATA **");
   private String charEncoding;
   private byte[][] defaultColumnValue;
   private ClientPreparedStatement deleter = null;
   private String deleteSQL = null;
   protected ClientPreparedStatement inserter = null;
   private String insertSQL = null;
   private boolean isUpdatable = false;
   private String notUpdatableReason = null;
   private List<Integer> primaryKeyIndicies = null;
   private String qualifiedAndQuotedTableName;
   private String quotedIdChar = null;
   private ClientPreparedStatement refresher;
   private String refreshSQL = null;
   private Row savedCurrentRow;
   protected ClientPreparedStatement updater = null;
   private String updateSQL = null;
   private boolean populateInserterWithDefaultValues = false;
   private boolean pedantic;
   private boolean hasLongColumnInfo = false;
   private Map<String, Map<String, Map<String, Integer>>> databasesUsedToTablesUsed = null;
   private boolean onInsertRow = false;
   protected boolean doingUpdates = false;

   public UpdatableResultSet(ResultsetRows tuples, JdbcConnection conn, StatementImpl creatorStmt) throws SQLException {
      super(tuples, conn, creatorStmt);
      this.checkUpdatability();
      this.charEncoding = (String)this.session.getPropertySet().getStringProperty(PropertyKey.characterEncoding).getValue();
      this.populateInserterWithDefaultValues = (Boolean)this.getSession().getPropertySet().getBooleanProperty(PropertyKey.populateInsertRowWithDefaultValues).getValue();
      this.pedantic = (Boolean)this.getSession().getPropertySet().getBooleanProperty(PropertyKey.pedantic).getValue();
      this.hasLongColumnInfo = this.getSession().getServerSession().hasLongColumnInfo();
   }

   public boolean absolute(int row) throws SQLException {
      try {
         boolean ret = super.absolute(row);
         if (this.onInsertRow) {
            this.onInsertRow = false;
         }

         if (this.doingUpdates) {
            this.doingUpdates = false;
         }

         return ret;
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void afterLast() throws SQLException {
      try {
         super.afterLast();
         if (this.onInsertRow) {
            this.onInsertRow = false;
         }

         if (this.doingUpdates) {
            this.doingUpdates = false;
         }

      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public void beforeFirst() throws SQLException {
      try {
         super.beforeFirst();
         if (this.onInsertRow) {
            this.onInsertRow = false;
         }

         if (this.doingUpdates) {
            this.doingUpdates = false;
         }

      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public void cancelRowUpdates() throws SQLException {
      try {
         if (this.doingUpdates) {
            this.doingUpdates = false;
            this.updater.clearParameters();
         }

      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   protected void checkRowPos() throws SQLException {
      if (!this.onInsertRow) {
         super.checkRowPos();
      }

   }

   public void checkUpdatability() throws SQLException {
      try {
         if (this.getMetadata() != null) {
            String singleTableName = null;
            String dbName = null;
            int primaryKeyCount = 0;
            Field[] fields = this.getMetadata().getFields();
            if (this.db == null || this.db.length() == 0) {
               this.db = fields[0].getDatabaseName();
               if (this.db == null || this.db.length() == 0) {
                  throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.43"), "S1009", this.getExceptionInterceptor());
               }
            }

            if (fields.length <= 0) {
               this.isUpdatable = false;
               this.notUpdatableReason = Messages.getString("NotUpdatableReason.3");
            } else {
               singleTableName = fields[0].getOriginalTableName();
               dbName = fields[0].getDatabaseName();
               if (singleTableName == null) {
                  singleTableName = fields[0].getTableName();
                  dbName = this.db;
               }

               if (singleTableName == null) {
                  this.isUpdatable = false;
                  this.notUpdatableReason = Messages.getString("NotUpdatableReason.3");
               } else {
                  if (fields[0].isPrimaryKey()) {
                     ++primaryKeyCount;
                  }

                  int i = 1;

                  while(true) {
                     if (i < fields.length) {
                        String otherTableName = fields[i].getOriginalTableName();
                        String otherDbName = fields[i].getDatabaseName();
                        if (otherTableName == null) {
                           otherTableName = fields[i].getTableName();
                           otherDbName = this.db;
                        }

                        if (otherTableName == null) {
                           this.isUpdatable = false;
                           this.notUpdatableReason = Messages.getString("NotUpdatableReason.3");
                           return;
                        }

                        if (!otherTableName.equals(singleTableName)) {
                           this.isUpdatable = false;
                           this.notUpdatableReason = Messages.getString("NotUpdatableReason.0");
                           return;
                        }

                        if (dbName != null && dbName.equals(otherDbName)) {
                           if (fields[i].isPrimaryKey()) {
                              ++primaryKeyCount;
                           }

                           ++i;
                           continue;
                        }

                        this.isUpdatable = false;
                        this.notUpdatableReason = Messages.getString("NotUpdatableReason.1");
                        return;
                     }

                     if ((Boolean)this.getSession().getPropertySet().getBooleanProperty(PropertyKey.strictUpdates).getValue()) {
                        DatabaseMetaData dbmd = this.getConnection().getMetaData();
                        ResultSet rs = null;
                        HashMap<String, String> primaryKeyNames = new HashMap();

                        try {
                           rs = this.session.getPropertySet().getEnumProperty(PropertyKey.databaseTerm).getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA ? dbmd.getPrimaryKeys((String)null, dbName, singleTableName) : dbmd.getPrimaryKeys(dbName, (String)null, singleTableName);

                           while(rs.next()) {
                              String keyName = rs.getString(4);
                              keyName = keyName.toUpperCase();
                              primaryKeyNames.put(keyName, keyName);
                           }
                        } finally {
                           if (rs != null) {
                              try {
                                 rs.close();
                              } catch (Exception var16) {
                                 AssertionFailedException.shouldNotHappen(var16);
                              }

                              rs = null;
                           }

                        }

                        int existingPrimaryKeysCount = primaryKeyNames.size();
                        if (existingPrimaryKeysCount == 0) {
                           this.isUpdatable = false;
                           this.notUpdatableReason = Messages.getString("NotUpdatableReason.5");
                           return;
                        }

                        for(int i = 0; i < fields.length; ++i) {
                           if (fields[i].isPrimaryKey()) {
                              String columnNameUC = fields[i].getName().toUpperCase();
                              if (primaryKeyNames.remove(columnNameUC) == null) {
                                 String originalName = fields[i].getOriginalName();
                                 if (originalName != null && primaryKeyNames.remove(originalName.toUpperCase()) == null) {
                                    this.isUpdatable = false;
                                    this.notUpdatableReason = Messages.getString("NotUpdatableReason.6", new Object[]{originalName});
                                    return;
                                 }
                              }
                           }
                        }

                        this.isUpdatable = primaryKeyNames.isEmpty();
                        if (!this.isUpdatable) {
                           this.notUpdatableReason = existingPrimaryKeysCount > 1 ? Messages.getString("NotUpdatableReason.7") : Messages.getString("NotUpdatableReason.4");
                           return;
                        }
                     }

                     if (primaryKeyCount == 0) {
                        this.isUpdatable = false;
                        this.notUpdatableReason = Messages.getString("NotUpdatableReason.4");
                        return;
                     }

                     this.isUpdatable = true;
                     this.notUpdatableReason = null;
                     return;
                  }
               }
            }
         }
      } catch (SQLException var18) {
         this.isUpdatable = false;
         this.notUpdatableReason = var18.getMessage();
      }
   }

   public void deleteRow() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (!this.isUpdatable) {
               throw new NotUpdatable(this.notUpdatableReason);
            } else if (this.onInsertRow) {
               throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.1"), this.getExceptionInterceptor());
            } else if (this.rowData.size() == 0) {
               throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.2"), this.getExceptionInterceptor());
            } else if (this.isBeforeFirst()) {
               throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.3"), this.getExceptionInterceptor());
            } else if (this.isAfterLast()) {
               throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.4"), this.getExceptionInterceptor());
            } else {
               if (this.deleter == null) {
                  if (this.deleteSQL == null) {
                     this.generateStatements();
                  }

                  this.deleter = (ClientPreparedStatement)this.connection.clientPrepareStatement(this.deleteSQL);
               }

               this.deleter.clearParameters();
               int numKeys = this.primaryKeyIndicies.size();

               for(int i = 0; i < numKeys; ++i) {
                  int index = (Integer)this.primaryKeyIndicies.get(i);
                  this.setParamValue(this.deleter, i + 1, this.thisRow, index, this.getMetadata().getFields()[index]);
               }

               this.deleter.executeUpdate();
               this.rowData.remove();
               this.prev();
            }
         }
      } catch (CJException var8) {
         throw SQLExceptionsMapping.translateException(var8, this.getExceptionInterceptor());
      }
   }

   private void setParamValue(ClientPreparedStatement ps, int psIdx, Row row, int rsIdx, Field field) throws SQLException {
      byte[] val = row.getBytes(rsIdx);
      if (val == null) {
         ps.setNull(psIdx, MysqlType.NULL);
      } else {
         switch (field.getMysqlType()) {
            case NULL:
               ps.setNull(psIdx, MysqlType.NULL);
               break;
            case TINYINT:
            case TINYINT_UNSIGNED:
            case SMALLINT:
            case SMALLINT_UNSIGNED:
            case MEDIUMINT:
            case MEDIUMINT_UNSIGNED:
            case INT:
            case INT_UNSIGNED:
            case YEAR:
               ps.setInt(psIdx, this.getInt(rsIdx + 1));
               break;
            case BIGINT:
               ps.setLong(psIdx, this.getLong(rsIdx + 1));
               break;
            case BIGINT_UNSIGNED:
               ps.setBigInteger(psIdx, this.getBigInteger(rsIdx + 1));
               break;
            case CHAR:
            case ENUM:
            case SET:
            case VARCHAR:
            case JSON:
            case TINYTEXT:
            case TEXT:
            case MEDIUMTEXT:
            case LONGTEXT:
            case DECIMAL:
            case DECIMAL_UNSIGNED:
               ps.setString(psIdx, this.getString(rsIdx + 1));
               break;
            case DATE:
               ps.setDate(psIdx, this.getDate(rsIdx + 1));
               break;
            case TIMESTAMP:
               ((PreparedQuery)ps.getQuery()).getQueryBindings().bindTimestamp(ps.getCoreParameterIndex(psIdx), this.getTimestamp(rsIdx + 1), (Calendar)null, field.getDecimals(), MysqlType.TIMESTAMP);
               break;
            case DATETIME:
               ps.setObject(psIdx, this.getObject(rsIdx + 1, LocalDateTime.class), MysqlType.DATETIME, field.getDecimals());
               break;
            case TIME:
               ps.setTime(psIdx, this.getTime(rsIdx + 1));
               break;
            case DOUBLE:
            case DOUBLE_UNSIGNED:
            case FLOAT:
            case FLOAT_UNSIGNED:
            case BOOLEAN:
            case BIT:
               ps.setBytesNoEscapeNoQuotes(psIdx, val);
               break;
            default:
               ps.setBytes(psIdx, val);
         }

      }
   }

   private void extractDefaultValues() throws SQLException {
      DatabaseMetaData dbmd = this.getConnection().getMetaData();
      this.defaultColumnValue = new byte[this.getMetadata().getFields().length][];
      ResultSet columnsResultSet = null;
      Iterator var3 = this.databasesUsedToTablesUsed.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry<String, Map<String, Map<String, Integer>>> dbEntry = (Map.Entry)var3.next();
         Iterator var5 = ((Map)dbEntry.getValue()).entrySet().iterator();

         while(var5.hasNext()) {
            Map.Entry<String, Map<String, Integer>> tableEntry = (Map.Entry)var5.next();
            String tableName = (String)tableEntry.getKey();
            Map<String, Integer> columnNamesToIndices = (Map)tableEntry.getValue();

            try {
               columnsResultSet = this.session.getPropertySet().getEnumProperty(PropertyKey.databaseTerm).getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA ? dbmd.getColumns((String)null, this.db, tableName, "%") : dbmd.getColumns(this.db, (String)null, tableName, "%");

               while(columnsResultSet.next()) {
                  String columnName = columnsResultSet.getString("COLUMN_NAME");
                  byte[] defaultValue = columnsResultSet.getBytes("COLUMN_DEF");
                  if (columnNamesToIndices.containsKey(columnName)) {
                     int localColumnIndex = (Integer)columnNamesToIndices.get(columnName);
                     this.defaultColumnValue[localColumnIndex] = defaultValue;
                  }
               }
            } finally {
               if (columnsResultSet != null) {
                  columnsResultSet.close();
                  columnsResultSet = null;
               }

            }
         }
      }

   }

   public boolean first() throws SQLException {
      try {
         boolean ret = super.first();
         if (this.onInsertRow) {
            this.onInsertRow = false;
         }

         if (this.doingUpdates) {
            this.doingUpdates = false;
         }

         return ret;
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   protected void generateStatements() throws SQLException {
      try {
         if (!this.isUpdatable) {
            this.doingUpdates = false;
            this.onInsertRow = false;
            throw new NotUpdatable(this.notUpdatableReason);
         } else {
            String quotedId = this.getQuotedIdChar();
            Map<String, String> tableNamesSoFar = null;
            if (this.session.getServerSession().isLowerCaseTableNames()) {
               tableNamesSoFar = new TreeMap(String.CASE_INSENSITIVE_ORDER);
               this.databasesUsedToTablesUsed = new TreeMap(String.CASE_INSENSITIVE_ORDER);
            } else {
               tableNamesSoFar = new TreeMap();
               this.databasesUsedToTablesUsed = new TreeMap();
            }

            this.primaryKeyIndicies = new ArrayList();
            StringBuilder fieldValues = new StringBuilder();
            StringBuilder keyValues = new StringBuilder();
            StringBuilder columnNames = new StringBuilder();
            StringBuilder insertPlaceHolders = new StringBuilder();
            StringBuilder allTablesBuf = new StringBuilder();
            Map<Integer, String> columnIndicesToTable = new HashMap();
            Field[] fields = this.getMetadata().getFields();

            for(int i = 0; i < fields.length; ++i) {
               Map<String, Integer> updColumnNameToIndex = null;
               String tableOnlyName;
               String fqTableName;
               String fqTableName;
               if (fields[i].getOriginalTableName() != null) {
                  tableOnlyName = fields[i].getDatabaseName();
                  fqTableName = fields[i].getOriginalTableName();
                  fqTableName = StringUtils.getFullyQualifiedName(tableOnlyName, fqTableName, quotedId, this.pedantic);
                  if (!tableNamesSoFar.containsKey(fqTableName)) {
                     if (!tableNamesSoFar.isEmpty()) {
                        allTablesBuf.append(',');
                     }

                     allTablesBuf.append(fqTableName);
                     tableNamesSoFar.put(fqTableName, fqTableName);
                  }

                  columnIndicesToTable.put(i, fqTableName);
                  updColumnNameToIndex = this.getColumnsToIndexMapForTableAndDB(tableOnlyName, fqTableName);
               } else {
                  tableOnlyName = fields[i].getTableName();
                  if (tableOnlyName != null) {
                     fqTableName = StringUtils.quoteIdentifier(tableOnlyName, quotedId, this.pedantic);
                     if (!tableNamesSoFar.containsKey(fqTableName)) {
                        if (!tableNamesSoFar.isEmpty()) {
                           allTablesBuf.append(',');
                        }

                        allTablesBuf.append(fqTableName);
                        tableNamesSoFar.put(fqTableName, fqTableName);
                     }

                     columnIndicesToTable.put(i, fqTableName);
                     updColumnNameToIndex = this.getColumnsToIndexMapForTableAndDB(this.db, tableOnlyName);
                  }
               }

               tableOnlyName = fields[i].getOriginalName();
               fqTableName = this.hasLongColumnInfo && tableOnlyName != null && tableOnlyName.length() > 0 ? tableOnlyName : fields[i].getName();
               if (updColumnNameToIndex != null && fqTableName != null) {
                  updColumnNameToIndex.put(fqTableName, i);
               }

               fqTableName = fields[i].getOriginalTableName();
               String tableName = this.hasLongColumnInfo && fqTableName != null && fqTableName.length() > 0 ? fqTableName : fields[i].getTableName();
               String databaseName = fields[i].getDatabaseName();
               String qualifiedColumnName = StringUtils.getFullyQualifiedName(databaseName, tableName, quotedId, this.pedantic) + '.' + StringUtils.quoteIdentifier(fqTableName, quotedId, this.pedantic);
               if (fields[i].isPrimaryKey()) {
                  this.primaryKeyIndicies.add(i);
                  if (keyValues.length() > 0) {
                     keyValues.append(" AND ");
                  }

                  keyValues.append(qualifiedColumnName);
                  keyValues.append("<=>");
                  keyValues.append("?");
               }

               if (fieldValues.length() == 0) {
                  fieldValues.append("SET ");
               } else {
                  fieldValues.append(",");
                  columnNames.append(",");
                  insertPlaceHolders.append(",");
               }

               insertPlaceHolders.append("?");
               columnNames.append(qualifiedColumnName);
               fieldValues.append(qualifiedColumnName);
               fieldValues.append("=?");
            }

            this.qualifiedAndQuotedTableName = allTablesBuf.toString();
            this.updateSQL = "UPDATE " + this.qualifiedAndQuotedTableName + " " + fieldValues.toString() + " WHERE " + keyValues.toString();
            this.insertSQL = "INSERT INTO " + this.qualifiedAndQuotedTableName + " (" + columnNames.toString() + ") VALUES (" + insertPlaceHolders.toString() + ")";
            this.refreshSQL = "SELECT " + columnNames.toString() + " FROM " + this.qualifiedAndQuotedTableName + " WHERE " + keyValues.toString();
            this.deleteSQL = "DELETE FROM " + this.qualifiedAndQuotedTableName + " WHERE " + keyValues.toString();
         }
      } catch (CJException var19) {
         throw SQLExceptionsMapping.translateException(var19, this.getExceptionInterceptor());
      }
   }

   private Map<String, Integer> getColumnsToIndexMapForTableAndDB(String databaseName, String tableName) {
      Map<String, Map<String, Integer>> tablesUsedToColumnsMap = (Map)this.databasesUsedToTablesUsed.get(databaseName);
      if (tablesUsedToColumnsMap == null) {
         tablesUsedToColumnsMap = this.session.getServerSession().isLowerCaseTableNames() ? new TreeMap(String.CASE_INSENSITIVE_ORDER) : new TreeMap();
         this.databasesUsedToTablesUsed.put(databaseName, tablesUsedToColumnsMap);
      }

      Map<String, Integer> nameToIndex = (Map)((Map)tablesUsedToColumnsMap).get(tableName);
      if (nameToIndex == null) {
         nameToIndex = new HashMap();
         ((Map)tablesUsedToColumnsMap).put(tableName, nameToIndex);
      }

      return (Map)nameToIndex;
   }

   public int getConcurrency() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            return this.isUpdatable ? 1008 : 1007;
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   private String getQuotedIdChar() throws SQLException {
      if (this.quotedIdChar == null) {
         this.quotedIdChar = this.session.getIdentifierQuoteString();
      }

      return this.quotedIdChar;
   }

   public void insertRow() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
               throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.7"), this.getExceptionInterceptor());
            } else {
               this.inserter.executeUpdate();
               long autoIncrementId = this.inserter.getLastInsertID();
               Field[] fields = this.getMetadata().getFields();
               byte[][] newRow = new byte[fields.length][];

               for(int i = 0; i < fields.length; ++i) {
                  newRow[i] = this.inserter.isNull(i + 1) ? null : this.inserter.getBytesRepresentation(i + 1);
                  if (fields[i].isAutoIncrement() && autoIncrementId > 0L) {
                     newRow[i] = StringUtils.getBytes(String.valueOf(autoIncrementId));
                     this.inserter.setBytesNoEscapeNoQuotes(i + 1, newRow[i]);
                  }
               }

               Row resultSetRow = new ByteArrayRow(newRow, this.getExceptionInterceptor());
               this.refreshRow(this.inserter, resultSetRow);
               this.rowData.addRow(resultSetRow);
               this.resetInserter();
            }
         }
      } catch (CJException var10) {
         throw SQLExceptionsMapping.translateException(var10, this.getExceptionInterceptor());
      }
   }

   public boolean isAfterLast() throws SQLException {
      try {
         return super.isAfterLast();
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public boolean isBeforeFirst() throws SQLException {
      try {
         return super.isBeforeFirst();
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public boolean isFirst() throws SQLException {
      try {
         return super.isFirst();
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public boolean isLast() throws SQLException {
      try {
         return super.isLast();
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   boolean isUpdatable() {
      return this.isUpdatable;
   }

   public boolean last() throws SQLException {
      try {
         boolean ret = super.last();
         if (this.onInsertRow) {
            this.onInsertRow = false;
         }

         if (this.doingUpdates) {
            this.doingUpdates = false;
         }

         return ret;
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public void moveToCurrentRow() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (!this.isUpdatable) {
               throw new NotUpdatable(this.notUpdatableReason);
            } else {
               if (this.onInsertRow) {
                  this.onInsertRow = false;
                  this.thisRow = this.savedCurrentRow;
               }

            }
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void moveToInsertRow() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (!this.isUpdatable) {
               throw new NotUpdatable(this.notUpdatableReason);
            } else {
               if (this.inserter == null) {
                  if (this.insertSQL == null) {
                     this.generateStatements();
                  }

                  this.inserter = (ClientPreparedStatement)this.getConnection().clientPrepareStatement(this.insertSQL);
                  this.inserter.getQueryBindings().setColumnDefinition(this.getMetadata());
                  if (this.populateInserterWithDefaultValues) {
                     this.extractDefaultValues();
                  }
               }

               this.resetInserter();
               Field[] fields = this.getMetadata().getFields();
               int numFields = fields.length;
               this.onInsertRow = true;
               this.doingUpdates = false;
               this.savedCurrentRow = this.thisRow;
               byte[][] newRowData = new byte[numFields][];
               this.thisRow = new ByteArrayRow(newRowData, this.getExceptionInterceptor());
               this.thisRow.setMetadata(this.getMetadata());

               for(int i = 0; i < numFields; ++i) {
                  if (!this.populateInserterWithDefaultValues) {
                     this.inserter.setBytesNoEscapeNoQuotes(i + 1, StringUtils.getBytes("DEFAULT"));
                     newRowData = (byte[][])null;
                  } else if (this.defaultColumnValue[i] == null) {
                     this.inserter.setNull(i + 1, MysqlType.NULL);
                     newRowData[i] = null;
                  } else {
                     Field f = fields[i];
                     switch (f.getMysqlTypeId()) {
                        case 7:
                        case 10:
                        case 11:
                        case 12:
                           if (this.defaultColumnValue[i].length > 7 && this.defaultColumnValue[i][0] == 67 && this.defaultColumnValue[i][1] == 85 && this.defaultColumnValue[i][2] == 82 && this.defaultColumnValue[i][3] == 82 && this.defaultColumnValue[i][4] == 69 && this.defaultColumnValue[i][5] == 78 && this.defaultColumnValue[i][6] == 84 && this.defaultColumnValue[i][7] == 95) {
                              this.inserter.setBytesNoEscapeNoQuotes(i + 1, this.defaultColumnValue[i]);
                           } else {
                              this.inserter.setBytes(i + 1, this.defaultColumnValue[i], false, false);
                           }
                           break;
                        case 8:
                        case 9:
                        default:
                           this.inserter.setBytes(i + 1, this.defaultColumnValue[i], false, false);
                     }

                     byte[] defaultValueCopy = new byte[this.defaultColumnValue[i].length];
                     System.arraycopy(this.defaultColumnValue[i], 0, defaultValueCopy, 0, defaultValueCopy.length);
                     newRowData[i] = defaultValueCopy;
                  }
               }

            }
         }
      } catch (CJException var11) {
         throw SQLExceptionsMapping.translateException(var11, this.getExceptionInterceptor());
      }
   }

   public boolean next() throws SQLException {
      try {
         boolean ret = super.next();
         if (this.onInsertRow) {
            this.onInsertRow = false;
         }

         if (this.doingUpdates) {
            this.doingUpdates = false;
         }

         return ret;
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public boolean prev() throws SQLException {
      boolean ret = super.prev();
      if (this.onInsertRow) {
         this.onInsertRow = false;
      }

      if (this.doingUpdates) {
         this.doingUpdates = false;
      }

      return ret;
   }

   public boolean previous() throws SQLException {
      try {
         boolean ret = super.previous();
         if (this.onInsertRow) {
            this.onInsertRow = false;
         }

         if (this.doingUpdates) {
            this.doingUpdates = false;
         }

         return ret;
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public void realClose(boolean calledExplicitly) throws SQLException {
      try {
         if (!this.isClosed) {
            synchronized(this.checkClosed().getConnectionMutex()) {
               SQLException sqlEx = null;
               if (this.useUsageAdvisor && this.deleter == null && this.inserter == null && this.refresher == null && this.updater == null) {
                  this.eventSink.processEvent((byte)0, this.session, this.getOwningStatement(), this, 0L, new Throwable(), Messages.getString("UpdatableResultSet.34"));
               }

               try {
                  if (this.deleter != null) {
                     this.deleter.close();
                  }
               } catch (SQLException var10) {
                  sqlEx = var10;
               }

               try {
                  if (this.inserter != null) {
                     this.inserter.close();
                  }
               } catch (SQLException var9) {
                  sqlEx = var9;
               }

               try {
                  if (this.refresher != null) {
                     this.refresher.close();
                  }
               } catch (SQLException var8) {
                  sqlEx = var8;
               }

               try {
                  if (this.updater != null) {
                     this.updater.close();
                  }
               } catch (SQLException var7) {
                  sqlEx = var7;
               }

               super.realClose(calledExplicitly);
               if (sqlEx != null) {
                  throw sqlEx;
               }
            }
         }
      } catch (CJException var12) {
         throw SQLExceptionsMapping.translateException(var12, this.getExceptionInterceptor());
      }
   }

   public void refreshRow() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (this.isStrictlyForwardOnly()) {
               throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly"));
            } else if (!this.isUpdatable) {
               throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
            } else if (this.onInsertRow) {
               throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.8"), this.getExceptionInterceptor());
            } else if (this.rowData.size() == 0) {
               throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.9"), this.getExceptionInterceptor());
            } else if (this.isBeforeFirst()) {
               throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.10"), this.getExceptionInterceptor());
            } else if (this.isAfterLast()) {
               throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.11"), this.getExceptionInterceptor());
            } else {
               this.refreshRow(this.updater, this.thisRow);
            }
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   private void refreshRow(ClientPreparedStatement updateInsertStmt, Row rowToRefresh) throws SQLException {
      if (this.refresher == null) {
         if (this.refreshSQL == null) {
            this.generateStatements();
         }

         this.refresher = ((ResultsetRow)this.thisRow).isBinaryEncoded() ? (ClientPreparedStatement)this.getConnection().serverPrepareStatement(this.refreshSQL) : (ClientPreparedStatement)this.getConnection().clientPrepareStatement(this.refreshSQL);
         this.refresher.getQueryBindings().setColumnDefinition(this.getMetadata());
      }

      this.refresher.clearParameters();
      int numKeys = this.primaryKeyIndicies.size();

      int i;
      byte[] origBytes;
      for(int i = 0; i < numKeys; ++i) {
         byte[] dataFrom = null;
         i = (Integer)this.primaryKeyIndicies.get(i);
         if (!this.doingUpdates && !this.onInsertRow) {
            this.setParamValue(this.refresher, i + 1, this.thisRow, i, this.getMetadata().getFields()[i]);
         } else {
            byte[] dataFrom = updateInsertStmt.getBytesRepresentation(i + 1);
            if (!updateInsertStmt.isNull(i + 1) && dataFrom.length != 0) {
               dataFrom = StringUtils.stripEnclosure(dataFrom, "_binary'", "'");
               origBytes = updateInsertStmt.getOrigBytes(i + 1);
               if (origBytes != null) {
                  if (this.refresher instanceof ServerPreparedStatement) {
                     this.refresher.setBytesNoEscapeNoQuotes(i + 1, origBytes);
                  } else {
                     this.refresher.setBytesNoEscapeNoQuotes(i + 1, dataFrom);
                  }
               } else {
                  this.refresher.setBytesNoEscape(i + 1, dataFrom);
               }
            } else {
               this.setParamValue(this.refresher, i + 1, this.thisRow, i, this.getMetadata().getFields()[i]);
            }
         }
      }

      ResultSet rs = null;

      try {
         rs = this.refresher.executeQuery();
         int numCols = rs.getMetaData().getColumnCount();
         if (!rs.next()) {
            throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.12"), "S1000", this.getExceptionInterceptor());
         }

         for(i = 0; i < numCols; ++i) {
            origBytes = rs.getBytes(i + 1);
            rowToRefresh.setBytes(i, origBytes != null && !rs.wasNull() ? origBytes : null);
         }
      } finally {
         if (rs != null) {
            try {
               rs.close();
            } catch (SQLException var13) {
            }
         }

      }

   }

   public boolean relative(int rows) throws SQLException {
      try {
         boolean ret = super.relative(rows);
         if (this.onInsertRow) {
            this.onInsertRow = false;
         }

         if (this.doingUpdates) {
            this.doingUpdates = false;
         }

         return ret;
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   private void resetInserter() throws SQLException {
      this.inserter.clearParameters();

      for(int i = 0; i < this.getMetadata().getFields().length; ++i) {
         this.inserter.setNull(i + 1, 0);
      }

   }

   public boolean rowDeleted() throws SQLException {
      try {
         throw SQLError.createSQLFeatureNotSupportedException();
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public boolean rowInserted() throws SQLException {
      try {
         throw SQLError.createSQLFeatureNotSupportedException();
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public boolean rowUpdated() throws SQLException {
      try {
         throw SQLError.createSQLFeatureNotSupportedException();
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public void setResultSetConcurrency(int concurrencyFlag) {
      super.setResultSetConcurrency(concurrencyFlag);
   }

   protected void syncUpdate() throws SQLException {
      if (this.updater == null) {
         if (this.updateSQL == null) {
            this.generateStatements();
         }

         this.updater = (ClientPreparedStatement)this.getConnection().clientPrepareStatement(this.updateSQL);
         this.updater.getQueryBindings().setColumnDefinition(this.getMetadata());
      }

      Field[] fields = this.getMetadata().getFields();
      int numFields = fields.length;
      this.updater.clearParameters();

      int i;
      for(i = 0; i < numFields; ++i) {
         if (this.thisRow.getBytes(i) != null) {
            switch (fields[i].getMysqlType()) {
               case DATE:
               case TIMESTAMP:
               case DATETIME:
               case TIME:
                  this.updater.setString(i + 1, this.getString(i + 1));
                  break;
               default:
                  this.updater.setObject(i + 1, this.getObject(i + 1), fields[i].getMysqlType());
            }
         } else {
            this.updater.setNull(i + 1, 0);
         }
      }

      i = this.primaryKeyIndicies.size();

      for(int i = 0; i < i; ++i) {
         int idx = (Integer)this.primaryKeyIndicies.get(i);
         this.setParamValue(this.updater, numFields + i + 1, this.thisRow, idx, fields[idx]);
      }

   }

   public void updateRow() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (!this.isUpdatable) {
               throw new NotUpdatable(this.notUpdatableReason);
            } else {
               if (this.doingUpdates) {
                  this.updater.executeUpdate();
                  this.refreshRow(this.updater, this.thisRow);
                  this.doingUpdates = false;
               } else if (this.onInsertRow) {
                  throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.44"), this.getExceptionInterceptor());
               }

               this.syncUpdate();
            }
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public int getHoldability() throws SQLException {
      try {
         throw SQLError.createSQLFeatureNotSupportedException();
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
      try {
         this.updateAsciiStream(this.findColumn(columnLabel), x, length);
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
               if (!this.doingUpdates) {
                  this.doingUpdates = true;
                  this.syncUpdate();
               }

               this.updater.setAsciiStream(columnIndex, x, length);
            } else {
               this.inserter.setAsciiStream(columnIndex, x, length);
               this.thisRow.setBytes(columnIndex - 1, STREAM_DATA_MARKER);
            }

         }
      } catch (CJException var8) {
         throw SQLExceptionsMapping.translateException(var8, this.getExceptionInterceptor());
      }
   }

   public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
      try {
         this.updateBigDecimal(this.findColumn(columnLabel), x);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
               if (!this.doingUpdates) {
                  this.doingUpdates = true;
                  this.syncUpdate();
               }

               this.updater.setBigDecimal(columnIndex, x);
            } else {
               this.inserter.setBigDecimal(columnIndex, x);
               this.thisRow.setBytes(columnIndex - 1, x == null ? null : StringUtils.getBytes(x.toString()));
            }

         }
      } catch (CJException var7) {
         throw SQLExceptionsMapping.translateException(var7, this.getExceptionInterceptor());
      }
   }

   public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
      try {
         this.updateBinaryStream(this.findColumn(columnLabel), x, length);
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
               if (!this.doingUpdates) {
                  this.doingUpdates = true;
                  this.syncUpdate();
               }

               this.updater.setBinaryStream(columnIndex, x, length);
            } else {
               this.inserter.setBinaryStream(columnIndex, x, length);
               this.thisRow.setBytes(columnIndex - 1, x == null ? null : STREAM_DATA_MARKER);
            }

         }
      } catch (CJException var8) {
         throw SQLExceptionsMapping.translateException(var8, this.getExceptionInterceptor());
      }
   }

   public void updateBlob(String columnLabel, Blob blob) throws SQLException {
      try {
         this.updateBlob(this.findColumn(columnLabel), blob);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateBlob(int columnIndex, Blob blob) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
               if (!this.doingUpdates) {
                  this.doingUpdates = true;
                  this.syncUpdate();
               }

               this.updater.setBlob(columnIndex, blob);
            } else {
               this.inserter.setBlob(columnIndex, blob);
               this.thisRow.setBytes(columnIndex - 1, blob == null ? null : STREAM_DATA_MARKER);
            }

         }
      } catch (CJException var7) {
         throw SQLExceptionsMapping.translateException(var7, this.getExceptionInterceptor());
      }
   }

   public void updateBoolean(String columnLabel, boolean x) throws SQLException {
      try {
         this.updateBoolean(this.findColumn(columnLabel), x);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateBoolean(int columnIndex, boolean x) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
               if (!this.doingUpdates) {
                  this.doingUpdates = true;
                  this.syncUpdate();
               }

               this.updater.setBoolean(columnIndex, x);
            } else {
               this.inserter.setBoolean(columnIndex, x);
               this.thisRow.setBytes(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex));
            }

         }
      } catch (CJException var7) {
         throw SQLExceptionsMapping.translateException(var7, this.getExceptionInterceptor());
      }
   }

   public void updateByte(String columnLabel, byte x) throws SQLException {
      try {
         this.updateByte(this.findColumn(columnLabel), x);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateByte(int columnIndex, byte x) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
               if (!this.doingUpdates) {
                  this.doingUpdates = true;
                  this.syncUpdate();
               }

               this.updater.setByte(columnIndex, x);
            } else {
               this.inserter.setByte(columnIndex, x);
               this.thisRow.setBytes(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex));
            }

         }
      } catch (CJException var7) {
         throw SQLExceptionsMapping.translateException(var7, this.getExceptionInterceptor());
      }
   }

   public void updateBytes(String columnLabel, byte[] x) throws SQLException {
      try {
         this.updateBytes(this.findColumn(columnLabel), x);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateBytes(int columnIndex, byte[] x) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
               if (!this.doingUpdates) {
                  this.doingUpdates = true;
                  this.syncUpdate();
               }

               this.updater.setBytes(columnIndex, x);
            } else {
               this.inserter.setBytes(columnIndex, x);
               this.thisRow.setBytes(columnIndex - 1, x);
            }

         }
      } catch (CJException var7) {
         throw SQLExceptionsMapping.translateException(var7, this.getExceptionInterceptor());
      }
   }

   public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
      try {
         this.updateCharacterStream(this.findColumn(columnLabel), reader, length);
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
               if (!this.doingUpdates) {
                  this.doingUpdates = true;
                  this.syncUpdate();
               }

               this.updater.setCharacterStream(columnIndex, x, length);
            } else {
               this.inserter.setCharacterStream(columnIndex, x, length);
               this.thisRow.setBytes(columnIndex - 1, x == null ? null : STREAM_DATA_MARKER);
            }

         }
      } catch (CJException var8) {
         throw SQLExceptionsMapping.translateException(var8, this.getExceptionInterceptor());
      }
   }

   public void updateClob(String columnLabel, Clob clob) throws SQLException {
      try {
         this.updateClob(this.findColumn(columnLabel), clob);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateClob(int columnIndex, Clob clob) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (clob == null) {
               this.updateNull(columnIndex);
            } else {
               this.updateCharacterStream(columnIndex, clob.getCharacterStream(), (int)clob.length());
            }

         }
      } catch (CJException var7) {
         throw SQLExceptionsMapping.translateException(var7, this.getExceptionInterceptor());
      }
   }

   public void updateDate(String columnLabel, Date x) throws SQLException {
      try {
         this.updateDate(this.findColumn(columnLabel), x);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateDate(int columnIndex, Date x) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
               if (!this.doingUpdates) {
                  this.doingUpdates = true;
                  this.syncUpdate();
               }

               this.updater.setDate(columnIndex, x);
            } else {
               this.inserter.setDate(columnIndex, x);
               this.thisRow.setBytes(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex));
            }

         }
      } catch (CJException var7) {
         throw SQLExceptionsMapping.translateException(var7, this.getExceptionInterceptor());
      }
   }

   public void updateDouble(String columnLabel, double x) throws SQLException {
      try {
         this.updateDouble(this.findColumn(columnLabel), x);
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void updateDouble(int columnIndex, double x) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
               if (!this.doingUpdates) {
                  this.doingUpdates = true;
                  this.syncUpdate();
               }

               this.updater.setDouble(columnIndex, x);
            } else {
               this.inserter.setDouble(columnIndex, x);
               this.thisRow.setBytes(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex));
            }

         }
      } catch (CJException var8) {
         throw SQLExceptionsMapping.translateException(var8, this.getExceptionInterceptor());
      }
   }

   public void updateFloat(String columnLabel, float x) throws SQLException {
      try {
         this.updateFloat(this.findColumn(columnLabel), x);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateFloat(int columnIndex, float x) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
               if (!this.doingUpdates) {
                  this.doingUpdates = true;
                  this.syncUpdate();
               }

               this.updater.setFloat(columnIndex, x);
            } else {
               this.inserter.setFloat(columnIndex, x);
               this.thisRow.setBytes(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex));
            }

         }
      } catch (CJException var7) {
         throw SQLExceptionsMapping.translateException(var7, this.getExceptionInterceptor());
      }
   }

   public void updateInt(String columnLabel, int x) throws SQLException {
      try {
         this.updateInt(this.findColumn(columnLabel), x);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateInt(int columnIndex, int x) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
               if (!this.doingUpdates) {
                  this.doingUpdates = true;
                  this.syncUpdate();
               }

               this.updater.setInt(columnIndex, x);
            } else {
               this.inserter.setInt(columnIndex, x);
               this.thisRow.setBytes(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex));
            }

         }
      } catch (CJException var7) {
         throw SQLExceptionsMapping.translateException(var7, this.getExceptionInterceptor());
      }
   }

   public void updateLong(String columnLabel, long x) throws SQLException {
      try {
         this.updateLong(this.findColumn(columnLabel), x);
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void updateLong(int columnIndex, long x) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
               if (!this.doingUpdates) {
                  this.doingUpdates = true;
                  this.syncUpdate();
               }

               this.updater.setLong(columnIndex, x);
            } else {
               this.inserter.setLong(columnIndex, x);
               this.thisRow.setBytes(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex));
            }

         }
      } catch (CJException var8) {
         throw SQLExceptionsMapping.translateException(var8, this.getExceptionInterceptor());
      }
   }

   public void updateNull(String columnLabel) throws SQLException {
      try {
         this.updateNull(this.findColumn(columnLabel));
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public void updateNull(int columnIndex) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
               if (!this.doingUpdates) {
                  this.doingUpdates = true;
                  this.syncUpdate();
               }

               this.updater.setNull(columnIndex, 0);
            } else {
               this.inserter.setNull(columnIndex, 0);
               this.thisRow.setBytes(columnIndex - 1, (byte[])null);
            }

         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void updateObject(String columnLabel, Object x) throws SQLException {
      try {
         this.updateObject(this.findColumn(columnLabel), x);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateObject(int columnIndex, Object x) throws SQLException {
      try {
         this.updateObjectInternal(columnIndex, x, (Integer)((Integer)null), 0);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateObject(String columnLabel, Object x, int scale) throws SQLException {
      try {
         this.updateObject(this.findColumn(columnLabel), x, scale);
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void updateObject(int columnIndex, Object x, int scale) throws SQLException {
      try {
         this.updateObjectInternal(columnIndex, x, (Integer)null, scale);
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   protected void updateObjectInternal(int columnIndex, Object x, Integer targetType, int scaleOrLength) throws SQLException {
      try {
         MysqlType targetMysqlType = targetType == null ? null : MysqlType.getByJdbcType(targetType);
         this.updateObjectInternal(columnIndex, x, (SQLType)targetMysqlType, scaleOrLength);
      } catch (FeatureNotAvailableException var6) {
         throw SQLError.createSQLFeatureNotSupportedException(Messages.getString("Statement.UnsupportedSQLType") + JDBCType.valueOf(targetType), "S1C00", this.getExceptionInterceptor());
      }
   }

   protected void updateObjectInternal(int columnIndex, Object x, SQLType targetType, int scaleOrLength) throws SQLException {
      synchronized(this.checkClosed().getConnectionMutex()) {
         if (!this.onInsertRow) {
            if (!this.doingUpdates) {
               this.doingUpdates = true;
               this.syncUpdate();
            }

            if (targetType == null) {
               this.updater.setObject(columnIndex, x);
            } else {
               this.updater.setObject(columnIndex, x, targetType);
            }
         } else {
            if (targetType == null) {
               this.inserter.setObject(columnIndex, x);
            } else {
               this.inserter.setObject(columnIndex, x, targetType);
            }

            this.thisRow.setBytes(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex));
         }

      }
   }

   public void updateObject(String columnLabel, Object x, SQLType targetSqlType) throws SQLException {
      try {
         this.updateObject(this.findColumn(columnLabel), x, targetSqlType);
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void updateObject(int columnIndex, Object x, SQLType targetSqlType) throws SQLException {
      try {
         this.updateObjectInternal(columnIndex, x, (SQLType)targetSqlType, 0);
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void updateObject(String columnLabel, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
      try {
         this.updateObject(this.findColumn(columnLabel), x, targetSqlType, scaleOrLength);
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void updateObject(int columnIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
      try {
         this.updateObjectInternal(columnIndex, x, targetSqlType, scaleOrLength);
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void updateShort(String columnLabel, short x) throws SQLException {
      try {
         this.updateShort(this.findColumn(columnLabel), x);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateShort(int columnIndex, short x) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
               if (!this.doingUpdates) {
                  this.doingUpdates = true;
                  this.syncUpdate();
               }

               this.updater.setShort(columnIndex, x);
            } else {
               this.inserter.setShort(columnIndex, x);
               this.thisRow.setBytes(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex));
            }

         }
      } catch (CJException var7) {
         throw SQLExceptionsMapping.translateException(var7, this.getExceptionInterceptor());
      }
   }

   public void updateString(String columnLabel, String x) throws SQLException {
      try {
         this.updateString(this.findColumn(columnLabel), x);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateString(int columnIndex, String x) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
               if (!this.doingUpdates) {
                  this.doingUpdates = true;
                  this.syncUpdate();
               }

               this.updater.setString(columnIndex, x);
            } else {
               this.inserter.setString(columnIndex, x);
               this.thisRow.setBytes(columnIndex - 1, x == null ? null : StringUtils.getBytes(x, this.charEncoding));
            }

         }
      } catch (CJException var7) {
         throw SQLExceptionsMapping.translateException(var7, this.getExceptionInterceptor());
      }
   }

   public void updateTime(String columnLabel, Time x) throws SQLException {
      try {
         this.updateTime(this.findColumn(columnLabel), x);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateTime(int columnIndex, Time x) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
               if (!this.doingUpdates) {
                  this.doingUpdates = true;
                  this.syncUpdate();
               }

               this.updater.setTime(columnIndex, x);
            } else {
               this.inserter.setTime(columnIndex, x);
               this.thisRow.setBytes(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex));
            }

         }
      } catch (CJException var7) {
         throw SQLExceptionsMapping.translateException(var7, this.getExceptionInterceptor());
      }
   }

   public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
      try {
         this.updateTimestamp(this.findColumn(columnLabel), x);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
               if (!this.doingUpdates) {
                  this.doingUpdates = true;
                  this.syncUpdate();
               }

               this.updater.setTimestamp(columnIndex, x);
            } else {
               this.inserter.setTimestamp(columnIndex, x);
               this.thisRow.setBytes(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex));
            }

         }
      } catch (CJException var7) {
         throw SQLExceptionsMapping.translateException(var7, this.getExceptionInterceptor());
      }
   }

   public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
      try {
         this.updateAsciiStream(this.findColumn(columnLabel), x);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
      try {
         if (!this.onInsertRow) {
            if (!this.doingUpdates) {
               this.doingUpdates = true;
               this.syncUpdate();
            }

            this.updater.setAsciiStream(columnIndex, x);
         } else {
            this.inserter.setAsciiStream(columnIndex, x);
            this.thisRow.setBytes(columnIndex - 1, STREAM_DATA_MARKER);
         }

      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
      try {
         this.updateAsciiStream(this.findColumn(columnLabel), x, length);
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
      try {
         if (!this.onInsertRow) {
            if (!this.doingUpdates) {
               this.doingUpdates = true;
               this.syncUpdate();
            }

            this.updater.setAsciiStream(columnIndex, x, length);
         } else {
            this.inserter.setAsciiStream(columnIndex, x, length);
            this.thisRow.setBytes(columnIndex - 1, STREAM_DATA_MARKER);
         }

      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
      try {
         this.updateBinaryStream(this.findColumn(columnLabel), x);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
      try {
         if (!this.onInsertRow) {
            if (!this.doingUpdates) {
               this.doingUpdates = true;
               this.syncUpdate();
            }

            this.updater.setBinaryStream(columnIndex, x);
         } else {
            this.inserter.setBinaryStream(columnIndex, x);
            this.thisRow.setBytes(columnIndex - 1, x == null ? null : STREAM_DATA_MARKER);
         }

      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
      try {
         this.updateBinaryStream(this.findColumn(columnLabel), x, length);
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
      try {
         if (!this.onInsertRow) {
            if (!this.doingUpdates) {
               this.doingUpdates = true;
               this.syncUpdate();
            }

            this.updater.setBinaryStream(columnIndex, x, length);
         } else {
            this.inserter.setBinaryStream(columnIndex, x, length);
            this.thisRow.setBytes(columnIndex - 1, x == null ? null : STREAM_DATA_MARKER);
         }

      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
      try {
         this.updateBlob(this.findColumn(columnLabel), inputStream);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
      try {
         if (!this.onInsertRow) {
            if (!this.doingUpdates) {
               this.doingUpdates = true;
               this.syncUpdate();
            }

            this.updater.setBlob(columnIndex, inputStream);
         } else {
            this.inserter.setBlob(columnIndex, inputStream);
            this.thisRow.setBytes(columnIndex - 1, inputStream == null ? null : STREAM_DATA_MARKER);
         }

      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
      try {
         this.updateBlob(this.findColumn(columnLabel), inputStream, length);
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
      try {
         if (!this.onInsertRow) {
            if (!this.doingUpdates) {
               this.doingUpdates = true;
               this.syncUpdate();
            }

            this.updater.setBlob(columnIndex, inputStream, length);
         } else {
            this.inserter.setBlob(columnIndex, inputStream, length);
            this.thisRow.setBytes(columnIndex - 1, inputStream == null ? null : STREAM_DATA_MARKER);
         }

      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
      try {
         this.updateCharacterStream(this.findColumn(columnLabel), reader);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
      try {
         if (!this.onInsertRow) {
            if (!this.doingUpdates) {
               this.doingUpdates = true;
               this.syncUpdate();
            }

            this.updater.setCharacterStream(columnIndex, x);
         } else {
            this.inserter.setCharacterStream(columnIndex, x);
            this.thisRow.setBytes(columnIndex - 1, x == null ? null : STREAM_DATA_MARKER);
         }

      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
      try {
         this.updateCharacterStream(this.findColumn(columnLabel), reader, length);
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
      try {
         if (!this.onInsertRow) {
            if (!this.doingUpdates) {
               this.doingUpdates = true;
               this.syncUpdate();
            }

            this.updater.setCharacterStream(columnIndex, x, length);
         } else {
            this.inserter.setCharacterStream(columnIndex, x, length);
            this.thisRow.setBytes(columnIndex - 1, x == null ? null : STREAM_DATA_MARKER);
         }

      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void updateClob(String columnLabel, Reader reader) throws SQLException {
      try {
         this.updateClob(this.findColumn(columnLabel), reader);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateClob(int columnIndex, Reader reader) throws SQLException {
      try {
         this.updateCharacterStream(columnIndex, reader);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
      try {
         this.updateClob(this.findColumn(columnLabel), reader, length);
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
      try {
         this.updateCharacterStream(columnIndex, reader, length);
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
      try {
         this.updateNCharacterStream(this.findColumn(columnLabel), reader);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
      try {
         String fieldEncoding = this.getMetadata().getFields()[columnIndex - 1].getEncoding();
         if (fieldEncoding != null && fieldEncoding.equals("UTF-8")) {
            if (!this.onInsertRow) {
               if (!this.doingUpdates) {
                  this.doingUpdates = true;
                  this.syncUpdate();
               }

               this.updater.setNCharacterStream(columnIndex, x);
            } else {
               this.inserter.setNCharacterStream(columnIndex, x);
               this.thisRow.setBytes(columnIndex - 1, x == null ? null : STREAM_DATA_MARKER);
            }

         } else {
            throw new SQLException(Messages.getString("ResultSet.16"));
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
      try {
         this.updateNCharacterStream(this.findColumn(columnLabel), reader, length);
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            String fieldEncoding = this.getMetadata().getFields()[columnIndex - 1].getEncoding();
            if (fieldEncoding != null && fieldEncoding.equals("UTF-8")) {
               if (!this.onInsertRow) {
                  if (!this.doingUpdates) {
                     this.doingUpdates = true;
                     this.syncUpdate();
                  }

                  this.updater.setNCharacterStream(columnIndex, x, length);
               } else {
                  this.inserter.setNCharacterStream(columnIndex, x, length);
                  this.thisRow.setBytes(columnIndex - 1, x == null ? null : STREAM_DATA_MARKER);
               }

            } else {
               throw new SQLException(Messages.getString("ResultSet.16"));
            }
         }
      } catch (CJException var10) {
         throw SQLExceptionsMapping.translateException(var10, this.getExceptionInterceptor());
      }
   }

   public void updateNClob(String columnLabel, Reader reader) throws SQLException {
      try {
         this.updateNClob(this.findColumn(columnLabel), reader);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateNClob(int columnIndex, Reader reader) throws SQLException {
      try {
         String fieldEncoding = this.getMetadata().getFields()[columnIndex - 1].getEncoding();
         if (fieldEncoding != null && fieldEncoding.equals("UTF-8")) {
            this.updateCharacterStream(columnIndex, reader);
         } else {
            throw new SQLException(Messages.getString("ResultSet.17"));
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
      try {
         this.updateNClob(this.findColumn(columnLabel), reader, length);
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
      try {
         String fieldEncoding = this.getMetadata().getFields()[columnIndex - 1].getEncoding();
         if (fieldEncoding != null && fieldEncoding.equals("UTF-8")) {
            this.updateCharacterStream(columnIndex, reader, length);
         } else {
            throw new SQLException(Messages.getString("ResultSet.17"));
         }
      } catch (CJException var7) {
         throw SQLExceptionsMapping.translateException(var7, this.getExceptionInterceptor());
      }
   }

   public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
      try {
         this.updateNClob(this.findColumn(columnLabel), nClob);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            String fieldEncoding = this.getMetadata().getFields()[columnIndex - 1].getEncoding();
            if (fieldEncoding != null && fieldEncoding.equals("UTF-8")) {
               if (nClob == null) {
                  this.updateNull(columnIndex);
               } else {
                  this.updateNCharacterStream(columnIndex, nClob.getCharacterStream(), (long)((int)nClob.length()));
               }

            } else {
               throw new SQLException(Messages.getString("ResultSet.17"));
            }
         }
      } catch (CJException var8) {
         throw SQLExceptionsMapping.translateException(var8, this.getExceptionInterceptor());
      }
   }

   public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
      try {
         this.updateSQLXML(this.findColumn(columnLabel), xmlObject);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
      try {
         this.updateString(columnIndex, ((MysqlSQLXML)xmlObject).getString());
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateNString(String columnLabel, String x) throws SQLException {
      try {
         this.updateNString(this.findColumn(columnLabel), x);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateNString(int columnIndex, String x) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            String fieldEncoding = this.getMetadata().getFields()[columnIndex - 1].getEncoding();
            if (fieldEncoding != null && fieldEncoding.equals("UTF-8")) {
               if (!this.onInsertRow) {
                  if (!this.doingUpdates) {
                     this.doingUpdates = true;
                     this.syncUpdate();
                  }

                  this.updater.setNString(columnIndex, x);
               } else {
                  this.inserter.setNString(columnIndex, x);
                  this.thisRow.setBytes(columnIndex - 1, x == null ? null : StringUtils.getBytes(x, fieldEncoding));
               }

            } else {
               throw new SQLException(Messages.getString("ResultSet.18"));
            }
         }
      } catch (CJException var8) {
         throw SQLExceptionsMapping.translateException(var8, this.getExceptionInterceptor());
      }
   }

   public Reader getNCharacterStream(String columnLabel) throws SQLException {
      try {
         return this.getNCharacterStream(this.findColumn(columnLabel));
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public Reader getNCharacterStream(int columnIndex) throws SQLException {
      try {
         String fieldEncoding = this.getMetadata().getFields()[columnIndex - 1].getEncoding();
         if (fieldEncoding != null && fieldEncoding.equals("UTF-8")) {
            return this.getCharacterStream(columnIndex);
         } else {
            throw new SQLException(Messages.getString("ResultSet.11"));
         }
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public NClob getNClob(String columnLabel) throws SQLException {
      try {
         return this.getNClob(this.findColumn(columnLabel));
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public NClob getNClob(int columnIndex) throws SQLException {
      try {
         String fieldEncoding = this.getMetadata().getFields()[columnIndex - 1].getEncoding();
         if (fieldEncoding != null && fieldEncoding.equals("UTF-8")) {
            String asString = this.getStringForNClob(columnIndex);
            return asString == null ? null : new com.mysql.cj.jdbc.NClob(asString, this.getExceptionInterceptor());
         } else {
            throw new SQLException("Can not call getNClob() when field's charset isn't UTF-8");
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public String getNString(String columnLabel) throws SQLException {
      try {
         return this.getNString(this.findColumn(columnLabel));
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public String getNString(int columnIndex) throws SQLException {
      try {
         String fieldEncoding = this.getMetadata().getFields()[columnIndex - 1].getEncoding();
         if (fieldEncoding != null && fieldEncoding.equals("UTF-8")) {
            return this.getString(columnIndex);
         } else {
            throw new SQLException("Can not call getNString() when field's charset isn't UTF-8");
         }
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public SQLXML getSQLXML(String columnLabel) throws SQLException {
      try {
         return this.getSQLXML(this.findColumn(columnLabel));
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public SQLXML getSQLXML(int columnIndex) throws SQLException {
      try {
         return new MysqlSQLXML(this, columnIndex, this.getExceptionInterceptor());
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   private String getStringForNClob(int columnIndex) throws SQLException {
      String asString = null;
      String forcedEncoding = "UTF-8";

      try {
         byte[] asBytes = null;
         byte[] asBytes = this.getBytes(columnIndex);
         if (asBytes != null) {
            asString = new String(asBytes, forcedEncoding);
         }

         return asString;
      } catch (UnsupportedEncodingException var5) {
         throw SQLError.createSQLException("Unsupported character encoding " + forcedEncoding, "S1009", this.getExceptionInterceptor());
      }
   }

   public boolean isClosed() throws SQLException {
      try {
         return this.isClosed;
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public boolean isWrapperFor(Class<?> iface) throws SQLException {
      try {
         this.checkClosed();
         return iface.isInstance(this);
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public <T> T unwrap(Class<T> iface) throws SQLException {
      try {
         try {
            return iface.cast(this);
         } catch (ClassCastException var4) {
            throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009", this.getExceptionInterceptor());
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }
}
