/*    */ package org.apache.http.util;
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
/*    */ public class Asserts
/*    */ {
/*    */   public static void check(boolean expression, String message) {
/* 33 */     if (!expression) {
/* 34 */       throw new IllegalStateException(message);
/*    */     }
/*    */   }
/*    */   
/*    */   public static void check(boolean expression, String message, Object... args) {
/* 39 */     if (!expression) {
/* 40 */       throw new IllegalStateException(String.format(message, args));
/*    */     }
/*    */   }
/*    */   
/*    */   public static void check(boolean expression, String message, Object arg) {
/* 45 */     if (!expression) {
/* 46 */       throw new IllegalStateException(String.format(message, new Object[] { arg }));
/*    */     }
/*    */   }
/*    */   
/*    */   public static void notNull(Object object, String name) {
/* 51 */     if (object == null) {
/* 52 */       throw new IllegalStateException(name + " is null");
/*    */     }
/*    */   }
/*    */   
/*    */   public static void notEmpty(CharSequence s, String name) {
/* 57 */     if (TextUtils.isEmpty(s)) {
/* 58 */       throw new IllegalStateException(name + " is empty");
/*    */     }
/*    */   }
/*    */   
/*    */   public static void notBlank(CharSequence s, String name) {
/* 63 */     if (TextUtils.isBlank(s))
/* 64 */       throw new IllegalStateException(name + " is blank"); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\htt\\util\Asserts.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */