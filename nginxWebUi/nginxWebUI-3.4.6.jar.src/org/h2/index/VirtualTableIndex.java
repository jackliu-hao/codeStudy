/*    */ package org.h2.index;
/*    */ 
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.result.Row;
/*    */ import org.h2.table.IndexColumn;
/*    */ import org.h2.table.Table;
/*    */ import org.h2.table.VirtualTable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class VirtualTableIndex
/*    */   extends Index
/*    */ {
/*    */   protected VirtualTableIndex(VirtualTable paramVirtualTable, String paramString, IndexColumn[] paramArrayOfIndexColumn) {
/* 20 */     super((Table)paramVirtualTable, 0, paramString, paramArrayOfIndexColumn, 0, IndexType.createNonUnique(true));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void close(SessionLocal paramSessionLocal) {}
/*    */ 
/*    */ 
/*    */   
/*    */   public void add(SessionLocal paramSessionLocal, Row paramRow) {
/* 30 */     throw DbException.getUnsupportedException("Virtual table");
/*    */   }
/*    */ 
/*    */   
/*    */   public void remove(SessionLocal paramSessionLocal, Row paramRow) {
/* 35 */     throw DbException.getUnsupportedException("Virtual table");
/*    */   }
/*    */ 
/*    */   
/*    */   public void remove(SessionLocal paramSessionLocal) {
/* 40 */     throw DbException.getUnsupportedException("Virtual table");
/*    */   }
/*    */ 
/*    */   
/*    */   public void truncate(SessionLocal paramSessionLocal) {
/* 45 */     throw DbException.getUnsupportedException("Virtual table");
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean needRebuild() {
/* 50 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public void checkRename() {
/* 55 */     throw DbException.getUnsupportedException("Virtual table");
/*    */   }
/*    */ 
/*    */   
/*    */   public long getRowCount(SessionLocal paramSessionLocal) {
/* 60 */     return this.table.getRowCount(paramSessionLocal);
/*    */   }
/*    */ 
/*    */   
/*    */   public long getRowCountApproximation(SessionLocal paramSessionLocal) {
/* 65 */     return this.table.getRowCountApproximation(paramSessionLocal);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\index\VirtualTableIndex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */