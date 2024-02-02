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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class LangUtils
/*    */ {
/*    */   public static final int HASH_SEED = 17;
/*    */   public static final int HASH_OFFSET = 37;
/*    */   
/*    */   public static int hashCode(int seed, int hashcode) {
/* 47 */     return seed * 37 + hashcode;
/*    */   }
/*    */   
/*    */   public static int hashCode(int seed, boolean b) {
/* 51 */     return hashCode(seed, b ? 1 : 0);
/*    */   }
/*    */   
/*    */   public static int hashCode(int seed, Object obj) {
/* 55 */     return hashCode(seed, (obj != null) ? obj.hashCode() : 0);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static boolean equals(Object obj1, Object obj2) {
/* 66 */     return (obj1 == null) ? ((obj2 == null)) : obj1.equals(obj2);
/*    */   }
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
/*    */   public static boolean equals(Object[] a1, Object[] a2) {
/* 84 */     if (a1 == null) {
/* 85 */       return (a2 == null);
/*    */     }
/* 87 */     if (a2 != null && a1.length == a2.length) {
/* 88 */       for (int i = 0; i < a1.length; i++) {
/* 89 */         if (!equals(a1[i], a2[i])) {
/* 90 */           return false;
/*    */         }
/*    */       } 
/* 93 */       return true;
/*    */     } 
/* 95 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\htt\\util\LangUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */