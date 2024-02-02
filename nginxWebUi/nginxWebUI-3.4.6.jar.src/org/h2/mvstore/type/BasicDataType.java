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
/*    */ 
/*    */ public abstract class BasicDataType<T>
/*    */   implements DataType<T>
/*    */ {
/*    */   public abstract int getMemory(T paramT);
/*    */   
/*    */   public abstract void write(WriteBuffer paramWriteBuffer, T paramT);
/*    */   
/*    */   public abstract T read(ByteBuffer paramByteBuffer);
/*    */   
/*    */   public int compare(T paramT1, T paramT2) {
/* 30 */     throw DataUtils.newUnsupportedOperationException("Can not compare");
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isMemoryEstimationAllowed() {
/* 35 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public int binarySearch(T paramT, Object paramObject, int paramInt1, int paramInt2) {
/* 40 */     T[] arrayOfT = cast(paramObject);
/* 41 */     int i = 0;
/* 42 */     int j = paramInt1 - 1;
/*    */ 
/*    */ 
/*    */     
/* 46 */     int k = paramInt2 - 1;
/* 47 */     if (k < 0 || k > j) {
/* 48 */       k = j >>> 1;
/*    */     }
/* 50 */     while (i <= j) {
/* 51 */       int m = compare(paramT, arrayOfT[k]);
/* 52 */       if (m > 0) {
/* 53 */         i = k + 1;
/* 54 */       } else if (m < 0) {
/* 55 */         j = k - 1;
/*    */       } else {
/* 57 */         return k;
/*    */       } 
/* 59 */       k = i + j >>> 1;
/*    */     } 
/* 61 */     return i ^ 0xFFFFFFFF;
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(WriteBuffer paramWriteBuffer, Object paramObject, int paramInt) {
/* 66 */     for (byte b = 0; b < paramInt; b++) {
/* 67 */       write(paramWriteBuffer, cast(paramObject)[b]);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void read(ByteBuffer paramByteBuffer, Object paramObject, int paramInt) {
/* 73 */     for (byte b = 0; b < paramInt; b++) {
/* 74 */       cast(paramObject)[b] = read(paramByteBuffer);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 80 */     return getClass().getName().hashCode();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object paramObject) {
/* 85 */     return (paramObject != null && getClass().equals(paramObject.getClass()));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected final T[] cast(Object paramObject) {
/* 96 */     return (T[])paramObject;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\type\BasicDataType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */