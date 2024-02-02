/*    */ package org.h2.value.lob;
/*    */ 
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.InputStream;
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
/*    */ public final class LobDataInMemory
/*    */   extends LobData
/*    */ {
/*    */   private final byte[] small;
/*    */   
/*    */   public LobDataInMemory(byte[] paramArrayOfbyte) {
/* 22 */     if (paramArrayOfbyte == null) {
/* 23 */       throw new IllegalStateException();
/*    */     }
/* 25 */     this.small = paramArrayOfbyte;
/*    */   }
/*    */ 
/*    */   
/*    */   public InputStream getInputStream(long paramLong) {
/* 30 */     return new ByteArrayInputStream(this.small);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public byte[] getSmall() {
/* 39 */     return this.small;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getMemory() {
/* 48 */     return this.small.length + 127;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\lob\LobDataInMemory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */