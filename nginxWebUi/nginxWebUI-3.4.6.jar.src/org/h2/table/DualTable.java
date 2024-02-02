/*    */ package org.h2.table;
/*    */ 
/*    */ import org.h2.engine.Database;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.index.DualIndex;
/*    */ import org.h2.index.Index;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DualTable
/*    */   extends VirtualTable
/*    */ {
/*    */   public static final String NAME = "DUAL";
/*    */   
/*    */   public DualTable(Database paramDatabase) {
/* 30 */     super(paramDatabase.getMainSchema(), 0, "DUAL");
/* 31 */     setColumns(new Column[0]);
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 36 */     return paramStringBuilder.append("DUAL");
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean canGetRowCount(SessionLocal paramSessionLocal) {
/* 41 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public long getRowCount(SessionLocal paramSessionLocal) {
/* 46 */     return 1L;
/*    */   }
/*    */ 
/*    */   
/*    */   public TableType getTableType() {
/* 51 */     return TableType.SYSTEM_TABLE;
/*    */   }
/*    */ 
/*    */   
/*    */   public Index getScanIndex(SessionLocal paramSessionLocal) {
/* 56 */     return (Index)new DualIndex(this);
/*    */   }
/*    */ 
/*    */   
/*    */   public long getMaxDataModificationId() {
/* 61 */     return 0L;
/*    */   }
/*    */ 
/*    */   
/*    */   public long getRowCountApproximation(SessionLocal paramSessionLocal) {
/* 66 */     return 1L;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isDeterministic() {
/* 71 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\table\DualTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */