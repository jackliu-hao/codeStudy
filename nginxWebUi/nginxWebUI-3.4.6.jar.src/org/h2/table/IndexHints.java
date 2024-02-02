/*    */ package org.h2.table;
/*    */ 
/*    */ import java.util.LinkedHashSet;
/*    */ import java.util.Set;
/*    */ import org.h2.index.Index;
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
/*    */ public final class IndexHints
/*    */ {
/*    */   private final LinkedHashSet<String> allowedIndexes;
/*    */   
/*    */   private IndexHints(LinkedHashSet<String> paramLinkedHashSet) {
/* 26 */     this.allowedIndexes = paramLinkedHashSet;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static IndexHints createUseIndexHints(LinkedHashSet<String> paramLinkedHashSet) {
/* 36 */     return new IndexHints(paramLinkedHashSet);
/*    */   }
/*    */   
/*    */   public Set<String> getAllowedIndexes() {
/* 40 */     return this.allowedIndexes;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 45 */     return "IndexHints{allowedIndexes=" + this.allowedIndexes + '}';
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean allowIndex(Index paramIndex) {
/* 55 */     return this.allowedIndexes.contains(paramIndex.getName());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\table\IndexHints.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */