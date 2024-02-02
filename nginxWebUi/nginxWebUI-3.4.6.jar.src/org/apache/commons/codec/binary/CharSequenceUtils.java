/*    */ package org.apache.commons.codec.binary;
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
/*    */ public class CharSequenceUtils
/*    */ {
/*    */   static boolean regionMatches(CharSequence cs, boolean ignoreCase, int thisStart, CharSequence substring, int start, int length) {
/* 51 */     if (cs instanceof String && substring instanceof String) {
/* 52 */       return ((String)cs).regionMatches(ignoreCase, thisStart, (String)substring, start, length);
/*    */     }
/* 54 */     int index1 = thisStart;
/* 55 */     int index2 = start;
/* 56 */     int tmpLen = length;
/*    */     
/* 58 */     while (tmpLen-- > 0) {
/* 59 */       char c1 = cs.charAt(index1++);
/* 60 */       char c2 = substring.charAt(index2++);
/*    */       
/* 62 */       if (c1 == c2) {
/*    */         continue;
/*    */       }
/*    */       
/* 66 */       if (!ignoreCase) {
/* 67 */         return false;
/*    */       }
/*    */ 
/*    */       
/* 71 */       if (Character.toUpperCase(c1) != Character.toUpperCase(c2) && 
/* 72 */         Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
/* 73 */         return false;
/*    */       }
/*    */     } 
/*    */     
/* 77 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\binary\CharSequenceUtils.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */