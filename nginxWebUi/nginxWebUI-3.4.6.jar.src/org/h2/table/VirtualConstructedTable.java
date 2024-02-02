/*    */ package org.h2.table;
/*    */ 
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.index.Index;
/*    */ import org.h2.index.VirtualConstructedTableIndex;
/*    */ import org.h2.result.ResultInterface;
/*    */ import org.h2.schema.Schema;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class VirtualConstructedTable
/*    */   extends VirtualTable
/*    */ {
/*    */   protected VirtualConstructedTable(Schema paramSchema, int paramInt, String paramString) {
/* 20 */     super(paramSchema, paramInt, paramString);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public abstract ResultInterface getResult(SessionLocal paramSessionLocal);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Index getScanIndex(SessionLocal paramSessionLocal) {
/* 34 */     return (Index)new VirtualConstructedTableIndex(this, IndexColumn.wrap(this.columns));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public long getMaxDataModificationId() {
/* 41 */     return Long.MAX_VALUE;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\table\VirtualConstructedTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */