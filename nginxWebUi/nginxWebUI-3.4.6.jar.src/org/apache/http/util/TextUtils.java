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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class TextUtils
/*    */ {
/*    */   public static boolean isEmpty(CharSequence s) {
/* 39 */     if (s == null) {
/* 40 */       return true;
/*    */     }
/* 42 */     return (s.length() == 0);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static boolean isBlank(CharSequence s) {
/* 49 */     if (s == null) {
/* 50 */       return true;
/*    */     }
/* 52 */     for (int i = 0; i < s.length(); i++) {
/* 53 */       if (!Character.isWhitespace(s.charAt(i))) {
/* 54 */         return false;
/*    */       }
/*    */     } 
/* 57 */     return true;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static boolean containsBlanks(CharSequence s) {
/* 64 */     if (s == null) {
/* 65 */       return false;
/*    */     }
/* 67 */     for (int i = 0; i < s.length(); i++) {
/* 68 */       if (Character.isWhitespace(s.charAt(i))) {
/* 69 */         return true;
/*    */       }
/*    */     } 
/* 72 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\htt\\util\TextUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */