/*     */ package org.h2.table;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.index.Index;
/*     */ import org.h2.index.IndexType;
/*     */ import org.h2.index.MetaIndex;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.Row;
/*     */ import org.h2.result.SearchRow;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueNull;
/*     */ import org.h2.value.ValueVarchar;
/*     */ import org.h2.value.ValueVarcharIgnoreCase;
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
/*     */ 
/*     */ 
/*     */ public abstract class MetaTable
/*     */   extends Table
/*     */ {
/*     */   public static final long ROW_COUNT_APPROXIMATION = 1000L;
/*     */   protected final int type;
/*     */   protected int indexColumn;
/*     */   protected MetaIndex metaIndex;
/*     */   
/*     */   protected MetaTable(Schema paramSchema, int paramInt1, int paramInt2) {
/*  59 */     super(paramSchema, paramInt1, (String)null, true, true);
/*  60 */     this.type = paramInt2;
/*     */   }
/*     */   
/*     */   protected final void setMetaTableName(String paramString) {
/*  64 */     setObjectName(this.database.sysIdentifier(paramString));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final Column column(String paramString) {
/*  75 */     return new Column(this.database.sysIdentifier(paramString), 
/*  76 */         (this.database.getSettings()).caseInsensitiveIdentifiers ? TypeInfo.TYPE_VARCHAR_IGNORECASE : TypeInfo.TYPE_VARCHAR);
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
/*     */   protected final Column column(String paramString, TypeInfo paramTypeInfo) {
/*  90 */     return new Column(this.database.sysIdentifier(paramString), paramTypeInfo);
/*     */   }
/*     */ 
/*     */   
/*     */   public final String getCreateSQL() {
/*  95 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final Index addIndex(SessionLocal paramSessionLocal, String paramString1, int paramInt1, IndexColumn[] paramArrayOfIndexColumn, int paramInt2, IndexType paramIndexType, boolean paramBoolean, String paramString2) {
/* 101 */     throw DbException.getUnsupportedException("META");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final String identifier(String paramString) {
/* 111 */     if ((this.database.getSettings()).databaseToLower) {
/* 112 */       paramString = (paramString == null) ? null : StringUtils.toLowerEnglish(paramString);
/*     */     }
/* 114 */     return paramString;
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
/*     */   protected final boolean checkIndex(SessionLocal paramSessionLocal, String paramString, Value paramValue1, Value paramValue2) {
/*     */     Value value;
/* 127 */     if (paramString == null || (paramValue1 == null && paramValue2 == null)) {
/* 128 */       return true;
/*     */     }
/*     */     
/* 131 */     if ((this.database.getSettings()).caseInsensitiveIdentifiers) {
/* 132 */       ValueVarcharIgnoreCase valueVarcharIgnoreCase = ValueVarcharIgnoreCase.get(paramString);
/*     */     } else {
/* 134 */       value = ValueVarchar.get(paramString);
/*     */     } 
/* 136 */     if (paramValue1 != null && paramSessionLocal.compare(value, paramValue1) < 0) {
/* 137 */       return false;
/*     */     }
/* 139 */     if (paramValue2 != null && paramSessionLocal.compare(value, paramValue2) > 0) {
/* 140 */       return false;
/*     */     }
/* 142 */     return true;
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
/*     */   protected final boolean hideTable(Table paramTable, SessionLocal paramSessionLocal) {
/* 154 */     return (paramTable.isHidden() && paramSessionLocal != this.database.getSystemSession());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract ArrayList<Row> generateRows(SessionLocal paramSessionLocal, SearchRow paramSearchRow1, SearchRow paramSearchRow2);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isInsertable() {
/* 170 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public final void removeRow(SessionLocal paramSessionLocal, Row paramRow) {
/* 175 */     throw DbException.getUnsupportedException("META");
/*     */   }
/*     */ 
/*     */   
/*     */   public final void addRow(SessionLocal paramSessionLocal, Row paramRow) {
/* 180 */     throw DbException.getUnsupportedException("META");
/*     */   }
/*     */ 
/*     */   
/*     */   public final void removeChildrenAndResources(SessionLocal paramSessionLocal) {
/* 185 */     throw DbException.getUnsupportedException("META");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void close(SessionLocal paramSessionLocal) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void add(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, Object... paramVarArgs) {
/* 201 */     Value[] arrayOfValue = new Value[paramVarArgs.length];
/* 202 */     for (byte b = 0; b < paramVarArgs.length; b++) {
/* 203 */       Object object = paramVarArgs[b];
/* 204 */       Value value = (Value)((object == null) ? ValueNull.INSTANCE : ((object instanceof String) ? ValueVarchar.get((String)object) : object));
/* 205 */       arrayOfValue[b] = this.columns[b].convert((CastDataProvider)paramSessionLocal, value);
/*     */     } 
/* 207 */     paramArrayList.add(Row.get(arrayOfValue, 1, paramArrayList.size()));
/*     */   }
/*     */ 
/*     */   
/*     */   public final void checkRename() {
/* 212 */     throw DbException.getUnsupportedException("META");
/*     */   }
/*     */ 
/*     */   
/*     */   public final void checkSupportAlter() {
/* 217 */     throw DbException.getUnsupportedException("META");
/*     */   }
/*     */ 
/*     */   
/*     */   public final long truncate(SessionLocal paramSessionLocal) {
/* 222 */     throw DbException.getUnsupportedException("META");
/*     */   }
/*     */ 
/*     */   
/*     */   public long getRowCount(SessionLocal paramSessionLocal) {
/* 227 */     throw DbException.getInternalError(toString());
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canGetRowCount(SessionLocal paramSessionLocal) {
/* 232 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean canDrop() {
/* 237 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public final TableType getTableType() {
/* 242 */     return TableType.SYSTEM_TABLE;
/*     */   }
/*     */ 
/*     */   
/*     */   public final Index getScanIndex(SessionLocal paramSessionLocal) {
/* 247 */     return (Index)new MetaIndex(this, IndexColumn.wrap(this.columns), true);
/*     */   }
/*     */ 
/*     */   
/*     */   public final ArrayList<Index> getIndexes() {
/* 252 */     ArrayList<Index> arrayList = new ArrayList(2);
/* 253 */     if (this.metaIndex == null) {
/* 254 */       return arrayList;
/*     */     }
/* 256 */     arrayList.add(new MetaIndex(this, IndexColumn.wrap(this.columns), true));
/*     */     
/* 258 */     arrayList.add(this.metaIndex);
/* 259 */     return arrayList;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getRowCountApproximation(SessionLocal paramSessionLocal) {
/* 264 */     return 1000L;
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean isDeterministic() {
/* 269 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean canReference() {
/* 274 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\table\MetaTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */