/*    */ package com.mysql.cj.util;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ import java.util.Set;
/*    */ import java.util.TreeSet;
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
/*    */ public class SequentialIdLease
/*    */ {
/* 37 */   private Set<Integer> sequentialIdsLease = new TreeSet<>();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int allocateSequentialId() {
/* 45 */     int nextSequentialId = 0;
/* 46 */     for (Iterator<Integer> it = this.sequentialIdsLease.iterator(); it.hasNext() && nextSequentialId + 1 == ((Integer)it.next()).intValue(); nextSequentialId++);
/*    */ 
/*    */     
/* 49 */     this.sequentialIdsLease.add(Integer.valueOf(++nextSequentialId));
/* 50 */     return nextSequentialId;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void releaseSequentialId(int sequentialId) {
/* 60 */     this.sequentialIdsLease.remove(Integer.valueOf(sequentialId));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\c\\util\SequentialIdLease.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */