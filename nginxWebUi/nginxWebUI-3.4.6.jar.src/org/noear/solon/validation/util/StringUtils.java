/*    */ package org.noear.solon.validation.util;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class StringUtils
/*    */ {
/*    */   public static boolean isDigits(String str) {
/* 13 */     if (str != null && str.length() != 0) {
/* 14 */       int l = str.length();
/*    */       
/* 16 */       for (int i = 0; i < l; i++) {
/* 17 */         if (!Character.isDigit(str.codePointAt(i))) {
/* 18 */           return false;
/*    */         }
/*    */       } 
/*    */       
/* 22 */       return true;
/*    */     } 
/* 24 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public static boolean isInteger(String str) {
/* 29 */     return isNumberDo(str, false);
/*    */   }
/*    */   
/*    */   public static boolean isNumber(String str) {
/* 33 */     return isNumberDo(str, true);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static boolean isNumberDo(String str, boolean incDot) {
/* 40 */     if (str != null && str.length() != 0) {
/* 41 */       char[] chars = str.toCharArray();
/* 42 */       int l = chars.length;
/*    */       
/* 44 */       int start = (chars[0] != '-' && chars[0] != '+') ? 0 : 1;
/* 45 */       boolean hasDot = false;
/*    */       
/* 47 */       for (int i = start; i < l; i++) {
/* 48 */         int ch = chars[i];
/*    */         
/* 50 */         if (incDot && 
/* 51 */           ch == 46) {
/* 52 */           if (hasDot) {
/* 53 */             return false;
/*    */           }
/* 55 */           hasDot = true;
/*    */ 
/*    */ 
/*    */ 
/*    */         
/*    */         }
/* 61 */         else if (!Character.isDigit(ch)) {
/* 62 */           return false;
/*    */         } 
/*    */       } 
/*    */       
/* 66 */       return true;
/*    */     } 
/* 68 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\validatio\\util\StringUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */