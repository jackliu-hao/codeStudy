/*     */ package org.h2.mvstore.db;
/*     */ 
/*     */ import java.util.List;
/*     */ import org.h2.command.query.AllColumnsForPlan;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.index.Cursor;
/*     */ import org.h2.index.IndexType;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.mvstore.MVMap;
/*     */ import org.h2.result.Row;
/*     */ import org.h2.result.RowFactory;
/*     */ import org.h2.result.SearchRow;
/*     */ import org.h2.result.SortOrder;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.IndexColumn;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.table.TableFilter;
/*     */ import org.h2.value.VersionedValue;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MVDelegateIndex
/*     */   extends MVIndex<Long, SearchRow>
/*     */ {
/*     */   private final MVPrimaryIndex mainIndex;
/*     */   
/*     */   public MVDelegateIndex(MVTable paramMVTable, int paramInt, String paramString, MVPrimaryIndex paramMVPrimaryIndex, IndexType paramIndexType) {
/*  33 */     super((Table)paramMVTable, paramInt, paramString, IndexColumn.wrap(new Column[] { paramMVTable.getColumn(paramMVPrimaryIndex.getMainIndexColumn()) }, ), 1, paramIndexType);
/*     */     
/*  35 */     this.mainIndex = paramMVPrimaryIndex;
/*  36 */     if (paramInt < 0) {
/*  37 */       throw DbException.getInternalError(paramString);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public RowFactory getRowFactory() {
/*  43 */     return this.mainIndex.getRowFactory();
/*     */   }
/*     */ 
/*     */   
/*     */   public void addRowsToBuffer(List<Row> paramList, String paramString) {
/*  48 */     throw DbException.getInternalError();
/*     */   }
/*     */ 
/*     */   
/*     */   public void addBufferedRows(List<String> paramList) {
/*  53 */     throw DbException.getInternalError();
/*     */   }
/*     */ 
/*     */   
/*     */   public MVMap<Long, VersionedValue<SearchRow>> getMVMap() {
/*  58 */     return this.mainIndex.getMVMap();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void add(SessionLocal paramSessionLocal, Row paramRow) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public Row getRow(SessionLocal paramSessionLocal, long paramLong) {
/*  68 */     return this.mainIndex.getRow(paramSessionLocal, paramLong);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isRowIdIndex() {
/*  73 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canGetFirstOrLast() {
/*  78 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close(SessionLocal paramSessionLocal) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public Cursor find(SessionLocal paramSessionLocal, SearchRow paramSearchRow1, SearchRow paramSearchRow2) {
/*  88 */     return this.mainIndex.find(paramSessionLocal, paramSearchRow1, paramSearchRow2);
/*     */   }
/*     */ 
/*     */   
/*     */   public Cursor findFirstOrLast(SessionLocal paramSessionLocal, boolean paramBoolean) {
/*  93 */     return this.mainIndex.findFirstOrLast(paramSessionLocal, paramBoolean);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getColumnIndex(Column paramColumn) {
/*  98 */     if (paramColumn.getColumnId() == this.mainIndex.getMainIndexColumn()) {
/*  99 */       return 0;
/*     */     }
/* 101 */     return -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isFirstColumn(Column paramColumn) {
/* 106 */     return (getColumnIndex(paramColumn) == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getCost(SessionLocal paramSessionLocal, int[] paramArrayOfint, TableFilter[] paramArrayOfTableFilter, int paramInt, SortOrder paramSortOrder, AllColumnsForPlan paramAllColumnsForPlan) {
/* 113 */     return (10L * getCostRangeIndex(paramArrayOfint, this.mainIndex.getRowCountApproximation(paramSessionLocal), paramArrayOfTableFilter, paramInt, paramSortOrder, true, paramAllColumnsForPlan));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean needRebuild() {
/* 119 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void remove(SessionLocal paramSessionLocal, Row paramRow) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void update(SessionLocal paramSessionLocal, Row paramRow1, Row paramRow2) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void remove(SessionLocal paramSessionLocal) {
/* 134 */     this.mainIndex.setMainIndexColumn(-1);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void truncate(SessionLocal paramSessionLocal) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public long getRowCount(SessionLocal paramSessionLocal) {
/* 144 */     return this.mainIndex.getRowCount(paramSessionLocal);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getRowCountApproximation(SessionLocal paramSessionLocal) {
/* 149 */     return this.mainIndex.getRowCountApproximation(paramSessionLocal);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\db\MVDelegateIndex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */