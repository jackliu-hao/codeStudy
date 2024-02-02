/*     */ package com.mysql.cj.jdbc;
/*     */ 
/*     */ import com.mysql.cj.BindValue;
/*     */ import com.mysql.cj.MysqlType;
/*     */ import com.mysql.cj.PreparedQuery;
/*     */ import com.mysql.cj.QueryBindings;
/*     */ import com.mysql.cj.Session;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.conf.PropertySet;
/*     */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*     */ import com.mysql.cj.jdbc.exceptions.SQLError;
/*     */ import com.mysql.cj.jdbc.result.ResultSetFactory;
/*     */ import com.mysql.cj.jdbc.result.ResultSetImpl;
/*     */ import com.mysql.cj.protocol.ColumnDefinition;
/*     */ import com.mysql.cj.protocol.ResultsetRows;
/*     */ import com.mysql.cj.protocol.a.result.ByteArrayRow;
/*     */ import com.mysql.cj.protocol.a.result.ResultsetRowsStatic;
/*     */ import com.mysql.cj.result.DefaultColumnDefinition;
/*     */ import com.mysql.cj.result.Field;
/*     */ import com.mysql.cj.result.Row;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.net.URL;
/*     */ import java.sql.Array;
/*     */ import java.sql.Blob;
/*     */ import java.sql.Clob;
/*     */ import java.sql.Date;
/*     */ import java.sql.Ref;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ParameterBindingsImpl
/*     */   implements ParameterBindings
/*     */ {
/*     */   private QueryBindings<?> queryBindings;
/*     */   private List<Object> batchedArgs;
/*     */   private PropertySet propertySet;
/*     */   private ExceptionInterceptor exceptionInterceptor;
/*     */   private ResultSetImpl bindingsAsRs;
/*     */   private BindValue[] bindValues;
/*     */   
/*     */   ParameterBindingsImpl(PreparedQuery<?> query, Session session, ResultSetFactory resultSetFactory) throws SQLException {
/*  77 */     this.queryBindings = query.getQueryBindings();
/*  78 */     this.batchedArgs = query.getBatchedArgs();
/*  79 */     this.propertySet = session.getPropertySet();
/*  80 */     this.exceptionInterceptor = session.getExceptionInterceptor();
/*     */     
/*  82 */     List<Row> rows = new ArrayList<>();
/*  83 */     int paramCount = query.getParameterCount();
/*  84 */     this.bindValues = new BindValue[paramCount];
/*  85 */     for (int i = 0; i < paramCount; i++) {
/*  86 */       this.bindValues[i] = this.queryBindings.getBindValues()[i].clone();
/*     */     }
/*  88 */     byte[][] rowData = new byte[paramCount][];
/*  89 */     Field[] typeMetadata = new Field[paramCount];
/*     */     
/*  91 */     for (int j = 0; j < paramCount; j++) {
/*  92 */       int batchCommandIndex = query.getBatchCommandIndex();
/*  93 */       rowData[j] = (batchCommandIndex == -1) ? getBytesRepresentation(j) : getBytesRepresentationForBatch(j, batchCommandIndex);
/*     */       
/*  95 */       int charsetIndex = 0;
/*     */       
/*  97 */       switch (this.queryBindings.getBindValues()[j].getMysqlType()) {
/*     */         case BINARY:
/*     */         case BLOB:
/*     */         case GEOMETRY:
/*     */         case LONGBLOB:
/*     */         case MEDIUMBLOB:
/*     */         case TINYBLOB:
/*     */         case UNKNOWN:
/*     */         case VARBINARY:
/* 106 */           charsetIndex = 63;
/*     */           break;
/*     */         default:
/*     */           try {
/* 110 */             charsetIndex = session.getServerSession().getCharsetSettings().getCollationIndexForJavaEncoding((String)this.propertySet
/* 111 */                 .getStringProperty(PropertyKey.characterEncoding).getValue(), session.getServerSession().getServerVersion());
/* 112 */           } catch (RuntimeException ex) {
/* 113 */             throw SQLError.createSQLException(ex.toString(), "S1009", ex, null);
/*     */           } 
/*     */           break;
/*     */       } 
/*     */ 
/*     */       
/* 119 */       Field parameterMetadata = new Field(null, "parameter_" + (j + 1), charsetIndex, (String)this.propertySet.getStringProperty(PropertyKey.characterEncoding).getValue(), this.queryBindings.getBindValues()[j].getMysqlType(), (rowData[j]).length);
/*     */       
/* 121 */       typeMetadata[j] = parameterMetadata;
/*     */     } 
/*     */     
/* 124 */     rows.add(new ByteArrayRow(rowData, this.exceptionInterceptor));
/*     */     
/* 126 */     this.bindingsAsRs = resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(rows, (ColumnDefinition)new DefaultColumnDefinition(typeMetadata)));
/*     */     
/* 128 */     this.bindingsAsRs.next();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private byte[] getBytesRepresentation(int parameterIndex) {
/* 137 */     return this.queryBindings.getBytesRepresentation(parameterIndex);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private byte[] getBytesRepresentationForBatch(int parameterIndex, int commandIndex) {
/* 150 */     Object batchedArg = this.batchedArgs.get(commandIndex);
/* 151 */     if (batchedArg instanceof String) {
/* 152 */       return StringUtils.getBytes((String)batchedArg, (String)this.propertySet.getStringProperty(PropertyKey.characterEncoding).getValue());
/*     */     }
/*     */     
/* 155 */     return ((QueryBindings)batchedArg).getBytesRepresentation(parameterIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   public Array getArray(int parameterIndex) throws SQLException {
/* 160 */     return this.bindingsAsRs.getArray(parameterIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   public InputStream getAsciiStream(int parameterIndex) throws SQLException {
/* 165 */     return this.bindingsAsRs.getAsciiStream(parameterIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
/* 170 */     return this.bindingsAsRs.getBigDecimal(parameterIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   public InputStream getBinaryStream(int parameterIndex) throws SQLException {
/* 175 */     return this.bindingsAsRs.getBinaryStream(parameterIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   public Blob getBlob(int parameterIndex) throws SQLException {
/* 180 */     return this.bindingsAsRs.getBlob(parameterIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getBoolean(int parameterIndex) throws SQLException {
/* 185 */     return this.bindingsAsRs.getBoolean(parameterIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   public byte getByte(int parameterIndex) throws SQLException {
/* 190 */     return this.bindingsAsRs.getByte(parameterIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getBytes(int parameterIndex) throws SQLException {
/* 195 */     return this.bindingsAsRs.getBytes(parameterIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   public Reader getCharacterStream(int parameterIndex) throws SQLException {
/* 200 */     return this.bindingsAsRs.getCharacterStream(parameterIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   public Clob getClob(int parameterIndex) throws SQLException {
/* 205 */     return this.bindingsAsRs.getClob(parameterIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   public Date getDate(int parameterIndex) throws SQLException {
/* 210 */     return this.bindingsAsRs.getDate(parameterIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   public double getDouble(int parameterIndex) throws SQLException {
/* 215 */     return this.bindingsAsRs.getDouble(parameterIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   public float getFloat(int parameterIndex) throws SQLException {
/* 220 */     return this.bindingsAsRs.getFloat(parameterIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getInt(int parameterIndex) throws SQLException {
/* 225 */     return this.bindingsAsRs.getInt(parameterIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   public BigInteger getBigInteger(int parameterIndex) throws SQLException {
/* 230 */     return this.bindingsAsRs.getBigInteger(parameterIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLong(int parameterIndex) throws SQLException {
/* 235 */     return this.bindingsAsRs.getLong(parameterIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   public Reader getNCharacterStream(int parameterIndex) throws SQLException {
/* 240 */     return this.bindingsAsRs.getCharacterStream(parameterIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   public Reader getNClob(int parameterIndex) throws SQLException {
/* 245 */     return this.bindingsAsRs.getCharacterStream(parameterIndex);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getObject(int parameterIndex) throws SQLException {
/* 252 */     if (this.bindValues[parameterIndex - 1].isNull()) {
/* 253 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 258 */     switch (this.queryBindings.getBindValues()[parameterIndex - 1].getMysqlType()) {
/*     */       case TINYINT:
/*     */       case TINYINT_UNSIGNED:
/* 261 */         return Byte.valueOf(getByte(parameterIndex));
/*     */       case SMALLINT:
/*     */       case SMALLINT_UNSIGNED:
/* 264 */         return Short.valueOf(getShort(parameterIndex));
/*     */       case INT:
/*     */       case INT_UNSIGNED:
/* 267 */         return Integer.valueOf(getInt(parameterIndex));
/*     */       case BIGINT:
/* 269 */         return Long.valueOf(getLong(parameterIndex));
/*     */       case BIGINT_UNSIGNED:
/* 271 */         return getBigInteger(parameterIndex);
/*     */       case FLOAT:
/*     */       case FLOAT_UNSIGNED:
/* 274 */         return Float.valueOf(getFloat(parameterIndex));
/*     */       case DOUBLE:
/*     */       case DOUBLE_UNSIGNED:
/* 277 */         return Double.valueOf(getDouble(parameterIndex));
/*     */     } 
/* 279 */     return this.bindingsAsRs.getObject(parameterIndex);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Ref getRef(int parameterIndex) throws SQLException {
/* 285 */     return this.bindingsAsRs.getRef(parameterIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   public short getShort(int parameterIndex) throws SQLException {
/* 290 */     return this.bindingsAsRs.getShort(parameterIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString(int parameterIndex) throws SQLException {
/* 295 */     return this.bindingsAsRs.getString(parameterIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   public Time getTime(int parameterIndex) throws SQLException {
/* 300 */     return this.bindingsAsRs.getTime(parameterIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   public Timestamp getTimestamp(int parameterIndex) throws SQLException {
/* 305 */     return this.bindingsAsRs.getTimestamp(parameterIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   public URL getURL(int parameterIndex) throws SQLException {
/* 310 */     return this.bindingsAsRs.getURL(parameterIndex);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isNull(int parameterIndex) throws SQLException {
/* 317 */     return this.queryBindings.isNull(parameterIndex - 1);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\ParameterBindingsImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */