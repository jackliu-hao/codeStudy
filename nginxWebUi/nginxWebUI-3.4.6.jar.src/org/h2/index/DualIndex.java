/*    */ package org.h2.index;
/*    */ 
/*    */ import org.h2.command.query.AllColumnsForPlan;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.result.Row;
/*    */ import org.h2.result.SearchRow;
/*    */ import org.h2.result.SortOrder;
/*    */ import org.h2.table.DualTable;
/*    */ import org.h2.table.TableFilter;
/*    */ import org.h2.table.VirtualTable;
/*    */ import org.h2.value.Value;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DualIndex
/*    */   extends VirtualTableIndex
/*    */ {
/*    */   public DualIndex(DualTable paramDualTable) {
/* 24 */     super((VirtualTable)paramDualTable, "DUAL_INDEX", new org.h2.table.IndexColumn[0]);
/*    */   }
/*    */ 
/*    */   
/*    */   public Cursor find(SessionLocal paramSessionLocal, SearchRow paramSearchRow1, SearchRow paramSearchRow2) {
/* 29 */     return new DualCursor();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public double getCost(SessionLocal paramSessionLocal, int[] paramArrayOfint, TableFilter[] paramArrayOfTableFilter, int paramInt, SortOrder paramSortOrder, AllColumnsForPlan paramAllColumnsForPlan) {
/* 35 */     return 1.0D;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCreateSQL() {
/* 40 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean canGetFirstOrLast() {
/* 45 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public Cursor findFirstOrLast(SessionLocal paramSessionLocal, boolean paramBoolean) {
/* 50 */     return new SingleRowCursor(Row.get(Value.EMPTY_VALUES, 1));
/*    */   }
/*    */ 
/*    */   
/*    */   public String getPlanSQL() {
/* 55 */     return "dual index";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\index\DualIndex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */