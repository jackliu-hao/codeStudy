/*     */ package org.h2.index;
/*     */ 
/*     */ import org.h2.command.query.AllColumnsForPlan;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.Row;
/*     */ import org.h2.result.SearchRow;
/*     */ import org.h2.result.SortOrder;
/*     */ import org.h2.table.IndexColumn;
/*     */ import org.h2.table.RangeTable;
/*     */ import org.h2.table.TableFilter;
/*     */ import org.h2.table.VirtualTable;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueBigint;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RangeIndex
/*     */   extends VirtualTableIndex
/*     */ {
/*     */   private final RangeTable rangeTable;
/*     */   
/*     */   public RangeIndex(RangeTable paramRangeTable, IndexColumn[] paramArrayOfIndexColumn) {
/*  30 */     super((VirtualTable)paramRangeTable, "RANGE_INDEX", paramArrayOfIndexColumn);
/*  31 */     this.rangeTable = paramRangeTable;
/*     */   }
/*     */ 
/*     */   
/*     */   public Cursor find(SessionLocal paramSessionLocal, SearchRow paramSearchRow1, SearchRow paramSearchRow2) {
/*  36 */     long l1 = this.rangeTable.getMin(paramSessionLocal);
/*  37 */     long l2 = this.rangeTable.getMax(paramSessionLocal);
/*  38 */     long l3 = this.rangeTable.getStep(paramSessionLocal);
/*  39 */     if (l3 == 0L) {
/*  40 */       throw DbException.get(90142);
/*     */     }
/*  42 */     if (paramSearchRow1 != null) {
/*     */       try {
/*  44 */         long l = paramSearchRow1.getValue(0).getLong();
/*  45 */         if (l3 > 0L) {
/*  46 */           if (l > l1) {
/*  47 */             l1 += (l - l1 + l3 - 1L) / l3 * l3;
/*     */           }
/*  49 */         } else if (l > l2) {
/*  50 */           l2 = l;
/*     */         } 
/*  52 */       } catch (DbException dbException) {}
/*     */     }
/*     */ 
/*     */     
/*  56 */     if (paramSearchRow2 != null) {
/*     */       try {
/*  58 */         long l = paramSearchRow2.getValue(0).getLong();
/*  59 */         if (l3 > 0L) {
/*  60 */           if (l < l2) {
/*  61 */             l2 = l;
/*     */           }
/*  63 */         } else if (l < l1) {
/*  64 */           l1 -= (l1 - l - l3 - 1L) / l3 * l3;
/*     */         } 
/*  66 */       } catch (DbException dbException) {}
/*     */     }
/*     */ 
/*     */     
/*  70 */     return new RangeCursor(l1, l2, l3);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getCost(SessionLocal paramSessionLocal, int[] paramArrayOfint, TableFilter[] paramArrayOfTableFilter, int paramInt, SortOrder paramSortOrder, AllColumnsForPlan paramAllColumnsForPlan) {
/*  77 */     return 1.0D;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQL() {
/*  82 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canGetFirstOrLast() {
/*  87 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public Cursor findFirstOrLast(SessionLocal paramSessionLocal, boolean paramBoolean) {
/*  92 */     long l1 = this.rangeTable.getMin(paramSessionLocal);
/*  93 */     long l2 = this.rangeTable.getMax(paramSessionLocal);
/*  94 */     long l3 = this.rangeTable.getStep(paramSessionLocal);
/*  95 */     if (l3 == 0L) {
/*  96 */       throw DbException.get(90142);
/*     */     }
/*  98 */     return new SingleRowCursor(((l3 > 0L) ? (l1 <= l2) : (l1 >= l2)) ? 
/*  99 */         Row.get(new Value[] { (Value)ValueBigint.get(((paramBoolean ^ ((l1 >= l2) ? 1 : 0)) != 0) ? l1 : l2) }, 1) : null);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getPlanSQL() {
/* 104 */     return "range index";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\index\RangeIndex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */