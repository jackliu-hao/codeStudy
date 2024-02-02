/*     */ package org.h2.result;
/*     */ 
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import org.h2.engine.Session;
/*     */ import org.h2.engine.SessionRemote;
/*     */ import org.h2.jdbc.JdbcConnection;
/*     */ import org.h2.jdbc.JdbcResultSet;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.util.JdbcUtils;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.util.Utils;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueNull;
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
/*     */ public class UpdatableRow
/*     */ {
/*     */   private final JdbcConnection conn;
/*     */   private final ResultInterface result;
/*     */   private final int columnCount;
/*     */   private String schemaName;
/*     */   private String tableName;
/*     */   private ArrayList<String> key;
/*     */   private boolean isUpdatable;
/*     */   
/*     */   public UpdatableRow(JdbcConnection paramJdbcConnection, ResultInterface paramResultInterface) throws SQLException {
/*  52 */     this.conn = paramJdbcConnection;
/*  53 */     this.result = paramResultInterface;
/*  54 */     this.columnCount = paramResultInterface.getVisibleColumnCount();
/*  55 */     if (this.columnCount == 0) {
/*     */       return;
/*     */     }
/*  58 */     for (byte b = 0; b < this.columnCount; b++) {
/*  59 */       String str3 = paramResultInterface.getTableName(b);
/*  60 */       String str4 = paramResultInterface.getSchemaName(b);
/*  61 */       if (str3 == null || str4 == null) {
/*     */         return;
/*     */       }
/*  64 */       if (this.tableName == null) {
/*  65 */         this.tableName = str3;
/*  66 */       } else if (!this.tableName.equals(str3)) {
/*     */         return;
/*     */       } 
/*  69 */       if (this.schemaName == null) {
/*  70 */         this.schemaName = str4;
/*  71 */       } else if (!this.schemaName.equals(str4)) {
/*     */         return;
/*     */       } 
/*     */     } 
/*  75 */     String str1 = "BASE TABLE";
/*  76 */     Session session = paramJdbcConnection.getSession();
/*  77 */     if (session instanceof SessionRemote && ((SessionRemote)session)
/*  78 */       .getClientVersion() <= 19) {
/*  79 */       str1 = "TABLE";
/*     */     }
/*  81 */     DatabaseMetaData databaseMetaData = paramJdbcConnection.getMetaData();
/*  82 */     ResultSet resultSet = databaseMetaData.getTables(null, 
/*  83 */         StringUtils.escapeMetaDataPattern(this.schemaName), 
/*  84 */         StringUtils.escapeMetaDataPattern(this.tableName), new String[] { str1 });
/*     */     
/*  86 */     if (!resultSet.next()) {
/*     */       return;
/*     */     }
/*  89 */     String str2 = resultSet.getString("TABLE_NAME");
/*     */ 
/*     */ 
/*     */     
/*  93 */     boolean bool = (!str2.equals(this.tableName) && str2.equalsIgnoreCase(this.tableName)) ? true : false;
/*  94 */     this.key = Utils.newSmallArrayList();
/*  95 */     resultSet = databaseMetaData.getPrimaryKeys(null, 
/*  96 */         StringUtils.escapeMetaDataPattern(this.schemaName), this.tableName);
/*     */     
/*  98 */     while (resultSet.next()) {
/*  99 */       String str = resultSet.getString("COLUMN_NAME");
/* 100 */       this.key.add(bool ? StringUtils.toUpperEnglish(str) : str);
/*     */     } 
/* 102 */     if (isIndexUsable(this.key)) {
/* 103 */       this.isUpdatable = true;
/*     */       return;
/*     */     } 
/* 106 */     this.key.clear();
/* 107 */     resultSet = databaseMetaData.getIndexInfo(null, 
/* 108 */         StringUtils.escapeMetaDataPattern(this.schemaName), this.tableName, true, true);
/*     */     
/* 110 */     while (resultSet.next()) {
/* 111 */       short s = resultSet.getShort("ORDINAL_POSITION");
/* 112 */       if (s == 1) {
/*     */         
/* 114 */         if (isIndexUsable(this.key)) {
/* 115 */           this.isUpdatable = true;
/*     */           return;
/*     */         } 
/* 118 */         this.key.clear();
/*     */       } 
/* 120 */       String str = resultSet.getString("COLUMN_NAME");
/* 121 */       this.key.add(bool ? StringUtils.toUpperEnglish(str) : str);
/*     */     } 
/* 123 */     if (isIndexUsable(this.key)) {
/* 124 */       this.isUpdatable = true;
/*     */       return;
/*     */     } 
/* 127 */     this.key = null;
/*     */   }
/*     */   
/*     */   private boolean isIndexUsable(ArrayList<String> paramArrayList) {
/* 131 */     if (paramArrayList.isEmpty()) {
/* 132 */       return false;
/*     */     }
/* 134 */     for (String str : paramArrayList) {
/* 135 */       if (findColumnIndex(str) < 0) {
/* 136 */         return false;
/*     */       }
/*     */     } 
/* 139 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isUpdatable() {
/* 148 */     return this.isUpdatable;
/*     */   }
/*     */   
/*     */   private int findColumnIndex(String paramString) {
/* 152 */     for (byte b = 0; b < this.columnCount; b++) {
/* 153 */       String str = this.result.getColumnName(b);
/* 154 */       if (str.equals(paramString)) {
/* 155 */         return b;
/*     */       }
/*     */     } 
/* 158 */     return -1;
/*     */   }
/*     */   
/*     */   private int getColumnIndex(String paramString) {
/* 162 */     int i = findColumnIndex(paramString);
/* 163 */     if (i < 0) {
/* 164 */       throw DbException.get(42122, paramString);
/*     */     }
/* 166 */     return i;
/*     */   }
/*     */   
/*     */   private void appendColumnList(StringBuilder paramStringBuilder, boolean paramBoolean) {
/* 170 */     for (byte b = 0; b < this.columnCount; b++) {
/* 171 */       if (b > 0) {
/* 172 */         paramStringBuilder.append(',');
/*     */       }
/* 174 */       String str = this.result.getColumnName(b);
/* 175 */       StringUtils.quoteIdentifier(paramStringBuilder, str);
/* 176 */       if (paramBoolean) {
/* 177 */         paramStringBuilder.append("=? ");
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private void appendKeyCondition(StringBuilder paramStringBuilder) {
/* 183 */     paramStringBuilder.append(" WHERE ");
/* 184 */     for (byte b = 0; b < this.key.size(); b++) {
/* 185 */       if (b > 0) {
/* 186 */         paramStringBuilder.append(" AND ");
/*     */       }
/* 188 */       StringUtils.quoteIdentifier(paramStringBuilder, this.key.get(b)).append("=?");
/*     */     } 
/*     */   } private void setKey(PreparedStatement paramPreparedStatement, int paramInt, Value[] paramArrayOfValue) throws SQLException {
/*     */     byte b;
/*     */     int i;
/* 193 */     for (b = 0, i = this.key.size(); b < i; b++) {
/* 194 */       String str = this.key.get(b);
/* 195 */       int j = getColumnIndex(str);
/* 196 */       Value value = paramArrayOfValue[j];
/* 197 */       if (value == null || value == ValueNull.INSTANCE)
/*     */       {
/*     */         
/* 200 */         throw DbException.get(2000);
/*     */       }
/* 202 */       JdbcUtils.set(paramPreparedStatement, paramInt + b, value, this.conn);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void appendTableName(StringBuilder paramStringBuilder) {
/* 219 */     if (this.schemaName != null && this.schemaName.length() > 0) {
/* 220 */       StringUtils.quoteIdentifier(paramStringBuilder, this.schemaName).append('.');
/*     */     }
/* 222 */     StringUtils.quoteIdentifier(paramStringBuilder, this.tableName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Value[] readRow(Value[] paramArrayOfValue) throws SQLException {
/* 233 */     StringBuilder stringBuilder = new StringBuilder("SELECT ");
/* 234 */     appendColumnList(stringBuilder, false);
/* 235 */     stringBuilder.append(" FROM ");
/* 236 */     appendTableName(stringBuilder);
/* 237 */     appendKeyCondition(stringBuilder);
/* 238 */     PreparedStatement preparedStatement = this.conn.prepareStatement(stringBuilder.toString());
/* 239 */     setKey(preparedStatement, 1, paramArrayOfValue);
/* 240 */     JdbcResultSet jdbcResultSet = (JdbcResultSet)preparedStatement.executeQuery();
/* 241 */     if (!jdbcResultSet.next()) {
/* 242 */       throw DbException.get(2000);
/*     */     }
/* 244 */     Value[] arrayOfValue = new Value[this.columnCount];
/* 245 */     for (byte b = 0; b < this.columnCount; b++) {
/* 246 */       arrayOfValue[b] = ValueToObjectConverter.readValue(this.conn.getSession(), jdbcResultSet, b + 1);
/*     */     }
/* 248 */     return arrayOfValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void deleteRow(Value[] paramArrayOfValue) throws SQLException {
/* 258 */     StringBuilder stringBuilder = new StringBuilder("DELETE FROM ");
/* 259 */     appendTableName(stringBuilder);
/* 260 */     appendKeyCondition(stringBuilder);
/* 261 */     PreparedStatement preparedStatement = this.conn.prepareStatement(stringBuilder.toString());
/* 262 */     setKey(preparedStatement, 1, paramArrayOfValue);
/* 263 */     int i = preparedStatement.executeUpdate();
/* 264 */     if (i != 1)
/*     */     {
/* 266 */       throw DbException.get(2000);
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
/*     */   public void updateRow(Value[] paramArrayOfValue1, Value[] paramArrayOfValue2) throws SQLException {
/* 278 */     StringBuilder stringBuilder = new StringBuilder("UPDATE ");
/* 279 */     appendTableName(stringBuilder);
/* 280 */     stringBuilder.append(" SET ");
/* 281 */     appendColumnList(stringBuilder, true);
/*     */ 
/*     */ 
/*     */     
/* 285 */     appendKeyCondition(stringBuilder);
/* 286 */     PreparedStatement preparedStatement = this.conn.prepareStatement(stringBuilder.toString());
/* 287 */     byte b = 1; int i;
/* 288 */     for (i = 0; i < this.columnCount; i++) {
/* 289 */       Value value = paramArrayOfValue2[i];
/* 290 */       if (value == null) {
/* 291 */         value = paramArrayOfValue1[i];
/*     */       }
/* 293 */       JdbcUtils.set(preparedStatement, b++, value, this.conn);
/*     */     } 
/* 295 */     setKey(preparedStatement, b, paramArrayOfValue1);
/* 296 */     i = preparedStatement.executeUpdate();
/* 297 */     if (i != 1)
/*     */     {
/* 299 */       throw DbException.get(2000);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void insertRow(Value[] paramArrayOfValue) throws SQLException {
/* 310 */     StringBuilder stringBuilder = new StringBuilder("INSERT INTO ");
/* 311 */     appendTableName(stringBuilder);
/* 312 */     stringBuilder.append('(');
/* 313 */     appendColumnList(stringBuilder, false);
/* 314 */     stringBuilder.append(")VALUES(");
/* 315 */     for (byte b1 = 0; b1 < this.columnCount; b1++) {
/* 316 */       if (b1 > 0) {
/* 317 */         stringBuilder.append(',');
/*     */       }
/* 319 */       Value value = paramArrayOfValue[b1];
/* 320 */       if (value == null) {
/* 321 */         stringBuilder.append("DEFAULT");
/*     */       } else {
/* 323 */         stringBuilder.append('?');
/*     */       } 
/*     */     } 
/* 326 */     stringBuilder.append(')');
/* 327 */     PreparedStatement preparedStatement = this.conn.prepareStatement(stringBuilder.toString()); int i; byte b2;
/* 328 */     for (i = 0, b2 = 0; i < this.columnCount; i++) {
/* 329 */       Value value = paramArrayOfValue[i];
/* 330 */       if (value != null) {
/* 331 */         JdbcUtils.set(preparedStatement, b2++ + 1, value, this.conn);
/*     */       }
/*     */     } 
/* 334 */     i = preparedStatement.executeUpdate();
/* 335 */     if (i != 1)
/* 336 */       throw DbException.get(2000); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\result\UpdatableRow.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */