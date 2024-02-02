/*    */ package org.apache.commons.compress.utils;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Iterator;
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
/*    */ public class Lists
/*    */ {
/*    */   public static <E> ArrayList<E> newArrayList() {
/* 39 */     return new ArrayList<>();
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
/*    */   public static <E> ArrayList<E> newArrayList(Iterator<? extends E> iterator) {
/* 52 */     ArrayList<E> list = newArrayList();
/* 53 */     Iterators.addAll(list, iterator);
/* 54 */     return list;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compres\\utils\Lists.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */