/*      */ package org.h2.jdbc;
/*      */ 
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.math.BigDecimal;
/*      */ import java.net.URL;
/*      */ import java.sql.Array;
/*      */ import java.sql.Blob;
/*      */ import java.sql.CallableStatement;
/*      */ import java.sql.Clob;
/*      */ import java.sql.Date;
/*      */ import java.sql.NClob;
/*      */ import java.sql.Ref;
/*      */ import java.sql.ResultSetMetaData;
/*      */ import java.sql.RowId;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLType;
/*      */ import java.sql.SQLXML;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.BitSet;
/*      */ import java.util.Calendar;
/*      */ import java.util.HashMap;
/*      */ import java.util.Map;
/*      */ import org.h2.expression.ParameterInterface;
/*      */ import org.h2.message.DbException;
/*      */ import org.h2.value.Value;
/*      */ import org.h2.value.ValueNull;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class JdbcCallableStatement
/*      */   extends JdbcPreparedStatement
/*      */   implements CallableStatement
/*      */ {
/*      */   private BitSet outParameters;
/*      */   private int maxOutParameters;
/*      */   private HashMap<String, Integer> namedParameters;
/*      */   
/*      */   JdbcCallableStatement(JdbcConnection paramJdbcConnection, String paramString, int paramInt1, int paramInt2, int paramInt3) {
/*   68 */     super(paramJdbcConnection, paramString, paramInt1, paramInt2, paramInt3, (Object)null);
/*   69 */     setTrace(this.session.getTrace(), 0, paramInt1);
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
/*      */ 
/*      */ 
/*      */   
/*      */   public int executeUpdate() throws SQLException {
/*      */     try {
/*   91 */       checkClosed();
/*   92 */       if (this.command.isQuery()) {
/*   93 */         executeQuery();
/*   94 */         return 0;
/*      */       } 
/*   96 */       return super.executeUpdate();
/*   97 */     } catch (Exception exception) {
/*   98 */       throw logAndConvert(exception);
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
/*      */ 
/*      */ 
/*      */   
/*      */   public long executeLargeUpdate() throws SQLException {
/*      */     try {
/*  121 */       checkClosed();
/*  122 */       if (this.command.isQuery()) {
/*  123 */         executeQuery();
/*  124 */         return 0L;
/*      */       } 
/*  126 */       return super.executeLargeUpdate();
/*  127 */     } catch (Exception exception) {
/*  128 */       throw logAndConvert(exception);
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
/*      */   public void registerOutParameter(int paramInt1, int paramInt2) throws SQLException {
/*  141 */     registerOutParameter(paramInt1);
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
/*      */   public void registerOutParameter(int paramInt1, int paramInt2, String paramString) throws SQLException {
/*  154 */     registerOutParameter(paramInt1);
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
/*      */   public void registerOutParameter(int paramInt1, int paramInt2, int paramInt3) throws SQLException {
/*  167 */     registerOutParameter(paramInt1);
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
/*      */   public void registerOutParameter(String paramString1, int paramInt, String paramString2) throws SQLException {
/*  180 */     registerOutParameter(getIndexForName(paramString1), paramInt, paramString2);
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
/*      */   public void registerOutParameter(String paramString, int paramInt1, int paramInt2) throws SQLException {
/*  193 */     registerOutParameter(getIndexForName(paramString), paramInt1, paramInt2);
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
/*      */   public void registerOutParameter(String paramString, int paramInt) throws SQLException {
/*  205 */     registerOutParameter(getIndexForName(paramString), paramInt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean wasNull() throws SQLException {
/*  215 */     return getOpenResultSet().wasNull();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public URL getURL(int paramInt) throws SQLException {
/*  223 */     throw unsupported("url");
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
/*      */   public String getString(int paramInt) throws SQLException {
/*  236 */     checkRegistered(paramInt);
/*  237 */     return getOpenResultSet().getString(paramInt);
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
/*      */   public boolean getBoolean(int paramInt) throws SQLException {
/*  250 */     checkRegistered(paramInt);
/*  251 */     return getOpenResultSet().getBoolean(paramInt);
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
/*      */   public byte getByte(int paramInt) throws SQLException {
/*  264 */     checkRegistered(paramInt);
/*  265 */     return getOpenResultSet().getByte(paramInt);
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
/*      */   public short getShort(int paramInt) throws SQLException {
/*  278 */     checkRegistered(paramInt);
/*  279 */     return getOpenResultSet().getShort(paramInt);
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
/*      */   public int getInt(int paramInt) throws SQLException {
/*  292 */     checkRegistered(paramInt);
/*  293 */     return getOpenResultSet().getInt(paramInt);
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
/*      */   public long getLong(int paramInt) throws SQLException {
/*  306 */     checkRegistered(paramInt);
/*  307 */     return getOpenResultSet().getLong(paramInt);
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
/*      */   public float getFloat(int paramInt) throws SQLException {
/*  320 */     checkRegistered(paramInt);
/*  321 */     return getOpenResultSet().getFloat(paramInt);
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
/*      */   public double getDouble(int paramInt) throws SQLException {
/*  334 */     checkRegistered(paramInt);
/*  335 */     return getOpenResultSet().getDouble(paramInt);
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
/*      */   @Deprecated
/*      */   public BigDecimal getBigDecimal(int paramInt1, int paramInt2) throws SQLException {
/*  353 */     checkRegistered(paramInt1);
/*  354 */     return getOpenResultSet().getBigDecimal(paramInt1, paramInt2);
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
/*      */   public byte[] getBytes(int paramInt) throws SQLException {
/*  367 */     checkRegistered(paramInt);
/*  368 */     return getOpenResultSet().getBytes(paramInt);
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
/*      */   public Date getDate(int paramInt) throws SQLException {
/*  386 */     checkRegistered(paramInt);
/*  387 */     return getOpenResultSet().getDate(paramInt);
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
/*      */   public Time getTime(int paramInt) throws SQLException {
/*  405 */     checkRegistered(paramInt);
/*  406 */     return getOpenResultSet().getTime(paramInt);
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
/*      */   public Timestamp getTimestamp(int paramInt) throws SQLException {
/*  424 */     checkRegistered(paramInt);
/*  425 */     return getOpenResultSet().getTimestamp(paramInt);
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
/*      */   public Object getObject(int paramInt) throws SQLException {
/*  439 */     checkRegistered(paramInt);
/*  440 */     return getOpenResultSet().getObject(paramInt);
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
/*      */   public BigDecimal getBigDecimal(int paramInt) throws SQLException {
/*  453 */     checkRegistered(paramInt);
/*  454 */     return getOpenResultSet().getBigDecimal(paramInt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object getObject(int paramInt, Map<String, Class<?>> paramMap) throws SQLException {
/*  464 */     throw unsupported("map");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Ref getRef(int paramInt) throws SQLException {
/*  472 */     throw unsupported("ref");
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
/*      */   public Blob getBlob(int paramInt) throws SQLException {
/*  485 */     checkRegistered(paramInt);
/*  486 */     return getOpenResultSet().getBlob(paramInt);
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
/*      */   public Clob getClob(int paramInt) throws SQLException {
/*  499 */     checkRegistered(paramInt);
/*  500 */     return getOpenResultSet().getClob(paramInt);
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
/*      */   public Array getArray(int paramInt) throws SQLException {
/*  513 */     checkRegistered(paramInt);
/*  514 */     return getOpenResultSet().getArray(paramInt);
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
/*      */ 
/*      */   
/*      */   public Date getDate(int paramInt, Calendar paramCalendar) throws SQLException {
/*  534 */     checkRegistered(paramInt);
/*  535 */     return getOpenResultSet().getDate(paramInt, paramCalendar);
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
/*      */ 
/*      */   
/*      */   public Time getTime(int paramInt, Calendar paramCalendar) throws SQLException {
/*  555 */     checkRegistered(paramInt);
/*  556 */     return getOpenResultSet().getTime(paramInt, paramCalendar);
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
/*      */ 
/*      */   
/*      */   public Timestamp getTimestamp(int paramInt, Calendar paramCalendar) throws SQLException {
/*  576 */     checkRegistered(paramInt);
/*  577 */     return getOpenResultSet().getTimestamp(paramInt, paramCalendar);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public URL getURL(String paramString) throws SQLException {
/*  585 */     throw unsupported("url");
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
/*      */ 
/*      */   
/*      */   public Timestamp getTimestamp(String paramString, Calendar paramCalendar) throws SQLException {
/*  605 */     return getTimestamp(getIndexForName(paramString), paramCalendar);
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
/*      */ 
/*      */   
/*      */   public Time getTime(String paramString, Calendar paramCalendar) throws SQLException {
/*  625 */     return getTime(getIndexForName(paramString), paramCalendar);
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
/*      */ 
/*      */   
/*      */   public Date getDate(String paramString, Calendar paramCalendar) throws SQLException {
/*  645 */     return getDate(getIndexForName(paramString), paramCalendar);
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
/*      */   public Array getArray(String paramString) throws SQLException {
/*  658 */     return getArray(getIndexForName(paramString));
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
/*      */   public Clob getClob(String paramString) throws SQLException {
/*  671 */     return getClob(getIndexForName(paramString));
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
/*      */   public Blob getBlob(String paramString) throws SQLException {
/*  684 */     return getBlob(getIndexForName(paramString));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Ref getRef(String paramString) throws SQLException {
/*  692 */     throw unsupported("ref");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object getObject(String paramString, Map<String, Class<?>> paramMap) throws SQLException {
/*  702 */     throw unsupported("map");
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
/*      */   public BigDecimal getBigDecimal(String paramString) throws SQLException {
/*  715 */     return getBigDecimal(getIndexForName(paramString));
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
/*      */   public Object getObject(String paramString) throws SQLException {
/*  729 */     return getObject(getIndexForName(paramString));
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
/*      */   public Timestamp getTimestamp(String paramString) throws SQLException {
/*  747 */     return getTimestamp(getIndexForName(paramString));
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
/*      */   public Time getTime(String paramString) throws SQLException {
/*  765 */     return getTime(getIndexForName(paramString));
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
/*      */   public Date getDate(String paramString) throws SQLException {
/*  783 */     return getDate(getIndexForName(paramString));
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
/*      */   public byte[] getBytes(String paramString) throws SQLException {
/*  796 */     return getBytes(getIndexForName(paramString));
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
/*      */   public double getDouble(String paramString) throws SQLException {
/*  809 */     return getDouble(getIndexForName(paramString));
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
/*      */   public float getFloat(String paramString) throws SQLException {
/*  822 */     return getFloat(getIndexForName(paramString));
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
/*      */   public long getLong(String paramString) throws SQLException {
/*  835 */     return getLong(getIndexForName(paramString));
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
/*      */   public int getInt(String paramString) throws SQLException {
/*  848 */     return getInt(getIndexForName(paramString));
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
/*      */   public short getShort(String paramString) throws SQLException {
/*  861 */     return getShort(getIndexForName(paramString));
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
/*      */   public byte getByte(String paramString) throws SQLException {
/*  874 */     return getByte(getIndexForName(paramString));
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
/*      */   public boolean getBoolean(String paramString) throws SQLException {
/*  887 */     return getBoolean(getIndexForName(paramString));
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
/*      */   public String getString(String paramString) throws SQLException {
/*  900 */     return getString(getIndexForName(paramString));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public RowId getRowId(int paramInt) throws SQLException {
/*  910 */     throw unsupported("rowId");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public RowId getRowId(String paramString) throws SQLException {
/*  920 */     throw unsupported("rowId");
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
/*      */   public NClob getNClob(int paramInt) throws SQLException {
/*  933 */     checkRegistered(paramInt);
/*  934 */     return getOpenResultSet().getNClob(paramInt);
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
/*      */   public NClob getNClob(String paramString) throws SQLException {
/*  947 */     return getNClob(getIndexForName(paramString));
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
/*      */   public SQLXML getSQLXML(int paramInt) throws SQLException {
/*  960 */     checkRegistered(paramInt);
/*  961 */     return getOpenResultSet().getSQLXML(paramInt);
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
/*      */   public SQLXML getSQLXML(String paramString) throws SQLException {
/*  974 */     return getSQLXML(getIndexForName(paramString));
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
/*      */   public String getNString(int paramInt) throws SQLException {
/*  987 */     checkRegistered(paramInt);
/*  988 */     return getOpenResultSet().getNString(paramInt);
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
/*      */   public String getNString(String paramString) throws SQLException {
/* 1001 */     return getNString(getIndexForName(paramString));
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
/*      */   public Reader getNCharacterStream(int paramInt) throws SQLException {
/* 1015 */     checkRegistered(paramInt);
/* 1016 */     return getOpenResultSet().getNCharacterStream(paramInt);
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
/*      */   public Reader getNCharacterStream(String paramString) throws SQLException {
/* 1030 */     return getNCharacterStream(getIndexForName(paramString));
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
/*      */   public Reader getCharacterStream(int paramInt) throws SQLException {
/* 1044 */     checkRegistered(paramInt);
/* 1045 */     return getOpenResultSet().getCharacterStream(paramInt);
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
/*      */   public Reader getCharacterStream(String paramString) throws SQLException {
/* 1059 */     return getCharacterStream(getIndexForName(paramString));
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
/*      */   public void setNull(String paramString1, int paramInt, String paramString2) throws SQLException {
/* 1075 */     setNull(getIndexForName(paramString1), paramInt, paramString2);
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
/*      */   public void setNull(String paramString, int paramInt) throws SQLException {
/* 1087 */     setNull(getIndexForName(paramString), paramInt);
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
/*      */ 
/*      */   
/*      */   public void setTimestamp(String paramString, Timestamp paramTimestamp, Calendar paramCalendar) throws SQLException {
/* 1107 */     setTimestamp(getIndexForName(paramString), paramTimestamp, paramCalendar);
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
/*      */ 
/*      */   
/*      */   public void setTime(String paramString, Time paramTime, Calendar paramCalendar) throws SQLException {
/* 1127 */     setTime(getIndexForName(paramString), paramTime, paramCalendar);
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
/*      */ 
/*      */   
/*      */   public void setDate(String paramString, Date paramDate, Calendar paramCalendar) throws SQLException {
/* 1147 */     setDate(getIndexForName(paramString), paramDate, paramCalendar);
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
/*      */   public void setCharacterStream(String paramString, Reader paramReader, int paramInt) throws SQLException {
/* 1163 */     setCharacterStream(getIndexForName(paramString), paramReader, paramInt);
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
/*      */   public void setObject(String paramString, Object paramObject) throws SQLException {
/* 1176 */     setObject(getIndexForName(paramString), paramObject);
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
/*      */   public void setObject(String paramString, Object paramObject, int paramInt) throws SQLException {
/* 1192 */     setObject(getIndexForName(paramString), paramObject, paramInt);
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
/*      */   public void setObject(String paramString, Object paramObject, int paramInt1, int paramInt2) throws SQLException {
/* 1209 */     setObject(getIndexForName(paramString), paramObject, paramInt1, paramInt2);
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
/*      */   public void setObject(String paramString, Object paramObject, SQLType paramSQLType) throws SQLException {
/* 1224 */     setObject(getIndexForName(paramString), paramObject, paramSQLType);
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
/*      */   public void setObject(String paramString, Object paramObject, SQLType paramSQLType, int paramInt) throws SQLException {
/* 1241 */     setObject(getIndexForName(paramString), paramObject, paramSQLType, paramInt);
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
/*      */   public void setBinaryStream(String paramString, InputStream paramInputStream, int paramInt) throws SQLException {
/* 1257 */     setBinaryStream(getIndexForName(paramString), paramInputStream, paramInt);
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
/*      */   public void setAsciiStream(String paramString, InputStream paramInputStream, long paramLong) throws SQLException {
/* 1273 */     setAsciiStream(getIndexForName(paramString), paramInputStream, paramLong);
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
/*      */   public void setTimestamp(String paramString, Timestamp paramTimestamp) throws SQLException {
/* 1291 */     setTimestamp(getIndexForName(paramString), paramTimestamp);
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
/*      */   public void setTime(String paramString, Time paramTime) throws SQLException {
/* 1309 */     setTime(getIndexForName(paramString), paramTime);
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
/*      */   public void setDate(String paramString, Date paramDate) throws SQLException {
/* 1327 */     setDate(getIndexForName(paramString), paramDate);
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
/*      */   public void setBytes(String paramString, byte[] paramArrayOfbyte) throws SQLException {
/* 1339 */     setBytes(getIndexForName(paramString), paramArrayOfbyte);
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
/*      */   public void setString(String paramString1, String paramString2) throws SQLException {
/* 1351 */     setString(getIndexForName(paramString1), paramString2);
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
/*      */   public void setBigDecimal(String paramString, BigDecimal paramBigDecimal) throws SQLException {
/* 1364 */     setBigDecimal(getIndexForName(paramString), paramBigDecimal);
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
/*      */   public void setDouble(String paramString, double paramDouble) throws SQLException {
/* 1376 */     setDouble(getIndexForName(paramString), paramDouble);
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
/*      */   public void setFloat(String paramString, float paramFloat) throws SQLException {
/* 1388 */     setFloat(getIndexForName(paramString), paramFloat);
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
/*      */   public void setLong(String paramString, long paramLong) throws SQLException {
/* 1400 */     setLong(getIndexForName(paramString), paramLong);
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
/*      */   public void setInt(String paramString, int paramInt) throws SQLException {
/* 1412 */     setInt(getIndexForName(paramString), paramInt);
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
/*      */   public void setShort(String paramString, short paramShort) throws SQLException {
/* 1424 */     setShort(getIndexForName(paramString), paramShort);
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
/*      */   public void setByte(String paramString, byte paramByte) throws SQLException {
/* 1436 */     setByte(getIndexForName(paramString), paramByte);
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
/*      */   public void setBoolean(String paramString, boolean paramBoolean) throws SQLException {
/* 1448 */     setBoolean(getIndexForName(paramString), paramBoolean);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setURL(String paramString, URL paramURL) throws SQLException {
/* 1456 */     throw unsupported("url");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setRowId(String paramString, RowId paramRowId) throws SQLException {
/* 1465 */     throw unsupported("rowId");
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
/*      */   public void setNString(String paramString1, String paramString2) throws SQLException {
/* 1478 */     setNString(getIndexForName(paramString1), paramString2);
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
/*      */   public void setNCharacterStream(String paramString, Reader paramReader, long paramLong) throws SQLException {
/* 1494 */     setNCharacterStream(getIndexForName(paramString), paramReader, paramLong);
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
/*      */   public void setNClob(String paramString, NClob paramNClob) throws SQLException {
/* 1507 */     setNClob(getIndexForName(paramString), paramNClob);
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
/*      */   public void setClob(String paramString, Reader paramReader, long paramLong) throws SQLException {
/* 1523 */     setClob(getIndexForName(paramString), paramReader, paramLong);
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
/*      */   public void setBlob(String paramString, InputStream paramInputStream, long paramLong) throws SQLException {
/* 1539 */     setBlob(getIndexForName(paramString), paramInputStream, paramLong);
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
/*      */   public void setNClob(String paramString, Reader paramReader, long paramLong) throws SQLException {
/* 1555 */     setNClob(getIndexForName(paramString), paramReader, paramLong);
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
/*      */   public void setBlob(String paramString, Blob paramBlob) throws SQLException {
/* 1568 */     setBlob(getIndexForName(paramString), paramBlob);
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
/*      */   public void setClob(String paramString, Clob paramClob) throws SQLException {
/* 1580 */     setClob(getIndexForName(paramString), paramClob);
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
/*      */   public void setAsciiStream(String paramString, InputStream paramInputStream) throws SQLException {
/* 1595 */     setAsciiStream(getIndexForName(paramString), paramInputStream);
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
/*      */   public void setAsciiStream(String paramString, InputStream paramInputStream, int paramInt) throws SQLException {
/* 1611 */     setAsciiStream(getIndexForName(paramString), paramInputStream, paramInt);
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
/*      */   public void setBinaryStream(String paramString, InputStream paramInputStream) throws SQLException {
/* 1626 */     setBinaryStream(getIndexForName(paramString), paramInputStream);
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
/*      */   public void setBinaryStream(String paramString, InputStream paramInputStream, long paramLong) throws SQLException {
/* 1642 */     setBinaryStream(getIndexForName(paramString), paramInputStream, paramLong);
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
/*      */   public void setBlob(String paramString, InputStream paramInputStream) throws SQLException {
/* 1657 */     setBlob(getIndexForName(paramString), paramInputStream);
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
/*      */   public void setCharacterStream(String paramString, Reader paramReader) throws SQLException {
/* 1672 */     setCharacterStream(getIndexForName(paramString), paramReader);
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
/*      */   public void setCharacterStream(String paramString, Reader paramReader, long paramLong) throws SQLException {
/* 1688 */     setCharacterStream(getIndexForName(paramString), paramReader, paramLong);
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
/*      */   public void setClob(String paramString, Reader paramReader) throws SQLException {
/* 1702 */     setClob(getIndexForName(paramString), paramReader);
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
/*      */   public void setNCharacterStream(String paramString, Reader paramReader) throws SQLException {
/* 1717 */     setNCharacterStream(getIndexForName(paramString), paramReader);
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
/*      */   public void setNClob(String paramString, Reader paramReader) throws SQLException {
/* 1732 */     setNClob(getIndexForName(paramString), paramReader);
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
/*      */   public void setSQLXML(String paramString, SQLXML paramSQLXML) throws SQLException {
/* 1745 */     setSQLXML(getIndexForName(paramString), paramSQLXML);
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
/*      */   public <T> T getObject(int paramInt, Class<T> paramClass) throws SQLException {
/* 1760 */     return getOpenResultSet().getObject(paramInt, paramClass);
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
/*      */   public <T> T getObject(String paramString, Class<T> paramClass) throws SQLException {
/* 1775 */     return getObject(getIndexForName(paramString), paramClass);
/*      */   }
/*      */   
/*      */   private ResultSetMetaData getCheckedMetaData() throws SQLException {
/* 1779 */     ResultSetMetaData resultSetMetaData = getMetaData();
/* 1780 */     if (resultSetMetaData == null) {
/* 1781 */       throw DbException.getUnsupportedException("Supported only for calling stored procedures");
/*      */     }
/*      */     
/* 1784 */     return resultSetMetaData;
/*      */   }
/*      */   
/*      */   private void checkIndexBounds(int paramInt) {
/* 1788 */     checkClosed();
/* 1789 */     if (paramInt < 1 || paramInt > this.maxOutParameters) {
/* 1790 */       throw DbException.getInvalidValueException("parameterIndex", Integer.valueOf(paramInt));
/*      */     }
/*      */   }
/*      */   
/*      */   private void registerOutParameter(int paramInt) throws SQLException {
/*      */     try {
/* 1796 */       checkClosed();
/* 1797 */       if (this.outParameters == null) {
/* 1798 */         this.maxOutParameters = Math.min(
/* 1799 */             getParameterMetaData().getParameterCount(), 
/* 1800 */             getCheckedMetaData().getColumnCount());
/* 1801 */         this.outParameters = new BitSet();
/*      */       } 
/* 1803 */       checkIndexBounds(paramInt);
/* 1804 */       ParameterInterface parameterInterface = this.command.getParameters().get(--paramInt);
/* 1805 */       if (!parameterInterface.isValueSet()) {
/* 1806 */         parameterInterface.setValue((Value)ValueNull.INSTANCE, false);
/*      */       }
/* 1808 */       this.outParameters.set(paramInt);
/* 1809 */     } catch (Exception exception) {
/* 1810 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void checkRegistered(int paramInt) throws SQLException {
/*      */     try {
/* 1816 */       checkIndexBounds(paramInt);
/* 1817 */       if (!this.outParameters.get(paramInt - 1)) {
/* 1818 */         throw DbException.getInvalidValueException("parameterIndex", Integer.valueOf(paramInt));
/*      */       }
/* 1820 */     } catch (Exception exception) {
/* 1821 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */   
/*      */   private int getIndexForName(String paramString) throws SQLException {
/*      */     try {
/* 1827 */       checkClosed();
/* 1828 */       if (this.namedParameters == null) {
/* 1829 */         ResultSetMetaData resultSetMetaData = getCheckedMetaData();
/* 1830 */         int i = resultSetMetaData.getColumnCount();
/* 1831 */         HashMap<Object, Object> hashMap = new HashMap<>();
/* 1832 */         for (byte b = 1; b <= i; b++) {
/* 1833 */           hashMap.put(resultSetMetaData.getColumnLabel(b), Integer.valueOf(b));
/*      */         }
/* 1835 */         this.namedParameters = (HashMap)hashMap;
/*      */       } 
/* 1837 */       Integer integer = this.namedParameters.get(paramString);
/* 1838 */       if (integer == null) {
/* 1839 */         throw DbException.getInvalidValueException("parameterName", paramString);
/*      */       }
/* 1841 */       return integer.intValue();
/* 1842 */     } catch (Exception exception) {
/* 1843 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */   
/*      */   private JdbcResultSet getOpenResultSet() throws SQLException {
/*      */     try {
/* 1849 */       checkClosed();
/* 1850 */       if (this.resultSet == null) {
/* 1851 */         throw DbException.get(2000);
/*      */       }
/* 1853 */       if (this.resultSet.isBeforeFirst()) {
/* 1854 */         this.resultSet.next();
/*      */       }
/* 1856 */       return this.resultSet;
/* 1857 */     } catch (Exception exception) {
/* 1858 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\jdbc\JdbcCallableStatement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */