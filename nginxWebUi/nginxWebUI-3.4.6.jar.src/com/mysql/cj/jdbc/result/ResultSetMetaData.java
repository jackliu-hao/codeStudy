/*     */ package com.mysql.cj.jdbc.result;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.MysqlType;
/*     */ import com.mysql.cj.Session;
/*     */ import com.mysql.cj.conf.PropertyDefinitions;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.exceptions.CJException;
/*     */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*     */ import com.mysql.cj.jdbc.exceptions.SQLError;
/*     */ import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
/*     */ import com.mysql.cj.result.Field;
/*     */ import java.sql.ResultSetMetaData;
/*     */ import java.sql.SQLException;
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
/*     */ public class ResultSetMetaData
/*     */   implements ResultSetMetaData
/*     */ {
/*     */   private Session session;
/*     */   private Field[] fields;
/*     */   
/*     */   private static int clampedGetLength(Field f) {
/*  49 */     long fieldLength = f.getLength();
/*     */     
/*  51 */     if (fieldLength > 2147483647L) {
/*  52 */       fieldLength = 2147483647L;
/*     */     }
/*     */     
/*  55 */     return (int)fieldLength;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean useOldAliasBehavior = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean treatYearAsDate = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ExceptionInterceptor exceptionInterceptor;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultSetMetaData(Session session, Field[] fields, boolean useOldAliasBehavior, boolean treatYearAsDate, ExceptionInterceptor exceptionInterceptor) {
/*  83 */     this.session = session;
/*  84 */     this.fields = fields;
/*  85 */     this.useOldAliasBehavior = useOldAliasBehavior;
/*  86 */     this.treatYearAsDate = treatYearAsDate;
/*  87 */     this.exceptionInterceptor = exceptionInterceptor;
/*     */   }
/*     */   
/*     */   public String getCatalogName(int column) throws SQLException {
/*     */     
/*  92 */     try { if (this.session.getPropertySet().getEnumProperty(PropertyKey.databaseTerm).getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) {
/*  93 */         return "";
/*     */       }
/*  95 */       String database = getField(column).getDatabaseName();
/*  96 */       return (database == null) ? "" : database; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
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
/*     */ 
/*     */ 
/*     */   
/*     */   public String getColumnCharacterEncoding(int column) throws SQLException {
/* 113 */     return getField(column).getEncoding();
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
/*     */ 
/*     */   
/*     */   public String getColumnCharacterSet(int column) throws SQLException {
/* 128 */     return this.session.getServerSession().getCharsetSettings().getMysqlCharsetNameForCollationIndex(Integer.valueOf(getField(column).getCollationIndex()));
/*     */   }
/*     */   
/*     */   public String getColumnClassName(int column) throws SQLException {
/*     */     
/* 133 */     try { Field f = getField(column);
/*     */       
/* 135 */       switch (f.getMysqlType()) {
/*     */         case YEAR:
/* 137 */           if (!this.treatYearAsDate) {
/* 138 */             return Short.class.getName();
/*     */           }
/* 140 */           return f.getMysqlType().getClassName();
/*     */       } 
/*     */       
/* 143 */       return f.getMysqlType().getClassName(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */ 
/*     */   
/*     */   public int getColumnCount() throws SQLException {
/*     */     
/* 150 */     try { return this.fields.length; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public int getColumnDisplaySize(int column) throws SQLException {
/*     */     
/* 155 */     try { Field f = getField(column);
/*     */       
/* 157 */       int lengthInBytes = clampedGetLength(f);
/*     */       
/* 159 */       return lengthInBytes / this.session.getServerSession().getCharsetSettings().getMaxBytesPerChar(Integer.valueOf(f.getCollationIndex()), f.getEncoding()); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public String getColumnLabel(int column) throws SQLException {
/*     */     
/* 164 */     try { if (this.useOldAliasBehavior) {
/* 165 */         return getColumnName(column);
/*     */       }
/*     */       
/* 168 */       return getField(column).getColumnLabel(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public String getColumnName(int column) throws SQLException {
/*     */     
/* 173 */     try { if (this.useOldAliasBehavior) {
/* 174 */         return getField(column).getName();
/*     */       }
/*     */       
/* 177 */       String name = getField(column).getOriginalName();
/*     */       
/* 179 */       if (name == null) {
/* 180 */         return getField(column).getName();
/*     */       }
/*     */       
/* 183 */       return name; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public int getColumnType(int column) throws SQLException {
/*     */     
/* 188 */     try { return getField(column).getJavaType(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public String getColumnTypeName(int column) throws SQLException {
/*     */     
/* 193 */     try { Field field = getField(column);
/* 194 */       return field.getMysqlType().getName(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
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
/*     */   
/*     */   protected Field getField(int columnIndex) throws SQLException {
/* 209 */     if (columnIndex < 1 || columnIndex > this.fields.length) {
/* 210 */       throw SQLError.createSQLException(Messages.getString("ResultSetMetaData.46"), "S1002", this.exceptionInterceptor);
/*     */     }
/*     */ 
/*     */     
/* 214 */     return this.fields[columnIndex - 1];
/*     */   }
/*     */   
/*     */   public int getPrecision(int column) throws SQLException {
/*     */     try {
/* 219 */       Field f = getField(column);
/*     */       
/* 221 */       switch (f.getMysqlType()) {
/*     */         
/*     */         case TINYBLOB:
/*     */         case BLOB:
/*     */         case MEDIUMBLOB:
/*     */         case LONGBLOB:
/* 227 */           return clampedGetLength(f);
/*     */       } 
/*     */       
/* 230 */       return f.getMysqlType().isDecimal() ? clampedGetLength(f) : (
/* 231 */         clampedGetLength(f) / this.session.getServerSession().getCharsetSettings().getMaxBytesPerChar(Integer.valueOf(f.getCollationIndex()), f.getEncoding()));
/*     */     } catch (CJException cJException) {
/*     */       throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
/*     */     } 
/*     */   }
/*     */   public int getScale(int column) throws SQLException {
/*     */     
/* 238 */     try { Field f = getField(column);
/*     */       
/* 240 */       if (f.getMysqlType().isDecimal()) {
/* 241 */         return f.getDecimals();
/*     */       }
/*     */       
/* 244 */       return 0; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public String getSchemaName(int column) throws SQLException {
/*     */     
/* 249 */     try { if (this.session.getPropertySet().getEnumProperty(PropertyKey.databaseTerm).getValue() == PropertyDefinitions.DatabaseTerm.CATALOG) {
/* 250 */         return "";
/*     */       }
/* 252 */       String database = getField(column).getDatabaseName();
/* 253 */       return (database == null) ? "" : database; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public String getTableName(int column) throws SQLException {
/*     */     
/* 258 */     try { String res = this.useOldAliasBehavior ? getField(column).getTableName() : getField(column).getOriginalTableName();
/* 259 */       return (res == null) ? "" : res; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public boolean isAutoIncrement(int column) throws SQLException {
/*     */     
/* 264 */     try { Field f = getField(column);
/*     */       
/* 266 */       return f.isAutoIncrement(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   } public boolean isCaseSensitive(int column) throws SQLException {
/*     */     
/*     */     try { String collationName;
/* 271 */       Field field = getField(column);
/*     */       
/* 273 */       switch (field.getMysqlType()) {
/*     */         case YEAR:
/*     */         case BIT:
/*     */         case TINYINT:
/*     */         case TINYINT_UNSIGNED:
/*     */         case SMALLINT:
/*     */         case SMALLINT_UNSIGNED:
/*     */         case INT:
/*     */         case INT_UNSIGNED:
/*     */         case MEDIUMINT:
/*     */         case MEDIUMINT_UNSIGNED:
/*     */         case BIGINT:
/*     */         case BIGINT_UNSIGNED:
/*     */         case FLOAT:
/*     */         case FLOAT_UNSIGNED:
/*     */         case DOUBLE:
/*     */         case DOUBLE_UNSIGNED:
/*     */         case DATE:
/*     */         case TIME:
/*     */         case TIMESTAMP:
/*     */         case DATETIME:
/* 294 */           return false;
/*     */         
/*     */         case CHAR:
/*     */         case VARCHAR:
/*     */         case TINYTEXT:
/*     */         case TEXT:
/*     */         case MEDIUMTEXT:
/*     */         case LONGTEXT:
/*     */         case JSON:
/*     */         case ENUM:
/*     */         case SET:
/* 305 */           collationName = this.session.getServerSession().getCharsetSettings().getCollationNameForCollationIndex(Integer.valueOf(field.getCollationIndex()));
/* 306 */           return (collationName != null && !collationName.endsWith("_ci"));
/*     */       } 
/*     */       
/* 309 */       return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   
/*     */   public boolean isCurrency(int column) throws SQLException {
/*     */     
/* 315 */     try { return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public boolean isDefinitelyWritable(int column) throws SQLException {
/*     */     
/* 320 */     try { return isWritable(column); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public int isNullable(int column) throws SQLException {
/*     */     
/* 325 */     try { if (!getField(column).isNotNull()) {
/* 326 */         return 1;
/*     */       }
/*     */       
/* 329 */       return 0; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public boolean isReadOnly(int column) throws SQLException {
/*     */     
/* 334 */     try { return getField(column).isReadOnly(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public boolean isSearchable(int column) throws SQLException {
/*     */     
/* 339 */     try { return true; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public boolean isSigned(int column) throws SQLException {
/*     */     
/* 344 */     try { return MysqlType.isSigned(getField(column).getMysqlType()); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public boolean isWritable(int column) throws SQLException {
/*     */     
/* 349 */     try { return !isReadOnly(column); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   
/*     */   public String toString() {
/* 354 */     StringBuilder toStringBuf = new StringBuilder();
/* 355 */     toStringBuf.append(super.toString());
/* 356 */     toStringBuf.append(" - Field level information: ");
/*     */     
/* 358 */     for (int i = 0; i < this.fields.length; i++) {
/* 359 */       toStringBuf.append("\n\t");
/* 360 */       toStringBuf.append(this.fields[i].toString());
/*     */     } 
/*     */     
/* 363 */     return toStringBuf.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isWrapperFor(Class<?> iface) throws SQLException {
/*     */     
/* 369 */     try { return iface.isInstance(this); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   
/*     */   public <T> T unwrap(Class<T> iface) throws SQLException {
/*     */     
/*     */     try { 
/* 376 */       try { return iface.cast(this); }
/* 377 */       catch (ClassCastException cce)
/* 378 */       { throw SQLError.createSQLException(Messages.getString("Common.UnableToUnwrap", new Object[] { iface.toString() }), "S1009", this.exceptionInterceptor); }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */ 
/*     */   
/*     */   public Field[] getFields() {
/* 384 */     return this.fields;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\result\ResultSetMetaData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */