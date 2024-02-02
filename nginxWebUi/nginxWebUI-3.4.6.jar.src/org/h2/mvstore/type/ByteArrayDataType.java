/*    */ package org.h2.mvstore.type;
/*    */ 
/*    */ import java.nio.ByteBuffer;
/*    */ import org.h2.mvstore.DataUtils;
/*    */ import org.h2.mvstore.WriteBuffer;
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
/*    */ public final class ByteArrayDataType
/*    */   extends BasicDataType<byte[]>
/*    */ {
/* 19 */   public static final ByteArrayDataType INSTANCE = new ByteArrayDataType();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getMemory(byte[] paramArrayOfbyte) {
/* 25 */     return paramArrayOfbyte.length;
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(WriteBuffer paramWriteBuffer, byte[] paramArrayOfbyte) {
/* 30 */     paramWriteBuffer.putVarInt(paramArrayOfbyte.length);
/* 31 */     paramWriteBuffer.put(paramArrayOfbyte);
/*    */   }
/*    */ 
/*    */   
/*    */   public byte[] read(ByteBuffer paramByteBuffer) {
/* 36 */     int i = DataUtils.readVarInt(paramByteBuffer);
/* 37 */     byte[] arrayOfByte = new byte[i];
/* 38 */     paramByteBuffer.get(arrayOfByte);
/* 39 */     return arrayOfByte;
/*    */   }
/*    */ 
/*    */   
/*    */   public byte[][] createStorage(int paramInt) {
/* 44 */     return new byte[paramInt][];
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\type\ByteArrayDataType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */