/*    */ package org.h2.value.lob;
/*    */ 
/*    */ import java.io.BufferedInputStream;
/*    */ import java.io.InputStream;
/*    */ import org.h2.engine.SessionRemote;
/*    */ import org.h2.store.DataHandler;
/*    */ import org.h2.store.LobStorageRemoteInputStream;
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
/*    */ public final class LobDataFetchOnDemand
/*    */   extends LobData
/*    */ {
/*    */   private SessionRemote handler;
/*    */   private final int tableId;
/*    */   private final long lobId;
/*    */   protected final byte[] hmac;
/*    */   
/*    */   public LobDataFetchOnDemand(DataHandler paramDataHandler, int paramInt, long paramLong, byte[] paramArrayOfbyte) {
/* 39 */     this.hmac = paramArrayOfbyte;
/* 40 */     this.handler = (SessionRemote)paramDataHandler;
/* 41 */     this.tableId = paramInt;
/* 42 */     this.lobId = paramLong;
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
/* 53 */     throw new IllegalStateException();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getTableId() {
/* 62 */     return this.tableId;
/*    */   }
/*    */   
/*    */   public long getLobId() {
/* 66 */     return this.lobId;
/*    */   }
/*    */ 
/*    */   
/*    */   public InputStream getInputStream(long paramLong) {
/* 71 */     return new BufferedInputStream((InputStream)new LobStorageRemoteInputStream(this.handler, this.lobId, this.hmac));
/*    */   }
/*    */ 
/*    */   
/*    */   public DataHandler getDataHandler() {
/* 76 */     return (DataHandler)this.handler;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 81 */     return "lob-table: table: " + this.tableId + " id: " + this.lobId;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\lob\LobDataFetchOnDemand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */