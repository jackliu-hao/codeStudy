/*     */ package com.mysql.cj.jdbc;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.MysqlType;
/*     */ import com.mysql.cj.Session;
/*     */ import com.mysql.cj.exceptions.CJException;
/*     */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*     */ import com.mysql.cj.jdbc.exceptions.SQLError;
/*     */ import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
/*     */ import com.mysql.cj.jdbc.result.ResultSetMetaData;
/*     */ import com.mysql.cj.result.Field;
/*     */ import java.sql.ParameterMetaData;
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
/*     */ 
/*     */ 
/*     */ public class MysqlParameterMetadata
/*     */   implements ParameterMetaData
/*     */ {
/*     */   boolean returnSimpleMetadata = false;
/*  47 */   ResultSetMetaData metadata = null;
/*     */   
/*  49 */   int parameterCount = 0;
/*     */   
/*     */   private ExceptionInterceptor exceptionInterceptor;
/*     */   
/*     */   public MysqlParameterMetadata(Session session, Field[] fieldInfo, int parameterCount, ExceptionInterceptor exceptionInterceptor) {
/*  54 */     this.metadata = new ResultSetMetaData(session, fieldInfo, false, true, exceptionInterceptor);
/*     */     
/*  56 */     this.parameterCount = parameterCount;
/*  57 */     this.exceptionInterceptor = exceptionInterceptor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   MysqlParameterMetadata(int count) {
/*  68 */     this.parameterCount = count;
/*  69 */     this.returnSimpleMetadata = true;
/*     */   }
/*     */   
/*     */   public int getParameterCount() throws SQLException {
/*     */     
/*  74 */     try { return this.parameterCount; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public int isNullable(int arg0) throws SQLException {
/*     */     
/*  79 */     try { checkAvailable();
/*     */       
/*  81 */       return this.metadata.isNullable(arg0); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   private void checkAvailable() throws SQLException {
/*  85 */     if (this.metadata == null || this.metadata.getFields() == null) {
/*  86 */       throw SQLError.createSQLException(Messages.getString("MysqlParameterMetadata.0"), "S1C00", this.exceptionInterceptor);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isSigned(int arg0) throws SQLException {
/*     */     
/*  93 */     try { if (this.returnSimpleMetadata) {
/*  94 */         checkBounds(arg0);
/*     */         
/*  96 */         return false;
/*     */       } 
/*     */       
/*  99 */       checkAvailable();
/*     */       
/* 101 */       return this.metadata.isSigned(arg0); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public int getPrecision(int arg0) throws SQLException {
/*     */     
/* 106 */     try { if (this.returnSimpleMetadata) {
/* 107 */         checkBounds(arg0);
/*     */         
/* 109 */         return 0;
/*     */       } 
/*     */       
/* 112 */       checkAvailable();
/*     */       
/* 114 */       return this.metadata.getPrecision(arg0); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public int getScale(int arg0) throws SQLException {
/*     */     
/* 119 */     try { if (this.returnSimpleMetadata) {
/* 120 */         checkBounds(arg0);
/*     */         
/* 122 */         return 0;
/*     */       } 
/*     */       
/* 125 */       checkAvailable();
/*     */       
/* 127 */       return this.metadata.getScale(arg0); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public int getParameterType(int arg0) throws SQLException {
/*     */     
/* 132 */     try { if (this.returnSimpleMetadata) {
/* 133 */         checkBounds(arg0);
/*     */         
/* 135 */         return MysqlType.VARCHAR.getJdbcType();
/*     */       } 
/*     */       
/* 138 */       checkAvailable();
/*     */       
/* 140 */       return this.metadata.getColumnType(arg0); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public String getParameterTypeName(int arg0) throws SQLException {
/*     */     
/* 145 */     try { if (this.returnSimpleMetadata) {
/* 146 */         checkBounds(arg0);
/*     */         
/* 148 */         return MysqlType.VARCHAR.getName();
/*     */       } 
/*     */       
/* 151 */       checkAvailable();
/*     */       
/* 153 */       return this.metadata.getColumnTypeName(arg0); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public String getParameterClassName(int arg0) throws SQLException {
/*     */     
/* 158 */     try { if (this.returnSimpleMetadata) {
/* 159 */         checkBounds(arg0);
/*     */         
/* 161 */         return "java.lang.String";
/*     */       } 
/*     */       
/* 164 */       checkAvailable();
/*     */       
/* 166 */       return this.metadata.getColumnClassName(arg0); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public int getParameterMode(int arg0) throws SQLException {
/*     */     
/* 171 */     try { return 1; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   private void checkBounds(int paramNumber) throws SQLException {
/* 175 */     if (paramNumber < 1) {
/* 176 */       throw SQLError.createSQLException(Messages.getString("MysqlParameterMetadata.1", new Object[] { Integer.valueOf(paramNumber) }), "S1009", this.exceptionInterceptor);
/*     */     }
/*     */ 
/*     */     
/* 180 */     if (paramNumber > this.parameterCount) {
/* 181 */       throw SQLError.createSQLException(Messages.getString("MysqlParameterMetadata.2", new Object[] { Integer.valueOf(paramNumber), Integer.valueOf(this.parameterCount) }), "S1009", this.exceptionInterceptor);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWrapperFor(Class<?> iface) throws SQLException {
/*     */     
/* 190 */     try { return iface.isInstance(this); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   
/*     */   public <T> T unwrap(Class<T> iface) throws SQLException {
/*     */     
/*     */     try { 
/* 197 */       try { return iface.cast(this); }
/* 198 */       catch (ClassCastException cce)
/* 199 */       { throw SQLError.createSQLException(Messages.getString("Common.UnableToUnwrap", new Object[] { iface.toString() }), "S1009", this.exceptionInterceptor); }  } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\MysqlParameterMetadata.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */