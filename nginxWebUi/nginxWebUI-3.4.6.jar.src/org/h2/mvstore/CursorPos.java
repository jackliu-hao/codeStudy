/*    */ package org.h2.mvstore;
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
/*    */ public final class CursorPos<K, V>
/*    */ {
/*    */   public Page<K, V> page;
/*    */   public int index;
/*    */   public CursorPos<K, V> parent;
/*    */   
/*    */   public CursorPos(Page<K, V> paramPage, int paramInt, CursorPos<K, V> paramCursorPos) {
/* 36 */     this.page = paramPage;
/* 37 */     this.index = paramInt;
/* 38 */     this.parent = paramCursorPos;
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
/*    */   static <K, V> CursorPos<K, V> traverseDown(Page<K, V> paramPage, K paramK) {
/* 54 */     CursorPos<K, V> cursorPos = null;
/* 55 */     while (!paramPage.isLeaf()) {
/* 56 */       int i = paramPage.binarySearch(paramK) + 1;
/* 57 */       if (i < 0) {
/* 58 */         i = -i;
/*    */       }
/* 60 */       cursorPos = new CursorPos<>(paramPage, i, cursorPos);
/* 61 */       paramPage = paramPage.getChildPage(i);
/*    */     } 
/* 63 */     return new CursorPos<>(paramPage, paramPage.binarySearch(paramK), cursorPos);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   int processRemovalInfo(long paramLong) {
/* 73 */     int i = 0;
/* 74 */     for (CursorPos<K, V> cursorPos = this; cursorPos != null; cursorPos = cursorPos.parent) {
/* 75 */       i += cursorPos.page.removePage(paramLong);
/*    */     }
/* 77 */     return i;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 82 */     return "CursorPos{page=" + this.page + ", index=" + this.index + ", parent=" + this.parent + '}';
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\CursorPos.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */