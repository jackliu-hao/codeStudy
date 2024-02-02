package com.mysql.cj.jdbc.result;

import com.mysql.cj.Messages;
import com.mysql.cj.MysqlType;
import com.mysql.cj.NativeSession;
import com.mysql.cj.Query;
import com.mysql.cj.Session;
import com.mysql.cj.WarningListener;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.conf.RuntimeProperty;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.jdbc.BlobFromLocator;
import com.mysql.cj.jdbc.JdbcConnection;
import com.mysql.cj.jdbc.JdbcPreparedStatement;
import com.mysql.cj.jdbc.JdbcStatement;
import com.mysql.cj.jdbc.MysqlSQLXML;
import com.mysql.cj.jdbc.StatementImpl;
import com.mysql.cj.jdbc.exceptions.NotUpdatable;
import com.mysql.cj.jdbc.exceptions.SQLError;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import com.mysql.cj.log.ProfilerEventHandler;
import com.mysql.cj.protocol.ColumnDefinition;
import com.mysql.cj.protocol.ResultsetRows;
import com.mysql.cj.protocol.a.result.NativeResultset;
import com.mysql.cj.protocol.a.result.OkPacket;
import com.mysql.cj.result.BigDecimalValueFactory;
import com.mysql.cj.result.BinaryStreamValueFactory;
import com.mysql.cj.result.BooleanValueFactory;
import com.mysql.cj.result.ByteValueFactory;
import com.mysql.cj.result.DoubleValueFactory;
import com.mysql.cj.result.DurationValueFactory;
import com.mysql.cj.result.Field;
import com.mysql.cj.result.FloatValueFactory;
import com.mysql.cj.result.IntegerValueFactory;
import com.mysql.cj.result.LocalDateTimeValueFactory;
import com.mysql.cj.result.LocalDateValueFactory;
import com.mysql.cj.result.LocalTimeValueFactory;
import com.mysql.cj.result.LongValueFactory;
import com.mysql.cj.result.OffsetDateTimeValueFactory;
import com.mysql.cj.result.OffsetTimeValueFactory;
import com.mysql.cj.result.Row;
import com.mysql.cj.result.ShortValueFactory;
import com.mysql.cj.result.SqlDateValueFactory;
import com.mysql.cj.result.SqlTimeValueFactory;
import com.mysql.cj.result.SqlTimestampValueFactory;
import com.mysql.cj.result.StringValueFactory;
import com.mysql.cj.result.UtilCalendarValueFactory;
import com.mysql.cj.result.ValueFactory;
import com.mysql.cj.result.ZonedDateTimeValueFactory;
import com.mysql.cj.util.LogUtils;
import com.mysql.cj.util.StringUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Struct;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ResultSetImpl extends NativeResultset implements ResultSetInternalMethods, WarningListener {
   static int resultCounter = 1;
   protected String db = null;
   protected boolean[] columnUsed = null;
   protected volatile JdbcConnection connection;
   protected NativeSession session = null;
   protected int currentRow = -1;
   protected ProfilerEventHandler eventSink = null;
   Calendar fastDefaultCal = null;
   Calendar fastClientCal = null;
   protected int fetchDirection = 1000;
   protected int fetchSize = 0;
   protected char firstCharOfQuery;
   protected boolean isClosed = false;
   private StatementImpl owningStatement;
   private String pointOfOrigin;
   protected int resultSetConcurrency = 0;
   protected int resultSetType = 0;
   JdbcPreparedStatement statementUsedForFetchingRows;
   protected boolean useUsageAdvisor = false;
   protected boolean gatherPerfMetrics = false;
   protected boolean scrollTolerant = false;
   protected SQLWarning warningChain = null;
   protected Statement wrapperStatement;
   private boolean padCharsWithSpace = false;
   private boolean useColumnNamesInFindColumn;
   private ExceptionInterceptor exceptionInterceptor;
   private ValueFactory<Boolean> booleanValueFactory;
   private ValueFactory<Byte> byteValueFactory;
   private ValueFactory<Short> shortValueFactory;
   private ValueFactory<Integer> integerValueFactory;
   private ValueFactory<Long> longValueFactory;
   private ValueFactory<Float> floatValueFactory;
   private ValueFactory<Double> doubleValueFactory;
   private ValueFactory<BigDecimal> bigDecimalValueFactory;
   private ValueFactory<InputStream> binaryStreamValueFactory;
   private ValueFactory<Time> defaultTimeValueFactory;
   private ValueFactory<Timestamp> defaultTimestampValueFactory;
   private ValueFactory<Calendar> defaultUtilCalendarValueFactory;
   private ValueFactory<LocalDate> defaultLocalDateValueFactory;
   private ValueFactory<LocalDateTime> defaultLocalDateTimeValueFactory;
   private ValueFactory<LocalTime> defaultLocalTimeValueFactory;
   private ValueFactory<OffsetTime> defaultOffsetTimeValueFactory;
   private ValueFactory<OffsetDateTime> defaultOffsetDateTimeValueFactory;
   private ValueFactory<ZonedDateTime> defaultZonedDateTimeValueFactory;
   protected RuntimeProperty<Boolean> emulateLocators;
   protected boolean yearIsDateType = true;
   private boolean onValidRow = false;
   private String invalidRowReason = null;

   public ResultSetImpl(OkPacket ok, JdbcConnection conn, StatementImpl creatorStmt) {
      super(ok);
      this.connection = conn;
      this.owningStatement = creatorStmt;
      if (this.connection != null) {
         this.session = (NativeSession)conn.getSession();
         this.exceptionInterceptor = this.connection.getExceptionInterceptor();
         this.padCharsWithSpace = (Boolean)this.connection.getPropertySet().getBooleanProperty(PropertyKey.padCharsWithSpace).getValue();
      }

   }

   public ResultSetImpl(ResultsetRows tuples, JdbcConnection conn, StatementImpl creatorStmt) throws SQLException {
      this.connection = conn;
      this.session = (NativeSession)conn.getSession();
      this.db = creatorStmt != null ? creatorStmt.getCurrentDatabase() : conn.getDatabase();
      this.owningStatement = creatorStmt;
      this.exceptionInterceptor = this.connection.getExceptionInterceptor();
      PropertySet pset = this.connection.getPropertySet();
      this.emulateLocators = pset.getBooleanProperty(PropertyKey.emulateLocators);
      this.padCharsWithSpace = (Boolean)pset.getBooleanProperty(PropertyKey.padCharsWithSpace).getValue();
      this.yearIsDateType = (Boolean)pset.getBooleanProperty(PropertyKey.yearIsDateType).getValue();
      this.useUsageAdvisor = (Boolean)pset.getBooleanProperty(PropertyKey.useUsageAdvisor).getValue();
      this.gatherPerfMetrics = (Boolean)pset.getBooleanProperty(PropertyKey.gatherPerfMetrics).getValue();
      this.scrollTolerant = (Boolean)pset.getBooleanProperty(PropertyKey.scrollTolerantForwardOnly).getValue();
      this.booleanValueFactory = new BooleanValueFactory(pset);
      this.byteValueFactory = new ByteValueFactory(pset);
      this.shortValueFactory = new ShortValueFactory(pset);
      this.integerValueFactory = new IntegerValueFactory(pset);
      this.longValueFactory = new LongValueFactory(pset);
      this.floatValueFactory = new FloatValueFactory(pset);
      this.doubleValueFactory = new DoubleValueFactory(pset);
      this.bigDecimalValueFactory = new BigDecimalValueFactory(pset);
      this.binaryStreamValueFactory = new BinaryStreamValueFactory(pset);
      this.defaultTimeValueFactory = new SqlTimeValueFactory(pset, (Calendar)null, this.session.getServerSession().getDefaultTimeZone(), this);
      this.defaultTimestampValueFactory = new SqlTimestampValueFactory(pset, (Calendar)null, this.session.getServerSession().getDefaultTimeZone(), this.session.getServerSession().getSessionTimeZone());
      this.defaultUtilCalendarValueFactory = new UtilCalendarValueFactory(pset, this.session.getServerSession().getDefaultTimeZone(), this.session.getServerSession().getSessionTimeZone());
      this.defaultLocalDateValueFactory = new LocalDateValueFactory(pset, this);
      this.defaultLocalTimeValueFactory = new LocalTimeValueFactory(pset, this);
      this.defaultLocalDateTimeValueFactory = new LocalDateTimeValueFactory(pset);
      this.defaultOffsetTimeValueFactory = new OffsetTimeValueFactory(pset, this.session.getProtocol().getServerSession().getDefaultTimeZone());
      this.defaultOffsetDateTimeValueFactory = new OffsetDateTimeValueFactory(pset, this.session.getProtocol().getServerSession().getDefaultTimeZone(), this.session.getProtocol().getServerSession().getSessionTimeZone());
      this.defaultZonedDateTimeValueFactory = new ZonedDateTimeValueFactory(pset, this.session.getProtocol().getServerSession().getDefaultTimeZone(), this.session.getProtocol().getServerSession().getSessionTimeZone());
      this.columnDefinition = tuples.getMetadata();
      this.rowData = tuples;
      this.updateCount = (long)this.rowData.size();
      if (this.rowData.size() > 0) {
         if (this.updateCount == 1L && this.thisRow == null) {
            this.rowData.close();
            this.updateCount = -1L;
         }
      } else {
         this.thisRow = null;
      }

      this.rowData.setOwner(this);
      if (this.columnDefinition.getFields() != null) {
         this.initializeWithMetadata();
      }

      this.useColumnNamesInFindColumn = (Boolean)pset.getBooleanProperty(PropertyKey.useColumnNamesInFindColumn).getValue();
      this.setRowPositionValidity();
   }

   public void initializeWithMetadata() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            this.initRowsWithMetadata();
            if (this.useUsageAdvisor) {
               this.columnUsed = new boolean[this.columnDefinition.getFields().length];
               this.pointOfOrigin = LogUtils.findCallingClassAndMethod(new Throwable());
               this.resultId = resultCounter++;
               this.eventSink = this.session.getProfilerEventHandler();
            }

            if (this.gatherPerfMetrics) {
               this.session.getProtocol().getMetricsHolder().incrementNumberOfResultSetsCreated();
               Set<String> tableNamesSet = new HashSet();

               for(int i = 0; i < this.columnDefinition.getFields().length; ++i) {
                  Field f = this.columnDefinition.getFields()[i];
                  String tableName = f.getOriginalTableName();
                  if (tableName == null) {
                     tableName = f.getTableName();
                  }

                  if (tableName != null) {
                     if (this.connection.lowerCaseTableNames()) {
                        tableName = tableName.toLowerCase();
                     }

                     tableNamesSet.add(tableName);
                  }
               }

               this.session.getProtocol().getMetricsHolder().reportNumberOfTablesAccessed(tableNamesSet.size());
            }

         }
      } catch (CJException var9) {
         throw SQLExceptionsMapping.translateException(var9, this.getExceptionInterceptor());
      }
   }

   public boolean absolute(int row) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (!this.hasRows()) {
               throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", this.getExceptionInterceptor());
            } else if (this.isStrictlyForwardOnly()) {
               throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly"));
            } else {
               boolean b;
               if (this.rowData.size() == 0) {
                  b = false;
               } else if (row == 0) {
                  this.beforeFirst();
                  b = false;
               } else if (row == 1) {
                  b = this.first();
               } else if (row == -1) {
                  b = this.last();
               } else if (row > this.rowData.size()) {
                  this.afterLast();
                  b = false;
               } else if (row < 0) {
                  int newRowPosition = this.rowData.size() + row + 1;
                  if (newRowPosition <= 0) {
                     this.beforeFirst();
                     b = false;
                  } else {
                     b = this.absolute(newRowPosition);
                  }
               } else {
                  --row;
                  this.rowData.setCurrentRow(row);
                  this.thisRow = this.rowData.get(row);
                  b = true;
               }

               this.setRowPositionValidity();
               return b;
            }
         }
      } catch (CJException var8) {
         throw SQLExceptionsMapping.translateException(var8, this.getExceptionInterceptor());
      }
   }

   public void afterLast() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (!this.hasRows()) {
               throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", this.getExceptionInterceptor());
            } else if (this.isStrictlyForwardOnly()) {
               throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly"));
            } else {
               if (this.rowData.size() != 0) {
                  this.rowData.afterLast();
                  this.thisRow = null;
               }

               this.setRowPositionValidity();
            }
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void beforeFirst() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (!this.hasRows()) {
               throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", this.getExceptionInterceptor());
            } else if (this.isStrictlyForwardOnly()) {
               throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly"));
            } else if (this.rowData.size() != 0) {
               this.rowData.beforeFirst();
               this.thisRow = null;
               this.setRowPositionValidity();
            }
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void cancelRowUpdates() throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   protected final JdbcConnection checkClosed() throws SQLException {
      JdbcConnection c = this.connection;
      if (c == null) {
         throw SQLError.createSQLException(Messages.getString("ResultSet.Operation_not_allowed_after_ResultSet_closed_144"), "S1000", this.getExceptionInterceptor());
      } else {
         return c;
      }
   }

   protected final void checkColumnBounds(int columnIndex) throws SQLException {
      synchronized(this.checkClosed().getConnectionMutex()) {
         if (columnIndex < 1) {
            throw SQLError.createSQLException(Messages.getString("ResultSet.Column_Index_out_of_range_low", new Object[]{columnIndex, this.columnDefinition.getFields().length}), "S1009", this.getExceptionInterceptor());
         } else if (columnIndex > this.columnDefinition.getFields().length) {
            throw SQLError.createSQLException(Messages.getString("ResultSet.Column_Index_out_of_range_high", new Object[]{columnIndex, this.columnDefinition.getFields().length}), "S1009", this.getExceptionInterceptor());
         } else {
            if (this.useUsageAdvisor) {
               this.columnUsed[columnIndex - 1] = true;
            }

         }
      }
   }

   protected void checkRowPos() throws SQLException {
      this.checkClosed();
      if (!this.onValidRow) {
         throw SQLError.createSQLException(this.invalidRowReason, "S1000", this.getExceptionInterceptor());
      }
   }

   private void setRowPositionValidity() throws SQLException {
      if (!this.rowData.isDynamic() && this.rowData.size() == 0) {
         this.invalidRowReason = Messages.getString("ResultSet.Illegal_operation_on_empty_result_set");
         this.onValidRow = false;
      } else if (this.rowData.isBeforeFirst()) {
         this.invalidRowReason = Messages.getString("ResultSet.Before_start_of_result_set_146");
         this.onValidRow = false;
      } else if (this.rowData.isAfterLast()) {
         this.invalidRowReason = Messages.getString("ResultSet.After_end_of_result_set_148");
         this.onValidRow = false;
      } else {
         this.onValidRow = true;
         this.invalidRowReason = null;
      }

   }

   public void clearWarnings() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            this.warningChain = null;
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void close() throws SQLException {
      try {
         this.realClose(true);
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public void populateCachedMetaData(CachedResultSetMetaData cachedMetaData) throws SQLException {
      try {
         this.columnDefinition.exportTo(cachedMetaData);
         cachedMetaData.setMetadata(this.getMetaData());
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public void deleteRow() throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public int findColumn(String columnName) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            Integer index = this.columnDefinition.findColumn(columnName, this.useColumnNamesInFindColumn, 1);
            if (index == -1) {
               throw SQLError.createSQLException(Messages.getString("ResultSet.Column____112") + columnName + Messages.getString("ResultSet.___not_found._113"), "S0022", this.getExceptionInterceptor());
            } else {
               return index;
            }
         }
      } catch (CJException var7) {
         throw SQLExceptionsMapping.translateException(var7, this.getExceptionInterceptor());
      }
   }

   public boolean first() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (!this.hasRows()) {
               throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", this.getExceptionInterceptor());
            } else if (this.isStrictlyForwardOnly()) {
               throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly"));
            } else {
               boolean b = true;
               if (this.rowData.isEmpty()) {
                  b = false;
               } else {
                  this.rowData.beforeFirst();
                  this.thisRow = (Row)this.rowData.next();
               }

               this.setRowPositionValidity();
               return b;
            }
         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public Array getArray(int columnIndex) throws SQLException {
      try {
         this.checkRowPos();
         this.checkColumnBounds(columnIndex);
         throw SQLError.createSQLFeatureNotSupportedException();
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public Array getArray(String colName) throws SQLException {
      try {
         return this.getArray(this.findColumn(colName));
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public InputStream getAsciiStream(int columnIndex) throws SQLException {
      try {
         this.checkRowPos();
         this.checkColumnBounds(columnIndex);
         return this.getBinaryStream(columnIndex);
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public InputStream getAsciiStream(String columnName) throws SQLException {
      try {
         return this.getAsciiStream(this.findColumn(columnName));
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
      try {
         this.checkRowPos();
         this.checkColumnBounds(columnIndex);
         return (BigDecimal)this.thisRow.getValue(columnIndex - 1, this.bigDecimalValueFactory);
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   /** @deprecated */
   @Deprecated
   public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
      try {
         this.checkRowPos();
         this.checkColumnBounds(columnIndex);
         ValueFactory<BigDecimal> vf = new BigDecimalValueFactory(this.session.getPropertySet(), scale);
         vf.setPropertySet(this.connection.getPropertySet());
         return (BigDecimal)this.thisRow.getValue(columnIndex - 1, vf);
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public BigDecimal getBigDecimal(String columnName) throws SQLException {
      try {
         return this.getBigDecimal(this.findColumn(columnName));
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   /** @deprecated */
   @Deprecated
   public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException {
      try {
         return this.getBigDecimal(this.findColumn(columnName), scale);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public InputStream getBinaryStream(int columnIndex) throws SQLException {
      try {
         this.checkRowPos();
         this.checkColumnBounds(columnIndex);
         return (InputStream)this.thisRow.getValue(columnIndex - 1, this.binaryStreamValueFactory);
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public InputStream getBinaryStream(String columnName) throws SQLException {
      try {
         return this.getBinaryStream(this.findColumn(columnName));
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public Blob getBlob(int columnIndex) throws SQLException {
      try {
         this.checkRowPos();
         this.checkColumnBounds(columnIndex);
         if (this.thisRow.getNull(columnIndex - 1)) {
            return null;
         } else {
            return (Blob)(!(Boolean)this.emulateLocators.getValue() ? new com.mysql.cj.jdbc.Blob(this.thisRow.getBytes(columnIndex - 1), this.getExceptionInterceptor()) : new BlobFromLocator(this, columnIndex, this.getExceptionInterceptor()));
         }
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public Blob getBlob(String colName) throws SQLException {
      try {
         return this.getBlob(this.findColumn(colName));
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public boolean getBoolean(int columnIndex) throws SQLException {
      try {
         Boolean res = (Boolean)this.getObject(columnIndex, Boolean.TYPE);
         return res == null ? false : res;
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public boolean getBoolean(String columnName) throws SQLException {
      try {
         return this.getBoolean(this.findColumn(columnName));
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public byte getByte(int columnIndex) throws SQLException {
      try {
         Byte res = (Byte)this.getObject(columnIndex, Byte.TYPE);
         return res == null ? 0 : res;
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public byte getByte(String columnName) throws SQLException {
      try {
         return this.getByte(this.findColumn(columnName));
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public byte[] getBytes(int columnIndex) throws SQLException {
      try {
         this.checkRowPos();
         this.checkColumnBounds(columnIndex);
         return this.thisRow.getBytes(columnIndex - 1);
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public byte[] getBytes(String columnName) throws SQLException {
      try {
         return this.getBytes(this.findColumn(columnName));
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public Reader getCharacterStream(int columnIndex) throws SQLException {
      try {
         this.checkRowPos();
         this.checkColumnBounds(columnIndex);
         InputStream stream = this.getBinaryStream(columnIndex);
         if (stream == null) {
            return null;
         } else {
            Field f = this.columnDefinition.getFields()[columnIndex - 1];

            try {
               return new InputStreamReader(stream, f.getEncoding());
            } catch (UnsupportedEncodingException var7) {
               SQLException sqlEx = SQLError.createSQLException("Cannot read value with encoding: " + f.getEncoding(), this.exceptionInterceptor);
               sqlEx.initCause(var7);
               throw sqlEx;
            }
         }
      } catch (CJException var8) {
         throw SQLExceptionsMapping.translateException(var8, this.getExceptionInterceptor());
      }
   }

   public Reader getCharacterStream(String columnName) throws SQLException {
      try {
         return this.getCharacterStream(this.findColumn(columnName));
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public Clob getClob(int columnIndex) throws SQLException {
      try {
         String asString = this.getStringForClob(columnIndex);
         return asString == null ? null : new com.mysql.cj.jdbc.Clob(asString, this.getExceptionInterceptor());
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public Clob getClob(String colName) throws SQLException {
      try {
         return this.getClob(this.findColumn(colName));
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public Date getDate(int columnIndex) throws SQLException {
      try {
         this.checkRowPos();
         this.checkColumnBounds(columnIndex);
         return (Date)this.thisRow.getValue(columnIndex - 1, new SqlDateValueFactory(this.session.getPropertySet(), (Calendar)null, this.session.getServerSession().getDefaultTimeZone(), this));
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public Date getDate(int columnIndex, Calendar cal) throws SQLException {
      try {
         this.checkRowPos();
         this.checkColumnBounds(columnIndex);
         return (Date)this.thisRow.getValue(columnIndex - 1, new SqlDateValueFactory(this.session.getPropertySet(), cal, cal != null ? cal.getTimeZone() : this.session.getServerSession().getDefaultTimeZone(), this));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public Date getDate(String columnName) throws SQLException {
      try {
         return this.getDate(this.findColumn(columnName));
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public Date getDate(String columnName, Calendar cal) throws SQLException {
      try {
         return this.getDate(this.findColumn(columnName), cal);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public double getDouble(int columnIndex) throws SQLException {
      try {
         Double res = (Double)this.getObject(columnIndex, Double.TYPE);
         return res == null ? 0.0 : res;
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public double getDouble(String columnName) throws SQLException {
      try {
         return this.getDouble(this.findColumn(columnName));
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public float getFloat(int columnIndex) throws SQLException {
      try {
         Float res = (Float)this.getObject(columnIndex, Float.TYPE);
         return res == null ? 0.0F : res;
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public float getFloat(String columnName) throws SQLException {
      try {
         return this.getFloat(this.findColumn(columnName));
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public int getInt(int columnIndex) throws SQLException {
      try {
         Integer res = (Integer)this.getObject(columnIndex, Integer.TYPE);
         return res == null ? 0 : res;
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public BigInteger getBigInteger(int columnIndex) throws SQLException {
      try {
         String stringVal = this.getString(columnIndex);
         if (stringVal == null) {
            return null;
         } else {
            try {
               return new BigInteger(stringVal);
            } catch (NumberFormatException var5) {
               throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigInteger", new Object[]{columnIndex, stringVal}), "S1009", this.getExceptionInterceptor());
            }
         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public int getInt(String columnName) throws SQLException {
      try {
         return this.getInt(this.findColumn(columnName));
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public long getLong(int columnIndex) throws SQLException {
      try {
         Long res = (Long)this.getObject(columnIndex, Long.TYPE);
         return res == null ? 0L : res;
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public long getLong(String columnName) throws SQLException {
      try {
         return this.getLong(this.findColumn(columnName));
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public short getShort(int columnIndex) throws SQLException {
      try {
         Short res = (Short)this.getObject(columnIndex, Short.TYPE);
         return res == null ? 0 : res;
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public short getShort(String columnName) throws SQLException {
      try {
         return this.getShort(this.findColumn(columnName));
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public String getString(int columnIndex) throws SQLException {
      try {
         this.checkRowPos();
         this.checkColumnBounds(columnIndex);
         Field f = this.columnDefinition.getFields()[columnIndex - 1];
         ValueFactory<String> vf = new StringValueFactory(this.session.getPropertySet());
         String stringVal = (String)this.thisRow.getValue(columnIndex - 1, vf);
         if (this.padCharsWithSpace && stringVal != null && f.getMysqlTypeId() == 254) {
            int maxBytesPerChar = this.session.getServerSession().getCharsetSettings().getMaxBytesPerChar(f.getCollationIndex(), f.getEncoding());
            int fieldLength = (int)f.getLength() / maxBytesPerChar;
            return StringUtils.padString(stringVal, fieldLength);
         } else {
            return stringVal;
         }
      } catch (CJException var8) {
         throw SQLExceptionsMapping.translateException(var8, this.getExceptionInterceptor());
      }
   }

   public String getString(String columnName) throws SQLException {
      try {
         return this.getString(this.findColumn(columnName));
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   private String getStringForClob(int columnIndex) throws SQLException {
      String asString = null;
      String forcedEncoding = this.connection.getPropertySet().getStringProperty(PropertyKey.clobCharacterEncoding).getStringValue();
      if (forcedEncoding == null) {
         asString = this.getString(columnIndex);
      } else {
         byte[] asBytes = this.getBytes(columnIndex);
         if (asBytes != null) {
            asString = StringUtils.toString(asBytes, forcedEncoding);
         }
      }

      return asString;
   }

   public Time getTime(int columnIndex) throws SQLException {
      try {
         this.checkRowPos();
         this.checkColumnBounds(columnIndex);
         return (Time)this.thisRow.getValue(columnIndex - 1, this.defaultTimeValueFactory);
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public Time getTime(int columnIndex, Calendar cal) throws SQLException {
      try {
         this.checkRowPos();
         this.checkColumnBounds(columnIndex);
         ValueFactory<Time> vf = new SqlTimeValueFactory(this.session.getPropertySet(), cal, cal != null ? cal.getTimeZone() : this.session.getServerSession().getDefaultTimeZone());
         return (Time)this.thisRow.getValue(columnIndex - 1, vf);
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public Time getTime(String columnName) throws SQLException {
      try {
         return this.getTime(this.findColumn(columnName));
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public Time getTime(String columnName, Calendar cal) throws SQLException {
      try {
         return this.getTime(this.findColumn(columnName), cal);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public Timestamp getTimestamp(int columnIndex) throws SQLException {
      try {
         this.checkRowPos();
         this.checkColumnBounds(columnIndex);
         return (Timestamp)this.thisRow.getValue(columnIndex - 1, this.defaultTimestampValueFactory);
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public LocalDate getLocalDate(int columnIndex) throws SQLException {
      this.checkRowPos();
      this.checkColumnBounds(columnIndex);
      return (LocalDate)this.thisRow.getValue(columnIndex - 1, this.defaultLocalDateValueFactory);
   }

   public LocalDateTime getLocalDateTime(int columnIndex) throws SQLException {
      this.checkRowPos();
      this.checkColumnBounds(columnIndex);
      return (LocalDateTime)this.thisRow.getValue(columnIndex - 1, this.defaultLocalDateTimeValueFactory);
   }

   public LocalTime getLocalTime(int columnIndex) throws SQLException {
      this.checkRowPos();
      this.checkColumnBounds(columnIndex);
      return (LocalTime)this.thisRow.getValue(columnIndex - 1, this.defaultLocalTimeValueFactory);
   }

   public Calendar getUtilCalendar(int columnIndex) throws SQLException {
      this.checkRowPos();
      this.checkColumnBounds(columnIndex);
      return (Calendar)this.thisRow.getValue(columnIndex - 1, this.defaultUtilCalendarValueFactory);
   }

   public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
      try {
         this.checkRowPos();
         this.checkColumnBounds(columnIndex);
         ValueFactory<Timestamp> vf = new SqlTimestampValueFactory(this.session.getPropertySet(), cal, this.session.getServerSession().getDefaultTimeZone(), this.session.getServerSession().getSessionTimeZone());
         return (Timestamp)this.thisRow.getValue(columnIndex - 1, vf);
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public Timestamp getTimestamp(String columnName) throws SQLException {
      try {
         return this.getTimestamp(this.findColumn(columnName));
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException {
      try {
         return this.getTimestamp(this.findColumn(columnName), cal);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public Reader getNCharacterStream(int columnIndex) throws SQLException {
      try {
         this.checkRowPos();
         this.checkColumnBounds(columnIndex);
         String fieldEncoding = this.columnDefinition.getFields()[columnIndex - 1].getEncoding();
         if (fieldEncoding != null && fieldEncoding.equals("UTF-8")) {
            return this.getCharacterStream(columnIndex);
         } else {
            throw new SQLException("Can not call getNCharacterStream() when field's charset isn't UTF-8");
         }
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public Reader getNCharacterStream(String columnName) throws SQLException {
      try {
         return this.getNCharacterStream(this.findColumn(columnName));
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public NClob getNClob(int columnIndex) throws SQLException {
      try {
         this.checkRowPos();
         this.checkColumnBounds(columnIndex);
         String fieldEncoding = this.columnDefinition.getFields()[columnIndex - 1].getEncoding();
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

   public NClob getNClob(String columnName) throws SQLException {
      try {
         return this.getNClob(this.findColumn(columnName));
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   private String getStringForNClob(int columnIndex) throws SQLException {
      String asString = null;
      String forcedEncoding = "UTF-8";

      try {
         byte[] asBytes = this.getBytes(columnIndex);
         if (asBytes != null) {
            asString = new String(asBytes, forcedEncoding);
         }

         return asString;
      } catch (UnsupportedEncodingException var5) {
         throw SQLError.createSQLException("Unsupported character encoding " + forcedEncoding, "S1009", this.getExceptionInterceptor());
      }
   }

   public String getNString(int columnIndex) throws SQLException {
      try {
         this.checkRowPos();
         this.checkColumnBounds(columnIndex);
         String fieldEncoding = this.columnDefinition.getFields()[columnIndex - 1].getEncoding();
         if (fieldEncoding != null && fieldEncoding.equals("UTF-8")) {
            return this.getString(columnIndex);
         } else {
            throw new SQLException("Can not call getNString() when field's charset isn't UTF-8");
         }
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public String getNString(String columnName) throws SQLException {
      try {
         return this.getNString(this.findColumn(columnName));
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public int getConcurrency() throws SQLException {
      try {
         return 1007;
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public String getCursorName() throws SQLException {
      try {
         throw SQLError.createSQLException(Messages.getString("ResultSet.Positioned_Update_not_supported"), "S1C00", this.getExceptionInterceptor());
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public int getFetchDirection() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            return this.fetchDirection;
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public int getFetchSize() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            return this.fetchSize;
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public char getFirstCharOfQuery() {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            return this.firstCharOfQuery;
         }
      } catch (SQLException var4) {
         throw new RuntimeException(var4);
      }
   }

   public java.sql.ResultSetMetaData getMetaData() throws SQLException {
      try {
         this.checkClosed();
         return new ResultSetMetaData(this.session, this.columnDefinition.getFields(), (Boolean)this.session.getPropertySet().getBooleanProperty(PropertyKey.useOldAliasMetadataBehavior).getValue(), this.yearIsDateType, this.getExceptionInterceptor());
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public Object getObject(int columnIndex) throws SQLException {
      try {
         this.checkRowPos();
         this.checkColumnBounds(columnIndex);
         int columnIndexMinusOne = columnIndex - 1;
         if (this.thisRow.getNull(columnIndexMinusOne)) {
            return null;
         } else {
            Field field = this.columnDefinition.getFields()[columnIndexMinusOne];
            switch (field.getMysqlType()) {
               case BIT:
                  if (!field.isBinary() && !field.isBlob()) {
                     return field.isSingleBit() ? this.getBoolean(columnIndex) : this.getBytes(columnIndex);
                  } else {
                     byte[] data = this.getBytes(columnIndex);
                     if (!(Boolean)this.connection.getPropertySet().getBooleanProperty(PropertyKey.autoDeserialize).getValue()) {
                        return data;
                     } else {
                        Object obj = data;
                        if (data != null && data.length >= 2) {
                           if (data[0] != -84 || data[1] != -19) {
                              return this.getString(columnIndex);
                           }

                           try {
                              ByteArrayInputStream bytesIn = new ByteArrayInputStream(data);
                              ObjectInputStream objIn = new ObjectInputStream(bytesIn);
                              obj = objIn.readObject();
                              objIn.close();
                              bytesIn.close();
                           } catch (ClassNotFoundException var13) {
                              throw SQLError.createSQLException(Messages.getString("ResultSet.Class_not_found___91") + var13.toString() + Messages.getString("ResultSet._while_reading_serialized_object_92"), this.getExceptionInterceptor());
                           } catch (IOException var14) {
                              obj = data;
                           }
                        }

                        return obj;
                     }
                  }
               case BOOLEAN:
                  return this.getBoolean(columnIndex);
               case TINYINT:
                  return Integer.valueOf(this.getByte(columnIndex));
               case TINYINT_UNSIGNED:
               case SMALLINT:
               case SMALLINT_UNSIGNED:
               case MEDIUMINT:
               case MEDIUMINT_UNSIGNED:
               case INT:
                  return this.getInt(columnIndex);
               case INT_UNSIGNED:
               case BIGINT:
                  return this.getLong(columnIndex);
               case BIGINT_UNSIGNED:
                  return this.getBigInteger(columnIndex);
               case DECIMAL:
               case DECIMAL_UNSIGNED:
                  String stringVal = this.getString(columnIndex);
                  if (stringVal != null) {
                     if (stringVal.length() == 0) {
                        return new BigDecimal(0);
                     }

                     try {
                        return new BigDecimal(stringVal);
                     } catch (NumberFormatException var12) {
                        throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[]{stringVal, columnIndex}), "S1009", this.getExceptionInterceptor());
                     }
                  }

                  return null;
               case FLOAT:
               case FLOAT_UNSIGNED:
                  return new Float(this.getFloat(columnIndex));
               case DOUBLE:
               case DOUBLE_UNSIGNED:
                  return new Double(this.getDouble(columnIndex));
               case CHAR:
               case ENUM:
               case SET:
               case VARCHAR:
               case TINYTEXT:
                  return this.getString(columnIndex);
               case TEXT:
               case MEDIUMTEXT:
               case LONGTEXT:
               case JSON:
                  return this.getStringForClob(columnIndex);
               case GEOMETRY:
                  return this.getBytes(columnIndex);
               case BINARY:
               case VARBINARY:
               case TINYBLOB:
               case MEDIUMBLOB:
               case LONGBLOB:
               case BLOB:
                  if (!field.isBinary() && !field.isBlob()) {
                     return this.getBytes(columnIndex);
                  } else {
                     byte[] data = this.getBytes(columnIndex);
                     if (!(Boolean)this.connection.getPropertySet().getBooleanProperty(PropertyKey.autoDeserialize).getValue()) {
                        return data;
                     } else {
                        Object obj = data;
                        if (data != null && data.length >= 2) {
                           if (data[0] != -84 || data[1] != -19) {
                              return this.getString(columnIndex);
                           }

                           try {
                              ByteArrayInputStream bytesIn = new ByteArrayInputStream(data);
                              ObjectInputStream objIn = new ObjectInputStream(bytesIn);
                              obj = objIn.readObject();
                              objIn.close();
                              bytesIn.close();
                           } catch (ClassNotFoundException var10) {
                              throw SQLError.createSQLException(Messages.getString("ResultSet.Class_not_found___91") + var10.toString() + Messages.getString("ResultSet._while_reading_serialized_object_92"), this.getExceptionInterceptor());
                           } catch (IOException var11) {
                              obj = data;
                           }
                        }

                        return obj;
                     }
                  }
               case YEAR:
                  return this.yearIsDateType ? this.getDate(columnIndex) : this.getShort(columnIndex);
               case DATE:
                  return this.getDate(columnIndex);
               case TIME:
                  return this.getTime(columnIndex);
               case TIMESTAMP:
                  return this.getTimestamp(columnIndex);
               case DATETIME:
                  return this.getLocalDateTime(columnIndex);
               default:
                  return this.getString(columnIndex);
            }
         }
      } catch (CJException var15) {
         throw SQLExceptionsMapping.translateException(var15, this.getExceptionInterceptor());
      }
   }

   public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
      try {
         if (type == null) {
            throw SQLError.createSQLException("Type parameter can not be null", "S1009", this.getExceptionInterceptor());
         } else {
            synchronized(this.checkClosed().getConnectionMutex()) {
               if (type.equals(String.class)) {
                  return this.getString(columnIndex);
               } else if (type.equals(BigDecimal.class)) {
                  return this.getBigDecimal(columnIndex);
               } else if (type.equals(BigInteger.class)) {
                  return this.getBigInteger(columnIndex);
               } else if (!type.equals(Boolean.class) && !type.equals(Boolean.TYPE)) {
                  if (!type.equals(Byte.class) && !type.equals(Byte.TYPE)) {
                     if (!type.equals(Short.class) && !type.equals(Short.TYPE)) {
                        if (!type.equals(Integer.class) && !type.equals(Integer.TYPE)) {
                           if (!type.equals(Long.class) && !type.equals(Long.TYPE)) {
                              if (!type.equals(Float.class) && !type.equals(Float.TYPE)) {
                                 if (!type.equals(Double.class) && !type.equals(Double.TYPE)) {
                                    if (type.equals(byte[].class)) {
                                       return this.getBytes(columnIndex);
                                    } else if (type.equals(Date.class)) {
                                       return this.getDate(columnIndex);
                                    } else if (type.equals(Time.class)) {
                                       return this.getTime(columnIndex);
                                    } else if (type.equals(Timestamp.class)) {
                                       return this.getTimestamp(columnIndex);
                                    } else if (type.equals(java.util.Date.class)) {
                                       Timestamp ts = this.getTimestamp(columnIndex);
                                       return ts == null ? null : java.util.Date.from(ts.toInstant());
                                    } else if (type.equals(Calendar.class)) {
                                       return this.getUtilCalendar(columnIndex);
                                    } else if (type.equals(com.mysql.cj.jdbc.Clob.class)) {
                                       return this.getClob(columnIndex);
                                    } else if (type.equals(com.mysql.cj.jdbc.Blob.class)) {
                                       return this.getBlob(columnIndex);
                                    } else if (type.equals(Array.class)) {
                                       return this.getArray(columnIndex);
                                    } else if (type.equals(Ref.class)) {
                                       return this.getRef(columnIndex);
                                    } else if (type.equals(URL.class)) {
                                       return this.getURL(columnIndex);
                                    } else if (type.equals(Struct.class)) {
                                       throw new SQLFeatureNotSupportedException();
                                    } else if (type.equals(RowId.class)) {
                                       return this.getRowId(columnIndex);
                                    } else if (type.equals(NClob.class)) {
                                       return this.getNClob(columnIndex);
                                    } else if (type.equals(SQLXML.class)) {
                                       return this.getSQLXML(columnIndex);
                                    } else if (type.equals(LocalDate.class)) {
                                       return this.getLocalDate(columnIndex);
                                    } else if (type.equals(LocalDateTime.class)) {
                                       return this.getLocalDateTime(columnIndex);
                                    } else if (type.equals(LocalTime.class)) {
                                       return this.getLocalTime(columnIndex);
                                    } else if (type.equals(OffsetDateTime.class)) {
                                       this.checkRowPos();
                                       this.checkColumnBounds(columnIndex);
                                       return this.thisRow.getValue(columnIndex - 1, this.defaultOffsetDateTimeValueFactory);
                                    } else if (type.equals(OffsetTime.class)) {
                                       this.checkRowPos();
                                       this.checkColumnBounds(columnIndex);
                                       return this.thisRow.getValue(columnIndex - 1, this.defaultOffsetTimeValueFactory);
                                    } else if (type.equals(ZonedDateTime.class)) {
                                       this.checkRowPos();
                                       this.checkColumnBounds(columnIndex);
                                       return this.thisRow.getValue(columnIndex - 1, this.defaultZonedDateTimeValueFactory);
                                    } else if (type.equals(Duration.class)) {
                                       this.checkRowPos();
                                       this.checkColumnBounds(columnIndex);
                                       return this.thisRow.getValue(columnIndex - 1, new DurationValueFactory(this.session.getPropertySet()));
                                    } else if ((Boolean)this.connection.getPropertySet().getBooleanProperty(PropertyKey.autoDeserialize).getValue()) {
                                       Object var10000;
                                       try {
                                          var10000 = this.getObject(columnIndex);
                                       } catch (ClassCastException var8) {
                                          SQLException sqlEx = SQLError.createSQLException("Conversion not supported for type " + type.getName(), "S1009", this.getExceptionInterceptor());
                                          sqlEx.initCause(var8);
                                          throw sqlEx;
                                       }

                                       return var10000;
                                    } else {
                                       throw SQLError.createSQLException("Conversion not supported for type " + type.getName(), "S1009", this.getExceptionInterceptor());
                                    }
                                 } else {
                                    this.checkRowPos();
                                    this.checkColumnBounds(columnIndex);
                                    return this.thisRow.getValue(columnIndex - 1, this.doubleValueFactory);
                                 }
                              } else {
                                 this.checkRowPos();
                                 this.checkColumnBounds(columnIndex);
                                 return this.thisRow.getValue(columnIndex - 1, this.floatValueFactory);
                              }
                           } else {
                              this.checkRowPos();
                              this.checkColumnBounds(columnIndex);
                              return this.thisRow.getValue(columnIndex - 1, this.longValueFactory);
                           }
                        } else {
                           this.checkRowPos();
                           this.checkColumnBounds(columnIndex);
                           return this.thisRow.getValue(columnIndex - 1, this.integerValueFactory);
                        }
                     } else {
                        this.checkRowPos();
                        this.checkColumnBounds(columnIndex);
                        return this.thisRow.getValue(columnIndex - 1, this.shortValueFactory);
                     }
                  } else {
                     this.checkRowPos();
                     this.checkColumnBounds(columnIndex);
                     return this.thisRow.getValue(columnIndex - 1, this.byteValueFactory);
                  }
               } else {
                  this.checkRowPos();
                  this.checkColumnBounds(columnIndex);
                  return this.thisRow.getValue(columnIndex - 1, this.booleanValueFactory);
               }
            }
         }
      } catch (CJException var10) {
         throw SQLExceptionsMapping.translateException(var10, this.getExceptionInterceptor());
      }
   }

   public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
      try {
         return this.getObject(this.findColumn(columnLabel), type);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public Object getObject(int i, Map<String, Class<?>> map) throws SQLException {
      try {
         return this.getObject(i);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public Object getObject(String columnName) throws SQLException {
      try {
         return this.getObject(this.findColumn(columnName));
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public Object getObject(String colName, Map<String, Class<?>> map) throws SQLException {
      try {
         return this.getObject(this.findColumn(colName), map);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public Object getObjectStoredProc(int columnIndex, int desiredSqlType) throws SQLException {
      try {
         this.checkRowPos();
         this.checkColumnBounds(columnIndex);
         Object value = this.thisRow.getBytes(columnIndex - 1);
         if (value == null) {
            return null;
         } else {
            Field field = this.columnDefinition.getFields()[columnIndex - 1];
            MysqlType desiredMysqlType = MysqlType.getByJdbcType(desiredSqlType);
            switch (desiredMysqlType) {
               case BIT:
               case BOOLEAN:
                  return this.getBoolean(columnIndex);
               case TINYINT:
               case TINYINT_UNSIGNED:
                  return this.getInt(columnIndex);
               case SMALLINT:
               case SMALLINT_UNSIGNED:
                  return this.getInt(columnIndex);
               case MEDIUMINT:
               case MEDIUMINT_UNSIGNED:
               case INT:
               case INT_UNSIGNED:
                  if (field.isUnsigned() && field.getMysqlTypeId() != 9) {
                     return this.getLong(columnIndex);
                  }

                  return this.getInt(columnIndex);
               case BIGINT:
                  return this.getLong(columnIndex);
               case BIGINT_UNSIGNED:
                  return this.getBigInteger(columnIndex);
               case DECIMAL:
               case DECIMAL_UNSIGNED:
                  String stringVal = this.getString(columnIndex);
                  if (stringVal != null) {
                     BigDecimal val;
                     if (stringVal.length() == 0) {
                        val = new BigDecimal(0);
                        return val;
                     }

                     try {
                        val = new BigDecimal(stringVal);
                        return val;
                     } catch (NumberFormatException var10) {
                        throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[]{stringVal, columnIndex}), "S1009", this.getExceptionInterceptor());
                     }
                  }

                  return null;
               case FLOAT:
               case FLOAT_UNSIGNED:
                  return new Float(this.getFloat(columnIndex));
               case DOUBLE:
               case DOUBLE_UNSIGNED:
                  return new Double(this.getDouble(columnIndex));
               case CHAR:
               case ENUM:
               case SET:
               case VARCHAR:
               case TINYTEXT:
                  return this.getString(columnIndex);
               case TEXT:
               case MEDIUMTEXT:
               case LONGTEXT:
               case JSON:
                  return this.getStringForClob(columnIndex);
               case GEOMETRY:
               case BINARY:
               case VARBINARY:
               case TINYBLOB:
               case MEDIUMBLOB:
               case LONGBLOB:
               case BLOB:
                  return this.getBytes(columnIndex);
               case YEAR:
               case DATE:
                  if (field.getMysqlType() == MysqlType.YEAR && !this.yearIsDateType) {
                     return this.getShort(columnIndex);
                  }

                  return this.getDate(columnIndex);
               case TIME:
                  return this.getTime(columnIndex);
               case TIMESTAMP:
                  return this.getTimestamp(columnIndex);
               default:
                  return this.getString(columnIndex);
            }
         }
      } catch (CJException var11) {
         throw SQLExceptionsMapping.translateException(var11, this.getExceptionInterceptor());
      }
   }

   public Object getObjectStoredProc(int i, Map<Object, Object> map, int desiredSqlType) throws SQLException {
      try {
         return this.getObjectStoredProc(i, desiredSqlType);
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public Object getObjectStoredProc(String columnName, int desiredSqlType) throws SQLException {
      try {
         return this.getObjectStoredProc(this.findColumn(columnName), desiredSqlType);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public Object getObjectStoredProc(String colName, Map<Object, Object> map, int desiredSqlType) throws SQLException {
      try {
         return this.getObjectStoredProc(this.findColumn(colName), map, desiredSqlType);
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public Ref getRef(int i) throws SQLException {
      try {
         this.checkColumnBounds(i);
         throw SQLError.createSQLFeatureNotSupportedException();
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public Ref getRef(String colName) throws SQLException {
      try {
         return this.getRef(this.findColumn(colName));
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public int getRow() throws SQLException {
      try {
         this.checkClosed();
         if (!this.hasRows()) {
            throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", this.getExceptionInterceptor());
         } else {
            int currentRowNumber = this.rowData.getPosition();
            int row = false;
            int row;
            if (!this.rowData.isDynamic()) {
               if (currentRowNumber >= 0 && !this.rowData.isAfterLast() && !this.rowData.isEmpty()) {
                  row = currentRowNumber + 1;
               } else {
                  row = 0;
               }
            } else {
               row = currentRowNumber + 1;
            }

            return row;
         }
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public Statement getStatement() throws SQLException {
      try {
         try {
            synchronized(this.checkClosed().getConnectionMutex()) {
               return (Statement)(this.wrapperStatement != null ? this.wrapperStatement : this.owningStatement);
            }
         } catch (SQLException var5) {
            throw SQLError.createSQLException("Operation not allowed on closed ResultSet.", "S1000", this.getExceptionInterceptor());
         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public int getType() throws SQLException {
      try {
         return this.resultSetType;
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   /** @deprecated */
   @Deprecated
   public InputStream getUnicodeStream(int columnIndex) throws SQLException {
      try {
         this.checkRowPos();
         return this.getBinaryStream(columnIndex);
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   /** @deprecated */
   @Deprecated
   public InputStream getUnicodeStream(String columnName) throws SQLException {
      try {
         return this.getUnicodeStream(this.findColumn(columnName));
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public URL getURL(int colIndex) throws SQLException {
      try {
         String val = this.getString(colIndex);
         if (val == null) {
            return null;
         } else {
            try {
               return new URL(val);
            } catch (MalformedURLException var5) {
               throw SQLError.createSQLException(Messages.getString("ResultSet.Malformed_URL____104") + val + "'", "S1009", this.getExceptionInterceptor());
            }
         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public URL getURL(String colName) throws SQLException {
      try {
         String val = this.getString(colName);
         if (val == null) {
            return null;
         } else {
            try {
               return new URL(val);
            } catch (MalformedURLException var5) {
               throw SQLError.createSQLException(Messages.getString("ResultSet.Malformed_URL____107") + val + "'", "S1009", this.getExceptionInterceptor());
            }
         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public SQLWarning getWarnings() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            return this.warningChain;
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void insertRow() throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public boolean isAfterLast() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (!this.hasRows()) {
               throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", this.getExceptionInterceptor());
            } else {
               return this.rowData.isAfterLast();
            }
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public boolean isBeforeFirst() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (!this.hasRows()) {
               throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", this.getExceptionInterceptor());
            } else {
               return this.rowData.isBeforeFirst();
            }
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public boolean isFirst() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (!this.hasRows()) {
               throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", this.getExceptionInterceptor());
            } else {
               return this.rowData.isFirst();
            }
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public boolean isLast() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (!this.hasRows()) {
               throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", this.getExceptionInterceptor());
            } else {
               return this.rowData.isLast();
            }
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   protected boolean isStrictlyForwardOnly() {
      return this.resultSetType == 1003 && !this.scrollTolerant;
   }

   public boolean last() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (!this.hasRows()) {
               throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", this.getExceptionInterceptor());
            } else if (this.isStrictlyForwardOnly()) {
               throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly"));
            } else {
               boolean b = true;
               if (this.rowData.size() == 0) {
                  b = false;
               } else {
                  this.rowData.beforeLast();
                  this.thisRow = (Row)this.rowData.next();
               }

               this.setRowPositionValidity();
               return b;
            }
         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void moveToCurrentRow() throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public void moveToInsertRow() throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public boolean next() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (!this.hasRows()) {
               throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", this.getExceptionInterceptor());
            } else {
               boolean b;
               if (this.rowData.size() == 0) {
                  b = false;
               } else {
                  this.thisRow = (Row)this.rowData.next();
                  if (this.thisRow == null) {
                     b = false;
                  } else {
                     this.clearWarnings();
                     b = true;
                  }
               }

               this.setRowPositionValidity();
               return b;
            }
         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public boolean prev() throws SQLException {
      synchronized(this.checkClosed().getConnectionMutex()) {
         int rowIndex = this.rowData.getPosition();
         boolean b = true;
         if (rowIndex - 1 >= 0) {
            --rowIndex;
            this.rowData.setCurrentRow(rowIndex);
            this.thisRow = this.rowData.get(rowIndex);
            b = true;
         } else if (rowIndex - 1 == -1) {
            --rowIndex;
            this.rowData.setCurrentRow(rowIndex);
            this.thisRow = null;
            b = false;
         } else {
            b = false;
         }

         this.setRowPositionValidity();
         return b;
      }
   }

   public boolean previous() throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (!this.hasRows()) {
               throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", this.getExceptionInterceptor());
            } else if (this.isStrictlyForwardOnly()) {
               throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly"));
            } else {
               return this.prev();
            }
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void realClose(boolean calledExplicitly) throws SQLException {
      try {
         JdbcConnection locallyScopedConn = this.connection;
         if (locallyScopedConn != null) {
            synchronized(locallyScopedConn.getConnectionMutex()) {
               if (!this.isClosed) {
                  boolean var19 = false;

                  try {
                     var19 = true;
                     if (this.useUsageAdvisor) {
                        if (!calledExplicitly) {
                           this.eventSink.processEvent((byte)0, this.session, this.owningStatement, this, 0L, new Throwable(), Messages.getString("ResultSet.ResultSet_implicitly_closed_by_driver"));
                        }

                        int resultSetSizeThreshold = (Integer)locallyScopedConn.getPropertySet().getIntegerProperty(PropertyKey.resultSetSizeThreshold).getValue();
                        if (this.rowData.size() > resultSetSizeThreshold) {
                           this.eventSink.processEvent((byte)0, this.session, this.owningStatement, this, 0L, new Throwable(), Messages.getString("ResultSet.Too_Large_Result_Set", new Object[]{this.rowData.size(), resultSetSizeThreshold}));
                        }

                        if (!this.isLast() && !this.isAfterLast() && this.rowData.size() != 0) {
                           this.eventSink.processEvent((byte)0, this.session, this.owningStatement, this, 0L, new Throwable(), Messages.getString("ResultSet.Possible_incomplete_traversal_of_result_set", new Object[]{this.getRow(), this.rowData.size()}));
                        }

                        if (this.columnUsed.length > 0) {
                           if (!this.rowData.wasEmpty()) {
                              StringBuilder buf = new StringBuilder();

                              for(int i = 0; i < this.columnUsed.length; ++i) {
                                 if (!this.columnUsed[i]) {
                                    if (buf.length() > 0) {
                                       buf.append(", ");
                                    }

                                    buf.append(this.columnDefinition.getFields()[i].getFullName());
                                 }
                              }

                              if (buf.length() > 0) {
                                 this.eventSink.processEvent((byte)0, this.session, this.owningStatement, this, 0L, new Throwable(), Messages.getString("ResultSet.The_following_columns_were_never_referenced", new String[]{buf.toString()}));
                                 var19 = false;
                              } else {
                                 var19 = false;
                              }
                           } else {
                              var19 = false;
                           }
                        } else {
                           var19 = false;
                        }
                     } else {
                        var19 = false;
                     }
                  } finally {
                     if (var19) {
                        if (this.owningStatement != null && calledExplicitly) {
                           this.owningStatement.removeOpenResultSet(this);
                        }

                        SQLException exceptionDuringClose = null;
                        if (this.rowData != null) {
                           try {
                              this.rowData.close();
                           } catch (CJException var20) {
                              exceptionDuringClose = SQLExceptionsMapping.translateException(var20);
                           }
                        }

                        if (this.statementUsedForFetchingRows != null) {
                           try {
                              this.statementUsedForFetchingRows.realClose(true, false);
                           } catch (SQLException var22) {
                              if (exceptionDuringClose != null) {
                                 exceptionDuringClose.setNextException(var22);
                              } else {
                                 exceptionDuringClose = var22;
                              }
                           }
                        }

                        this.rowData = null;
                        this.columnDefinition = null;
                        this.eventSink = null;
                        this.warningChain = null;
                        this.owningStatement = null;
                        this.db = null;
                        this.serverInfo = null;
                        this.thisRow = null;
                        this.fastDefaultCal = null;
                        this.fastClientCal = null;
                        this.connection = null;
                        this.session = null;
                        this.isClosed = true;
                        if (exceptionDuringClose != null) {
                           throw exceptionDuringClose;
                        }

                     }
                  }

                  if (this.owningStatement != null && calledExplicitly) {
                     this.owningStatement.removeOpenResultSet(this);
                  }

                  SQLException exceptionDuringClose = null;
                  if (this.rowData != null) {
                     try {
                        this.rowData.close();
                     } catch (CJException var21) {
                        exceptionDuringClose = SQLExceptionsMapping.translateException(var21);
                     }
                  }

                  if (this.statementUsedForFetchingRows != null) {
                     try {
                        this.statementUsedForFetchingRows.realClose(true, false);
                     } catch (SQLException var23) {
                        if (exceptionDuringClose != null) {
                           exceptionDuringClose.setNextException(var23);
                        } else {
                           exceptionDuringClose = var23;
                        }
                     }
                  }

                  this.rowData = null;
                  this.columnDefinition = null;
                  this.eventSink = null;
                  this.warningChain = null;
                  this.owningStatement = null;
                  this.db = null;
                  this.serverInfo = null;
                  this.thisRow = null;
                  this.fastDefaultCal = null;
                  this.fastClientCal = null;
                  this.connection = null;
                  this.session = null;
                  this.isClosed = true;
                  if (exceptionDuringClose != null) {
                     throw exceptionDuringClose;
                  }
               }
            }
         }
      } catch (CJException var26) {
         throw SQLExceptionsMapping.translateException(var26, this.getExceptionInterceptor());
      }
   }

   public boolean isClosed() throws SQLException {
      try {
         return this.isClosed;
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public void refreshRow() throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public boolean relative(int rows) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (!this.hasRows()) {
               throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", this.getExceptionInterceptor());
            } else if (this.isStrictlyForwardOnly()) {
               throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly"));
            } else if (this.rowData.size() == 0) {
               this.setRowPositionValidity();
               return false;
            } else {
               this.rowData.moveRowRelative(rows);
               this.thisRow = this.rowData.get(this.rowData.getPosition());
               this.setRowPositionValidity();
               return !this.rowData.isAfterLast() && !this.rowData.isBeforeFirst();
            }
         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
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

   public void setFetchDirection(int direction) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (direction != 1000 && direction != 1001 && direction != 1002) {
               throw SQLError.createSQLException(Messages.getString("ResultSet.Illegal_value_for_fetch_direction_64"), "S1009", this.getExceptionInterceptor());
            } else if (this.isStrictlyForwardOnly() && direction != 1000) {
               String constName = direction == 1001 ? "ResultSet.FETCH_REVERSE" : "ResultSet.FETCH_UNKNOWN";
               throw ExceptionFactory.createException(Messages.getString("ResultSet.Unacceptable_value_for_fetch_direction", new Object[]{constName}));
            } else {
               this.fetchDirection = direction;
            }
         }
      } catch (CJException var7) {
         throw SQLExceptionsMapping.translateException(var7, this.getExceptionInterceptor());
      }
   }

   public void setFetchSize(int rows) throws SQLException {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            if (rows < 0) {
               throw SQLError.createSQLException(Messages.getString("ResultSet.Value_must_be_between_0_and_getMaxRows()_66"), "S1009", this.getExceptionInterceptor());
            } else {
               this.fetchSize = rows;
            }
         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void setFirstCharOfQuery(char c) {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            this.firstCharOfQuery = c;
         }
      } catch (SQLException var5) {
         throw new RuntimeException(var5);
      }
   }

   public void setOwningStatement(JdbcStatement owningStatement) {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            this.owningStatement = (StatementImpl)owningStatement;
         }
      } catch (SQLException var5) {
         throw new RuntimeException(var5);
      }
   }

   public synchronized void setResultSetConcurrency(int concurrencyFlag) {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            this.resultSetConcurrency = concurrencyFlag;
         }
      } catch (SQLException var5) {
         throw new RuntimeException(var5);
      }
   }

   public synchronized void setResultSetType(int typeFlag) {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            this.resultSetType = typeFlag;
         }
      } catch (SQLException var5) {
         throw new RuntimeException(var5);
      }
   }

   public void setServerInfo(String info) {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            this.serverInfo = info;
         }
      } catch (SQLException var5) {
         throw new RuntimeException(var5);
      }
   }

   public synchronized void setStatementUsedForFetchingRows(JdbcPreparedStatement stmt) {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            this.statementUsedForFetchingRows = stmt;
         }
      } catch (SQLException var5) {
         throw new RuntimeException(var5);
      }
   }

   public synchronized void setWrapperStatement(Statement wrapperStatement) {
      try {
         synchronized(this.checkClosed().getConnectionMutex()) {
            this.wrapperStatement = wrapperStatement;
         }
      } catch (SQLException var5) {
         throw new RuntimeException(var5);
      }
   }

   public String toString() {
      return this.hasRows() ? super.toString() : "Result set representing update count of " + this.updateCount;
   }

   public void updateArray(int columnIndex, Array arg1) throws SQLException {
      try {
         throw SQLError.createSQLFeatureNotSupportedException();
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateArray(String columnLabel, Array arg1) throws SQLException {
      try {
         throw SQLError.createSQLFeatureNotSupportedException();
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void updateBlob(int columnIndex, Blob arg1) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateBlob(String columnLabel, Blob arg1) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void updateBoolean(int columnIndex, boolean x) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateBoolean(String columnName, boolean x) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateByte(int columnIndex, byte x) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateByte(String columnName, byte x) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateBytes(int columnIndex, byte[] x) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateBytes(String columnName, byte[] x) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void updateCharacterStream(String columnName, Reader reader, int length) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void updateClob(int columnIndex, Clob arg1) throws SQLException {
      try {
         throw SQLError.createSQLFeatureNotSupportedException();
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateClob(String columnName, Clob clob) throws SQLException {
      try {
         throw SQLError.createSQLFeatureNotSupportedException();
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateClob(int columnIndex, Reader reader) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateClob(String columnLabel, Reader reader) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void updateDate(int columnIndex, Date x) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateDate(String columnName, Date x) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateDouble(int columnIndex, double x) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void updateDouble(String columnName, double x) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void updateFloat(int columnIndex, float x) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateFloat(String columnName, float x) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateInt(int columnIndex, int x) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateInt(String columnName, int x) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateLong(int columnIndex, long x) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void updateLong(String columnName, long x) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateNClob(String columnName, NClob nClob) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateNClob(int columnIndex, Reader reader) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateNClob(String columnLabel, Reader reader) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void updateNull(int columnIndex) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public void updateNull(String columnName) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public void updateNString(int columnIndex, String nString) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateNString(String columnLabel, String nString) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateObject(int columnIndex, Object x) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateObject(String columnName, Object x) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateObject(int columnIndex, Object x, int scale) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void updateObject(String columnName, Object x, int scale) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void updateObject(int columnIndex, Object x, SQLType targetSqlType) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void updateObject(int columnIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void updateObject(String columnLabel, Object x, SQLType targetSqlType) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public void updateObject(String columnLabel, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.getExceptionInterceptor());
      }
   }

   public void updateRef(int columnIndex, Ref arg1) throws SQLException {
      try {
         throw SQLError.createSQLFeatureNotSupportedException();
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateRef(String columnLabel, Ref arg1) throws SQLException {
      try {
         throw SQLError.createSQLFeatureNotSupportedException();
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateRow() throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public void updateRowId(int columnIndex, RowId x) throws SQLException {
      try {
         throw SQLError.createSQLFeatureNotSupportedException();
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateRowId(String columnName, RowId x) throws SQLException {
      try {
         throw SQLError.createSQLFeatureNotSupportedException();
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateShort(int columnIndex, short x) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateShort(String columnName, short x) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateString(int columnIndex, String x) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateString(String columnName, String x) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateTime(int columnIndex, Time x) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateTime(String columnName, Time x) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public void updateTimestamp(String columnName, Timestamp x) throws SQLException {
      try {
         throw new NotUpdatable(Messages.getString("NotUpdatable.0"));
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.getExceptionInterceptor());
      }
   }

   public boolean wasNull() throws SQLException {
      try {
         return this.thisRow.wasNull();
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   protected ExceptionInterceptor getExceptionInterceptor() {
      return this.exceptionInterceptor;
   }

   public int getHoldability() throws SQLException {
      try {
         throw SQLError.createSQLFeatureNotSupportedException();
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.getExceptionInterceptor());
      }
   }

   public RowId getRowId(int columnIndex) throws SQLException {
      try {
         throw SQLError.createSQLFeatureNotSupportedException();
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public RowId getRowId(String columnLabel) throws SQLException {
      try {
         throw SQLError.createSQLFeatureNotSupportedException();
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public SQLXML getSQLXML(int columnIndex) throws SQLException {
      try {
         this.checkColumnBounds(columnIndex);
         return new MysqlSQLXML(this, columnIndex, this.getExceptionInterceptor());
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
      }
   }

   public SQLXML getSQLXML(String columnLabel) throws SQLException {
      try {
         return this.getSQLXML(this.findColumn(columnLabel));
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, this.getExceptionInterceptor());
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
            throw SQLError.createSQLException(Messages.getString("Common.UnableToUnwrap", new Object[]{iface.toString()}), "S1009", this.getExceptionInterceptor());
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.getExceptionInterceptor());
      }
   }

   public synchronized void warningEncountered(String warning) {
      SQLWarning w = new SQLWarning(warning);
      if (this.warningChain == null) {
         this.warningChain = w;
      } else {
         this.warningChain.setNextWarning(w);
      }

   }

   public ColumnDefinition getMetadata() {
      return this.columnDefinition;
   }

   public StatementImpl getOwningStatement() {
      return this.owningStatement;
   }

   public void closeOwner(boolean calledExplicitly) {
      try {
         this.realClose(calledExplicitly);
      } catch (SQLException var3) {
         throw ExceptionFactory.createException((String)var3.getMessage(), (Throwable)var3);
      }
   }

   public JdbcConnection getConnection() {
      return this.connection;
   }

   public Session getSession() {
      return this.connection != null ? this.connection.getSession() : null;
   }

   public String getPointOfOrigin() {
      return this.pointOfOrigin;
   }

   public int getOwnerFetchSize() {
      try {
         return this.getFetchSize();
      } catch (SQLException var2) {
         throw ExceptionFactory.createException((String)var2.getMessage(), (Throwable)var2);
      }
   }

   public Query getOwningQuery() {
      return this.owningStatement;
   }

   public int getOwningStatementMaxRows() {
      return this.owningStatement == null ? -1 : this.owningStatement.maxRows;
   }

   public int getOwningStatementFetchSize() {
      try {
         return this.owningStatement == null ? 0 : this.owningStatement.getFetchSize();
      } catch (SQLException var2) {
         throw ExceptionFactory.createException((String)var2.getMessage(), (Throwable)var2);
      }
   }

   public long getOwningStatementServerId() {
      return this.owningStatement == null ? 0L : this.owningStatement.getServerStatementId();
   }

   public Object getSyncMutex() {
      return this.connection != null ? this.connection.getConnectionMutex() : null;
   }
}
