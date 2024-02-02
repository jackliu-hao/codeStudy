/*    */ package org.noear.snack.core.utils;
/*    */ 
/*    */ public class StringUtil
/*    */ {
/*    */   public static boolean isEmpty(String s) {
/*  6 */     return (s == null || s.length() == 0);
/*    */   }
/*    */ 
/*    */   
/*    */   public static boolean isInteger(String str) {
/* 11 */     return isNumberDo(str, false);
/*    */   }
/*    */   
/*    */   public static boolean isNumber(String str) {
/* 15 */     return isNumberDo(str, true);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static boolean isNumberDo(String str, boolean incDot) {
/* 22 */     if (str != null && str.length() != 0) {
/* 23 */       char[] chars = str.toCharArray();
/* 24 */       int l = chars.length;
/*    */       
/* 26 */       int start = (chars[0] != '-' && chars[0] != '+') ? 0 : 1;
/* 27 */       boolean hasDot = false;
/*    */       
/* 29 */       for (int i = start; i < l; i++) {
/* 30 */         int ch = chars[i];
/*    */         
/* 32 */         if (incDot && 
/* 33 */           ch == 46) {
/* 34 */           if (hasDot) {
/* 35 */             return false;
/*    */           }
/* 37 */           hasDot = true;
/*    */ 
/*    */ 
/*    */ 
/*    */         
/*    */         }
/* 43 */         else if (!Character.isDigit(ch)) {
/* 44 */           return false;
/*    */         } 
/*    */       } 
/*    */       
/* 48 */       return true;
/*    */     } 
/* 50 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\snack\cor\\utils\StringUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */