/*    */ package org.apache.commons.compress.utils;
/*    */ 
/*    */ import java.util.Collections;
/*    */ import java.util.HashSet;
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
/*    */ public class Sets
/*    */ {
/*    */   @SafeVarargs
/*    */   public static <E> HashSet<E> newHashSet(E... elements) {
/* 46 */     HashSet<E> set = new HashSet<>(elements.length);
/* 47 */     Collections.addAll(set, elements);
/* 48 */     return set;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compres\\utils\Sets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */