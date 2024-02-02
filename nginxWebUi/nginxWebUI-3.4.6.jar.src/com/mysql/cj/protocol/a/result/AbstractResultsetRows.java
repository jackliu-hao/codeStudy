/*    */ package com.mysql.cj.protocol.a.result;
/*    */ 
/*    */ import com.mysql.cj.protocol.ColumnDefinition;
/*    */ import com.mysql.cj.protocol.ProtocolEntityFactory;
/*    */ import com.mysql.cj.protocol.ResultsetRow;
/*    */ import com.mysql.cj.protocol.ResultsetRows;
/*    */ import com.mysql.cj.protocol.ResultsetRowsOwner;
/*    */ import com.mysql.cj.protocol.a.NativePacketPayload;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class AbstractResultsetRows
/*    */   implements ResultsetRows
/*    */ {
/*    */   protected static final int BEFORE_START_OF_ROWS = -1;
/*    */   protected ColumnDefinition metadata;
/* 54 */   protected int currentPositionInFetchedRows = -1;
/*    */ 
/*    */   
/*    */   protected boolean wasEmpty = false;
/*    */ 
/*    */   
/*    */   protected ResultsetRowsOwner owner;
/*    */ 
/*    */   
/*    */   protected ProtocolEntityFactory<ResultsetRow, NativePacketPayload> rowFactory;
/*    */ 
/*    */   
/*    */   public void setOwner(ResultsetRowsOwner rs) {
/* 67 */     this.owner = rs;
/*    */   }
/*    */ 
/*    */   
/*    */   public ResultsetRowsOwner getOwner() {
/* 72 */     return this.owner;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setMetadata(ColumnDefinition columnDefinition) {
/* 77 */     this.metadata = columnDefinition;
/*    */   }
/*    */   
/*    */   public ColumnDefinition getMetadata() {
/* 81 */     return this.metadata;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean wasEmpty() {
/* 86 */     return this.wasEmpty;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\result\AbstractResultsetRows.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */