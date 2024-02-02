/*    */ package org.h2.index;
/*    */ 
/*    */ import org.h2.command.query.AllColumnsForPlan;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.result.SearchRow;
/*    */ import org.h2.result.SortOrder;
/*    */ import org.h2.table.IndexColumn;
/*    */ import org.h2.table.TableFilter;
/*    */ import org.h2.table.VirtualConstructedTable;
/*    */ import org.h2.table.VirtualTable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class VirtualConstructedTableIndex
/*    */   extends VirtualTableIndex
/*    */ {
/*    */   private final VirtualConstructedTable table;
/*    */   
/*    */   public VirtualConstructedTableIndex(VirtualConstructedTable paramVirtualConstructedTable, IndexColumn[] paramArrayOfIndexColumn) {
/* 27 */     super((VirtualTable)paramVirtualConstructedTable, (String)null, paramArrayOfIndexColumn);
/* 28 */     this.table = paramVirtualConstructedTable;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isFindUsingFullTableScan() {
/* 33 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public Cursor find(SessionLocal paramSessionLocal, SearchRow paramSearchRow1, SearchRow paramSearchRow2) {
/* 38 */     return new VirtualTableCursor(this, paramSearchRow1, paramSearchRow2, this.table.getResult(paramSessionLocal));
/*    */   }
/*    */ 
/*    */   
/*    */   public double getCost(SessionLocal paramSessionLocal, int[] paramArrayOfint, TableFilter[] paramArrayOfTableFilter, int paramInt, SortOrder paramSortOrder, AllColumnsForPlan paramAllColumnsForPlan) {
/*    */     long l;
/* 44 */     if (paramArrayOfint != null) {
/* 45 */       throw DbException.getUnsupportedException("Virtual table");
/*    */     }
/*    */     
/* 48 */     if (this.table.canGetRowCount(paramSessionLocal)) {
/* 49 */       l = this.table.getRowCountApproximation(paramSessionLocal);
/*    */     } else {
/* 51 */       l = (this.database.getSettings()).estimatedFunctionTableRows;
/*    */     } 
/* 53 */     return (l * 10L);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getPlanSQL() {
/* 58 */     return (this.table instanceof org.h2.table.FunctionTable) ? "function" : "table scan";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean canScan() {
/* 63 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\index\VirtualConstructedTableIndex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */