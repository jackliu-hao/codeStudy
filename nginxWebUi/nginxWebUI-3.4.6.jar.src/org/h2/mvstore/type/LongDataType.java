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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LongDataType
/*    */   extends BasicDataType<Long>
/*    */ {
/* 22 */   public static final LongDataType INSTANCE = new LongDataType();
/*    */   
/* 24 */   private static final Long[] EMPTY_LONG_ARR = new Long[0];
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getMemory(Long paramLong) {
/* 30 */     return 8;
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(WriteBuffer paramWriteBuffer, Long paramLong) {
/* 35 */     paramWriteBuffer.putVarLong(paramLong.longValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public Long read(ByteBuffer paramByteBuffer) {
/* 40 */     return Long.valueOf(DataUtils.readVarLong(paramByteBuffer));
/*    */   }
/*    */ 
/*    */   
/*    */   public Long[] createStorage(int paramInt) {
/* 45 */     return (paramInt == 0) ? EMPTY_LONG_ARR : new Long[paramInt];
/*    */   }
/*    */ 
/*    */   
/*    */   public int compare(Long paramLong1, Long paramLong2) {
/* 50 */     return Long.compare(paramLong1.longValue(), paramLong2.longValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public int binarySearch(Long paramLong, Object paramObject, int paramInt1, int paramInt2) {
/* 55 */     long l = paramLong.longValue();
/* 56 */     Long[] arrayOfLong = cast(paramObject);
/* 57 */     boolean bool = false;
/* 58 */     int i = paramInt1 - 1;
/*    */ 
/*    */ 
/*    */     
/* 62 */     int j = paramInt2 - 1;
/* 63 */     if (j < 0 || j > i) {
/* 64 */       j = i >>> 1;
/*    */     }
/* 66 */     return binarySearch(l, arrayOfLong, bool, i, j);
/*    */   }
/*    */   
/*    */   private static int binarySearch(long paramLong, Long[] paramArrayOfLong, int paramInt1, int paramInt2, int paramInt3) {
/* 70 */     while (paramInt1 <= paramInt2) {
/* 71 */       long l = paramArrayOfLong[paramInt3].longValue();
/* 72 */       if (paramLong > l) {
/* 73 */         paramInt1 = paramInt3 + 1;
/* 74 */       } else if (paramLong < l) {
/* 75 */         paramInt2 = paramInt3 - 1;
/*    */       } else {
/* 77 */         return paramInt3;
/*    */       } 
/* 79 */       paramInt3 = paramInt1 + paramInt2 >>> 1;
/*    */     } 
/* 81 */     return -(paramInt1 + 1);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\type\LongDataType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */