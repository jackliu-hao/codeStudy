/*     */ package org.h2.result;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import org.h2.engine.Session;
/*     */ import org.h2.util.Utils;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
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
/*     */ public class SimpleResult
/*     */   implements ResultInterface, ResultTarget
/*     */ {
/*     */   private final ArrayList<Column> columns;
/*     */   private final ArrayList<Value[]> rows;
/*     */   private final String schemaName;
/*     */   private final String tableName;
/*     */   private int rowId;
/*     */   
/*     */   static final class Column
/*     */   {
/*     */     final String alias;
/*     */     final String columnName;
/*     */     final TypeInfo columnType;
/*     */     
/*     */     Column(String param1String1, String param1String2, TypeInfo param1TypeInfo) {
/*  36 */       if (param1String1 == null || param1String2 == null) {
/*  37 */         throw new NullPointerException();
/*     */       }
/*  39 */       this.alias = param1String1;
/*  40 */       this.columnName = param1String2;
/*  41 */       this.columnType = param1TypeInfo;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public int hashCode() {
/*  47 */       int i = 1;
/*  48 */       i = 31 * i + this.alias.hashCode();
/*  49 */       i = 31 * i + this.columnName.hashCode();
/*  50 */       return i;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object param1Object) {
/*  55 */       if (this == param1Object)
/*  56 */         return true; 
/*  57 */       if (param1Object == null || getClass() != param1Object.getClass())
/*  58 */         return false; 
/*  59 */       Column column = (Column)param1Object;
/*  60 */       return (this.alias.equals(column.alias) && this.columnName.equals(column.columnName));
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/*  65 */       if (this.alias.equals(this.columnName)) {
/*  66 */         return this.columnName;
/*     */       }
/*  68 */       return this.columnName + ' ' + this.alias;
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
/*     */   public SimpleResult() {
/*  85 */     this("", "");
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
/*     */   public SimpleResult(String paramString1, String paramString2) {
/*  97 */     this.columns = Utils.newSmallArrayList();
/*  98 */     this.rows = (ArrayList)new ArrayList<>();
/*  99 */     this.schemaName = paramString1;
/* 100 */     this.tableName = paramString2;
/* 101 */     this.rowId = -1;
/*     */   }
/*     */   
/*     */   private SimpleResult(ArrayList<Column> paramArrayList, ArrayList<Value[]> paramArrayList1, String paramString1, String paramString2) {
/* 105 */     this.columns = paramArrayList;
/* 106 */     this.rows = paramArrayList1;
/* 107 */     this.schemaName = paramString1;
/* 108 */     this.tableName = paramString2;
/* 109 */     this.rowId = -1;
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
/*     */ 
/*     */   
/*     */   public void addColumn(String paramString1, String paramString2, int paramInt1, long paramLong, int paramInt2) {
/* 127 */     addColumn(paramString1, paramString2, TypeInfo.getTypeInfo(paramInt1, paramLong, paramInt2, null));
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
/*     */   public void addColumn(String paramString, TypeInfo paramTypeInfo) {
/* 139 */     addColumn(new Column(paramString, paramString, paramTypeInfo));
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
/*     */   public void addColumn(String paramString1, String paramString2, TypeInfo paramTypeInfo) {
/* 153 */     addColumn(new Column(paramString1, paramString2, paramTypeInfo));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void addColumn(Column paramColumn) {
/* 163 */     assert this.rows.isEmpty();
/* 164 */     this.columns.add(paramColumn);
/*     */   }
/*     */ 
/*     */   
/*     */   public void addRow(Value... paramVarArgs) {
/* 169 */     assert paramVarArgs.length == this.columns.size();
/* 170 */     this.rows.add(paramVarArgs);
/*     */   }
/*     */ 
/*     */   
/*     */   public void reset() {
/* 175 */     this.rowId = -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value[] currentRow() {
/* 180 */     return this.rows.get(this.rowId);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean next() {
/* 185 */     int i = this.rows.size();
/* 186 */     if (this.rowId < i) {
/* 187 */       return (++this.rowId < i);
/*     */     }
/* 189 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getRowId() {
/* 194 */     return this.rowId;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isAfterLast() {
/* 199 */     return (this.rowId >= this.rows.size());
/*     */   }
/*     */ 
/*     */   
/*     */   public int getVisibleColumnCount() {
/* 204 */     return this.columns.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getRowCount() {
/* 209 */     return this.rows.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasNext() {
/* 214 */     return (this.rowId < this.rows.size() - 1);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needToClose() {
/* 219 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public String getAlias(int paramInt) {
/* 229 */     return ((Column)this.columns.get(paramInt)).alias;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSchemaName(int paramInt) {
/* 234 */     return this.schemaName;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getTableName(int paramInt) {
/* 239 */     return this.tableName;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getColumnName(int paramInt) {
/* 244 */     return ((Column)this.columns.get(paramInt)).columnName;
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getColumnType(int paramInt) {
/* 249 */     return ((Column)this.columns.get(paramInt)).columnType;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isIdentity(int paramInt) {
/* 254 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getNullable(int paramInt) {
/* 259 */     return 2;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFetchSize(int paramInt) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public int getFetchSize() {
/* 269 */     return 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isLazy() {
/* 274 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isClosed() {
/* 279 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public SimpleResult createShallowCopy(Session paramSession) {
/* 284 */     return new SimpleResult(this.columns, this.rows, this.schemaName, this.tableName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void limitsWereApplied() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void sortRows(Comparator<? super Value[]> paramComparator) {
/* 299 */     this.rows.sort((Comparator)paramComparator);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\result\SimpleResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */