/*    */ package org.h2.store;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import org.h2.engine.SessionRemote;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.mvstore.DataUtils;
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
/*    */ public class LobStorageRemoteInputStream
/*    */   extends InputStream
/*    */ {
/*    */   private final SessionRemote sessionRemote;
/*    */   private final long lobId;
/*    */   private final byte[] hmac;
/*    */   private long pos;
/*    */   
/*    */   public LobStorageRemoteInputStream(SessionRemote paramSessionRemote, long paramLong, byte[] paramArrayOfbyte) {
/* 35 */     this.sessionRemote = paramSessionRemote;
/* 36 */     this.lobId = paramLong;
/* 37 */     this.hmac = paramArrayOfbyte;
/*    */   }
/*    */ 
/*    */   
/*    */   public int read() throws IOException {
/* 42 */     byte[] arrayOfByte = new byte[1];
/* 43 */     int i = read(arrayOfByte, 0, 1);
/* 44 */     return (i < 0) ? i : (arrayOfByte[0] & 0xFF);
/*    */   }
/*    */ 
/*    */   
/*    */   public int read(byte[] paramArrayOfbyte) throws IOException {
/* 49 */     return read(paramArrayOfbyte, 0, paramArrayOfbyte.length);
/*    */   }
/*    */ 
/*    */   
/*    */   public int read(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
/* 54 */     assert paramInt2 >= 0;
/* 55 */     if (paramInt2 == 0) {
/* 56 */       return 0;
/*    */     }
/*    */     try {
/* 59 */       paramInt2 = this.sessionRemote.readLob(this.lobId, this.hmac, this.pos, paramArrayOfbyte, paramInt1, paramInt2);
/* 60 */     } catch (DbException dbException) {
/* 61 */       throw DataUtils.convertToIOException(dbException);
/*    */     } 
/* 63 */     if (paramInt2 == 0) {
/* 64 */       return -1;
/*    */     }
/* 66 */     this.pos += paramInt2;
/* 67 */     return paramInt2;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\LobStorageRemoteInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */