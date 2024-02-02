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
/*    */ public class EventArgUtil
/*    */ {
/*    */   public static final Throwable extractThrowable(Object[] argArray) {
/* 19 */     if (argArray == null || argArray.length == 0) {
/* 20 */       return null;
/*    */     }
/*    */     
/* 23 */     Object lastEntry = argArray[argArray.length - 1];
/* 24 */     if (lastEntry instanceof Throwable) {
/* 25 */       return (Throwable)lastEntry;
/*    */     }
/* 27 */     return null;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Object[] trimmedCopy(Object[] argArray) {
/* 37 */     if (argArray == null || argArray.length == 0) {
/* 38 */       throw new IllegalStateException("non-sensical empty or null argument array");
/*    */     }
/* 40 */     int trimemdLen = argArray.length - 1;
/* 41 */     Object[] trimmed = new Object[trimemdLen];
/* 42 */     System.arraycopy(argArray, 0, trimmed, 0, trimemdLen);
/* 43 */     return trimmed;
/*    */   }
/*    */   
/*    */   public static Object[] arrangeArguments(Object[] argArray) {
/* 47 */     return argArray;
/*    */   }
/*    */   
/*    */   public static boolean successfulExtraction(Throwable throwable) {
/* 51 */     return (throwable != null);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\spi\EventArgUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */