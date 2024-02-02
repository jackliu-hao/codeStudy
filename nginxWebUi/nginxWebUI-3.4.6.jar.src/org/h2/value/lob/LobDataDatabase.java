/*    */ package org.h2.value.lob;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.store.DataHandler;
/*    */ import org.h2.value.ValueLob;
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
/*    */ public final class LobDataDatabase
/*    */   extends LobData
/*    */ {
/*    */   private DataHandler handler;
/*    */   private final int tableId;
/*    */   private final long lobId;
/*    */   private boolean isRecoveryReference;
/*    */   
/*    */   public LobDataDatabase(DataHandler paramDataHandler, int paramInt, long paramLong) {
/* 36 */     this.handler = paramDataHandler;
/* 37 */     this.tableId = paramInt;
/* 38 */     this.lobId = paramLong;
/*    */   }
/*    */ 
/*    */   
/*    */   public void remove(ValueLob paramValueLob) {
/* 43 */     if (this.handler != null) {
/* 44 */       this.handler.getLobStorage().removeLob(paramValueLob);
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isLinkedToTable() {
/* 56 */     return (this.tableId >= 0);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getTableId() {
/* 65 */     return this.tableId;
/*    */   }
/*    */   
/*    */   public long getLobId() {
/* 69 */     return this.lobId;
/*    */   }
/*    */ 
/*    */   
/*    */   public InputStream getInputStream(long paramLong) {
/*    */     try {
/* 75 */       return this.handler.getLobStorage().getInputStream(this.lobId, this.tableId, paramLong);
/* 76 */     } catch (IOException iOException) {
/* 77 */       throw DbException.convertIOException(iOException, toString());
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public DataHandler getDataHandler() {
/* 83 */     return this.handler;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 88 */     return "lob-table: table: " + this.tableId + " id: " + this.lobId;
/*    */   }
/*    */   
/*    */   public void setRecoveryReference(boolean paramBoolean) {
/* 92 */     this.isRecoveryReference = paramBoolean;
/*    */   }
/*    */   
/*    */   public boolean isRecoveryReference() {
/* 96 */     return this.isRecoveryReference;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\lob\LobDataDatabase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */