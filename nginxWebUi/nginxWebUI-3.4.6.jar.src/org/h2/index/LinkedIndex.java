/*     */ package org.h2.index;
/*     */ 
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.util.ArrayList;
/*     */ import org.h2.command.query.AllColumnsForPlan;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.Row;
/*     */ import org.h2.result.SearchRow;
/*     */ import org.h2.result.SortOrder;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.IndexColumn;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.table.TableFilter;
/*     */ import org.h2.table.TableLink;
/*     */ import org.h2.util.Utils;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueNull;
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
/*     */ public class LinkedIndex
/*     */   extends Index
/*     */ {
/*     */   private final TableLink link;
/*     */   private final String targetTableName;
/*     */   private long rowCount;
/*  38 */   private final int sqlFlags = 1;
/*     */   
/*     */   public LinkedIndex(TableLink paramTableLink, int paramInt1, IndexColumn[] paramArrayOfIndexColumn, int paramInt2, IndexType paramIndexType) {
/*  41 */     super((Table)paramTableLink, paramInt1, null, paramArrayOfIndexColumn, paramInt2, paramIndexType);
/*  42 */     this.link = paramTableLink;
/*  43 */     this.targetTableName = this.link.getQualifiedTable();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQL() {
/*  48 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close(SessionLocal paramSessionLocal) {}
/*     */ 
/*     */   
/*     */   private static boolean isNull(Value paramValue) {
/*  57 */     return (paramValue == null || paramValue == ValueNull.INSTANCE);
/*     */   }
/*     */ 
/*     */   
/*     */   public void add(SessionLocal paramSessionLocal, Row paramRow) {
/*  62 */     ArrayList<Value> arrayList = Utils.newSmallArrayList();
/*  63 */     StringBuilder stringBuilder = new StringBuilder("INSERT INTO ");
/*  64 */     stringBuilder.append(this.targetTableName).append(" VALUES(");
/*  65 */     for (byte b = 0; b < paramRow.getColumnCount(); b++) {
/*  66 */       Value value = paramRow.getValue(b);
/*  67 */       if (b > 0) {
/*  68 */         stringBuilder.append(", ");
/*     */       }
/*  70 */       if (value == null) {
/*  71 */         stringBuilder.append("DEFAULT");
/*  72 */       } else if (isNull(value)) {
/*  73 */         stringBuilder.append("NULL");
/*     */       } else {
/*  75 */         stringBuilder.append('?');
/*  76 */         arrayList.add(value);
/*     */       } 
/*     */     } 
/*  79 */     stringBuilder.append(')');
/*  80 */     String str = stringBuilder.toString();
/*     */     try {
/*  82 */       this.link.execute(str, arrayList, true, paramSessionLocal);
/*  83 */       this.rowCount++;
/*  84 */     } catch (Exception exception) {
/*  85 */       throw TableLink.wrapException(str, exception);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Cursor find(SessionLocal paramSessionLocal, SearchRow paramSearchRow1, SearchRow paramSearchRow2) {
/*  91 */     ArrayList<Value> arrayList = Utils.newSmallArrayList();
/*  92 */     StringBuilder stringBuilder = (new StringBuilder("SELECT * FROM ")).append(this.targetTableName).append(" T");
/*  93 */     boolean bool = false; byte b;
/*  94 */     for (b = 0; paramSearchRow1 != null && b < paramSearchRow1.getColumnCount(); b++) {
/*  95 */       Value value = paramSearchRow1.getValue(b);
/*  96 */       if (value != null) {
/*  97 */         stringBuilder.append(bool ? " AND " : " WHERE ");
/*  98 */         bool = true;
/*  99 */         Column column = this.table.getColumn(b);
/* 100 */         column.getSQL(stringBuilder, 1);
/* 101 */         if (value == ValueNull.INSTANCE) {
/* 102 */           stringBuilder.append(" IS NULL");
/*     */         } else {
/* 104 */           stringBuilder.append(">=");
/* 105 */           addParameter(stringBuilder, column);
/* 106 */           arrayList.add(value);
/*     */         } 
/*     */       } 
/*     */     } 
/* 110 */     for (b = 0; paramSearchRow2 != null && b < paramSearchRow2.getColumnCount(); b++) {
/* 111 */       Value value = paramSearchRow2.getValue(b);
/* 112 */       if (value != null) {
/* 113 */         stringBuilder.append(bool ? " AND " : " WHERE ");
/* 114 */         bool = true;
/* 115 */         Column column = this.table.getColumn(b);
/* 116 */         column.getSQL(stringBuilder, 1);
/* 117 */         if (value == ValueNull.INSTANCE) {
/* 118 */           stringBuilder.append(" IS NULL");
/*     */         } else {
/* 120 */           stringBuilder.append("<=");
/* 121 */           addParameter(stringBuilder, column);
/* 122 */           arrayList.add(value);
/*     */         } 
/*     */       } 
/*     */     } 
/* 126 */     String str = stringBuilder.toString();
/*     */     try {
/* 128 */       PreparedStatement preparedStatement = this.link.execute(str, arrayList, false, paramSessionLocal);
/* 129 */       ResultSet resultSet = preparedStatement.getResultSet();
/* 130 */       return new LinkedCursor(this.link, resultSet, paramSessionLocal, str, preparedStatement);
/* 131 */     } catch (Exception exception) {
/* 132 */       throw TableLink.wrapException(str, exception);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void addParameter(StringBuilder paramStringBuilder, Column paramColumn) {
/* 137 */     TypeInfo typeInfo = paramColumn.getType();
/* 138 */     if (typeInfo.getValueType() == 1 && this.link.isOracle()) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 143 */       paramStringBuilder.append("CAST(? AS CHAR(").append(typeInfo.getPrecision()).append("))");
/*     */     } else {
/* 145 */       paramStringBuilder.append('?');
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getCost(SessionLocal paramSessionLocal, int[] paramArrayOfint, TableFilter[] paramArrayOfTableFilter, int paramInt, SortOrder paramSortOrder, AllColumnsForPlan paramAllColumnsForPlan) {
/* 153 */     return (100L + getCostRangeIndex(paramArrayOfint, this.rowCount + 1000L, paramArrayOfTableFilter, paramInt, paramSortOrder, false, paramAllColumnsForPlan));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void remove(SessionLocal paramSessionLocal) {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void truncate(SessionLocal paramSessionLocal) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void checkRename() {
/* 169 */     throw DbException.getUnsupportedException("LINKED");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needRebuild() {
/* 174 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void remove(SessionLocal paramSessionLocal, Row paramRow) {
/* 179 */     ArrayList<Value> arrayList = Utils.newSmallArrayList();
/* 180 */     StringBuilder stringBuilder = (new StringBuilder("DELETE FROM ")).append(this.targetTableName).append(" WHERE ");
/* 181 */     for (byte b = 0; b < paramRow.getColumnCount(); b++) {
/* 182 */       if (b > 0) {
/* 183 */         stringBuilder.append("AND ");
/*     */       }
/* 185 */       Column column = this.table.getColumn(b);
/* 186 */       column.getSQL(stringBuilder, 1);
/* 187 */       Value value = paramRow.getValue(b);
/* 188 */       if (isNull(value)) {
/* 189 */         stringBuilder.append(" IS NULL ");
/*     */       } else {
/* 191 */         stringBuilder.append('=');
/* 192 */         addParameter(stringBuilder, column);
/* 193 */         arrayList.add(value);
/* 194 */         stringBuilder.append(' ');
/*     */       } 
/*     */     } 
/* 197 */     String str = stringBuilder.toString();
/*     */     try {
/* 199 */       PreparedStatement preparedStatement = this.link.execute(str, arrayList, false, paramSessionLocal);
/* 200 */       int i = preparedStatement.executeUpdate();
/* 201 */       this.link.reusePreparedStatement(preparedStatement, str);
/* 202 */       this.rowCount -= i;
/* 203 */     } catch (Exception exception) {
/* 204 */       throw TableLink.wrapException(str, exception);
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
/*     */   public void update(Row paramRow1, Row paramRow2, SessionLocal paramSessionLocal) {
/* 217 */     ArrayList<Value> arrayList = Utils.newSmallArrayList();
/* 218 */     StringBuilder stringBuilder = (new StringBuilder("UPDATE ")).append(this.targetTableName).append(" SET "); byte b;
/* 219 */     for (b = 0; b < paramRow2.getColumnCount(); b++) {
/* 220 */       if (b > 0) {
/* 221 */         stringBuilder.append(", ");
/*     */       }
/* 223 */       this.table.getColumn(b).getSQL(stringBuilder, 1).append('=');
/* 224 */       Value value = paramRow2.getValue(b);
/* 225 */       if (value == null) {
/* 226 */         stringBuilder.append("DEFAULT");
/*     */       } else {
/* 228 */         stringBuilder.append('?');
/* 229 */         arrayList.add(value);
/*     */       } 
/*     */     } 
/* 232 */     stringBuilder.append(" WHERE ");
/* 233 */     for (b = 0; b < paramRow1.getColumnCount(); b++) {
/* 234 */       Column column = this.table.getColumn(b);
/* 235 */       if (b > 0) {
/* 236 */         stringBuilder.append(" AND ");
/*     */       }
/* 238 */       column.getSQL(stringBuilder, 1);
/* 239 */       Value value = paramRow1.getValue(b);
/* 240 */       if (isNull(value)) {
/* 241 */         stringBuilder.append(" IS NULL");
/*     */       } else {
/* 243 */         stringBuilder.append('=');
/* 244 */         arrayList.add(value);
/* 245 */         addParameter(stringBuilder, column);
/*     */       } 
/*     */     } 
/* 248 */     String str = stringBuilder.toString();
/*     */     try {
/* 250 */       this.link.execute(str, arrayList, true, paramSessionLocal);
/* 251 */     } catch (Exception exception) {
/* 252 */       throw TableLink.wrapException(str, exception);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long getRowCount(SessionLocal paramSessionLocal) {
/* 258 */     return this.rowCount;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getRowCountApproximation(SessionLocal paramSessionLocal) {
/* 263 */     return this.rowCount;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\index\LinkedIndex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */