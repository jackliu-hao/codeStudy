/*    */ package net.jodah.expiringmap.internal;
/*    */ 
/*    */ import java.util.NoSuchElementException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class Assert
/*    */ {
/*    */   public static <T> T notNull(T reference, String parameterName) {
/* 13 */     if (reference == null)
/* 14 */       throw new NullPointerException(parameterName + " cannot be null"); 
/* 15 */     return reference;
/*    */   }
/*    */   
/*    */   public static void operation(boolean condition, String message) {
/* 19 */     if (!condition)
/* 20 */       throw new UnsupportedOperationException(message); 
/*    */   }
/*    */   
/*    */   public static void state(boolean expression, String errorMessageFormat, Object... args) {
/* 24 */     if (!expression)
/* 25 */       throw new IllegalStateException(String.format(errorMessageFormat, args)); 
/*    */   }
/*    */   
/*    */   public static void element(Object element, Object key) {
/* 29 */     if (element == null)
/* 30 */       throw new NoSuchElementException(key.toString()); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\net\jodah\expiringmap\internal\Assert.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */