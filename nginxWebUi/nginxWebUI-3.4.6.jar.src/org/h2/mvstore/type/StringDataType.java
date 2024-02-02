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
/*    */ public class StringDataType
/*    */   extends BasicDataType<String>
/*    */ {
/* 17 */   public static final StringDataType INSTANCE = new StringDataType();
/*    */   
/* 19 */   private static final String[] EMPTY_STRING_ARR = new String[0];
/*    */ 
/*    */   
/*    */   public String[] createStorage(int paramInt) {
/* 23 */     return (paramInt == 0) ? EMPTY_STRING_ARR : new String[paramInt];
/*    */   }
/*    */ 
/*    */   
/*    */   public int compare(String paramString1, String paramString2) {
/* 28 */     return paramString1.compareTo(paramString2);
/*    */   }
/*    */ 
/*    */   
/*    */   public int binarySearch(String paramString, Object paramObject, int paramInt1, int paramInt2) {
/* 33 */     String[] arrayOfString = cast(paramObject);
/* 34 */     int i = 0;
/* 35 */     int j = paramInt1 - 1;
/*    */ 
/*    */ 
/*    */     
/* 39 */     int k = paramInt2 - 1;
/* 40 */     if (k < 0 || k > j) {
/* 41 */       k = j >>> 1;
/*    */     }
/* 43 */     while (i <= j) {
/* 44 */       int m = paramString.compareTo(arrayOfString[k]);
/* 45 */       if (m > 0) {
/* 46 */         i = k + 1;
/* 47 */       } else if (m < 0) {
/* 48 */         j = k - 1;
/*    */       } else {
/* 50 */         return k;
/*    */       } 
/* 52 */       k = i + j >>> 1;
/*    */     } 
/* 54 */     return -(i + 1);
/*    */   }
/*    */   
/*    */   public int getMemory(String paramString) {
/* 58 */     return 24 + 2 * paramString.length();
/*    */   }
/*    */ 
/*    */   
/*    */   public String read(ByteBuffer paramByteBuffer) {
/* 63 */     return DataUtils.readString(paramByteBuffer);
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(WriteBuffer paramWriteBuffer, String paramString) {
/* 68 */     int i = paramString.length();
/* 69 */     paramWriteBuffer.putVarInt(i).putStringData(paramString, i);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\type\StringDataType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */