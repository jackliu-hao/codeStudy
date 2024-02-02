/*    */ package com.google.zxing.common.detector;
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
/*    */ public final class MathUtils
/*    */ {
/*    */   public static int round(float d) {
/* 37 */     return (int)(d + ((d < 0.0F) ? -0.5F : 0.5F));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static float distance(float aX, float aY, float bX, float bY) {
/* 48 */     float xDiff = aX - bX;
/* 49 */     float yDiff = aY - bY;
/* 50 */     return (float)Math.sqrt((xDiff * xDiff + yDiff * yDiff));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static float distance(int aX, int aY, int bX, int bY) {
/* 61 */     int xDiff = aX - bX;
/* 62 */     int yDiff = aY - bY;
/* 63 */     return (float)Math.sqrt((xDiff * xDiff + yDiff * yDiff));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static int sum(int[] array) {
/* 71 */     int count = 0; int arrayOfInt[], i; byte b;
/* 72 */     for (i = (arrayOfInt = array).length, b = 0; b < i; ) { int a = arrayOfInt[b];
/* 73 */       count += a; b++; }
/*    */     
/* 75 */     return count;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\common\detector\MathUtils.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */