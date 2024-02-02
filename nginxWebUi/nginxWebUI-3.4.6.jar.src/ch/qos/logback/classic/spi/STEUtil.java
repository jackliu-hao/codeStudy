/*    */ package ch.qos.logback.classic.spi;
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
/*    */ public class STEUtil
/*    */ {
/*    */   static int UNUSED_findNumberOfCommonFrames(StackTraceElement[] steArray, StackTraceElement[] otherSTEArray) {
/* 19 */     if (otherSTEArray == null) {
/* 20 */       return 0;
/*    */     }
/*    */     
/* 23 */     int steIndex = steArray.length - 1;
/* 24 */     int parentIndex = otherSTEArray.length - 1;
/* 25 */     int count = 0;
/* 26 */     while (steIndex >= 0 && parentIndex >= 0 && 
/* 27 */       steArray[steIndex].equals(otherSTEArray[parentIndex])) {
/* 28 */       count++;
/*    */ 
/*    */ 
/*    */       
/* 32 */       steIndex--;
/* 33 */       parentIndex--;
/*    */     } 
/* 35 */     return count;
/*    */   }
/*    */   
/*    */   static int findNumberOfCommonFrames(StackTraceElement[] steArray, StackTraceElementProxy[] otherSTEPArray) {
/* 39 */     if (otherSTEPArray == null) {
/* 40 */       return 0;
/*    */     }
/*    */     
/* 43 */     int steIndex = steArray.length - 1;
/* 44 */     int parentIndex = otherSTEPArray.length - 1;
/* 45 */     int count = 0;
/* 46 */     while (steIndex >= 0 && parentIndex >= 0 && 
/* 47 */       steArray[steIndex].equals((otherSTEPArray[parentIndex]).ste)) {
/* 48 */       count++;
/*    */ 
/*    */ 
/*    */       
/* 52 */       steIndex--;
/* 53 */       parentIndex--;
/*    */     } 
/* 55 */     return count;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\spi\STEUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */