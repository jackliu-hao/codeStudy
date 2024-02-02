/*      */ package com.mysql.cj.jdbc.result;
/*      */ 
/*      */ import com.mysql.cj.Messages;
/*      */ import com.mysql.cj.MysqlConnection;
/*      */ import com.mysql.cj.MysqlType;
/*      */ import com.mysql.cj.NativeSession;
/*      */ import com.mysql.cj.Query;
/*      */ import com.mysql.cj.Session;
/*      */ import com.mysql.cj.WarningListener;
/*      */ import com.mysql.cj.conf.PropertyKey;
/*      */ import com.mysql.cj.conf.PropertySet;
/*      */ import com.mysql.cj.conf.RuntimeProperty;
/*      */ import com.mysql.cj.exceptions.CJException;
/*      */ import com.mysql.cj.exceptions.ExceptionFactory;
/*      */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*      */ import com.mysql.cj.jdbc.Blob;
/*      */ import com.mysql.cj.jdbc.BlobFromLocator;
/*      */ import com.mysql.cj.jdbc.Clob;
/*      */ import com.mysql.cj.jdbc.JdbcConnection;
/*      */ import com.mysql.cj.jdbc.JdbcPreparedStatement;
/*      */ import com.mysql.cj.jdbc.JdbcPropertySet;
/*      */ import com.mysql.cj.jdbc.JdbcStatement;
/*      */ import com.mysql.cj.jdbc.MysqlSQLXML;
/*      */ import com.mysql.cj.jdbc.NClob;
/*      */ import com.mysql.cj.jdbc.StatementImpl;
/*      */ import com.mysql.cj.jdbc.exceptions.NotUpdatable;
/*      */ import com.mysql.cj.jdbc.exceptions.SQLError;
/*      */ import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
/*      */ import com.mysql.cj.log.ProfilerEventHandler;
/*      */ import com.mysql.cj.protocol.ColumnDefinition;
/*      */ import com.mysql.cj.protocol.ResultsetRows;
/*      */ import com.mysql.cj.protocol.a.result.NativeResultset;
/*      */ import com.mysql.cj.protocol.a.result.OkPacket;
/*      */ import com.mysql.cj.result.BigDecimalValueFactory;
/*      */ import com.mysql.cj.result.BinaryStreamValueFactory;
/*      */ import com.mysql.cj.result.BooleanValueFactory;
/*      */ import com.mysql.cj.result.ByteValueFactory;
/*      */ import com.mysql.cj.result.DoubleValueFactory;
/*      */ import com.mysql.cj.result.DurationValueFactory;
/*      */ import com.mysql.cj.result.Field;
/*      */ import com.mysql.cj.result.FloatValueFactory;
/*      */ import com.mysql.cj.result.IntegerValueFactory;
/*      */ import com.mysql.cj.result.LocalDateTimeValueFactory;
/*      */ import com.mysql.cj.result.LocalDateValueFactory;
/*      */ import com.mysql.cj.result.LocalTimeValueFactory;
/*      */ import com.mysql.cj.result.LongValueFactory;
/*      */ import com.mysql.cj.result.OffsetDateTimeValueFactory;
/*      */ import com.mysql.cj.result.OffsetTimeValueFactory;
/*      */ import com.mysql.cj.result.Row;
/*      */ import com.mysql.cj.result.ShortValueFactory;
/*      */ import com.mysql.cj.result.SqlDateValueFactory;
/*      */ import com.mysql.cj.result.SqlTimeValueFactory;
/*      */ import com.mysql.cj.result.SqlTimestampValueFactory;
/*      */ import com.mysql.cj.result.StringValueFactory;
/*      */ import com.mysql.cj.result.UtilCalendarValueFactory;
/*      */ import com.mysql.cj.result.ValueFactory;
/*      */ import com.mysql.cj.result.ZonedDateTimeValueFactory;
/*      */ import com.mysql.cj.util.LogUtils;
/*      */ import com.mysql.cj.util.StringUtils;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.URL;
/*      */ import java.sql.Array;
/*      */ import java.sql.Blob;
/*      */ import java.sql.Clob;
/*      */ import java.sql.Date;
/*      */ import java.sql.NClob;
/*      */ import java.sql.Ref;
/*      */ import java.sql.ResultSetMetaData;
/*      */ import java.sql.RowId;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLFeatureNotSupportedException;
/*      */ import java.sql.SQLType;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.SQLXML;
/*      */ import java.sql.Statement;
/*      */ import java.sql.Struct;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.time.Duration;
/*      */ import java.time.LocalDate;
/*      */ import java.time.LocalDateTime;
/*      */ import java.time.LocalTime;
/*      */ import java.time.OffsetDateTime;
/*      */ import java.time.OffsetTime;
/*      */ import java.time.ZonedDateTime;
/*      */ import java.util.Calendar;
/*      */ import java.util.Date;
/*      */ import java.util.HashSet;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
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
/*      */ public class ResultSetImpl
/*      */   extends NativeResultset
/*      */   implements ResultSetInternalMethods, WarningListener
/*      */ {
/*  127 */   static int resultCounter = 1;
/*      */ 
/*      */   
/*  130 */   protected String db = null;
/*      */ 
/*      */   
/*  133 */   protected boolean[] columnUsed = null;
/*      */ 
/*      */   
/*      */   protected volatile JdbcConnection connection;
/*      */   
/*  138 */   protected NativeSession session = null;
/*      */ 
/*      */   
/*  141 */   protected int currentRow = -1;
/*      */   
/*  143 */   protected ProfilerEventHandler eventSink = null;
/*      */   
/*  145 */   Calendar fastDefaultCal = null;
/*  146 */   Calendar fastClientCal = null;
/*      */ 
/*      */   
/*  149 */   protected int fetchDirection = 1000;
/*      */ 
/*      */   
/*  152 */   protected int fetchSize = 0;
/*      */ 
/*      */ 
/*      */   
/*      */   protected char firstCharOfQuery;
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean isClosed = false;
/*      */ 
/*      */ 
/*      */   
/*      */   private StatementImpl owningStatement;
/*      */ 
/*      */ 
/*      */   
/*      */   private String pointOfOrigin;
/*      */ 
/*      */ 
/*      */   
/*  172 */   protected int resultSetConcurrency = 0;
/*      */ 
/*      */   
/*  175 */   protected int resultSetType = 0; JdbcPreparedStatement statementUsedForFetchingRows; protected boolean useUsageAdvisor = false; protected boolean gatherPerfMetrics = false; protected boolean scrollTolerant = false; protected Statement wrapperStatement; private boolean padCharsWithSpace = false; private boolean useColumnNamesInFindColumn; private ExceptionInterceptor exceptionInterceptor; private ValueFactory<Boolean> booleanValueFactory; private ValueFactory<Byte> byteValueFactory; private ValueFactory<Short> shortValueFactory; private ValueFactory<Integer> integerValueFactory; private ValueFactory<Long> longValueFactory; private ValueFactory<Float> floatValueFactory; private ValueFactory<Double> doubleValueFactory; private ValueFactory<BigDecimal> bigDecimalValueFactory; private ValueFactory<InputStream> binaryStreamValueFactory;
/*      */   private ValueFactory<Time> defaultTimeValueFactory;
/*      */   private ValueFactory<Timestamp> defaultTimestampValueFactory;
/*      */   private ValueFactory<Calendar> defaultUtilCalendarValueFactory;
/*      */   private ValueFactory<LocalDate> defaultLocalDateValueFactory;
/*      */   private ValueFactory<LocalDateTime> defaultLocalDateTimeValueFactory;
/*      */   private ValueFactory<LocalTime> defaultLocalTimeValueFactory;
/*      */   private ValueFactory<OffsetTime> defaultOffsetTimeValueFactory;
/*      */   private ValueFactory<OffsetDateTime> defaultOffsetDateTimeValueFactory;
/*      */   private ValueFactory<ZonedDateTime> defaultZonedDateTimeValueFactory;
/*      */   protected RuntimeProperty<Boolean> emulateLocators;
/*  186 */   protected SQLWarning warningChain = null;
/*      */   protected boolean yearIsDateType = true;
/*      */   private boolean onValidRow;
/*      */   private String invalidRowReason;
/*      */   public void initializeWithMetadata() throws SQLException { try { synchronized (checkClosed().getConnectionMutex()) { initRowsWithMetadata(); if (this.useUsageAdvisor) { this.columnUsed = new boolean[(this.columnDefinition.getFields()).length]; this.pointOfOrigin = LogUtils.findCallingClassAndMethod(new Throwable()); this.resultId = resultCounter++; this.eventSink = this.session.getProfilerEventHandler(); }
/*      */          if (this.gatherPerfMetrics) { this.session.getProtocol().getMetricsHolder().incrementNumberOfResultSetsCreated(); Set<String> tableNamesSet = new HashSet<>(); for (int i = 0; i < (this.columnDefinition.getFields()).length; i++) { Field f = this.columnDefinition.getFields()[i]; String tableName = f.getOriginalTableName(); if (tableName == null)
/*      */               tableName = f.getTableName();  if (tableName != null) { if (this.connection.lowerCaseTableNames())
/*      */                 tableName = tableName.toLowerCase();  tableNamesSet.add(tableName); }
/*      */              }
/*      */            this.session.getProtocol().getMetricsHolder().reportNumberOfTablesAccessed(tableNamesSet.size()); }
/*      */          }
/*      */        return; }
/*      */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */      } public boolean absolute(int row) throws SQLException { try { synchronized (checkClosed().getConnectionMutex()) { boolean b; if (!hasRows())
/*      */           throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", getExceptionInterceptor());  if (isStrictlyForwardOnly())
/*      */           throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly"));  if (this.rowData.size() == 0) { b = false; }
/*      */         else if (row == 0) { beforeFirst(); b = false; }
/*      */         else if (row == 1) { b = first(); }
/*      */         else if (row == -1) { b = last(); }
/*      */         else if (row > this.rowData.size()) { afterLast(); b = false; }
/*      */         else if (row < 0) { int newRowPosition = this.rowData.size() + row + 1; if (newRowPosition <= 0) { beforeFirst(); b = false; }
/*      */           else { b = absolute(newRowPosition); }
/*      */            }
/*      */         else { row--; this.rowData.setCurrentRow(row); this.thisRow = this.rowData.get(row); b = true; }
/*      */          setRowPositionValidity(); return b; }
/*      */        }
/*      */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */      } public void afterLast() throws SQLException { try {
/*      */       synchronized (checkClosed().getConnectionMutex()) {
/*      */         if (!hasRows())
/*      */           throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", getExceptionInterceptor());  if (isStrictlyForwardOnly())
/*      */           throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly"));  if (this.rowData.size() != 0) {
/*      */           this.rowData.afterLast(); this.thisRow = null;
/*      */         }  setRowPositionValidity();
/*      */       }  return;
/*      */     } catch (CJException cJException) {
/*      */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     }  } public void beforeFirst() throws SQLException { try {
/*      */       synchronized (checkClosed().getConnectionMutex()) {
/*      */         if (!hasRows())
/*      */           throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", getExceptionInterceptor());  if (isStrictlyForwardOnly())
/*      */           throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly"));  if (this.rowData.size() == 0)
/*      */           return;  this.rowData.beforeFirst(); this.thisRow = null; setRowPositionValidity();
/*      */       }  return;
/*      */     } catch (CJException cJException) {
/*      */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*  232 */     }  } public ResultSetImpl(OkPacket ok, JdbcConnection conn, StatementImpl creatorStmt) { super(ok);
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
/*      */ 
/*      */ 
/*      */     
/*  536 */     this.onValidRow = false;
/*  537 */     this.invalidRowReason = null; this.connection = conn; this.owningStatement = creatorStmt; if (this.connection != null) { this.session = (NativeSession)conn.getSession(); this.exceptionInterceptor = this.connection.getExceptionInterceptor(); this.padCharsWithSpace = ((Boolean)this.connection.getPropertySet().getBooleanProperty(PropertyKey.padCharsWithSpace).getValue()).booleanValue(); }  } public void cancelRowUpdates() throws SQLException { try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }  } protected final JdbcConnection checkClosed() throws SQLException { JdbcConnection c = this.connection; if (c == null) throw SQLError.createSQLException(Messages.getString("ResultSet.Operation_not_allowed_after_ResultSet_closed_144"), "S1000", getExceptionInterceptor());  return c; } protected final void checkColumnBounds(int columnIndex) throws SQLException { synchronized (checkClosed().getConnectionMutex()) { if (columnIndex < 1) throw SQLError.createSQLException(Messages.getString("ResultSet.Column_Index_out_of_range_low", new Object[] { Integer.valueOf(columnIndex), Integer.valueOf((this.columnDefinition.getFields()).length) }), "S1009", getExceptionInterceptor());  if (columnIndex > (this.columnDefinition.getFields()).length) throw SQLError.createSQLException(Messages.getString("ResultSet.Column_Index_out_of_range_high", new Object[] { Integer.valueOf(columnIndex), Integer.valueOf((this.columnDefinition.getFields()).length) }), "S1009", getExceptionInterceptor());  if (this.useUsageAdvisor) this.columnUsed[columnIndex - 1] = true;  }  } protected void checkRowPos() throws SQLException { checkClosed(); if (!this.onValidRow) throw SQLError.createSQLException(this.invalidRowReason, "S1000", getExceptionInterceptor());  } public ResultSetImpl(ResultsetRows tuples, JdbcConnection conn, StatementImpl creatorStmt) throws SQLException { this.onValidRow = false; this.invalidRowReason = null; this.connection = conn; this.session = (NativeSession)conn.getSession(); this.db = (creatorStmt != null) ? creatorStmt.getCurrentDatabase() : conn.getDatabase(); this.owningStatement = creatorStmt; this.exceptionInterceptor = this.connection.getExceptionInterceptor(); JdbcPropertySet jdbcPropertySet = this.connection.getPropertySet(); this.emulateLocators = jdbcPropertySet.getBooleanProperty(PropertyKey.emulateLocators); this.padCharsWithSpace = ((Boolean)jdbcPropertySet.getBooleanProperty(PropertyKey.padCharsWithSpace).getValue()).booleanValue(); this.yearIsDateType = ((Boolean)jdbcPropertySet.getBooleanProperty(PropertyKey.yearIsDateType).getValue()).booleanValue(); this.useUsageAdvisor = ((Boolean)jdbcPropertySet.getBooleanProperty(PropertyKey.useUsageAdvisor).getValue()).booleanValue(); this.gatherPerfMetrics = ((Boolean)jdbcPropertySet.getBooleanProperty(PropertyKey.gatherPerfMetrics).getValue()).booleanValue(); this.scrollTolerant = ((Boolean)jdbcPropertySet.getBooleanProperty(PropertyKey.scrollTolerantForwardOnly).getValue()).booleanValue(); this.booleanValueFactory = (ValueFactory<Boolean>)new BooleanValueFactory((PropertySet)jdbcPropertySet); this.byteValueFactory = (ValueFactory<Byte>)new ByteValueFactory((PropertySet)jdbcPropertySet); this.shortValueFactory = (ValueFactory<Short>)new ShortValueFactory((PropertySet)jdbcPropertySet); this.integerValueFactory = (ValueFactory<Integer>)new IntegerValueFactory((PropertySet)jdbcPropertySet); this.longValueFactory = (ValueFactory<Long>)new LongValueFactory((PropertySet)jdbcPropertySet); this.floatValueFactory = (ValueFactory<Float>)new FloatValueFactory((PropertySet)jdbcPropertySet); this.doubleValueFactory = (ValueFactory<Double>)new DoubleValueFactory((PropertySet)jdbcPropertySet); this.bigDecimalValueFactory = (ValueFactory<BigDecimal>)new BigDecimalValueFactory((PropertySet)jdbcPropertySet); this.binaryStreamValueFactory = (ValueFactory<InputStream>)new BinaryStreamValueFactory((PropertySet)jdbcPropertySet); this.defaultTimeValueFactory = (ValueFactory<Time>)new SqlTimeValueFactory((PropertySet)jdbcPropertySet, null, this.session.getServerSession().getDefaultTimeZone(), this); this.defaultTimestampValueFactory = (ValueFactory<Timestamp>)new SqlTimestampValueFactory((PropertySet)jdbcPropertySet, null, this.session.getServerSession().getDefaultTimeZone(), this.session.getServerSession().getSessionTimeZone()); this.defaultUtilCalendarValueFactory = (ValueFactory<Calendar>)new UtilCalendarValueFactory((PropertySet)jdbcPropertySet, this.session.getServerSession().getDefaultTimeZone(), this.session.getServerSession().getSessionTimeZone()); this.defaultLocalDateValueFactory = (ValueFactory<LocalDate>)new LocalDateValueFactory((PropertySet)jdbcPropertySet, this); this.defaultLocalTimeValueFactory = (ValueFactory<LocalTime>)new LocalTimeValueFactory((PropertySet)jdbcPropertySet, this); this.defaultLocalDateTimeValueFactory = (ValueFactory<LocalDateTime>)new LocalDateTimeValueFactory((PropertySet)jdbcPropertySet); this.defaultOffsetTimeValueFactory = (ValueFactory<OffsetTime>)new OffsetTimeValueFactory((PropertySet)jdbcPropertySet, this.session.getProtocol().getServerSession().getDefaultTimeZone()); this.defaultOffsetDateTimeValueFactory = (ValueFactory<OffsetDateTime>)new OffsetDateTimeValueFactory((PropertySet)jdbcPropertySet, this.session.getProtocol().getServerSession().getDefaultTimeZone(), this.session.getProtocol().getServerSession().getSessionTimeZone()); this.defaultZonedDateTimeValueFactory = (ValueFactory<ZonedDateTime>)new ZonedDateTimeValueFactory((PropertySet)jdbcPropertySet, this.session.getProtocol().getServerSession().getDefaultTimeZone(), this.session.getProtocol().getServerSession().getSessionTimeZone()); this.columnDefinition = tuples.getMetadata(); this.rowData = tuples; this.updateCount = this.rowData.size(); if (this.rowData.size() > 0) { if (this.updateCount == 1L && this.thisRow == null) { this.rowData.close(); this.updateCount = -1L; }  }
/*      */     else { this.thisRow = null; }
/*      */      this.rowData.setOwner(this); if (this.columnDefinition.getFields() != null)
/*  540 */       initializeWithMetadata();  this.useColumnNamesInFindColumn = ((Boolean)jdbcPropertySet.getBooleanProperty(PropertyKey.useColumnNamesInFindColumn).getValue()).booleanValue(); setRowPositionValidity(); } private void setRowPositionValidity() throws SQLException { if (!this.rowData.isDynamic() && this.rowData.size() == 0) {
/*  541 */       this.invalidRowReason = Messages.getString("ResultSet.Illegal_operation_on_empty_result_set");
/*  542 */       this.onValidRow = false;
/*  543 */     } else if (this.rowData.isBeforeFirst()) {
/*  544 */       this.invalidRowReason = Messages.getString("ResultSet.Before_start_of_result_set_146");
/*  545 */       this.onValidRow = false;
/*  546 */     } else if (this.rowData.isAfterLast()) {
/*  547 */       this.invalidRowReason = Messages.getString("ResultSet.After_end_of_result_set_148");
/*  548 */       this.onValidRow = false;
/*      */     } else {
/*  550 */       this.onValidRow = true;
/*  551 */       this.invalidRowReason = null;
/*      */     }  }
/*      */ 
/*      */   
/*      */   public void clearWarnings() throws SQLException {
/*      */     
/*  557 */     try { synchronized (checkClosed().getConnectionMutex()) {
/*  558 */         this.warningChain = null;
/*      */       }  return; }
/*  560 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void close() throws SQLException {
/*      */     
/*  564 */     try { realClose(true); return; }
/*  565 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void populateCachedMetaData(CachedResultSetMetaData cachedMetaData) throws SQLException {
/*      */     
/*  569 */     try { this.columnDefinition.exportTo(cachedMetaData);
/*  570 */       cachedMetaData.setMetadata(getMetaData()); return; }
/*  571 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void deleteRow() throws SQLException {
/*      */     
/*  575 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public int findColumn(String columnName) throws SQLException {
/*      */     
/*  580 */     try { synchronized (checkClosed().getConnectionMutex())
/*  581 */       { Integer index = Integer.valueOf(this.columnDefinition.findColumn(columnName, this.useColumnNamesInFindColumn, 1));
/*      */         
/*  583 */         if (index.intValue() == -1) {
/*  584 */           throw SQLError.createSQLException(
/*  585 */               Messages.getString("ResultSet.Column____112") + columnName + Messages.getString("ResultSet.___not_found._113"), "S0022", 
/*  586 */               getExceptionInterceptor());
/*      */         }
/*      */         
/*  589 */         return index.intValue(); }  }
/*  590 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean first() throws SQLException {
/*      */     
/*  595 */     try { synchronized (checkClosed().getConnectionMutex())
/*  596 */       { if (!hasRows()) {
/*  597 */           throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", 
/*  598 */               getExceptionInterceptor());
/*      */         }
/*      */         
/*  601 */         if (isStrictlyForwardOnly()) {
/*  602 */           throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly"));
/*      */         }
/*      */         
/*  605 */         boolean b = true;
/*      */         
/*  607 */         if (this.rowData.isEmpty()) {
/*  608 */           b = false;
/*      */         } else {
/*  610 */           this.rowData.beforeFirst();
/*  611 */           this.thisRow = (Row)this.rowData.next();
/*      */         } 
/*      */         
/*  614 */         setRowPositionValidity();
/*      */         
/*  616 */         return b; }  }
/*  617 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Array getArray(int columnIndex) throws SQLException {
/*      */     
/*  622 */     try { checkRowPos();
/*  623 */       checkColumnBounds(columnIndex);
/*  624 */       throw SQLError.createSQLFeatureNotSupportedException(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Array getArray(String colName) throws SQLException {
/*      */     
/*  629 */     try { return getArray(findColumn(colName)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public InputStream getAsciiStream(int columnIndex) throws SQLException {
/*      */     
/*  634 */     try { checkRowPos();
/*  635 */       checkColumnBounds(columnIndex);
/*  636 */       return getBinaryStream(columnIndex); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public InputStream getAsciiStream(String columnName) throws SQLException {
/*      */     
/*  641 */     try { return getAsciiStream(findColumn(columnName)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
/*      */     
/*  646 */     try { checkRowPos();
/*  647 */       checkColumnBounds(columnIndex);
/*  648 */       return (BigDecimal)this.thisRow.getValue(columnIndex - 1, this.bigDecimalValueFactory); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   @Deprecated
/*      */   public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
/*      */     
/*  654 */     try { checkRowPos();
/*  655 */       checkColumnBounds(columnIndex);
/*  656 */       BigDecimalValueFactory bigDecimalValueFactory = new BigDecimalValueFactory(this.session.getPropertySet(), scale);
/*  657 */       bigDecimalValueFactory.setPropertySet((PropertySet)this.connection.getPropertySet());
/*  658 */       return (BigDecimal)this.thisRow.getValue(columnIndex - 1, (ValueFactory)bigDecimalValueFactory); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public BigDecimal getBigDecimal(String columnName) throws SQLException {
/*      */     
/*  663 */     try { return getBigDecimal(findColumn(columnName)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   @Deprecated
/*      */   public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException {
/*      */     
/*  669 */     try { return getBigDecimal(findColumn(columnName), scale); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public InputStream getBinaryStream(int columnIndex) throws SQLException {
/*      */     
/*  674 */     try { checkRowPos();
/*  675 */       checkColumnBounds(columnIndex);
/*  676 */       return (InputStream)this.thisRow.getValue(columnIndex - 1, this.binaryStreamValueFactory); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public InputStream getBinaryStream(String columnName) throws SQLException {
/*      */     
/*  681 */     try { return getBinaryStream(findColumn(columnName)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Blob getBlob(int columnIndex) throws SQLException {
/*      */     
/*  686 */     try { checkRowPos();
/*  687 */       checkColumnBounds(columnIndex);
/*      */       
/*  689 */       if (this.thisRow.getNull(columnIndex - 1)) {
/*  690 */         return null;
/*      */       }
/*      */       
/*  693 */       if (!((Boolean)this.emulateLocators.getValue()).booleanValue()) {
/*  694 */         return (Blob)new Blob(this.thisRow.getBytes(columnIndex - 1), getExceptionInterceptor());
/*      */       }
/*      */       
/*  697 */       return (Blob)new BlobFromLocator(this, columnIndex, getExceptionInterceptor()); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Blob getBlob(String colName) throws SQLException {
/*      */     
/*  702 */     try { return getBlob(findColumn(colName)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean getBoolean(int columnIndex) throws SQLException {
/*      */     
/*  707 */     try { Boolean res = getObject(columnIndex, (Class)boolean.class);
/*  708 */       return (res == null) ? false : res.booleanValue(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean getBoolean(String columnName) throws SQLException {
/*      */     
/*  713 */     try { return getBoolean(findColumn(columnName)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public byte getByte(int columnIndex) throws SQLException {
/*      */     
/*  718 */     try { Byte res = getObject(columnIndex, (Class)byte.class);
/*  719 */       return (res == null) ? 0 : res.byteValue(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public byte getByte(String columnName) throws SQLException {
/*      */     
/*  724 */     try { return getByte(findColumn(columnName)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public byte[] getBytes(int columnIndex) throws SQLException {
/*      */     
/*  729 */     try { checkRowPos();
/*  730 */       checkColumnBounds(columnIndex);
/*  731 */       return this.thisRow.getBytes(columnIndex - 1); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public byte[] getBytes(String columnName) throws SQLException {
/*      */     
/*  736 */     try { return getBytes(findColumn(columnName)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Reader getCharacterStream(int columnIndex) throws SQLException {
/*      */     
/*  741 */     try { checkRowPos();
/*  742 */       checkColumnBounds(columnIndex);
/*  743 */       InputStream stream = getBinaryStream(columnIndex);
/*      */       
/*  745 */       if (stream == null) {
/*  746 */         return null;
/*      */       }
/*      */       
/*  749 */       Field f = this.columnDefinition.getFields()[columnIndex - 1];
/*      */       
/*  751 */       try { return new InputStreamReader(stream, f.getEncoding()); }
/*  752 */       catch (UnsupportedEncodingException e)
/*  753 */       { SQLException sqlEx = SQLError.createSQLException("Cannot read value with encoding: " + f.getEncoding(), this.exceptionInterceptor);
/*  754 */         sqlEx.initCause(e);
/*  755 */         throw sqlEx; }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public Reader getCharacterStream(String columnName) throws SQLException {
/*      */     
/*  761 */     try { return getCharacterStream(findColumn(columnName)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Clob getClob(int columnIndex) throws SQLException {
/*      */     
/*  766 */     try { String asString = getStringForClob(columnIndex);
/*      */       
/*  768 */       if (asString == null) {
/*  769 */         return null;
/*      */       }
/*      */       
/*  772 */       return (Clob)new Clob(asString, getExceptionInterceptor()); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Clob getClob(String colName) throws SQLException {
/*      */     
/*  777 */     try { return getClob(findColumn(colName)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public Date getDate(int columnIndex) throws SQLException { try {
/*  782 */       checkRowPos();
/*  783 */       checkColumnBounds(columnIndex);
/*  784 */       return (Date)this.thisRow.getValue(columnIndex - 1, (ValueFactory)new SqlDateValueFactory(this.session
/*  785 */             .getPropertySet(), null, this.session.getServerSession().getDefaultTimeZone(), this));
/*      */     } catch (CJException cJException) {
/*      */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     }  }
/*      */   public Date getDate(int columnIndex, Calendar cal) throws SQLException { try {
/*  790 */       checkRowPos();
/*  791 */       checkColumnBounds(columnIndex);
/*  792 */       return (Date)this.thisRow.getValue(columnIndex - 1, (ValueFactory)new SqlDateValueFactory(this.session.getPropertySet(), cal, (cal != null) ? cal
/*  793 */             .getTimeZone() : this.session.getServerSession().getDefaultTimeZone(), this));
/*      */     } catch (CJException cJException) {
/*      */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     }  } public Date getDate(String columnName) throws SQLException {
/*      */     
/*  798 */     try { return getDate(findColumn(columnName)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Date getDate(String columnName, Calendar cal) throws SQLException {
/*      */     
/*  803 */     try { return getDate(findColumn(columnName), cal); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public double getDouble(int columnIndex) throws SQLException {
/*      */     
/*  808 */     try { Double res = getObject(columnIndex, (Class)double.class);
/*  809 */       return (res == null) ? 0.0D : res.doubleValue(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public double getDouble(String columnName) throws SQLException {
/*      */     
/*  814 */     try { return getDouble(findColumn(columnName)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public float getFloat(int columnIndex) throws SQLException {
/*      */     
/*  819 */     try { Float res = getObject(columnIndex, (Class)float.class);
/*  820 */       return (res == null) ? 0.0F : res.floatValue(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public float getFloat(String columnName) throws SQLException {
/*      */     
/*  825 */     try { return getFloat(findColumn(columnName)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public int getInt(int columnIndex) throws SQLException {
/*      */     
/*  830 */     try { Integer res = getObject(columnIndex, (Class)int.class);
/*  831 */       return (res == null) ? 0 : res.intValue(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public BigInteger getBigInteger(int columnIndex) throws SQLException { try {
/*  836 */       String stringVal = getString(columnIndex);
/*  837 */       if (stringVal == null) {
/*  838 */         return null;
/*      */       }
/*      */       try {
/*  841 */         return new BigInteger(stringVal);
/*  842 */       } catch (NumberFormatException nfe) {
/*  843 */         throw SQLError.createSQLException(
/*  844 */             Messages.getString("ResultSet.Bad_format_for_BigInteger", new Object[] { Integer.valueOf(columnIndex), stringVal }), "S1009", 
/*  845 */             getExceptionInterceptor());
/*      */       } 
/*      */     } catch (CJException cJException) {
/*      */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     }  } public int getInt(String columnName) throws SQLException {
/*      */     
/*  851 */     try { return getInt(findColumn(columnName)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public long getLong(int columnIndex) throws SQLException {
/*      */     
/*  856 */     try { Long res = getObject(columnIndex, (Class)long.class);
/*  857 */       return (res == null) ? 0L : res.longValue(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public long getLong(String columnName) throws SQLException {
/*      */     
/*  862 */     try { return getLong(findColumn(columnName)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public short getShort(int columnIndex) throws SQLException {
/*      */     
/*  867 */     try { Short res = getObject(columnIndex, (Class)short.class);
/*  868 */       return (res == null) ? 0 : res.shortValue(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public short getShort(String columnName) throws SQLException {
/*      */     
/*  873 */     try { return getShort(findColumn(columnName)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public String getString(int columnIndex) throws SQLException {
/*      */     
/*  878 */     try { checkRowPos();
/*  879 */       checkColumnBounds(columnIndex);
/*      */       
/*  881 */       Field f = this.columnDefinition.getFields()[columnIndex - 1];
/*  882 */       StringValueFactory stringValueFactory = new StringValueFactory(this.session.getPropertySet());
/*  883 */       String stringVal = (String)this.thisRow.getValue(columnIndex - 1, (ValueFactory)stringValueFactory);
/*      */       
/*  885 */       if (this.padCharsWithSpace && stringVal != null && f.getMysqlTypeId() == 254) {
/*  886 */         int maxBytesPerChar = this.session.getServerSession().getCharsetSettings().getMaxBytesPerChar(Integer.valueOf(f.getCollationIndex()), f.getEncoding());
/*  887 */         int fieldLength = (int)f.getLength() / maxBytesPerChar;
/*  888 */         return StringUtils.padString(stringVal, fieldLength);
/*      */       } 
/*      */       
/*  891 */       return stringVal; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public String getString(String columnName) throws SQLException {
/*      */     
/*  896 */     try { return getString(findColumn(columnName)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   private String getStringForClob(int columnIndex) throws SQLException {
/*  900 */     String asString = null;
/*      */     
/*  902 */     String forcedEncoding = this.connection.getPropertySet().getStringProperty(PropertyKey.clobCharacterEncoding).getStringValue();
/*      */     
/*  904 */     if (forcedEncoding == null) {
/*  905 */       asString = getString(columnIndex);
/*      */     } else {
/*  907 */       byte[] asBytes = getBytes(columnIndex);
/*      */       
/*  909 */       if (asBytes != null) {
/*  910 */         asString = StringUtils.toString(asBytes, forcedEncoding);
/*      */       }
/*      */     } 
/*      */     
/*  914 */     return asString;
/*      */   }
/*      */   
/*      */   public Time getTime(int columnIndex) throws SQLException {
/*      */     
/*  919 */     try { checkRowPos();
/*  920 */       checkColumnBounds(columnIndex);
/*  921 */       return (Time)this.thisRow.getValue(columnIndex - 1, this.defaultTimeValueFactory); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Time getTime(int columnIndex, Calendar cal) throws SQLException {
/*      */     
/*  926 */     try { checkRowPos();
/*  927 */       checkColumnBounds(columnIndex);
/*      */       
/*  929 */       SqlTimeValueFactory sqlTimeValueFactory = new SqlTimeValueFactory(this.session.getPropertySet(), cal, (cal != null) ? cal.getTimeZone() : this.session.getServerSession().getDefaultTimeZone());
/*  930 */       return (Time)this.thisRow.getValue(columnIndex - 1, (ValueFactory)sqlTimeValueFactory); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Time getTime(String columnName) throws SQLException {
/*      */     
/*  935 */     try { return getTime(findColumn(columnName)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Time getTime(String columnName, Calendar cal) throws SQLException {
/*      */     
/*  940 */     try { return getTime(findColumn(columnName), cal); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Timestamp getTimestamp(int columnIndex) throws SQLException {
/*      */     
/*  945 */     try { checkRowPos();
/*  946 */       checkColumnBounds(columnIndex);
/*  947 */       return (Timestamp)this.thisRow.getValue(columnIndex - 1, this.defaultTimestampValueFactory); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public LocalDate getLocalDate(int columnIndex) throws SQLException {
/*  951 */     checkRowPos();
/*  952 */     checkColumnBounds(columnIndex);
/*  953 */     return (LocalDate)this.thisRow.getValue(columnIndex - 1, this.defaultLocalDateValueFactory);
/*      */   }
/*      */   
/*      */   public LocalDateTime getLocalDateTime(int columnIndex) throws SQLException {
/*  957 */     checkRowPos();
/*  958 */     checkColumnBounds(columnIndex);
/*  959 */     return (LocalDateTime)this.thisRow.getValue(columnIndex - 1, this.defaultLocalDateTimeValueFactory);
/*      */   }
/*      */   
/*      */   public LocalTime getLocalTime(int columnIndex) throws SQLException {
/*  963 */     checkRowPos();
/*  964 */     checkColumnBounds(columnIndex);
/*  965 */     return (LocalTime)this.thisRow.getValue(columnIndex - 1, this.defaultLocalTimeValueFactory);
/*      */   }
/*      */   
/*      */   public Calendar getUtilCalendar(int columnIndex) throws SQLException {
/*  969 */     checkRowPos();
/*  970 */     checkColumnBounds(columnIndex);
/*  971 */     return (Calendar)this.thisRow.getValue(columnIndex - 1, this.defaultUtilCalendarValueFactory);
/*      */   }
/*      */   
/*      */   public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
/*      */     
/*  976 */     try { checkRowPos();
/*  977 */       checkColumnBounds(columnIndex);
/*      */       
/*  979 */       SqlTimestampValueFactory sqlTimestampValueFactory = new SqlTimestampValueFactory(this.session.getPropertySet(), cal, this.session.getServerSession().getDefaultTimeZone(), this.session.getServerSession().getSessionTimeZone());
/*  980 */       return (Timestamp)this.thisRow.getValue(columnIndex - 1, (ValueFactory)sqlTimestampValueFactory); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Timestamp getTimestamp(String columnName) throws SQLException {
/*      */     
/*  985 */     try { return getTimestamp(findColumn(columnName)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException {
/*      */     
/*  990 */     try { return getTimestamp(findColumn(columnName), cal); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Reader getNCharacterStream(int columnIndex) throws SQLException {
/*      */     
/*  995 */     try { checkRowPos();
/*  996 */       checkColumnBounds(columnIndex);
/*      */       
/*  998 */       String fieldEncoding = this.columnDefinition.getFields()[columnIndex - 1].getEncoding();
/*  999 */       if (fieldEncoding == null || !fieldEncoding.equals("UTF-8")) {
/* 1000 */         throw new SQLException("Can not call getNCharacterStream() when field's charset isn't UTF-8");
/*      */       }
/* 1002 */       return getCharacterStream(columnIndex); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Reader getNCharacterStream(String columnName) throws SQLException {
/*      */     
/* 1007 */     try { return getNCharacterStream(findColumn(columnName)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public NClob getNClob(int columnIndex) throws SQLException {
/*      */     
/* 1012 */     try { checkRowPos();
/* 1013 */       checkColumnBounds(columnIndex);
/*      */       
/* 1015 */       String fieldEncoding = this.columnDefinition.getFields()[columnIndex - 1].getEncoding();
/* 1016 */       if (fieldEncoding == null || !fieldEncoding.equals("UTF-8")) {
/* 1017 */         throw new SQLException("Can not call getNClob() when field's charset isn't UTF-8");
/*      */       }
/*      */       
/* 1020 */       String asString = getStringForNClob(columnIndex);
/*      */       
/* 1022 */       if (asString == null) {
/* 1023 */         return null;
/*      */       }
/*      */       
/* 1026 */       return (NClob)new NClob(asString, getExceptionInterceptor()); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public NClob getNClob(String columnName) throws SQLException {
/*      */     
/* 1031 */     try { return getNClob(findColumn(columnName)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   private String getStringForNClob(int columnIndex) throws SQLException {
/* 1035 */     String asString = null;
/*      */     
/* 1037 */     String forcedEncoding = "UTF-8";
/*      */     
/*      */     try {
/* 1040 */       byte[] asBytes = getBytes(columnIndex);
/*      */       
/* 1042 */       if (asBytes != null) {
/* 1043 */         asString = new String(asBytes, forcedEncoding);
/*      */       }
/* 1045 */     } catch (UnsupportedEncodingException uee) {
/* 1046 */       throw SQLError.createSQLException("Unsupported character encoding " + forcedEncoding, "S1009", 
/* 1047 */           getExceptionInterceptor());
/*      */     } 
/*      */     
/* 1050 */     return asString;
/*      */   }
/*      */   
/*      */   public String getNString(int columnIndex) throws SQLException {
/*      */     
/* 1055 */     try { checkRowPos();
/* 1056 */       checkColumnBounds(columnIndex);
/*      */       
/* 1058 */       String fieldEncoding = this.columnDefinition.getFields()[columnIndex - 1].getEncoding();
/* 1059 */       if (fieldEncoding == null || !fieldEncoding.equals("UTF-8")) {
/* 1060 */         throw new SQLException("Can not call getNString() when field's charset isn't UTF-8");
/*      */       }
/* 1062 */       return getString(columnIndex); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public String getNString(String columnName) throws SQLException {
/*      */     
/* 1067 */     try { return getNString(findColumn(columnName)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public int getConcurrency() throws SQLException {
/*      */     
/* 1072 */     try { return 1007; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public String getCursorName() throws SQLException { try {
/* 1077 */       throw SQLError.createSQLException(Messages.getString("ResultSet.Positioned_Update_not_supported"), "S1C00", 
/* 1078 */           getExceptionInterceptor());
/*      */     } catch (CJException cJException) {
/*      */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     }  } public int getFetchDirection() throws SQLException {
/*      */     
/* 1083 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1084 */       { return this.fetchDirection; }  }
/* 1085 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public int getFetchSize() throws SQLException {
/*      */     
/* 1090 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1091 */       { return this.fetchSize; }  }
/* 1092 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public char getFirstCharOfQuery() {
/*      */     try {
/* 1098 */       synchronized (checkClosed().getConnectionMutex()) {
/* 1099 */         return this.firstCharOfQuery;
/*      */       } 
/* 1101 */     } catch (SQLException e) {
/* 1102 */       throw new RuntimeException(e);
/*      */     } 
/*      */   }
/*      */   
/*      */   public ResultSetMetaData getMetaData() throws SQLException {
/*      */     try {
/* 1108 */       checkClosed();
/*      */       
/* 1110 */       return new ResultSetMetaData((Session)this.session, this.columnDefinition.getFields(), ((Boolean)this.session
/* 1111 */           .getPropertySet().getBooleanProperty(PropertyKey.useOldAliasMetadataBehavior).getValue()).booleanValue(), this.yearIsDateType, 
/* 1112 */           getExceptionInterceptor());
/*      */     } catch (CJException cJException) {
/*      */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     }  } public Object getObject(int columnIndex) throws SQLException { 
/*      */     try { String stringVal;
/* 1117 */       checkRowPos();
/* 1118 */       checkColumnBounds(columnIndex);
/*      */       
/* 1120 */       int columnIndexMinusOne = columnIndex - 1;
/*      */ 
/*      */       
/* 1123 */       if (this.thisRow.getNull(columnIndexMinusOne)) {
/* 1124 */         return null;
/*      */       }
/*      */       
/* 1127 */       Field field = this.columnDefinition.getFields()[columnIndexMinusOne];
/* 1128 */       switch (field.getMysqlType()) {
/*      */         
/*      */         case BIT:
/* 1131 */           if (field.isBinary() || field.isBlob()) {
/* 1132 */             byte[] data = getBytes(columnIndex);
/*      */             
/* 1134 */             if (((Boolean)this.connection.getPropertySet().getBooleanProperty(PropertyKey.autoDeserialize).getValue()).booleanValue()) {
/* 1135 */               Object obj = data;
/*      */               
/* 1137 */               if (data != null && data.length >= 2) {
/* 1138 */                 if (data[0] == -84 && data[1] == -19) {
/*      */                   
/*      */                   try {
/* 1141 */                     ByteArrayInputStream bytesIn = new ByteArrayInputStream(data);
/* 1142 */                     ObjectInputStream objIn = new ObjectInputStream(bytesIn);
/* 1143 */                     obj = objIn.readObject();
/* 1144 */                     objIn.close();
/* 1145 */                     bytesIn.close();
/* 1146 */                   } catch (ClassNotFoundException cnfe) {
/* 1147 */                     throw SQLError.createSQLException(Messages.getString("ResultSet.Class_not_found___91") + cnfe.toString() + 
/* 1148 */                         Messages.getString("ResultSet._while_reading_serialized_object_92"), getExceptionInterceptor());
/* 1149 */                   } catch (IOException ex) {
/* 1150 */                     obj = data;
/*      */                   } 
/*      */                 } else {
/* 1153 */                   return getString(columnIndex);
/*      */                 } 
/*      */               }
/*      */               
/* 1157 */               return obj;
/*      */             } 
/*      */             
/* 1160 */             return data;
/*      */           } 
/*      */           
/* 1163 */           return field.isSingleBit() ? Boolean.valueOf(getBoolean(columnIndex)) : getBytes(columnIndex);
/*      */         
/*      */         case BOOLEAN:
/* 1166 */           return Boolean.valueOf(getBoolean(columnIndex));
/*      */         
/*      */         case TINYINT:
/* 1169 */           return Integer.valueOf(getByte(columnIndex));
/*      */         
/*      */         case TINYINT_UNSIGNED:
/*      */         case SMALLINT:
/*      */         case SMALLINT_UNSIGNED:
/*      */         case MEDIUMINT:
/*      */         case MEDIUMINT_UNSIGNED:
/*      */         case INT:
/* 1177 */           return Integer.valueOf(getInt(columnIndex));
/*      */         
/*      */         case INT_UNSIGNED:
/*      */         case BIGINT:
/* 1181 */           return Long.valueOf(getLong(columnIndex));
/*      */         
/*      */         case BIGINT_UNSIGNED:
/* 1184 */           return getBigInteger(columnIndex);
/*      */         
/*      */         case DECIMAL:
/*      */         case DECIMAL_UNSIGNED:
/* 1188 */           stringVal = getString(columnIndex);
/*      */           
/* 1190 */           if (stringVal != null) {
/* 1191 */             if (stringVal.length() == 0) {
/* 1192 */               return new BigDecimal(0);
/*      */             }
/*      */             
/*      */             try {
/* 1196 */               return new BigDecimal(stringVal);
/* 1197 */             } catch (NumberFormatException ex) {
/* 1198 */               throw SQLError.createSQLException(
/* 1199 */                   Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009", 
/* 1200 */                   getExceptionInterceptor());
/*      */             } 
/*      */           } 
/* 1203 */           return null;
/*      */         
/*      */         case FLOAT:
/*      */         case FLOAT_UNSIGNED:
/* 1207 */           return new Float(getFloat(columnIndex));
/*      */         
/*      */         case DOUBLE:
/*      */         case DOUBLE_UNSIGNED:
/* 1211 */           return new Double(getDouble(columnIndex));
/*      */         
/*      */         case CHAR:
/*      */         case ENUM:
/*      */         case SET:
/*      */         case VARCHAR:
/*      */         case TINYTEXT:
/* 1218 */           return getString(columnIndex);
/*      */         
/*      */         case TEXT:
/*      */         case MEDIUMTEXT:
/*      */         case LONGTEXT:
/*      */         case JSON:
/* 1224 */           return getStringForClob(columnIndex);
/*      */         
/*      */         case GEOMETRY:
/* 1227 */           return getBytes(columnIndex);
/*      */         
/*      */         case BINARY:
/*      */         case VARBINARY:
/*      */         case TINYBLOB:
/*      */         case MEDIUMBLOB:
/*      */         case LONGBLOB:
/*      */         case BLOB:
/* 1235 */           if (field.isBinary() || field.isBlob()) {
/* 1236 */             byte[] data = getBytes(columnIndex);
/*      */             
/* 1238 */             if (((Boolean)this.connection.getPropertySet().getBooleanProperty(PropertyKey.autoDeserialize).getValue()).booleanValue()) {
/* 1239 */               Object obj = data;
/*      */               
/* 1241 */               if (data != null && data.length >= 2) {
/* 1242 */                 if (data[0] == -84 && data[1] == -19) {
/*      */                   
/*      */                   try {
/* 1245 */                     ByteArrayInputStream bytesIn = new ByteArrayInputStream(data);
/* 1246 */                     ObjectInputStream objIn = new ObjectInputStream(bytesIn);
/* 1247 */                     obj = objIn.readObject();
/* 1248 */                     objIn.close();
/* 1249 */                     bytesIn.close();
/* 1250 */                   } catch (ClassNotFoundException cnfe) {
/* 1251 */                     throw SQLError.createSQLException(Messages.getString("ResultSet.Class_not_found___91") + cnfe.toString() + 
/* 1252 */                         Messages.getString("ResultSet._while_reading_serialized_object_92"), getExceptionInterceptor());
/* 1253 */                   } catch (IOException ex) {
/* 1254 */                     obj = data;
/*      */                   } 
/*      */                 } else {
/* 1257 */                   return getString(columnIndex);
/*      */                 } 
/*      */               }
/*      */               
/* 1261 */               return obj;
/*      */             } 
/*      */             
/* 1264 */             return data;
/*      */           } 
/*      */           
/* 1267 */           return getBytes(columnIndex);
/*      */         
/*      */         case YEAR:
/* 1270 */           return this.yearIsDateType ? getDate(columnIndex) : Short.valueOf(getShort(columnIndex));
/*      */         
/*      */         case DATE:
/* 1273 */           return getDate(columnIndex);
/*      */         
/*      */         case TIME:
/* 1276 */           return getTime(columnIndex);
/*      */         
/*      */         case TIMESTAMP:
/* 1279 */           return getTimestamp(columnIndex);
/*      */         
/*      */         case DATETIME:
/* 1282 */           return getLocalDateTime(columnIndex);
/*      */       } 
/*      */       
/* 1285 */       return getString(columnIndex); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */      }
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
/*      */     
/* 1292 */     try { if (type == null) {
/* 1293 */         throw SQLError.createSQLException("Type parameter can not be null", "S1009", getExceptionInterceptor());
/*      */       }
/*      */       
/* 1296 */       synchronized (checkClosed().getConnectionMutex())
/* 1297 */       { if (type.equals(String.class)) {
/* 1298 */           return (T)getString(columnIndex);
/*      */         }
/* 1300 */         if (type.equals(BigDecimal.class)) {
/* 1301 */           return (T)getBigDecimal(columnIndex);
/*      */         }
/* 1303 */         if (type.equals(BigInteger.class)) {
/* 1304 */           return (T)getBigInteger(columnIndex);
/*      */         }
/* 1306 */         if (type.equals(Boolean.class) || type.equals(boolean.class)) {
/* 1307 */           checkRowPos();
/* 1308 */           checkColumnBounds(columnIndex);
/* 1309 */           return (T)this.thisRow.getValue(columnIndex - 1, this.booleanValueFactory);
/*      */         } 
/* 1311 */         if (type.equals(Byte.class) || type.equals(byte.class)) {
/* 1312 */           checkRowPos();
/* 1313 */           checkColumnBounds(columnIndex);
/* 1314 */           return (T)this.thisRow.getValue(columnIndex - 1, this.byteValueFactory);
/*      */         } 
/* 1316 */         if (type.equals(Short.class) || type.equals(short.class)) {
/* 1317 */           checkRowPos();
/* 1318 */           checkColumnBounds(columnIndex);
/* 1319 */           return (T)this.thisRow.getValue(columnIndex - 1, this.shortValueFactory);
/*      */         } 
/* 1321 */         if (type.equals(Integer.class) || type.equals(int.class)) {
/* 1322 */           checkRowPos();
/* 1323 */           checkColumnBounds(columnIndex);
/* 1324 */           return (T)this.thisRow.getValue(columnIndex - 1, this.integerValueFactory);
/*      */         } 
/* 1326 */         if (type.equals(Long.class) || type.equals(long.class)) {
/* 1327 */           checkRowPos();
/* 1328 */           checkColumnBounds(columnIndex);
/* 1329 */           return (T)this.thisRow.getValue(columnIndex - 1, this.longValueFactory);
/*      */         } 
/* 1331 */         if (type.equals(Float.class) || type.equals(float.class)) {
/* 1332 */           checkRowPos();
/* 1333 */           checkColumnBounds(columnIndex);
/* 1334 */           return (T)this.thisRow.getValue(columnIndex - 1, this.floatValueFactory);
/*      */         } 
/* 1336 */         if (type.equals(Double.class) || type.equals(double.class)) {
/* 1337 */           checkRowPos();
/* 1338 */           checkColumnBounds(columnIndex);
/* 1339 */           return (T)this.thisRow.getValue(columnIndex - 1, this.doubleValueFactory);
/*      */         } 
/* 1341 */         if (type.equals(byte[].class)) {
/* 1342 */           return (T)getBytes(columnIndex);
/*      */         }
/* 1344 */         if (type.equals(Date.class)) {
/* 1345 */           return (T)getDate(columnIndex);
/*      */         }
/* 1347 */         if (type.equals(Time.class)) {
/* 1348 */           return (T)getTime(columnIndex);
/*      */         }
/* 1350 */         if (type.equals(Timestamp.class)) {
/* 1351 */           return (T)getTimestamp(columnIndex);
/*      */         }
/* 1353 */         if (type.equals(Date.class)) {
/* 1354 */           Timestamp ts = getTimestamp(columnIndex);
/* 1355 */           return (ts == null) ? null : (T)Date.from(ts.toInstant());
/*      */         } 
/* 1357 */         if (type.equals(Calendar.class)) {
/* 1358 */           return (T)getUtilCalendar(columnIndex);
/*      */         }
/* 1360 */         if (type.equals(Clob.class)) {
/* 1361 */           return (T)getClob(columnIndex);
/*      */         }
/* 1363 */         if (type.equals(Blob.class)) {
/* 1364 */           return (T)getBlob(columnIndex);
/*      */         }
/* 1366 */         if (type.equals(Array.class)) {
/* 1367 */           return (T)getArray(columnIndex);
/*      */         }
/* 1369 */         if (type.equals(Ref.class)) {
/* 1370 */           return (T)getRef(columnIndex);
/*      */         }
/* 1372 */         if (type.equals(URL.class)) {
/* 1373 */           return (T)getURL(columnIndex);
/*      */         }
/* 1375 */         if (type.equals(Struct.class)) {
/* 1376 */           throw new SQLFeatureNotSupportedException();
/*      */         }
/* 1378 */         if (type.equals(RowId.class)) {
/* 1379 */           return (T)getRowId(columnIndex);
/*      */         }
/* 1381 */         if (type.equals(NClob.class)) {
/* 1382 */           return (T)getNClob(columnIndex);
/*      */         }
/* 1384 */         if (type.equals(SQLXML.class)) {
/* 1385 */           return (T)getSQLXML(columnIndex);
/*      */         }
/* 1387 */         if (type.equals(LocalDate.class)) {
/* 1388 */           return (T)getLocalDate(columnIndex);
/*      */         }
/* 1390 */         if (type.equals(LocalDateTime.class)) {
/* 1391 */           return (T)getLocalDateTime(columnIndex);
/*      */         }
/* 1393 */         if (type.equals(LocalTime.class)) {
/* 1394 */           return (T)getLocalTime(columnIndex);
/*      */         }
/* 1396 */         if (type.equals(OffsetDateTime.class)) {
/* 1397 */           checkRowPos();
/* 1398 */           checkColumnBounds(columnIndex);
/* 1399 */           return (T)this.thisRow.getValue(columnIndex - 1, this.defaultOffsetDateTimeValueFactory);
/*      */         } 
/* 1401 */         if (type.equals(OffsetTime.class)) {
/* 1402 */           checkRowPos();
/* 1403 */           checkColumnBounds(columnIndex);
/* 1404 */           return (T)this.thisRow.getValue(columnIndex - 1, this.defaultOffsetTimeValueFactory);
/*      */         } 
/* 1406 */         if (type.equals(ZonedDateTime.class)) {
/* 1407 */           checkRowPos();
/* 1408 */           checkColumnBounds(columnIndex);
/* 1409 */           return (T)this.thisRow.getValue(columnIndex - 1, this.defaultZonedDateTimeValueFactory);
/*      */         } 
/* 1411 */         if (type.equals(Duration.class)) {
/* 1412 */           checkRowPos();
/* 1413 */           checkColumnBounds(columnIndex);
/* 1414 */           return (T)this.thisRow.getValue(columnIndex - 1, (ValueFactory)new DurationValueFactory(this.session.getPropertySet()));
/*      */         } 
/*      */         
/* 1417 */         if (((Boolean)this.connection.getPropertySet().getBooleanProperty(PropertyKey.autoDeserialize).getValue()).booleanValue()) {
/*      */           try {
/* 1419 */             return (T)getObject(columnIndex);
/* 1420 */           } catch (ClassCastException cce) {
/* 1421 */             SQLException sqlEx = SQLError.createSQLException("Conversion not supported for type " + type.getName(), "S1009", 
/* 1422 */                 getExceptionInterceptor());
/* 1423 */             sqlEx.initCause(cce);
/*      */             
/* 1425 */             throw sqlEx;
/*      */           } 
/*      */         }
/*      */         
/* 1429 */         throw SQLError.createSQLException("Conversion not supported for type " + type.getName(), "S1009", 
/* 1430 */             getExceptionInterceptor()); }  }
/* 1431 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
/*      */     
/* 1436 */     try { return getObject(findColumn(columnLabel), type); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Object getObject(int i, Map<String, Class<?>> map) throws SQLException {
/*      */     
/* 1441 */     try { return getObject(i); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Object getObject(String columnName) throws SQLException {
/*      */     
/* 1446 */     try { return getObject(findColumn(columnName)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Object getObject(String colName, Map<String, Class<?>> map) throws SQLException {
/*      */     
/* 1451 */     try { return getObject(findColumn(colName), map); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public Object getObjectStoredProc(int columnIndex, int desiredSqlType) throws SQLException {
/*      */     
/*      */     try { String stringVal;
/* 1456 */       checkRowPos();
/* 1457 */       checkColumnBounds(columnIndex);
/*      */       
/* 1459 */       Object value = this.thisRow.getBytes(columnIndex - 1);
/*      */       
/* 1461 */       if (value == null) {
/* 1462 */         return null;
/*      */       }
/*      */       
/* 1465 */       Field field = this.columnDefinition.getFields()[columnIndex - 1];
/*      */       
/* 1467 */       MysqlType desiredMysqlType = MysqlType.getByJdbcType(desiredSqlType);
/*      */       
/* 1469 */       switch (desiredMysqlType) {
/*      */         case BIT:
/*      */         case BOOLEAN:
/* 1472 */           return Boolean.valueOf(getBoolean(columnIndex));
/*      */         
/*      */         case TINYINT:
/*      */         case TINYINT_UNSIGNED:
/* 1476 */           return Integer.valueOf(getInt(columnIndex));
/*      */         
/*      */         case SMALLINT:
/*      */         case SMALLINT_UNSIGNED:
/* 1480 */           return Integer.valueOf(getInt(columnIndex));
/*      */         
/*      */         case MEDIUMINT:
/*      */         case MEDIUMINT_UNSIGNED:
/*      */         case INT:
/*      */         case INT_UNSIGNED:
/* 1486 */           if (!field.isUnsigned() || field.getMysqlTypeId() == 9) {
/* 1487 */             return Integer.valueOf(getInt(columnIndex));
/*      */           }
/* 1489 */           return Long.valueOf(getLong(columnIndex));
/*      */         
/*      */         case BIGINT:
/* 1492 */           return Long.valueOf(getLong(columnIndex));
/*      */         
/*      */         case BIGINT_UNSIGNED:
/* 1495 */           return getBigInteger(columnIndex);
/*      */         
/*      */         case DECIMAL:
/*      */         case DECIMAL_UNSIGNED:
/* 1499 */           stringVal = getString(columnIndex);
/*      */ 
/*      */           
/* 1502 */           if (stringVal != null) {
/* 1503 */             BigDecimal val; if (stringVal.length() == 0) {
/* 1504 */               val = new BigDecimal(0);
/*      */               
/* 1506 */               return val;
/*      */             } 
/*      */             
/*      */             try {
/* 1510 */               val = new BigDecimal(stringVal);
/* 1511 */             } catch (NumberFormatException ex) {
/* 1512 */               throw SQLError.createSQLException(
/* 1513 */                   Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009", 
/* 1514 */                   getExceptionInterceptor());
/*      */             } 
/*      */             
/* 1517 */             return val;
/*      */           } 
/*      */           
/* 1520 */           return null;
/*      */         
/*      */         case FLOAT:
/*      */         case FLOAT_UNSIGNED:
/* 1524 */           return new Float(getFloat(columnIndex));
/*      */         
/*      */         case DOUBLE:
/*      */         case DOUBLE_UNSIGNED:
/* 1528 */           return new Double(getDouble(columnIndex));
/*      */         
/*      */         case CHAR:
/*      */         case ENUM:
/*      */         case SET:
/*      */         case VARCHAR:
/*      */         case TINYTEXT:
/* 1535 */           return getString(columnIndex);
/*      */         
/*      */         case TEXT:
/*      */         case MEDIUMTEXT:
/*      */         case LONGTEXT:
/*      */         case JSON:
/* 1541 */           return getStringForClob(columnIndex);
/*      */         
/*      */         case GEOMETRY:
/*      */         case BINARY:
/*      */         case VARBINARY:
/*      */         case TINYBLOB:
/*      */         case MEDIUMBLOB:
/*      */         case LONGBLOB:
/*      */         case BLOB:
/* 1550 */           return getBytes(columnIndex);
/*      */         
/*      */         case YEAR:
/*      */         case DATE:
/* 1554 */           if (field.getMysqlType() == MysqlType.YEAR && !this.yearIsDateType) {
/* 1555 */             return Short.valueOf(getShort(columnIndex));
/*      */           }
/*      */           
/* 1558 */           return getDate(columnIndex);
/*      */         
/*      */         case TIME:
/* 1561 */           return getTime(columnIndex);
/*      */         
/*      */         case TIMESTAMP:
/* 1564 */           return getTimestamp(columnIndex);
/*      */       } 
/*      */       
/* 1567 */       return getString(columnIndex); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public Object getObjectStoredProc(int i, Map<Object, Object> map, int desiredSqlType) throws SQLException {
/*      */     
/* 1573 */     try { return getObjectStoredProc(i, desiredSqlType); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Object getObjectStoredProc(String columnName, int desiredSqlType) throws SQLException {
/*      */     
/* 1578 */     try { return getObjectStoredProc(findColumn(columnName), desiredSqlType); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Object getObjectStoredProc(String colName, Map<Object, Object> map, int desiredSqlType) throws SQLException {
/*      */     
/* 1583 */     try { return getObjectStoredProc(findColumn(colName), map, desiredSqlType); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Ref getRef(int i) throws SQLException {
/*      */     
/* 1588 */     try { checkColumnBounds(i);
/* 1589 */       throw SQLError.createSQLFeatureNotSupportedException(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Ref getRef(String colName) throws SQLException {
/*      */     
/* 1594 */     try { return getRef(findColumn(colName)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public int getRow() throws SQLException {
/*      */     
/* 1599 */     try { checkClosed();
/*      */       
/* 1601 */       if (!hasRows()) {
/* 1602 */         throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", 
/* 1603 */             getExceptionInterceptor());
/*      */       }
/*      */       
/* 1606 */       int currentRowNumber = this.rowData.getPosition();
/* 1607 */       int row = 0;
/*      */ 
/*      */       
/* 1610 */       if (!this.rowData.isDynamic()) {
/* 1611 */         if (currentRowNumber < 0 || this.rowData.isAfterLast() || this.rowData.isEmpty()) {
/* 1612 */           row = 0;
/*      */         } else {
/* 1614 */           row = currentRowNumber + 1;
/*      */         } 
/*      */       } else {
/*      */         
/* 1618 */         row = currentRowNumber + 1;
/*      */       } 
/*      */       
/* 1621 */       return row; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Statement getStatement() throws SQLException {
/*      */     try {
/*      */       try {
/* 1627 */         synchronized (checkClosed().getConnectionMutex()) {
/* 1628 */           if (this.wrapperStatement != null) {
/* 1629 */             return this.wrapperStatement;
/*      */           }
/*      */           
/* 1632 */           return (Statement)this.owningStatement;
/*      */         }
/*      */       
/* 1635 */       } catch (SQLException sqlEx) {
/* 1636 */         throw SQLError.createSQLException("Operation not allowed on closed ResultSet.", "S1000", 
/* 1637 */             getExceptionInterceptor());
/*      */       } 
/*      */     } catch (CJException cJException) {
/*      */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public int getType() throws SQLException {
/*      */     
/* 1644 */     try { return this.resultSetType; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   @Deprecated
/*      */   public InputStream getUnicodeStream(int columnIndex) throws SQLException {
/*      */     
/* 1650 */     try { checkRowPos();
/*      */       
/* 1652 */       return getBinaryStream(columnIndex); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   @Deprecated
/*      */   public InputStream getUnicodeStream(String columnName) throws SQLException {
/*      */     
/* 1658 */     try { return getUnicodeStream(findColumn(columnName)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public URL getURL(int colIndex) throws SQLException { try {
/* 1663 */       String val = getString(colIndex);
/*      */       
/* 1665 */       if (val == null) {
/* 1666 */         return null;
/*      */       }
/*      */       
/*      */       try {
/* 1670 */         return new URL(val);
/* 1671 */       } catch (MalformedURLException mfe) {
/* 1672 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Malformed_URL____104") + val + "'", "S1009", 
/* 1673 */             getExceptionInterceptor());
/*      */       } 
/*      */     } catch (CJException cJException) {
/*      */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     }  }
/*      */   public URL getURL(String colName) throws SQLException { try {
/* 1679 */       String val = getString(colName);
/*      */       
/* 1681 */       if (val == null) {
/* 1682 */         return null;
/*      */       }
/*      */       
/*      */       try {
/* 1686 */         return new URL(val);
/* 1687 */       } catch (MalformedURLException mfe) {
/* 1688 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Malformed_URL____107") + val + "'", "S1009", 
/* 1689 */             getExceptionInterceptor());
/*      */       } 
/*      */     } catch (CJException cJException) {
/*      */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     }  } public SQLWarning getWarnings() throws SQLException {
/*      */     
/* 1695 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1696 */       { return this.warningChain; }  }
/* 1697 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void insertRow() throws SQLException {
/*      */     
/* 1702 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean isAfterLast() throws SQLException {
/*      */     
/* 1707 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1708 */       { if (!hasRows()) {
/* 1709 */           throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", 
/* 1710 */               getExceptionInterceptor());
/*      */         }
/* 1712 */         return this.rowData.isAfterLast(); }  }
/* 1713 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean isBeforeFirst() throws SQLException {
/*      */     
/* 1718 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1719 */       { if (!hasRows()) {
/* 1720 */           throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", 
/* 1721 */               getExceptionInterceptor());
/*      */         }
/*      */         
/* 1724 */         return this.rowData.isBeforeFirst(); }  }
/* 1725 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean isFirst() throws SQLException {
/*      */     
/* 1730 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1731 */       { if (!hasRows()) {
/* 1732 */           throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", 
/* 1733 */               getExceptionInterceptor());
/*      */         }
/*      */         
/* 1736 */         return this.rowData.isFirst(); }  }
/* 1737 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean isLast() throws SQLException {
/*      */     
/* 1742 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1743 */       { if (!hasRows()) {
/* 1744 */           throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", 
/* 1745 */               getExceptionInterceptor());
/*      */         }
/*      */         
/* 1748 */         return this.rowData.isLast(); }  }
/* 1749 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean isStrictlyForwardOnly() {
/* 1760 */     return (this.resultSetType == 1003 && !this.scrollTolerant);
/*      */   }
/*      */   
/*      */   public boolean last() throws SQLException {
/*      */     
/* 1765 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1766 */       { if (!hasRows()) {
/* 1767 */           throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", 
/* 1768 */               getExceptionInterceptor());
/*      */         }
/*      */         
/* 1771 */         if (isStrictlyForwardOnly()) {
/* 1772 */           throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly"));
/*      */         }
/*      */         
/* 1775 */         boolean b = true;
/*      */         
/* 1777 */         if (this.rowData.size() == 0) {
/* 1778 */           b = false;
/*      */         } else {
/* 1780 */           this.rowData.beforeLast();
/* 1781 */           this.thisRow = (Row)this.rowData.next();
/*      */         } 
/*      */         
/* 1784 */         setRowPositionValidity();
/*      */         
/* 1786 */         return b; }  }
/* 1787 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void moveToCurrentRow() throws SQLException {
/*      */     
/* 1792 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void moveToInsertRow() throws SQLException {
/*      */     
/* 1797 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean next() throws SQLException {
/*      */     
/* 1802 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1803 */       { boolean b; if (!hasRows()) {
/* 1804 */           throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", 
/* 1805 */               getExceptionInterceptor());
/*      */         }
/*      */ 
/*      */ 
/*      */         
/* 1810 */         if (this.rowData.size() == 0) {
/* 1811 */           b = false;
/*      */         } else {
/* 1813 */           this.thisRow = (Row)this.rowData.next();
/*      */           
/* 1815 */           if (this.thisRow == null) {
/* 1816 */             b = false;
/*      */           } else {
/* 1818 */             clearWarnings();
/*      */             
/* 1820 */             b = true;
/*      */           } 
/*      */         } 
/*      */ 
/*      */         
/* 1825 */         setRowPositionValidity();
/*      */         
/* 1827 */         return b; }  }
/* 1828 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
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
/*      */   public boolean prev() throws SQLException {
/* 1845 */     synchronized (checkClosed().getConnectionMutex()) {
/*      */       
/* 1847 */       int rowIndex = this.rowData.getPosition();
/*      */       
/* 1849 */       boolean b = true;
/*      */       
/* 1851 */       if (rowIndex - 1 >= 0) {
/* 1852 */         rowIndex--;
/* 1853 */         this.rowData.setCurrentRow(rowIndex);
/* 1854 */         this.thisRow = this.rowData.get(rowIndex);
/*      */         
/* 1856 */         b = true;
/* 1857 */       } else if (rowIndex - 1 == -1) {
/* 1858 */         rowIndex--;
/* 1859 */         this.rowData.setCurrentRow(rowIndex);
/* 1860 */         this.thisRow = null;
/*      */         
/* 1862 */         b = false;
/*      */       } else {
/* 1864 */         b = false;
/*      */       } 
/*      */       
/* 1867 */       setRowPositionValidity();
/*      */       
/* 1869 */       return b;
/*      */     } 
/*      */   }
/*      */   
/*      */   public boolean previous() throws SQLException {
/*      */     
/* 1875 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1876 */       { if (!hasRows()) {
/* 1877 */           throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", 
/* 1878 */               getExceptionInterceptor());
/*      */         }
/*      */         
/* 1881 */         if (isStrictlyForwardOnly()) {
/* 1882 */           throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly"));
/*      */         }
/*      */         
/* 1885 */         return prev(); }  }
/* 1886 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void realClose(boolean calledExplicitly) throws SQLException {
/*      */     try {
/* 1891 */       JdbcConnection locallyScopedConn = this.connection;
/*      */       
/* 1893 */       if (locallyScopedConn == null) {
/*      */         return;
/*      */       }
/*      */       
/* 1897 */       synchronized (locallyScopedConn.getConnectionMutex()) {
/*      */         
/* 1899 */         if (this.isClosed) {
/*      */           return;
/*      */         }
/*      */         
/*      */         try {
/* 1904 */           if (this.useUsageAdvisor) {
/*      */             
/* 1906 */             if (!calledExplicitly) {
/* 1907 */               this.eventSink.processEvent((byte)0, (Session)this.session, (Query)this.owningStatement, this, 0L, new Throwable(), 
/* 1908 */                   Messages.getString("ResultSet.ResultSet_implicitly_closed_by_driver"));
/*      */             }
/*      */             
/* 1911 */             int resultSetSizeThreshold = ((Integer)locallyScopedConn.getPropertySet().getIntegerProperty(PropertyKey.resultSetSizeThreshold).getValue()).intValue();
/* 1912 */             if (this.rowData.size() > resultSetSizeThreshold) {
/* 1913 */               this.eventSink.processEvent((byte)0, (Session)this.session, (Query)this.owningStatement, this, 0L, new Throwable(), 
/* 1914 */                   Messages.getString("ResultSet.Too_Large_Result_Set", new Object[] {
/* 1915 */                       Integer.valueOf(this.rowData.size()), Integer.valueOf(resultSetSizeThreshold)
/*      */                     }));
/*      */             }
/* 1918 */             if (!isLast() && !isAfterLast() && this.rowData.size() != 0) {
/* 1919 */               this.eventSink.processEvent((byte)0, (Session)this.session, (Query)this.owningStatement, this, 0L, new Throwable(), 
/* 1920 */                   Messages.getString("ResultSet.Possible_incomplete_traversal_of_result_set", new Object[] {
/* 1921 */                       Integer.valueOf(getRow()), Integer.valueOf(this.rowData.size())
/*      */                     }));
/*      */             }
/*      */             
/* 1925 */             if (this.columnUsed.length > 0 && !this.rowData.wasEmpty()) {
/* 1926 */               StringBuilder buf = new StringBuilder();
/* 1927 */               for (int i = 0; i < this.columnUsed.length; i++) {
/* 1928 */                 if (!this.columnUsed[i]) {
/* 1929 */                   if (buf.length() > 0) {
/* 1930 */                     buf.append(", ");
/*      */                   }
/* 1932 */                   buf.append(this.columnDefinition.getFields()[i].getFullName());
/*      */                 } 
/*      */               } 
/* 1935 */               if (buf.length() > 0) {
/* 1936 */                 this.eventSink.processEvent((byte)0, (Session)this.session, (Query)this.owningStatement, this, 0L, new Throwable(), 
/* 1937 */                     Messages.getString("ResultSet.The_following_columns_were_never_referenced", (Object[])new String[] { buf.toString() }));
/*      */               }
/*      */             } 
/*      */           } 
/*      */         } finally {
/* 1942 */           if (this.owningStatement != null && calledExplicitly) {
/* 1943 */             this.owningStatement.removeOpenResultSet(this);
/*      */           }
/*      */           
/* 1946 */           SQLException exceptionDuringClose = null;
/*      */           
/* 1948 */           if (this.rowData != null) {
/*      */             try {
/* 1950 */               this.rowData.close();
/* 1951 */             } catch (CJException sqlEx) {
/* 1952 */               exceptionDuringClose = SQLExceptionsMapping.translateException((Throwable)sqlEx);
/*      */             } 
/*      */           }
/*      */           
/* 1956 */           if (this.statementUsedForFetchingRows != null) {
/*      */             try {
/* 1958 */               this.statementUsedForFetchingRows.realClose(true, false);
/* 1959 */             } catch (SQLException sqlEx) {
/* 1960 */               if (exceptionDuringClose != null) {
/* 1961 */                 exceptionDuringClose.setNextException(sqlEx);
/*      */               } else {
/* 1963 */                 exceptionDuringClose = sqlEx;
/*      */               } 
/*      */             } 
/*      */           }
/*      */           
/* 1968 */           this.rowData = null;
/* 1969 */           this.columnDefinition = null;
/* 1970 */           this.eventSink = null;
/* 1971 */           this.warningChain = null;
/* 1972 */           this.owningStatement = null;
/* 1973 */           this.db = null;
/* 1974 */           this.serverInfo = null;
/* 1975 */           this.thisRow = null;
/* 1976 */           this.fastDefaultCal = null;
/* 1977 */           this.fastClientCal = null;
/* 1978 */           this.connection = null;
/* 1979 */           this.session = null;
/*      */           
/* 1981 */           this.isClosed = true;
/*      */           
/* 1983 */           if (exceptionDuringClose != null)
/* 1984 */             throw exceptionDuringClose; 
/*      */         } 
/*      */       }  return;
/*      */     } catch (CJException cJException) {
/* 1988 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public boolean isClosed() throws SQLException {
/*      */     
/* 1992 */     try { return this.isClosed; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void refreshRow() throws SQLException {
/*      */     
/* 1997 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean relative(int rows) throws SQLException {
/*      */     
/* 2002 */     try { synchronized (checkClosed().getConnectionMutex())
/* 2003 */       { if (!hasRows()) {
/* 2004 */           throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", 
/* 2005 */               getExceptionInterceptor());
/*      */         }
/*      */         
/* 2008 */         if (isStrictlyForwardOnly()) {
/* 2009 */           throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly"));
/*      */         }
/*      */         
/* 2012 */         if (this.rowData.size() == 0) {
/* 2013 */           setRowPositionValidity();
/*      */           
/* 2015 */           return false;
/*      */         } 
/*      */         
/* 2018 */         this.rowData.moveRowRelative(rows);
/* 2019 */         this.thisRow = this.rowData.get(this.rowData.getPosition());
/*      */         
/* 2021 */         setRowPositionValidity();
/*      */         
/* 2023 */         return (!this.rowData.isAfterLast() && !this.rowData.isBeforeFirst()); }  }
/* 2024 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean rowDeleted() throws SQLException {
/*      */     
/* 2029 */     try { throw SQLError.createSQLFeatureNotSupportedException(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean rowInserted() throws SQLException {
/*      */     
/* 2034 */     try { throw SQLError.createSQLFeatureNotSupportedException(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean rowUpdated() throws SQLException {
/*      */     
/* 2039 */     try { throw SQLError.createSQLFeatureNotSupportedException(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void setFetchDirection(int direction) throws SQLException {
/*      */     
/* 2044 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 2045 */         if (direction != 1000 && direction != 1001 && direction != 1002) {
/* 2046 */           throw SQLError.createSQLException(Messages.getString("ResultSet.Illegal_value_for_fetch_direction_64"), "S1009", 
/* 2047 */               getExceptionInterceptor());
/*      */         }
/*      */         
/* 2050 */         if (isStrictlyForwardOnly() && direction != 1000) {
/* 2051 */           String constName = (direction == 1001) ? "ResultSet.FETCH_REVERSE" : "ResultSet.FETCH_UNKNOWN";
/* 2052 */           throw ExceptionFactory.createException(Messages.getString("ResultSet.Unacceptable_value_for_fetch_direction", new Object[] { constName }));
/*      */         } 
/*      */         
/* 2055 */         this.fetchDirection = direction;
/*      */       }  return; }
/* 2057 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setFetchSize(int rows) throws SQLException {
/*      */     
/* 2061 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 2062 */         if (rows < 0) {
/* 2063 */           throw SQLError.createSQLException(Messages.getString("ResultSet.Value_must_be_between_0_and_getMaxRows()_66"), "S1009", 
/* 2064 */               getExceptionInterceptor());
/*      */         }
/*      */         
/* 2067 */         this.fetchSize = rows;
/*      */       }  return; }
/* 2069 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void setFirstCharOfQuery(char c) {
/*      */     try {
/* 2074 */       synchronized (checkClosed().getConnectionMutex()) {
/* 2075 */         this.firstCharOfQuery = c;
/*      */       } 
/* 2077 */     } catch (SQLException e) {
/* 2078 */       throw new RuntimeException(e);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setOwningStatement(JdbcStatement owningStatement) {
/*      */     try {
/* 2085 */       synchronized (checkClosed().getConnectionMutex()) {
/* 2086 */         this.owningStatement = (StatementImpl)owningStatement;
/*      */       } 
/* 2088 */     } catch (SQLException e) {
/* 2089 */       throw new RuntimeException(e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setResultSetConcurrency(int concurrencyFlag) {
/*      */     try {
/* 2101 */       synchronized (checkClosed().getConnectionMutex()) {
/* 2102 */         this.resultSetConcurrency = concurrencyFlag;
/*      */       } 
/* 2104 */     } catch (SQLException e) {
/* 2105 */       throw new RuntimeException(e);
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
/*      */   public synchronized void setResultSetType(int typeFlag) {
/*      */     try {
/* 2118 */       synchronized (checkClosed().getConnectionMutex()) {
/* 2119 */         this.resultSetType = typeFlag;
/*      */       } 
/* 2121 */     } catch (SQLException e) {
/* 2122 */       throw new RuntimeException(e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setServerInfo(String info) {
/*      */     try {
/* 2134 */       synchronized (checkClosed().getConnectionMutex()) {
/* 2135 */         this.serverInfo = info;
/*      */       } 
/* 2137 */     } catch (SQLException e) {
/* 2138 */       throw new RuntimeException(e);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public synchronized void setStatementUsedForFetchingRows(JdbcPreparedStatement stmt) {
/*      */     try {
/* 2145 */       synchronized (checkClosed().getConnectionMutex()) {
/* 2146 */         this.statementUsedForFetchingRows = stmt;
/*      */       } 
/* 2148 */     } catch (SQLException e) {
/* 2149 */       throw new RuntimeException(e);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public synchronized void setWrapperStatement(Statement wrapperStatement) {
/*      */     try {
/* 2156 */       synchronized (checkClosed().getConnectionMutex()) {
/* 2157 */         this.wrapperStatement = wrapperStatement;
/*      */       } 
/* 2159 */     } catch (SQLException e) {
/* 2160 */       throw new RuntimeException(e);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public String toString() {
/* 2166 */     return hasRows() ? super.toString() : ("Result set representing update count of " + this.updateCount);
/*      */   }
/*      */   
/*      */   public void updateArray(int columnIndex, Array arg1) throws SQLException {
/*      */     
/* 2171 */     try { throw SQLError.createSQLFeatureNotSupportedException(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateArray(String columnLabel, Array arg1) throws SQLException {
/*      */     
/* 2176 */     try { throw SQLError.createSQLFeatureNotSupportedException(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
/*      */     
/* 2181 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
/*      */     
/* 2186 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
/*      */     
/* 2191 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException {
/*      */     
/* 2196 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
/*      */     
/* 2201 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
/*      */     
/* 2206 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
/*      */     
/* 2211 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException {
/*      */     
/* 2216 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
/*      */     
/* 2221 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
/*      */     
/* 2226 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
/*      */     
/* 2231 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException {
/*      */     
/* 2236 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
/*      */     
/* 2241 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
/*      */     
/* 2246 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateBlob(int columnIndex, Blob arg1) throws SQLException {
/*      */     
/* 2251 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateBlob(String columnLabel, Blob arg1) throws SQLException {
/*      */     
/* 2256 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
/*      */     
/* 2261 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
/*      */     
/* 2266 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
/*      */     
/* 2271 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
/*      */     
/* 2277 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateBoolean(int columnIndex, boolean x) throws SQLException {
/*      */     
/* 2282 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateBoolean(String columnName, boolean x) throws SQLException {
/*      */     
/* 2287 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateByte(int columnIndex, byte x) throws SQLException {
/*      */     
/* 2292 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateByte(String columnName, byte x) throws SQLException {
/*      */     
/* 2297 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateBytes(int columnIndex, byte[] x) throws SQLException {
/*      */     
/* 2302 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateBytes(String columnName, byte[] x) throws SQLException {
/*      */     
/* 2307 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
/*      */     
/* 2312 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
/*      */     
/* 2317 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
/*      */     
/* 2322 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateCharacterStream(String columnName, Reader reader, int length) throws SQLException {
/*      */     
/* 2327 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
/*      */     
/* 2332 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
/*      */     
/* 2337 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateClob(int columnIndex, Clob arg1) throws SQLException {
/*      */     
/* 2342 */     try { throw SQLError.createSQLFeatureNotSupportedException(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateClob(String columnName, Clob clob) throws SQLException {
/*      */     
/* 2347 */     try { throw SQLError.createSQLFeatureNotSupportedException(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateClob(int columnIndex, Reader reader) throws SQLException {
/*      */     
/* 2352 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateClob(String columnLabel, Reader reader) throws SQLException {
/*      */     
/* 2357 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
/*      */     
/* 2362 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
/*      */     
/* 2367 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateDate(int columnIndex, Date x) throws SQLException {
/*      */     
/* 2372 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateDate(String columnName, Date x) throws SQLException {
/*      */     
/* 2377 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateDouble(int columnIndex, double x) throws SQLException {
/*      */     
/* 2382 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateDouble(String columnName, double x) throws SQLException {
/*      */     
/* 2387 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateFloat(int columnIndex, float x) throws SQLException {
/*      */     
/* 2392 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateFloat(String columnName, float x) throws SQLException {
/*      */     
/* 2397 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateInt(int columnIndex, int x) throws SQLException {
/*      */     
/* 2402 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateInt(String columnName, int x) throws SQLException {
/*      */     
/* 2407 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateLong(int columnIndex, long x) throws SQLException {
/*      */     
/* 2412 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateLong(String columnName, long x) throws SQLException {
/*      */     
/* 2417 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
/*      */     
/* 2422 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
/*      */     
/* 2427 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
/*      */     
/* 2432 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
/*      */     
/* 2437 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
/*      */     
/* 2442 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateNClob(String columnName, NClob nClob) throws SQLException {
/*      */     
/* 2447 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateNClob(int columnIndex, Reader reader) throws SQLException {
/*      */     
/* 2452 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateNClob(String columnLabel, Reader reader) throws SQLException {
/*      */     
/* 2457 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
/*      */     
/* 2462 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
/*      */     
/* 2467 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateNull(int columnIndex) throws SQLException {
/*      */     
/* 2472 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateNull(String columnName) throws SQLException {
/*      */     
/* 2477 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateNString(int columnIndex, String nString) throws SQLException {
/*      */     
/* 2482 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateNString(String columnLabel, String nString) throws SQLException {
/*      */     
/* 2487 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateObject(int columnIndex, Object x) throws SQLException {
/*      */     
/* 2492 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateObject(String columnName, Object x) throws SQLException {
/*      */     
/* 2497 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateObject(int columnIndex, Object x, int scale) throws SQLException {
/*      */     
/* 2502 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateObject(String columnName, Object x, int scale) throws SQLException {
/*      */     
/* 2507 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateObject(int columnIndex, Object x, SQLType targetSqlType) throws SQLException {
/*      */     
/* 2512 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateObject(int columnIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
/*      */     
/* 2517 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateObject(String columnLabel, Object x, SQLType targetSqlType) throws SQLException {
/*      */     
/* 2522 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateObject(String columnLabel, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
/*      */     
/* 2527 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateRef(int columnIndex, Ref arg1) throws SQLException {
/*      */     
/* 2532 */     try { throw SQLError.createSQLFeatureNotSupportedException(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateRef(String columnLabel, Ref arg1) throws SQLException {
/*      */     
/* 2537 */     try { throw SQLError.createSQLFeatureNotSupportedException(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateRow() throws SQLException {
/*      */     
/* 2542 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateRowId(int columnIndex, RowId x) throws SQLException {
/*      */     
/* 2547 */     try { throw SQLError.createSQLFeatureNotSupportedException(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateRowId(String columnName, RowId x) throws SQLException {
/*      */     
/* 2552 */     try { throw SQLError.createSQLFeatureNotSupportedException(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateShort(int columnIndex, short x) throws SQLException {
/*      */     
/* 2557 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateShort(String columnName, short x) throws SQLException {
/*      */     
/* 2562 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
/*      */     
/* 2567 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
/*      */     
/* 2572 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateString(int columnIndex, String x) throws SQLException {
/*      */     
/* 2577 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateString(String columnName, String x) throws SQLException {
/*      */     
/* 2582 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateTime(int columnIndex, Time x) throws SQLException {
/*      */     
/* 2587 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateTime(String columnName, Time x) throws SQLException {
/*      */     
/* 2592 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
/*      */     
/* 2597 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void updateTimestamp(String columnName, Timestamp x) throws SQLException {
/*      */     
/* 2602 */     try { throw new NotUpdatable(Messages.getString("NotUpdatable.0")); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean wasNull() throws SQLException {
/*      */     
/* 2607 */     try { return this.thisRow.wasNull(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   protected ExceptionInterceptor getExceptionInterceptor() {
/* 2611 */     return this.exceptionInterceptor;
/*      */   }
/*      */   
/*      */   public int getHoldability() throws SQLException {
/*      */     
/* 2616 */     try { throw SQLError.createSQLFeatureNotSupportedException(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public RowId getRowId(int columnIndex) throws SQLException {
/*      */     
/* 2621 */     try { throw SQLError.createSQLFeatureNotSupportedException(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public RowId getRowId(String columnLabel) throws SQLException {
/*      */     
/* 2626 */     try { throw SQLError.createSQLFeatureNotSupportedException(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public SQLXML getSQLXML(int columnIndex) throws SQLException {
/*      */     
/* 2631 */     try { checkColumnBounds(columnIndex);
/*      */       
/* 2633 */       return (SQLXML)new MysqlSQLXML(this, columnIndex, getExceptionInterceptor()); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public SQLXML getSQLXML(String columnLabel) throws SQLException {
/*      */     
/* 2638 */     try { return getSQLXML(findColumn(columnLabel)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean isWrapperFor(Class<?> iface) throws SQLException {
/*      */     
/* 2643 */     try { checkClosed();
/*      */ 
/*      */       
/* 2646 */       return iface.isInstance(this); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public <T> T unwrap(Class<T> iface) throws SQLException {
/*      */     try {
/*      */       try {
/* 2653 */         return iface.cast(this);
/* 2654 */       } catch (ClassCastException cce) {
/* 2655 */         throw SQLError.createSQLException(Messages.getString("Common.UnableToUnwrap", new Object[] { iface.toString() }), "S1009", 
/* 2656 */             getExceptionInterceptor());
/*      */       } 
/*      */     } catch (CJException cJException) {
/*      */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public synchronized void warningEncountered(String warning) {
/* 2665 */     SQLWarning w = new SQLWarning(warning);
/* 2666 */     if (this.warningChain == null) {
/* 2667 */       this.warningChain = w;
/*      */     } else {
/* 2669 */       this.warningChain.setNextWarning(w);
/*      */     } 
/*      */   }
/*      */   
/*      */   public ColumnDefinition getMetadata() {
/* 2674 */     return this.columnDefinition;
/*      */   }
/*      */   
/*      */   public StatementImpl getOwningStatement() {
/* 2678 */     return this.owningStatement;
/*      */   }
/*      */ 
/*      */   
/*      */   public void closeOwner(boolean calledExplicitly) {
/*      */     try {
/* 2684 */       realClose(calledExplicitly);
/* 2685 */     } catch (SQLException e) {
/* 2686 */       throw ExceptionFactory.createException(e.getMessage(), e);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public JdbcConnection getConnection() {
/* 2692 */     return this.connection;
/*      */   }
/*      */ 
/*      */   
/*      */   public Session getSession() {
/* 2697 */     return (this.connection != null) ? this.connection.getSession() : null;
/*      */   }
/*      */ 
/*      */   
/*      */   public String getPointOfOrigin() {
/* 2702 */     return this.pointOfOrigin;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getOwnerFetchSize() {
/*      */     try {
/* 2708 */       return getFetchSize();
/* 2709 */     } catch (SQLException e) {
/* 2710 */       throw ExceptionFactory.createException(e.getMessage(), e);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public Query getOwningQuery() {
/* 2716 */     return (Query)this.owningStatement;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getOwningStatementMaxRows() {
/* 2721 */     return (this.owningStatement == null) ? -1 : this.owningStatement.maxRows;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getOwningStatementFetchSize() {
/*      */     try {
/* 2727 */       return (this.owningStatement == null) ? 0 : this.owningStatement.getFetchSize();
/* 2728 */     } catch (SQLException e) {
/* 2729 */       throw ExceptionFactory.createException(e.getMessage(), e);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public long getOwningStatementServerId() {
/* 2735 */     return (this.owningStatement == null) ? 0L : this.owningStatement.getServerStatementId();
/*      */   }
/*      */ 
/*      */   
/*      */   public Object getSyncMutex() {
/* 2740 */     return (this.connection != null) ? this.connection.getConnectionMutex() : null;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\result\ResultSetImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */