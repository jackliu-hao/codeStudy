/*     */ package org.h2.index;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import org.h2.command.query.AllColumnsForPlan;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.Row;
/*     */ import org.h2.result.SearchRow;
/*     */ import org.h2.result.SortOrder;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.IndexColumn;
/*     */ import org.h2.table.MetaTable;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.table.TableFilter;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MetaIndex
/*     */   extends Index
/*     */ {
/*     */   private final MetaTable meta;
/*     */   private final boolean scan;
/*     */   
/*     */   public MetaIndex(MetaTable paramMetaTable, IndexColumn[] paramArrayOfIndexColumn, boolean paramBoolean) {
/*  30 */     super((Table)paramMetaTable, 0, null, paramArrayOfIndexColumn, 0, IndexType.createNonUnique(true));
/*  31 */     this.meta = paramMetaTable;
/*  32 */     this.scan = paramBoolean;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close(SessionLocal paramSessionLocal) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void add(SessionLocal paramSessionLocal, Row paramRow) {
/*  42 */     throw DbException.getUnsupportedException("META");
/*     */   }
/*     */ 
/*     */   
/*     */   public void remove(SessionLocal paramSessionLocal, Row paramRow) {
/*  47 */     throw DbException.getUnsupportedException("META");
/*     */   }
/*     */ 
/*     */   
/*     */   public Cursor find(SessionLocal paramSessionLocal, SearchRow paramSearchRow1, SearchRow paramSearchRow2) {
/*  52 */     ArrayList<Row> arrayList = this.meta.generateRows(paramSessionLocal, paramSearchRow1, paramSearchRow2);
/*  53 */     return new MetaCursor(arrayList);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getCost(SessionLocal paramSessionLocal, int[] paramArrayOfint, TableFilter[] paramArrayOfTableFilter, int paramInt, SortOrder paramSortOrder, AllColumnsForPlan paramAllColumnsForPlan) {
/*  60 */     if (this.scan) {
/*  61 */       return 10000.0D;
/*     */     }
/*  63 */     return getCostRangeIndex(paramArrayOfint, 1000L, paramArrayOfTableFilter, paramInt, paramSortOrder, false, paramAllColumnsForPlan);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void truncate(SessionLocal paramSessionLocal) {
/*  69 */     throw DbException.getUnsupportedException("META");
/*     */   }
/*     */ 
/*     */   
/*     */   public void remove(SessionLocal paramSessionLocal) {
/*  74 */     throw DbException.getUnsupportedException("META");
/*     */   }
/*     */ 
/*     */   
/*     */   public int getColumnIndex(Column paramColumn) {
/*  79 */     if (this.scan)
/*     */     {
/*  81 */       return -1;
/*     */     }
/*  83 */     return super.getColumnIndex(paramColumn);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isFirstColumn(Column paramColumn) {
/*  88 */     if (this.scan) {
/*  89 */       return false;
/*     */     }
/*  91 */     return super.isFirstColumn(paramColumn);
/*     */   }
/*     */ 
/*     */   
/*     */   public void checkRename() {
/*  96 */     throw DbException.getUnsupportedException("META");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needRebuild() {
/* 101 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQL() {
/* 106 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getRowCount(SessionLocal paramSessionLocal) {
/* 111 */     return 1000L;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getRowCountApproximation(SessionLocal paramSessionLocal) {
/* 116 */     return 1000L;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getDiskSpaceUsed() {
/* 121 */     return this.meta.getDiskSpaceUsed();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getPlanSQL() {
/* 126 */     return "meta";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\index\MetaIndex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */