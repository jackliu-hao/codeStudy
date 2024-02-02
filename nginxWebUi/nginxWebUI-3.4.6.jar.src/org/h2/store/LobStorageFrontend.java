/*    */ package org.h2.store;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.Reader;
/*    */ import org.h2.engine.SessionRemote;
/*    */ import org.h2.value.ValueBlob;
/*    */ import org.h2.value.ValueClob;
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
/*    */ 
/*    */ 
/*    */ public class LobStorageFrontend
/*    */   implements LobStorageInterface
/*    */ {
/*    */   public static final int TABLE_ID_SESSION_VARIABLE = -1;
/*    */   public static final int TABLE_TEMP = -2;
/*    */   public static final int TABLE_RESULT = -3;
/*    */   private final SessionRemote sessionRemote;
/*    */   
/*    */   public LobStorageFrontend(SessionRemote paramSessionRemote) {
/* 40 */     this.sessionRemote = paramSessionRemote;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void removeLob(ValueLob paramValueLob) {}
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public InputStream getInputStream(long paramLong1, long paramLong2) throws IOException {
/* 52 */     throw new IllegalStateException();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public InputStream getInputStream(long paramLong1, int paramInt, long paramLong2) throws IOException {
/* 59 */     throw new IllegalStateException();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isReadOnly() {
/* 64 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public ValueLob copyLob(ValueLob paramValueLob, int paramInt) {
/* 69 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ 
/*    */   
/*    */   public void removeAllForTable(int paramInt) {
/* 74 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ValueBlob createBlob(InputStream paramInputStream, long paramLong) {
/* 82 */     return ValueBlob.createTempBlob(paramInputStream, paramLong, (DataHandler)this.sessionRemote);
/*    */   }
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
/*    */   public ValueClob createClob(Reader paramReader, long paramLong) {
/* 97 */     return ValueClob.createTempClob(paramReader, paramLong, (DataHandler)this.sessionRemote);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\LobStorageFrontend.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */