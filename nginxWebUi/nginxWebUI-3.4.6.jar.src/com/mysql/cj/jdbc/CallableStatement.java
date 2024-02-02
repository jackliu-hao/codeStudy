/*      */ package com.mysql.cj.jdbc;
/*      */ 
/*      */ import com.mysql.cj.Messages;
/*      */ import com.mysql.cj.MysqlType;
/*      */ import com.mysql.cj.ParseInfo;
/*      */ import com.mysql.cj.PreparedQuery;
/*      */ import com.mysql.cj.conf.PropertyDefinitions;
/*      */ import com.mysql.cj.conf.PropertyKey;
/*      */ import com.mysql.cj.exceptions.CJException;
/*      */ import com.mysql.cj.exceptions.FeatureNotAvailableException;
/*      */ import com.mysql.cj.jdbc.exceptions.SQLError;
/*      */ import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
/*      */ import com.mysql.cj.jdbc.result.ResultSetImpl;
/*      */ import com.mysql.cj.jdbc.result.ResultSetInternalMethods;
/*      */ import com.mysql.cj.protocol.ColumnDefinition;
/*      */ import com.mysql.cj.protocol.ResultsetRows;
/*      */ import com.mysql.cj.protocol.a.result.ByteArrayRow;
/*      */ import com.mysql.cj.protocol.a.result.ResultsetRowsStatic;
/*      */ import com.mysql.cj.result.DefaultColumnDefinition;
/*      */ import com.mysql.cj.result.Field;
/*      */ import com.mysql.cj.result.Row;
/*      */ import com.mysql.cj.util.SearchMode;
/*      */ import com.mysql.cj.util.StringUtils;
/*      */ import com.mysql.cj.util.Util;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.math.BigDecimal;
/*      */ import java.net.URL;
/*      */ import java.sql.Array;
/*      */ import java.sql.Blob;
/*      */ import java.sql.CallableStatement;
/*      */ import java.sql.Clob;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.Date;
/*      */ import java.sql.JDBCType;
/*      */ import java.sql.NClob;
/*      */ import java.sql.ParameterMetaData;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.Ref;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.RowId;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLType;
/*      */ import java.sql.SQLXML;
/*      */ import java.sql.Statement;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class CallableStatement
/*      */   extends ClientPreparedStatement
/*      */   implements CallableStatement
/*      */ {
/*      */   private static final int NOT_OUTPUT_PARAMETER_INDICATOR = -2147483648;
/*      */   private static final String PARAMETER_NAMESPACE_PREFIX = "@com_mysql_jdbc_outparam_";
/*      */   
/*      */   protected static class CallableStatementParam
/*      */   {
/*      */     int index;
/*      */     int inOutModifier;
/*      */     boolean isIn;
/*      */     boolean isOut;
/*      */     int jdbcType;
/*      */     short nullability;
/*      */     String paramName;
/*      */     int precision;
/*      */     int scale;
/*      */     String typeName;
/*  106 */     MysqlType desiredMysqlType = MysqlType.UNKNOWN;
/*      */ 
/*      */     
/*      */     CallableStatementParam(String name, int idx, boolean in, boolean out, int jdbcType, String typeName, int precision, int scale, short nullability, int inOutModifier) {
/*  110 */       this.paramName = name;
/*  111 */       this.isIn = in;
/*  112 */       this.isOut = out;
/*  113 */       this.index = idx;
/*      */       
/*  115 */       this.jdbcType = jdbcType;
/*  116 */       this.typeName = typeName;
/*  117 */       this.precision = precision;
/*  118 */       this.scale = scale;
/*  119 */       this.nullability = nullability;
/*  120 */       this.inOutModifier = inOutModifier;
/*      */     }
/*      */ 
/*      */     
/*      */     protected Object clone() throws CloneNotSupportedException {
/*  125 */       return super.clone();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public class CallableStatementParamInfo
/*      */     implements ParameterMetaData
/*      */   {
/*      */     String dbInUse;
/*      */ 
/*      */     
/*      */     boolean isFunctionCall;
/*      */ 
/*      */     
/*      */     String nativeSql;
/*      */ 
/*      */     
/*      */     int numParameters;
/*      */ 
/*      */     
/*      */     List<CallableStatement.CallableStatementParam> parameterList;
/*      */ 
/*      */     
/*      */     Map<String, CallableStatement.CallableStatementParam> parameterMap;
/*      */ 
/*      */     
/*      */     boolean isReadOnlySafeProcedure = false;
/*      */ 
/*      */     
/*      */     boolean isReadOnlySafeChecked = false;
/*      */ 
/*      */ 
/*      */     
/*      */     CallableStatementParamInfo(CallableStatementParamInfo fullParamInfo) {
/*  161 */       this.nativeSql = ((PreparedQuery)CallableStatement.this.query).getOriginalSql();
/*  162 */       this.dbInUse = CallableStatement.this.getCurrentDatabase();
/*  163 */       this.isFunctionCall = fullParamInfo.isFunctionCall;
/*      */       
/*  165 */       int[] localParameterMap = CallableStatement.this.placeholderToParameterIndexMap;
/*  166 */       int parameterMapLength = localParameterMap.length;
/*      */       
/*  168 */       this.isReadOnlySafeProcedure = fullParamInfo.isReadOnlySafeProcedure;
/*  169 */       this.isReadOnlySafeChecked = fullParamInfo.isReadOnlySafeChecked;
/*  170 */       this.parameterList = new ArrayList<>(fullParamInfo.numParameters);
/*  171 */       this.parameterMap = new HashMap<>(fullParamInfo.numParameters);
/*      */       
/*  173 */       if (this.isFunctionCall)
/*      */       {
/*  175 */         this.parameterList.add(fullParamInfo.parameterList.get(0));
/*      */       }
/*      */       
/*  178 */       int offset = this.isFunctionCall ? 1 : 0;
/*      */       
/*  180 */       for (int i = 0; i < parameterMapLength; i++) {
/*  181 */         if (localParameterMap[i] != 0) {
/*  182 */           CallableStatement.CallableStatementParam param = fullParamInfo.parameterList.get(localParameterMap[i] + offset);
/*      */           
/*  184 */           this.parameterList.add(param);
/*  185 */           this.parameterMap.put(param.paramName, param);
/*      */         } 
/*      */       } 
/*      */       
/*  189 */       this.numParameters = this.parameterList.size();
/*      */     }
/*      */ 
/*      */     
/*      */     CallableStatementParamInfo(ResultSet paramTypesRs) throws SQLException {
/*  194 */       boolean hadRows = paramTypesRs.last();
/*      */       
/*  196 */       this.nativeSql = ((PreparedQuery)CallableStatement.this.query).getOriginalSql();
/*  197 */       this.dbInUse = CallableStatement.this.getCurrentDatabase();
/*  198 */       this.isFunctionCall = CallableStatement.this.callingStoredFunction;
/*      */       
/*  200 */       if (hadRows) {
/*  201 */         this.numParameters = paramTypesRs.getRow();
/*      */         
/*  203 */         this.parameterList = new ArrayList<>(this.numParameters);
/*  204 */         this.parameterMap = new HashMap<>(this.numParameters);
/*      */         
/*  206 */         paramTypesRs.beforeFirst();
/*      */         
/*  208 */         addParametersFromDBMD(paramTypesRs);
/*      */       } else {
/*  210 */         this.numParameters = 0;
/*      */       } 
/*      */       
/*  213 */       if (this.isFunctionCall) {
/*  214 */         this.numParameters++;
/*      */       }
/*      */     }
/*      */     
/*      */     private void addParametersFromDBMD(ResultSet paramTypesRs) throws SQLException {
/*  219 */       int i = 0;
/*      */       
/*  221 */       while (paramTypesRs.next()) {
/*  222 */         int inOutModifier; String paramName = paramTypesRs.getString(4);
/*      */         
/*  224 */         switch (paramTypesRs.getInt(5)) {
/*      */           case 1:
/*  226 */             inOutModifier = 1;
/*      */             break;
/*      */           case 2:
/*  229 */             inOutModifier = 2;
/*      */             break;
/*      */           case 4:
/*      */           case 5:
/*  233 */             inOutModifier = 4;
/*      */             break;
/*      */           default:
/*  236 */             inOutModifier = 0;
/*      */             break;
/*      */         } 
/*  239 */         boolean isOutParameter = false;
/*  240 */         boolean isInParameter = false;
/*      */         
/*  242 */         if (i == 0 && this.isFunctionCall) {
/*  243 */           isOutParameter = true;
/*  244 */           isInParameter = false;
/*  245 */         } else if (inOutModifier == 2) {
/*  246 */           isOutParameter = true;
/*  247 */           isInParameter = true;
/*  248 */         } else if (inOutModifier == 1) {
/*  249 */           isOutParameter = false;
/*  250 */           isInParameter = true;
/*  251 */         } else if (inOutModifier == 4) {
/*  252 */           isOutParameter = true;
/*  253 */           isInParameter = false;
/*      */         } 
/*      */         
/*  256 */         int jdbcType = paramTypesRs.getInt(6);
/*  257 */         String typeName = paramTypesRs.getString(7);
/*  258 */         int precision = paramTypesRs.getInt(8);
/*  259 */         int scale = paramTypesRs.getInt(10);
/*  260 */         short nullability = paramTypesRs.getShort(12);
/*      */         
/*  262 */         CallableStatement.CallableStatementParam paramInfoToAdd = new CallableStatement.CallableStatementParam(paramName, i++, isInParameter, isOutParameter, jdbcType, typeName, precision, scale, nullability, inOutModifier);
/*      */ 
/*      */         
/*  265 */         this.parameterList.add(paramInfoToAdd);
/*  266 */         this.parameterMap.put(paramName, paramInfoToAdd);
/*      */       } 
/*      */     }
/*      */     
/*      */     protected void checkBounds(int paramIndex) throws SQLException {
/*  271 */       int localParamIndex = paramIndex - 1;
/*      */       
/*  273 */       if (paramIndex < 0 || localParamIndex >= this.numParameters) {
/*  274 */         throw SQLError.createSQLException(Messages.getString("CallableStatement.11", new Object[] { Integer.valueOf(paramIndex), Integer.valueOf(this.numParameters) }), "S1009", CallableStatement.this
/*  275 */             .getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     protected Object clone() throws CloneNotSupportedException {
/*  281 */       return super.clone();
/*      */     }
/*      */     
/*      */     CallableStatement.CallableStatementParam getParameter(int index) {
/*  285 */       return this.parameterList.get(index);
/*      */     }
/*      */     
/*      */     CallableStatement.CallableStatementParam getParameter(String name) {
/*  289 */       return this.parameterMap.get(name);
/*      */     }
/*      */     
/*      */     public String getParameterClassName(int arg0) throws SQLException {
/*      */       
/*  294 */       try { String mysqlTypeName = getParameterTypeName(arg0);
/*      */         
/*  296 */         MysqlType mysqlType = MysqlType.getByName(mysqlTypeName);
/*  297 */         switch (mysqlType) {
/*      */           case YEAR:
/*  299 */             if (!((Boolean)CallableStatement.this.session.getPropertySet().getBooleanProperty(PropertyKey.yearIsDateType).getValue()).booleanValue()) {
/*  300 */               return Short.class.getName();
/*      */             }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  310 */             return mysqlType.getClassName();
/*      */         } 
/*      */         
/*  313 */         return mysqlType.getClassName(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
/*      */     
/*      */     }
/*      */ 
/*      */     
/*      */     public int getParameterCount() throws SQLException {
/*      */       
/*  320 */       try { if (this.parameterList == null) {
/*  321 */           return 0;
/*      */         }
/*      */         
/*  324 */         return this.parameterList.size(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
/*      */     
/*      */     }
/*      */     public int getParameterMode(int arg0) throws SQLException {
/*      */       
/*  329 */       try { checkBounds(arg0);
/*      */         
/*  331 */         return (getParameter(arg0 - 1)).inOutModifier; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
/*      */     
/*      */     }
/*      */     public int getParameterType(int arg0) throws SQLException {
/*      */       
/*  336 */       try { checkBounds(arg0);
/*      */         
/*  338 */         return (getParameter(arg0 - 1)).jdbcType; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
/*      */     
/*      */     }
/*      */     public String getParameterTypeName(int arg0) throws SQLException {
/*      */       
/*  343 */       try { checkBounds(arg0);
/*      */         
/*  345 */         return (getParameter(arg0 - 1)).typeName; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
/*      */     
/*      */     }
/*      */     public int getPrecision(int arg0) throws SQLException {
/*      */       
/*  350 */       try { checkBounds(arg0);
/*      */         
/*  352 */         return (getParameter(arg0 - 1)).precision; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
/*      */     
/*      */     }
/*      */     public int getScale(int arg0) throws SQLException {
/*      */       
/*  357 */       try { checkBounds(arg0);
/*      */         
/*  359 */         return (getParameter(arg0 - 1)).scale; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
/*      */     
/*      */     }
/*      */     public int isNullable(int arg0) throws SQLException {
/*      */       
/*  364 */       try { checkBounds(arg0);
/*      */         
/*  366 */         return (getParameter(arg0 - 1)).nullability; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
/*      */     
/*      */     }
/*      */     public boolean isSigned(int arg0) throws SQLException {
/*      */       
/*  371 */       try { checkBounds(arg0);
/*      */         
/*  373 */         return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
/*      */     
/*      */     }
/*      */     Iterator<CallableStatement.CallableStatementParam> iterator() {
/*  377 */       return this.parameterList.iterator();
/*      */     }
/*      */     
/*      */     int numberOfParameters() {
/*  381 */       return this.numParameters;
/*      */     }
/*      */     
/*      */     public boolean isWrapperFor(Class<?> iface) throws SQLException {
/*      */       
/*  386 */       try { CallableStatement.this.checkClosed();
/*      */ 
/*      */         
/*  389 */         return iface.isInstance(this); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
/*      */     
/*      */     }
/*      */     
/*      */     public <T> T unwrap(Class<T> iface) throws SQLException {
/*      */       try {
/*      */         try {
/*  396 */           return iface.cast(this);
/*  397 */         } catch (ClassCastException cce) {
/*  398 */           throw SQLError.createSQLException(Messages.getString("Common.UnableToUnwrap", new Object[] { iface.toString() }), "S1009", CallableStatement.this
/*  399 */               .getExceptionInterceptor());
/*      */         } 
/*      */       } catch (CJException cJException) {
/*      */         throw SQLExceptionsMapping.translateException(cJException);
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private static String mangleParameterName(String origParameterName) {
/*  409 */     if (origParameterName == null) {
/*  410 */       return null;
/*      */     }
/*      */     
/*  413 */     int offset = 0;
/*      */     
/*  415 */     if (origParameterName.length() > 0 && origParameterName.charAt(0) == '@') {
/*  416 */       offset = 1;
/*      */     }
/*      */     
/*  419 */     StringBuilder paramNameBuf = new StringBuilder("@com_mysql_jdbc_outparam_".length() + origParameterName.length());
/*  420 */     paramNameBuf.append("@com_mysql_jdbc_outparam_");
/*  421 */     paramNameBuf.append(origParameterName.substring(offset));
/*      */     
/*  423 */     return paramNameBuf.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean callingStoredFunction = false;
/*      */ 
/*      */   
/*      */   private ResultSetInternalMethods functionReturnValueResults;
/*      */ 
/*      */   
/*      */   private boolean hasOutputParams = false;
/*      */ 
/*      */   
/*      */   private ResultSetInternalMethods outputParameterResults;
/*      */ 
/*      */   
/*      */   protected boolean outputParamWasNull = false;
/*      */ 
/*      */   
/*      */   private int[] parameterIndexToRsIndex;
/*      */ 
/*      */   
/*      */   protected CallableStatementParamInfo paramInfo;
/*      */   
/*      */   private CallableStatementParam returnValueParam;
/*      */   
/*      */   private boolean noAccessToProcedureBodies;
/*      */   
/*      */   private int[] placeholderToParameterIndexMap;
/*      */ 
/*      */   
/*      */   public CallableStatement(JdbcConnection conn, CallableStatementParamInfo paramInfo) throws SQLException {
/*  456 */     super(conn, paramInfo.nativeSql, paramInfo.dbInUse);
/*      */     
/*  458 */     this.paramInfo = paramInfo;
/*  459 */     this.callingStoredFunction = this.paramInfo.isFunctionCall;
/*      */     
/*  461 */     if (this.callingStoredFunction) {
/*  462 */       ((PreparedQuery)this.query).setParameterCount(((PreparedQuery)this.query).getParameterCount() + 1);
/*      */     }
/*      */     
/*  465 */     this.retrieveGeneratedKeys = true;
/*      */     
/*  467 */     this.noAccessToProcedureBodies = ((Boolean)conn.getPropertySet().getBooleanProperty(PropertyKey.noAccessToProcedureBodies).getValue()).booleanValue();
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
/*      */   protected static CallableStatement getInstance(JdbcConnection conn, String sql, String db, boolean isFunctionCall) throws SQLException {
/*  487 */     return new CallableStatement(conn, sql, db, isFunctionCall);
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
/*      */   protected static CallableStatement getInstance(JdbcConnection conn, CallableStatementParamInfo paramInfo) throws SQLException {
/*  503 */     return new CallableStatement(conn, paramInfo);
/*      */   }
/*      */ 
/*      */   
/*      */   private void generateParameterMap() throws SQLException {
/*      */     try {
/*  509 */       synchronized (checkClosed().getConnectionMutex()) {
/*  510 */         if (this.paramInfo == null) {
/*      */           return;
/*      */         }
/*      */ 
/*      */ 
/*      */         
/*  516 */         int parameterCountFromMetaData = this.paramInfo.getParameterCount();
/*      */ 
/*      */ 
/*      */         
/*  520 */         if (this.callingStoredFunction) {
/*  521 */           parameterCountFromMetaData--;
/*      */         }
/*      */         
/*  524 */         PreparedQuery<?> q = (PreparedQuery)this.query;
/*  525 */         if (this.paramInfo != null && q.getParameterCount() != parameterCountFromMetaData) {
/*  526 */           this.placeholderToParameterIndexMap = new int[q.getParameterCount()];
/*      */ 
/*      */           
/*  529 */           int startPos = this.callingStoredFunction ? StringUtils.indexOfIgnoreCase(q.getOriginalSql(), "SELECT") : StringUtils.indexOfIgnoreCase(q.getOriginalSql(), "CALL");
/*      */           
/*  531 */           if (startPos != -1) {
/*  532 */             int parenOpenPos = q.getOriginalSql().indexOf('(', startPos + 4);
/*      */             
/*  534 */             if (parenOpenPos != -1) {
/*  535 */               int parenClosePos = StringUtils.indexOfIgnoreCase(parenOpenPos, q.getOriginalSql(), ")", "'", "'", SearchMode.__FULL);
/*      */               
/*  537 */               if (parenClosePos != -1) {
/*  538 */                 List<?> parsedParameters = StringUtils.split(q.getOriginalSql().substring(parenOpenPos + 1, parenClosePos), ",", "'\"", "'\"", true);
/*      */ 
/*      */                 
/*  541 */                 int numParsedParameters = parsedParameters.size();
/*      */ 
/*      */ 
/*      */                 
/*  545 */                 if (numParsedParameters != q.getParameterCount());
/*      */ 
/*      */ 
/*      */                 
/*  549 */                 int placeholderCount = 0;
/*      */                 
/*  551 */                 for (int i = 0; i < numParsedParameters; i++) {
/*  552 */                   if (((String)parsedParameters.get(i)).equals("?"))
/*  553 */                     this.placeholderToParameterIndexMap[placeholderCount++] = i; 
/*      */                 } 
/*      */               } 
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       }  return;
/*      */     } catch (CJException cJException) {
/*  561 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
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
/*      */   public CallableStatement(JdbcConnection conn, String sql, String db, boolean isFunctionCall) throws SQLException {
/*  579 */     super(conn, sql, db);
/*      */     
/*  581 */     this.callingStoredFunction = isFunctionCall;
/*      */     
/*  583 */     if (!this.callingStoredFunction) {
/*  584 */       if (!StringUtils.startsWithIgnoreCaseAndWs(sql, "CALL")) {
/*      */         
/*  586 */         fakeParameterTypes(false);
/*      */       } else {
/*  588 */         determineParameterTypes();
/*      */       } 
/*      */       
/*  591 */       generateParameterMap();
/*      */     } else {
/*  593 */       determineParameterTypes();
/*  594 */       generateParameterMap();
/*      */       
/*  596 */       ((PreparedQuery)this.query).setParameterCount(((PreparedQuery)this.query).getParameterCount() + 1);
/*      */     } 
/*      */     
/*  599 */     this.retrieveGeneratedKeys = true;
/*  600 */     this.noAccessToProcedureBodies = ((Boolean)conn.getPropertySet().getBooleanProperty(PropertyKey.noAccessToProcedureBodies).getValue()).booleanValue();
/*      */   }
/*      */   
/*      */   public void addBatch() throws SQLException {
/*      */     
/*  605 */     try { setOutParams();
/*      */       
/*  607 */       super.addBatch(); return; }
/*  608 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } private CallableStatementParam checkIsOutputParam(int paramIndex) throws SQLException {
/*      */     
/*  612 */     try { synchronized (checkClosed().getConnectionMutex())
/*  613 */       { if (this.callingStoredFunction) {
/*  614 */           if (paramIndex == 1) {
/*      */             
/*  616 */             if (this.returnValueParam == null) {
/*  617 */               this.returnValueParam = new CallableStatementParam("", 0, false, true, MysqlType.VARCHAR.getJdbcType(), "VARCHAR", 0, 0, (short)2, 5);
/*      */             }
/*      */ 
/*      */             
/*  621 */             return this.returnValueParam;
/*      */           } 
/*      */ 
/*      */           
/*  625 */           paramIndex--;
/*      */         } 
/*      */         
/*  628 */         checkParameterIndexBounds(paramIndex);
/*      */         
/*  630 */         int localParamIndex = paramIndex - 1;
/*      */         
/*  632 */         if (this.placeholderToParameterIndexMap != null) {
/*  633 */           localParamIndex = this.placeholderToParameterIndexMap[localParamIndex];
/*      */         }
/*      */         
/*  636 */         CallableStatementParam paramDescriptor = this.paramInfo.getParameter(localParamIndex);
/*      */ 
/*      */ 
/*      */         
/*  640 */         if (this.noAccessToProcedureBodies) {
/*  641 */           paramDescriptor.isOut = true;
/*  642 */           paramDescriptor.isIn = true;
/*  643 */           paramDescriptor.inOutModifier = 2;
/*  644 */         } else if (!paramDescriptor.isOut) {
/*  645 */           throw SQLError.createSQLException(Messages.getString("CallableStatement.9", new Object[] { Integer.valueOf(paramIndex) }), "S1009", 
/*  646 */               getExceptionInterceptor());
/*      */         } 
/*      */         
/*  649 */         this.hasOutputParams = true;
/*      */         
/*  651 */         return paramDescriptor; }  }
/*  652 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void checkParameterIndexBounds(int paramIndex) throws SQLException {
/*      */     
/*  663 */     try { synchronized (checkClosed().getConnectionMutex()) {
/*  664 */         this.paramInfo.checkBounds(paramIndex);
/*      */       }  return; }
/*  666 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void checkStreamability() throws SQLException {
/*  677 */     if (this.hasOutputParams && createStreamingResultSet()) {
/*  678 */       throw SQLError.createSQLException(Messages.getString("CallableStatement.14"), "S1C00", 
/*  679 */           getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */   
/*      */   public void clearParameters() throws SQLException {
/*      */     
/*  685 */     try { synchronized (checkClosed().getConnectionMutex()) {
/*  686 */         super.clearParameters();
/*      */         
/*      */         try {
/*  689 */           if (this.outputParameterResults != null) {
/*  690 */             this.outputParameterResults.close();
/*      */           }
/*      */         } finally {
/*  693 */           this.outputParameterResults = null;
/*      */         } 
/*      */       }  return; }
/*  696 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
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
/*      */   private void fakeParameterTypes(boolean isReallyProcedure) throws SQLException {
/*      */     
/*  709 */     try { synchronized (checkClosed().getConnectionMutex()) {
/*  710 */         String encoding = this.connection.getSession().getServerSession().getCharsetSettings().getMetadataEncoding();
/*  711 */         int collationIndex = this.connection.getSession().getServerSession().getCharsetSettings().getMetadataCollationIndex();
/*  712 */         Field[] fields = new Field[13];
/*      */         
/*  714 */         fields[0] = new Field("", "PROCEDURE_CAT", collationIndex, encoding, MysqlType.CHAR, 0);
/*  715 */         fields[1] = new Field("", "PROCEDURE_SCHEM", collationIndex, encoding, MysqlType.CHAR, 0);
/*  716 */         fields[2] = new Field("", "PROCEDURE_NAME", collationIndex, encoding, MysqlType.CHAR, 0);
/*  717 */         fields[3] = new Field("", "COLUMN_NAME", collationIndex, encoding, MysqlType.CHAR, 0);
/*  718 */         fields[4] = new Field("", "COLUMN_TYPE", collationIndex, encoding, MysqlType.CHAR, 0);
/*  719 */         fields[5] = new Field("", "DATA_TYPE", collationIndex, encoding, MysqlType.SMALLINT, 0);
/*  720 */         fields[6] = new Field("", "TYPE_NAME", collationIndex, encoding, MysqlType.CHAR, 0);
/*  721 */         fields[7] = new Field("", "PRECISION", collationIndex, encoding, MysqlType.INT, 0);
/*  722 */         fields[8] = new Field("", "LENGTH", collationIndex, encoding, MysqlType.INT, 0);
/*  723 */         fields[9] = new Field("", "SCALE", collationIndex, encoding, MysqlType.SMALLINT, 0);
/*  724 */         fields[10] = new Field("", "RADIX", collationIndex, encoding, MysqlType.SMALLINT, 0);
/*  725 */         fields[11] = new Field("", "NULLABLE", collationIndex, encoding, MysqlType.SMALLINT, 0);
/*  726 */         fields[12] = new Field("", "REMARKS", collationIndex, encoding, MysqlType.CHAR, 0);
/*      */         
/*  728 */         String procName = isReallyProcedure ? extractProcedureName() : null;
/*      */         
/*  730 */         byte[] procNameAsBytes = null;
/*      */         
/*  732 */         procNameAsBytes = (procName == null) ? null : StringUtils.getBytes(procName, "UTF-8");
/*      */         
/*  734 */         ArrayList<Row> resultRows = new ArrayList<>();
/*      */         
/*  736 */         for (int i = 0; i < ((PreparedQuery)this.query).getParameterCount(); i++) {
/*  737 */           byte[][] row = new byte[13][];
/*  738 */           row[0] = null;
/*  739 */           row[1] = null;
/*  740 */           row[2] = procNameAsBytes;
/*  741 */           row[3] = s2b(String.valueOf(i));
/*      */           
/*  743 */           row[4] = s2b(String.valueOf(1));
/*      */           
/*  745 */           row[5] = s2b(String.valueOf(MysqlType.VARCHAR.getJdbcType()));
/*  746 */           row[6] = s2b(MysqlType.VARCHAR.getName());
/*  747 */           row[7] = s2b(Integer.toString(65535));
/*  748 */           row[8] = s2b(Integer.toString(65535));
/*  749 */           row[9] = s2b(Integer.toString(0));
/*  750 */           row[10] = s2b(Integer.toString(10));
/*      */           
/*  752 */           row[11] = s2b(Integer.toString(2));
/*      */           
/*  754 */           row[12] = null;
/*      */           
/*  756 */           resultRows.add(new ByteArrayRow(row, getExceptionInterceptor()));
/*      */         } 
/*      */         
/*  759 */         ResultSetImpl resultSetImpl = this.resultSetFactory.createFromResultsetRows(1007, 1004, (ResultsetRows)new ResultsetRowsStatic(resultRows, (ColumnDefinition)new DefaultColumnDefinition(fields)));
/*      */ 
/*      */         
/*  762 */         convertGetProcedureColumnsToInternalDescriptors((ResultSet)resultSetImpl);
/*      */       }  return; }
/*  764 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } private void determineParameterTypes() throws SQLException { try {
/*  767 */       synchronized (checkClosed().getConnectionMutex()) {
/*  768 */         ResultSet paramTypesRs = null;
/*      */ 
/*      */         
/*      */         try {
/*  772 */           String procName = extractProcedureName();
/*  773 */           String quotedId = this.session.getIdentifierQuoteString();
/*      */           
/*  775 */           List<?> parseList = StringUtils.splitDBdotName(procName, "", quotedId, this.session.getServerSession().isNoBackslashEscapesSet());
/*  776 */           String tmpDb = "";
/*      */           
/*  778 */           if (parseList.size() == 2) {
/*  779 */             tmpDb = (String)parseList.get(0);
/*  780 */             procName = (String)parseList.get(1);
/*      */           } 
/*      */ 
/*      */ 
/*      */           
/*  785 */           DatabaseMetaData dbmd = this.connection.getMetaData();
/*      */           
/*  787 */           boolean useDb = false;
/*      */           
/*  789 */           if (tmpDb.length() <= 0) {
/*  790 */             useDb = true;
/*      */           }
/*      */ 
/*      */ 
/*      */           
/*  795 */           paramTypesRs = (this.session.getPropertySet().getEnumProperty(PropertyKey.databaseTerm).getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? dbmd.getProcedureColumns(null, useDb ? getCurrentDatabase() : tmpDb, procName, "%") : dbmd.getProcedureColumns(useDb ? getCurrentDatabase() : tmpDb, null, procName, "%");
/*      */           
/*  797 */           boolean hasResults = false;
/*      */           try {
/*  799 */             if (paramTypesRs.next()) {
/*  800 */               paramTypesRs.previous();
/*  801 */               hasResults = true;
/*      */             } 
/*  803 */           } catch (Exception exception) {}
/*      */ 
/*      */           
/*  806 */           if (hasResults) {
/*  807 */             convertGetProcedureColumnsToInternalDescriptors(paramTypesRs);
/*      */           } else {
/*  809 */             fakeParameterTypes(true);
/*      */           } 
/*      */         } finally {
/*  812 */           SQLException sqlExRethrow = null;
/*      */           
/*  814 */           if (paramTypesRs != null) {
/*      */             try {
/*  816 */               paramTypesRs.close();
/*  817 */             } catch (SQLException sqlEx) {
/*  818 */               sqlExRethrow = sqlEx;
/*      */             } 
/*      */             
/*  821 */             paramTypesRs = null;
/*      */           } 
/*      */           
/*  824 */           if (sqlExRethrow != null)
/*  825 */             throw sqlExRethrow; 
/*      */         } 
/*      */       }  return;
/*      */     } catch (CJException cJException) {
/*  829 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     }  } private void convertGetProcedureColumnsToInternalDescriptors(ResultSet paramTypesRs) throws SQLException {
/*      */     
/*  832 */     try { synchronized (checkClosed().getConnectionMutex()) {
/*  833 */         this.paramInfo = new CallableStatementParamInfo(paramTypesRs);
/*      */       }  return; }
/*  835 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public boolean execute() throws SQLException {
/*      */     
/*  839 */     try { synchronized (checkClosed().getConnectionMutex())
/*  840 */       { boolean returnVal = false;
/*      */         
/*  842 */         checkStreamability();
/*      */         
/*  844 */         setInOutParamsOnServer();
/*  845 */         setOutParams();
/*      */         
/*  847 */         returnVal = super.execute();
/*      */         
/*  849 */         if (this.callingStoredFunction) {
/*  850 */           this.functionReturnValueResults = this.results;
/*  851 */           this.functionReturnValueResults.next();
/*  852 */           this.results = null;
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  869 */         retrieveOutParams();
/*      */         
/*  871 */         if (!this.callingStoredFunction) {
/*  872 */           return returnVal;
/*      */         }
/*      */ 
/*      */         
/*  876 */         return false; }  }
/*  877 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public ResultSet executeQuery() throws SQLException {
/*      */     
/*  882 */     try { synchronized (checkClosed().getConnectionMutex())
/*      */       
/*  884 */       { checkStreamability();
/*      */         
/*  886 */         ResultSet execResults = null;
/*      */         
/*  888 */         setInOutParamsOnServer();
/*  889 */         setOutParams();
/*      */         
/*  891 */         execResults = super.executeQuery();
/*      */         
/*  893 */         retrieveOutParams();
/*      */         
/*  895 */         return execResults; }  }
/*  896 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public int executeUpdate() throws SQLException {
/*      */     
/*  901 */     try { return Util.truncateAndConvertToInt(executeLargeUpdate()); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   private String extractProcedureName() throws SQLException {
/*  905 */     String sanitizedSql = StringUtils.stripCommentsAndHints(((PreparedQuery)this.query).getOriginalSql(), "`'\"", "`'\"", 
/*  906 */         !this.session.getServerSession().isNoBackslashEscapesSet());
/*      */ 
/*      */     
/*  909 */     int endCallIndex = StringUtils.indexOfIgnoreCase(sanitizedSql, "CALL ");
/*  910 */     int offset = 5;
/*      */     
/*  912 */     if (endCallIndex == -1) {
/*  913 */       endCallIndex = StringUtils.indexOfIgnoreCase(sanitizedSql, "SELECT ");
/*  914 */       offset = 7;
/*      */     } 
/*      */     
/*  917 */     if (endCallIndex != -1) {
/*  918 */       StringBuilder nameBuf = new StringBuilder();
/*      */       
/*  920 */       String trimmedStatement = sanitizedSql.substring(endCallIndex + offset).trim();
/*      */       
/*  922 */       int statementLength = trimmedStatement.length();
/*      */       
/*  924 */       for (int i = 0; i < statementLength; i++) {
/*  925 */         char c = trimmedStatement.charAt(i);
/*      */         
/*  927 */         if (Character.isWhitespace(c) || c == '(' || c == '?') {
/*      */           break;
/*      */         }
/*  930 */         nameBuf.append(c);
/*      */       } 
/*      */ 
/*      */       
/*  934 */       return nameBuf.toString();
/*      */     } 
/*      */     
/*  937 */     throw SQLError.createSQLException(Messages.getString("CallableStatement.1"), "S1000", getExceptionInterceptor());
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
/*      */   protected String fixParameterName(String paramNameIn) throws SQLException {
/*      */     
/*  952 */     try { synchronized (checkClosed().getConnectionMutex())
/*  953 */       { if (paramNameIn == null) {
/*  954 */           paramNameIn = "nullpn";
/*      */         }
/*      */         
/*  957 */         if (this.noAccessToProcedureBodies) {
/*  958 */           throw SQLError.createSQLException(Messages.getString("CallableStatement.23"), "S1009", 
/*  959 */               getExceptionInterceptor());
/*      */         }
/*      */         
/*  962 */         return mangleParameterName(paramNameIn); }  }
/*  963 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Array getArray(int i) throws SQLException {
/*      */     
/*  968 */     try { synchronized (checkClosed().getConnectionMutex())
/*  969 */       { ResultSetInternalMethods rs = getOutputParameters(i);
/*      */         
/*  971 */         Array retValue = rs.getArray(mapOutputParameterIndexToRsIndex(i));
/*      */         
/*  973 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/*  975 */         return retValue; }  }
/*  976 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Array getArray(String parameterName) throws SQLException {
/*      */     
/*  981 */     try { synchronized (checkClosed().getConnectionMutex())
/*  982 */       { ResultSetInternalMethods rs = getOutputParameters(0);
/*      */         
/*  984 */         Array retValue = rs.getArray(fixParameterName(parameterName));
/*      */         
/*  986 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/*  988 */         return retValue; }  }
/*  989 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
/*      */     
/*  994 */     try { synchronized (checkClosed().getConnectionMutex())
/*  995 */       { ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */         
/*  997 */         BigDecimal retValue = rs.getBigDecimal(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */         
/*  999 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1001 */         return retValue; }  }
/* 1002 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   @Deprecated
/*      */   public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
/*      */     
/* 1008 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1009 */       { ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */         
/* 1011 */         BigDecimal retValue = rs.getBigDecimal(mapOutputParameterIndexToRsIndex(parameterIndex), scale);
/*      */         
/* 1013 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1015 */         return retValue; }  }
/* 1016 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public BigDecimal getBigDecimal(String parameterName) throws SQLException {
/*      */     
/* 1021 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1022 */       { ResultSetInternalMethods rs = getOutputParameters(0);
/*      */         
/* 1024 */         BigDecimal retValue = rs.getBigDecimal(fixParameterName(parameterName));
/*      */         
/* 1026 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1028 */         return retValue; }  }
/* 1029 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Blob getBlob(int parameterIndex) throws SQLException {
/*      */     
/* 1034 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1035 */       { ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */         
/* 1037 */         Blob retValue = rs.getBlob(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */         
/* 1039 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1041 */         return retValue; }  }
/* 1042 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Blob getBlob(String parameterName) throws SQLException {
/*      */     
/* 1047 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1048 */       { ResultSetInternalMethods rs = getOutputParameters(0);
/*      */         
/* 1050 */         Blob retValue = rs.getBlob(fixParameterName(parameterName));
/*      */         
/* 1052 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1054 */         return retValue; }  }
/* 1055 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean getBoolean(int parameterIndex) throws SQLException {
/*      */     
/* 1060 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1061 */       { ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */         
/* 1063 */         boolean retValue = rs.getBoolean(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */         
/* 1065 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1067 */         return retValue; }  }
/* 1068 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public boolean getBoolean(String parameterName) throws SQLException {
/*      */     
/* 1073 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1074 */       { ResultSetInternalMethods rs = getOutputParameters(0);
/*      */         
/* 1076 */         boolean retValue = rs.getBoolean(fixParameterName(parameterName));
/*      */         
/* 1078 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1080 */         return retValue; }  }
/* 1081 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public byte getByte(int parameterIndex) throws SQLException {
/*      */     
/* 1086 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1087 */       { ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */         
/* 1089 */         byte retValue = rs.getByte(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */         
/* 1091 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1093 */         return retValue; }  }
/* 1094 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public byte getByte(String parameterName) throws SQLException {
/*      */     
/* 1099 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1100 */       { ResultSetInternalMethods rs = getOutputParameters(0);
/*      */         
/* 1102 */         byte retValue = rs.getByte(fixParameterName(parameterName));
/*      */         
/* 1104 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1106 */         return retValue; }  }
/* 1107 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public byte[] getBytes(int parameterIndex) throws SQLException {
/*      */     
/* 1112 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1113 */       { ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */         
/* 1115 */         byte[] retValue = rs.getBytes(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */         
/* 1117 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1119 */         return retValue; }  }
/* 1120 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public byte[] getBytes(String parameterName) throws SQLException {
/*      */     
/* 1125 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1126 */       { ResultSetInternalMethods rs = getOutputParameters(0);
/*      */         
/* 1128 */         byte[] retValue = rs.getBytes(fixParameterName(parameterName));
/*      */         
/* 1130 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1132 */         return retValue; }  }
/* 1133 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Clob getClob(int parameterIndex) throws SQLException {
/*      */     
/* 1138 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1139 */       { ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */         
/* 1141 */         Clob retValue = rs.getClob(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */         
/* 1143 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1145 */         return retValue; }  }
/* 1146 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Clob getClob(String parameterName) throws SQLException {
/*      */     
/* 1151 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1152 */       { ResultSetInternalMethods rs = getOutputParameters(0);
/*      */         
/* 1154 */         Clob retValue = rs.getClob(fixParameterName(parameterName));
/*      */         
/* 1156 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1158 */         return retValue; }  }
/* 1159 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Date getDate(int parameterIndex) throws SQLException {
/*      */     
/* 1164 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1165 */       { ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */         
/* 1167 */         Date retValue = rs.getDate(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */         
/* 1169 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1171 */         return retValue; }  }
/* 1172 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
/*      */     
/* 1177 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1178 */       { ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */         
/* 1180 */         Date retValue = rs.getDate(mapOutputParameterIndexToRsIndex(parameterIndex), cal);
/*      */         
/* 1182 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1184 */         return retValue; }  }
/* 1185 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Date getDate(String parameterName) throws SQLException {
/*      */     
/* 1190 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1191 */       { ResultSetInternalMethods rs = getOutputParameters(0);
/*      */         
/* 1193 */         Date retValue = rs.getDate(fixParameterName(parameterName));
/*      */         
/* 1195 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1197 */         return retValue; }  }
/* 1198 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Date getDate(String parameterName, Calendar cal) throws SQLException {
/*      */     
/* 1203 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1204 */       { ResultSetInternalMethods rs = getOutputParameters(0);
/*      */         
/* 1206 */         Date retValue = rs.getDate(fixParameterName(parameterName), cal);
/*      */         
/* 1208 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1210 */         return retValue; }  }
/* 1211 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public double getDouble(int parameterIndex) throws SQLException {
/*      */     
/* 1216 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1217 */       { ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */         
/* 1219 */         double retValue = rs.getDouble(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */         
/* 1221 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1223 */         return retValue; }  }
/* 1224 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public double getDouble(String parameterName) throws SQLException {
/*      */     
/* 1229 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1230 */       { ResultSetInternalMethods rs = getOutputParameters(0);
/*      */         
/* 1232 */         double retValue = rs.getDouble(fixParameterName(parameterName));
/*      */         
/* 1234 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1236 */         return retValue; }  }
/* 1237 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public float getFloat(int parameterIndex) throws SQLException {
/*      */     
/* 1242 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1243 */       { ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */         
/* 1245 */         float retValue = rs.getFloat(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */         
/* 1247 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1249 */         return retValue; }  }
/* 1250 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public float getFloat(String parameterName) throws SQLException {
/*      */     
/* 1255 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1256 */       { ResultSetInternalMethods rs = getOutputParameters(0);
/*      */         
/* 1258 */         float retValue = rs.getFloat(fixParameterName(parameterName));
/*      */         
/* 1260 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1262 */         return retValue; }  }
/* 1263 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public int getInt(int parameterIndex) throws SQLException {
/*      */     
/* 1268 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1269 */       { ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */         
/* 1271 */         int retValue = rs.getInt(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */         
/* 1273 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1275 */         return retValue; }  }
/* 1276 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public int getInt(String parameterName) throws SQLException {
/*      */     
/* 1281 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1282 */       { ResultSetInternalMethods rs = getOutputParameters(0);
/*      */         
/* 1284 */         int retValue = rs.getInt(fixParameterName(parameterName));
/*      */         
/* 1286 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1288 */         return retValue; }  }
/* 1289 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public long getLong(int parameterIndex) throws SQLException {
/*      */     
/* 1294 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1295 */       { ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */         
/* 1297 */         long retValue = rs.getLong(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */         
/* 1299 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1301 */         return retValue; }  }
/* 1302 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public long getLong(String parameterName) throws SQLException {
/*      */     
/* 1307 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1308 */       { ResultSetInternalMethods rs = getOutputParameters(0);
/*      */         
/* 1310 */         long retValue = rs.getLong(fixParameterName(parameterName));
/*      */         
/* 1312 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1314 */         return retValue; }  }
/* 1315 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } protected int getNamedParamIndex(String paramName, boolean forOut) throws SQLException {
/*      */     
/* 1319 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1320 */       { if (this.noAccessToProcedureBodies) {
/* 1321 */           throw SQLError.createSQLException("No access to parameters by name when connection has been configured not to access procedure bodies", "S1009", 
/* 1322 */               getExceptionInterceptor());
/*      */         }
/*      */         
/* 1325 */         if (paramName == null || paramName.length() == 0) {
/* 1326 */           throw SQLError.createSQLException(Messages.getString("CallableStatement.2"), "S1009", 
/* 1327 */               getExceptionInterceptor());
/*      */         }
/*      */         
/*      */         CallableStatementParam namedParamInfo;
/* 1331 */         if (this.paramInfo == null || (namedParamInfo = this.paramInfo.getParameter(paramName)) == null) {
/* 1332 */           throw SQLError.createSQLException(Messages.getString("CallableStatement.3", new Object[] { paramName }), "S1009", 
/* 1333 */               getExceptionInterceptor());
/*      */         }
/*      */         
/* 1336 */         if (forOut && !namedParamInfo.isOut) {
/* 1337 */           throw SQLError.createSQLException(Messages.getString("CallableStatement.5", new Object[] { paramName }), "S1009", 
/* 1338 */               getExceptionInterceptor());
/*      */         }
/*      */         
/* 1341 */         if (this.placeholderToParameterIndexMap == null) {
/* 1342 */           return namedParamInfo.index + 1;
/*      */         }
/*      */         
/* 1345 */         for (int i = 0; i < this.placeholderToParameterIndexMap.length; i++) {
/* 1346 */           if (this.placeholderToParameterIndexMap[i] == namedParamInfo.index) {
/* 1347 */             return i + 1;
/*      */           }
/*      */         } 
/*      */         
/* 1351 */         throw SQLError.createSQLException(Messages.getString("CallableStatement.6", new Object[] { paramName }), "S1009", 
/* 1352 */             getExceptionInterceptor()); }  }
/* 1353 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Object getObject(int parameterIndex) throws SQLException {
/*      */     
/* 1358 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1359 */       { CallableStatementParam paramDescriptor = checkIsOutputParam(parameterIndex);
/*      */         
/* 1361 */         ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */         
/* 1363 */         Object retVal = rs.getObjectStoredProc(mapOutputParameterIndexToRsIndex(parameterIndex), paramDescriptor.desiredMysqlType.getJdbcType());
/*      */         
/* 1365 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1367 */         return retVal; }  }
/* 1368 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Object getObject(int parameterIndex, Map<String, Class<?>> map) throws SQLException {
/*      */     
/* 1373 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1374 */       { ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */         
/* 1376 */         Object retVal = rs.getObject(mapOutputParameterIndexToRsIndex(parameterIndex), map);
/*      */         
/* 1378 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1380 */         return retVal; }  }
/* 1381 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Object getObject(String parameterName) throws SQLException {
/*      */     
/* 1386 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1387 */       { ResultSetInternalMethods rs = getOutputParameters(0);
/*      */         
/* 1389 */         Object retValue = rs.getObject(fixParameterName(parameterName));
/*      */         
/* 1391 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1393 */         return retValue; }  }
/* 1394 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException {
/*      */     
/* 1399 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1400 */       { ResultSetInternalMethods rs = getOutputParameters(0);
/*      */         
/* 1402 */         Object retValue = rs.getObject(fixParameterName(parameterName), map);
/*      */         
/* 1404 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1406 */         return retValue; }  }
/* 1407 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
/*      */     
/* 1412 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1413 */       { ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */ 
/*      */         
/* 1416 */         T retVal = (T)((ResultSetImpl)rs).getObject(mapOutputParameterIndexToRsIndex(parameterIndex), type);
/*      */         
/* 1418 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1420 */         return retVal; }  }
/* 1421 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
/*      */     
/* 1426 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1427 */       { ResultSetInternalMethods rs = getOutputParameters(0);
/*      */         
/* 1429 */         T retValue = (T)((ResultSetImpl)rs).getObject(fixParameterName(parameterName), type);
/*      */         
/* 1431 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1433 */         return retValue; }  }
/* 1434 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
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
/*      */   protected ResultSetInternalMethods getOutputParameters(int paramIndex) throws SQLException {
/*      */     
/* 1451 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1452 */       { this.outputParamWasNull = false;
/*      */         
/* 1454 */         if (paramIndex == 1 && this.callingStoredFunction && this.returnValueParam != null) {
/* 1455 */           return this.functionReturnValueResults;
/*      */         }
/*      */         
/* 1458 */         if (this.outputParameterResults == null) {
/* 1459 */           if (this.paramInfo.numberOfParameters() == 0) {
/* 1460 */             throw SQLError.createSQLException(Messages.getString("CallableStatement.7"), "S1009", 
/* 1461 */                 getExceptionInterceptor());
/*      */           }
/* 1463 */           throw SQLError.createSQLException(Messages.getString("CallableStatement.8"), "S1000", 
/* 1464 */               getExceptionInterceptor());
/*      */         } 
/*      */         
/* 1467 */         return this.outputParameterResults; }  }
/* 1468 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public ParameterMetaData getParameterMetaData() throws SQLException {
/*      */     
/* 1473 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1474 */       { if (this.placeholderToParameterIndexMap == null) {
/* 1475 */           return this.paramInfo;
/*      */         }
/*      */         
/* 1478 */         return new CallableStatementParamInfo(this.paramInfo); }  }
/* 1479 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Ref getRef(int parameterIndex) throws SQLException {
/*      */     
/* 1484 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1485 */       { ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */         
/* 1487 */         Ref retValue = rs.getRef(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */         
/* 1489 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1491 */         return retValue; }  }
/* 1492 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Ref getRef(String parameterName) throws SQLException {
/*      */     
/* 1497 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1498 */       { ResultSetInternalMethods rs = getOutputParameters(0);
/*      */         
/* 1500 */         Ref retValue = rs.getRef(fixParameterName(parameterName));
/*      */         
/* 1502 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1504 */         return retValue; }  }
/* 1505 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public short getShort(int parameterIndex) throws SQLException {
/*      */     
/* 1510 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1511 */       { ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */         
/* 1513 */         short retValue = rs.getShort(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */         
/* 1515 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1517 */         return retValue; }  }
/* 1518 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public short getShort(String parameterName) throws SQLException {
/*      */     
/* 1523 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1524 */       { ResultSetInternalMethods rs = getOutputParameters(0);
/*      */         
/* 1526 */         short retValue = rs.getShort(fixParameterName(parameterName));
/*      */         
/* 1528 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1530 */         return retValue; }  }
/* 1531 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public String getString(int parameterIndex) throws SQLException {
/*      */     
/* 1536 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1537 */       { ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */         
/* 1539 */         String retValue = rs.getString(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */         
/* 1541 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1543 */         return retValue; }  }
/* 1544 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public String getString(String parameterName) throws SQLException {
/*      */     
/* 1549 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1550 */       { ResultSetInternalMethods rs = getOutputParameters(0);
/*      */         
/* 1552 */         String retValue = rs.getString(fixParameterName(parameterName));
/*      */         
/* 1554 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1556 */         return retValue; }  }
/* 1557 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Time getTime(int parameterIndex) throws SQLException {
/*      */     
/* 1562 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1563 */       { ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */         
/* 1565 */         Time retValue = rs.getTime(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */         
/* 1567 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1569 */         return retValue; }  }
/* 1570 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
/*      */     
/* 1575 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1576 */       { ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */         
/* 1578 */         Time retValue = rs.getTime(mapOutputParameterIndexToRsIndex(parameterIndex), cal);
/*      */         
/* 1580 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1582 */         return retValue; }  }
/* 1583 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Time getTime(String parameterName) throws SQLException {
/*      */     
/* 1588 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1589 */       { ResultSetInternalMethods rs = getOutputParameters(0);
/*      */         
/* 1591 */         Time retValue = rs.getTime(fixParameterName(parameterName));
/*      */         
/* 1593 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1595 */         return retValue; }  }
/* 1596 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Time getTime(String parameterName, Calendar cal) throws SQLException {
/*      */     
/* 1601 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1602 */       { ResultSetInternalMethods rs = getOutputParameters(0);
/*      */         
/* 1604 */         Time retValue = rs.getTime(fixParameterName(parameterName), cal);
/*      */         
/* 1606 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1608 */         return retValue; }  }
/* 1609 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Timestamp getTimestamp(int parameterIndex) throws SQLException {
/*      */     
/* 1614 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1615 */       { ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */         
/* 1617 */         Timestamp retValue = rs.getTimestamp(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */         
/* 1619 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1621 */         return retValue; }  }
/* 1622 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
/*      */     
/* 1627 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1628 */       { ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */         
/* 1630 */         Timestamp retValue = rs.getTimestamp(mapOutputParameterIndexToRsIndex(parameterIndex), cal);
/*      */         
/* 1632 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1634 */         return retValue; }  }
/* 1635 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Timestamp getTimestamp(String parameterName) throws SQLException {
/*      */     
/* 1640 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1641 */       { ResultSetInternalMethods rs = getOutputParameters(0);
/*      */         
/* 1643 */         Timestamp retValue = rs.getTimestamp(fixParameterName(parameterName));
/*      */         
/* 1645 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1647 */         return retValue; }  }
/* 1648 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
/*      */     
/* 1653 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1654 */       { ResultSetInternalMethods rs = getOutputParameters(0);
/*      */         
/* 1656 */         Timestamp retValue = rs.getTimestamp(fixParameterName(parameterName), cal);
/*      */         
/* 1658 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1660 */         return retValue; }  }
/* 1661 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public URL getURL(int parameterIndex) throws SQLException {
/*      */     
/* 1666 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1667 */       { ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */         
/* 1669 */         URL retValue = rs.getURL(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */         
/* 1671 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1673 */         return retValue; }  }
/* 1674 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public URL getURL(String parameterName) throws SQLException {
/*      */     
/* 1679 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1680 */       { ResultSetInternalMethods rs = getOutputParameters(0);
/*      */         
/* 1682 */         URL retValue = rs.getURL(fixParameterName(parameterName));
/*      */         
/* 1684 */         this.outputParamWasNull = rs.wasNull();
/*      */         
/* 1686 */         return retValue; }  }
/* 1687 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   protected int mapOutputParameterIndexToRsIndex(int paramIndex) throws SQLException {
/*      */     
/* 1692 */     try { synchronized (checkClosed().getConnectionMutex())
/* 1693 */       { if (this.returnValueParam != null && paramIndex == 1) {
/* 1694 */           return 1;
/*      */         }
/*      */         
/* 1697 */         checkParameterIndexBounds(paramIndex);
/*      */         
/* 1699 */         int localParamIndex = paramIndex - 1;
/*      */         
/* 1701 */         if (this.placeholderToParameterIndexMap != null) {
/* 1702 */           localParamIndex = this.placeholderToParameterIndexMap[localParamIndex];
/*      */         }
/*      */         
/* 1705 */         int rsIndex = this.parameterIndexToRsIndex[localParamIndex];
/*      */         
/* 1707 */         if (rsIndex == Integer.MIN_VALUE) {
/* 1708 */           throw SQLError.createSQLException(Messages.getString("CallableStatement.21", new Object[] { Integer.valueOf(paramIndex) }), "S1009", 
/* 1709 */               getExceptionInterceptor());
/*      */         }
/*      */         
/* 1712 */         return rsIndex + 1; }  }
/* 1713 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   protected void registerOutParameter(int parameterIndex, MysqlType mysqlType) throws SQLException {
/* 1717 */     CallableStatementParam paramDescriptor = checkIsOutputParam(parameterIndex);
/* 1718 */     paramDescriptor.desiredMysqlType = mysqlType;
/*      */   }
/*      */   
/*      */   public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
/*      */     
/*      */     try { try {
/* 1724 */         MysqlType mt = MysqlType.getByJdbcType(sqlType);
/* 1725 */         registerOutParameter(parameterIndex, mt);
/* 1726 */       } catch (FeatureNotAvailableException nae) {
/* 1727 */         throw SQLError.createSQLFeatureNotSupportedException(Messages.getString("Statement.UnsupportedSQLType") + JDBCType.valueOf(sqlType), "S1C00", 
/* 1728 */             getExceptionInterceptor());
/*      */       }  return; }
/* 1730 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void registerOutParameter(int parameterIndex, SQLType sqlType) throws SQLException {
/*      */     
/* 1734 */     try { if (sqlType instanceof MysqlType) {
/* 1735 */         registerOutParameter(parameterIndex, (MysqlType)sqlType);
/*      */       } else {
/* 1737 */         registerOutParameter(parameterIndex, sqlType.getVendorTypeNumber().intValue());
/*      */       }  return; }
/* 1739 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } protected void registerOutParameter(int parameterIndex, MysqlType mysqlType, int scale) throws SQLException {
/* 1742 */     registerOutParameter(parameterIndex, mysqlType);
/*      */   }
/*      */   
/*      */   public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
/*      */     
/* 1747 */     try { registerOutParameter(parameterIndex, sqlType); return; }
/* 1748 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void registerOutParameter(int parameterIndex, SQLType sqlType, int scale) throws SQLException {
/*      */     
/* 1752 */     try { if (sqlType instanceof MysqlType) {
/* 1753 */         registerOutParameter(parameterIndex, (MysqlType)sqlType, scale);
/*      */       } else {
/* 1755 */         registerOutParameter(parameterIndex, sqlType.getVendorTypeNumber().intValue(), scale);
/*      */       }  return; }
/* 1757 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } protected void registerOutParameter(int parameterIndex, MysqlType mysqlType, String typeName) throws SQLException {
/* 1760 */     registerOutParameter(parameterIndex, mysqlType);
/*      */   }
/*      */   
/*      */   public void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {
/*      */     
/*      */     try { try {
/* 1766 */         MysqlType mt = MysqlType.getByJdbcType(sqlType);
/* 1767 */         registerOutParameter(parameterIndex, mt, typeName);
/* 1768 */       } catch (FeatureNotAvailableException nae) {
/* 1769 */         throw SQLError.createSQLFeatureNotSupportedException(Messages.getString("Statement.UnsupportedSQLType") + JDBCType.valueOf(sqlType), "S1C00", 
/* 1770 */             getExceptionInterceptor());
/*      */       }  return; }
/* 1772 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void registerOutParameter(int parameterIndex, SQLType sqlType, String typeName) throws SQLException {
/*      */     
/* 1776 */     try { if (sqlType instanceof MysqlType) {
/* 1777 */         registerOutParameter(parameterIndex, (MysqlType)sqlType, typeName);
/*      */       } else {
/* 1779 */         registerOutParameter(parameterIndex, sqlType.getVendorTypeNumber().intValue(), typeName);
/*      */       }  return; }
/* 1781 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
/*      */     
/* 1785 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1786 */         registerOutParameter(getNamedParamIndex(parameterName, true), sqlType);
/*      */       }  return; }
/* 1788 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void registerOutParameter(String parameterName, SQLType sqlType) throws SQLException {
/*      */     
/* 1792 */     try { if (sqlType instanceof MysqlType) {
/* 1793 */         registerOutParameter(getNamedParamIndex(parameterName, true), (MysqlType)sqlType);
/*      */       } else {
/* 1795 */         registerOutParameter(getNamedParamIndex(parameterName, true), sqlType.getVendorTypeNumber().intValue());
/*      */       }  return; }
/* 1797 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
/*      */     
/* 1801 */     try { registerOutParameter(getNamedParamIndex(parameterName, true), sqlType, scale); return; }
/* 1802 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void registerOutParameter(String parameterName, SQLType sqlType, int scale) throws SQLException {
/*      */     
/* 1806 */     try { if (sqlType instanceof MysqlType) {
/* 1807 */         registerOutParameter(getNamedParamIndex(parameterName, true), (MysqlType)sqlType, scale);
/*      */       } else {
/* 1809 */         registerOutParameter(getNamedParamIndex(parameterName, true), sqlType.getVendorTypeNumber().intValue(), scale);
/*      */       }  return; }
/* 1811 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
/*      */     
/* 1815 */     try { registerOutParameter(getNamedParamIndex(parameterName, true), sqlType, typeName); return; }
/* 1816 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void registerOutParameter(String parameterName, SQLType sqlType, String typeName) throws SQLException {
/*      */     
/* 1820 */     try { if (sqlType instanceof MysqlType) {
/* 1821 */         registerOutParameter(getNamedParamIndex(parameterName, true), (MysqlType)sqlType, typeName);
/*      */       } else {
/* 1823 */         registerOutParameter(parameterName, sqlType.getVendorTypeNumber().intValue(), typeName);
/*      */       }  return; }
/* 1825 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void retrieveOutParams() throws SQLException {
/*      */     
/* 1834 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 1835 */         int numParameters = this.paramInfo.numberOfParameters();
/*      */         
/* 1837 */         this.parameterIndexToRsIndex = new int[numParameters];
/*      */         
/* 1839 */         for (int i = 0; i < numParameters; i++) {
/* 1840 */           this.parameterIndexToRsIndex[i] = Integer.MIN_VALUE;
/*      */         }
/*      */         
/* 1843 */         int localParamIndex = 0;
/*      */         
/* 1845 */         if (numParameters > 0) {
/* 1846 */           StringBuilder outParameterQuery = new StringBuilder("SELECT ");
/*      */           
/* 1848 */           boolean firstParam = true;
/* 1849 */           boolean hadOutputParams = false;
/*      */           
/* 1851 */           for (Iterator<CallableStatementParam> paramIter = this.paramInfo.iterator(); paramIter.hasNext(); ) {
/* 1852 */             CallableStatementParam retrParamInfo = paramIter.next();
/*      */             
/* 1854 */             if (retrParamInfo.isOut) {
/* 1855 */               hadOutputParams = true;
/*      */               
/* 1857 */               this.parameterIndexToRsIndex[retrParamInfo.index] = localParamIndex++;
/*      */               
/* 1859 */               if (retrParamInfo.paramName == null) {
/* 1860 */                 retrParamInfo.paramName = "nullnp" + retrParamInfo.index;
/*      */               }
/*      */               
/* 1863 */               String outParameterName = mangleParameterName(retrParamInfo.paramName);
/*      */               
/* 1865 */               if (!firstParam) {
/* 1866 */                 outParameterQuery.append(",");
/*      */               } else {
/* 1868 */                 firstParam = false;
/*      */               } 
/*      */               
/* 1871 */               if (!outParameterName.startsWith("@")) {
/* 1872 */                 outParameterQuery.append('@');
/*      */               }
/*      */               
/* 1875 */               outParameterQuery.append(outParameterName);
/*      */             } 
/*      */           } 
/*      */           
/* 1879 */           if (hadOutputParams) {
/*      */             
/* 1881 */             Statement outParameterStmt = null;
/* 1882 */             ResultSet outParamRs = null;
/*      */             
/*      */             try {
/* 1885 */               outParameterStmt = this.connection.createStatement();
/* 1886 */               outParamRs = outParameterStmt.executeQuery(outParameterQuery.toString());
/* 1887 */               this.outputParameterResults = (ResultSetInternalMethods)this.resultSetFactory.createFromResultsetRows(outParamRs.getConcurrency(), outParamRs.getType(), ((ResultSetInternalMethods)outParamRs)
/* 1888 */                   .getRows());
/*      */               
/* 1890 */               if (!this.outputParameterResults.next()) {
/* 1891 */                 this.outputParameterResults.close();
/* 1892 */                 this.outputParameterResults = null;
/*      */               } 
/*      */             } finally {
/* 1895 */               if (outParameterStmt != null) {
/* 1896 */                 outParameterStmt.close();
/*      */               }
/*      */             } 
/*      */           } else {
/* 1900 */             this.outputParameterResults = null;
/*      */           } 
/*      */         } else {
/* 1903 */           this.outputParameterResults = null;
/*      */         } 
/*      */       }  return; }
/* 1906 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
/*      */     
/* 1910 */     try { setAsciiStream(getNamedParamIndex(parameterName, false), x, length); return; }
/* 1911 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
/*      */     
/* 1915 */     try { setBigDecimal(getNamedParamIndex(parameterName, false), x); return; }
/* 1916 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
/*      */     
/* 1920 */     try { setBinaryStream(getNamedParamIndex(parameterName, false), x, length); return; }
/* 1921 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setBoolean(String parameterName, boolean x) throws SQLException {
/*      */     
/* 1925 */     try { setBoolean(getNamedParamIndex(parameterName, false), x); return; }
/* 1926 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setByte(String parameterName, byte x) throws SQLException {
/*      */     
/* 1930 */     try { setByte(getNamedParamIndex(parameterName, false), x); return; }
/* 1931 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setBytes(String parameterName, byte[] x) throws SQLException {
/*      */     
/* 1935 */     try { setBytes(getNamedParamIndex(parameterName, false), x); return; }
/* 1936 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {
/*      */     
/* 1940 */     try { setCharacterStream(getNamedParamIndex(parameterName, false), reader, length); return; }
/* 1941 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setDate(String parameterName, Date x) throws SQLException {
/*      */     
/* 1945 */     try { setDate(getNamedParamIndex(parameterName, false), x); return; }
/* 1946 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
/*      */     
/* 1950 */     try { setDate(getNamedParamIndex(parameterName, false), x, cal); return; }
/* 1951 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setDouble(String parameterName, double x) throws SQLException {
/*      */     
/* 1955 */     try { setDouble(getNamedParamIndex(parameterName, false), x); return; }
/* 1956 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void setFloat(String parameterName, float x) throws SQLException { 
/* 1960 */     try { setFloat(getNamedParamIndex(parameterName, false), x); return; }
/* 1961 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */      } private void setInOutParamsOnServer() throws SQLException {
/*      */     try {
/* 1964 */       synchronized (checkClosed().getConnectionMutex()) {
/* 1965 */         if (this.paramInfo.numParameters > 0)
/* 1966 */           for (Iterator<CallableStatementParam> paramIter = this.paramInfo.iterator(); paramIter.hasNext(); ) {
/*      */             
/* 1968 */             CallableStatementParam inParamInfo = paramIter.next();
/*      */             
/* 1970 */             if (inParamInfo.isOut && inParamInfo.isIn) {
/* 1971 */               if (inParamInfo.paramName == null) {
/* 1972 */                 inParamInfo.paramName = "nullnp" + inParamInfo.index;
/*      */               }
/*      */               
/* 1975 */               String inOutParameterName = mangleParameterName(inParamInfo.paramName);
/* 1976 */               StringBuilder queryBuf = new StringBuilder(4 + inOutParameterName.length() + 1 + 1);
/* 1977 */               queryBuf.append("SET ");
/* 1978 */               queryBuf.append(inOutParameterName);
/* 1979 */               queryBuf.append("=?");
/*      */               
/* 1981 */               ClientPreparedStatement setPstmt = null;
/*      */               
/*      */               try {
/* 1984 */                 setPstmt = this.connection.clientPrepareStatement(queryBuf.toString()).<ClientPreparedStatement>unwrap(ClientPreparedStatement.class);
/*      */                 
/* 1986 */                 if (((PreparedQuery)this.query).getQueryBindings().getBindValues()[inParamInfo.index].isNull()) {
/* 1987 */                   setPstmt.setBytesNoEscapeNoQuotes(1, "NULL".getBytes());
/*      */                 } else {
/*      */                   
/* 1990 */                   byte[] parameterAsBytes = getBytesRepresentation(inParamInfo.index + 1);
/*      */                   
/* 1992 */                   if (parameterAsBytes != null) {
/* 1993 */                     if (parameterAsBytes.length > 8 && parameterAsBytes[0] == 95 && parameterAsBytes[1] == 98 && parameterAsBytes[2] == 105 && parameterAsBytes[3] == 110 && parameterAsBytes[4] == 97 && parameterAsBytes[5] == 114 && parameterAsBytes[6] == 121 && parameterAsBytes[7] == 39) {
/*      */ 
/*      */                       
/* 1996 */                       setPstmt.setBytesNoEscapeNoQuotes(1, parameterAsBytes);
/*      */                     } else {
/* 1998 */                       switch (inParamInfo.desiredMysqlType) {
/*      */                         case BIT:
/*      */                         case BINARY:
/*      */                         case GEOMETRY:
/*      */                         case TINYBLOB:
/*      */                         case BLOB:
/*      */                         case MEDIUMBLOB:
/*      */                         case LONGBLOB:
/*      */                         case VARBINARY:
/* 2007 */                           setPstmt.setBytes(1, parameterAsBytes);
/*      */                           break;
/*      */                         
/*      */                         default:
/* 2011 */                           setPstmt.setBytesNoEscape(1, parameterAsBytes); break;
/*      */                       } 
/*      */                     } 
/*      */                   } else {
/* 2015 */                     setPstmt.setNull(1, MysqlType.NULL);
/*      */                   } 
/*      */                 } 
/*      */                 
/* 2019 */                 setPstmt.executeUpdate();
/*      */               } finally {
/* 2021 */                 if (setPstmt != null)
/* 2022 */                   setPstmt.close(); 
/*      */               } 
/*      */             } 
/*      */           }  
/*      */       } 
/*      */       return;
/*      */     } catch (CJException cJException) {
/* 2029 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public void setInt(String parameterName, int x) throws SQLException {
/*      */     
/* 2033 */     try { setInt(getNamedParamIndex(parameterName, false), x); return; }
/* 2034 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setLong(String parameterName, long x) throws SQLException {
/*      */     
/* 2038 */     try { setLong(getNamedParamIndex(parameterName, false), x); return; }
/* 2039 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setNull(String parameterName, int sqlType) throws SQLException {
/*      */     
/* 2043 */     try { setNull(getNamedParamIndex(parameterName, false), sqlType); return; }
/* 2044 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
/*      */     
/* 2048 */     try { setNull(getNamedParamIndex(parameterName, false), sqlType, typeName); return; }
/* 2049 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setObject(String parameterName, Object x) throws SQLException {
/*      */     
/* 2053 */     try { setObject(getNamedParamIndex(parameterName, false), x); return; }
/* 2054 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
/*      */     
/* 2058 */     try { setObject(getNamedParamIndex(parameterName, false), x, targetSqlType); return; }
/* 2059 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setObject(String parameterName, Object x, SQLType targetSqlType) throws SQLException {
/*      */     
/* 2063 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 2064 */         setObject(getNamedParamIndex(parameterName, false), x, targetSqlType);
/*      */       }  return; }
/* 2066 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
/*      */     
/* 2070 */     try { setObject(getNamedParamIndex(parameterName, false), x, targetSqlType, scale); return; }
/* 2071 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void setObject(String parameterName, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException { 
/* 2075 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 2076 */         setObject(getNamedParamIndex(parameterName, false), x, targetSqlType, scaleOrLength);
/*      */       }  return; }
/* 2078 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */      } private void setOutParams() throws SQLException {
/*      */     try {
/* 2081 */       synchronized (checkClosed().getConnectionMutex()) {
/* 2082 */         if (this.paramInfo.numParameters > 0)
/* 2083 */           for (Iterator<CallableStatementParam> paramIter = this.paramInfo.iterator(); paramIter.hasNext(); ) {
/* 2084 */             CallableStatementParam outParamInfo = paramIter.next();
/*      */             
/* 2086 */             if (!this.callingStoredFunction && outParamInfo.isOut) {
/*      */               
/* 2088 */               if (outParamInfo.paramName == null) {
/* 2089 */                 outParamInfo.paramName = "nullnp" + outParamInfo.index;
/*      */               }
/*      */               
/* 2092 */               String outParameterName = mangleParameterName(outParamInfo.paramName);
/*      */               
/* 2094 */               int outParamIndex = 0;
/*      */               
/* 2096 */               if (this.placeholderToParameterIndexMap == null) {
/* 2097 */                 outParamIndex = outParamInfo.index + 1;
/*      */               } else {
/*      */                 
/* 2100 */                 boolean found = false;
/*      */                 
/* 2102 */                 for (int i = 0; i < this.placeholderToParameterIndexMap.length; i++) {
/* 2103 */                   if (this.placeholderToParameterIndexMap[i] == outParamInfo.index) {
/* 2104 */                     outParamIndex = i + 1;
/* 2105 */                     found = true;
/*      */                     
/*      */                     break;
/*      */                   } 
/*      */                 } 
/* 2110 */                 if (!found) {
/* 2111 */                   throw SQLError.createSQLException(Messages.getString("CallableStatement.21", new Object[] { outParamInfo.paramName }), "S1009", 
/* 2112 */                       getExceptionInterceptor());
/*      */                 }
/*      */               } 
/*      */               
/* 2116 */               setBytesNoEscapeNoQuotes(outParamIndex, StringUtils.getBytes(outParameterName, this.charEncoding));
/*      */             } 
/*      */           }  
/*      */       }  return;
/*      */     } catch (CJException cJException) {
/* 2121 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public void setShort(String parameterName, short x) throws SQLException {
/*      */     
/* 2125 */     try { setShort(getNamedParamIndex(parameterName, false), x); return; }
/* 2126 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setString(String parameterName, String x) throws SQLException {
/*      */     
/* 2130 */     try { setString(getNamedParamIndex(parameterName, false), x); return; }
/* 2131 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setTime(String parameterName, Time x) throws SQLException {
/*      */     
/* 2135 */     try { setTime(getNamedParamIndex(parameterName, false), x); return; }
/* 2136 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
/*      */     
/* 2140 */     try { setTime(getNamedParamIndex(parameterName, false), x, cal); return; }
/* 2141 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
/*      */     
/* 2145 */     try { setTimestamp(getNamedParamIndex(parameterName, false), x); return; }
/* 2146 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
/*      */     
/* 2150 */     try { setTimestamp(getNamedParamIndex(parameterName, false), x, cal); return; }
/* 2151 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setURL(String parameterName, URL val) throws SQLException {
/*      */     
/* 2155 */     try { setURL(getNamedParamIndex(parameterName, false), val); return; }
/* 2156 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public boolean wasNull() throws SQLException {
/*      */     
/* 2160 */     try { synchronized (checkClosed().getConnectionMutex())
/* 2161 */       { return this.outputParamWasNull; }  }
/* 2162 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public int[] executeBatch() throws SQLException {
/*      */     
/* 2167 */     try { return Util.truncateAndConvertToInt(executeLargeBatch()); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */ 
/*      */   
/*      */   protected int getParameterIndexOffset() {
/* 2173 */     if (this.callingStoredFunction) {
/* 2174 */       return -1;
/*      */     }
/*      */     
/* 2177 */     return super.getParameterIndexOffset();
/*      */   }
/*      */   
/*      */   public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
/*      */     try {
/* 2182 */       setAsciiStream(getNamedParamIndex(parameterName, false), x); return;
/*      */     } catch (CJException cJException) {
/* 2184 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
/*      */     try {
/* 2188 */       setAsciiStream(getNamedParamIndex(parameterName, false), x, length); return;
/*      */     } catch (CJException cJException) {
/* 2190 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
/*      */     try {
/* 2194 */       setBinaryStream(getNamedParamIndex(parameterName, false), x); return;
/*      */     } catch (CJException cJException) {
/* 2196 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {
/*      */     try {
/* 2200 */       setBinaryStream(getNamedParamIndex(parameterName, false), x, length); return;
/*      */     } catch (CJException cJException) {
/* 2202 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public void setBlob(String parameterName, Blob x) throws SQLException {
/*      */     try {
/* 2206 */       setBlob(getNamedParamIndex(parameterName, false), x); return;
/*      */     } catch (CJException cJException) {
/* 2208 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public void setBlob(String parameterName, InputStream inputStream) throws SQLException {
/*      */     try {
/* 2212 */       setBlob(getNamedParamIndex(parameterName, false), inputStream); return;
/*      */     } catch (CJException cJException) {
/* 2214 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
/*      */     try {
/* 2218 */       setBlob(getNamedParamIndex(parameterName, false), inputStream, length); return;
/*      */     } catch (CJException cJException) {
/* 2220 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public void setCharacterStream(String parameterName, Reader reader) throws SQLException {
/*      */     try {
/* 2224 */       setCharacterStream(getNamedParamIndex(parameterName, false), reader); return;
/*      */     } catch (CJException cJException) {
/* 2226 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
/*      */     try {
/* 2230 */       setCharacterStream(getNamedParamIndex(parameterName, false), reader, length); return;
/*      */     } catch (CJException cJException) {
/* 2232 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public void setClob(String parameterName, Clob x) throws SQLException {
/*      */     try {
/* 2236 */       setClob(getNamedParamIndex(parameterName, false), x); return;
/*      */     } catch (CJException cJException) {
/* 2238 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public void setClob(String parameterName, Reader reader) throws SQLException {
/*      */     try {
/* 2242 */       setClob(getNamedParamIndex(parameterName, false), reader); return;
/*      */     } catch (CJException cJException) {
/* 2244 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public void setClob(String parameterName, Reader reader, long length) throws SQLException {
/*      */     try {
/* 2248 */       setClob(getNamedParamIndex(parameterName, false), reader, length); return;
/*      */     } catch (CJException cJException) {
/* 2250 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public void setNCharacterStream(String parameterName, Reader value) throws SQLException {
/*      */     try {
/* 2254 */       setNCharacterStream(getNamedParamIndex(parameterName, false), value); return;
/*      */     } catch (CJException cJException) {
/* 2256 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
/*      */     try {
/* 2260 */       setNCharacterStream(getNamedParamIndex(parameterName, false), value, length); return;
/*      */     } catch (CJException cJException) {
/* 2262 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean checkReadOnlyProcedure() throws SQLException {
/*      */     
/* 2272 */     try { synchronized (checkClosed().getConnectionMutex()) {
/* 2273 */         if (this.noAccessToProcedureBodies) {
/* 2274 */           return false;
/*      */         }
/*      */         
/* 2277 */         if (this.paramInfo.isReadOnlySafeChecked) {
/* 2278 */           return this.paramInfo.isReadOnlySafeProcedure;
/*      */         }
/*      */         
/* 2281 */         ResultSet rs = null;
/* 2282 */         PreparedStatement ps = null;
/*      */         
/*      */         try {
/* 2285 */           String procName = extractProcedureName();
/*      */           
/* 2287 */           String db = getCurrentDatabase();
/*      */           
/* 2289 */           if (procName.indexOf(".") != -1) {
/* 2290 */             db = procName.substring(0, procName.indexOf("."));
/*      */             
/* 2292 */             if (StringUtils.startsWithIgnoreCaseAndWs(db, "`") && db.trim().endsWith("`")) {
/* 2293 */               db = db.substring(1, db.length() - 1);
/*      */             }
/*      */             
/* 2296 */             procName = procName.substring(procName.indexOf(".") + 1);
/* 2297 */             procName = StringUtils.toString(StringUtils.stripEnclosure(StringUtils.getBytes(procName), "`", "`"));
/*      */           } 
/* 2299 */           ps = this.connection.prepareStatement("SELECT SQL_DATA_ACCESS FROM information_schema.routines WHERE routine_schema = ? AND routine_name = ?");
/* 2300 */           ps.setMaxRows(0);
/* 2301 */           ps.setFetchSize(0);
/*      */           
/* 2303 */           ps.setString(1, db);
/* 2304 */           ps.setString(2, procName);
/* 2305 */           rs = ps.executeQuery();
/* 2306 */           if (rs.next()) {
/* 2307 */             String sqlDataAccess = rs.getString(1);
/* 2308 */             if ("READS SQL DATA".equalsIgnoreCase(sqlDataAccess) || "NO SQL".equalsIgnoreCase(sqlDataAccess)) {
/* 2309 */               synchronized (this.paramInfo) {
/* 2310 */                 this.paramInfo.isReadOnlySafeChecked = true;
/* 2311 */                 this.paramInfo.isReadOnlySafeProcedure = true;
/*      */               } 
/* 2313 */               return true;
/*      */             } 
/*      */           } 
/* 2316 */         } catch (SQLException sQLException) {
/*      */         
/*      */         } finally {
/* 2319 */           if (rs != null) {
/* 2320 */             rs.close();
/*      */           }
/* 2322 */           if (ps != null) {
/* 2323 */             ps.close();
/*      */           }
/*      */         } 
/*      */         
/* 2327 */         this.paramInfo.isReadOnlySafeChecked = false;
/* 2328 */         this.paramInfo.isReadOnlySafeProcedure = false;
/*      */       } 
/* 2330 */       return false; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */ 
/*      */   
/*      */   protected boolean checkReadOnlySafeStatement() throws SQLException {
/* 2336 */     if (ParseInfo.isReadOnlySafeQuery(((PreparedQuery)this.query).getOriginalSql(), this.session.getServerSession().isNoBackslashEscapesSet())) {
/* 2337 */       String sql = ((PreparedQuery)this.query).getOriginalSql();
/* 2338 */       int statementKeywordPos = ParseInfo.indexOfStatementKeyword(sql, this.session.getServerSession().isNoBackslashEscapesSet());
/* 2339 */       if (StringUtils.startsWithIgnoreCaseAndWs(sql, "CALL", statementKeywordPos) || 
/* 2340 */         StringUtils.startsWithIgnoreCaseAndWs(sql, "SELECT", statementKeywordPos)) {
/*      */         
/* 2342 */         if (!this.connection.isReadOnly()) {
/* 2343 */           return true;
/*      */         }
/* 2345 */         return checkReadOnlyProcedure();
/*      */       } 
/* 2347 */       return true;
/*      */     } 
/* 2349 */     return !this.connection.isReadOnly();
/*      */   }
/*      */   
/*      */   public RowId getRowId(int parameterIndex) throws SQLException {
/*      */     
/* 2354 */     try { ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */       
/* 2356 */       RowId retValue = rs.getRowId(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */       
/* 2358 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 2360 */       return retValue; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public RowId getRowId(String parameterName) throws SQLException {
/*      */     
/* 2365 */     try { ResultSetInternalMethods rs = getOutputParameters(0);
/*      */       
/* 2367 */       RowId retValue = rs.getRowId(fixParameterName(parameterName));
/*      */       
/* 2369 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 2371 */       return retValue; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public void setRowId(String parameterName, RowId x) throws SQLException {
/*      */     
/* 2376 */     try { setRowId(getNamedParamIndex(parameterName, false), x); return; }
/* 2377 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setNString(String parameterName, String value) throws SQLException {
/*      */     
/* 2381 */     try { setNString(getNamedParamIndex(parameterName, false), value); return; }
/* 2382 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   } public void setNClob(String parameterName, NClob value) throws SQLException {
/*      */     try {
/* 2386 */       setNClob(getNamedParamIndex(parameterName, false), value); return;
/*      */     } catch (CJException cJException) {
/* 2388 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public void setNClob(String parameterName, Reader reader) throws SQLException {
/*      */     try {
/* 2392 */       setNClob(getNamedParamIndex(parameterName, false), reader); return;
/*      */     } catch (CJException cJException) {
/* 2394 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
/*      */     try {
/* 2398 */       setNClob(getNamedParamIndex(parameterName, false), reader, length); return;
/*      */     } catch (CJException cJException) {
/* 2400 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
/*      */     try {
/* 2404 */       setSQLXML(getNamedParamIndex(parameterName, false), xmlObject); return;
/*      */     } catch (CJException cJException) {
/* 2406 */       throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor());
/*      */     } 
/*      */   } public SQLXML getSQLXML(int parameterIndex) throws SQLException {
/*      */     
/* 2410 */     try { ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */       
/* 2412 */       SQLXML retValue = rs.getSQLXML(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */       
/* 2414 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 2416 */       return retValue; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public SQLXML getSQLXML(String parameterName) throws SQLException {
/*      */     
/* 2422 */     try { ResultSetInternalMethods rs = getOutputParameters(0);
/*      */       
/* 2424 */       SQLXML retValue = rs.getSQLXML(fixParameterName(parameterName));
/*      */       
/* 2426 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 2428 */       return retValue; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public String getNString(int parameterIndex) throws SQLException {
/*      */     
/* 2433 */     try { ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */       
/* 2435 */       String retValue = rs.getNString(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */       
/* 2437 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 2439 */       return retValue; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public String getNString(String parameterName) throws SQLException {
/*      */     
/* 2444 */     try { ResultSetInternalMethods rs = getOutputParameters(0);
/* 2445 */       String retValue = rs.getNString(fixParameterName(parameterName));
/*      */       
/* 2447 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 2449 */       return retValue; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Reader getNCharacterStream(int parameterIndex) throws SQLException {
/*      */     
/* 2454 */     try { ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */       
/* 2456 */       Reader retValue = rs.getNCharacterStream(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */       
/* 2458 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 2460 */       return retValue; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Reader getNCharacterStream(String parameterName) throws SQLException {
/*      */     
/* 2465 */     try { ResultSetInternalMethods rs = getOutputParameters(0);
/* 2466 */       Reader retValue = rs.getNCharacterStream(fixParameterName(parameterName));
/*      */       
/* 2468 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 2470 */       return retValue; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Reader getCharacterStream(int parameterIndex) throws SQLException {
/*      */     
/* 2475 */     try { ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */       
/* 2477 */       Reader retValue = rs.getCharacterStream(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */       
/* 2479 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 2481 */       return retValue; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public Reader getCharacterStream(String parameterName) throws SQLException {
/*      */     
/* 2486 */     try { ResultSetInternalMethods rs = getOutputParameters(0);
/*      */       
/* 2488 */       Reader retValue = rs.getCharacterStream(fixParameterName(parameterName));
/*      */       
/* 2490 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 2492 */       return retValue; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public NClob getNClob(int parameterIndex) throws SQLException {
/*      */     
/* 2497 */     try { ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */       
/* 2499 */       NClob retValue = rs.getNClob(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */       
/* 2501 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 2503 */       return retValue; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public NClob getNClob(String parameterName) throws SQLException {
/*      */     
/* 2508 */     try { ResultSetInternalMethods rs = getOutputParameters(0);
/*      */       
/* 2510 */       NClob retValue = rs.getNClob(fixParameterName(parameterName));
/*      */       
/* 2512 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 2514 */       return retValue; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
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
/*      */   protected byte[] s2b(String s) {
/* 2526 */     return (s == null) ? null : StringUtils.getBytes(s, this.charEncoding);
/*      */   }
/*      */   
/*      */   public long executeLargeUpdate() throws SQLException {
/*      */     
/* 2531 */     try { synchronized (checkClosed().getConnectionMutex())
/* 2532 */       { long returnVal = -1L;
/*      */         
/* 2534 */         checkStreamability();
/*      */         
/* 2536 */         if (this.callingStoredFunction) {
/* 2537 */           execute();
/*      */           
/* 2539 */           return -1L;
/*      */         } 
/*      */         
/* 2542 */         setInOutParamsOnServer();
/* 2543 */         setOutParams();
/*      */         
/* 2545 */         returnVal = super.executeLargeUpdate();
/*      */         
/* 2547 */         retrieveOutParams();
/*      */         
/* 2549 */         return returnVal; }  }
/* 2550 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public long[] executeLargeBatch() throws SQLException {
/*      */     
/* 2555 */     try { if (this.hasOutputParams) {
/* 2556 */         throw SQLError.createSQLException("Can't call executeBatch() on CallableStatement with OUTPUT parameters", "S1009", 
/* 2557 */             getExceptionInterceptor());
/*      */       }
/*      */       
/* 2560 */       return super.executeLargeBatch(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\CallableStatement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */