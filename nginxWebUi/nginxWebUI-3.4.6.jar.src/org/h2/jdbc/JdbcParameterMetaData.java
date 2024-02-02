/*     */ package org.h2.jdbc;
/*     */ 
/*     */ import java.sql.ParameterMetaData;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import org.h2.command.CommandInterface;
/*     */ import org.h2.expression.ParameterInterface;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.message.Trace;
/*     */ import org.h2.message.TraceObject;
/*     */ import org.h2.util.MathUtils;
/*     */ import org.h2.value.DataType;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.ValueToObjectConverter;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class JdbcParameterMetaData
/*     */   extends TraceObject
/*     */   implements ParameterMetaData
/*     */ {
/*     */   private final JdbcPreparedStatement prep;
/*     */   private final int paramCount;
/*     */   private final ArrayList<? extends ParameterInterface> parameters;
/*     */   
/*     */   JdbcParameterMetaData(Trace paramTrace, JdbcPreparedStatement paramJdbcPreparedStatement, CommandInterface paramCommandInterface, int paramInt) {
/*  33 */     setTrace(paramTrace, 11, paramInt);
/*  34 */     this.prep = paramJdbcPreparedStatement;
/*  35 */     this.parameters = paramCommandInterface.getParameters();
/*  36 */     this.paramCount = this.parameters.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getParameterCount() throws SQLException {
/*     */     try {
/*  47 */       debugCodeCall("getParameterCount");
/*  48 */       checkClosed();
/*  49 */       return this.paramCount;
/*  50 */     } catch (Exception exception) {
/*  51 */       throw logAndConvert(exception);
/*     */     } 
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
/*     */   public int getParameterMode(int paramInt) throws SQLException {
/*     */     try {
/*  65 */       debugCodeCall("getParameterMode", paramInt);
/*  66 */       getParameter(paramInt);
/*  67 */       return 1;
/*  68 */     } catch (Exception exception) {
/*  69 */       throw logAndConvert(exception);
/*     */     } 
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
/*     */   public int getParameterType(int paramInt) throws SQLException {
/*     */     try {
/*  83 */       debugCodeCall("getParameterType", paramInt);
/*  84 */       TypeInfo typeInfo = getParameter(paramInt).getType();
/*  85 */       if (typeInfo.getValueType() == -1) {
/*  86 */         typeInfo = TypeInfo.TYPE_VARCHAR;
/*     */       }
/*  88 */       return DataType.convertTypeToSQLType(typeInfo);
/*  89 */     } catch (Exception exception) {
/*  90 */       throw logAndConvert(exception);
/*     */     } 
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
/*     */   public int getPrecision(int paramInt) throws SQLException {
/*     */     try {
/* 104 */       debugCodeCall("getPrecision", paramInt);
/* 105 */       TypeInfo typeInfo = getParameter(paramInt).getType();
/* 106 */       return (typeInfo.getValueType() == -1) ? 0 : MathUtils.convertLongToInt(typeInfo.getPrecision());
/* 107 */     } catch (Exception exception) {
/* 108 */       throw logAndConvert(exception);
/*     */     } 
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
/*     */   public int getScale(int paramInt) throws SQLException {
/*     */     try {
/* 122 */       debugCodeCall("getScale", paramInt);
/* 123 */       TypeInfo typeInfo = getParameter(paramInt).getType();
/* 124 */       return (typeInfo.getValueType() == -1) ? 0 : typeInfo.getScale();
/* 125 */     } catch (Exception exception) {
/* 126 */       throw logAndConvert(exception);
/*     */     } 
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
/*     */   public int isNullable(int paramInt) throws SQLException {
/*     */     try {
/* 140 */       debugCodeCall("isNullable", paramInt);
/* 141 */       return getParameter(paramInt).getNullable();
/* 142 */     } catch (Exception exception) {
/* 143 */       throw logAndConvert(exception);
/*     */     } 
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
/*     */   public boolean isSigned(int paramInt) throws SQLException {
/*     */     try {
/* 157 */       debugCodeCall("isSigned", paramInt);
/* 158 */       getParameter(paramInt);
/* 159 */       return true;
/* 160 */     } catch (Exception exception) {
/* 161 */       throw logAndConvert(exception);
/*     */     } 
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
/*     */   public String getParameterClassName(int paramInt) throws SQLException {
/*     */     try {
/* 175 */       debugCodeCall("getParameterClassName", paramInt);
/* 176 */       int i = getParameter(paramInt).getType().getValueType();
/* 177 */       if (i == -1) {
/* 178 */         i = 2;
/*     */       }
/* 180 */       return ValueToObjectConverter.getDefaultClass(i, true).getName();
/* 181 */     } catch (Exception exception) {
/* 182 */       throw logAndConvert(exception);
/*     */     } 
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
/*     */   public String getParameterTypeName(int paramInt) throws SQLException {
/*     */     try {
/* 196 */       debugCodeCall("getParameterTypeName", paramInt);
/* 197 */       TypeInfo typeInfo = getParameter(paramInt).getType();
/* 198 */       if (typeInfo.getValueType() == -1) {
/* 199 */         typeInfo = TypeInfo.TYPE_VARCHAR;
/*     */       }
/* 201 */       return typeInfo.getDeclaredTypeName();
/* 202 */     } catch (Exception exception) {
/* 203 */       throw logAndConvert(exception);
/*     */     } 
/*     */   }
/*     */   
/*     */   private ParameterInterface getParameter(int paramInt) {
/* 208 */     checkClosed();
/* 209 */     if (paramInt < 1 || paramInt > this.paramCount) {
/* 210 */       throw DbException.getInvalidValueException("param", Integer.valueOf(paramInt));
/*     */     }
/* 212 */     return this.parameters.get(paramInt - 1);
/*     */   }
/*     */   
/*     */   private void checkClosed() {
/* 216 */     this.prep.checkClosed();
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
/*     */   public <T> T unwrap(Class<T> paramClass) throws SQLException {
/*     */     try {
/* 229 */       if (isWrapperFor(paramClass)) {
/* 230 */         return (T)this;
/*     */       }
/* 232 */       throw DbException.getInvalidValueException("iface", paramClass);
/* 233 */     } catch (Exception exception) {
/* 234 */       throw logAndConvert(exception);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWrapperFor(Class<?> paramClass) throws SQLException {
/* 246 */     return (paramClass != null && paramClass.isAssignableFrom(getClass()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 254 */     return getTraceObjectName() + ": parameterCount=" + this.paramCount;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\jdbc\JdbcParameterMetaData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */