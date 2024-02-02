/*    */ package org.h2.value.lob;
/*    */ 
/*    */ import java.io.InputStream;
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
/*    */ public abstract class LobData
/*    */ {
/*    */   public abstract InputStream getInputStream(long paramLong);
/*    */   
/*    */   public DataHandler getDataHandler() {
/* 29 */     return null;
/*    */   }
/*    */   
/*    */   public boolean isLinkedToTable() {
/* 33 */     return false;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void remove(ValueLob paramValueLob) {}
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getMemory() {
/* 50 */     return 140;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\lob\LobData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */